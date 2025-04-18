package it.polimi.ingsw.is25am49.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteClientMethods extends Remote {
    public void clientTestConnection() throws RemoteException;
}
