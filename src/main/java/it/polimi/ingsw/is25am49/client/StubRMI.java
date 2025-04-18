package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.server.RemoteClientMethods;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class StubRMI implements RemoteServerMethods {
    RemoteServerMethods stub;
    public static void main(String[] args){
        try {
            ClientController controller = new ClientController(1);
            controller.clientTestConnection();
        }catch(RemoteException e){
            System.out.println(e.getMessage());
        }
    }

    void createStub(){
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 1099);
            stub = (RemoteServerMethods) reg.lookup("ServerController");
        }catch (Exception e){
            System.out.println("errore lato Stub: "+e.getMessage());
        }
    }

    @Override
    public void serverTestConnection() throws RemoteException {
        createStub();
        if(stub != null){
            stub.serverTestConnection();
        }else{
            System.out.println("Stub non creato");
        }
    }
}

