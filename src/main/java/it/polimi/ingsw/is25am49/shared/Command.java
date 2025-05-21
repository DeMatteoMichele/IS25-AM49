package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.client.model.adventurecards.Planet;
import it.polimi.ingsw.is25am49.server.RemoteClient;
import it.polimi.ingsw.is25am49.server.ServerController;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Command {
    private ClientController clientController;
    private ServerController serverController;
    private final String methodName;
    private final String typeOfMessage;
    private final ArrayList<Object> parameters;

    public Command(String methodName, ClientController clientController) {
        this.methodName = methodName;
        this.parameters = new ArrayList<>();
        this.clientController = clientController;
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

    public boolean callMethod() {
        switch (methodName) {
            case "registerClientRMI" -> serverController.registerClientRMI((RemoteClient) parameters.get(0));
            case "adminSetup" -> {
                try {
                    serverController.adminSetup((int) parameters.get(0), (int) parameters.get(1), (it.polimi.ingsw.is25am49.server.model.enums.GameMode) parameters.get(2));
                } catch (Exception e) {
                    System.err.println("Errore in adminSetup: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "playerJoin" -> {
                try {
                    serverController.playerJoin((int) parameters.get(0), parameters.get(1).toString());
                } catch (Exception e) {
                    System.err.println("Errore in playerJoin: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userDrawTile" -> {
                try {
                    serverController.userDrawTile((int) parameters.get(0), (int) parameters.get(1));
                } catch (Exception e) {
                    System.err.println("Errore in userDrawTile: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userPlaceTile" -> {
                try {
                    serverController.userPlaceTile((int) parameters.get(0), (Point) parameters.get(1), (it.polimi.ingsw.is25am49.server.model.enums.Orientation) parameters.get(2));
                } catch (Exception e) {
                    System.err.println("Errore in userPlaceTile: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userPickTile" -> {
                try {
                    serverController.userPickTile((int) parameters.get(0), (Point) parameters.get(1));
                } catch (Exception e) {
                    System.err.println("Errore in userPickTile: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userDiscardTile" -> {
                try {
                    serverController.userDiscardTile((int) parameters.get(0));
                } catch (Exception e) {
                    System.err.println("Errore in userDiscardTile: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userFinishShipboard" -> {
                try {
                    serverController.userFinishShipboard((int) parameters.get(0));
                } catch (Exception e) {
                    System.err.println("Errore in userFinishShipboard: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userLookSmallAdventureCardDeck" -> {
                try {
                    serverController.userLookSmallAdventureCardDeck((int) parameters.get(0), (int) parameters.get(1));
                } catch (Exception e) {
                    System.err.println("Errore in userLookSmallAdventureCardDeck: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userStopLookSmallAdventureCardDeck" -> {
                try {
                    serverController.userStopLookSmallAdventureCardDeck((int) parameters.get(0), (int) parameters.get(1));
                } catch (Exception e) {
                    System.err.println("Errore in userStopLookSmallAdventureCardDeck: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userPlaceTimer" -> {
                try {
                    serverController.userPlaceTimer((int) parameters.get(0));
                } catch (Exception e) {
                    System.err.println("Errore in userPlaceTimer: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userSelectTypeOfToken" -> {
                try {
                    serverController.userSelectTypeOfToken((int) parameters.get(0), (it.polimi.ingsw.is25am49.server.model.enums.Token) parameters.get(1), (Point) parameters.get(2));
                } catch (Exception e) {
                    System.err.println("Errore in userSelectTypeOfToken: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userThrowDices" -> {
                try {
                    serverController.userThrowDices((int) parameters.get(0));
                } catch (Exception e) {
                    System.err.println("Errore in userThrowDices: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userDrawAdventureCard" -> {
                try {
                    serverController.userDrawAdventureCard((int) parameters.get(0));
                } catch (Exception e) {
                    System.err.println("Errore in userDrawAdventureCard: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userFinishDecisionActivating" -> {
                try {
                    serverController.userFinishDecisionActivating((int) parameters.get(0), (HashSet<Point>) parameters.get(1));
                } catch (Exception e) {
                    System.err.println("Errore in userFinishDecisionActivating: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userFinishDecisionToken" -> {
                try {
                    serverController.userFinishDecisionToken((int) parameters.get(0), (ArrayList<Pair<Point, it.polimi.ingsw.is25am49.server.model.enums.Token>>) parameters.get(1));
                } catch (Exception e) {
                    System.err.println("Errore in userFinishDecisionToken: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userAcceptPrizes" -> {
                try {
                    serverController.userAcceptPrizes((int) parameters.get(0), (boolean) parameters.get(1));
                } catch (Exception e) {
                    System.err.println("Errore in userAcceptPrizes: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userLandOnPlanet" -> {
                try {
                    serverController.userLandOnPlanet((int) parameters.get(0), (boolean) parameters.get(1), (int) parameters.get(2));
                } catch (Exception e) {
                    System.err.println("Errore in userLandOnPlanet: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userLandOnStationOrShip" -> {
                try {
                    serverController.userLandOnStationOrShip((int) parameters.get(0), (boolean) parameters.get(1));
                } catch (Exception e) {
                    System.err.println("Errore in userLandOnStationOrShip: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userStopFlight" -> {
                try {
                    serverController.userStopFlight((int) parameters.get(0));
                } catch (Exception e) {
                    System.err.println("Errore in userStopFlight: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userQuitGame" -> {
                try {
                    serverController.userQuitGame((int) parameters.get(0));
                } catch (Exception e) {
                    System.err.println("Errore in userQuitGame: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "adminCalculateScore" -> {
                try {
                    serverController.adminCalculateScore((int) parameters.get(0));
                } catch (Exception e) {
                    System.err.println("Errore in adminCalculateScore: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "adminEndGame" -> {
                try {
                    serverController.adminEndGame((int) parameters.get(0));
                } catch (Exception e) {
                    System.err.println("Errore in adminEndGame: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "errorCallback" -> {
                try {
                    clientController.errorCallback((int) parameters.get(0), (String) parameters.get(1));
                } catch (Exception e) {
                    System.err.println("Errore in errorCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "registerClientCallback" -> {
                try {
                    clientController.registerClientCallback((int) parameters.get(0));
                } catch (Exception e) {
                    System.err.println("Errore durante registerClientCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "adminSetupCallback" -> {
                try {
                    clientController.adminSetupCallback((int) parameters.get(0), (int) parameters.get(1), (it.polimi.ingsw.is25am49.client.model.enums.GameMode) parameters.get(2));
                } catch (Exception e) {
                    System.err.println("Errore in adminSetupCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "playerJoinCallback" -> {
                try {
                    clientController.playerJoinCallback((int) parameters.get(0), (String) parameters.get(1), (int) parameters.get(2));
                } catch (Exception e) {
                    System.err.println("Errore in playerJoinCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userDrawTileCallback" -> {
                try {
                    clientController.userDrawTileCallback((int) parameters.get(0), (int) parameters.get(1));
                } catch (Exception e) {
                    System.err.println("Errore in userDrawTileCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userPlaceTileCallback" -> {
                try {
                    clientController.userPlaceTileCallback((int) parameters.get(0), (Point) parameters.get(1), (it.polimi.ingsw.is25am49.client.model.enums.Orientation) parameters.get(2));
                } catch (Exception e) {
                    System.err.println("Errore in userPlaceTileCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userPickTileCallback" -> {
                try {
                    clientController.userPickTileCallback((int) parameters.get(0), (Point) parameters.get(1));
                } catch (Exception e) {
                    System.err.println("Errore in userPickTileCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userDiscardTileCallback" -> {
                try {
                    clientController.userDiscardTileCallback((int) parameters.get(0));
                } catch (Exception e) {
                    System.err.println("Errore in userDiscardTileCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userFinishShipboardCallback" -> {
                try {
                    clientController.userFinishShipboardCallback((int) parameters.get(0));
                } catch (Exception e) {
                    System.err.println("Errore in userFinishShipboardCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userLookSmallAdventureCardDeckCallback" -> {
                try {
                    clientController.userLookSmallAdventureCardDeckCallback((int) parameters.get(0), (int) parameters.get(1));
                } catch (Exception e) {
                    System.err.println("Errore in userLookSmallAdventureCardDeckCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userStopLookSmallAdventureCardDeckCallback" -> {
                try {
                    clientController.userStopLookSmallAdventureCardDeckCallback((int) parameters.get(0), (int) parameters.get(1));
                } catch (Exception e) {
                    System.err.println("Errore in userStopLookSmallAdventureCardDeckCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userPlaceTimerCallback" -> {
                try {
                    clientController.userPlaceTimerCallback((int) parameters.get(0));
                } catch (Exception e) {
                    System.err.println("Errore in userPlaceTimerCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userSelectTypeOfTokenCallback" -> {
                try {
                    clientController.userSelectTypeOfTokenCallback((int) parameters.get(0), (it.polimi.ingsw.is25am49.client.model.enums.Token) parameters.get(1), (Point) parameters.get(2));
                } catch (Exception e) {
                    System.err.println("Errore in userSelectTypeOfTokenCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userThrowDicesCallback" -> {
                try {
                    clientController.userThrowDicesCallback((int) parameters.get(0), (int) parameters.get(1));
                } catch (Exception e) {
                    System.err.println("Errore in userThrowDicesCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userDrawAdventureCardCallback" -> {
                try {
                    clientController.userDrawAdventureCardCallback((int) parameters.get(0));
                } catch (Exception e) {
                    System.err.println("Errore in userDrawAdventureCardCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userFinishDecisionActivatingCallback" -> {
                try {
                    clientController.userFinishDecisionActivatingCallback((int) parameters.get(0), (Set<Point>) parameters.get(1));
                } catch (Exception e) {
                    System.err.println("Errore in userFinishDecisionActivatingCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userFinishDecisionTokenCallback" -> {
                try {
                    clientController.userFinishDecisionTokenCallback((int) parameters.get(0), (ArrayList<Pair<Point, it.polimi.ingsw.is25am49.client.model.enums.Token>>) parameters.get(1));
                } catch (Exception e) {
                    System.err.println("Errore in userFinishDecisionTokenCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userAcceptPrizesCallback" -> {
                try {
                    clientController.userAcceptPrizesCallback((int) parameters.get(0),(boolean) parameters.get(1) );
                } catch (Exception e) {
                    System.err.println("Errore in userAcceptPrizesCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userLandOnPlanetCallback" -> {
                try {
                    clientController.userLandOnPlanetCallback((int) parameters.get(0), (boolean) parameters.get(1), (int) parameters.get(2));
                } catch (Exception e) {
                    System.err.println("Errore in userLandOnPlanetCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userLandOnStationOrShipCallback" -> {
                try {
                    clientController.userLandOnStationOrShipCallback((int) parameters.get(0), (boolean) parameters.get(1));
                } catch (Exception e) {
                    System.err.println("Errore in userLandOnStationOrShipCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "userStopFlightCallback" -> {
                try {
                    clientController.userStopFlightCallback((int) parameters.get(0));
                } catch (Exception e) {
                    System.err.println("Errore in userStopFlightCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "adminCalculateScoreCallback" -> {
                try {
                    clientController.adminCalculateScoreCallback((int) parameters.get(0));
                } catch (Exception e) {
                    System.err.println("Errore in adminCalculateScoreCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "adminEndGameCallback" -> {
                try {
                    clientController.adminEndGameCallback((int) parameters.get(0));
                } catch (Exception e) {
                    System.err.println("Errore in adminEndGameCallback: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            case "changeState" -> {
                try {
                    clientController.changeState((String) parameters.get(0));
                } catch (Exception e) {
                    System.err.println("Errore in changeState: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            default -> throw new IllegalArgumentException("Metodo non riconosciuto: " + methodName);
        }
        return true;
    }

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }

    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }

    public String toString() {
        return typeOfMessage + " " + methodName + " (" + parameters.toString() + ")";
    }
}