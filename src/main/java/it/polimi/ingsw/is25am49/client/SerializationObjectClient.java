package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.server.RemoteClient;
import it.polimi.ingsw.is25am49.client.model.adventurecards.Planet;
import it.polimi.ingsw.is25am49.client.model.enums.Color;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.enums.Orientation;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SerializationObjectClient {
    private final ClientController clientController;
    private final String methodName;
    private final String typeOfMessage;
    private final ArrayList<Object> parameters;
    private boolean isServer;

    public SerializationObjectClient(String methodName, ClientController clientController) {
        this.methodName = methodName;
        this.parameters = new ArrayList<>();
        this.clientController = clientController;
        this.isServer = false;
        if (methodName.equals("registerClientCallback") || methodName.equals("registerClientSocket")) {
            typeOfMessage = "HSK";
        } else {
            typeOfMessage = "MSG";
        }
    }

    public static String compactJsonString(String json) {
        StringBuilder compactedJsonString = new StringBuilder();
        int i;
        for (i = 0; i < json.length(); i++) {
            if (json.charAt(i) != '\n' && json.charAt(i) != '\t' && json.charAt(i) != ' ' && json.charAt(i) != '\r') {
                compactedJsonString.append(json.charAt(i));
            }
        }
        return compactedJsonString.toString();
    }

    public String getTypeOfMessage() {
        return typeOfMessage;
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

    public boolean callClientMethod() {
        if (!isServer) {
            assert clientController != null;
            switch (methodName) {
                case "notifyClient" -> clientController.notifyClient((String) parameters.get(0));
                case "registerClientCallback" -> {
                    try {
                        clientController.registerClientCallback((int) parameters.get(0));
                    } catch (Exception e) {
                        System.err.println("Errore durante registerClientCallback: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                case "playerJoinCallback" -> clientController.playerJoinCallback((int) parameters.get(0), (int) parameters.get(1));
                case "playerErrorJoinCallback" -> clientController.playerErrorJoinCallback((int) parameters.get(0), parameters.get(1).toString());
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

    public String toString() {
        return typeOfMessage + " " + methodName + " (" + parameters.toString() + ")";
    }
}