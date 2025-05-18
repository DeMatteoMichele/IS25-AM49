package it.polimi.ingsw.is25am49.client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.concurrent.*;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
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
    public void setGamesession(GameSession gameSession) {
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
                stringBuilder.append("\t+-------------------------------+\n")
                             .append("\t|       BUILD STATE :)          |\n")
                             .append("\t+-------------------------------+\n");
                lineReader.printAbove(stringBuilder.toString());
            }
            default -> {
                printUpdateMessage(error + "stateIndex: " + stateIndex);
            }
        }
    }

    @Override
    public void updateGameSessionNewPlayer(int playerId) {
        if (!gameSession.checkIsInLobby(myId)) {
            StringBuilder str = new StringBuilder();
            str.append("welcome to the game ").append(username).append("!\n");
            str.append("__________________________________________________________________\n\n");
            str.append(gameSession).append("\n");
            str.append("GameLobby - commands: \n").append("1. join lobby\n2. setup game\n");
            str.append("__________________________________________________________________\n");
            str.append("choose an option:");
            graphicsQueue.add(str.toString());
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
            switch (input.trim() + ":" + gameSession.getState().getStateIndex()) {
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
                    int mode = 0, max = 0;
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
                default -> printUpdateMessage("⚠ Error : invalid command");
            }
        } catch (Exception e) {
            printUpdateMessage("(errore switch) "+error + e.getMessage());
        }
    }
}


