package it.polimi.ingsw.is25am49.client;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import it.polimi.ingsw.is25am49.shared.Command;

public class ServerHandler {
    private ClientSerializer clientSerializer;
    private Socket clientSocket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> heartBeatTask;
    private final AtomicBoolean connected;
    private final String serverIP;
    private final int serverPort;
    private final BlockingQueue<Command> InGoingStringQueue;
    private final LinkedBlockingDeque<Command> OutGoingFileQueue;
    private int myId;
    private boolean serverKnowsMe;

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
        OutGoingFileQueue = new LinkedBlockingDeque<>(1000);
        connected = new AtomicBoolean(false);
        scheduler = Executors.newScheduledThreadPool(10);
        serverKnowsMe = false;
        submitToScheduler();
    }

    /**
     * This method is used to establish connection to the server.
     * It creates a new socket with serverIp and serverPort and initializes the input and output streams.
     * If the connection is successful, it sets the connected variable to true (this variable is useful to stop the attemptConnection method).
     * Starts the executor to make it listen to the server. Starts the heart beat agent.
     *
     * @throws IOException If an error occurs while establishing the connection.
     */
    private void connection() throws IOException {
        clientSocket = new Socket(serverIP, serverPort);
        objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        connected.set(true);
        serverKnowsMe = true;
    }

    private void submitToScheduler() {
        scheduler.scheduleWithFixedDelay(() -> listenToServer(), 0, 25, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(() -> processStringQueueToSerializer(), 512, 25, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(() -> processOutGoingQueue(), 512, 25, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(() -> connectionHealthControl(), 0, 2000, TimeUnit.MILLISECONDS);
        heartBeatTask = scheduler.scheduleWithFixedDelay(this::heartBeat, 10, 5, TimeUnit.SECONDS);
    }

    /**
     * This method is used to send a heart beat message to the server.
     * It is called periodically in a scheduled task.
     * It checks if the client is connected and sends a HTB to the server.
     * If it fails, it disconnects and tries to reconnect.
     */
    private void heartBeat() {
        if (connected.get()) {
            Command command = new Command("heartbeat", myId);
            putInTheOutGoingFileQueue(command);
        }
    }

    /**
     * This method is used to listen to the server and receive messages.
     * While the client is connected, it reads the length of the message, then reads the message itself and puts it in a BlockingQueue.
     * The BlockingQueue is processed by the processStringQueue method.
     */
    private void listenToServer() {
        if (connected.get()) {
            try {
                Command command = (Command) objectInputStream.readObject();
                InGoingStringQueue.put(command);
            } catch (IOException e) {
                System.out.println("Error while listening to server, putting the connection to false");
                disconnect();
            } catch (InterruptedException e) {
                System.out.println("Error while putting the message in the queue, thread has been interrupted");
            } catch (ClassNotFoundException e) {
                System.out.println("Error reading command in serverhandler from inputstream");
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
        if (connected.get()) {
            Command command = InGoingStringQueue.poll();
            if (command != null)
                clientSerializer.putFileFromSocketInQueue(command);
        }
    }


    /**
     * This method is used to disconnect from the server and try to reconnect.
     * It works only if at the time the method is called the connection flag is true, ensuring redundant disconnection are avoided in case more methods call this .
     * It sets the connected flag to false.
     * It closes the input/output streams and the socket, then submits a new connection attempt to the executor service.
     */
    private synchronized void disconnect() {
        if (connected.get()) {
            try {
                connected.set(false);
                if (!clientSocket.isClosed()) {
                    objectInputStream.close();
                    objectOutputStream.close();
                    clientSocket.close();
                }
//                stopHeartBeat();
            } catch (IOException e) {
                System.err.println("Errore durante la chiusura delle risorse per la disconnesione: " + e.getMessage());
            }
//        catch (SocketException e) {
//            System.err.println("Socket already closed: " + e.getMessage());
//        }
        }
    }

    /**
     * This method is used to attempt to reconnect to the server after the original connection has failed.
     * It tries to connect to the server a certain number of times (thresholdTries).
     * If it can't make the connection, it waits for a certain amount of time (secondsForReconnection) before trying again using the timer() method.
     */
    public void connectionHealthControl() {
        if (!connected.get() && serverKnowsMe) {
            clientSerializer.askControllerToResetModel();
            System.out.println("Attempting to reconnect to the server...");
            try {
                handshake();
                System.err.println("Connection succesful to: " + serverIP + ":" + serverPort);
            } catch (IOException e) {
                disconnect(); //Reminder: if the connection isn't established, this does nothing. Seems redundant but it's not: maybe the socket in tryconnection has been created but the creation of stream gone wrong
            }
        }
    }

    private void handshake() throws IOException {
        clientSerializer.askControllerToStartRegistration();
    }

//    /**
//     * This method is used to set a timer for the reconnection attempts.
//     * It uses a ScheduledExecutorService to schedule a task that sets the timeIsNotDone variable to false after a certain amount of time (secondsForReconnection).
//     * The while ensures that the thread is really waiting until timeIsNotDone becomes false.
//     */
//    private void timer() {
//        timeIsDone = true;
//        scheduler.schedule(() -> {
//            timeIsDone = false;
//        }, secondsForReconnection, TimeUnit.SECONDS);
//        while (timeIsDone) {
//        }
//    }


    /**
     * This method is called by ClientSerializer, it is used to put a JSON file in the OutGoingFileQueue.
     * It uses a BlockingQueue to ensure thread safety.
     * If the queue is full, it waits until space becomes available.
     *
     * @param command The JSON file to be put in the OutGoingFileQueue.
     */
    public void putInTheOutGoingFileQueue(Command command) {
        OutGoingFileQueue.add(command);
    }

    /**
     * This method is used to process messages in OutGoingFileQueue.
     * It's a method that runs in an executors, submitted in the tryconnection method.
     * It takes the messages from the queue and sends them to the server using the sendMessageToServer method.
     * The method take() blocks itself until a message is available in the queue.
     */
    private void processOutGoingQueue() {
        if (connected.get()) {
            Command command = OutGoingFileQueue.poll();
            if (command != null) {
                sendMessageToServer(command);
            }
        }
    }

    /**
     * This method is used to send a message to the server.
     * It takes a JSON file, converts it in a string, sends the string just obtained in the outputsream.
     * It synchronizes on the outputStreamLock object to ensure thread safety.
     * If an IOException occurs, it disconnects and tries to reconnect.
     *
     * @param command The JSON file to be sent to the server.
     * @throws IOException If an I/O error occurs while sending the message.
     */
    private void sendMessageToServer(Command command) {
        try {
            if (connected.get()) {
                objectOutputStream.reset();
                objectOutputStream.writeObject(command);
                objectOutputStream.flush();
            } else
                OutGoingFileQueue.push(command);
        } catch (IOException e) {
            System.out.println("Error while sending message to server, disconnecting");
            disconnect();
            e.printStackTrace();
            OutGoingFileQueue.push(command);
        }
    }

    public void setClientSerializer(ClientSerializer clientSerializer) {
        this.clientSerializer = clientSerializer;
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
            throw new RuntimeException("Error while trying to connect to the server - " + e.getMessage());
        }
    }

    public void setMyId(int playerId) {
        myId = playerId;
    }
}




