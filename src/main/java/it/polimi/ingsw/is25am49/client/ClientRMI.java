package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.shared.RemoteClient;
import it.polimi.ingsw.is25am49.shared.Command;
import it.polimi.ingsw.is25am49.shared.RemoteServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientRMI implements RemoteServer, RemoteClient {
    public GameSession gameSession;

    private boolean isConnected;

    public ClientRMI() {}
    public ClientRMI(GameSession session) {
        gameSession = session;
    }

    RemoteServer connect(String serverIP, int serverPort) throws RemoteException {
        RemoteServer serverStub = null;
        try {
            Registry reg = LocateRegistry.getRegistry(serverIP, 1099);//eventualmente serverPort
            serverStub = (RemoteServer) reg.lookup("Server");//nome serverController da cambiare
            System.out.println("Client successfully connected to ["+serverIP+" , 1099]");

        }catch(RemoteException | NotBoundException e){
            System.out.println("errore creazione stub --> "+e.getMessage());
        }
        return serverStub;
    }

    @Override
    public void command_FromClientToServer(Command command) throws RemoteException {

    }

    public void setGamesession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    /**THIS METHOD IS CALLED BY THE SERVER
     *REMEMBER: RMI WORKS BY CALLING METHODS ON A REMOTE OBJECT
     * SO: SERVER CAN CALL HIS INTERFACE METHOD(command_FromServerToClient) ONLY IF
     * CLIENTRMI(the remote object) HAS THE METHOD IMPLEMENTED.
     * REMEMBER, WE CAN CALL A METHOD ON AN OBJECT ONLY IF THE OBJECT HAS THAT METHOD. OTHERWISE, WE ARE NOT CALLING ANYTHING.
     * EXAMPLE: TO CALL PLACETILE ON THE SHIPBOARD, THE SHIPBOARD MUST HAVE PLACETILE ON ITS CODE.
     */

    @Override
    public void command_FromServerToClient(Command command) throws RemoteException {

    }

    @Override
    public void command_FromServerToSpecificClient(Command command, int clientId) throws RemoteException {

    }
}

