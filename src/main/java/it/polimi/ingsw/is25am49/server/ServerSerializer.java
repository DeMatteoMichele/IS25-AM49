package it.polimi.ingsw.is25am49.server;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.is25am49.shared.Command;

import java.rmi.RemoteException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSerializer implements RemoteClient {
    private final ServerController serverController;
    private ClientHandler clientHandler;
    private final BlockingQueue<Command> filesFromSocketQueue;
    private final BlockingQueue<Command> historyQueue;
    private final ExecutorService executorService;
    private int idCounter;


    public ServerSerializer(ServerController serverController) {
        this.serverController = serverController;
        this.idCounter = -1;
        filesFromSocketQueue = new ArrayBlockingQueue<Command>(4000, true);
        historyQueue = new ArrayBlockingQueue<Command>(50000, true);
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
                deSerializeStringFromClient(command);
            } catch (InterruptedException e) {
                System.err.println("Thread that process the FilesFromSocketQueue has been interrrupted: " + e.getMessage());
            }
        }
    }

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    //se cambiano i parametri dei metodi bisogna modificare questo codice
    public Command deSerializeStringFromClient(Command command) {
        command.setServerController(serverController);
        if (command.getTypeOfMessage().equals("MSG")) {
            command.callMethod();
        } else if (command.getTypeOfMessage().equals("HSK")) {
            if (command.getPlayerId() == -1) {
                idCounter++;
                command.addParameter(idCounter);
                command.setPlayerId(idCounter);
            } else {
                command.addParameter(command.getPlayerId());
                command.setPlayerId(command.getPlayerId());
            }
        }
        return command;
    }

    public Command serializeObjectFromServer(Command command, boolean toSpecificClient, int clientId) {
        if (!command.getTypeOfMessage().equals("HSK") && !command.getMethodName().startsWith("error")) {
            clientHandler.putFileInTheHistoryQueue(command);
        }
        if (!toSpecificClient) {
            clientHandler.putFileInTheBroadcastQueue(command);
            System.out.println("putting file in the broadcast queue");
        } else {
            clientHandler.putFileInTheOutGoingFileQueue(command, clientId);
        }
        return command;
    }

    public void putFileFromClientSocketInQueue(Command command) {
        try {
            filesFromSocketQueue.put(command);
        } catch (InterruptedException e) {
            System.err.println("Error putting file from socket in queue in serializer cause thread was interrupted: " + e.getMessage());
        }
    }

    public int deserializeHandshake(Command command) {
        // ritorna id del client e mette il pacchetto nella coda outgoing unicast
        Command commandTemp = deSerializeStringFromClient(command);
        Command commandToSend = new Command("registerClientCallback", commandTemp.getPlayerId());
        serializeObjectFromServer(commandToSend, true, commandTemp.getPlayerId());
        return commandTemp.getPlayerId();
    }


    @Override
    public void command_FromServerToClient(Command command) throws RemoteException {
        serializeObjectFromServer(command, false, 99);
    }

    @Override
    public void command_FromServerToSpecificClient(Command command, int clientId) throws RemoteException {
        serializeObjectFromServer(command, true, clientId);
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
        clientHandler.gameStarted();
    }

}