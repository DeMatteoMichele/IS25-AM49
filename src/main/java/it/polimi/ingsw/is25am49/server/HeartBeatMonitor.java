package it.polimi.ingsw.is25am49.server;

public class HeartBeatMonitor implements Runnable {
    private final ClientHandler clientHandler;
    private final int thresholdHeartBeatTime= 15000;
    private final int clientId;

    public HeartBeatMonitor(ClientHandler clientHandler, int clientId) {
        this.clientHandler = clientHandler;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            while (clientHandler.getConnections().get(clientId)) {
                if (System.currentTimeMillis() - clientHandler.getLastHeartBeats().get(clientId) > thresholdHeartBeatTime) {
                    System.out.println("Client " + clientId + " is not responding.");
                    clientHandler.attemptToConnect(clientId);
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
