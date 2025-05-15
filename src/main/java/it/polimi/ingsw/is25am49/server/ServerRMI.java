package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.server.model.GameSession;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ServerRMI extends UnicastRemoteObject implements RemoteClient {
    ServerController serverController;
    HashMap<Integer, RemoteClient> clients;

    public ServerRMI() throws RemoteException {
        super();
        serverController=new ServerController(new GameSession());
        clients=new HashMap<>();
    }

    @Override
    public void sendCommand(Command command) throws RemoteException {

    }
}
