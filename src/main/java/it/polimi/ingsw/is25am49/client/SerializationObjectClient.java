package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.server.RemoteClient;
import it.polimi.ingsw.is25am49.server.ServerController;
import it.polimi.ingsw.is25am49.server.model.adventurecards.Planet;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SerializationObjectClient {
    private final ServerController serverController;
    private final ClientController clientController;
    private final String methodName;
    private final ArrayList<Object> parameters;
    private boolean isServer;

    public SerializationObjectClient(String methodName, ServerController serverController) {
        this.methodName = methodName;
        this.parameters = new ArrayList<>();
        this.serverController = serverController;
        this.clientController = null;
        this.isServer = true;
    }

    public SerializationObjectClient(String methodName, ClientController clientController) {
        this.methodName = methodName;
        this.parameters = new ArrayList<>();
        this.clientController = clientController;
        this.serverController = null;
        this.isServer = false;
    }

    public boolean isThisOnServer() {
        return isServer;
    }

    public void addParameter(Object parameter) {
        this.parameters.add(parameter);
    }

    public ArrayList<Object> getParameters() {
        return parameters;
    }

    public String getMethodName() {
        return methodName;
    }

    public int sizeParameters() {
        return parameters.size();
    }

    public boolean callServerMethod() {
        if (isServer) {
            assert serverController != null;
            switch (methodName) {
                case "serverTestConnection" -> serverController.serverTestConnection((int) parameters.get(0));
                case "registerClientRMI" -> serverController.registerClientRMI((RemoteClient) parameters.get(0));
                case "registerClientSocket" -> serverController.registerClientSocket((int) parameters.get(0));
                case "adminSetup" ->
                        serverController.adminSetup((int) parameters.get(0), (int) parameters.get(1), (GameMode) parameters.get(2));
                case "playerJoin" ->
                        serverController.playerJoin((int) parameters.get(0), parameters.get(1).toString(), (Color) parameters.get(2));
                case "userDrawTile" -> serverController.userDrawTile((int) parameters.get(0), (int) parameters.get(1));
                case "userPlaceTile" ->
                        serverController.userPlaceTile((int) parameters.get(0), (Point) parameters.get(1), (Orientation) parameters.get(2));
                case "userPickTile" ->
                        serverController.userPickTile((int) parameters.get(0), (Point) parameters.get(1));
                case "userDiscardTile" -> serverController.userDiscardTile((int) parameters.get(0));
                case "userFinishShipboard" -> serverController.userFinishShipboard((int) parameters.get(0));
                case "userLookSmallAdventureCardDeck" ->
                        serverController.userLookSmallAdventureCardDeck((int) parameters.get(0), (int) parameters.get(1));
                case "userStopLookSmallAdventureCardDeck" ->
                        serverController.userStopLookSmallAdventureCardDeck((int) parameters.get(0), (int) parameters.get(1));
                case "userPlaceTimer" -> serverController.userPlaceTimer((int) parameters.get(0));
                case "userSelectTypeOfToken" ->
                        serverController.userSelectTypeOfToken((int) parameters.get(0), (Token) parameters.get(1), (Point) parameters.get(2));
                case "userThrowDices" -> serverController.userThrowDices((int) parameters.get(0));
                case "userDrawAdventureCard" -> serverController.userDrawAdventureCard((int) parameters.get(0));
                case "userFinishDecisionActivating" ->
                        serverController.userFinishDecisionActivating((int) parameters.get(0), (HashSet<Point>) parameters.get(1));
                case "userFinishDecisionToken" ->
                        serverController.userFinishDecisionToken((int) parameters.get(0), (ArrayList<Pair<Point, Token>>) parameters.get(1));
                case "userAcceptPrizes" ->
                        serverController.userAcceptPrizes((int) parameters.get(0), (boolean) parameters.get(1));
                case "userLandOnPlanet" ->
                        serverController.userLandOnPlanet((int) parameters.get(0), (boolean) parameters.get(1), (Planet) parameters.get(2));
                case "userLandOnStationOrShip" ->
                        serverController.userLandOnStationOrShip((int) parameters.get(0), (boolean) parameters.get(1));
                case "userStopFlight" -> serverController.userStopFlight((int) parameters.get(0));
                case "adminCalculateScore" -> serverController.adminCalculateScore((int) parameters.get(0));
                case "userEndGame" -> serverController.userEndGame((int) parameters.get(0));
                default -> throw new IllegalArgumentException("Metodo non riconosciuto: " + methodName);
            }
            return true;
        } else {
            return false;
        }
    }
    public boolean callClientMethod() {
        if (!isServer) {
            assert clientController != null;
            switch (methodName) {
                case "notifyClient" -> clientController.notifyClient((String) parameters.get(0));
                case "registerClientCallback" ->
                        clientController.registerClientCallback((RemoteClient) parameters.get(0), (int) parameters.get(1));
                case "playerJoinCallback" -> clientController.playerJoinCallback((int) parameters.get(0));
                case "gameStartedCallback" -> clientController.gameStartedCallback();
                case "userDrawTileCallback" ->
                        clientController.userDrawTileCallback((int) parameters.get(0), (int) parameters.get(1));
                case "userErrorDrawTileCallback" ->
                        clientController.userErrorDrawTileCallback((int) parameters.get(0), (int) parameters.get(1));
                case "userPlaceTileCallback" ->
                        clientController.userPlaceTileCallback((int) parameters.get(0), (Point) parameters.get(1), (Orientation) parameters.get(2));
                case "userPickTileCallback" ->
                        clientController.userPickTileCallback((int) parameters.get(0), (Point) parameters.get(1));
                case "userDiscardTileCallback" -> clientController.userDiscardTileCallback((int) parameters.get(0));
                case "userFinishShipboardCallback" ->
                        clientController.userFinishShipboardCallback((int) parameters.get(0));
                case "userLookSmallAdventureCardDeckCallback" ->
                        clientController.userLookSmallAdventureCardDeckCallback((int) parameters.get(0), (int) parameters.get(1));
                case "userErrorLookSmallAdventureCardDeckCallback" ->
                        clientController.userErrorLookSmallAdventureCardDeckCallback((int) parameters.get(0), (int) parameters.get(1));
                case "userStopLookSmallAdventureCardDeckCallback" ->
                        clientController.userStopLookSmallAdventureCardDeckCallback((int) parameters.get(0), (int) parameters.get(1));
                case "userPlaceTimerCallback" -> clientController.userPlaceTimerCallback((int) parameters.get(0));
                case "userErrorPlaceTimerCallback" ->
                        clientController.userErrorPlaceTimerCallback((int) parameters.get(0));
                case "userSelectTypeOfTokenCallback" ->
                        clientController.userSelectTypeOfTokenCallback((int) parameters.get(0), (Token) parameters.get(1), (Point) parameters.get(2));
                case "userThrowDicesCallback" ->
                        clientController.userThrowDicesCallback((int) parameters.get(0), (int) parameters.get(1));
                case "userDrawAdventureCardCallback" ->
                        clientController.userDrawAdventureCardCallback((int) parameters.get(0));
                case "userFinishDecisionActivatingCallback" ->
                        clientController.userFinishDecisionActivatingCallback((int) parameters.get(0), (Set<Point>) parameters.get(1));
                case "userFinishDecisionTokenCallback" ->
                        clientController.userFinishDecisionTokenCallback((int) parameters.get(0), (ArrayList<Pair<Point, Token>>) parameters.get(1));
                case "userAcceptPrizesCallback" -> clientController.userAcceptPrizesCallback((int) parameters.get(0));
                case "userLandOnPlanetCallback" ->
                        clientController.userLandOnPlanetCallback((int) parameters.get(0), (boolean) parameters.get(1), (Planet) parameters.get(2));
                case "userLandOnStationOrShipCallback" ->
                        clientController.userLandOnStationOrShipCallback((int) parameters.get(0), (boolean) parameters.get(1));
                case "userStopFlightCallback" -> clientController.userStopFlightCallback((int) parameters.get(0));
                case "adminCalculateScoreCallback" ->
                        clientController.adminCalculateScoreCallback((int) parameters.get(0));
                case "userEndGameCallback" -> clientController.userEndGameCallback((int) parameters.get(0));
                case "changeState" -> clientController.changeState((String) parameters.get(0));
                default -> throw new IllegalArgumentException("Metodo non riconosciuto: " + methodName);
            }
            return true;
        } else {
            return false;
        }
    }
}