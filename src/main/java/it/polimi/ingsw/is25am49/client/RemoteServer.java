package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.server.RemoteClient;
import it.polimi.ingsw.is25am49.server.model.adventurecards.Planet;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Set;

public interface RemoteServer extends Remote {
    void serverTestConnection(int playerId) throws RemoteException;
    Integer registerClientRMI(RemoteClient client) throws RemoteException;
    // Metodi InitState !!!!!!!!! ZIOPORCO

    void adminSetup(int playerId, int numberOfPlayers, GameMode mode) throws RemoteException;
    void playerJoin(int playerId, String username, Color color) throws RemoteException;



    // Metodi BuildState !!!!!!!!! ZIOPORCO

    void userDrawTile(int playerId, int tileId) throws RemoteException;
    void userPlaceTile(int playerId, Point coordinates, Orientation orientation) throws RemoteException;
    void userPickTile(int playerId, Point coordinates) throws RemoteException;
    void userDiscardTile(int playerId) throws RemoteException;
    void userFinishShipboard(int playerId) throws RemoteException;
    void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) throws RemoteException;
    void userStopLookSmallAdventureCardDeck(int playerId,int smallDeckIndex) throws RemoteException;
    void userPlaceTimer(int playerId) throws RemoteException;
    void userSelectTypeOfToken(int playerId, Token token, Point coordinates) throws RemoteException;

    //Metodi AdventureState !!!!!!!!!! ZIOPORCO

    void userThrowDices(int playerId) throws RemoteException;
    void userDrawAdventureCard(int playerId) throws RemoteException;
    void userFinishDecisionActivating(int playerId, Set<Point> coordinates) throws RemoteException;
    void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) throws RemoteException;
    void userAcceptPrizes(int playerId,boolean wantsRewards) throws RemoteException;
    void userLandOnPlanet(int playerId, boolean isLanded, Planet planet) throws RemoteException;
    void userLandOnStationOrShip(int playerId, boolean isLanded) throws RemoteException;
    void userStopFlight(int playerId) throws RemoteException;

    //Metodi FinishScoreState !!!!!!!!!! ZIOPORCO
    void adminCalculateScore(int playerId) throws RemoteException;
    void userEndGame(int playerId) throws RemoteException;
}