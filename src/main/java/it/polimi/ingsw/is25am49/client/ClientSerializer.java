package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.server.RemoteClient;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.adventurecards.Planet;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClientSerializer implements RemoteServer {
    private ServerHandler serverHandler;
    public void placetile(){
        //traduci in un pacchetto serializzato
    }

    public void deSerializeStringFromServer(File fileJson){
        //deserializza il pacchetto
    }

    public File serializeObjectFromClient(String method, List<Object> parameters){
        //serializza il pacchetto
        return null;
    }

    @Override
    public void serverTestConnection(int playerId) throws RemoteException {
        System.out.println("no");
    }

    @Override
    public void registerClient(RemoteClient client, int playerId) throws RemoteException {

    }

    @Override
    public void adminSetup(int playerId, int numberOfPlayers, GameMode mode) throws RemoteException {

    }

    @Override
    public void playerJoin(String username, Color color) throws RemoteException {

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

    @Override
    public void userAcceptPrizes(int playerId, boolean wantsRewards) throws RemoteException {

    }

    @Override
    public void userLandOnPlanet(int playerId, boolean isLanded, Planet planet) throws RemoteException {

    }

    @Override
    public void userLandOnStationOrShip(int playerId, boolean isLanded) throws RemoteException {

    }

    @Override
    public void userStopFlight(Player player) throws RemoteException {

    }

    @Override
    public void adminCalculateScore(Player player) throws RemoteException {

    }

    @Override
    public void userEndGame(Player player) throws RemoteException {

    }
}
