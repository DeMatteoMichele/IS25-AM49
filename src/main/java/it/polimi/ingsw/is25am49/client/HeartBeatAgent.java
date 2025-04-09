package it.polimi.ingsw.is25am49.client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class HeartBeatAgent implements Runnable{

    private int interval = 5000;
    private Socket heartBeatSocket;
    private DataOutputStream heartBeatStream;
    private File heartBeatJson;

    public HeartBeatAgent(Socket heartBeatSocket, DataOutputStream heartBeatStream) {
        this.heartBeatSocket = heartBeatSocket;
        this.heartBeatStream = heartBeatStream;
    }
    @Override
    public void run() {
        while(true) {
            try {
                heartBeat();
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                throw new RuntimeException("HeartBeatAgent interrupted" + e.getMessage(), e);
            }
        }
    }

    private void heartBeat() {
        String isHeartBeat = "HTB"; // se non è un HeartBeat "MSG"
        try{
            heartBeatStream.writeInt(isHeartBeat.length());
            heartBeatStream.writeBytes(isHeartBeat);
            heartBeatStream.flush();
            System.out.println("Client still alive");
        }
        catch(IOException e){
            throw new RuntimeException("Errore durante l'invio del JSON: " + e.getMessage(), e);
        }
    }
}

