package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.server.model.GameSession;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.*;

public class ClientHandler {
    private final ServerSerializer serverSerializer;
    private ServerSocket serverSocket;
    private final ExecutorService executors;
    private final ScheduledExecutorService scheduler;
    private final ConcurrentHashMap<InetAddress, Socket> clientSockets;
    private final ConcurrentMap<InetAddress, Boolean> connections;
    private final ConcurrentMap<InetAddress, Long> lastHeartBeats;
    private final ConcurrentMap<InetAddress, DataOutputStream> outputStreams;
    private final ConcurrentMap<InetAddress, DataInputStream> inputStreams;
    private final ConcurrentMap<InetAddress,ArrayBlockingQueue<String>> InGoingStringQueues;
    private final ConcurrentMap<InetAddress,ArrayBlockingQueue<File>> OutGoingFileQueues;
    private final int maxClients;
    private volatile boolean timeIsNotDone;
    private final Thread serverThread;

    public ClientHandler(ServerSerializer serverSerializer, int port) {
        maxClients = 4;
        this.serverSerializer = serverSerializer;
        scheduler = Executors.newScheduledThreadPool(20);
        executors = Executors.newFixedThreadPool(10);
        clientSockets = new ConcurrentHashMap<>();
        outputStreams = new ConcurrentHashMap<>();
        inputStreams = new ConcurrentHashMap<>();
        connections = new ConcurrentHashMap<>();
        lastHeartBeats = new ConcurrentHashMap<>();
        InGoingStringQueues = new ConcurrentHashMap<>();
        OutGoingFileQueues = new ConcurrentHashMap<>();
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
        startCentralHeartBeatMonitor();
        while (true) {
            Socket socket = serverSocket.accept();
            InetAddress clientAddress = socket.getInetAddress();
            if ((clientSockets.size() < maxClients) || (clientSockets.size() == maxClients && connections.containsKey(clientAddress))) {
                System.out.println("Client connected: " + socket.getInetAddress());
                connectClient(socket, clientAddress);
            } else {
                System.out.println("Unwanted connection from: " + socket.getInetAddress());
                socket.close();
            }
        }
    }

    /**
     * This method creates all network components needed for the connection.
     * It submit to an executor the task of listening to that specific client which just connected.
     *
     * @param socket        The socket of the client.
     * @param clientAddress The InetAddress of the client being connected.
     */
    private void connectClient(Socket socket, InetAddress clientAddress) {
        try {
            lastHeartBeats.put(clientAddress, System.currentTimeMillis());
            clientSockets.put(clientAddress, socket);
            outputStreams.put(clientAddress, new DataOutputStream(socket.getOutputStream()));
            inputStreams.put(clientAddress, new DataInputStream(socket.getInputStream()));
            connections.put(clientAddress, true);
            InGoingStringQueues.put(clientAddress, new ArrayBlockingQueue<>(1000, true));
            OutGoingFileQueues.put(clientAddress, new ArrayBlockingQueue<>(1000, true));
            executors.submit(() -> listenToClient(clientAddress));
        } catch (IOException e) {
            System.out.println("Error while connecting to client " + clientAddress);
        }
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
            for (Map.Entry<InetAddress, Long> entry : lastHeartBeats.entrySet()) {
                if (connections.get(entry.getKey())) {
                    if (System.currentTimeMillis() - entry.getValue() > 15000) {
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
     * @param clientAddress The InetAddress of the client being handled.
     */
    private void listenToClient(InetAddress clientAddress) {  //TODO coda per i file json?
        executors.submit(()->processStringQueueToServerSerializer(clientAddress));
        while (connections.get(clientAddress)) {
            try {
                DataInputStream clientInputStream = inputStreams.get(clientAddress);
                int length = clientInputStream.readInt();
                byte[] buffer = new byte[length];
                clientInputStream.readFully(buffer, 0, length);
                String jsonString = new String(buffer, StandardCharsets.UTF_8);
                lastHeartBeats.put(clientAddress, System.currentTimeMillis());
                InGoingStringQueues.get(clientAddress).put(jsonString);
            } catch (IOException e) {
                System.out.println("Error while listening to client " + clientAddress + " shutting down his connection");
                shutDownClientRelated(clientAddress);
            } catch (InterruptedException e) {
                System.out.println("Error while putting the String in the queue for client " + clientAddress + " cause thread was interrupted");
            }
        }
    }

    /**
     * This method is used to process the InGoingStringQueue and pass the information to the server serializer.
     * It is executed by an executor for each client.
     * It takes the String from the queue and writes it to a file JSON.
     * The method take() is blocking, so it blocks until it finds an element in the queue to be taken.
     *
     * @param clientAddress The InetAddress corresponding to the specific client
     */
    private void processStringQueueToServerSerializer(InetAddress clientAddress) {
        while(connections.get(clientAddress)){
            try {
                String jsonString = InGoingStringQueues.get(clientAddress).take();
                File fileJson = new File(clientAddress.getHostAddress() + "_received.json");
                Files.writeString(fileJson.toPath(), jsonString);
                System.out.println("Json Received from " + clientAddress.getHostAddress());
                passInformationToServerSerializer(fileJson);
            } catch (InterruptedException e) {
                System.out.println("Error while taking the String from the queue for client " + clientAddress + " cause thread was interrupted");
            } catch (IOException e) {
                System.out.println("Error while writing the String in  the json for client " + clientAddress);
            }
        }
    }

    /**
     * This method stops the current thread for 3 seconds.
     */
    public void timer() {
        timeIsNotDone = true;
        scheduler.schedule(() -> {
            timeIsNotDone = false;
        }, 3, TimeUnit.SECONDS);
        while (timeIsNotDone) {
        }
    }

    /**
     * This method is used to pass the information received from the client to the server serializer.
     * It is called by the listenToClient method.
     *
     * @param fileJson The file containing the JSON string received from the client.
     */
    public void passInformationToServerSerializer(File fileJson) {
        serverSerializer.deSerializeStringFromClient(fileJson);
    }


    public void passInformationToClient(File fileJson) {
    }

    /**
     * This method is used to shut down all the network component of the client related to the given InetAddress.
     * It closes the input/output streams, the socket and sets the connection flag to false
     *
     * @param clientAddress The InetAddress of the client which we want shutdown.
     */
    private synchronized void shutDownClientRelated(InetAddress clientAddress) {
        try {
            if (clientSockets.get(clientAddress) != null && !clientSockets.get(clientAddress).isClosed()) {
                connections.put(clientAddress, false);
                inputStreams.get(clientAddress).close();
                outputStreams.get(clientAddress).close();
                clientSockets.get(clientAddress).close();
            }
        } catch (IOException e) {
            System.err.println("Errore nella chiusura delle connessioni precedenti: " + e.getMessage());
        }
    }

    public Map<InetAddress, Socket> getClientSockets() {
        return clientSockets;
    }

    public Map<InetAddress, DataOutputStream> getOutputStreams() {
        return outputStreams;
    }

    public Map<InetAddress, DataInputStream> getInputStreams() {
        return inputStreams;
    }

    public Map<InetAddress, Boolean> getConnections() {
        return connections;
    }

    public Map<InetAddress, Long> getLastHeartBeats() {
        return lastHeartBeats;
    }

    public static void main(String[] args) {
        GameSession gameSession = new GameSession();
        ServerController serverController = new ServerController(gameSession);
        ServerSerializer serverSerializer = new ServerSerializer(serverController);
        ClientHandler clientHandler = new ClientHandler(serverSerializer, 8080);
        serverSerializer.setClientHandler(clientHandler);
        serverController.setSerializer(serverSerializer);
        gameSession.setView(serverSerializer);
    }
}
