package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.shared.RemoteClient;
import it.polimi.ingsw.is25am49.shared.RemoteServer;
import it.polimi.ingsw.is25am49.shared.Command;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;

/**
 * The ServerRMI class implements the RemoteClient and RemoteServer interfaces.
 * It handles communication between the server and clients using RMI (Remote Method Invocation).
 * This class manages client connections, processes commands, and monitors client heartbeats.
 */
public class ServerRMI extends UnicastRemoteObject implements RemoteClient, RemoteServer {
    private boolean gameStarted;
    private final ServerController serverController;
    private NetworkController networkController;
    private final ConcurrentHashMap<Integer, RemoteClient> clients;
    private final ConcurrentHashMap<Integer, RemoteClient> matchPlayers;
    private final ConcurrentHashMap<Integer, Long> lastHeartBeats;
    private final BlockingQueue<Command> historyQueue;
    private final BlockingQueue<Command> commandsFromClientQueue;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduler;
    private int tempId;

    /**
     * Constructs a ServerRMI instance and initializes its components.
     * Schedules periodic tasks for heartbeat monitoring and command processing.
     *
     * @param serverController The controller for server operations.
     * @throws RemoteException If an RMI-related error occurs.
     */
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
        scheduler.scheduleAtFixedRate(this::startHeartBeatMonitoring, 10, 1, TimeUnit.SECONDS);
        executorService.submit(this::processFilesFromClientQueue);
    }

    /**
     * Sets the network controller for managing network-related operations.
     *
     * @param networkController The network controller instance.
     */
    public void setNetworkController(NetworkController networkController) {
        this.networkController = networkController;
    }

    /**
     * Sends a command from the server to all clients or match players.
     * Adds the command to the history queue.
     *
     * @param command The command to send.
     * @throws RemoteException If an RMI-related error occurs.
     */
    @Override
    public void command_FromServerToClient(Command command) throws RemoteException {
        if (!gameStarted) { //I send callbacks to all clients connected

            if (command.getMethodName().equals("playerJoinCallback") && clients.containsKey(command.getPlayerId())) {// i must add the player to the playersRMI
                networkController.addPlayerToLobbyRMI(command.getPlayerId());
                sayToServerRMIWhoHasJoined(command.getPlayerId());
            }

            clients.forEach((id, client) -> {
                try {
                    client.command_FromServerToClient(command);
                } catch (RemoteException e) {
                    System.out.println("Error sending command to client " + id + ": " + e.getMessage());
                }
            });
        } else {  //I send callbacks only to players in the match
            matchPlayers.forEach((id, client) -> {
                try {
                    client.command_FromServerToClient(command);
                } catch (RemoteException e) {
                    System.out.println("Error sending command to client " + id + ": " + e.getMessage());
                }
            });
        }
        if (!command.getTypeOfMessage().equals("HSK") && !command.getMethodName().startsWith("error")) {
            Command commandHistory = new Command(command.getMethodName(), command.getPlayerId());
            commandHistory.copyParameters(command.getParameters());
            commandHistory.setIsHistoryTrue();
            historyQueue.add(commandHistory);
        }
    }

    /**
     * Sends a command from the server to a specific client.
     *
     * @param command The command to send.
     * @param clientId The ID of the target client.
     * @throws RemoteException If an RMI-related error occurs.
     */
    private void command_FromServerToSpecificClient(Command command, int clientId) throws RemoteException {
        RemoteClient specificClient = clients.get(clientId);
        if (specificClient != null) {
            command.setServerController(serverController);
            specificClient.command_FromServerToClient(command);
        } else {
            System.out.println("Client with ID " + clientId + " not found.");
        }
    }
    /**
     * Signals that the game has started, updating the game state.
     */
    public void signalThatGameStarted() {
        gameStarted = true;
    }

    /**
     * Handles a command sent from a client to the server.
     * Updates the client's heartbeat timestamp and adds the command to the queue.
     *
     * @param command The command received from the client.
     * @throws RemoteException If an RMI-related error occurs.
     */
    @Override
    public void command_FromClientToServer(Command command) throws RemoteException {
        lastHeartBeats.put(command.getPlayerId(), System.currentTimeMillis());
        putFileFromClientInQueue(command);

    }
    /**
     * Adds a player to the match players map based on their ID.
     *
     * @param playerId The ID of the player to add.
     */
    public void sayToServerRMIWhoHasJoined(int playerId) {
        matchPlayers.put(playerId, clients.get(playerId));
    }

    /**
     * Processes commands from the client queue, executing their associated methods.
     */
    private void processFilesFromClientQueue() {
        while (true) {
            try {
                Command command = commandsFromClientQueue.take();
                command.setServerController(serverController);
                if (command.getTypeOfMessage().equals("MSG")) {
                    command.callMethod();
                } else if (command.getTypeOfMessage().equals("HSK")) {
                        if (command.getPlayerId() == -1) {
                            synchronized (networkController) {
                                networkController.updateIdCounter();
                                tempId = networkController.getIdCounter();
                            }
                            clients.put(tempId, (RemoteClient) command.getParameters().getFirst());
                            Command response = new Command("registerClientCallback", tempId);
                            response.addParameter(tempId);
                            command_FromServerToSpecificClient(response, tempId);
                            if (!gameStarted || networkController.isInPlayersRMI(command.getPlayerId()))
                                giveMeCommandsHistory(tempId);
                            serverController.playerConnected(tempId);
                        } else {
                            int myId = command.getPlayerId();
                            clients.put(myId, (RemoteClient) command.getParameters().getFirst());
                            if (networkController.getPlayersSocket().contains(myId)) {
                                networkController.getPlayersSocket().remove(myId);
                                networkController.addPlayerToLobbyRMI(myId);
                            }
                            sayToServerRMIWhoHasJoined(myId);
                            Command response = new Command("registerClientCallback", myId);
                            response.addParameter(myId);
                            command_FromServerToSpecificClient(response, myId);
                            if (!gameStarted || networkController.isInPlayersRMI(command.getPlayerId()))
                                giveMeCommandsHistory(myId);
                            serverController.playerConnected(myId);
                        }
                }
            } catch (InterruptedException e) {
                System.err.println("Thread that process the FilesFromSocketQueue has been interrrupted: " + e.getMessage());
            } catch (RemoteException e) {
                System.out.println("Error processing command from client: " + e.getMessage());
            }
        }
    }

    /**
     * Sends the command history to a specific client.
     *
     * @param clientId The ID of the client to receive the history.
     */
    private void giveMeCommandsHistory(int clientId) {
        for (Command commandBefore : historyQueue) {
            try {
                command_FromServerToSpecificClient(commandBefore, clientId);
            } catch (RemoteException e) {
                System.err.println("Error sending command history to client " + clientId + ": " + e.getMessage());
            }
        }
    }

    /**
     * Adds a command from a client to the processing queue.
     *
     * @param command The command to add.
     */
    private void putFileFromClientInQueue(Command command) {
        try {
            commandsFromClientQueue.put(command);
        } catch (InterruptedException e) {
            System.err.println("Error putting file from Client in queue in ServerRMI cause thread was interrupted: " + e.getMessage());
        }
    }

    /**
     * Monitors client heartbeats and removes inactive clients.
     * Disconnects clients that have not sent a heartbeat within the timeout period.
     */
    private void startHeartBeatMonitoring() {
        for (Map.Entry<Integer, Long> entry : lastHeartBeats.entrySet()) {
            if (clients.containsKey(entry.getKey())) {
                if (System.currentTimeMillis() - entry.getValue() > 4500) {
                    System.out.println("RMI: Client " + entry.getKey() + " doesn't respond. Shutting down his connection");
                    clients.remove(entry.getKey());
                    matchPlayers.remove(entry.getKey());
                    serverController.playerDisconnected(entry.getKey());
                }
            }
        }
    }
}
