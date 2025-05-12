package it.polimi.ingsw.is25am49.client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
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
    private ClientController controller;
    private GameSession gameSession;
    private String username;
    private int myId;

    private final String error = "⚠ Error : ";
    private final String prompt = ">> ";
    private final int graphicLength = 20;

    public Terminal terminal;
    public LineReader lineReader;

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
    }

    @Override
    public void setClientId(int myId) {
        this.myId = myId;
    }


    /**
     * IL MAIN DI GALAGY TRUCKER TUI
     */
    public static void main(String args[]) throws IOException {
        //Server settings:
        String serverIP = "localhost";
        int serverPort = 8080;
        //---------------------------------------

        TextualUserInterface ui = new TextualUserInterface();
        Terminal terminal = ui.terminal;
        LineReader reader = ui.lineReader;
        String textInput = "";

        int numInput = 0;
        terminal.writer().println("+------------------------------+");
        terminal.writer().println("| WELCOME TO GALAXY TRUCKER :) |");
        terminal.writer().println("+------------------------------+");

        //Utente inserisce Username
        while (true) {
            reader.printAbove("Insert your username:");
            textInput = reader.readLine(ui.prompt).trim();
            if (textInput.length() > 20 || textInput.isEmpty()) {
                ui.printUpdateMessage(ui.error + "username non valid");
                //terminal.writer().println(error+"username non valid");
            } else break;
        }
        ui.username = textInput;
        //---------------------------------------

        terminal.writer().println("\nwelcome to the game " + ui.username + "!");

        ui.setupConnection(serverIP, serverPort);
        ui.joinLobby(serverIP);
        terminal.flush();
        ui.startGame();
    }


//    ----------------------------------------------------------------------------------------------------------
//    MAIN  ↑
//    inizio metodi TUI:
//    - setupConnection
//    - joinLobby
//    - startGame
//    ----------------------------------------------------------------------------------------------------------

    /**
     * Method used from the TUI to register the client to the server via (Socket/RMI)
     *
     * @param serverIP   IP of the server
     * @param serverPort port of the server
     */
    public void setupConnection(String serverIP, int serverPort) {
        controller = new ClientController(gameSession, this);

        int numInput = 0;
        StringBuilder str = new StringBuilder();
        str.append("Which connection do you want to use:\n").
                append("\t1) RMI\n").
                append("\t2) Socket\n");
        //scelta della connessione
        while (true) {
            lineReader.printAbove(str.toString());
            try {
                numInput = Integer.parseInt(lineReader.readLine(prompt).trim());
            } catch (NumberFormatException _) {
            }
            if (numInput < 1 || numInput > 2) {
                printUpdateMessage(error + "invalid choice, retry");
            } else break;
        }

        terminal.flush();

//      CONNESSIONE AL SERVER + registrazione client
        try {
            switch (numInput) {

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
                            if (lineReader.readLine(prompt).trim().equals("Y")) setupConnection(serverIP, serverPort);
                            else {
                                printUpdateMessage("Server: closing...");
                                System.exit(0);
                            }
                        }
                    } catch (RemoteException e) {
                        printUpdateMessage("⚠ Error : something went wrong during the connection tio the server RMI --> " + e.getMessage());
                        //se voglio riprovare a connettermi
                        printUpdateMessage("\nwanna retry? Y");
                        if (lineReader.readLine(prompt).trim().equals("Y")) setupConnection(serverIP, serverPort);
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
                    controller.registerClientSocket();
                }
            }
        } catch (Exception e) {
            printUpdateMessage(error + e.getMessage());
            //se voglio riprovare a connettermi
            printUpdateMessage("\nwanna retry? Y");
            if (lineReader.readLine(prompt).trim().equalsIgnoreCase("y")) setupConnection(serverIP, serverPort);
            else {
                printUpdateMessage("closing...");
                System.exit(0);
            }
        }
    }


    /**
     * Method used to from the client to join the game lobby if possible
     *
     * @param serverIP
     */
    public void joinLobby(String serverIP) {
        int numInput = 0;
        StringBuilder str = new StringBuilder();
        str.append("+--------------------------------------------\n");
        str.append("| SERVER - ").append(serverIP).append("\tstatus - connected\n");
        str.append("+--------------------------------------------\n");
        str.append("| Aviable lobby:\n");
        if (gameSession.getGame().isPresent()) {
            terminal.writer().print("| 01) TestMatch - users " + gameSession.getGame().get().
                    numOfPlayers() + "/" + gameSession.getGame().get().getMaxPlayers());
            if (!gameSession.checkPlayers()) str.append(" - full\n");
        } else if (gameSession.getAdmin().isPresent()) str.append("| 01) TestMatch - users 1/4\n");
        else str.append("| 01) TestMatch - users 0/4 CREATE LOBBY\n");
        str.append("+--------------------------------------------\n");
        str.append("which one do you want to join?\n");
        lineReader.printAbove(str.toString());
        try {
            numInput = Integer.parseInt(lineReader.readLine(prompt).trim());
        } catch (NumberFormatException _) {
        }
        if (numInput != 1) {
            terminal.writer().println();
            printUpdateMessage(error + "invalid choice, retry");
            joinLobby(serverIP);
        } else try {
            controller.playerJoin(username);
        } catch (Exception e) {
            printUpdateMessage(error + e.getMessage());
            joinLobby(serverIP);
        }
    }

    public void startGame() throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        executor.submit(() -> {
            String inputString;
            while (true) {
                inputString = lineReader.readLine(prompt).trim();
                if (inputString.equalsIgnoreCase("quit")) {
                    printUpdateMessage("Server: closing...");
                    System.exit(0);
                }
                handleInput(inputString.toLowerCase());
                terminal.flush();
            }
        });
    }

//    ----------------------------------------------------------------------------------------------------------
//    MAIN ↑
//
//    inizio metodi interfaccia UserInterface: stampa delle grafiche
//    ----------------------------------------------------------------------------------------------------------

    @Override
    public void updateGameSessionNewPlayer(int playerId) {
        StringBuilder str = new StringBuilder();
        str.append("__________________________________________________________________\n\n").
                append("MENU gameSession - commands (only for admin): \n").
                append("- create game\n- set gamemode\n- set maxplayers\n").
                append(gameSession.toString()).append("\n");
        str.append("__________________________________________________________________\n");
        str.append("insert a command from above menu:");
        if (gameSession.getGame().isEmpty() && playerId == myId) {
            lineReader.printAbove(str.toString());
        }
        if (gameSession.getGame().isPresent()) {
            lineReader.printAbove(str.toString());
        }
    }

    @Override
    public void updateGameSessionCreateGame(int playerId, GameMode gameMode, int maxPlayers) {
        updateGameSessionNewPlayer(myId);
    }

    @Override
    public void printUpdateMessage(String msg) {
        AttributedString block;
        if (msg.contains(error))
            block = new AttributedStringBuilder().append(msg + "\n", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)).toAttributedString();
        else
            block = new AttributedStringBuilder().append(msg + "\n", AttributedStyle.BOLD.foreground(AttributedStyle.CYAN)).toAttributedString();
        lineReader.printAbove(block.toAnsi());
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
                case "create game:0" -> {
                    int mode = 0, max = 0;
                    lineReader.printAbove("\nSETTINGS\nSelect gamemode:\n\t0) FLIGHT_MODE\n\t1) LEVEL_1\n\t2) LEVEL_2\n");
                    try {
                        mode = Integer.parseInt(lineReader.readLine(">> ").trim());
                    } catch (NumberFormatException _) {
                    }
                    try {
                        max = Integer.parseInt(lineReader.readLine("insert max players:\n>> ").trim());
                    } catch (NumberFormatException _) {
                    }
                    if (mode >= 0 && mode < 3 && max > 0)
                        controller.adminSetup(max, GameMode.values()[mode]);
                    else {
                        printUpdateMessage("⚠ Error : invalid settings");
                        updateGameSessionCreateGame(myId, GameMode.DEFAULT, max);
                    }
                }
                case "set maxplayers:0" -> {
                    printUpdateMessage("comando non ancora implementato");
                }
                default -> printUpdateMessage("⚠ Error : invalid command");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}


