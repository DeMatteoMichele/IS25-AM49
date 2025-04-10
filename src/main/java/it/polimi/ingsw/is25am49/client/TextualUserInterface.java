package it.polimi.ingsw.is25am49.client;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TextualUserInterface implements View {
    private ClientController controller;

    public TextualUserInterface() {

    }
    public void showUpdates(String updateString){

    }
    public void start() {
        System.out.println("Welcome to the game!");
        ExecutorService executor = Executors.newFixedThreadPool(5);
        executor.submit(()->{
            Scanner in = new Scanner(System.in);
            while(true){
                String inputString;
                inputString = in.nextLine();
                if(inputString.equals("Quit"))
                    break;
                controller.handleInput(inputString);
            }
        });
    }
    public static void main(String args[]){
        ClientSerializer clientSerializer = new ClientSerializer();
        ServerHandler serverHandler = new ServerHandler(clientSerializer, "localhost", 8080);
        ClientController controller = new ClientController();
        TextualUserInterface tui = new TextualUserInterface();
        tui.start();
    }
}
