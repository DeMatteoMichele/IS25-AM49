package it.polimi.ingsw.is25am49.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerHandler {
    private ClientSerializer clientSerializer;
    private Socket clientSocket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private final ExecutorService executors;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> heartBeatTask;
    private volatile boolean timeIsDone;
    private final int thresholdTries;
    private final AtomicBoolean connected;
    private final int secondsForReconnection;
    private final String serverIP;
    private final int serverPort;
    private final Object outputStreamLock;
    private final BlockingQueue<String> InGoingStringQueue;
    private final BlockingQueue<File> OutGoingFileQueue;


    /**
     * Constructor for the ServerHandler class.
     * It initializes the server IP, port, client serializer, and other necessary variables.
     *
     * @param serverIP   The IP address of the server.
     * @param serverPort The port number of the server.
     */
    public ServerHandler(String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        InGoingStringQueue = new ArrayBlockingQueue<>(1000, true);
        OutGoingFileQueue = new ArrayBlockingQueue<>(1000, true);
        connected = new AtomicBoolean(false);
        scheduler = Executors.newScheduledThreadPool(10);
        thresholdTries = 20;
        secondsForReconnection = 5;
        outputStreamLock = new Object();
        executors = Executors.newFixedThreadPool(10);
    }

    /**
     * This method is used to establish connection to the server.
     * It creates a new socket with serverIp and serverPort and initializes the input and output streams.
     * If the connection is successful, it sets the connected variable to true (this variable is useful to stop the attemptConnection method).
     * Starts the executor to make it listen to the server. Starts the heart beat agent.
     * @throws IOException If an error occurs while establishing the connection.
     */
    private void connection() throws IOException {
        clientSocket = new Socket(serverIP, serverPort);
        outputStream = new DataOutputStream(clientSocket.getOutputStream());
        inputStream = new DataInputStream(clientSocket.getInputStream());
        executors.submit(this::listenToServer);
        executors.submit(this::processOutGoingQueue);
        connected.set(true);
        startHeartBeat();
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
     * This method is used to listen to the server and receive messages.
     * While the client is connected, it reads the length of the message, then reads the message itself and puts it in a BlockingQueue.
     * The BlockingQueue is processed by the processStringQueue method.
     */
    private void listenToServer() {
        executors.submit(this::processStringQueueToSerializer);
        while (connected.get()) {
            try {
                int length = inputStream.readInt();
                byte[] buffer = new byte[length];
                inputStream.readFully(buffer, 0, length);
                String jsonString = new String(buffer, StandardCharsets.UTF_8);
                InGoingStringQueue.put(jsonString);
            } catch (IOException e) {
                System.out.println("Error while listening to server, putting the connection to false");
                disconnectAndTryToReconnect();
            } catch (InterruptedException e) {
                System.out.println("Error while putting the message in the queue, thread has been interrupted");
            }
        }
    }

    /**
     * This method is used to process the messages in the BlockingQueue.
     * The BlockingQueue is thread-safe and method take() blocks itself until a message is available.
     * It takes the message from the queue, writes it to a JSON file, and passes it to the client serializer.
     * It keeps processing messages until the client is connected.
     */
    private void processStringQueueToSerializer() {
        while (connected.get()) {
            try {
                String jsonString = InGoingStringQueue.take();
                File fileJson = new File("jsonFileFromServer.json");
                Files.writeString(fileJson.toPath(), jsonString);
                clientSerializer.putFileFromSocketInQueue(fileJson);
            } catch (IOException e) {
                System.out.println("Error while writing the message to the file");
            } catch (InterruptedException e) {
                System.out.println("Error while taking the message from the queue, thread has been interrupted");
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
                stopHeartBeat();
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
                connection();
                System.out.println("Connection succesful to: " + serverIP + ":" + serverPort);
                break;
            } catch (IOException e) {
                disconnectAndTryToReconnect(); //Reminder: if the connection isn't established, does nothing. Seems redundant but it's not: maybe the socket in tryconnection has been created but the creation of stream gone wrong
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
    private void timer() {
        timeIsDone = true;
        scheduler.schedule(() -> {
            timeIsDone = false;
        }, secondsForReconnection, TimeUnit.SECONDS);
        while (timeIsDone) {
        }
    }


    /**
     * This method is called by ClientSerializer, it is used to put a JSON file in the OutGoingFileQueue.
     * It uses a BlockingQueue to ensure thread safety.
     * If the queue is full, it waits until space becomes available.
     *
     * @param fileJson The JSON file to be put in the OutGoingFileQueue.
     */
    public void putInTheOutGoingFileQueue(File fileJson) {
        try {
            OutGoingFileQueue.put(fileJson);
        } catch (InterruptedException e) {
            System.out.println("Error while putting the file in the OutGoingQueue, thread has been interrupted");
        }
    }

    /**
     * This method is used to process messages in OutGoingFileQueue.
     * It's a method that runs in an executors, submitted in the tryconnection method.
     * It takes the messages from the queue and sends them to the server using the sendMessageToServer method.
     * The method take() blocks itself until a message is available in the queue.
     */
    private void processOutGoingQueue() {
        while (connected.get()) {
            try {
                File fileJson = OutGoingFileQueue.take();
                sendMessageToServer(fileJson);
            } catch (IOException e) {
                System.out.println("Error while sending the message to the server");
            } catch (InterruptedException e) {
                System.out.println("Error while taking the message from the queue, thread has been interrupted");
            }
        }
    }

    /**
     * This method is used to send a message to the server.
     * It takes a JSON file, converts it in a string, sends the string just obtained in the outputsream.
     * It synchronizes on the outputStreamLock object to ensure thread safety.
     * If an IOException occurs, it disconnects and tries to reconnect.
     *
     * @param fileJson The JSON file to be sent to the server.
     * @throws IOException If an I/O error occurs while sending the message.
     */
    private void sendMessageToServer(File fileJson) throws IOException {
        synchronized (outputStreamLock) {
            try {
                String jsonContent = Files.readString(fileJson.toPath());
                outputStream.writeInt(jsonContent.length());
                outputStream.write(jsonContent.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                System.out.println("Message sent: " + jsonContent);
            } catch (IOException e) {
                System.out.println("Error while sending message to server, disconnecting");
                disconnectAndTryToReconnect();
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

    public void setClientSerializer(ClientSerializer clientSerializer) {
        this.clientSerializer = clientSerializer;
    }

    /**
     * Starts the heartbeat task.
     */
    private void startHeartBeat() {
        heartBeatTask = scheduler.scheduleWithFixedDelay(this::heartBeat, 10, 5, TimeUnit.SECONDS);
    }

    /**
     * Stops the heartbeat task.
     */
    private void stopHeartBeat() {
        if (heartBeatTask != null) {
            heartBeatTask.cancel(false);
        }
    }

    public void connectCommand() {
        try {
            connection();
        } catch (IOException e) {
            System.out.println("Error while trying to connect to the server: " + e.getMessage());
        }
    }
}

