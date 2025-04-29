package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.server.RemoteClient;
import it.polimi.ingsw.is25am49.server.model.adventurecards.Planet;
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
import java.util.Scanner;
import java.util.Set;

public class ClientRMI implements RemoteClient {
    private RemoteServer serverStub;
    public ClientController clientController;
    private Integer playerID;

    private boolean isConnected;

    public ClientRMI() {
        //il client si connette al server
        try {
            Registry reg = LocateRegistry.getRegistry("localhost", 1099);
            serverStub = (RemoteServer) reg.lookup("ServerController");
            isConnected = true;
            System.out.println("Client successfully connected to [localhost , 1099]");
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

            ClientController controller = new ClientController();
            controller.serverMethods=clientRMI.getServerStub();

            System.out.println("trying to log in to the server ...");
            UnicastRemoteObject.exportObject(clientRMI, 0);
            controller.playerID=clientRMI.getServerStub().registerClientRMI(clientRMI);
            System.out.println("client logged");

            controller.clientTestConnection();

        } catch (RemoteException e ) {
            System.out.println("errore client si registra su server: "+ e.getMessage());
        }
    }

    @Override
    public void notifyClient(String msg) throws RemoteException {
        System.out.println("server - "+msg);
    }

    @Override
    public void registerClientCallback(RemoteClient client, int playerId) throws RemoteException {

    }

    @Override
    public void playerJoinCallback(int playerId) throws RemoteException {

    }
    @Override
    public void gameStartedCallback() throws RemoteException {

    }

    @Override
    public void userDrawTileCallback(int playerId, int tileId) throws RemoteException {

    }

    @Override
    public void userErrorDrawTileCallback(int playerId, int tileId) throws RemoteException {

    }

    @Override
    public void userPlaceTileCallback(int playerId, Point coordinates, Orientation orientation) throws RemoteException {
        clientController.userPlaceTile();
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
    public void userAcceptPrizesCallback(int playerId) throws RemoteException {

    }

    @Override
    public void userLandOnPlanetCallback(int playerId, boolean isLanded, Planet planet) throws RemoteException {

    }

    @Override
    public void userLandOnStationOrShipCallback(int playerId, boolean isLanded) throws RemoteException {

    }
    @Override
    public void userStopFlightCallback(int playerId) throws RemoteException {

    }
    @Override
    public void adminCalculateScoreCallback(int playerId) throws RemoteException {

    }
    @Override
    public void userEndGameCallback(int playerId) throws RemoteException {

    }

    @Override
    public void changeState(String state) throws RemoteException {

    }
}

