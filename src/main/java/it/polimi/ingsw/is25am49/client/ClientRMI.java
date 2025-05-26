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
    private RemoteServer serverRMI;
    private final int thresholdTries;
    private int reconnectionTries;
    private final int secondsForReconnection;
    private final ExecutorService executorService;
    private final int triesForReconnection;
    private boolean isConnected;

    public ClientRMI(ClientController clientController, RemoteServer serverRMI) throws RemoteException {
        super();
        this.clientController = clientController;
        this.serverRMI = serverRMI;
        reconnectionTries = 0;
        thresholdTries = 20;
        secondsForReconnection = 5;
        executorService = Executors.newScheduledThreadPool(10);
        triesForReconnection = 10;
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

    public void sendHeartBeat() {
        if (isConnected) {
            try {
                serverRMI.command_FromClientToServer(new Command("heartbeat", clientController.getplayerId()));
                reconnectionTries = 0;
            } catch (RemoteException e) {
                isConnected = false;
            }
        }
    }

    private void startHeartBeat() {
        sendHeartBeat();
        try {
            Thread.sleep(secondsForReconnection);
        } catch (InterruptedException e) {
            System.out.println("Error while putting the message in the queue, thread has been interrupted");
        }
    }
}

