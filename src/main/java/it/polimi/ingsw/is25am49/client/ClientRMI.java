package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.shared.RemoteClient;
import it.polimi.ingsw.is25am49.shared.Command;
import it.polimi.ingsw.is25am49.shared.RemoteServer;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.*;

public class ClientRMI extends UnicastRemoteObject implements RemoteClient {
    private final ClientController clientController;
    private RemoteServer serverRMI;
    private final int thresholdTries;
    private final int initialDelay;
    private int reconnectionTries;
    private final int secondsDelayForReconnection;
    private final ExecutorService executorService;
    private boolean isConnected;
    private boolean hasResetted;
//    private volatile boolean timeIsDone;
    private final ScheduledExecutorService scheduler;
    private final UserInterface userInterface;
    private final BlockingQueue<Command> commandsFromServerQueue;


    /**
     * Constructs a ClientRMI instance to handle client-server communication via RMI.
     *
     * @param clientController the controller managing client-side logic
     * @param serverRMI the remote server interface
     * @param userInterface the client-side user interface
     * @param serverIP the IP address of the remote server
     * @throws RemoteException if there is an RMI communication-related issue
     */
    public ClientRMI(ClientController clientController, RemoteServer serverRMI, UserInterface userInterface, String serverIP) throws RemoteException {
        super();
        System.setProperty("java.rmi.server.hostname", serverIP);
        this.clientController = clientController;
        this.serverRMI = serverRMI;
        initialDelay = 5;
        this.userInterface = userInterface;
        reconnectionTries = 0;
        thresholdTries = 50;
        secondsDelayForReconnection = 2;
        executorService = Executors.newFixedThreadPool(10);
        scheduler = Executors.newScheduledThreadPool(10);
        hasResetted = false;
        isConnected = false;
        scheduler.scheduleAtFixedRate(this::sendHeartBeat, initialDelay, secondsDelayForReconnection, TimeUnit.SECONDS);
        commandsFromServerQueue = new ArrayBlockingQueue<>(50000, true);
        executorService.submit(this::processCommandsFromServerQueue);
    }

    /**
     * Handles a command sent from the server to the client and processes it by adding to a queue.
     *
     * @param command the command object sent from the server to be processed by the client.
     * @throws RemoteException if a communication error occurs during the remote method call.
     */

    @Override
    public void command_FromServerToClient(Command command) throws RemoteException {
        putFileFromServerInQueue(command);
    }

    /**
     * Adds a command object received from the server into the local command queue.
     * Handles thread interruption during the enqueue process.
     *
     * @param command the command object to be added to the queue
     */
    private void putFileFromServerInQueue(Command command) {
        try {
            commandsFromServerQueue.put(command);
        } catch (InterruptedException e) {
            System.err.println("Error putting file from Client in queue in ServerRMI cause thread was interrupted: " + e.getMessage());
        }
    }
    /**
     * Continuously processes commands from the server queue by executing the corresponding method.
     * Handles interruptions and pauses briefly between processing commands.
     * Updates client connection status based on specific command methods.
     */
    private void processCommandsFromServerQueue()  {
        while (true) {
            try {
                Command command = commandsFromServerQueue.take();
                if(command.getMethodName().equals("registerClientCallback")) {
                    isConnected = true;
                }
                command.setClientController(clientController);
                command.callMethod();
            } catch (InterruptedException e) {
                System.err.println("Thread that process the FilesFromSocketQueue has been interrrupted: " + e.getMessage());
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Sends a heartbeat signal to the server to maintain the connection status.
     * Attempts to reconnect if the connection is lost, with a limited number of retries.
     * Logs the connection attempts and updates the client state accordingly.
     */
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

    /**
     * Initiates the mechanism for periodically sending heartbeats
     * to the server to verify and maintain the connection status.
     * This method ensures the client remains connected or attempts to recover.
     */
    private void startHeartBeat() {
//        while(isConnected) {
            sendHeartBeat();
        //}
    }

    //remains here for the glory, but not used
//    private void timer(int secondsTime) {
//        timeIsDone = true;
//        scheduler.schedule(() -> {
//            timeIsDone = false;
//        }, secondsTime, TimeUnit.SECONDS);
//        while (timeIsDone) {
//            Thread.onSpinWait(); //to improve performance while waiting
//        }
//    }

    /**
     * Replaces the current RemoteServer stub with a new one.
     *
     * @param newServerRMI the new RemoteServer object to be assigned
     */
    public void replaceServerStub(RemoteServer newServerRMI) {
        this.serverRMI = newServerRMI;
    }
}

