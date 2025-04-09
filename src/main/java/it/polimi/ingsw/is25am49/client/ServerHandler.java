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

public class ServerHandler {
    private final ClientSerializer clientSerializer;
    private final Socket clientSocket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    ExecutorService executors;


    public ServerHandler(ClientSerializer clientSerializer) {
        this.clientSerializer = clientSerializer;
        try {
            clientSocket = new Socket("127.0.0.1", 8080);
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
            inputStream = new DataInputStream(clientSocket.getInputStream());
            executors = Executors.newFixedThreadPool(5);
            executors.submit(()->{
                while (true) {
                    try {
                        int length = inputStream.readInt();
                        byte[] buffer = new byte[length];
                        inputStream.readFully(buffer,0,length);
                        String jsonString = new String(buffer, StandardCharsets.UTF_8);
                        File fileJson = new File("received.json");
                        Files.writeString(fileJson.toPath(), jsonString);
                        passInformationToClientSerializer(fileJson);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (UnknownHostException e) {
            throw new RuntimeException("Host sconosciuto: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Errore di I/O: " + e.getMessage(), e);
        }
    }


    public void passInformationToClientHandler(File fileJson) {
        try {
            String jsonContent = Files.readString(fileJson.toPath());
            outputStream.writeInt(jsonContent.length());
            outputStream.writeBytes(jsonContent);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException("Errore durante l'invio del JSON: " + e.getMessage(), e);
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
