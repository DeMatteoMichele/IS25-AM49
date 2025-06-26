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
    private String serverIP = "localhost"; //default IP
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

    /**
     * Initializes a new instance of the TextualUserInterface class.
     * Sets up the game session, terminal interface, and a thread pool executor.
     * Prepares the graphics queue for managing user interface updates.
     */
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

    /**
     * Sets the identifier for the client.
     * @param myId the unique identifier to assign to the client
     */
    @Override
    public void setClientId(int myId) {
        this.myId = myId;
    }

    /**
     * Sets the current game session for the TextualUserInterface.
     *
     * @param gameSession the GameSession object to set as the current session
     */
    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

//----------------------------------------------------------------------------------------------------

    /**
     * Main entry point for the TextualUserInterface application.
     * Initializes the UI, sets up the connection, and starts the game.
     *
     * @param args Optional command-line arguments. If provided, the first argument is used as the server IP address.
     */
    public static void main(String args[]){
        TextualUserInterface ui = new TextualUserInterface();
        if(args.length != 0)ui.serverIP=args[0];
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
     * Sets up the connection to the server by allowing the user to select
     * the desired connection type and initiating the registration process.
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
     * Handles the user input for setting the username.
     * Provides options for reconnecting to a previous game or creating a new username.
     * Validates the input and invokes controller to set or initialize corresponding data.
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
     * Establishes the client connection to the server using either RMI or Socket
     * based on the connection type. Handles errors and retries as needed.
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

    /**
     * Establishes a connection to the RMI server and handles the client login process
     * with support for reconnection.
     *
     * @param isReconnecting specifies whether the client is attempting to reconnect to the server.
     * @param clientRMI an instance of ClientRMI representing the RMI client logic to interact with the server.
     * @throws NotBoundException if the requested binding is not found in the registry.
     * @throws IOException if an error occurs during communication with the server.
     */
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

    /**
     * Starts the game by handling user input and graphical updates asynchronously.
     * Manages input commands like "quit" and "reconnect" while also rendering graphics.
     * Utilizes multiple threads to maintain separate input processing and rendering tasks.
     */
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

    /**
     * Clears the console by printing empty lines.
     * Used to improve readability of the terminal output.
     */
    public void clearConsole() {
        for (int i = 0; i < 3; i++) {
            terminal.writer().println();
        }
    }

    /**
     * Inserts aliens onto the specified player's shipboard.
     * @param playerId the ID of the player whose shipboard will have aliens inserted
     */
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

    /**
     * Allows the user to assign tokens to specific coordinates on the shipboard until the user decides to stop.
     * Tokens are chosen interactively from a list of available options.
     * @return A list of pairs representing the chosen coordinates and their corresponding tokens.
     */
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
                        "br --> brown aliens\n" +
                        "pu --> purple aliens\n" +
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
                    case "br":
                        token = Token.BROWN_ALIENS;
                        break;
                    case "pu":
                        token = Token.PURPLE_ALIENS;
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

    /**
     * Inserts a specified number of random tiles into the player's shipboard, adhering to game rules and constraints.
     * Attempts to place, rotate, and draw tiles in randomized locations and orientations if allowed.
     * Recursively continues until the specified number of tiles are placed or the shipboard is full.
     *
     * @param numberOfTiles the number of tiles to be randomly inserted onto the shipboard; must be a positive integer
     */
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

    /**
     * Reads shipboard data from a specified file and performs operations like
     * drawing, rotating, and placing tiles based on the data.
     *
     * @param filename the name of the file containing shipboard information
     */
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

    /**
     * Prints an update message with a styled output depending on the content of the message.
     * @param msg the message to be printed, where specific formatting is applied based on its content
     */
    @Override
    public void printUpdateMessage(String msg) {
        AttributedString block;
        if (msg.contains(error))
            block = new AttributedStringBuilder().append(msg + "\n", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)).toAttributedString();
        else
            block = new AttributedStringBuilder().append(msg + "\n", AttributedStyle.INVERSE_OFF.foreground(AttributedStyle.CYAN)).toAttributedString();
        lineReader.printAbove(block.toAnsi());
    }

    /**
     * Updates the graphical representation of the game's state transition based
     * on the provided state index.
     *
     * @param stateIndex the index representing the current game state, where:
     *                   1 corresponds to the Build State,
     *                   2 corresponds to the Adventure State,
     *                   3 corresponds to the Finish State.
     */
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
            }
        }
    }

    /**
     * Updates the game session for a new player by adding relevant information to the graphics queue
     * if the player is not already in the game lobby.
     *
     * @param playerId the unique identifier of the player to be added to the game session
     */
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

    /**
     * Updates the game session and creates a game based on the given parameters.
     *
     * @param playerId   the ID of the player initiating the game creation
     * @param gameMode   the mode of the game to be created
     * @param maxPlayers the maximum number of players allowed in the game
     */
    @Override
    public void updateGameSessionCreateGame(int playerId, GameMode gameMode, int maxPlayers) {
        updateGameNewPlayer(playerId);
    }

    /**
     * Updates the game state when a new player joins the game session.
     *
     * @param playerId the ID of the newly joined player
     */
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

    /**
     * Builds and returns the menu for the build state, listing available commands and their indices.
     *
     * @return A string representation of the build state menu with command options.
     */
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

    /**
     * Updates the UI and displays the build state menu when the player places a tile.
     *
     * @param playerId    the ID of the player triggering the action
     * @param coordinates the coordinate where the tile is placed
     */
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

    /**
     * Updates the game state when a player picks a tile.
     *
     * @param playerId the ID of the player picking the tile
     * @param coordinates the coordinates of the selected tile
     * @param idTile the ID of the tile being picked
     */
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

    /**
     * Updates the interface to reflect changes when a tile is rotated clockwise by a player.
     * Displays the current game state, the player's details, and available menu options.
     *
     * @param playerId the identifier of the player rotating the tile
     */
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

    /**
     * Updates the discard tile action for the specified player.
     * @param playerId the ID of the player performing the discard action
     * @param idTile the ID of the tile being discarded
     */
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

    /**
     * Updates the game state for a tile drawn by a player.
     * @param playerId the ID of the player who drew the tile
     * @param idTIle the ID of the tile drawn
     */
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

    /**
     * Updates the finish shipboard state for the specified player and adds the
     * corresponding information to the graphics queue for display.
     *
     * @param playerId the ID of the player finishing their shipboard setup
     */
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

    /**
     * Updates the view of the specified small adventure card deck for the player.
     *
     * @param playerId       The ID of the player who triggered the action.
     * @param smallDeckIndex The index of the small adventure card deck to be displayed.
     */
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

    /**
     * Updates the state to stop looking at the small adventure card deck for a specified player.
     *
     * @param playerId the ID of the player who stops looking at the small adventure card deck
     * @param smallDeckIndex the index of the small adventure card deck being accessed
     */
    @Override
    public void updateStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        if(playerId == myId){
            updatePlaceTile(myId,new Point(7,7));
        }
    }

    /**
     * Updates the game state to reflect the placement of a timer on the board by the specified player.
     *
     * @param playerId the unique identifier of the player placing the timer
     */
    @Override
    public void updatePlaceTimer(int playerId) {
        var p= gameSession.getPlayer(playerId).get();
        printUpdateMessage("user: "+p.getUsername() +" placed the timer on the board\n"+gameSession.getGame().get().getTimer());
    }

    /**
     * Displays a notification indicating that the timer has completed.
     * Outputs a preformatted message to notify all users.
     */
    @Override
    public void updateTimerIsDone() {
        printUpdateMessage("""
                +-----------------------+
                | !!! TIMER IS DONE !!! |
                +-----------------------+
                """);
    }

    /**
     * Updates the selected type of token for a player at specified coordinates.
     *
     * @param playerId the unique identifier of the player
     * @param token the token to be added or updated
     * @param coordinates the location where the token will be updated
     */
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

    /**
     * Displays the shipboard of the specified player by generating and queuing a formatted string representation.
     * @param playerId the unique identifier of the player whose shipboard is to be displayed
     */
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
     * Updates the state of the card for a specific player.
     *
     * @param playerId the ID of the player whose card state is to be updated
     * @param cardState the new state of the card to be applied
     */
    @Override
    public void updateCardState(int playerId, AdventureStatesTypes cardState) {

    }

    /**
     * Updates the player's turn in the game.
     *
     * @param playerId the unique identifier of the player whose turn is being updated
     */
    @Override
    public void updateChangeTurn(int playerId) {

    }

    /**
     * Displays the menu options for the AdventureState, listing available commands.
     *
     * @return A string representing the AdventureState command menu.
     */
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
    /**
     * Updates the display message for a generic card action in the game, formatting it for the intended recipient(s).
     *
     * @param playerId  the ID of the player to whom the message should be displayed, or -99 for all players.
     * @param message   the card-related information to be displayed.
     */
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

    /**
     * A 2D array representing visual representations of dice faces.
     * Each sub-array corresponds to dice faces ranging from 1 to 6.
     * The contents of each sub-array are formatted as strings for graphical display.
     */
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
    /**
     * Updates the visual representation of the dice throw for a specific player in the game session.
     *
     * @param playerId the identifier of the player who is throwing the dice
     * @param firstDice the value of the first dice (1-6)
     * @param secondDice the value of the second dice (1-6)
     */
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

    /**
     * Updates the game state when a player draws an adventure card.
     * @param playerId the unique identifier of the player drawing the card
     * @param adventureCard the adventure card that was drawn by the player
     */
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

    /**
     * Updates the decision to finish activating specific coordinates for a player's game session.
     *
     * @param playerId The unique identifier of the player.
     * @param coordinates The set of coordinate points being activated.
     */
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

    /**
     * Updates the finish decision token process for a player.
     *
     * @param playerId     the unique identifier of the player making the update
     * @param listOfTokens the list of tokens with their corresponding positions
     * @param numTokens    the number of tokens to process in the update
     * @param removing     a flag indicating if tokens are being removed or inserted
     */
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

    /**
     * Updates the game state by marking specific areas or actions as deactivating for a given player.
     *
     * @param playerId    the unique identifier of the player initiating the deactivation.
     * @param coordinates the list of coordinates to deactivate within the game.
     */
    @Override
    public void updateDeactivating(int playerId, List<Point> coordinates) {
        //non chiaro
    }


    /**
     * Updates the status of whether a player has landed on a specific planet.
     *
     * @param playerId the unique identifier of the player
     * @param isLanded indicates if the player decided to land on the planet
     * @param planetId the unique identifier of the planet
     */
    @Override
    public void updateLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        Player p = gameSession.getPlayer(playerId).get();
        if(isLanded) updateGenericCardMessage(myId,"Player " + p.getUsername() + " has landed on planet with ID: " + planetId+ " successfully!");
        else printUpdateMessage("Player " + p.getUsername() + " decided not to land");
    }

    /**
     * Updates the status of a player's decision regarding landing on a station or ship.
     *
     * @param playerId the ID of the player making the decision
     * @param isLanded indicates whether the player decided to land
     * @param cardId the ID of the card associated with the decision
     */
    @Override
    public void updateLandOnStationOrShip(int playerId, boolean isLanded, String cardId) {
        Player p = gameSession.getPlayer(playerId).get();
        if(isLanded) updateGenericCardMessage(myId,"Player " + p.getUsername() + " decided to land on the station or ship with cardID: " + cardId);
        else printUpdateMessage("Player " + p.getUsername() + " decided not to land");
    }

    /**
     * Updates the acceptance status of prizes for a specified player.
     *
     * @param playerId the identifier of the player whose prize acceptance is being updated
     * @param isAccepted a boolean indicating whether the player has accepted the prizes
     */
    @Override
    public void updateAcceptPrizes(int playerId, boolean isAccepted) {
        Player p = gameSession.getPlayer(playerId).get();
        updateGenericCardMessage(myId,"Player " + p.getUsername() + " has "+(isAccepted?"accepted":"refused")+"  the prizes");
    }

    /**
     * Updates the game session when a player stops flight.
     *
     * @param playerId the unique identifier of the player who has stopped flight.
     */
    @Override
    public void updateStopFlight(int playerId) {
        Player p= gameSession.getPlayer(playerId).get();
        printUpdateMessage("!!!Player " + p.getUsername() + " has landed!!!\t-out of the game");
    }

    /**
     * Updates the game state after an enemy has been defeated by a player.
     *
     * @param playerIdWhoWon the ID of the player who defeated the enemy
     * @param enemyName the name of the defeated enemy
     * @param cardId the card ID associated with the defeated enemy
     */
    @Override
    public void updateEnemyDefeated(int playerIdWhoWon, String enemyName, String cardId) {

    }

    /**
     * Updates the shipboard display for a given player, reflecting the current game state.
     *
     * @param playerId the ID of the player whose shipboard state will be updated
     */
    public void updateShipBoard(int playerId) {
        String str = "ADVENTURE STATE\n"+"_".repeat( 200) + "\n\n" +
                gameSession.getPlayer(playerId).get().shipToString()+
                adventureStateMenu()+
                "_".repeat( 200)+"\n" +
                "choose an option:";
        graphicsQueue.add(str);
    }

    /**
     * Updates the UI to indicate that a crew member has been lost at a specific location.
     *
     * @param playerId the ID of the player who lost the crew member
     * @param coordinate the coordinates where the crew member was lost
     * @param token the type of token affected
     */
    @Override
    public void updateEffectModifyCrew(int playerId, Point coordinate, Token token) {
        updateGenericCardMessage(playerId,"you have lost a crew member on coordinates: <" +coordinate.x +","+coordinate.y +"> - token: " + token);
    }

    /**
     * Updates the user interface to reflect the effect of a cannonade on a player's shipboard.
     *
     * @param playerId the ID of the player whose shipboard is affected
     * @param coordinate the coordinates of the tile hit on the shipboard
     * @param or the orientation of the cannonade's impact
     */
    @Override
    public void updateEffectCannonade(int playerId, Point coordinate, Orientation or) {
        updateGenericCardMessage(playerId,"the cannonade successfully hit the shipboard\n -->tile in coordinates: <" +coordinate.x +","+coordinate.y +"> has been destroyed");
        updateShipBoard(playerId);
    }

    /**
     * Updates the effect indicating the loss of goods for a player at specified coordinates with a given token.
     * @param playerId the ID of the player experiencing the effect.
     * @param coordinate the coordinates where the goods are lost.
     * @param token the token representing the type of good lost.
     */
    @Override
    public void updateEffectLoseGoods(int playerId, Point coordinate, Token token) {
        updateGenericCardMessage(playerId,"you have lost a good on coordinates: <" +coordinate.x +","+coordinate.y +"> - token: " + token);
    }

    /**
     * Updates the game state to reflect that the player has won a good at the specified coordinates with a specific token.
     *
     * @param playerId   the ID of the player who won the good
     * @param coordinate the coordinates where the good was won
     * @param token      the type of token associated with the won good
     */
    @Override
    public void updateEffectWinGoods(int playerId, Point coordinate, Token token) {
        updateGenericCardMessage(playerId,"you have won a good on coordinates: <" +coordinate.x +","+coordinate.y +"> - token: " + token);
    }

    /**
     * Updates the number of goods won by a player.
     *
     * @param playerId the identifier of the player
     * @param redgoods the number of red goods won
     * @param yellowgoods the number of yellow goods won
     * @param greengoods the number of green goods won
     * @param bluegoods the number of blue goods won
     */
    @Override
    public void updateNumberOfWinGoods(int playerId, int redgoods, int yellowgoods, int greengoods, int bluegoods) {

    }

    /**
     * Updates the state of the energy effect by modifying the specified token at a given coordinate for the player.
     *
     * @param playerId the ID of the player whose state is being updated
     * @param coordinate the point on the board where the token is located
     * @param token the token to be modified or affected
     */
    @Override
    public void updateEffectModifyEnergy(int playerId, Point coordinate, Token token) {
        updateGenericCardMessage(playerId,"battery from coordinates: <" +coordinate.x +","+coordinate.y +"> have been removed");
    }

    /**
     * Updates the effect of a meteorite impact, destroying a tile on the shipboard and updating the game state.
     *
     * @param playerId the identifier of the player affected by the meteorite.
     * @param coordinate the x and y coordinates of the meteorite impact on the shipboard.
     * @param or the orientation related to the meteorite effect.
     */
    @Override
    public void updateEffectMeteorite(int playerId, Point coordinate, Orientation or) {
        updateGenericCardMessage(playerId,"the meteorite successfully hit the shipboard\n -->tile in coordinates: <" +coordinate.x +","+coordinate.y +"> has been destroyed");
        updateShipBoard(playerId);
    }

    /**
     * Updates the earned credits for a specific player and notifies them.
     *
     * @param playerId the unique identifier of the player
     * @param credits the amount of credits earned by the player
     */
    @Override
    public void updateEarnedCredits(int playerId, int credits) {
        updateGenericCardMessage(playerId, "you have earned " + credits + " credits");
    }

    /**
     * Updates the destroyed tiles for a player's ship board and broadcasts changes to the game session.
     *
     * @param idPlayer The ID of the player whose destroyed tiles are being updated.
     * @param destroyedTiles A set of points representing the coordinates of the destroyed tiles.
     */
    @Override
    public void updateDestroyedTiles(int idPlayer, Set<Point> destroyedTiles) {
        StringBuilder str = new StringBuilder();
        Player p = gameSession.getPlayer(idPlayer).get();
        if(!destroyedTiles.isEmpty()) {
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
    }

    /**
     * Updates the leaderboard display with the current game session state.
     *
     * @param leaderBoard the current leaderboard state to be displayed
     */
    @Override
    public void updateLeaderBoard(LeaderBoard leaderBoard) {
        String str = "ADVENTURE STATE\n"+"_".repeat( 200) + "\n\n" +
                gameSession.getLeaderBoard()+
                adventureStateMenu()+
                "_".repeat( 200)+"\n" +
                "choose an option:";
        graphicsQueue.add(str);
    }

    /**
     * Ends the game session and performs necessary cleanup operations.
     *
     * @param playerId the unique identifier of the player invoking the end-game process
     */
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

    /**
     * Updates and calculates the score for a given player.
     *
     * @param playerId the unique identifier of the player whose score is being calculated
     */
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
     * Handles user input and manages game state transitions and actions based on the provided input.
     *
     * @param input The command or action input provided by the user to interact with the game logic.
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
                                "3 --> ship_layout_3.json\n " +
                                "4 --> ship_layout_4.json\n " );

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
                    } else if(s.equalsIgnoreCase("n")){
                        controller.userFinishDecisionActivating(myId, points);
                    }else printUpdateMessage(error + "invalid input");

                }
                case "4:2" -> {//handleDecisionToken
                    lineReader.printAbove("do you want to decide for your tokens? (y/n) >> ");
                    String s = lineReader.readLine().trim();
                    if (s.equalsIgnoreCase("y")) {
                        ArrayList<Pair<Point, Token>> tokens = decisionToken();
                        if (!tokens.isEmpty()) controller.userFinishDecisionToken(myId, tokens);
                        else printUpdateMessage(error + "no point inserted");
                    }
                    else if(s.equalsIgnoreCase("n")) controller.userFinishDecisionToken(myId, new ArrayList<>());
                    else printUpdateMessage(error + "invalid input");
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
                    lineReader.printAbove("do you really want to end the game? (y/n) >> ");
                    String s = lineReader.readLine().trim();
                    if (s.equalsIgnoreCase("y"))controller.adminEndGame(myId);
                    else printUpdateMessage("game not ended");
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


