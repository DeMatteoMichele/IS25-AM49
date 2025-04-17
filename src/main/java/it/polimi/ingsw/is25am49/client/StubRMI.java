package it.polimi.ingsw.is25am49.client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class StubRMI implements VirtualServer{
    public static void main(String[] args){
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 1099);
            StubRMI stub = (StubRMI) reg.lookup("ServerController");

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}

