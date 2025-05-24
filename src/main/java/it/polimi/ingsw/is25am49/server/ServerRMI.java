package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.shared.RemoteClient;
import it.polimi.ingsw.is25am49.shared.RemoteServer;
import it.polimi.ingsw.is25am49.shared.Command;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerRMI extends UnicastRemoteObject implements RemoteClient, RemoteServer {
    private boolean gameStarted;
    private ServerController serverController;
    private HashMap<Integer, RemoteClient> clients;
    private HashMap<Integer, RemoteClient> matchPlayers;
    private final BlockingQueue<Command> historyQueue;
    private final BlockingQueue<Command> commandsFromClientQueue;
    private final ExecutorService executorService;

    public ServerRMI(ServerController serverController) throws RemoteException {
        super();
        this.serverController = serverController;
        this.clients = new HashMap<>();
        this.matchPlayers = new HashMap<>();
        historyQueue = new ArrayBlockingQueue<Command>(50000, true);
        commandsFromClientQueue = new ArrayBlockingQueue<Command>(50000, true);
        gameStarted = false;
        executorService = Executors.newFixedThreadPool(10);

        executorService.submit(this::processFilesFromClientQueue);
    }

    @Override
    public void command_FromServerToClient(Command command) throws RemoteException {
        if (!gameStarted || command.getMethodName().equals("playerJoinCallback")) {
            clients.forEach((id, client) -> {
                try {
                    client.command_FromServerToClient(command);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
        }
        else {
            matchPlayers.forEach((id, client) -> {
                try {
                    client.command_FromServerToClient(command);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
        }
        if (!command.getTypeOfMessage().equals("HSK") && !command.getMethodName().startsWith("error")) {
            historyQueue.add(command);
        }
    }

    public void command_FromServerToSpecificClient(Command command, int clientId) throws RemoteException {
        RemoteClient specificClient = clients.get(clientId);
        if (specificClient != null) {
            command.setServerController(serverController);
            specificClient.command_FromServerToClient(command);
        } else {
            System.out.println("Client with ID " + clientId + " not found.");
        }
    }

    public void signalThatGameStarted() {
        gameStarted = true;
    }

    /**THIS METHOD IS CALLED BY THE CLIENT
     *REMEMBER: RMI WORKS BY CALLING METHODS ON A REMOTE OBJECT
     * SO: CLIENT CAN CALL HIS INTERFACE METHOD(command_FromClientToServer) ONLY IF
     * SERVERRMI(the remote object) HAS THE METHOD IMPLEMENTED.
     * REMEMBER, WE CAN CALL A METHOD ON AN OBJECT ONLY IF THE OBJECT HAS THAT METHOD. OTHERWISE, WE ARE NOT CALLING ANYTHING.
     * EXAMPLE: TO CALL PLACETILE ON THE SHIPBOARD, THE SHIPBOARD MUST HAVE PLACETILE ON ITS CODE.
     */

    @Override
    public void command_FromClientToServer(Command command) throws RemoteException {
        putFileFromClientSocketInQueue(command);
    }
    public void sayToServerRMIWhoHasJoined(int playerId){
        matchPlayers.put(playerId, clients.get(playerId));
    }
    public void processFilesFromClientQueue(){
        while (true) {
            try {
                Command command = commandsFromClientQueue.take();
                if(command.getTypeOfMessage().equals("HSK")) {
                    synchronized (serverController) {
                        command.setServerController(serverController);
                        if(command.getPlayerId() == -1){
                            serverController.updateIdCounter();
                            int myId = serverController.getIdCounter();
                            serverController.addPlayerToLobbyRMI(myId);
                            clients.put(myId, (RemoteClient) command.getParameters().getFirst());
                            Command response = new Command("registerClientCallback", myId);
                            response.addParameter(myId);
                            command_FromServerToSpecificClient(response, myId);
                            giveMeCommandsHistory(myId);
                        }
                        else {
                            int myId = command.getPlayerId();
                            serverController.addPlayerToLobbyRMI(myId);
                            clients.put(myId, (RemoteClient) command.getParameters().getFirst());
                            Command response = new Command("registerClientCallback", myId);
                            response.addParameter(myId);
                            command_FromServerToSpecificClient(response, myId);
                            giveMeCommandsHistory(myId);
                        }


                    }
                }
                else if(command.getTypeOfMessage().equals("MSG")) {
                    command.setServerController(serverController);
                    command.callMethod();
                }
            } catch (InterruptedException e) {
                System.err.println("Thread that process the FilesFromSocketQueue has been interrrupted: " + e.getMessage());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public void giveMeCommandsHistory(int clientId) {
        for(Command commandBefore: historyQueue) {
            try {
                command_FromServerToSpecificClient(commandBefore, clientId);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public void putFileFromClientSocketInQueue(Command command) {
        try {
            commandsFromClientQueue.put(command);
        } catch (InterruptedException e) {
            System.err.println("Error putting file from socket in queue in serializer cause thread was interrupted: " + e.getMessage());
        }
    }
}
