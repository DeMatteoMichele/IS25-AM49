package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.client.RemoteServer;
import it.polimi.ingsw.is25am49.server.model.GameSession;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ServerRMI extends UnicastRemoteObject implements RemoteServer{
    ServerController serverController;
    HashMap<Integer, RemoteClient> clients;

    public ServerRMI() throws RemoteException {
        super();
    }
//    public setHandTile(){
//        controller.setHandTile();
//    }
    public static void main(String[] args) {
        try {
            RemoteServer controller = new ServerRMI();

            Registry reg = LocateRegistry.createRegistry(1099);
            reg.rebind("ServerController", controller);
            System.out.println("server in attesa...");

        }
        catch (RemoteException e){
            System.out.println("errore lato Skeleton: "+e.getMessage());
        }
    }

    @Override
    public void serverTestConnection(Integer playerId) throws RemoteException {
        serverController=new ServerController(new GameSession());
        serverController.serverTestConnection(playerId);
        clients.get(playerId).notifyClient("il client "+playerId+" è stato registrato");
    }

    @Override
    public void registerClient(RemoteClient client, Integer playerId) throws RemoteException {
        clients.put(playerId, client);
    }
}
