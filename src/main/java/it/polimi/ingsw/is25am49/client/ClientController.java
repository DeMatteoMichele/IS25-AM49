package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.enums.Color;
import it.polimi.ingsw.is25am49.client.model.states.*;
import it.polimi.ingsw.is25am49.server.RemoteClient;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.client.model.adventurecards.Planet;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.enums.Orientation;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ClientController {
    public int playerID;
    private GameSession gameSession;
    private RemoteServer serverMethods;
    private UserInterface ui;


    public ClientController(GameSession gameSession) {
        playerID = -1;  //non ancora assegnato
        this.gameSession = gameSession;
    }
    public ClientController(GameSession gameSession, UserInterface ui) {
        playerID = -1;  //non ancora assegnato
        this.gameSession = gameSession;
        this.ui = ui;
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

    public void registerClientCallback(int playerId)  {
        this.playerID = playerId;
        System.out.println("CONNESSIONE RIUSCTIA. IL TUO PLAYER ID È " + playerID);
    }

    public void playerJoinCallback(int playerId, int seedForGeneration)  {

    }

    public void playerErrorJoinCallback(int playerId, String typeOfError)  {

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
        State currentState;
        if (state.equals("InitState")) {
            currentState = new InitState(gameSession,ui);
        }
        else if(state.equals("BuildState")) {
            currentState = new BuildState(gameSession,ui);
        }
        else if(state.equals("AdventureState")) {
            currentState = new AdventureState(gameSession,ui);
        }
        else if(state.equals("FinishScoreState")) {
            currentState = new FinishScoreState(gameSession,ui);
        }
        else {
            throw new IllegalArgumentException("Invalid state: " + state);
        }
    }




    public void serverTestConnection(int playerId)  {

    }

    public void registerClientRMI(RemoteClient client)  {

    }

    public void adminSetup(int playerId, int numberOfPlayers, GameMode mode)  {

    }

    public void playerJoin(String username)  {
        try {
            serverMethods.playerJoin(playerID, username, Color.values()[playerID]);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void userDrawTile(int playerId, int tileId)  {

    }

    public void userPlaceTile(int playerId, Point coordinates, Orientation orientation)  {

    }

    public void userPickTile(int playerId, Point coordinates)  {

    }

    public void userDiscardTile(int playerId)  {

    }

    public void userFinishShipboard(int playerId)  {

    }

    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex)  {

    }

    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex)  {

    }

    public void userPlaceTimer(int playerId)  {

    }

    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates)  {

    }

    public void userThrowDices(int playerId)  {

    }

    public void userDrawAdventureCard(int playerId)  {

    }

    public void userFinishDecisionActivating(int playerId, HashSet<Point> coordinates)  {

    }

    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens)  {

    }

    public void userAcceptPrizes(int playerId, boolean wantsRewards)  {

    }

    public void userLandOnPlanet(int playerId, boolean isLanded, Planet planet)  {

    }

    public void userLandOnStationOrShip(int playerId, boolean isLanded)  {

    }

    public void userStopFlight(int playerId)  {

    }

    public void adminCalculateScore(int playerId)  {

    }

    public void userEndGame(int playerId)  {

    }

    public int getPlayerID(){
        return playerID;
    }
}