package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.client.RemoteServer;
import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ServerRMI extends UnicastRemoteObject implements RemoteServer {
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

    @Override
    public void serverTestConnection(int playerId) throws RemoteException {

    }

    @Override
    public void registerClient(RemoteClient client, int playerId) throws RemoteException {

    }

    @Override
    public void userDrawTile(int playerId, int tileId) throws RemoteException {

    }

    @Override
    public void userPlaceTile(int playerId, Point coordinates, Orientation orientation) throws RemoteException {

    }

    @Override
    public void userPickTile(int playerId, Point coordinates) throws RemoteException {

    }

    @Override
    public void userDiscardTile(int playerId) throws RemoteException {

    }

    @Override
    public void userFinishShipboard(int playerId) throws RemoteException {

    }

    @Override
    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) throws RemoteException {

    }

    @Override
    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) throws RemoteException {

    }

    @Override
    public void userPlaceTimer(int playerId) throws RemoteException {

    }

    @Override
    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) throws RemoteException {

    }

    @Override
    public void userThrowDices(int playerId) throws RemoteException {

    }

    @Override
    public void userDrawAdventureCard(int playerId) throws RemoteException {

    }

    @Override
    public void userFinishDecisionActivating(int playerId, Set<Point> coordinates) throws RemoteException {

    }

    @Override
    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) throws RemoteException {

    }
}
