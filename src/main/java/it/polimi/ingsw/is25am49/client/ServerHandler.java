package it.polimi.ingsw.is25am49.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
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
    int intervalHeartBeat = 5000;
    int numberOfTries;
    int thresholdTries;
    boolean connected;
    int secondsForReconnection;
    boolean timeIsNotDone;

    public ServerHandler(ClientSerializer clientSerializer) {
        this.clientSerializer = clientSerializer;
        scheduler = Executors.newScheduledThreadPool(5);
        thresholdTries = 10;
        secondsForReconnection = 3;
        attemptedConnection();
        executors = Executors.newFixedThreadPool(5);
        executors.submit(() -> {
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
                    attemptedConnection();
                }
            }
        });
        executors.submit(new HeartBeatAgent(clientSocket, outputStream));
    }

    public void attemptedConnection() {
        numberOfTries = 0;
        connected = false;
        while (!connected && numberOfTries <= thresholdTries) {
            try {
                timer();
                numberOfTries++;
                tryConnection();
                trySend();
                connected = true;
            } catch (IOException e) {
                shutDown();
            }
        }
    }

    public void timer() {
        timeIsNotDone = true;
        while (timeIsNotDone)
            scheduler.schedule(() -> {
                timeIsNotDone = false;
            }, secondsForReconnection, TimeUnit.SECONDS);
    }

    public void trySend() throws IOException {
        String tryString = "HTB";
        outputStream.writeInt(tryString.length());
        outputStream.writeBytes(tryString);
        outputStream.flush();
        System.out.println("Message: " + tryString);
    }

    public void tryConnection() throws IOException {
        clientSocket = new Socket("127.0.0.1", 8080);
        outputStream = new DataOutputStream(clientSocket.getOutputStream());
        inputStream = new DataInputStream(clientSocket.getInputStream());
        connected = true;
    }

    public void passInformationToClientHandler(File fileJson) {
        boolean messageSent = false;
        if (!connected) {
            attemptedConnection();
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
                attemptedConnection();
            }
        }
    }

    public void passInformationToClientSerializer(File fileJson) {
        clientSerializer.deSerializeStringFromServer(fileJson);
    }

    public void shutDown() {
        try {
            inputStream.close();
            outputStream.close();
            executors.shutdownNow();
            clientSocket.close();
            connected = false;
            System.out.println("Connection closed");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
