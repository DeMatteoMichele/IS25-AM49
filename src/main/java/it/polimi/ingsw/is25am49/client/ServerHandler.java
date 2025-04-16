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
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerHandler {
    private final ClientSerializer clientSerializer;
    private Socket clientSocket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private final ExecutorService executors;
    private final ScheduledExecutorService scheduler;
    private volatile boolean timeIsNotDone;
    private final int thresholdTries;
    private AtomicBoolean connected;
    private final int secondsForReconnection;
    private Thread heartBeatThread;
    private final String serverIP;
    private final int serverPort;
    private final Object outputStreamLock;


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
        connected = new AtomicBoolean(false);
        scheduler = Executors.newScheduledThreadPool(10);
        thresholdTries = 20;
        secondsForReconnection = 5;
        outputStreamLock = new Object();
        executors = Executors.newFixedThreadPool(10);
        firstConnection();
    }

    /**
     * This method is used to establish the first connection to the server.
     * It tries to connect to the server and starts the heart beat agent.
     * If it fails, it disconnects the client..
     */
    public void firstConnection() {
        try {
            tryConnection(serverPort);
            executors.submit(this::listenToServer);
            scheduler.scheduleWithFixedDelay(this::heartBeat, 0, 5, TimeUnit.SECONDS);
        } catch (IOException e) {
            disconnectAndTryToReconnect();
        }
    }

    /**
     * This method is used to send a heart beat message to the server.
     * It is called periodically in a scheduled task.
     * It checks if the client is connected and sends a HTB to the server.
     * If it fails, it disconnects and tries to reconnect.
     */
    private void heartBeat() {
        String HeartBeat = "HTB"; // se non è un HeartBeat "MSG"
        if (connected.get()) {
            synchronized (outputStreamLock) {
                try {
                    outputStream.writeInt(HeartBeat.length());
                    outputStream.writeBytes(HeartBeat);
                    outputStream.flush();
                    System.out.println("Client still alive");
                } catch (IOException e) {
                    disconnectAndTryToReconnect();
                }
            }
        }
    }

    /**
     * This method is used to establish a connection to the server.
     * It creates a new socket with serverIp and serverPort and initializes the input and output streams.
     * If the connection is successful, it sets the connected variable to true (this variable is useful to stop the attemptConnection method..
     */
    public void tryConnection(int port) throws IOException {
        clientSocket = new Socket(serverIP, port);
        outputStream = new DataOutputStream(clientSocket.getOutputStream());
        inputStream = new DataInputStream(clientSocket.getInputStream());
        connected.set(true);
    }

    /**
     * This method is used to listen to the server and receive messages.
     * While the client is connected, it reads the length of the message, then reads the message itself and writes it to a file JSON.
     * After that, it passes the information to the client serializer for further processing.
     */
    public void listenToServer() {  //TODO coda per i file json?
        while (true) {
            while (connected.get()) {
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
                    System.out.println("Error while listening to server, putting the connection to false");
                    disconnectAndTryToReconnect();
                }
            }
        }
    }


    /**
     * This method is used to disconnect from the server and try to reconnect.
     * It works only if at the time the method is called the connection flag is true, ensuring redundant disconnection are avoided in case more methods call this .
     * It sets the connected flag to false.
     * It closes the input/output streams and the socket, then submits a new connection attempt to the executor service.
     */
    private synchronized void disconnectAndTryToReconnect() {
        if (connected.get()) {
            connected.set(false);
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
                System.out.println("submitting the connection attempt");
                executors.submit(this::attemptToConnect);
            } catch (IOException e) {
                System.err.println("Errore durante la chiusura delle risorse per la disconnesione: " + e.getMessage());
            }
        }
    }

    /**
     * This method is used to attempt to reconnect to the server after the original connection has failed.
     * It tries to connect to the server a certain number of times (thresholdTries).
     * If it can't make the connection, it waits for a certain amount of time (secondsForReconnection) before trying again using the timer() method.
     */
    public void attemptToConnect() {
        System.out.println("Attempting to reconnect to the server...");
        int numberOfTries = 0;
        while (numberOfTries < thresholdTries) {
            try {
                numberOfTries++;
                System.out.println("Connecting to " + serverIP + ":" + serverPort + "\nTentative number: " + numberOfTries);
                tryConnection(serverPort);
                System.out.println("Connection succesful to: " + serverIP + ":" + serverPort);
                break;
            } catch (IOException e) {
                disconnectAndTryToReconnect(); //seems redundant but it's not: maybe the socket in tryconnection has been created but the creation of stream gone wrong
            }
            timer();
        }
        if (numberOfTries >= thresholdTries)
            System.out.println("Connection failed");
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
        if (!connected.get()) {
            System.out.println("Server disconnected.\n Write again the command when the connection will be ready");
        } else {
            synchronized (outputStreamLock) {
                try {
                    String jsonContent = Files.readString(fileJson.toPath());
                    outputStream.writeInt(jsonContent.length());
                    outputStream.writeBytes(jsonContent);
                    outputStream.flush();
                    System.out.println("Message: " + jsonContent);
                } catch (IOException e) {
                    System.out.println("Error while sending message to server, disconnecting");
                    disconnectAndTryToReconnect();
                }
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

    public Socket getClientSocket() {
        return clientSocket;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }
}
