package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.server.model.Player;

import java.rmi.RemoteException;

public class ClientController {
    private int playerID;
    public ClientModel clientModel;
    public RemoteServer serverMethods;

    public ClientController(){
        clientModel = new ClientModel();
    }
    public ClientController(int playerID){this.playerID = playerID;}
    public ClientController(Integer connectionType, Integer playerID){
        this.playerID = playerID;
        clientModel = new ClientModel();
        serverMethods= switch(connectionType){
            case 0 -> new ClientSerializer();
            case 1 -> new ClientRMI().getServerStub();
            default -> throw new IllegalStateException("METODO DI CONNESIIONE NON VALIDO --> Unexpected value: " + connectionType);
        };
    }

    public void clientTestConnection(){
        try {
            serverMethods.serverTestConnection(playerID);
        } catch (RemoteException e) {
            System.out.println("clientTestConnection failed --> " + e.getMessage());
        }
    }

    public void handleInput(String inputString){
        // da fare
    }

}
