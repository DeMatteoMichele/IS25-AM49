package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

public class CommandServer {
    private final ServerController serverController;
    private String methodName;
    private final String typeOfMessage;
    private final ArrayList<Object> parameters;
    private int playerId;

    public CommandServer(String methodName, ServerController serverController, int playerId) {
        this.methodName = methodName;
        this.parameters = new ArrayList<>();
        this.serverController = serverController;
        this.playerId = playerId;
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

    public boolean callServerMethod() {
        assert serverController != null;
        switch (methodName) {
            case "registerClientRMI" -> serverController.registerClientRMI((RemoteClient) parameters.get(0));
            case "adminSetup" -> {
                try {
                    serverController.adminSetup((int) parameters.get(0), (int) parameters.get(1), (GameMode) parameters.get(2));
                } catch (Exception e) {
                    System.err.println("Errore in adminSetup: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "playerJoin" -> {
                try {
                    serverController.playerJoin((int) parameters.get(0), parameters.get(1).toString(), (Color) parameters.get(2));
                } catch (Exception e) {
                    System.err.println("Errore in playerJoin: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userDrawTile" -> serverController.userDrawTile((int) parameters.get(0), (int) parameters.get(1));
            case "userPlaceTile" ->
                    serverController.userPlaceTile((int) parameters.get(0), (Point) parameters.get(1), (Orientation) parameters.get(2));
            case "userPickTile" -> serverController.userPickTile((int) parameters.get(0), (Point) parameters.get(1));
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
                    serverController.userLandOnPlanet((int) parameters.get(0), (boolean) parameters.get(1), (int) parameters.get(2));
            case "userLandOnStationOrShip" ->
                    serverController.userLandOnStationOrShip((int) parameters.get(0), (boolean) parameters.get(1));
            case "userStopFlight" -> serverController.userStopFlight((int) parameters.get(0));
            case "adminCalculateScore" -> serverController.adminCalculateScore((int) parameters.get(0));
            case "userEndGame" -> serverController.userEndGame((int) parameters.get(0));
            default -> throw new IllegalArgumentException("Metodo non riconosciuto: " + methodName);
        }
        return true;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setPlayerId(int idPlayer) {
        this.playerId = idPlayer;
    }

    public String toString() {
        return typeOfMessage + " " + methodName + " " + playerId + " (" + parameters.toString() + ")";
    }
}