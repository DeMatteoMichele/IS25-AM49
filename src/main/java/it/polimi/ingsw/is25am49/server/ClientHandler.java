package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.server.model.GameSession;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
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
    private Map<Integer, Socket> clientSockets;
    private Map<Integer, Boolean> connections;
    private Map<Integer, Long> lastHeartBeats;
    private Map<Integer, DataOutputStream> outputStreams;
    private Map<Integer, DataInputStream> inputStreams;
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
            System.out.println("Server started, waiting for client connection...");
            serverSocket = new ServerSocket(port);
            for (int i = 0; i < maxClients; i++) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());
                clientSockets.put(i, socket);
                outputStreams.put(i, new DataOutputStream(socket.getOutputStream()));
                inputStreams.put(i, new DataInputStream(socket.getInputStream()));
                connections.put(i, true);
                final int clientId = i;
                executors.submit(() -> handleClient(clientId));
                Thread heartBeatMonitorThread = new Thread(new HeartBeatMonitor(this, i));
                heartBeatMonitorThread.start();
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    public void handleClient(int clientId) {
        lastHeartBeats.put(clientId, System.currentTimeMillis());
        while (true) {
            while (connections.get(clientId)) {
                try {
                    if (inputStreams.get(clientId).available() > 0) {
                        listenToClient(clientId);
                        lastHeartBeats.put(clientId, System.currentTimeMillis());
                    }
                } catch (IOException e) {
                    connections.put(clientId, false);
                }
            }
        }
    }

    private void listenToClient(int clientId) throws IOException {  //TODO coda per i file json?
        DataInputStream clientInputStream = inputStreams.get(clientId);
        int length = clientInputStream.readInt();
        byte[] buffer = new byte[length];
        clientInputStream.readFully(buffer, 0, length);
        String jsonString = new String(buffer, StandardCharsets.UTF_8);
        File fileJson = new File("received.json");
        Files.writeString(fileJson.toPath(), jsonString);
        System.out.println("Json Received\n");
        passInformationToServerSerializer(fileJson);
    }

    public void attemptToConnect(int clientId) {
        int numAttempts = 0;
        shutDownClientRelated(clientId);
        while (!connections.get(clientId) && numAttempts <= thresholdAttempts) {
            try {
                timer();
                numAttempts++;
                System.out.println("Attempting to reconnect client " + clientId);
                serverSocket.setSoTimeout(secondsForReconnection * 1000);
                Socket socket = serverSocket.accept();
                System.out.println("Client reconnected: " + socket.getInetAddress());
                clientSockets.put(clientId, socket);
                outputStreams.put(clientId, new DataOutputStream(socket.getOutputStream()));
                inputStreams.put(clientId, new DataInputStream(socket.getInputStream()));
                connections.put(clientId, true);
                return;
            } catch (IOException e) {
                System.err.println("Error reconnecting client: " + e.getMessage());
            }
        }
        System.out.println("Client " + clientId + " didn't reconnect.");
    }

    public void timer() {
        try {
            Thread.sleep(secondsForReconnection * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void passInformationToServerSerializer(File fileJson) {
        serverSerializer.deSerializeStringFromClient(fileJson);
    }

    public void passInformationToClient(File fileJson) {
    }

    private void shutDownClientRelated(int clientId) {
        try {
            if (clientSockets.get(clientId) != null && !clientSockets.get(clientId).isClosed()) {
                connections.put(clientId, false);
                inputStreams.get(clientId).close();
                outputStreams.get(clientId).close();
                clientSockets.get(clientId).close();
            }
        } catch (IOException e) {
            System.err.println("Errore nella chiusura delle connessioni precedenti: " + e.getMessage());
        }
    }

    public Map<Integer, Socket> getClientSockets() {
        return clientSockets;
    }

    public Map<Integer, DataOutputStream> getOutputStreams() {
        return outputStreams;
    }

    public Map<Integer, DataInputStream> getInputStreams() {
        return inputStreams;
    }

    public Map<Integer, Boolean> getConnections() {
        return connections;
    }

    public Map<Integer, Long> getLastHeartBeats() {
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
