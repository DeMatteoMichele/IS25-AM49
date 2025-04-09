package it.polimi.ingsw.is25am49.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class HeartBeatAgent implements Runnable{

    private int interval=5000;
    private Socket heartBeatSocket;
    private OutputStream heartBeatStream;

    public HeartBeatAgent() {
        try {
            heartBeatSocket = new Socket("127.0.0.1", 8080);
            heartBeatStream = new DataOutputStream(heartBeatSocket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                heartBeat();
                Thread.sleep(interval);
            } catch (InterruptedException e) {

            }
        }
    }

    private void heartBeat() {

    }
}

