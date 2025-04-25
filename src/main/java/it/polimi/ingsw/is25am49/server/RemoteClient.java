package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.ConcurrentException;
import it.polimi.ingsw.is25am49.server.model.states.State;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Set;

public interface RemoteClient extends Remote {
    // void notifyClient(String msg) throws RemoteException;

    // Metodi BuildState !!!!!!!!! ZIOPORCO
    void userDrawTileCallback(int playerId, int tileId) throws RemoteException;
    void userErrorDrawTileCallback(int playerId, int tileId) throws RemoteException;
    void userPlaceTileCallback(int playerId, Point coordinates, Orientation orientation) throws RemoteException;
    void userPickTileCallback(int playerId, Point coordinates) throws RemoteException;
    void userDiscardTileCallback(int playerId) throws RemoteException;
    void userFinishShipboardCallback(int playerId) throws RemoteException;
    void userLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) throws RemoteException;
    void userErrorLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) throws RemoteException;
    void userStopLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) throws RemoteException;
    void userPlaceTimerCallback(int playerId) throws RemoteException;
    void userErrorPlaceTimerCallback(int playerId) throws RemoteException;
    void userSelectTypeOfTokenCallback(int playerId, Token token, Point coordinates) throws RemoteException;

    //Metodi AdventureState !!!!!!!!!! ZIOPORCO
    void userThrowDicesCallback(int playerId) throws RemoteException;
    void userDrawAdventureCardCallback(int playerId) throws RemoteException;
    void userFinishDecisionActivatingCallback(int playerId, Set<Point> coordinates) throws RemoteException;
    void userFinishDecisionTokenCallback(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) throws RemoteException;




    void changeState(String state) throws RemoteException;
}
