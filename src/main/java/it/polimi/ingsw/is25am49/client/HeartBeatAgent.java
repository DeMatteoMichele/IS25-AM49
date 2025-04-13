package it.polimi.ingsw.is25am49.client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class HeartBeatAgent implements Runnable {

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

    /**
     * This method is the runnable for the heart beat agent.
     * It sends a heart beat message to the server every interval milliseconds.
     * If the HeartBeatAgent has been interrupted by the shutdown method of ServerHandler, it waits for a notify from attemptToConnect.
     * It restarts after the notify.
     */
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(interval);
                heartBeat();
            } catch (InterruptedException e) {
                System.err.println("Wait HeartBeat: " + e.getMessage());
            }
        }
    }

    public void setClientConnection(ServerHandler serverHandler) {
        heartBeatSocket = serverHandler.getClientSocket();
        heartBeatStream = serverHandler.getOutputStream();
        this.serverHandler = serverHandler;
    }

    /**
     * This method is used to send a periodic heart beat message to the server.
     * * The message is "HTB" for heart beat.
     * It sends the length of the message and the HTB itself.
     * If it fails, it tries to reconnect to the server.
     */
    private void heartBeat() {
        String HeartBeat = "HTB"; // se non è un HeartBeat "MSG"
        try {
            heartBeatStream.writeInt(HeartBeat.length());
            heartBeatStream.writeBytes(HeartBeat);
            heartBeatStream.flush();
            System.out.println("Client still alive");
        } catch (IOException e) {
            serverHandler.attemptToConnect();
        }
    }
}

