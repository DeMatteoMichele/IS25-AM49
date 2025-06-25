package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.shared.Command;
import it.polimi.ingsw.is25am49.shared.RemoteClient;

import java.rmi.RemoteException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The ServerSerializer class handles serialization and deserialization of commands
 * between the server and clients. It manages queues for processing commands and
 * facilitates communication with the ClientHandler and NetworkController.
 */
public class ServerSerializer implements RemoteClient {
    private final ServerController serverController;
    private NetworkController networkController;
    private ClientHandler clientHandler;
    private final BlockingQueue<Command> filesFromSocketQueue;
    private final ExecutorService executorService;
    private int tempId;
    private boolean gameStarted;


    /**
     * Constructs a ServerSerializer instance and initializes its components.
     * Starts a thread for processing commands from the socket queue.
     *
     * @param serverController
     */
    public ServerSerializer(ServerController serverController) {
        this.serverController = serverController;
        this.tempId = -1;
        this.gameStarted = false;
        filesFromSocketQueue = new ArrayBlockingQueue<Command>(4000, true);
        executorService = Executors.newFixedThreadPool(10);
        executorService.submit(this::processFilesFromSocketQueue);
    }

    /**
     * This method is used to process the files from the socket queue.
     * It takes the file from the queue and deserializes it.
     * Method take() is blocking, so it will wait until a file is available in the queue.
     */
    private void processFilesFromSocketQueue() {
        while (true) {
            try {
                Command command = filesFromSocketQueue.take();
                deSerializeCommandFromClient(command);
            } catch (InterruptedException e) {
                System.err.println("Thread that process the FilesFromSocketQueue has been interrrupted: " + e.getMessage());
            }
        }
    }
    /**
     * Sets the NetworkController instance for managing network-related operations.
     *
     * @param networkController The NetworkController instance.
     */
    public void setNetworkController(NetworkController networkController) {
        this.networkController = networkController;
    }

    /**
     * Sets the ClientHandler instance for managing client-related operations.
     *
     * @param clientHandler The ClientHandler instance.
     */
    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * Deserializes a command received from a client and updates its parameters.
     * Handles handshake commands.
     *
     * @param command The command to deserialize.
     * @return The updated command.
     */
    public Command deSerializeCommandFromClient(Command command) {
        command.setServerController(serverController);
        if (command.getTypeOfMessage().equals("MSG")) {
            command.callMethod();
        } else if (command.getTypeOfMessage().equals("HSK")) {
            if (command.getPlayerId() == -1) {
                synchronized (networkController) {
                    networkController.updateIdCounter();
                    tempId = networkController.getIdCounter();
                }
                command.addParameter(tempId);
                command.setPlayerId(tempId);
            } else {
                if (networkController.getPlayersRMI().contains(command.getPlayerId())) {
                    networkController.getPlayersRMI().remove(command.getPlayerId());
                    networkController.addPlayerToLobbySocket(command.getPlayerId());
                    sayToClientHandlerWhoHasJoined(command.getPlayerId());
                }
                command.addParameter(command.getPlayerId());
                command.setPlayerId(command.getPlayerId());
            }
        }
        return command;
    }

    /**
     * Serializes a command from the server and sends it to clients.
     * Handles broadcasting and unicast scenarios based on the parameters.
     *
     * @param command The command to serialize.
     * @param toSpecificClient Whether the command is for a specific client.
     * @param clientId The ID of the target client (if applicable).
     * @return The serialized command.
     */
    public Command serializeObjectFromServer(Command command, boolean toSpecificClient, int clientId) {
        if (!toSpecificClient) {    //broadcast
            if (!gameStarted) {
                if (command.getMethodName().equals("playerJoinCallback") && clientHandler.containsClient(command.getPlayerId())) {
                    networkController.addPlayerToLobbySocket(command.getPlayerId());
                    sayToClientHandlerWhoHasJoined(command.getPlayerId());
                }
            }
            clientHandler.putFileInTheBroadcastQueue(command);
        } else {    //to a single client
            clientHandler.putFileInTheOutGoingFileQueue(command, clientId);
        }
        if (!command.getTypeOfMessage().equals("HSK") && !command.getMethodName().startsWith("error")) {
            Command commandHistory = new Command(command.getMethodName(), command.getPlayerId());
            commandHistory.copyParameters(command.getParameters());
            commandHistory.setIsHistoryTrue();
            clientHandler.putFileInTheHistoryQueue(commandHistory);
        }
        return command;
    }
    /**
     * Adds a command received from a client socket to the processing queue.
     *
     * @param command The command to add to the queue.
     */
    public void putFileFromClientSocketInQueue(Command command) {
        try {
            filesFromSocketQueue.put(command);
        } catch (InterruptedException e) {
            System.err.println("Error putting file from socket in queue in serializer cause thread was interrupted: " + e.getMessage());
        }
    }

    /**
     * Deserializes a handshake command and sends a response to the client.
     * Updates the client ID and adds the command to the outgoing queue.
     *
     * @param command The handshake command to deserialize.
     * @return The client ID extracted from the command.
     */
    public int deserializeHandshake(Command command) {
        // ritorna id del client e mette il pacchetto nella coda outgoing unicast
            Command commandTemp = deSerializeCommandFromClient(command);
            Command commandToSend = new Command("registerClientCallback", commandTemp.getPlayerId());
            serializeObjectFromServer(commandToSend, true, commandTemp.getPlayerId());
            return commandTemp.getPlayerId();
    }


    @Override
    public void command_FromServerToClient(Command command) throws RemoteException {
        serializeObjectFromServer(command, false, 99);
    }

    /**
     * This method is used to signal to the clienthandler that a player has joined.
     *
     * @param playerId the id of the player that has joined
     */
    public void sayToClientHandlerWhoHasJoined(int playerId) {
        clientHandler.playerHasJoined(playerId);
    }

    /**
     * This method is used to signal to the clienthandler that the game has started.
     */
    public void signalThatGameStarted() {
        gameStarted = true;
        clientHandler.gameStarted();
    }

}