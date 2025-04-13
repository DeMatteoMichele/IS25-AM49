package it.polimi.ingsw.is25am49.server;

import java.net.InetAddress;

public class HeartBeatMonitor implements Runnable {
    private final ClientHandler clientHandler;
    private final int thresholdHeartBeatTime= 15000;
    private final InetAddress clientAddress;

    public HeartBeatMonitor(ClientHandler clientHandler, InetAddress clientAddress) {
        this.clientHandler = clientHandler;
        this.clientAddress = clientAddress;
    }


    /**
     * This method is the runnable for the heart beat monitor.
     * It is the health checker of the connection between a specific client with a unique InetAddress and the server.
     * It computes every 5 seconds the delta between the current time and the last heartbeat time(this is stored in the clientHandler and updated every time the server receive a message).
     * If the delta is greater than the thresholdHeartBeatTime, it calls attemptToConnect.
     */
    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            while (clientHandler.getConnections().get(clientAddress)) {
                if (System.currentTimeMillis() - clientHandler.getLastHeartBeats().get(clientAddress) > thresholdHeartBeatTime) {
                    System.out.println("Client " + clientAddress + " is not responding.");
                    clientHandler.attemptToConnect(clientAddress);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
