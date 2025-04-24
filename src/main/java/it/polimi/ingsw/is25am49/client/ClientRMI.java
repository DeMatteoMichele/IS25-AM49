package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.server.RemoteClient;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClientRMI implements RemoteClient {
    private RemoteServer serverStub;
    private boolean isConnected;

    public ClientRMI() {
        //il client si connette al server
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 1099);
            serverStub = (RemoteServer) reg.lookup("ServerController");
            isConnected = true;
        }catch(RemoteException | NotBoundException e){
            System.out.println("errore creazione stub --> "+e.getMessage());
            isConnected = false;
        }
    }

    public RemoteServer getServerStub() {
        return serverStub;
    }
    public static void main(String[] args){
        try {
            //il client registra sul server
            ClientRMI clientRMI = new ClientRMI();
            RemoteClient client = clientRMI;

            ClientController controller = new ClientController(1,10);
            controller.clientTestConnection();
            System.out.println("client aggiunto al register client");

            UnicastRemoteObject.exportObject(client, 0);
            clientRMI.getServerStub().registerClient(client, 10);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void notifyClient(String msg) throws RemoteException {
        System.out.println("messaggio: "+msg);
    }
}

