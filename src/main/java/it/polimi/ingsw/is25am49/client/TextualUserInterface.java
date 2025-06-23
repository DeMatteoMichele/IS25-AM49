package it.polimi.ingsw.is25am49.client;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am49.client.model.*;
import it.polimi.ingsw.is25am49.client.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.client.model.shipboard.Tile;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Orientation;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalUsernameException;
import it.polimi.ingsw.is25am49.shared.RemoteServer;
import javafx.util.Pair;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

public class TextualUserInterface implements UserInterface {
    //Server settings:
    private final String serverIP = "192.168.231.154";
    private final int serverPortSocket = 8081;
    private final int serverPortRMI = 1099;

    private int connectionType;
    //---------------------------------------

    private ClientController controller;
    private GameSession gameSession;
    private String username;
    private int myId = -1;

    private final String error = "⚠ Error : ";
    private final String prompt = ">> ";
    private final LinkedBlockingDeque<String> graphicsQueue;

    public Terminal terminal;
    public LineReader lineReader;
    ExecutorService executor;

    int smallDeckIndex = -1; //todo hardcoddato, non ho un modo per capire che deck sta guardando il player

    public TextualUserInterface() {
        gameSession = new GameSession(this);
        try {
            terminal = TerminalBuilder.builder().system(true).build();
            lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .parser(new DefaultParser())
                    .history(new DefaultHistory())
                    .build();
        } catch (IOException e) {
            System.err.println("errore creazione terminale TUI: " + e.getMessage());
        }
        executor = Executors.newFixedThreadPool(5);
        graphicsQueue = new LinkedBlockingDeque<>();
    }

    @Override
    public void setClientId(int myId) {
        this.myId = myId;
    }

    /**
     * This method is used to set the game session if a diconnnection occurs
     * During the reconnection phase, the game session is resetted to allow the player
     * to reconnect to the game
     */
    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

//----------------------------------------------------------------------------------------------------

    /**
     * IL MAIN DI GALAXY TRUCKER TUI
     */
    public static void main(String args[]) throws IOException {
        TextualUserInterface ui = new TextualUserInterface();
        ui.controller = new ClientController(ui.gameSession, ui);
        Terminal terminal = ui.terminal;

        terminal.writer().println("+------------------------------+");
        terminal.writer().println("| WELCOME TO GALAXY TRUCKER :) |");
        terminal.writer().println("+------------------------------+");

        //stabilisco connessione al server
        ui.userSettings();
        ui.setupConnection();
        ui.updateGameSessionNewPlayer(ui.myId);

        ui.startGame();
    }


//    ----------------------------------------------------------------------------------------------------------
//    MAIN ↑
//    inizio metodi TUI:
//    - setupConnection
//    - registerClient
//    - startGame
//    ...
//    ----------------------------------------------------------------------------------------------------------

    /**
     * Method used from the TUI to register the client to the server via (Socket/RMI)
     */
    public void setupConnection() {
        int n = 0;
        StringBuilder str = new StringBuilder();
        str.append("Which connection do you want to use:\n").
                append("\t1) RMI\n").
                append("\t2) Socket\n");
        //scelta della connessione
        while (true) {
            lineReader.printAbove(str.toString());
            try {
                n = Integer.parseInt(lineReader.readLine(prompt).trim());
            } catch (NumberFormatException _) {
            }
            if (n < 1 || n > 2) {
                printUpdateMessage(error + "invalid choice, retry");
            } else break;
        }
        connectionType = n;
        //registrazione user al server
        registerClient();
    }

    /**
     * Method used from the setupConnection method to register the client to the server.
     * To register you must insert your Username that has to be unique!
     */
    public void userSettings() {
        //Utente inserisce Username
        lineReader.printAbove("Do you want to reconnect to a previous game? y/n");
        String risposta = lineReader.readLine(prompt).trim();
        while (true) {
            if (risposta.equalsIgnoreCase("y")) lineReader.printAbove("Insert your EXACT PREVIOUS username:");
            else lineReader.printAbove("Insert your username:");
            username = lineReader.readLine(prompt).trim();
            if (username.length() > 20 || username.isEmpty() || username.contains(" ")) {
                printUpdateMessage(error + "username non valid");
            } else break;
        }
        controller.setUsername(username);
        if (!risposta.equalsIgnoreCase("y"))
            controller.createMyIdJson();
    }

    /**
     * Method used from the setupConnection method to register the client to the server.
     * To register you must insert your Username that has to be unique!
     */
    public void registerClient() {
        try {
            switch (connectionType) {
                //CONNESSIONE RMI
                case 1 -> {
                    try {
                        ClientRMI clientRMI = null;
                        connectionRMI(false, clientRMI);
                    } catch (IOException | NotBoundException e) {
                        printUpdateMessage("⚠ Error : something went wrong during the connection tio the server RMI --> " + e.getMessage());
                        //se voglio riprovare a connettermi
                        printUpdateMessage("\nwanna retry? Y");
                        if (lineReader.readLine(prompt).trim().equalsIgnoreCase("y")) setupConnection();
                        else {
                            System.exit(0);
                            printUpdateMessage("closing...");
                        }
                    }
                }

                //CONNESSSIONE SOCKET
                case 2 -> {
                    ClientSerializer clientSerializer = new ClientSerializer(controller);
                    ServerHandler serverHandler = new ServerHandler(serverIP, serverPortSocket);
                    serverHandler.setClientSerializer(clientSerializer);
                    clientSerializer.setServerHandler(serverHandler);
                    controller.setServerMethods(clientSerializer);
                    controller.setServerHandler(serverHandler);
                    controller.registerClientSocket();
                }
            }
        } catch (Exception e) {
            printUpdateMessage(error + e.getMessage());
            //se voglio riprovare a connettermi
            printUpdateMessage("\nwanna retry? Y");
            if (lineReader.readLine(prompt).trim().equalsIgnoreCase("y")) setupConnection();
            else {
                printUpdateMessage("closing...");
                System.exit(0);
            }
        }
    }

    public void connectionRMI(boolean isReconnecting, ClientRMI clientRMI) throws NotBoundException, IOException {
        RemoteServer serverStub;
        Registry reg = LocateRegistry.getRegistry(serverIP, serverPortRMI);
        serverStub = (RemoteServer) reg.lookup("Server");
        if (!isReconnecting) {
            clientRMI = new ClientRMI(controller, serverStub, this, serverIP);
            System.out.println("Client successfully connected to ["+serverIP+" , 1099]");
            controller.setServerMethods(serverStub);
            try {
                System.out.println("trying to log in to the server ...");
                controller.registerClientRMI(clientRMI);
                System.out.println("client logged");
            } catch (RemoteException e) {
                System.out.println("client failed logging on server RMI --> " + e.getMessage());
                e.printStackTrace();
                //se voglio riprovare a connettermi
                System.out.println("\nwanna retry? Y");
                if (lineReader.readLine(prompt).trim().equalsIgnoreCase("y")) setupConnection();
                else {
                    printUpdateMessage("Server: closing...");
                    System.exit(0);
                }
            }
        } else {
            clientRMI.replaceServerStub(serverStub);
            System.out.println("Reconnected successfully to ["+serverIP+" , 1099]");
            controller.setServerMethods(serverStub);
            try {
                System.out.println("trying to log in to the server ...");
                controller.registerClientRMI(clientRMI);
                System.out.println("client logged");
            } catch (RemoteException e) {
                System.out.println("client failed logging on server RMI --> " + e.getMessage());
            }
        }

    }

    public void startGame() {
        executor.submit(() -> {
            String inputString;
            while (true) {
                inputString = lineReader.readLine(prompt).trim();
                if (inputString.equalsIgnoreCase("quit")) {
                    printUpdateMessage("Server: closing...");
                    executor.shutdownNow(); // Interrompe entrambi i thread
                    System.exit(0);
                }
                if( inputString.equalsIgnoreCase("reconnect")) {
                    controller.clientReset();
                    userSettings();
                    continue;
                }
                handleInput(inputString.toLowerCase());
                terminal.flush();
            }
        });
        executor.submit(() -> {
            try {
                Thread.sleep(1000);//aspetto un attimo per far arrivare i pacchetti
                while (true) {
                    String graphic = graphicsQueue.take();
//                    if (graphicsQueue.size() > 3) {
//                        printUpdateMessage("RECONNECTING ...");
//                        String next;
//                        while ((next = graphicsQueue.poll()) != null) {
//                            graphic = next;
//                            Thread.sleep(100);
//                        }
//                        printUpdateMessage("successfully reconnected");
//                        lineReader.printAbove(graphic);
//                    } else {
                        clearConsole();
                        lineReader.printAbove(graphic);
//                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // permette di fermare il thread in modo sicuro
            }
        });
    }

    public void clearConsole() {
        for (int i = 0; i < 3; i++) {
            terminal.writer().println();
        }
    }

    public void insertAliens(int playerId) {
        Player p = gameSession.getPlayer(playerId).get();
        if (p.getShipBoard().isCompleted()) {
            lineReader.printAbove("insert aliens on the Shipboard\n");
            if (p.getShipBoard().getAlienPoint().isPresent()) {
                Point alienPoint = p.getShipBoard().getAlienPoint().get();
                String selection = lineReader.readLine("insert alien on coordinates: <" + alienPoint.x+","+alienPoint.y+">\n" +
                        "a --> astronaut\n" +
                        "p --> purple aliens\n" +
                        "b --> brown aliens\n" +
                        ">> "
                ).trim();
                switch (selection.toLowerCase()) {
                    case "p":
                        controller.userSelectTypeOfToken(playerId, Token.PURPLE_ALIENS, alienPoint);
                        break;
                    case "b":
                        controller.userSelectTypeOfToken(playerId, Token.BROWN_ALIENS, alienPoint);
                        break;
                    case "a":
                        controller.userSelectTypeOfToken(playerId, Token.ASTRONAUT, alienPoint);
                        break;
                    default:
                        printUpdateMessage(error + "invalid choice");
                }
            } else {
                printUpdateMessage("ALL ALIENS INSERTED, waiting for other players ...\n");
            }
        } else throw new GameSettingsException("shipboard not completed yet");
    }

    public ArrayList<Pair<Point, Token>> decisionToken() {
        ArrayList<Pair<Point, Token>> tokens = new ArrayList<>();
        lineReader.printAbove("handle Tokens in the shipboard (s to stop):\n");
        String s = "";
        try {
            while (!s.equalsIgnoreCase("s")) {
                s = lineReader.readLine("Insert the coordinates (x,y) >> ");
                if( s.trim().isEmpty() || s.trim().equalsIgnoreCase("s")) return tokens; //se l'utente vuole fermarsi
                String[] coordinates = s.trim().split("\\s+");
                int x = Integer.parseInt(coordinates[0]);
                int y = Integer.parseInt(coordinates[1]);
                Point point = new Point(x, y);
                Token token;
                String selection = lineReader.readLine("select a token: " + point + "\n" +
                        "a --> astronaut\n" +
                        "g --> green goods\n" +
                        "b --> blue goods\n" +
                        "y --> yellow goods\n" +
                        "r --> red goods\n" +
                        "c --> battery cells\n" +
                        ">> "
                ).trim();
                switch (selection.toLowerCase()) {
                    case "a":
                        token = Token.ASTRONAUT;
                        break;
                    case "g":
                        token = Token.GREEN_GOODS;
                        break;
                    case "b":
                        token = Token.BLUE_GOODS;
                        break;
                    case "y":
                        token = Token.YELLOW_GOODS;
                        break;
                    case "r":
                        token = Token.RED_GOODS;
                        break;
                    case "c":
                        token = Token.BATTERY_CELLS;
                        break;
                    case "s":
                        return tokens; //se l'utente vuole fermarsi
                    default:
                        printUpdateMessage(error + "invalid choice");
                        continue;
                }
                tokens.add(new Pair<>(point, token));
            }
        } catch (Exception e) {
            printUpdateMessage(error + e.getMessage());
        }
        return tokens;
    }

    public void insertRandomTiles(int numberOfTiles) {
        Player player = gameSession.getPlayer(myId).get();
        ShipBoard shipboard = player.getShipBoard();
        int occupiedTiles = shipboard.getGrid().values().stream().flatMapToInt(t-> IntStream.of(t.isPresent()?1:0)).sum();
        if (player.getShipBoard().isCompleted()) {
            printUpdateMessage( error+"Shipboard already completed, you cannot generate a new one");
            return;
        }
        if(numberOfTiles<=0){
            printUpdateMessage( "insertion completed");
            return;
        }

        if(gameSession.getGame().isPresent()){
            Random rand = new Random();
            Game game = gameSession.getGame().get();
            int n= switch (game.getMode()){
                case FLIGHT_MODE,LEVEL_1 -> 19;
                case LEVEL_2 -> 28;
                default -> 0;
            };
            n=Math.min(numberOfTiles, n-occupiedTiles);
            int tr=0;
            while (tr<20){
                try {
                    tr++;
                    Thread.sleep(100);
                    controller.userDrawTile(myId, game.getGameboard().getTilesDeck().getRandomPresentIdTile());
                    break;
                }catch (Exception _){}
            }
            Orientation o= Orientation.values()[rand.nextInt(4)];
            tr=0;
            while (tr<20){
                try {
                    tr++;
                    Thread.sleep(100);
                    gameSession.getState().userRotateHandTile(myId, o);
                    break;
                } catch (Exception _) {}
            }
            tr=0;
            while (tr<20){
                try {
                    tr++;
                    Thread.sleep(100);
                    Point p = new Point(rand.nextInt(7) + 4, rand.nextInt(5) + 5);
                    controller.userPlaceTile(myId, p, o);
                    break;
                } catch (Exception _) {}
            }
            insertRandomTiles(n-1);
        }
    }

    public void readShipboardFromFile(String filename) {
        filename="src/main/resources/it/polimi/ingsw/is25am49/"+filename;
        int delay = 150; // delay in milliseconds
        try {
            int tr=0;
            File file = new File(filename);
            if (!file.exists()) {
                throw new FileNotFoundException("File ("+filename+") non trovato: " + file.getAbsolutePath());
            } else {
                Game game = gameSession.getGame().get();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootArray = objectMapper.readTree(file);
                for (JsonNode node : rootArray) {
                    try {
                        controller.userDrawTile(myId, node.get("id").asInt());
                        Orientation o= Orientation.valueOf(node.get("orientation").asText());
                        while (tr<35){
                            tr++;
                            try {
                                Thread.sleep(delay);
                                gameSession.getState().userRotateHandTile(myId, o);
                                break;
                            } catch (Exception _) {}
                        }
                        Thread.sleep(delay);
                        Point p = new Point(node.get("x").asInt(), node.get("y").asInt());
                        controller.userPlaceTile(myId, p, o);
                    }catch (Exception e){
                        printUpdateMessage(error + e.getMessage());
                    }
                }
                //controller.userFinishShipboard(myId);
            }
        } catch (FileNotFoundException e) {
            printUpdateMessage(error + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    ----------------------------------------------------------------------------------------------------------
//    MAIN ↑
//
//    inizio metodi interfaccia UserInterface: stampa delle grafiche
//    ----------------------------------------------------------------------------------------------------------

    @Override
    public void printUpdateMessage(String msg) {
        AttributedString block;
        if (msg.contains(error))
            block = new AttributedStringBuilder().append(msg + "\n", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)).toAttributedString();
        else
            block = new AttributedStringBuilder().append(msg + "\n", AttributedStyle.INVERSE_OFF.foreground(AttributedStyle.CYAN)).toAttributedString();
        lineReader.printAbove(block.toAnsi());
    }

    @Override
    public void updateStateTransition(int stateIndex) {
        StringBuilder stringBuilder = new StringBuilder();
        switch (stateIndex) {
            case 1 -> {//BuildState
                stringBuilder.append("+-------------------------------+\n")
                        .append("|       BUILD STATE :)          |\n")
                        .append("+-------------------------------+\n");
                graphicsQueue.add(stringBuilder.toString());
                updateDiscardTile(myId, -1);
            }
            case 2 -> {//AdventureState
                stringBuilder.append("+-------------------------------+\n")
                        .append("|       ADVENTURE STATE :)      |\n")
                        .append("+-------------------------------+\n");
                graphicsQueue.add(stringBuilder.toString());
                updateLeaderBoard(gameSession.getLeaderBoard());
            }
            case 3 -> {//FinishState
                stringBuilder.append("+-------------------------------+\n")
                        .append("|        FINISH STATE :)        |\n")
                        .append("+-------------------------------+\n\n")
                        .append("FinishState - commands:\n0. EndGame \n1. CalculateScore\n\n");
                graphicsQueue.add(stringBuilder.toString());
            }
            default -> {
                printUpdateMessage("stateIndex: " + stateIndex);
            }
        }
    }

    @Override
    public void updateGameSessionNewPlayer(int playerId) {
        if (!gameSession.checkIsInLobby(myId)) {
            String str = "welcome to the game " + username + "!\n" +
                    "__________________________________________________________________\n\n" +
                    gameSession + "\n" +
                    "GameLobby - commands: \n" + "1. join lobby\n2. setup game\n" +
                    "__________________________________________________________________\n" +
                    "choose an option:";
            graphicsQueue.add(str);
        }
    }

    @Override
    public void updateGameSessionCreateGame(int playerId, GameMode gameMode, int maxPlayers) {
        updateGameNewPlayer(playerId);
    }

    @Override
    public void updateGameNewPlayer(int playerId) {
        StringBuilder str = new StringBuilder();
        if (gameSession.getAdmin().map(a -> a.getIdPlayer() == myId).orElse(false) && gameSession.getGame().isEmpty()) {
            str.append("__________________________________________________________________\n\n");
            str.append("+----------------------------------------------\n");
            str.append("| LOBBY : Config\tmaxPlayers - 4\tgameMode - DEFAULT\n");
            str.append("+----------------------------------------------\n\n");
            str.append("GameLobby - commands: \n").append("1. join lobby\n2. setup game\n");
            str.append("\n__________________________________________________________________\n");
            str.append("choose an option:");
            lineReader.printAbove(str.toString());
        } else if (gameSession.checkIsInLobby(myId)) {
            str.append("__________________________________________________________________\n\n").
                    append(" SERVER - ").append(serverIP).append("\tstatus - connected\n").
                    append(gameSession.getGame().get()).append("\n");
            str.append("GameLobby - commands: \n").append("1. join lobby\n2. setup game\n");
            str.append("__________________________________________________________________\n");
            str.append("choose an option:");
            graphicsQueue.add(str.toString());
        }
    }

    public String buildStateMenu() {
        return """
                BuildState - commands:\s
                0.  PlaceTile                           6.  StopLookSmallAdventureCardDeck
                1.  PickTile                            7.  PlaceTimer
                2.  RotateTileClockWise                 8.  LookUncoveredTile
                3.  DiscardTile                         9.  LookAtShipboard
                4.  DrawTile                            10. FinishShipboard
                5.  LookSmallAdventureCardDeck          11. SelectTypeOfToken
                12. ←-- go back                         13. insertRandomTiles
                14. loadShipboard
                """;
    }

    @Override
    public void updatePlaceTile(int playerId, Point coordinates) {
        if(playerId == myId){
            String str = "BUILD STATE\n"+"_".repeat( 200) + "\n\n" +
                    gameSession.getGame().get().decksToString() +
                    gameSession.getPlayer(myId).get()+
                    buildStateMenu() +
                    "_".repeat( 200)+"\n" +
                    "choose an option:";
            graphicsQueue.add(str);
        }
    }

    @Override
    public void updatePickTile(int playerId, Point coordinates, int idTile) {
        if(playerId == myId){
            String str = "BUILD STATE\n"+"_".repeat( 200) + "\n\n" +
                    gameSession.getGame().get().decksToString()+
                    gameSession.getPlayer(myId).get()+
                    buildStateMenu() +
                    "_".repeat( 200)+"\n" +
                    "choose an option:";
            graphicsQueue.add(str);
        }
    }

    @Override
    public void updateRotateTileClockWise(int playerId) {
        String str = "BUILD STATE\n"+"_".repeat( 200) + "\n\n" +
                gameSession.getGame().get().decksToString() +
                gameSession.getPlayer(myId).get()+
                buildStateMenu() +
                "_".repeat( 200)+"\n" +
                "choose an option:";
                graphicsQueue.add(str);
    }

    @Override
    public void updateDiscardTile(int playerId, int idTile) {
        if(playerId == myId){
            String str = "BUILD STATE\n"+"_".repeat( 200) + "\n\n" +
                    gameSession.getGame().get().decksToString() +
                    gameSession.getPlayer(myId).get()+
                    buildStateMenu() +
                    "_".repeat( 200)+"\n" +
                    "choose an option:";
            graphicsQueue.add(str);
        }
    }

    @Override
    public void updateDrawTile(int playerId, int idTIle) {
        if(playerId == myId){
            String str = "BUILD STATE\n"+"_".repeat( 200) + "\n\n" +
                    gameSession.getGame().get().decksToString() +
                    gameSession.getPlayer(myId).get()+
                    buildStateMenu() +
                    "_".repeat( 200)+"\n" +
                    "choose an option:";
            graphicsQueue.add(str);
        }
    }

    @Override
    public void updateFinishShipboard(int playerId) {
        if(playerId == myId){
            String str = "BUILD STATE\n" + "_".repeat(200) + "\n\n" +
                    gameSession.getPlayer(playerId).get().shipToString() +
                   "\nSHIPBOARD COMPLETE !!!!\n └──> .11  SelectTypeOfToken\n"+
                    "_".repeat(200) + "\n" +
                    "choose an option:";
            graphicsQueue.add(str);
        }
    }

    @Override
    public void updateLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        if(playerId == myId) {
            String str = "BUILD STATE\n" + "_".repeat(200) + "\n\n" +
                    gameSession.getGame().get().getGameboard().smallDeckToString(smallDeckIndex) +
                    "BuildState - commands:\n 6. ←-- stop looking \n\n" +
                    "_".repeat(200) + "\n" +
                    "choose an option:";
            graphicsQueue.add(str);
        }
    }

    @Override
    public void updateStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        if(playerId == myId){
            updatePlaceTile(myId,new Point(7,7));
        }
    }

    @Override
    public void updatePlaceTimer(int playerId) {
        var p= gameSession.getPlayer(playerId).get();
        printUpdateMessage("user: "+p.getUsername() +" placed the timer on the board\n"+gameSession.getGame().get().getTimer());
    }

    @Override
    public void updateTimerIsDone() {
        printUpdateMessage("""
                +-----------------------+
                | !!! TIMER IS DONE !!! |
                +-----------------------+
                """);
    }

    @Override
    public void updateSelectTypeOfToken(int playerId, Token token, Point coordinates) {
        if(playerId == myId){
            String str = "BUILD STATE\n" + "_".repeat(200) + "\n\n" +
                    gameSession.getPlayer(playerId).get().shipToString() +
                    "\nSHIPBOARD COMPLETE !!!!\n └──> .11  SelectTypeOfToken\n"+
                    "token: "+token+" added on coordinates: "+coordinates+"\n" +
                    "_".repeat(200) + "\n" +
                    "choose an option:";
            graphicsQueue.add(str);

            updateFinishShipboard(playerId);
        }
    }

    @Override
    public void lookAtShipboard(int playerId) {
        String str = "BUILD STATE\n"+"_".repeat( 200) + "\n\n" +
                gameSession.getPlayer(playerId).get().shipToString()+
                //"BuildState - commands:\n 12. ←-- go back"+
                "\n\n"+
                "_".repeat( 200)+"\n" +
                "choose an option:";
        graphicsQueue.add(str);
    }

    /**
     * @param playerId
     * @param cardState
     */
    @Override
    public void updateCardState(int playerId, AdventureStatesTypes cardState) {

    }

    /**
     * @param playerId
     */
    @Override
    public void updateChangeTurn(int playerId) {

    }

    public String adventureStateMenu(){
        return """
                AdventureState - commands:\s
                1.  DrawAdventureCard           6.  LandOnStationOrShip
                2.  ThrowDices                  7.  AcceptPrizes
                3.  ActivateComponent           8.  StopFlight
                4.  DecisionToken               9.  LookAtShipboard
                5.  LandOnPlanet                10. StopFlight
                """;
    }
    @Override
    public void updateGenericCardMessage(int playerId, String message) {
        StringBuilder str = new StringBuilder("\u001B[1m\u001B[32mCARD INFO: \u001B[0m\u001B[32m");
        if (playerId == myId || playerId==-99){//playerid==-99 è per i messaggi per tutti
            int j=0;
            for (int i = 0; i < message.length(); i++) {
                if(message.charAt(i)=='\n')j=0;
                if (j > 0 && j % 100 == 0) {
                    str.append("-\n         -");
                }
                str.append(message.charAt(i));
                j++;
            }
        }else str.append("player ").append(gameSession.getPlayer(playerId).get().getUsername()).append(" is playing, wait for your turn\n");
        str.append("\n\u001B[0m");
        graphicsQueue.add(str.toString());
    }

    private static final String[][] DICE = {
            {
                    "+ - - - - +",
                    "|         |",
                    "|    ●    |",
                    "|         |",
                    "+ - - - - +"
            }, {
                    "+ - - - - +",
                    "|  ●      |",
                    "|         |",
                    "|      ●  |",
                    "+ - - - - +"
            }, {
                    "+ - - - - +",
                    "|  ●      |",
                    "|    ●    |",
                    "|      ●  |",
                    "+ - - - - +"
            }, {
                    "+ - - - - +",
                    "|  ●   ●  |",
                    "|         |",
                    "|  ●   ●  |",
                    "+ - - - - +"
            }, {
                    "+ - - - - +",
                    "|  ●   ●  |",
                    "|    ●    |",
                    "|  ●   ●  |",
                    "+ - - - - +"
            }, {
                    "+ - - - - +",
                    "|  ● ● ●  |",
                    "|         |",
                    "|  ● ● ●  |",
                    "+ - - - - +"
            }
    };
    @Override
    public void updateThrowDices(int playerId, int firstDice, int secondDice) {
        printUpdateMessage("Player " + gameSession.getPlayer(playerId).get().getUsername() + " has thrown the dices:\n");
            StringBuilder str = new StringBuilder();
            str.append("Throwing...\n");
            String[] dice1 = DICE[firstDice - 1];
            String[] dice2 = DICE[secondDice - 1];
            for (int i = 0; i < dice1.length; i++) {
                str.append(dice1[i]).append("   ").append(dice2[i]).append("\n");
            }
            lineReader.printAbove(str.toString());
    }

    @Override
    public void updateDrawAdventureCard(int playerId, AdventureCard adventureCard) {
        String str = "ADVENTURE STATE\n"+"_".repeat( 200) + "\n\n" +
                gameSession.getLeaderBoard()+
                adventureCard+
                adventureStateMenu()+
                "_".repeat( 200)+"\n" +
                "choose an option:";
        graphicsQueue.add(str);
    }

    @Override
    public void updateFinishDecisionActivating(int playerId, Set<Point> coordinates) {
        StringBuilder str = new StringBuilder();
        Player p = gameSession.getPlayer(playerId).get();
        str.append("Player ").append(p.getUsername()).append(" has activated:");
        for (Point pt : coordinates) {
            try{
                Tile t=p.getShipBoard().getTile(pt);
                str.append("[").append(pt.x).append(",").append(pt.y).append("] - ").append(t.getComponentType()).append("\n");
            }catch(Exception _){}
        }
        updateGenericCardMessage(myId,str.toString());
    }

    @Override
    public void updateFinishDecisionToken(int playerId, List<Pair<Point, Token>> listOfTokens, int numTokens, boolean removing) {
        StringBuilder str = new StringBuilder();
        Player p = gameSession.getPlayer(playerId).get();
        str.append("Player ").append(p.getUsername()).append(" has ").append(removing ? "removed" : "inserted").append(" the following tokens:\n");
        for (Pair<Point, Token> pt : listOfTokens) {
            try{
                Tile t=p.getShipBoard().getTile(pt.getKey());
                str.append("[").append(pt.getKey().x).append(",").append(pt.getKey().y).append("] - ").append(pt.getValue()).append("\n");
            }catch(Exception _){}
        }
        updateGenericCardMessage(myId,str.toString());
        if(playerId==myId)updateShipBoard(myId);
    }

    @Override
    public void updateDeactivating(int playerId, List<Point> coordinates) {
        //non chiaro
    }


    @Override
    public void updateLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        Player p = gameSession.getPlayer(playerId).get();
        if(isLanded) updateGenericCardMessage(myId,"Player " + p.getUsername() + " has landed on planet with ID: " + planetId+ " successfully!");
        else printUpdateMessage("Player " + p.getUsername() + " decided not to land");
    }

    @Override
    public void updateLandOnStationOrShip(int playerId, boolean isLanded, String cardId) {
        Player p = gameSession.getPlayer(playerId).get();
        if(isLanded) updateGenericCardMessage(myId,"Player " + p.getUsername() + " decided to land on the station or ship with cardID: " + cardId);
        else printUpdateMessage("Player " + p.getUsername() + " decided not to land");
    }

    @Override
    public void updateAcceptPrizes(int playerId, boolean isAccepted) {

    }

    @Override
    public void updateStopFlight(int playerId) {
        Player p= gameSession.getPlayer(playerId).get();
        printUpdateMessage("!!!Player " + p.getUsername() + " has landed!!!\t-out of the game");
    }

    @Override
    public void updateEnemyDefeated(int playerIdWhoWon, String enemyName, String cardId) {

    }

    public void updateShipBoard(int playerId) {
        String str = "ADVENTURE STATE\n"+"_".repeat( 200) + "\n\n" +
                gameSession.getPlayer(playerId).get().shipToString()+
                adventureStateMenu()+
                "_".repeat( 200)+"\n" +
                "choose an option:";
        graphicsQueue.add(str);
    }

    @Override
    public void updateEffectModifyCrew(int playerId, Point coordinate, Token token) {
        updateGenericCardMessage(playerId,"you have lost a crew member on coordinates: <" +coordinate.x +","+coordinate.y +"> - token: " + token);
    }

    @Override
    public void updateEffectCannonade(int playerId, Point coordinate) {
        updateGenericCardMessage(playerId,"the cannonade successfully hit the shipboard\n -->tile in coordinates: <" +coordinate.x +","+coordinate.y +"> has been destroyed");
        if(playerId==myId)updateShipBoard(playerId);
    }

    @Override
    public void updateEffectLoseGoods(int playerId, Point coordinate, Token token) {
        updateGenericCardMessage(playerId,"you have lost a good on coordinates: <" +coordinate.x +","+coordinate.y +"> - token: " + token);
    }

    @Override
    public void updateEffectWinGoods(int playerId, Point coordinate, Token token) {
        updateGenericCardMessage(playerId,"you have won a good on coordinates: <" +coordinate.x +","+coordinate.y +"> - token: " + token);
    }

    /**
     * @param playerId
     * @param redgoods
     * @param yellowgoods
     * @param greengoods
     * @param bluegoods
     */
    @Override
    public void updateNumberOfWinGoods(int playerId, int redgoods, int yellowgoods, int greengoods, int bluegoods) {

    }

    @Override
    public void updateEffectModifyEnergy(int playerId, Point coordinate, Token token) {
        updateGenericCardMessage(playerId,"you have selected a battery from coordinates: <" +coordinate.x +","+coordinate.y +">");
    }

    @Override
    public void updateEffectMeteorite(int playerId, Point coordinate) {
        updateGenericCardMessage(playerId,"the meteorite successfully hit the shipboard\n -->tile in coordinates: <" +coordinate.x +","+coordinate.y +"> has been destroyed");
        if(playerId==myId)updateShipBoard(playerId);
    }

    @Override
    public void updateEarnedCredits(int playerId, int credits) {
        updateGenericCardMessage(playerId, "you have earned " + credits + " credits");
    }

    @Override
    public void updateDestroyedTiles(int idPlayer, Set<Point> destroyedTiles) {
        StringBuilder str = new StringBuilder();
        Player p = gameSession.getPlayer(idPlayer).get();
        str.append("Player ").append(p.getUsername()).append(" has lost the following tiles:\n");
        for (Point pt : destroyedTiles) {
            try{
                Tile t=p.getShipBoard().getTile(pt);
                str.append("[").append(pt.x).append(",").append(pt.y).append("] - ").append(t.getComponentType()).append("\n");
            }catch(Exception _){}
        }
        updateGenericCardMessage(myId,str.toString());
        if(idPlayer==myId)updateShipBoard(myId);
    }

    @Override
    public void updateLeaderBoard(LeaderBoard leaderBoard) {
        String str = "ADVENTURE STATE\n"+"_".repeat( 200) + "\n\n" +
                gameSession.getLeaderBoard()+
                adventureStateMenu()+
                "_".repeat( 200)+"\n" +
                "choose an option:";
        graphicsQueue.add(str);
    }

    @Override
    public void updateEndGame(int playerId) {
        printUpdateMessage("""
                +-----------------------+
                | !!! GAME IS ENDED !!! |
                +-----------------------+
                
                thanks for playing with us!
                """);
        printUpdateMessage("Game: closing...");
        executor.shutdownNow();
        System.exit(0);
    }

    @Override
    public void updateCalculateScore(int playerId) {
        String str = "FINISH STATE\n"+"_".repeat( 200) + "\n\n" +
                gameSession.getGame().get().getGameboard()+
                "FinishState - commands:\n0. EndGame \n1. CalculateScore\n\n"+
                "_".repeat( 200)+"\n" +
                "choose an option:";
        graphicsQueue.add(str);
    }


//    ----------------------------------------------------------------------------------------------------------
//    MAIN ↑
//
//    HandleInput ↓
//    ----------------------------------------------------------------------------------------------------------

    /**
     * Method that handles the input string written by the user,
     * calling the methods on the ClientController
     *
     * @param input string
     */
    public void handleInput(String input) {
        try {
            //printUpdateMessage(input.trim() + ":" + gameSession.getState().getStateIndex());
            switch (input.trim() + ":" + gameSession.getState().getStateIndex()) {
                //INIT STATE
                case "1:0" -> { //JOIN LOBBY
                    try {
                        controller.playerJoin(username);
                    }catch (IllegalUsernameException e){
                        printUpdateMessage(error+e.getMessage());
                        userSettings();
                        updateGameSessionNewPlayer(myId);
                    } catch (Exception e) {
                        printUpdateMessage(error+e.getMessage());
                        updateGameSessionNewPlayer(myId);
                    }
                }
                case "2:0" -> { //SETUP GAME
                    int mode , max;
                    lineReader.printAbove("\nSETTINGS\nSelect gamemode:\n\t0. flight mode\n\t1. level 1\n\t2. level 2\n");
                    try {
                        mode = Integer.parseInt(lineReader.readLine(">> ").trim());
                        max = Integer.parseInt(lineReader.readLine("insert max players:\n>> ").trim());
                        controller.adminSetup(max, mode);
                    } catch (Exception e) {
                        printUpdateMessage(error+e.getMessage());
                        updateGameSessionCreateGame(myId, GameMode.DEFAULT, 4);
                    }
                }

                //BUILD STATE
                case "0:1"->{//placeTile
                    try{
                        String s = lineReader.readLine("Insert the coordinates (x,y) >> ");
                        String[] coordinates = s.trim().split("\\s+");
                        int x = Integer.parseInt(coordinates[0]);
                        int y = Integer.parseInt(coordinates[1]);
                        controller.userPlaceTile(myId,new Point(x,y), gameSession.getPlayer(myId).get().getHandTileOrientation());//todo sistema sta sleppa
                    }catch (Exception e){
                        printUpdateMessage(error+e.getMessage());
                        //updatePlaceTile(myId,new Point(0,0));
                    }
                }
                case "1:1"->{//PickTile
                    try {
                        String s = lineReader.readLine("Insert the coordinates (x,y) >> ");
                        String[] coordinates = s.trim().split("\\s+");
                        int x = Integer.parseInt(coordinates[0]);
                        int y = Integer.parseInt(coordinates[1]);
                        controller.userPickTile(myId, new Point(x, y));
                    }catch (Exception e){
                        printUpdateMessage(error+e.getMessage());
                        //updatePickTile(myId,new Point(0,0));
                    }
                }
                case "2:1"->{//rotateTile
                    try {
                        gameSession.getState().userRotateTileClockWise(myId);
                    }catch (Exception e){
                        printUpdateMessage(error+e.getMessage());
                        //updateRotateTileClockWise(myId);
                    }
                }
                case "3:1"->{//discardTile
                    try {
                        controller.userDiscardTile(myId);
                    }catch (Exception e){
                        printUpdateMessage(error+e.getMessage());
                        //updateDiscardTile(myId);
                    }

                }
                case "4:1"->{//DrawTile
                    try{
                        int id = Integer.parseInt(lineReader.readLine("select a IdTile from above>> ").trim());
                        controller.userDrawTile(myId, id);
                    }catch (Exception e){
                        printUpdateMessage(error+e.getMessage());
                        //updateDrawTile(myId,0);
                    }
                }
                case "5:1"->{//lookSmallAdventureCardDeck
                    try {//todo volendo si può fare che finché non smetto di guardare non posso fare nulla
                        smallDeckIndex = Integer.parseInt(lineReader.readLine("insert deck index (0-2) >> ").trim());
                        controller.userLookSmallAdventureCardDeck(myId, smallDeckIndex);
                    }catch (Exception e){
                        printUpdateMessage(error+e.getMessage());
                        //updateLookSmallAdventureCardDeck(myId,0);
                    }
                }
                case "6:1"->{//stopLookSmallAdventureCardDeck
                    try {
                        controller.userStopLookSmallAdventureCardDeck(myId,smallDeckIndex);
                        updateStopLookSmallAdventureCardDeck(myId,0);
                    }catch (Exception e){
                        printUpdateMessage(error+e.getMessage());
                        //updateStopLookSmallAdventureCardDeck(myId,0);
                    }
                }
                case "7:1"->{
                    controller.userPlaceTimer(myId);
                }
                case "8:1"->{//lookUncoveredTile
                    try{
                        int id = Integer.parseInt(lineReader.readLine("select a IdTile from above>> ").trim());
                        String s= gameSession.getGame().get().getGameboard().getTilesDeck().getTileById(id)+"\n"+
                                "BuildState - commands:\n8. LookUncoveredTile\n12. ←-- go back \n\n";
                        graphicsQueue.add(s);
                    }catch (Exception e ){
                        printUpdateMessage(error+e.getMessage());
                        //updateLookUncoveredTile(myId,new Point(0,0));
                    }
                }
                case "9:1" ->{//lookAtShipboard
                    lineReader.printAbove("which ship do you want to see?\n");
                    try {
                        for (Player p : gameSession.getGame().get().getListPlayers()) {
                            terminal.writer().println(p.getIdPlayer() + " --> [" + p.getUsername() + "]");
                        }
                        int id = Integer.parseInt(lineReader.readLine("insert the playerId >> ").trim());
                        if (gameSession.getPlayer(id).isPresent()) lookAtShipboard(id);
                        else printUpdateMessage(error + "player not found");
                    } catch (Exception e) {
                        printUpdateMessage(error + e.getMessage());
                        //updatePlaceTile(myId,new Point(7,7));
                    }
                }
                case "10:1" -> {
                    controller.userFinishShipboard(myId);
                }
                case "11:1" -> {//selectTypeOfToken
                    try {
                        insertAliens(myId);
                    } catch (Exception e) {
                        printUpdateMessage(error + e.getMessage());
                    }
                }
                case "12:1" -> {//go back
                    updatePlaceTile(myId, new Point(7, 7));
                }
                case "13:1" -> {//insertRandomTiles
                    try {
                        int numberOfTiles = Integer.parseInt(lineReader.readLine("insert the number of tiles >> ").trim());
                        if(numberOfTiles>0) insertRandomTiles(numberOfTiles);
                        else printUpdateMessage(error + "invalid input");
                        //controller.userFinishShipboard(myId);
                    } catch (Exception e) {
                        printUpdateMessage(error+e.getMessage());
                    }
                }
                case "14:1" -> {//loadShipboard
                    try {
                        lineReader.printAbove("which ship do you want to load:\n " +
                                "1 --> ship_layout_1.json\n " +
                                "2 --> ship_layout_2.json\n " +
                                "3 --> ship_layout_3.json\n"  +
                                "4 --> ship_layout_4.json\n" );

                        String s = lineReader.readLine(prompt).trim();
                        switch (s){
                            case "1" ->readShipboardFromFile("ship_layout_1.json");
                            case "2" ->readShipboardFromFile("ship_layout_2.json");
                            case "3"->readShipboardFromFile("ship_layout_3.json");
                            case "4"->readShipboardFromFile("ship_layout_4.json");
                            default -> printUpdateMessage(error + "invalid input");
                        }
                    } catch (Exception e) {
                        printUpdateMessage(error + e.getMessage());
                    }
                }

                case "1:2" -> {//DrawAdventureCard
                    controller.userDrawAdventureCard(myId);
                }
                case "2:2" -> {//ThrowDices
                    controller.userThrowDices(myId);
                }
                case "3:2" -> {//ActivateComponent
                    HashSet<Point> points = new HashSet<>();
                    lineReader.printAbove("do you want to activate some components? (y/n) >> ");
                    String s = lineReader.readLine().trim();
                    if (s.equalsIgnoreCase("y")) {
                        lineReader.printAbove("insert all the points you want to activate (s to stop):  ");
                        while (!s.equalsIgnoreCase("s")) {
                            try {
                                s = lineReader.readLine("Insert the coordinates (x,y) >> ");
                                if( s.equalsIgnoreCase("s")) break;
                                String[] coordinates = s.trim().split("\\s+");
                                int x = Integer.parseInt(coordinates[0]);
                                int y = Integer.parseInt(coordinates[1]);
                                points.add(new Point(x, y));
                            } catch (Exception e) {
                                printUpdateMessage(error + e.getMessage());
                            }
                        }
                        if (!points.isEmpty()) controller.userFinishDecisionActivating(myId, points);
                        else printUpdateMessage(error + "no point inserted");
                    } else {
                        controller.userFinishDecisionActivating(myId, points);
                    }

                }
                case "4:2" -> {//handleDecisionToken
                    lineReader.printAbove("do you want to handle the decision token? (y/n) >> ");
                    if (lineReader.readLine().equalsIgnoreCase("y")) {
                        ArrayList<Pair<Point, Token>> tokens = decisionToken();
                        if (!tokens.isEmpty()) controller.userFinishDecisionToken(myId, tokens);
                        else printUpdateMessage(error + "no point inserted");
                    }else controller.userFinishDecisionToken(myId, new ArrayList<>());
                }
                case "5:2" -> {//LandOnPlanet
                    try {
                        String s = lineReader.readLine("do you want to land? (y/n) >> ");
                        boolean isLanded = false;
                        int planetId = -1;
                        if (s.equalsIgnoreCase("y")) {
                            isLanded = true;
                            s = lineReader.readLine("Insert the planet id >> ");
                            planetId = Integer.parseInt(s.trim());
                        }
                        controller.userLandOnPlanet(myId, isLanded, planetId);
                    } catch (Exception e) {
                        printUpdateMessage(error + e.getMessage());
                    }
                }
                case "6:2" -> {//LandOnStationOrShip
                    try {
                        String s = lineReader.readLine("do you want to land? (y/n) >> ");
                        controller.userLandOnStationOrShip(myId, s.equalsIgnoreCase("y"));
                    } catch (Exception e) {
                        printUpdateMessage(error + e.getMessage());
                    }
                }
                case "7:2" -> {//AcceptPrizes
                    try {
                        lineReader.printAbove("do you want to accept the prizes? (y/n) >> ");
                        String s = lineReader.readLine().trim();
                        controller.userAcceptPrizes(myId, s.equalsIgnoreCase("y"));
                    } catch (Exception e) {
                        updateShipBoard(myId);
                    }
                }
                case "8:2" -> {//stopFlight
                    try {
                        lineReader.printAbove("do you really want to stop flight? (y/n) >> ");
                        String s = lineReader.readLine().trim();
                        if (s.equalsIgnoreCase("y")) controller.userQuitGame(myId);
                        else printUpdateMessage("flight not stopped");
                    } catch (Exception e) {
                        printUpdateMessage(error + e.getMessage());
                    }
                }
                case "9:2" -> {//lookAtShipboard
                    lineReader.printAbove("which ship do you want to see?\n");
                    try {
                        for (Player p : gameSession.getGame().get().getListPlayers()) {
                            terminal.writer().println(p.getIdPlayer() + " --> [" + p.getUsername() + "]");
                        }
                        int id = Integer.parseInt(lineReader.readLine("insert the playerId >> ").trim());
                        if (gameSession.getPlayer(id).isPresent()) updateShipBoard(id);
                        else printUpdateMessage(error + "player not found");
                    } catch (Exception e) {
                        printUpdateMessage(error + e.getMessage());
                    }
                }
                case "10:2" -> {//StopFlight
                    try {
                        lineReader.printAbove("do you really want to stop flight? (y/n) >> ");
                        String s = lineReader.readLine().trim();
                        if (s.equalsIgnoreCase("y")) {
                            controller.userStopFlight(myId);
                            printUpdateMessage("you will land at the end of this turn, you can still play");
                        }
                        else printUpdateMessage("flight not stopped");
                    } catch (Exception e) {
                        printUpdateMessage(error + e.getMessage());
                    }
                }
//                case "12:2" -> {//go back
//                    printUpdateMessage("goBack not implemented yet");
//                }
                case "0:3" -> {//EndGame
                    controller.adminEndGame(myId);
                }
                case "1:3" -> {//CalculateScore
                    controller.adminCalculateScore(myId);
                }
                default -> printUpdateMessage("⚠ Error : invalid command");
            }
        } catch (Exception e) {
            printUpdateMessage("(errore switch) "+error + e.getMessage());
            //e.printStackTrace();
        }
    }
}


