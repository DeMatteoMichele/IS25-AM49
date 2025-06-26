package it.polimi.ingsw.is25am49.client;


import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.shared.Command;
import it.polimi.ingsw.is25am49.shared.RemoteServer;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientSerializer implements RemoteServer {
    private ClientController clientController;
    private ServerHandler serverHandler;
    private BlockingQueue<Command> filesFromSocketQueue;
    private final ExecutorService executors;
    private GameSession gameSession;

    /**
     * Initializes a new ClientSerializer with the specified ClientController.
     * @param clientController the client controller used to manage client operations
     */
    public ClientSerializer(ClientController clientController) {
        this.filesFromSocketQueue = new ArrayBlockingQueue<>(1000, true);
        executors = Executors.newFixedThreadPool(20);
        executors.submit(this::processFilesFromSocketQueue);
        this.clientController = clientController;
    }

    /**
     * Continuously processes commands from the filesFromSocketQueue.
     * Each taken command is deserialized and executed.
     * Handles interruptions during queue operations.
     */
    private void processFilesFromSocketQueue() {
        while (true) {
            try {
                Command command = filesFromSocketQueue.take();
                deSerializeStringFromServer(command);
            } catch (InterruptedException e) {
                System.out.println("Error while taking the message from the queue, thread has been interrupted");
            }
        }
    }

    /**
     * Sets the current game session.
     *
     * @param gameSession the GameSession object to be set
     */
    public void setGamesession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    //IL SERIALIZER SE DEVE PASSARE UN FILE AL SERVERHANDLER GLIELO DEVE METTERE NELLA SUA OUTGOINGQUEUE TRAMITE IL METODO APPOSIT DEL SERVERHANDLER.

    /**
     * Puts a provided Command object into the filesFromSocketQueue for processing.
     *
     * @param command the Command object to be added to the queue.
     *                Cannot be null; expected to contain data from a socket.
     */
    public void putFileFromSocketInQueue(Command command) {
        try {
            filesFromSocketQueue.put(command);
        } catch (InterruptedException e) {
            System.out.println("Error while putting the file in the filesFromSocketQueue, thread has been interrupted");
        }
    }

    /**
     * Processes and executes a Command received from the server by associating it with
     * the client controller and invoking its corresponding methods.
     *
     * @param command The Command object received from the server which is to be deserialized and executed.
     * @return The processed Command object after execution has completed.
     */
    public Command deSerializeStringFromServer(Command command) {
        command.setClientController(clientController);
        command.callMethod();
        return command;
    }

    /**
     * Sends a command object to the server for processing by adding it to the server handler's
     * outgoing file queue.
     *
     * @param command the command object to be serialized and sent to the server
     * @return always returns null
     */
    public File serializeObjectFromClient(Command command) {
        serverHandler.putInTheOutGoingFileQueue(command);
        return null;
    }


    /**
     * Sets the server handler instance for managing server-related operations.
     *
     * @param serverHandler the ServerHandler instance to be assigned
     */
    public void setServerHandler(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    /**
     * Processes a command received from the client and sends it to the server.
     *
     * @param command the command object received from the client
     * @throws RemoteException if a communication-related error occurs
     */
    @Override
    public void command_FromClientToServer(Command command) throws RemoteException {
        serializeObjectFromClient(command);
    }

    /**
     * Initiates the client socket registration process by asking the controller
     * to register the client with the server.
     * Handles RemoteException during the registration process, logging an error message.
     * @throws IOException if an I/O error occurs during registration.
     */
    public void askControllerToStartRegistration() throws IOException {
        try {
            clientController.registerClientSocket();
        } catch (RemoteException e) {
            System.err.println("Error while trying to register the client socket due to remoteexception");
        }
    }

    /**
     * Triggers the client controller to reset the model, initializing a new game session
     * and resetting the client state to its default configuration.
     */
    public void askControllerToResetModel() {
        clientController.clientReset();
    }
}

