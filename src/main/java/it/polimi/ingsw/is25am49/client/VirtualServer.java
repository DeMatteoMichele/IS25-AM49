package it.polimi.ingsw.is25am49.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServer extends Remote {
    void clientTestConnection() throws RemoteException;
}