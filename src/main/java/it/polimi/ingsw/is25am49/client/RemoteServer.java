package it.polimi.ingsw.is25am49.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteServer extends Remote {
    void sendCommand(Command command) throws RemoteException;
}