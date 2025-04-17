package it.polimi.ingsw.is25am49.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualView extends Remote {
    public void testConnection() throws RemoteException;
}
