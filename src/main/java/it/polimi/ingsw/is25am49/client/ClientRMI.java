package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.shared.RemoteClient;
import it.polimi.ingsw.is25am49.shared.Command;
import it.polimi.ingsw.is25am49.shared.RemoteServer;

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
    private final RemoteServer serverRMI;
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


    public ClientRMI(ClientController clientController, RemoteServer serverRMI) throws RemoteException {
        super();
        this.clientController = clientController;
        this.serverRMI = serverRMI;
        this.initialDelay = 10;
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
                if(hasResetted) {
                    System.out.println("Reconnected to ServerRMI after " + reconnectionTries + " attempts.");
                    hasResetted = false;
                }
                reconnectionTries = 0;
                isConnected = true;
            } catch (RemoteException e) {
                if(!hasResetted) {
                    clientController.clientReset();
                    hasResetted = true;
                }
                reconnectionTries++;
                if(reconnectionTries > thresholdTries) {
                    isConnected = false;
                    System.out.println("Impossible to reconnect to ServerRMI after " + thresholdTries + " attempts. Shutting down client.");
                    clientController.clientReset();
                }
                else {
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
}

