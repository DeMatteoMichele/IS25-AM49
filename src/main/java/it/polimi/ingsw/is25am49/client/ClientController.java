package it.polimi.ingsw.is25am49.client;

import java.rmi.RemoteException;

public class ClientController {
    ClientModel clientModel;
    RemoteServerMethods server;

    public ClientController(){
        clientModel = new ClientModel();
    }
    public ClientController(Integer connectionType){
        clientModel = new ClientModel();
        server=switch(connectionType){
            case 0 -> new ClientSerializer();
            case 1 -> new StubRMI();
            default -> throw new IllegalStateException("METODO DI CONNESIIONE NON VALIDO --> Unexpected value: " + connectionType);
        };
    }

    public void clientTestConnection() throws RemoteException {
        server.serverTestConnection();
    }

    public void handleInput(String inputString){
        // da fare
    }

}
