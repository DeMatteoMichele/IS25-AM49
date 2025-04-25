package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.server.RemoteClient;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Set;

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

    @Override
    public void userDrawTileCallback(int playerId, int tileId) throws RemoteException {

    }

    @Override
    public void userErrorDrawTileCallback(int playerId, int tileId) throws RemoteException {

    }

    @Override
    public void userPlaceTileCallback(int playerId, Point coordinates, Orientation orientation) throws RemoteException {

    }

    @Override
    public void userPickTileCallback(int playerId, Point coordinates) throws RemoteException {

    }

    @Override
    public void userDiscardTileCallback(int playerId) throws RemoteException {

    }

    @Override
    public void userFinishShipboardCallback(int playerId) throws RemoteException {

    }

    @Override
    public void userLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) throws RemoteException {

    }

    @Override
    public void userErrorLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) throws RemoteException {

    }

    @Override
    public void userStopLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) throws RemoteException {

    }

    @Override
    public void userPlaceTimerCallback(int playerId) throws RemoteException {

    }

    @Override
    public void userErrorPlaceTimerCallback(int playerId) throws RemoteException {

    }

    @Override
    public void userSelectTypeOfTokenCallback(int playerId, Token token, Point coordinates) throws RemoteException {

    }

    @Override
    public void userThrowDicesCallback(int playerId, int result) throws RemoteException {

    }

    @Override
    public void userDrawAdventureCardCallback(int playerId) throws RemoteException {

    }

    @Override
    public void userFinishDecisionActivatingCallback(int playerId, Set<Point> coordinates) throws RemoteException {

    }

    @Override
    public void userFinishDecisionTokenCallback(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) throws RemoteException {

    }

    @Override
    public void changeState(String state) throws RemoteException {

    }
}

