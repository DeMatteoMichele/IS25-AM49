package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.client.RemoteServerMethods;
import it.polimi.ingsw.is25am49.server.model.GameSession;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class SkeletonRMI extends UnicastRemoteObject implements RemoteServerMethods {
    ServerController controller;
    public SkeletonRMI() throws RemoteException {
        super();
    }
//    public setHandTile(){
//        controller.setHandTile();
//    }
    public static void main(String[] args) {
        try {
            RemoteServerMethods controller = new SkeletonRMI();

            Registry reg = LocateRegistry.createRegistry(1099);
            reg.rebind("ServerController", controller);
            System.out.println("server in attesa...");

        }
        catch (RemoteException e){
            System.out.println("errore lato Skeleton: "+e.getMessage());
        }
    }

    @Override
    public void serverTestConnection() throws RemoteException {
        controller=new ServerController(new GameSession());
        controller.serverTestConnection();
    }
}
