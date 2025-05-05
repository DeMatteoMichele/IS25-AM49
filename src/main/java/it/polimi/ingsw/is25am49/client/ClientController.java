package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.server.RemoteClient;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.adventurecards.Planet;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Set;

public class ClientController {
    public int playerID;
    private GameSession gameSession;
    private RemoteServer serverMethods;

    public ClientController(GameSession gameSession) {
        playerID = -1;  //non ancora assegnato
    }

    public ClientController(int connectionType){
        //this.playerID = playerID;//TODO playerID viene assegnato dal server in qualche modo

    }

    public void clientLogin(){
        //todo playerID= setto l'id con il register client assegnato dal server switch con i due tipi di connessione
    }

    public void registerClientSocket(){
        try {
            serverMethods.registerClientSocket(playerID);
        } catch (RemoteException e) {
            System.out.println("registerClientSocket failed --> " + e.getMessage());
        }
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
    public RemoteServer getServerMethods() {
        return serverMethods;
    }

    public void notifyClient(String msg) {

    }

    public void registerClientCallback(RemoteClient client, int playerId)  {

    }

    public void playerJoinCallback(int playerId)  {

    }

    public void gameStartedCallback()  {

    }

    public void userDrawTileCallback(int playerId, int tileId)  {

    }

    public void userErrorDrawTileCallback(int playerId, int tileId)  {

    }

    public void userPlaceTileCallback(int playerId, Point coordinates, Orientation orientation)  {

    }

    public void userPickTileCallback(int playerId, Point coordinates)  {

    }

    public void userDiscardTileCallback(int playerId)  {

    }

    public void userFinishShipboardCallback(int playerId)  {

    }

    public void userLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex)  {

    }

    public void userErrorLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex)  {

    }

    public void userStopLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex)  {

    }

    public void userPlaceTimerCallback(int playerId)  {

    }

    public void userErrorPlaceTimerCallback(int playerId)  {

    }

    public void userSelectTypeOfTokenCallback(int playerId, Token token, Point coordinates)  {

    }

    public void userThrowDicesCallback(int playerId, int result)  {

    }

    public void userDrawAdventureCardCallback(int playerId)  {

    }

    public void userFinishDecisionActivatingCallback(int playerId, Set<Point> coordinates)  {

    }

    public void userFinishDecisionTokenCallback(int playerId, ArrayList<Pair<Point, Token>> listOfTokens)  {

    }

    public void userAcceptPrizesCallback(int playerId)  {

    }

    public void userLandOnPlanetCallback(int playerId, boolean isLanded, Planet planet)  {

    }

    public void userLandOnStationOrShipCallback(int playerId, boolean isLanded)  {

    }

    public void userStopFlightCallback(int playerId)  {

    }

    public void adminCalculateScoreCallback(int playerId)  {

    }

    public void userEndGameCallback(int playerId)  {

    }

    public void changeState(String state)  {

    }
}
