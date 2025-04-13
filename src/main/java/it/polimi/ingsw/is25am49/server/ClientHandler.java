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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler {
    private final ServerSerializer serverSerializer;
    private ServerSocket serverSocket;
    private ExecutorService executors;
    private Map<InetAddress, Socket> clientSockets;
    private Map<InetAddress, Boolean> connections;
    private Map<InetAddress, Long> lastHeartBeats;
    private Map<InetAddress, DataOutputStream> outputStreams;
    private Map<InetAddress, DataInputStream> inputStreams;
    private final int maxClients = 4;
    private final int thresholdAttempts = 10;
    private final int secondsForReconnection = 3;

    public ClientHandler(ServerSerializer serverSerializer, int port) {
        this.serverSerializer = serverSerializer;
        executors = Executors.newFixedThreadPool(10);
        clientSockets = new HashMap<>();
        outputStreams = new HashMap<>();
        inputStreams = new HashMap<>();
        connections = new HashMap<>();
        lastHeartBeats = new HashMap<>();
        try {
            startServer(port);

        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }


    private void startServer(int port) throws IOException {
        System.out.println("Server started, waiting for client connection...");
        serverSocket = new ServerSocket(port);
        while (clientSockets.size() < maxClients) {
            Socket socket = serverSocket.accept();
            InetAddress clientAddress = socket.getInetAddress();
            System.out.println("Client connected: " + socket.getInetAddress());
            clientSockets.put(clientAddress, socket);
            outputStreams.put(clientAddress, new DataOutputStream(socket.getOutputStream()));
            inputStreams.put(clientAddress, new DataInputStream(socket.getInputStream()));
            connections.put(clientAddress, true);
            executors.submit(() -> handleClient(clientAddress));
            Thread heartBeatMonitorThread = new Thread(new HeartBeatMonitor(this, clientAddress));
            heartBeatMonitorThread.start();
        }
    }

    /**
     * This method is used to handle the client connection.
     * It calls the listenToClient method and updates the last heart beat time every time it arrives a message.
     * If the connection is lost (it acknowledge this because when inputstream fails it throws IoException), it sets the connection flag to false.
     *
     * @param clientAddress The InetAddress of the client being handled.
     */
    public void handleClient(InetAddress clientAddress) {
        lastHeartBeats.put(clientAddress, System.currentTimeMillis());
        while (true) {
            while (connections.get(clientAddress)) {
                try {
                    if (inputStreams.get(clientAddress).available() > 0) {
                        listenToClient(clientAddress);
                        lastHeartBeats.put(clientAddress, System.currentTimeMillis());
                    }
                } catch (IOException e) {
                    connections.put(clientAddress, false);
                }
            }
        }
    }

    /**
     * Called by handleClient method.
     * This method is used to listen to the client and receive messages incoming from the InputStream from the client with id clientId
     * It reads the length of the message, then reads the message itself and writes it to a file JSON.
     * After that, it passes the information to the server serializer.
     *
     * @param clientAddress The InetAddress of the client being handled.
     */
    private void listenToClient(InetAddress clientAddress) throws IOException {  //TODO coda per i file json?
        DataInputStream clientInputStream = inputStreams.get(clientAddress);
        int length = clientInputStream.readInt();
        byte[] buffer = new byte[length];
        clientInputStream.readFully(buffer, 0, length);
        String jsonString = new String(buffer, StandardCharsets.UTF_8);
        File fileJson = new File("received.json");
        Files.writeString(fileJson.toPath(), jsonString);
        System.out.println("Json Received\n");
        passInformationToServerSerializer(fileJson);
    }


    /**
     * This method is used to attempt to reconnect a client after a disconnection.
     * This method is called by the HeartBeatMonitor when it detects that a client is not responding.
     * It tries to accept a new connection from the client and re-establish the input and output streams.
     * If the connection is successful, it updates the connection status and method handleClient can restart working correctly.
     * If all tentative failed, it prints in output and connection flag remain false
     *
     * @param clientAddress The InetAddress of the client being handled.
     */
    public void attemptToConnect(InetAddress clientAddress) {
        int numAttempts = 0;
        shutDownClientRelated(clientAddress);
        while (!connections.get(clientAddress) && numAttempts <= thresholdAttempts) {
            try {
                timer();
                numAttempts++;
                System.out.println("Attempting to reconnect client " + clientAddress);
                serverSocket.setSoTimeout(secondsForReconnection * 1000);
                Socket socket = serverSocket.accept();
                System.out.println("Client reconnected: " + socket.getInetAddress());
                clientSockets.put(clientAddress, socket);
                outputStreams.put(clientAddress, new DataOutputStream(socket.getOutputStream()));
                inputStreams.put(clientAddress, new DataInputStream(socket.getInputStream()));
                connections.put(clientAddress, true);
                return;
            } catch (IOException e) {
                System.err.println("Error reconnecting client: " + e.getMessage());
            }
        }
        System.out.println("Client " + clientAddress.getHostAddress() + " didn't reconnect.");
    }

    /**
     * This method stops the current thread for 1 second
     */
    public void timer() {   //TODO È DIVERSO DAL METODO IN SERVERHANDLER... DA MODIFICARE?
        try {
            Thread.sleep(secondsForReconnection * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
    private void shutDownClientRelated(InetAddress clientAddress) {
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
