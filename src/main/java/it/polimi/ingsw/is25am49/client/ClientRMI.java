package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.server.RemoteClient;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.server.model.adventurecards.Planet;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Set;

public class ClientRMI implements RemoteClient {
    public GameSession gameSession;
    private Integer playerID;

    private boolean isConnected;

    public ClientRMI() {}
    public ClientRMI(GameSession session) {
        gameSession = session;
    }

    RemoteServer connect(String serverIP, int serverPort) throws RemoteException {
        RemoteServer serverStub = null;
        try {
            Registry reg = LocateRegistry.getRegistry(serverIP, 1099);//eventualmente serverPort
            serverStub = (RemoteServer) reg.lookup("ServerController");//nome serverController da cambiare
            System.out.println("Client successfully connected to ["+serverIP+" , 1099]");

        }catch(RemoteException | NotBoundException e){
            System.out.println("errore creazione stub --> "+e.getMessage());
        }
        return serverStub;
    }

    @Override
    public void notifyClient(String msg) throws RemoteException {
        System.out.println("server - "+msg);
    }

    @Override
    public void registerClientCallback(int playerId) throws RemoteException {

    }

    @Override
    public void playerJoinCallback(int playerId, String username, int seedForGeneration) throws RemoteException {

    }

    @Override
    public void playerErrorJoinCallback(int playerId, String typeOfError) throws RemoteException {

    }

    @Override
    public void adminSetupCallback(int playerId, int maxPlayers, GameMode mode) throws RemoteException {

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

