package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.shared.Command;

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
    public void command_FromServerToClient(Command command) throws RemoteException {

    }

    @Override
    public void command_FromServerToSpecificClient(Command command, int clientId) throws RemoteException {

    }

    /**THIS METHOD IS CALLED BY THE CLIENT
     *REMEMBER: RMI WORKS BY CALLING METHODS ON A REMOTE OBJECT
     * SO: CLIENT CAN CALL HIS INTERFACE METHOD(command_FromClientToServer) ONLY IF
     * SERVERRMI(the remote object) HAS THE METHOD IMPLEMENTED.
     * REMEMBER, WE CAN CALL A METHOD ON AN OBJECT ONLY IF THE OBJECT HAS THAT METHOD. OTHERWISE, WE ARE NOT CALLING ANYTHING.
     * EXAMPLE: TO CALL PLACETILE ON THE SHIPBOARD, THE SHIPBOARD MUST HAVE PLACETILE ON ITS CODE.
     */
    public void command_FromClientToServer(Command command) throws RemoteException {
        //DO NOT CANCEL THIS METHOD
    }

    public void signalThatGameStarted() {
    }
}
