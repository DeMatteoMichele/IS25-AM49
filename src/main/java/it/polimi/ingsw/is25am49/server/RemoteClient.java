package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.shared.Command;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteClient extends Remote {
    void command_FromServerToClient(Command command) throws RemoteException;
    void command_FromServerToSpecificClient(Command command, int clientId) throws RemoteException;
}
