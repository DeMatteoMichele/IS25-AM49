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
    private final ScheduledExecutorService scheduler;
    private volatile boolean timeIsNotDone;
    private final int thresholdTries;
    private boolean connected;
    private final int secondsForReconnection;
    private Thread heartBeatThread;
    private final String serverIP;
    private final int serverPort;
    private HeartBeatAgent heartBeatAgent;


    /**
     * Constructor for the ServerHandler class.
     * It initializes the server IP, port, client serializer, and other necessary variables.
     * It also attempts to establish the first connection to the server and starts the heart beat agent.
     *
     * @param clientSerializer The client serializer used to serialize and deserialize messages.
     * @param serverIP         The IP address of the server.
     * @param serverPort       The port number of the server.
     */
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

    /**
     * This method is used to listen to the server and receive messages.
     * While the client is connected, it reads the length of the message, then reads the message itself and writes it to a file JSON.
     * After that, it passes the information to the client serializer for further processing.
     */
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

    /**
     * This method is used to attempt to reconnect to the server after the original connection has failed.
     * It tries to connect to the server a certain number of times (thresholdTries).
     * If it can't make the connection, it waits for a certain amount of time (secondsForReconnection) before trying again.
     * If the reconnection is successful, notifies the heartbeatagent to awake from the wait caused by precedent shutdown.
     * If the connection fails, it shuts down the client.
     */

    public void attemptToConnect() {
        int numberOfTries = 0;
        connected = false;
        while (!connected && numberOfTries <= thresholdTries) {
            try {
                numberOfTries++;
                System.out.println("Connecting to " + serverIP + ":" + serverPort + "\nTentative number: " + numberOfTries);
                timer();
                tryConnection();
                trySend();
                heartBeatAgent.setClientConnection(this);
            } catch (IOException e) {
                shutDown();
            }
        }
        if (numberOfTries >= thresholdTries)
            System.out.println("Connection failed");
        else
            System.out.println("Connection succesful to: " + serverIP + ":" + serverPort);
    }

    /**
     * This method is used to set a timer for the reconnection attempts.
     * It uses a ScheduledExecutorService to schedule a task that sets the timeIsNotDone variable to false after a certain amount of time (secondsForReconnection).
     * The while ensures that the thread is really waiting until timeIsNotDone becomes false.
     */
    public void timer() {
        timeIsNotDone = true;
        scheduler.schedule(() -> {
            timeIsNotDone = false;
        }, secondsForReconnection, TimeUnit.SECONDS);
        while (timeIsNotDone) {
        }
    }

    /**
     * This method is used to send a test message to the server during the reconnection attempt.
     * It sends a string "HTB" to the server and flushes the output stream.
     */

    public void trySend() throws IOException {
        String tryString = "HTB";
        outputStream.writeInt(tryString.length());
        outputStream.writeBytes(tryString);
        outputStream.flush();
        System.out.println("Message: " + tryString);
    }

    /**
     * This method is used to establish a connection to the server.
     * It creates a new socket with serverIp and serverPort and initializes the input and output streams.
     * If the connection is successful, it sets the connected variable to true (this variable is useful to stop the attemptConnection method..
     */
    public void tryConnection() throws IOException {
        clientSocket = new Socket(serverIP, serverPort);
        outputStream = new DataOutputStream(clientSocket.getOutputStream());
        inputStream = new DataInputStream(clientSocket.getInputStream());
        connected = true;
    }

    /**
     * This method is used to establish the first connection to the server.
     * It tries to connect to the server and starts the heart beat agent.
     * If it fails, it shuts down the client.
     */
    public void firstConnection() {
        try {
            tryConnection();
            heartBeatAgent = new HeartBeatAgent(this);
            heartBeatThread = new Thread(heartBeatAgent);
            heartBeatThread.start();
        } catch (IOException e) {
            shutDown();
        }
    }

    /**
     * This method is used to send information to the server.
     * It is called by the clientSerializer to send the serialized object.
     * It reads the content of a JSON file and sends it to the server.
     * It keeps trying to send the message until it is successful or the connected flag goes to false.
     */
    public void sendInformationToServer(File fileJson) {
        if (fileJson == null || !fileJson.exists()) {
            System.err.println("JSON doesnt exist");
            return;
        }
        if (!connected) {
            System.out.println("Server disconnected.\n Write again the command when the connection will be ready");
        } else {
            try {
                String jsonContent = Files.readString(fileJson.toPath());
                outputStream.writeInt(jsonContent.length());
                outputStream.writeBytes(jsonContent);
                outputStream.flush();
                System.out.println("Message: " + jsonContent);
            } catch (IOException e) {
                System.out.println("Server disconnected.\n Write again the command when the connection will be ready");
            }
        }
    }

    /**
     * This method is used to pass the information to the client serializer.
     * It is called by the listenToServer method after receiving a message from the server.
     */
    public void passInformationToClientSerializer(File fileJson) {
        clientSerializer.deSerializeStringFromServer(fileJson);
    }

    /**
     * This method is used to shut down the client.
     * It interrupts the heartbeat thread.
     * It sets the connected variable to false and closes the input and output streams, the socket, and the executor service.
     */
    public void shutDown() {
        connected = false;
        try {
//            if (heartBeatThread != null) {
//                heartBeatThread.interrupt();
//            }
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
