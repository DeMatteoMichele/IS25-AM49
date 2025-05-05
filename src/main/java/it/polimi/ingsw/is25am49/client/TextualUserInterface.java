package it.polimi.ingsw.is25am49.client;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.polimi.ingsw.is25am49.client.model.*;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class TextualUserInterface implements UserInterface {
    private ClientController controller;
    private GameSession session;
    public int myID;
    public String username;
    HashMap<String, String> graphics;

    public TextualUserInterface() {
        session = new GameSession();
    }

    public void showUpdates(String updateString) {

    }

    public static void main(String args[]) throws IOException {
        //Server settings:
        String serverIP = "localhost";
        int serverPort = 8080;
        //---------------------------------------

        TextualUserInterface ui = new TextualUserInterface();

        String textInput = "";
        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();
        LineReader reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .parser(new DefaultParser())
                .history(new DefaultHistory())
                .build();

        boolean wrongInput = false;
        int numInput = 0;
        String prompt = ">> ";
        String error = "Err : ";
        terminal.writer().println("+------------------------------+");
        terminal.writer().println("| WELCOME TO GALAXY TRUCKER :) |");
        terminal.writer().println("+------------------------------+");

        while (!wrongInput) {
            terminal.writer().println("Insert your username:");
            terminal.flush();
            textInput = reader.readLine(prompt).trim();
            wrongInput = textInput.length() <= 20 && !textInput.isEmpty();
            if (!wrongInput) {
                terminal.writer().println(error + "username non valid");
            }
        }
        wrongInput = false;
        ui.username = textInput;
        terminal.writer().println("\nwelcome to the game " + ui.username + "!");

        while (!wrongInput) {
            terminal.writer().println("Which connection do you want to use:");
            terminal.writer().println("\t1) RMI");
            terminal.writer().println("\t2) Socket");
            terminal.flush();
            numInput = Integer.parseInt(reader.readLine(prompt).trim());
            wrongInput = numInput >= 1 && numInput <= 2;
            if (!wrongInput) {
                terminal.writer().println(error + "invalid choice, retry");
            }
        }
        wrongInput = false;
        terminal.flush();

        //stabilisco la connessione todo gestione errori di connessione al server
        ui.setupConnection(numInput, serverIP, serverPort);

        while (!wrongInput) {
            terminal.writer().println("+--------------------------------------------");
            terminal.writer().println("| SERVER - " + serverIP + "\tstatus - connected");
            terminal.writer().println("+--------------------------------------------");
            terminal.writer().println("| Aviable lobby:");
            terminal.writer().println("| 01) TestMatch - users 0/4");
//            terminal.writer().print("| 01) TestMatch - users "+ui.session.getGame().get().numOfPlayers()+"/4"); //todo se game non fosse optional
//            if(ui.session.getGame().get().numOfPlayers()>=4) terminal.writer().println(" full");
//            else terminal.writer().println("");
            terminal.writer().println("+--------------------------------------------");
            terminal.writer().println("which one do you want to join?");
            terminal.flush();
            numInput = Integer.parseInt(reader.readLine(prompt).trim());
            wrongInput = numInput == 1;
            if (!wrongInput) {
                terminal.writer().println(error + "invalid choice, retry");
            }
        }
        wrongInput = false;
        terminal.flush();

        //ui.start();

    }

    public void start() {
        System.out.println("Welcome to the game!");
        ExecutorService executor = Executors.newFixedThreadPool(5);
        executor.submit(() -> {
            Scanner in = new Scanner(System.in);
            while (true) {
                String inputString;
                inputString = in.nextLine();
                if (inputString.equalsIgnoreCase("quit")) {
                    System.out.println("Server: Sto chiudendo...");
                    //todo non so come si spenga il server
                    System.exit(0);
                }
                controller.handleInput(inputString.toLowerCase());
            }
        });
    }

    public void setupConnection(int connectionType, String serverIP, int serverPort) {
        controller = new ClientController(session);
        switch (connectionType) {
            case 1 -> {
                RemoteServer serverStub = null;

                ClientRMI clientRMI = new ClientRMI(session);
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
                ClientSerializer clientSerializer = new ClientSerializer(session);
                ServerHandler serverHandler = new ServerHandler(serverIP, serverPort);
                serverHandler.setClientSerializer(clientSerializer);
                clientSerializer.setServerHandler(serverHandler);
                controller.setServerMethods(clientSerializer);
                //todo registraclient
            }
        }
    }

}


