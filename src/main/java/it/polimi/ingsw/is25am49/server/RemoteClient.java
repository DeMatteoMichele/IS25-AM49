package it.polimi.ingsw.is25am49.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteClient extends Remote {
    void sendCommand(Command command) throws RemoteException;
    void sendCommandToSpecificClient(Command command, int clientId) throws RemoteException;
}
