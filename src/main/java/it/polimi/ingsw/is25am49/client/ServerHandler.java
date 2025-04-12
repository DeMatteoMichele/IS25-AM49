package it.polimi.ingsw.is25am49.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerHandler {
    private final ClientSerializer clientSerializer;
    private Socket clientSocket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private final ExecutorService executors;
    private ScheduledExecutorService scheduler;
    private volatile boolean timeIsNotDone;
    private int numberOfTries;
    private final int thresholdTries;
    private boolean connected;
    private final int secondsForReconnection;
    private Thread heartBeatThread;
    private final String serverIP;
    private final int serverPort;
    private HeartBeatAgent heartBeatAgent;


    public ServerHandler(ClientSerializer clientSerializer, String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.clientSerializer = clientSerializer;
        heartBeatAgent = null;
        scheduler = Executors.newScheduledThreadPool(10);
        thresholdTries = 20;
        secondsForReconnection = 8;
        executors = Executors.newFixedThreadPool(10);
        firstConnection();
        executors.submit(this::listenToServer);
    }

    public void listenToServer() {  //TODO coda per i file json?
        while (true) {
            while (connected) {
                try {
                    int length = inputStream.readInt();
                    byte[] buffer = new byte[length];
                    inputStream.readFully(buffer, 0, length);
                    String jsonString = new String(buffer, StandardCharsets.UTF_8);
                    File fileJson = new File("received.json");
                    Files.writeString(fileJson.toPath(), jsonString);
                    System.out.println("Json Received\n");
                    passInformationToClientSerializer(fileJson);
                } catch (IOException e) {
                    System.out.println("Server Error");
                    connected = false;
                }
            }
        }
    }

    public void attemptToConnect() {
        numberOfTries = 0;
        connected = false;
        while (!connected && numberOfTries <= thresholdTries) {
            try {
                numberOfTries++;
                System.out.println("Connecting to " + serverIP + ":" + serverPort + "tentative number: " + numberOfTries);
                timer();
                tryConnection();
                trySend();
                synchronized (heartBeatAgent) {
                    heartBeatAgent.setClientConnection(this);
                    heartBeatAgent.notify();
                }
            } catch (IOException e) {
                shutDown();
            }
        }
        if(numberOfTries >= thresholdTries)
            System.out.println("Connection failed");
        else
            System.out.println("Connection succesful to: " + serverIP + ":" + serverPort);
    }

    public void timer() {
        timeIsNotDone = true;
        scheduler.schedule(() -> {
            timeIsNotDone = false;
        }, secondsForReconnection, TimeUnit.SECONDS);
        while (timeIsNotDone){}
    }

    public void trySend() throws IOException {
        String tryString = "HTB";
        outputStream.writeInt(tryString.length());
        outputStream.writeBytes(tryString);
        outputStream.flush();
        System.out.println("Message: " + tryString);
    }

    public void tryConnection() throws IOException {
        clientSocket = new Socket(serverIP, serverPort);
        outputStream = new DataOutputStream(clientSocket.getOutputStream());
        inputStream = new DataInputStream(clientSocket.getInputStream());
        connected = true;

    }
    public void firstConnection() {
        try {
            tryConnection();
            heartBeatAgent = new HeartBeatAgent(this);
            heartBeatThread = new Thread(heartBeatAgent);
            heartBeatThread.start();
        }
        catch (IOException e) {
            shutDown();
        }
    }

    public void passInformationToServer(File fileJson) {
        boolean messageSent = false;
        if (fileJson == null || !fileJson.exists()) {
            System.err.println("JSON doesnt exist");
            return;
        }
        if (!connected) {
            attemptToConnect();
        }
        while (!messageSent && connected) {
            try {
                String jsonContent = Files.readString(fileJson.toPath());
                outputStream.writeInt(jsonContent.length());
                outputStream.writeBytes(jsonContent);
                outputStream.flush();
                messageSent = true;
                System.out.println("Message: " + jsonContent);
            } catch (IOException e) {
                attemptToConnect();
            }
        }
    }

    public void passInformationToClientSerializer(File fileJson) {
        clientSerializer.deSerializeStringFromServer(fileJson);
    }

    public void shutDown() {
        connected = false;
        try {
            if (heartBeatThread != null) {
                heartBeatThread.interrupt();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (executors != null && !executors.isShutdown()) {
                executors.shutdownNow();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Errore durante la chiusura delle risorse: " + e.getMessage());
        }
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }
}
