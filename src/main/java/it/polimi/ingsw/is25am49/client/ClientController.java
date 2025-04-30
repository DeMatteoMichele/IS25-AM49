package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.server.model.Player;

import java.rmi.RemoteException;

public class ClientController {
    public int playerID;
    public ClientModel clientModel;
    public RemoteServer serverMethods;

    public ClientController(){

    }

    public ClientController(int connectionType){
        //this.playerID = playerID;//TODO playerID viene assegnato dal server in qualche modo

    }

    public void clientLogin(){
        //todo playerID= setto l'id con il register client assegnato dal server switch con i due tipi di connessione
    }

    public Integer getPlayerId(){
        return playerID;
    }

    public void clientTestConnection(){
        try {
            serverMethods.serverTestConnection(playerID);
        } catch (RemoteException e) {
            System.out.println("clientTestConnection failed --> " + e.getMessage());
        }
    }

    public void handleInput(String inputString){
        switch (inputString){
            case "placetile"://esempio
                //serverMethods.userPlaceTile();

        };
    }

    public void userPlaceTile(){
        //metodo chiamato dal callback, che aggiorna il model lato client
        //clientModel.placeTile
    }

    public void setServerMethods(RemoteServer serverMethods) {
        this.serverMethods = serverMethods;
    }
}
