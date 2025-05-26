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
    private final File directory;

    public ClientSerializer(GameSession session, ClientController clientController) {
        this.filesFromSocketQueue = new ArrayBlockingQueue<>(1000, true);
        executors = Executors.newFixedThreadPool(20);
        executors.submit(this::processFilesFromSocketQueue);
        gameSession = session;
        this.clientController = clientController;
        directory = new File("$FilesFromClientSerializer");
        if (!directory.exists()) {
            System.out.println("Directory doesn't exist, creating it");
            directory.mkdirs();
        }
    }

    public ClientSerializer(ClientController clientController) {
        this.filesFromSocketQueue = new ArrayBlockingQueue<>(1000, true);
        executors = Executors.newFixedThreadPool(20);
        executors.submit(this::processFilesFromSocketQueue);
        this.clientController = clientController;
        directory = new File("$FilesFromClientSerializer");
        if (!directory.exists()) {
            System.out.println("Directory doesn't exist, creating it");
            directory.mkdirs();
        }
    }

    private void processFilesFromSocketQueue() {
        while (true) {
            try {
                Command command = filesFromSocketQueue.take();
                System.out.println("Processing file: ");
                deSerializeStringFromServer(command);
            } catch (InterruptedException e) {
                System.out.println("Error while taking the message from the queue, thread has been interrupted");
            }
        }
    }

    public void setGamesession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    //IL SERIALIZER SE DEVE PASSARE UN FILE AL SERVERHANDLER GLIELO DEVE METTERE NELLA SUA OUTGOINGQUEUE TRAMITE IL METODO APPOSIT DEL SERVERHANDLER.

    /**
     * This method is used to put files in the queue.
     * It is called by ServerHandler after receiving a message from server
     */
    public void putFileFromSocketInQueue(Command command) {
        try {
            filesFromSocketQueue.put(command);
        } catch (InterruptedException e) {
            System.out.println("Error while putting the file in the filesFromSocketQueue, thread has been interrupted");
        }
    }

    public Command deSerializeStringFromServer(Command command) {
        command.setClientController(clientController);
        command.callMethod();
        return command;
    }

    public File serializeObjectFromClient(Command command) {
        serverHandler.putInTheOutGoingFileQueue(command);
        return null;
    }


    public void setServerHandler(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    @Override
    public void command_FromClientToServer(Command command) throws RemoteException {
        serializeObjectFromClient(command);
    }

    public void askControllerToStartRegistration() throws IOException {
        try {
            clientController.registerClientSocket();
        } catch (RemoteException e) {
            System.err.println("Error while trying to register the client socket due to remoteexception");
        }
    }

    public void askControllerToResetModel() {
        clientController.clientReset();
    }
}

