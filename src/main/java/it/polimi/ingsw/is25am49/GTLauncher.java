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
        String serverIP;
        boolean valid = false;
        do {
            System.out.print("Inserisci l 'IP del server (lettera qualsiasi per localhost): ");
            serverIP = scanner.nextLine().trim();
            if (serverIP.length() == 1) {
                serverIP = "localhost";
                valid = true;
            } else if (serverIP.equals("localhost")) {
                valid = true;
            } else {
                valid = true;
                int dotCount = 0;
                int lastDot = -1;
                for (int i = 0; i < serverIP.length(); i++){
                    char c = serverIP.charAt(i);
                    if(c == '.'){
                        dotCount++;
                        String segment = serverIP.substring(lastDot + 1, i);
                        try{
                            int value = Integer.parseInt(segment);
                            if(value < 0 || value > 255){
                                valid = false;
                                break;
                            }
                        } catch(NumberFormatException e){
                            valid = false;
                            break;
                        }
                        lastDot = i;
                    } else if(c < '0' || c > '9'){
                        valid = false;
                        break;
                    }
                }
                if(dotCount != 3){
                    valid = false;
                } else {
                    try{
                        int value = Integer.parseInt(serverIP.substring(lastDot + 1));
                        if(value < 0 || value > 255){
                            valid = false;
                        }
                    } catch(NumberFormatException e){
                        valid = false;
                    }
                }
                if(!valid){
                    System.out.println("IP non valido. Riprova.");
                }
            }
        } while(!valid);
        System.out.println("IP valido inserito: " + serverIP);
        return serverIP;
    }
}


