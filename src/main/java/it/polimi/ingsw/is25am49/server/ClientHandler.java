package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.shared.Command;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.concurrent.*;

public class ClientHandler {
    private final ServerSerializer serverSerializer;
    private ServerSocket serverSocket;
    private final ExecutorService executors;
    private final ScheduledExecutorService scheduler;
    private final ConcurrentHashMap<Integer, Socket> clientSockets;
    private final ConcurrentMap<Integer, Boolean> connections;
    private final ConcurrentMap<Integer, Long> lastHeartBeats;
    private final ConcurrentMap<Integer, ObjectOutputStream> outputStreams;
    private final ConcurrentMap<Integer, ObjectInputStream> inputStreams;
    private final ConcurrentMap<Integer, ArrayBlockingQueue<Command>> InGoingStringQueues;
    private final ConcurrentMap<Integer, LinkedBlockingDeque<Command>> outGoingFileQueue;
    private final ConcurrentMap<Integer, LinkedBlockingDeque<Command>> playersOutgoingFileQueue;
    private final ArrayBlockingQueue<Command> BroadcastQueue;
    private final ArrayBlockingQueue<Command> historyQueue;
    private static NetworkController networkController;
    private static GameSession gameSession;

    private final Thread serverThread;
    private boolean gameStarted;

    public ClientHandler(ServerSerializer serverSerializer, int port) {
        this.serverSerializer = serverSerializer;
        scheduler = Executors.newScheduledThreadPool(20);
        executors = Executors.newFixedThreadPool(20);
        clientSockets = new ConcurrentHashMap<>();
        outputStreams = new ConcurrentHashMap<>();
        inputStreams = new ConcurrentHashMap<>();
        connections = new ConcurrentHashMap<>();
        lastHeartBeats = new ConcurrentHashMap<>();
        InGoingStringQueues = new ConcurrentHashMap<>();
        outGoingFileQueue = new ConcurrentHashMap<>();
        playersOutgoingFileQueue = new ConcurrentHashMap<>();
        historyQueue = new ArrayBlockingQueue<>(50000, true);
        gameStarted = false;
        BroadcastQueue = new ArrayBlockingQueue<>(4000, true);
        serverThread = new Thread(() -> {
            try {
                startServer(port);
            } catch (IOException e) {
                System.err.println("Error starting server: " + e.getMessage());
            }
        });
        serverThread.start();
    }

    /**
     * This method is used to start the server and listens continously for incoming connections from clients.
     * It creates a new ServerSocket and waits for clients to connect.
     * It accepts only the first maxClients connections or connections from the clients that connected in the past.
     *
     * @param port The port number on which the server will listen for incoming connections.
     * @throws IOException If an I/O error occurs when creating the ServerSocket or accepting a connection.
     */
    private void startServer(int port) throws IOException {
        System.out.println("Server started, waiting for client connection...");
        serverSocket = new ServerSocket(port);
        scheduler.scheduleAtFixedRate(this::centralHeartBeatMonitor, 0, 5, TimeUnit.SECONDS);
        startBroadCastService();
        while (true) {
            Socket socket = serverSocket.accept();
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            int clientId = handleFirstMessage(inputStream);
            if (!gameStarted || networkController.isInPlayersSocket(clientId)) {
                executors.submit(() -> {
                    askForPreviousCommands(clientId);
                    gameSession.getServerController().playerConnected(clientId);
                });
            }
            if (!connections.containsKey(clientId)) {
                connect(socket, inputStream, outputStream, clientId);
                submitToScheduler(clientId);
            } else connect(socket, inputStream, outputStream, clientId);
            System.out.println("Client connected with id: " + clientId);
            System.out.println("size: " + clientSockets.size());
        }
    }

    private int handleFirstMessage(ObjectInputStream inputStream) {
        try {
            Command command = (Command) inputStream.readObject();
            return serverSerializer.deserializeHandshake(command);
        } catch (IOException e) {
            System.err.println("Error while reading the first message from new client ");
            return -100;
        } catch (ClassNotFoundException e) {
            System.err.println("Error while reading the object from the input stream for handshake : classnotfound");
            return -100;
        }
    }

    /**
     * This method creates all network components needed for the connection.
     * It submits to an executor the task of listening to that specific client which just connected.
     *
     * @param socket   The socket of the client.
     * @param clientId The clientId of the client being connected.
     */
    private void connect(Socket socket, ObjectInputStream clientInputStream, ObjectOutputStream clientOutputStream, int clientId) {
        lastHeartBeats.put(clientId, System.currentTimeMillis());
        clientSockets.put(clientId, socket);
        outputStreams.put(clientId, clientOutputStream);
        inputStreams.put(clientId, clientInputStream);
        connections.put(clientId, true);
        InGoingStringQueues.put(clientId, new ArrayBlockingQueue<>(1000, true));
    }

    private void submitToScheduler(int clientId) {
        scheduler.scheduleWithFixedDelay(() -> listenToClient(clientId), 0, 25, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(() -> processStringQueueToServerSerializer(clientId), 512, 25, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(() -> processOutGoingFileQueue(clientId), 512, 25, TimeUnit.MILLISECONDS);
    }

    /**
     * This method is used to start a centralized heart beat monitor that checks if all the clients are still connected.
     * It works through a ScheduledExecutorService.
     * It scans all the lastHeartBeat map and checks every 5 seconds if the last heart beat received from the clients is older than 15 seconds.
     * If it is, it shuts down the connection with that client.
     * Beware that this method is used as a double check for disconnections in case the catches for IOException in other methods fail.
     */
    private void centralHeartBeatMonitor() {
        for (Map.Entry<Integer, Long> entry : lastHeartBeats.entrySet()) {
            if (connections.get(entry.getKey())) {
                if (System.currentTimeMillis() - entry.getValue() > 20000) {
                    System.out.println("SOCKET: Client " + entry.getKey() + " doesn't respond. Shutting down his connection");
                    shutDownClientRelated(entry.getKey());
                }
            }
        }
    }

    /**
     * This method is used to listen to the client and receive messages incoming from the InputStream from the client with id clientId
     * It reads the length of the message, then reads the message itself and writes it to a file JSON.
     * After that, it passes the information to the server serializer.
     *
     * @param clientId The int of the client being handled.
     */
    private void listenToClient(int clientId) {
        ObjectInputStream clientInputStream = inputStreams.get(clientId);
        if (connections.get(clientId)) {
            try {
                Command command = (Command) clientInputStream.readObject();
                lastHeartBeats.put(clientId, System.currentTimeMillis());
                if (!command.getTypeOfMessage().equals("HTB"))
                    InGoingStringQueues.get(clientId).put(command);
            } catch (EOFException e) {
                System.out.println(clientId + " closed his outputstream");
                shutDownClientRelated(clientId);
            } catch (IOException e) {
                System.out.println("Error while listening to client " + clientId + " shutting down his connection");
                shutDownClientRelated(clientId);
            } catch (InterruptedException e) {
                System.out.println("Error while putting the String in the queue for client " + clientId + " cause thread was interrupted");
            } catch (ClassNotFoundException e) {
                System.err.println("Error while reading the object from the input stream for client " + clientId + " classnotfound");
            }
        }
    }

    /**
     * This method is called by serverserializer to put files in the outGoingFileQueue specific to the client with id=clientid.
     * It is executed by an executor for each client.
     * It takes the file from the queue and sends it to the client.
     * The method put() is blocking, so it blocks until there is space in the queue to put the file.
     *
     * @param command  The file that has to be put in the outGoingFileQueue.
     * @param clientId The int corresponding to the specific client
     */
    public void putFileInTheOutGoingFileQueue(Command command, int clientId) {
        if (outGoingFileQueue.get(clientId) == null || !connections.get(clientId)) {
            outGoingFileQueue.put(clientId, new LinkedBlockingDeque<>(10000));
        }
        outGoingFileQueue.get(clientId).add(command);
    }

    public void putFileInTheHistoryQueue(Command command) {
        try {
            historyQueue.put(command);
        } catch (InterruptedException e) {
            System.out.println("Error while putting the file in the historyQueue, thread has been interrupted");
        }
    }

    /**
     * This method is used to process the outGoingFileQueue.
     * It is executed by an executor for each client.
     * It takes the file from the queue and sends it to the client.
     * The method take() is blocking, so it blocks until it finds an element in the queue to be taken.
     *
     * @param clientId The int corresponding to the specific client
     */
    private void processOutGoingFileQueue(int clientId) {
        if (connections.get(clientId)) {
            Command command = outGoingFileQueue.get(clientId).poll();
            if (command != null) {
                sendMessageToSpecificClient(clientId, command);
            }
        }
    }

    /**
     * This method is used to send a file to the client with id=clientId.
     * It takes the file, converts it to a string and sends it to the client.
     * It uses the output stream of the client to send the file.
     *
     * @param clientId The int of the client which we want to send the file.
     * @param command  The file that has to be sent in broadcast.
     */
    private void sendMessageToSpecificClient(int clientId, Command command) {
        try {
            if (connections.get(clientId)) {
                outputStreams.get(clientId).writeObject(command);
                outputStreams.get(clientId).flush();
            } else
                outGoingFileQueue.get(clientId).push(command);
        } catch (IOException e) {
            shutDownClientRelated(clientId);
            System.out.println("Error while sending message in sendMessageToSpecifiClient " + clientId + " shutting down his connection");
            outGoingFileQueue.get(clientId).push(command);
        }
    }

    /**
     * This method is used to process the InGoingStringQueue and pass the information to the server serializer.
     * It is executed by an executor for each client.
     * It takes the String from the queue and writes it to a file JSON.
     * The method take() is blocking, so it blocks until it finds an element in the queue to be taken.
     *
     * @param clientId The int corresponding to the specific client
     */
    private void processStringQueueToServerSerializer(int clientId) {
        if (connections.get(clientId)) {
            Command command = InGoingStringQueues.get(clientId).poll();
            if (command != null) {
                serverSerializer.putFileFromClientSocketInQueue(command);
            }
        }
    }


    /**
     * This method is used to shut down all the network component of the client related to the given int.
     * It closes the input/output streams, the socket and sets the connection flag to false
     *
     * @param clientId The int of the client which we want shutdown.
     */
    private synchronized void shutDownClientRelated(int clientId) {
        try {
            connections.put(clientId, false);
            if (!clientSockets.get(clientId).isClosed()) {
                connections.put(clientId, false);
                inputStreams.get(clientId).close();
                outputStreams.get(clientId).close();
                clientSockets.get(clientId).close();
            }
            gameSession.getServerController().playerDisconnected(clientId);
        } catch (IOException e) {
            System.err.println("Errore nella chiusura delle connessioni precedenti: " + e.getMessage());
        }
    }

    private void askForPreviousCommands(int clientId) {
        for (Command command : historyQueue) {
            try {
                outGoingFileQueue.get(clientId).put(command);
            } catch (InterruptedException e) {
                System.out.println("Error while putting the file in the outGoingFileQueue from historyQueue, thread has been interrupted");
            }
        }
    }

    /**
     * This method is used to put files in the BroadcastQueue.
     * It is called by the server serializer.
     *
     * @param command The file that has to be put in the outGoingFileQueue.
     */
    public void putFileInTheBroadcastQueue(Command command) {
        try {
            BroadcastQueue.put(command);
        } catch (InterruptedException e) {
            System.out.println("Error while putting the file in the outGoingFileQueue, thread has been interrupted");
        }
    }


    /**
     * This method starts the outgoing broadcast service.
     * It is executed by an executor and is functioning until the server dies.
     * It uses the blocking method take() of the blockinqueue to take the file that has to be sent
     * When it finds a file to be taken, it pass it to the dispatchBroadcast method
     */
    private void startBroadCastService() {
        executors.submit(() -> {
            while (true) {
                try {
                    Command command = BroadcastQueue.take();
                    dispatchBroadcast(command);
                } catch (InterruptedException e) {
                    System.out.println("Error while taking the file from the queue");
                }
            }
        });
    }

    /**
     * This method is used to dispatch broadcast messages to all the
     *
     * @param command The command that has to be dispatched to all the clients.
     */
    private void dispatchBroadcast(Command command) {
        if (!gameStarted) {
            outGoingFileQueue.forEach((clientId, queue) -> {
                putFileInTheOutGoingFileQueue(command, clientId);
            });
        } else {
            playersOutgoingFileQueue.forEach((clientId, queue) -> {
                putFileInTheOutGoingFileQueue(command, clientId);
            });
        }
    }

    public static void main(String[] args) {
        gameSession = new GameSession();
        networkController = new NetworkController();
        ServerController serverController = new ServerController(gameSession);
        gameSession.setServerController(serverController);
        ServerSerializer serverSerializer = new ServerSerializer(serverController);
        serverSerializer.setNetworkController(networkController);
        ClientHandler clientHandler = new ClientHandler(serverSerializer, 8081);
        System.out.println("Server socket acceso!");
        serverSerializer.setClientHandler(clientHandler);
        serverController.setSerializer(serverSerializer);
        try {
            System.setProperty("java.rmi.server.hostname", "localhost");
            ServerRMI serverRMI = new ServerRMI(serverController);
            serverRMI.setNetworkController(networkController);
            Registry reg = LocateRegistry.createRegistry(1099);
            reg.rebind("Server", serverRMI);
            System.out.println("server RMI acceso!");
            serverController.setServerRMI(serverRMI);
        } catch (RemoteException e) {
            System.out.println("errore lato Skeleton: " + e.getMessage());
        }
    }

    public void playerHasJoined(int playerId) {
        playersOutgoingFileQueue.put(playerId, new LinkedBlockingDeque<Command>(1000));
    }

    public void gameStarted() {
        gameStarted = true;
    }

    public boolean containsClient(int playerId) {
        return connections.containsKey(playerId);
    }
}
