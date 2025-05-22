package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.server.model.GameSession;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
    private final ConcurrentMap<Integer, DataOutputStream> outputStreams;
    private final ConcurrentMap<Integer, DataInputStream> inputStreams;
    private final ConcurrentMap<Integer, ArrayBlockingQueue<String>> InGoingStringQueues;
    private final ConcurrentMap<Integer, LinkedBlockingDeque<File>> outGoingFileQueue;
    private final ConcurrentMap<Integer, LinkedBlockingDeque<File>> playersOutgoingFileQueue;
    private final ArrayBlockingQueue<File> BroadcastQueue;
    private final Thread serverThread;
    private boolean gameStarted;
    private final File directory;

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
        gameStarted = false;
        BroadcastQueue = new ArrayBlockingQueue<>(4000, true);
        directory = new File("$FilesFromClients");
        if (!directory.exists()) {
            System.out.println("Directory doesn't exist, creating it");
            directory.mkdirs();
        }
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
        //startCentralHeartBeatMonitor();
        startBroadCastService();
        while (true) {
            Socket socket = serverSocket.accept();
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            int clientId = handleFirstMessage(inputStream);
            if (!gameStarted || connections.containsKey(clientId)) {
                executors.submit(() -> askForPreviousCommands(clientId));
            }
            connectClient(socket, inputStream, outputStream, clientId);
            System.out.println("Client connected with id: " + clientId);
            System.out.println("size: " + clientSockets.size());
        }
    }

    private int handleFirstMessage(DataInputStream inputStream) {
        try {
            int length = inputStream.readInt();
            byte[] buffer = new byte[length];
            inputStream.readFully(buffer, 0, length);
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            File fileJson = new File(directory,"$"+java.util.UUID.randomUUID()+"first_received.json");
            fileJson.deleteOnExit();
            Files.writeString(fileJson.toPath(), jsonString, StandardCharsets.UTF_8);
            return serverSerializer.deserializeHandshake(fileJson);
        } catch (IOException e) {
            System.err.println("Error while reading the first message from new client ");
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
    private void connectClient(Socket socket, DataInputStream clientInputStream, DataOutputStream clientOutputStream, int clientId) {
        lastHeartBeats.put(clientId, System.currentTimeMillis());
        clientSockets.put(clientId, socket);
        outputStreams.put(clientId, clientOutputStream);
        inputStreams.put(clientId, clientInputStream);
        connections.put(clientId, true);
        if (InGoingStringQueues.get(clientId) == null)
            InGoingStringQueues.put(clientId, new ArrayBlockingQueue<>(1000, true));
        executors.submit(() -> listenToClient(clientId));
    }

    /**
     * This method is used to start a centralized heart beat monitor that checks if all the clients are still connected.
     * It works through a ScheduledExecutorService.
     * It scans all the lastHeartBeat map and checks every 5 seconds if the last heart beat received from the clients is older than 15 seconds.
     * If it is, it shuts down the connection with that client.
     * Beware that this method is used as a double check for disconnections in case the catches for IOException in other methods fail.
     */
    private void startCentralHeartBeatMonitor() {
        scheduler.scheduleAtFixedRate(() -> {
            for (Map.Entry<Integer, Long> entry : lastHeartBeats.entrySet()) {
                if (connections.get(entry.getKey())) {
                    if (System.currentTimeMillis() - entry.getValue() > 20000) {
                        System.out.println("Client " + entry.getKey() + " doesn't respond. Shutting down his connection");
                        shutDownClientRelated(entry.getKey());
                    }
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * This method is used to listen to the client and receive messages incoming from the InputStream from the client with id clientId
     * It reads the length of the message, then reads the message itself and writes it to a file JSON.
     * After that, it passes the information to the server serializer.
     *
     * @param clientId The int of the client being handled.
     */
    private void listenToClient(int clientId) {
        executors.submit(() -> processStringQueueToServerSerializer(clientId));
        executors.submit(() -> processOutGoingFileQueue(clientId));
        DataInputStream clientInputStream = inputStreams.get(clientId);
        while (connections.get(clientId)) {
            try {
                int length = clientInputStream.readInt();
                byte[] buffer = new byte[length];
                clientInputStream.readFully(buffer, 0, length);
                String jsonString = new String(buffer, StandardCharsets.UTF_8);
                lastHeartBeats.put(clientId, System.currentTimeMillis());
                if (!jsonString.equals("HTB"))
                    InGoingStringQueues.get(clientId).put(jsonString);
            } catch (IOException e) {
                System.out.println("Error while listening to client " + clientId + " shutting down his connection");
                shutDownClientRelated(clientId);
            } catch (InterruptedException e) {
                System.out.println("Error while putting the String in the queue for client " + clientId + " cause thread was interrupted");
            }
        }
    }

    /**
     * This method is called by serverserializer to put files in the outGoingFileQueue specific to the client with id=clientid.
     * It is executed by an executor for each client.
     * It takes the file from the queue and sends it to the client.
     * The method put() is blocking, so it blocks until there is space in the queue to put the file.
     *
     * @param filejson The file that has to be put in the outGoingFileQueue.
     * @param clientId The int corresponding to the specific client
     */
    public void putFileInTheOutGoingFileQueue(File filejson, int clientId) {
        try {
            if (outGoingFileQueue.get(clientId) == null) {
                outGoingFileQueue.put(clientId, new LinkedBlockingDeque<>(1000));
            }
            outGoingFileQueue.get(clientId).put(filejson);
        } catch (InterruptedException e) {
            System.out.println("Error while putting the file in the outGoingFileQueue, thread has been interrupted");
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
        System.out.println("Starting to process the outGoingFileQueue for client " + clientId);
        while (connections.get(clientId)) {
            try {
                File fileToSend = outGoingFileQueue.get(clientId).take();
                sendMessageToSpecificClient(clientId, fileToSend);
                Thread.sleep(200);
            } catch (InterruptedException e) {
                System.out.println("Error while taking the file from the Outgoing queue of the client " + clientId);
            }
        }
    }

    /**
     * This method is used to send a file to the client with id=clientId.
     * It takes the file, converts it to a string and sends it to the client.
     * It uses the output stream of the client to send the file.
     *
     * @param clientId   The int of the client which we want to send the file.
     * @param fileToSend The file that has to be sent in broadcast.
     */
    private void sendMessageToSpecificClient(int clientId, File fileToSend) {
        try {
            String jsonContent = Files.readString(fileToSend.toPath(), StandardCharsets.UTF_8);
            byte[] buffer = jsonContent.getBytes(StandardCharsets.UTF_8);
            int length = buffer.length;
            System.out.println(jsonContent);
            sendViaOutputStream(clientId, length, buffer);
        } catch (IOException e) {
            System.out.println("Error while sending message in sendmessagetospecificlient " + clientId + " shutting down his connection");
            shutDownClientRelated(clientId);
            try {
                outGoingFileQueue.get(clientId).putFirst(fileToSend);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
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
        while (connections.get(clientId)) {
            try {
                String jsonString = InGoingStringQueues.get(clientId).take();
                File fileJson = new File(directory,"$" + clientId + "_received"+java.util.UUID.randomUUID()+".json");
                fileJson.deleteOnExit();
                Files.writeString(fileJson.toPath(), jsonString, StandardCharsets.UTF_8);
                serverSerializer.putFileFromClientSocketInQueue(fileJson);
            } catch (InterruptedException e) {
                System.out.println("Error while taking the String from the queue for client " + clientId + " cause thread was interrupted");
            } catch (IOException e) {
                System.out.println("Error while writing the String in  the json for client " + clientId);
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
            if (clientSockets.get(clientId) != null) {
                connections.put(clientId, false);
                inputStreams.get(clientId).close();
                outputStreams.get(clientId).close();
                clientSockets.get(clientId).close();
            }
        } catch (IOException e) {
            System.err.println("Errore nella chiusura delle connessioni precedenti: " + e.getMessage());
        }
    }

    private void askForPreviousCommands(int clientId) {
        serverSerializer.giveMeCommandsHistory(clientId);
    }

    /**
     * This method is used to put files in the BroadcastQueue.
     * It is called by the server serializer.
     *
     * @param filejson The file that has to be put in the outGoingFileQueue.
     */
    public void putFileInTheBroadcastQueue(File filejson) {
        try {
            BroadcastQueue.put(filejson);
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
                    File fileToSend = BroadcastQueue.take();
                    dispatchBroadcast(fileToSend);
                } catch (InterruptedException e) {
                    System.out.println("Error while taking the file from the queue");
                }
            }
        });
    }

    /**
     * This method is used to dispatch broadcast messages to all the
     * @param fileToSend
     */
    private void dispatchBroadcast(File fileToSend) {
        if (!gameStarted) {
            outGoingFileQueue.forEach((clientId, queue) -> {
                putFileInTheOutGoingFileQueue(fileToSend, clientId);
            });
        } else {
            playersOutgoingFileQueue.forEach((clientId, queue) -> {
                putFileInTheOutGoingFileQueue(fileToSend, clientId);
            });
        }
    }


    /**
     * This method is used to flush the output stream of the client.
     * It checks if the client is still connected and if it is, it sends the file to the client via its outputstream.
     *
     * @param clientId     The int of the client which we want to send the file.
     * @param length       The length of the file to be sent.
     * @param buffer       The byte array of the file to be sent.
     */
    private void sendViaOutputStream(int clientId, int length, byte[] buffer) throws IOException {
        while(!connections.get(clientId)) {};
        if (connections.get(clientId)) {
            outputStreams.get(clientId).writeInt(length);
            outputStreams.get(clientId).write(buffer);
            outputStreams.get(clientId).flush();
        }
        else throw new IOException("Player is disconnected, putting this file in head of his queue");
    }

    public static void main(String[] args) {
        GameSession gameSession = new GameSession();
        ServerController serverController = new ServerController(gameSession);
        gameSession.setServerController(serverController);
        ServerSerializer serverSerializer = new ServerSerializer(serverController);
        ClientHandler clientHandler = new ClientHandler(serverSerializer, 8082);
        System.out.println("Server socket accesso!");
        serverSerializer.setClientHandler(clientHandler);
        serverController.setSerializer(serverSerializer);
        try {
            ServerRMI serverRMI = new ServerRMI();
            Registry reg = LocateRegistry.createRegistry(1099);
            reg.rebind("ServerController", serverRMI);
            System.out.println("server RMI acceso!");
            serverController.setServerRMI(serverRMI);
        } catch (RemoteException e) {
            System.out.println("errore lato Skeleton: " + e.getMessage());
        }
    }

    public void playerHasJoined(int playerId) {
        playersOutgoingFileQueue.put(playerId, new LinkedBlockingDeque<File>(1000));
    }

    public void gameStarted() {
        gameStarted = true;
        System.out.println("Game started");
    }
}
