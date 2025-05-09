package it.polimi.ingsw.is25am49.client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import it.polimi.ingsw.is25am49.client.model.*;
import it.polimi.ingsw.is25am49.client.model.enums.Color;
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
        }catch (IOException e){
            System.err.println("errore creazione terminale TUI: "+e.getMessage());
        }
    }
    public static void main(String args[]) throws IOException {
        //Server settings:
        String serverIP = "localhost";
        int serverPort = 8080;
        //---------------------------------------

        TextualUserInterface ui = new TextualUserInterface();
        Terminal terminal=ui.terminal;
        LineReader reader=ui.lineReader;
        String textInput = "";


        boolean wrongInput=false;
        int numInput = 0;
        String prompt= ">> ";
        String error="⚠ Error : ";
        terminal.writer().println("+------------------------------+");
        terminal.writer().println("| WELCOME TO GALAXY TRUCKER :) |");
        terminal.writer().println("+------------------------------+");

        while (!wrongInput) {
            terminal.writer().println("Insert your username:");
            terminal.flush();
            textInput=reader.readLine(prompt).trim();
            wrongInput= textInput.length() <= 20 && !textInput.isEmpty();
            if (!wrongInput) {
                System.err.println(error+"username non valid");
                //terminal.writer().println(error+"username non valid");
            }
        }wrongInput=false;
        ui.username = textInput;
        terminal.writer().println("\nwelcome to the game "+ ui.username+"!");

        while (!wrongInput) {
            terminal.writer().println("Which connection do you want to use:");
            terminal.writer().println("\t1) RMI");
            terminal.writer().println("\t2) Socket");
            terminal.flush();
            numInput= Integer.parseInt(reader.readLine(prompt).trim());
            wrongInput= numInput>=1 && numInput<=2;
            if (!wrongInput) {
                System.err.println(error+"invalid choice, retry");
                //terminal.writer().println(error+"invalid choice, retry");
            }
        }wrongInput=false;
        terminal.flush();

        //stabilisco la connessione todo gestione errori di connessione al server
        ui.setupConnection(numInput,serverIP,serverPort);

        while (!wrongInput) {
            terminal.writer().println("+--------------------------------------------");
            terminal.writer().println("| SERVER - "+serverIP+"\tstatus - connected");
            terminal.writer().println("+--------------------------------------------");
            terminal.writer().println("| Aviable lobby:");
            terminal.writer().println("| 01) TestMatch - users 0/4");
//            terminal.writer().print("| 01) TestMatch - users "+ui.session.getGame().get().numOfPlayers()+"/4"); //todo se game non fosse optional
//            if(ui.session.getGame().get().numOfPlayers()>=4) terminal.writer().println(" full");
//            else terminal.writer().println("");
            terminal.writer().println("+--------------------------------------------");
            terminal.writer().println("which one do you want to join?");
            terminal.flush();
            numInput= Integer.parseInt(reader.readLine(prompt).trim());
            wrongInput= numInput==1;
            if (!wrongInput) {
                terminal.writer().println(error+"invalid choice, retry");
            }
        }wrongInput=false;

        terminal.flush();
        ui.gameSession.getState().userJoinLobby(Color.BLUE, ui.username,1);
        ui.startGame();
    }

    public void setupConnection(int connectionType, String serverIP, int serverPort) {
        controller= new ClientController(gameSession);
        switch (connectionType) {
            case 1 -> {
                ClientRMI clientRMI = new ClientRMI(gameSession);
                try {
                    controller.setServerMethods(clientRMI.connect(serverIP, serverPort));
                } catch (RemoteException e) {
                    System.out.println("Errore durante la connessione al server");
                }
                try {
                    //todo registerClientRmi con settaggio dell'id
                    System.out.println("trying to log in to the server ...");
                    UnicastRemoteObject.exportObject(clientRMI, 0);
                    //controller.playerID=clientRMI.getServerStub().registerClientRMI(clientRMI);
                    System.out.println("client logged");
                } catch (RemoteException e) {
                    System.out.println("errore client si registra su server: " + e.getMessage());
                }
            }
            case 2 -> {
                ClientSerializer clientSerializer = new ClientSerializer(gameSession);
                ServerHandler serverHandler = new ServerHandler(serverIP, serverPort);
                serverHandler.setClientSerializer(clientSerializer);
                clientSerializer.setServerHandler(serverHandler);
                controller.setServerMethods(clientSerializer);
                //todo registraclient
                //controller.registerClientSocket();
            }
        }
    }

    public void startGame() throws IOException {
        String prompt = ">> ";
        String error="⚠ Error : ";
        ExecutorService executor = Executors.newFixedThreadPool(5);
        executor.submit(() -> {
            String inputString;
            while (true) {
                inputString=lineReader.readLine(prompt).trim();
                if (inputString.equalsIgnoreCase("quit")) {
                    System.out.println("Server: Sto chiudendo...");
                    //todo non so come si spenga il server
                    System.exit(0);
                }
                handleInput(inputString.toLowerCase());
            }
        });
    }

    @Override
    public void updateGameSessionNewPlayer(Object delta) {
        terminal.writer().println("__________________________________________________________________\n");
        terminal.writer().println("MENU gameSession - commands (only for admin): ");
        terminal.writer().println("- start\n- createGame\n- cicciogamer\n");
        terminal.writer().println(gameSession.toString());
        terminal.writer().println();
        terminal.writer().println("__________________________________________________________________");
        terminal.writer().println("insert a command from above menu:");
        terminal.flush();
    }

    public void handleInput(String input) {
        try{
            switch (input.trim()){
                case "start"->{
                    System.out.println("comando start non ancora implementato");
                }
                case "stop"->{
                    System.out.println("comando stop non ancora implementato");
                }
                case "creategame"->{
                    gameSession.getState().adminCreateGame(gameSession.getAdmin().orElse(null));
                }
                default ->terminal.writer().println("⚠ Error : invalid command");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}


