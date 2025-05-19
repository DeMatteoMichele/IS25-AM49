package it.polimi.ingsw.is25am49.client;

import java.awt.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.*;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalUsernameException;
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
    private final String serverIP = "localhost";
    private final int serverPort = 8081;
    private int connectionType;
    //---------------------------------------

    private ClientController controller;
    private GameSession gameSession;
    private String username;
    private int myId=-1;

    private final String error = "⚠ Error : ";
    private final String prompt = ">> ";
    private final LinkedBlockingDeque<String> graphicsQueue;

    public Terminal terminal;
    public LineReader lineReader;
    ExecutorService executor ;

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
     * IL MAIN DI GALAGY TRUCKER TUI
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
            if (username.length() > 20 || username.isEmpty()) {
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
                    ClientRMI clientRMI = new ClientRMI(gameSession);
                    try {
                        controller.setServerMethods(clientRMI.connect(serverIP, serverPort));
                        try {
                            //todo registerClientRmi con settaggio dell'id
                            System.out.println("trying to log in to the server ...");
                            UnicastRemoteObject.exportObject(clientRMI, 0);
                            controller.registerClientRMI(clientRMI);
                            System.out.println("client logged");
                        } catch (RemoteException e) {
                            System.out.println("client failed logging on server RMI --> " + e.getMessage());
                            //se voglio riprovare a connettermi
                            System.out.println("\nwanna retry? Y");
                            if (lineReader.readLine(prompt).trim().equalsIgnoreCase("y")) setupConnection();
                            else {
                                printUpdateMessage("Server: closing...");
                                System.exit(0);
                            }
                        }
                    } catch (RemoteException e) {
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
                    ClientSerializer clientSerializer = new ClientSerializer(gameSession, controller);
                    ServerHandler serverHandler = new ServerHandler(serverIP, serverPort);
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
                handleInput(inputString.toLowerCase());
                terminal.flush();
            }
        });
        executor.submit(() -> {
            try {
                Thread.sleep(1000);//aspetto un attimo per far arrivare i pacchetti
                while (true) {
                    String graphic = graphicsQueue.take();
                    if(!graphicsQueue.isEmpty()){
                        printUpdateMessage("RECONNETTING ...");
                        String next;
                        while ((next = graphicsQueue.poll()) != null) {
                            graphic = next;
                            Thread.sleep(100);
                        }
                        printUpdateMessage("successfully reconnected");
                        lineReader.printAbove(graphic);
                    }else{
                        clearConsole();
                        lineReader.printAbove(graphic);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // permette di fermare il thread in modo sicuro
            }
        });
    }
    public void clearConsole() {
        for (int i = 0; i < 15; i++) {
            terminal.writer().println();
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
            block = new AttributedStringBuilder().append(msg + "\n", AttributedStyle.BOLD.foreground(AttributedStyle.CYAN)).toAttributedString();
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
                lineReader.printAbove(stringBuilder.toString()); //TODO eventualmente fare graphicQueue.add
                updateDiscardTile(myId);
            }
            default -> {
                printUpdateMessage(error + "stateIndex: " + stateIndex);
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

    public String buildStateMenu(){
        return """
                BuildState - commands:\s
                1.  PickTile
                2.  RotateTileClockWise
                3.  DiscardTile
                4.  DrawTile
                5.  FinishShipboard
                6.  LookSmallAdventureCardDeck
                7.  StopLookSmallAdventureCardDeck
                8.  PlaceTimer
                9.  TimerIsDone
                10. SelectTypeOfToken
                """;
    }
    @Override
    public void updatePlaceTile(int playerId, Point coordinates) {

    }

    @Override
    public void updatePickTile(int playerId, Point coordinates) {

    }

    @Override
    public void updateRotateTileClockWise(int playerId) {

    }

    @Override
    public void updateDiscardTile(int playerId) {
        if(playerId == myId){
            String str = "BUILD STATE\n"+"_".repeat( 200) + "\n\n" +
                    gameSession.getGame().get().getGameboard().getTilesDeck() +
                    buildStateMenu() +
                    "_".repeat( 200)+"\n" +
                    "choose an option:";
            graphicsQueue.add(str);
        }
    }

    @Override
    public void updateDrawTile(int playerId, int idTIle) {

    }

    @Override
    public void updateFinishShipboard(int playerId) {

    }

    @Override
    public void updateLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {

    }

    @Override
    public void updateStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {

    }

    @Override
    public void updatePlaceTimer(int playerId) {

    }

    @Override
    public void updateTimerIsDone() {

    }

    @Override
    public void updateSelectTypeOfToken(int playerId, Token token, Point coordinates) {

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
                case "1:1"->{
                    printUpdateMessage("commando PickTile non ancora implementato\n");
                }
                case "2:1"->{
                    printUpdateMessage("commando RotateTileClockWise non ancora implementato\n");
                }
                case "3:1"->{
                    printUpdateMessage("commando DiscardTile non ancora implementato\n");
                }
                case "4:1"->{
                    printUpdateMessage("commando DrawTile non ancora implementato\n");
                }
                case "5:1"->{
                    printUpdateMessage("commando FinishShipboard non ancora implementato\n");
                }
                case "6:1"->{
                    printUpdateMessage("commando LookSmallAdventureCardDeck non ancora implementato\n");
                }
                case "7:1"->{
                    printUpdateMessage("commando StopLookSmallAdventureCardDeck non ancora implementato\n");
                }
                case "8:1"->{
                    printUpdateMessage("commando PlaceTimer non ancora implementato\n");
                }
                case "9:1"->{
                    printUpdateMessage("commando TimerIsDone non ancora implementato\n");
                }
                case "10:1"->{
                    printUpdateMessage("commando SelectTypeOfToken non ancora implementato\n");
                }
                default -> printUpdateMessage("⚠ Error : invalid command");
            }
        } catch (Exception e) {
            printUpdateMessage("(errore switch) "+error + e.getMessage());
        }
    }
}


