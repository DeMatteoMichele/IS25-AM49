package it.polimi.ingsw.is25am49.client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.polimi.ingsw.is25am49.client.model.*;
import it.polimi.ingsw.is25am49.client.model.enums.Color;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class TextualUserInterface implements UserInterface {
    private ClientController controller;
    private GameSession gameSession;
    private int myID;
    private String username;

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

    public static void main(String args[]) throws IOException {
        //Server settings:
        String serverIP = "localhost";
        int serverPort = 8080;
        //---------------------------------------

        TextualUserInterface ui = new TextualUserInterface();
        Terminal terminal = ui.terminal;
        LineReader reader = ui.lineReader;
        String textInput = "";

        boolean wrongInput = false;
        int numInput = 0;
        String prompt = ">> ";
        String error = "⚠ Error : ";
        terminal.writer().println("+------------------------------+");
        terminal.writer().println("| WELCOME TO GALAXY TRUCKER :) |");
        terminal.writer().println("+------------------------------+");

        while (!wrongInput) {
            terminal.writer().println("Insert your username:");
            terminal.flush();
            textInput = reader.readLine(prompt).trim();
            wrongInput = textInput.length() <= 20 && !textInput.isEmpty();
            if (!wrongInput) {
                System.err.println(error + "username non valid");
                //terminal.writer().println(error+"username non valid");
            }
        }
        wrongInput = false;
        ui.username = textInput;
        terminal.writer().println("\nwelcome to the game " + ui.username + "!");

        ui.setupConnection(serverIP, serverPort);

        while (true) {
            terminal.writer().println("+--------------------------------------------");
            terminal.writer().println("| SERVER - " + serverIP + "\tstatus - connected");
            terminal.writer().println("+--------------------------------------------");
            terminal.writer().println("| Aviable lobby:");
            //terminal.writer().println("| 01) TestMatch - users 0/4");
            if (ui.gameSession.getGame().isPresent()) {
                terminal.writer().print("| 01) TestMatch - users " + ui.gameSession.getGame().get().numOfPlayers() + "/" + ui.gameSession.getGame().get().getMaxPlayers());
                if (!ui.gameSession.checkPlayers()) terminal.writer().println(" - full");
            } else if (ui.gameSession.getAdmin().isPresent()) terminal.writer().println("| 01) TestMatch - users 1/4");
            else terminal.writer().println("| 01) TestMatch - users 0/4 create lobby");
            terminal.writer().println("+--------------------------------------------");
            terminal.writer().println("which one do you want to join?");
            terminal.flush();
            try {
                numInput = Integer.parseInt(reader.readLine(prompt).trim());
            } catch (NumberFormatException _) {
            }
            if (numInput != 1) {
                terminal.writer().println();
                System.err.println("⚠ Error : invalid choice, retry\n");
            } else try {
                ui.controller.playerJoin(ui.username);
                //ui.gameSession.getState().userJoinLobby(Color.BLUE, ui.username, 23);
                break;
            } catch (Exception e) {
                terminal.writer().println();
                System.err.println(error + e.getMessage());
            }
        }
        terminal.flush();
        ui.startGame();
    }

    public void setupConnection(String serverIP, int serverPort) {
        controller = new ClientController(gameSession, this);
        int numInput = 0;
        String prompt = ">> ";
        String error = "⚠ Error : ";
        while (true) {
            terminal.writer().println("Which connection do you want to use:");
            terminal.writer().println("\t1) RMI");
            terminal.writer().println("\t2) Socket");
            terminal.flush();
            try {
                numInput = Integer.parseInt(lineReader.readLine(prompt).trim());
            } catch (NumberFormatException _) {
            }
            if (numInput < 1 || numInput > 2) {
                System.err.println(error + "invalid choice, retry");
            } else break;
        }

        terminal.flush();

        try {
            switch (numInput) {
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
                            System.out.println("⚠ Error : client failed logging on server RMI --> " + e.getMessage());
                            //se voglio riprovare a connettermi
                            System.out.println("\nwanna retry? Y");
                            if (lineReader.readLine(prompt).trim().equals("Y")) setupConnection(serverIP, serverPort);
                            else {
                                System.exit(0);
                                System.out.println("Server: closing...");
                            }
                        }
                    } catch (RemoteException e) {
                        System.err.println("⚠ Error : something went wrong during the connection tio the server RMI --> " + e.getMessage());
                        //se voglio riprovare a connettermi
                        System.out.println("\nwanna retry? Y");
                        if (lineReader.readLine(prompt).trim().equals("Y")) setupConnection(serverIP, serverPort);
                        else {
                            System.exit(0);
                            System.out.println("closing...");
                        }
                    }
                }
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
            System.err.println("⚠ Error : " + e.getMessage());
            //se voglio riprovare a connettermi
            System.out.println("\nwanna retry? Y");
            if (lineReader.readLine(prompt).trim().equalsIgnoreCase("y")) setupConnection(serverIP, serverPort);
            else {
                System.out.println("closing...");
                System.exit(0);
            }
        }
    }

    public void startGame() throws IOException {
        String prompt = ">> ";
        String error = "⚠ Error : ";
        ExecutorService executor = Executors.newFixedThreadPool(5);
        executor.submit(() -> {
            String inputString;
            while (true) {
                inputString = lineReader.readLine(prompt).trim();
                if (inputString.equalsIgnoreCase("quit")) {
                    System.out.println("Server: closing...");
                    System.exit(0);
                }
                handleInput(inputString.toLowerCase());
                terminal.flush();
            }
        });
    }

    @Override
    public void updateGameSessionNewPlayer(int playerId) {
        terminal.writer().println("__________________________________________________________________\n");
        terminal.writer().println("MENU gameSession - commands (only for admin): ");
        terminal.writer().println("- create game\n- set gamemode\n- set maxplayers\n");
        terminal.writer().println(gameSession.toString());
        terminal.writer().println();
        terminal.writer().println("__________________________________________________________________");
        terminal.writer().println("insert a command from above menu:");
        terminal.flush();
    }

    @Override
    public void updateGameSessionSettings(GameMode gameMode, int maxPlayers) {
        updateGameSessionNewPlayer(999);
    }

    @Override
    public void printUpdateMessage(String msg) {
        System.err.println("⚠ Error : "+msg);
    }

    /**
     *
     * @param input string
     */
    public void handleInput(String input) {
        try {
            switch (input.trim() + ":" + gameSession.getState().getStateIndex()) {
                case "create game:0" -> {
                    int mode = 0, max = 0;
                    terminal.writer().println("\nSET SETTINGS\n\nSelect gamemode:\n0) FLIGHT_MODE\n1) LEVEL_1\n2) LEVEL_2\n");
                    try {
                        mode = Integer.parseInt(lineReader.readLine(">> ").trim());
                    } catch (NumberFormatException _) {
                    }
                    try {
                        max = Integer.parseInt(lineReader.readLine("\ninsert max players:\n>> ").trim());
                    } catch (NumberFormatException _) {
                    }
                    if (mode >= 0 && mode < 3 && max>0)
                        controller.adminSetup(max ,GameMode.values()[mode] );
                    else {
                        terminal.writer().println("⚠ Error : invalid settings");
                        updateGameSessionSettings(GameMode.DEFAULT,999);
                    }
                }
                case "set maxplayers:0" -> {
                    System.out.println("comando non ancora implementato");
                }
                default -> terminal.writer().println("⚠ Error : invalid command");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}


