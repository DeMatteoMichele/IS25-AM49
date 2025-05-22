package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.client.RemoteServer;
import it.polimi.ingsw.is25am49.server.model.GameSession;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ServerRMI extends UnicastRemoteObject implements RemoteClient, RemoteServer {
    ServerController serverController;
    HashMap<Integer, RemoteClient> clients;

    public ServerRMI(ServerController serverController) throws RemoteException {
        super();
        this.serverController = serverController;
    }

    @Override
    public void command_FromServerToClient(Command command) throws RemoteException {

    }

    @Override
    public void command_FromServerToSpecificClient(Command command, int clientId) throws RemoteException {

    }

    public void signalThatGameStarted() {

    }

    /**THIS METHOD IS CALLED BY THE CLIENT
     *REMEMBER: RMI WORKS BY CALLING METHODS ON A REMOTE OBJECT
     * SO: CLIENT CAN CALL HIS INTERFACE METHOD(command_FromClientToServer) ONLY IF
     * SERVERRMI(the remote object) HAS THE METHOD IMPLEMENTED.
     * REMEMBER, WE CAN CALL A METHOD ON AN OBJECT ONLY IF THE OBJECT HAS THAT METHOD. OTHERWISE, WE ARE NOT CALLING ANYTHING.
     * EXAMPLE: TO CALL PLACETILE ON THE SHIPBOARD, THE SHIPBOARD MUST HAVE PLACETILE ON ITS CODE.
     */

    @Override
    public void command_FromClientToServer(it.polimi.ingsw.is25am49.client.Command command) throws RemoteException {
        if(command.getTypeOfMessage().equals("HSK")) {

        }
    }
}
