package it.polimi.ingsw.is25am49.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteClient extends Remote {
    void command_FromServerToClient(Command command) throws RemoteException;
}
