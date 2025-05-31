package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.shared.RemoteClient;
import it.polimi.ingsw.is25am49.shared.RemoteServer;
import it.polimi.ingsw.is25am49.shared.Command;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;

public class ServerRMI extends UnicastRemoteObject implements RemoteClient, RemoteServer {
    private boolean gameStarted;
    private final ServerController serverController;
    private final ConcurrentHashMap<Integer, RemoteClient> clients;
    private final ConcurrentHashMap<Integer, RemoteClient> matchPlayers;
    private final ConcurrentHashMap<Integer, Long> lastHeartBeats;
    private final BlockingQueue<Command> historyQueue;
    private final BlockingQueue<Command> commandsFromClientQueue;
    private final ExecutorService executorService;
    private volatile boolean timeIsDone;
    private final ScheduledExecutorService scheduler;

    public ServerRMI(ServerController serverController) throws RemoteException {
        super();
        this.serverController = serverController;
        this.clients = new ConcurrentHashMap<>();
        this.matchPlayers = new ConcurrentHashMap<>();
        this.lastHeartBeats = new ConcurrentHashMap<>();
        historyQueue = new ArrayBlockingQueue<Command>(50000, true);
        commandsFromClientQueue = new ArrayBlockingQueue<Command>(50000, true);
        gameStarted = false;
        executorService = Executors.newFixedThreadPool(100);
        scheduler = Executors.newScheduledThreadPool(100);
        scheduler.scheduleAtFixedRate(this::startHeartBeatMonitoring, 10, 5, TimeUnit.SECONDS);
        executorService.submit(this::processFilesFromClientQueue);
    }

    @Override
    public void command_FromServerToClient(Command command) throws RemoteException {
        if (!gameStarted || command.getMethodName().equals("playerJoinCallback")) {
            clients.forEach((id, client) -> {
                try {
                    client.command_FromServerToClient(command);
                } catch (RemoteException e) {
                    System.out.println("Error sending command to client " + id + ": " + e.getMessage());
                }
            });
        }
        else {
            matchPlayers.forEach((id, client) -> {
                try {
                    client.command_FromServerToClient(command);
                } catch (RemoteException e) {
                    System.out.println("Error sending command to client " + id + ": " + e.getMessage());
                }
            });
        }
        if (!command.getTypeOfMessage().equals("HSK") && !command.getMethodName().startsWith("error")) {
            historyQueue.add(command);
        }
    }

    private void command_FromServerToSpecificClient(Command command, int clientId) throws RemoteException {
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
        if(command.getTypeOfMessage().equals("HTB")) {
            lastHeartBeats.put(command.getPlayerId(), System.currentTimeMillis());
        }
        else {
            putFileFromClientInQueue(command);
        }
    }
    public void sayToServerRMIWhoHasJoined(int playerId){
        matchPlayers.put(playerId, clients.get(playerId));
    }
    private void processFilesFromClientQueue(){
        while (true) {
            try {
                Command command = commandsFromClientQueue.take();
                if(command.getTypeOfMessage().equals("HSK") && (!gameStarted || serverController.isInPlayers(command.getPlayerId()))) {
                    synchronized (serverController) {
                        command.setServerController(serverController);
                        if(command.getPlayerId() == -1){
                            serverController.updateIdCounter();
                            int myId = serverController.getIdCounter();
                            RemoteClient put = clients.put(myId, (RemoteClient) command.getParameters().getFirst());
                            Command response = new Command("registerClientCallback", myId);
                            response.addParameter(myId);
                            command_FromServerToSpecificClient(response, myId);
                            giveMeCommandsHistory(myId);
                        }
                        else {
                            int myId = command.getPlayerId();
                            if(matchPlayers.containsKey(myId))
                                matchPlayers.put(myId, (RemoteClient) command.getParameters().getFirst());
                            clients.put(myId, (RemoteClient) command.getParameters().getFirst());
                            Command response = new Command("registerClientCallback", myId);
                            response.addParameter(myId);
                            command_FromServerToSpecificClient(response, myId);
                            giveMeCommandsHistory(myId);
                        }


                    }
                }
                else if(command.getTypeOfMessage().equals("MSG")) {
                    if(!gameStarted) {
                        if (command.getMethodName().equals("playerJoin"))
                            serverController.addPlayerToLobbyRMI(command.getPlayerId());
                    }
                    command.setServerController(serverController);
                    command.callMethod();
                }
            } catch (InterruptedException e) {
                System.err.println("Thread that process the FilesFromSocketQueue has been interrrupted: " + e.getMessage());
            } catch (RemoteException e) {
                System.out.println("Error processing command from client: " + e.getMessage());
            }
        }
    }
    private void giveMeCommandsHistory(int clientId) {
        for(Command commandBefore: historyQueue) {
            try {
                command_FromServerToSpecificClient(commandBefore, clientId);
            } catch (RemoteException e) {
                System.err.println("Error sending command history to client " + clientId + ": " + e.getMessage());
            }
        }
    }
    private void putFileFromClientInQueue(Command command) {
        try {
            commandsFromClientQueue.put(command);
        } catch (InterruptedException e) {
            System.err.println("Error putting file from Client in queue in ServerRMI cause thread was interrupted: " + e.getMessage());
        }
    }
    private void startHeartBeatMonitoring() {
        for (Map.Entry<Integer, Long> entry : lastHeartBeats.entrySet()) {
            if (clients.containsKey(entry.getKey())) {
                if (System.currentTimeMillis() - entry.getValue() > 20000) {
                    System.out.println("Client " + entry.getKey() + " doesn't respond. Shutting down his connection");
                    clients.remove(entry.getKey());
                    matchPlayers.remove(entry.getKey());
                }
            }
        }
        timer(5);
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
