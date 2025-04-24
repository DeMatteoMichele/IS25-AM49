package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.server.RemoteClient;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteServer extends Remote {
    void serverTestConnection(Integer playerId) throws RemoteException;
    void registerClient(RemoteClient client, Integer playerId)throws RemoteException;
}