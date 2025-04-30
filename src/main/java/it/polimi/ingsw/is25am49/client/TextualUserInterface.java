package it.polimi.ingsw.is25am49.client;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TextualUserInterface implements UserInterface {
    private ClientController controller;

    public TextualUserInterface() {

    }

    public void showUpdates(String updateString) {

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
        /*TODO INTEGRARE SCELTA DEL TIPO DI CONNESSIONE NELLA TUI
           clientModel = new ClientModel();
            serverMethods = switch(connectionType){
            case 0 -> new ClientSerializer();
            case 1 -> new ClientRMI().getServerStub();
            default -> throw new IllegalStateException("METODO DI CONNESIIONE NON VALIDO --> Unexpected value: " + connectionType);
         */
    }

    public static void main(String args[]) {
        ClientSerializer clientSerializer = new ClientSerializer();
        ServerHandler serverHandler = new ServerHandler("localhost", 8080);
        serverHandler.setClientSerializer(clientSerializer);
        clientSerializer.setServerHandler(serverHandler);
        /*TODO DA CAMBIARE IL COSTRUTTORE DEL CONTROLLER, BISOGNA FARE IL SET DEI SERVERMETHODS
            ClientController controller = new ClientController();
        */
        TextualUserInterface tui = new TextualUserInterface();
        tui.start();
    }
}

