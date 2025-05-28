package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.shared.RemoteClient;
import it.polimi.ingsw.is25am49.shared.Command;
import it.polimi.ingsw.is25am49.shared.RemoteServer;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientRMI extends UnicastRemoteObject implements RemoteClient {
    private final ClientController clientController;
    private RemoteServer serverRMI;
    private final int thresholdTries;
    private final int initialDelay;
    private int reconnectionTries;
    private final int secondsDelayForReconnection;
    private final ExecutorService executorService;
    private final int triesForReconnection;
    private boolean isConnected;
    private boolean hasResetted;
    private volatile boolean timeIsDone;
    private final ScheduledExecutorService scheduler;
    private final UserInterface userInterface;


    public ClientRMI(ClientController clientController, RemoteServer serverRMI, UserInterface userInterface) throws RemoteException {
        super();
        this.clientController = clientController;
        this.serverRMI = serverRMI;
        initialDelay = 10;
        this.userInterface = userInterface;
        reconnectionTries = 0;
        thresholdTries = 20;
        secondsDelayForReconnection = 5;
        executorService = Executors.newFixedThreadPool(10);
        scheduler = Executors.newScheduledThreadPool(10);
        triesForReconnection = 10;
        hasResetted = false;
        isConnected = false;
        executorService.submit(this::startHeartBeat);

    }

    /**THIS METHOD IS CALLED BY THE SERVER
     *REMEMBER: RMI WORKS BY CALLING METHODS ON A REMOTE OBJECT
     * SO: SERVER CAN CALL HIS INTERFACE METHOD(command_FromServerToClient) ONLY IF
     * CLIENTRMI(the remote object) HAS THE METHOD IMPLEMENTED.
     * REMEMBER, WE CAN CALL A METHOD ON AN OBJECT ONLY IF THE OBJECT HAS THAT METHOD. OTHERWISE, WE ARE NOT CALLING ANYTHING.
     * EXAMPLE: TO CALL PLACETILE ON THE SHIPBOARD, THE SHIPBOARD MUST HAVE PLACETILE ON ITS CODE.
     */

    @Override
    public void command_FromServerToClient(Command command) throws RemoteException {
        if(command.getMethodName().equals("registerClientCallback")) {
            isConnected = true;
        }
        command.setClientController(clientController);
        command.callMethod();
    }

    private void sendHeartBeat() {
        if (isConnected) {
            try {
                serverRMI.command_FromClientToServer(new Command("heartbeat", clientController.getplayerId()));
                if (hasResetted) {
                    System.out.println("Reconnected to ServerRMI after " + reconnectionTries + " attempts.");
                    hasResetted = false;
                }
                reconnectionTries = 0;
                isConnected = true;
            } catch (RemoteException e) {
                clientController.clientReset();
                try {
                    userInterface.connectionRMI(true, this);
                } catch (IOException | NotBoundException ex) {
                    if (!hasResetted) {
                        System.out.println("⚠ Error : something went wrong during the connection to the server RMI --> " + e.getMessage());
                        //se voglio riprovare a connettermi da capire bene come gesitre l'interfaccia comune
//                        System.out.println("\nwanna retry? Y");
//                        if (ui.lineReader.readLine(prompt).trim().equalsIgnoreCase("y")) userInterface.setup;
//                        else {
//                            System.exit(0);
//                            printUpdateMessage("closing...");
                    }
                }

                if (!hasResetted) {
                    clientController.clientReset();
                    hasResetted = true;
                }
                reconnectionTries++;
                if (reconnectionTries > thresholdTries) {
                    isConnected = false;
                    System.out.println("Impossible to reconnect to ServerRMI after " + thresholdTries + " attempts. Shutting down client.");
                    clientController.clientReset();
                } else {
                    System.out.println("Failed to send heartbeat to ServerRMI, retrying in " + secondsDelayForReconnection + " seconds. Attempt " + reconnectionTries + "/" + thresholdTries);
                }
            }
        }
    }

    private void startHeartBeat() {
        timer(initialDelay);
        while(isConnected) {
            sendHeartBeat();
            timer(secondsDelayForReconnection);
        }
    }

    private void timer(int secondsTime) {
        timeIsDone = true;
        scheduler.schedule(() -> {
            timeIsDone = false;
        }, secondsTime, TimeUnit.SECONDS);
        while (timeIsDone) {
            Thread.onSpinWait(); //to improve performance while waiting
        }
    }

    public void replaceServerStub(RemoteServer newServerRMI) {
        this.serverRMI = newServerRMI;
    }
}

