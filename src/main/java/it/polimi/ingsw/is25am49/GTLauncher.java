package it.polimi.ingsw.is25am49;

import it.polimi.ingsw.is25am49.client.GUI.GraphicUserInterface;
import it.polimi.ingsw.is25am49.client.TextualUserInterface;
import it.polimi.ingsw.is25am49.server.ClientHandler;

import java.util.Scanner;
import java.util.regex.Pattern;

public class GTLauncher {
    public static void main(String[] args) {
        System.out.println("you are running the Galaxy Trucker Launcher");

        while (true){
            System.out.println("select:");
            System.out.println("1. SERVER");
            System.out.println("2. CLIENT");
            System.out.print(">> ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().trim();

            switch (input){
                case "1"->{
                    System.out.println("Starting server...");
                    ClientHandler.main(args);
                }
                case "2"-> {
                    String serverIP = askForIp();
                    String uiChoice;
                    while (true) {
                        System.out.println("Choose UI:");
                        System.out.println("1. TUI");
                        System.out.println("2. GUI");
                        System.out.print(">> ");
                        uiChoice = scanner.nextLine().trim();
                        switch (uiChoice) {
                            case "1" -> {
                                System.out.println("Starting CLI client...");
                                TextualUserInterface.main(new String[]{serverIP});
                            }
                            case "2" -> {
                                System.out.println("Starting GUI client...");
                                GraphicUserInterface.main(new String[]{serverIP});
                            }
                            default -> {
                                System.err.println("Invalid input");
                                continue;
                            }
                        }
                        break;
                    }
                }
                default->{
                    System.err.println("Invalid input");
                    continue;
                }
            }
            break;
        }


    }

    private static String askForIp(){
        Scanner scanner = new Scanner(System.in);
        String ipPattern =
                "^localhost$|^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        Pattern pattern = Pattern.compile(ipPattern);
        String serverIP;
        do {
            System.out.print("Inserisci l'IP del server (lettera qualsiasi per localhost): ");
            serverIP = scanner.nextLine().trim();
            if (serverIP.matches("[a-zA-Z]")) {
                serverIP = "localhost";
            }
            if (!pattern.matcher(serverIP).matches()) {
                System.out.println("IP non valido. Riprova.");
            }
        } while (!pattern.matcher(serverIP).matches());
        System.out.println("IP valido inserito: " + serverIP);
        return serverIP;
    }
}


