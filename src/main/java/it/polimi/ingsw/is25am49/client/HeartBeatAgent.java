package it.polimi.ingsw.is25am49.client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class HeartBeatAgent implements Runnable{

    private int interval = 5000;
    private Socket heartBeatSocket;
    private DataOutputStream heartBeatStream;
    private File heartBeatJson;
    ServerHandler serverHandler;

    public HeartBeatAgent(ServerHandler serverHandler) {
        heartBeatSocket = serverHandler.getClientSocket();
        heartBeatStream = serverHandler.getOutputStream();
        this.serverHandler = serverHandler;
    }
    @Override
    public synchronized void run() {
        while(true) {
            try {
                Thread.sleep(interval);
                heartBeat();
            } catch (InterruptedException e) {
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                    System.err.println("Wait HeartBeat: " + e.getMessage());
                }
            }
        }
    }
    public void setClientConnection(ServerHandler serverHandler) {
        heartBeatSocket = serverHandler.getClientSocket();
        heartBeatStream = serverHandler.getOutputStream();
        this.serverHandler = serverHandler;
    }
    private void heartBeat() {
        String HeartBeat = "HTB"; // se non è un HeartBeat "MSG"
        try{
            heartBeatStream.writeInt(HeartBeat.length());
            heartBeatStream.writeBytes(HeartBeat);
            heartBeatStream.flush();
            System.out.println("Client still alive");
        }
        catch(IOException e){
            serverHandler.attemptToConnect();
        }
    }
}

