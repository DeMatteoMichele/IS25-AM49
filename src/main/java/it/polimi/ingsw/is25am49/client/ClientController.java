package it.polimi.ingsw.is25am49.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.enums.Color;
import it.polimi.ingsw.is25am49.client.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.client.model.states.*;
import it.polimi.ingsw.is25am49.client.model.adventurecards.Planet;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.enums.Orientation;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ClientController {
    public int playerId;
    private GameSession gameSession;
    private RemoteServer serverMethods;
    private ServerHandler serverHandler;
    private UserInterface ui;
    private String username;

    private String error= "⚠ Error : ";
    /**
     * Solo per i test
     */
    public ClientController(GameSession gameSession) {
        playerId = -1;  //non ancora assegnato
        this.gameSession = gameSession;
    }

    public ClientController(GameSession gameSession, UserInterface ui) {
        playerId = -1;  //non ancora assegnato
        this.gameSession = gameSession;
        this.ui = ui;
    }

    public void userPlaceTile() {
        //metodo chiamato dal callback, che aggiorna il model lato client
        //clientModel.placeTile
    }

    public void setServerMethods(RemoteServer serverMethods) {
        this.serverMethods = serverMethods;
    }

    public void setServerHandler(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public RemoteServer getServerMethods() {
        return serverMethods;
    }

    public void errorCallback(int playerId, String errorMessage) {
        ui.printUpdateMessage(error + errorMessage);
    }

    public void registerClientCallback(int playerId) {
        this.playerId = playerId;
        ui.setClientId(playerId);
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("playerId", playerId);
            File idFile = new File("$IdClient"+username+".json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(idFile, rootNode);
        } catch (IOException e) {
            ui.printUpdateMessage("Errore durante il salvataggio dell'ID: " + e.getMessage());
        }
    }

    public void adminSetupCallback(int playerId, int numberOfPlayers, GameMode mode) {
        try {
            gameSession.getState().adminCreateGame(playerId, mode, numberOfPlayers);
        } catch (Exception e) {
            ui.printUpdateMessage(error+ "Errore durante la configurazione del gioco --> " + e.getMessage());
        }
    }

    public void playerJoinCallback(int playerId, String username, int seedForGeneration) {
        gameSession.getState().userJoinLobby(Color.values()[playerId], username, playerId);
        gameSession.setSeedForGeneration(seedForGeneration);
    }

    public void gameStartedCallback() {

    }

    public void userDrawTileCallback(int playerId, int tileId) {

    }

    public void userPlaceTileCallback(int playerId, Point coordinates, Orientation orientation) {

    }

    public void userPickTileCallback(int playerId, Point coordinates) {

    }

    public void userDiscardTileCallback(int playerId) {

    }

    public void userFinishShipboardCallback(int playerId) {

    }

    public void userLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) {

    }

    public void userStopLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) {

    }

    public void userPlaceTimerCallback(int playerId) {

    }

    public void userSelectTypeOfTokenCallback(int playerId, Token token, Point coordinates) {

    }

    public void userThrowDicesCallback(int playerId, int result) {

    }

    public void userDrawAdventureCardCallback(int playerId) {

    }

    public void userFinishDecisionActivatingCallback(int playerId, Set<Point> coordinates) {

    }

    public void userFinishDecisionTokenCallback(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {

    }

    public void userAcceptPrizesCallback(int playerId) {

    }

    public void userLandOnPlanetCallback(int playerId, boolean isLanded, Planet planet) {

    }

    public void userLandOnStationOrShipCallback(int playerId, boolean isLanded) {

    }

    public void userStopFlightCallback(int playerId) {

    }

    public void adminCalculateScoreCallback(int playerId) {

    }

    public void userEndGameCallback(int playerId) {

    }

    public void changeState(String state) {
        State currentState;
        if (state.equals("InitState")) {
            currentState = new InitState(gameSession, ui);
        } else if (state.equals("BuildState")) {
            currentState = new BuildState(gameSession, ui);
        } else if (state.equals("AdventureState")) {
            currentState = new AdventureState(gameSession, ui);
        } else if (state.equals("FinishScoreState")) {
            currentState = new FinishScoreState(gameSession, ui);
        } else {
            throw new IllegalArgumentException("Invalid state: " + state);
        }
    }

    public void registerClientRMI(RemoteServer client) throws RemoteException {

    }

    /**
     * This method is used to register the client socket on the server.
     * It creates a temporary file with the playerId and puts it in the outgoing queue.
     * @throws RemoteException if there is an error during the remote call
     */
    public void registerClientSocket() throws RemoteException {
        Command command = new Command("registerClientSocket", this);
        try {
            ObjectMapper mapper = new ObjectMapper();
            File idFile = new File("$IdClient"+username+".json");
            playerId = mapper.readTree(idFile).get("playerId").asInt();
        } catch (IOException e) {
            System.out.println("Errore durante la lettura dell'ID: " + e.getMessage());
        }
        command.addParameter(playerId);
        serverMethods.command_FromClientToServer(command);
        serverHandler.connectCommand();
    }


    public void adminSetup(int numberOfPlayers, GameMode mode) {
        try {
            gameSession.getState().checkAdminCreateGame(playerId);
            Command command = new Command("adminSetup", this);
            command.addParameter(playerId);
            command.addParameter(numberOfPlayers);
            command.addParameter(mode);
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException | GameSettingsException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void playerJoin(String username) {
        try {
            gameSession.getState().checkUserJoinLobby(playerId);
            Command command = new Command("playerJoin", this);
            command.addParameter(playerId);
            command.addParameter(username);
            command.addParameter(Color.values()[playerId]);
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException | GameSettingsException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void userDrawTile(int playerId, int tileId) throws RemoteException {
        Command command = new Command("userDrawTile", this);
        command.addParameter(playerId);
        command.addParameter(tileId);
        serverMethods.command_FromClientToServer(command);
    }

    public void userPlaceTile(int playerId, Point coordinates, Orientation orientation) throws RemoteException {
        Command command = new Command("userPlaceTile", this);
        command.addParameter(playerId);
        command.addParameter(coordinates);
        command.addParameter(orientation);
        serverMethods.command_FromClientToServer(command);
    }

    public void userPickTile(int playerId, Point coordinates) throws RemoteException {
        Command command = new Command("userPickTile", this);
        command.addParameter(playerId);
        command.addParameter(coordinates);
        serverMethods.command_FromClientToServer(command);
    }

    public void userDiscardTile(int playerId) throws RemoteException {
        Command command = new Command("userDiscardTile", this);
        command.addParameter(playerId);
        serverMethods.command_FromClientToServer(command);
    }

    public void userFinishShipboard(int playerId) throws RemoteException {
        Command command = new Command("userFinishShipboard", this);
        command.addParameter(playerId);
        serverMethods.command_FromClientToServer(command);
    }

    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) throws RemoteException {
        Command command = new Command("userLookSmallAdventureCardDeck", this);
        command.addParameter(playerId);
        command.addParameter(smallDeckIndex);
        serverMethods.command_FromClientToServer(command);
    }

    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) throws RemoteException {
        Command command = new Command("userStopLookSmallAdventureCardDeck", this);
        command.addParameter(playerId);
        command.addParameter(smallDeckIndex);
        serverMethods.command_FromClientToServer(command);
    }

    public void userPlaceTimer(int playerId) throws RemoteException {
        Command command = new Command("userPlaceTimer", this);
        command.addParameter(playerId);
        serverMethods.command_FromClientToServer(command);
    }

    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) throws RemoteException {
        Command command = new Command("userSelectTypeOfToken", this);
        command.addParameter(playerId);
        command.addParameter(token);
        command.addParameter(coordinates);
        serverMethods.command_FromClientToServer(command);
    }

    public void userThrowDices(int playerId) throws RemoteException {
        Command command = new Command("userThrowDices", this);
        command.addParameter(playerId);
        serverMethods.command_FromClientToServer(command);
    }

    public void userDrawAdventureCard(int playerId) throws RemoteException {
        Command command = new Command("userDrawAdventureCard", this);
        command.addParameter(playerId);
        serverMethods.command_FromClientToServer(command);
    }

    public void userFinishDecisionActivating(int playerId, HashSet<Point> coordinates) throws RemoteException {
        Command command = new Command("userFinishDecisionActivating", this);
        command.addParameter(playerId);
        command.addParameter(coordinates);
        serverMethods.command_FromClientToServer(command);
    }

    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) throws RemoteException {
        Command command = new Command("userFinishDecisionToken", this);
        command.addParameter(playerId);
        command.addParameter(listOfTokens);
        serverMethods.command_FromClientToServer(command);
    }

    public void userAcceptPrizes(int playerId, boolean wantsRewards) throws RemoteException {
        Command command = new Command("userAcceptPrizes", this);
        command.addParameter(playerId);
        command.addParameter(wantsRewards);
        serverMethods.command_FromClientToServer(command);
    }

    public void userLandOnPlanet(int playerId, boolean isLanded, Planet planet) throws RemoteException {
        Command command = new Command("userLandOnPlanet", this);
        command.addParameter(playerId);
        command.addParameter(isLanded);
        command.addParameter(planet);
        serverMethods.command_FromClientToServer(command);
    }

    public void userLandOnStationOrShip(int playerId, boolean isLanded) throws RemoteException {
        Command command = new Command("userLandOnStationOrShip", this);
        command.addParameter(playerId);
        command.addParameter(isLanded);
        serverMethods.command_FromClientToServer(command);
    }

    public void userStopFlight(int playerId) throws RemoteException {
        Command command = new Command("userStopFlight", this);
        command.addParameter(playerId);
        serverMethods.command_FromClientToServer(command);
    }

    public void adminCalculateScore(int playerId) throws RemoteException {
        Command command = new Command("adminCalculateScore", this);
        command.addParameter(playerId);
        serverMethods.command_FromClientToServer(command);
    }

    public void userEndGame(int playerId) throws RemoteException {
        Command command = new Command("userEndGame", this);
        command.addParameter(playerId);
        serverMethods.command_FromClientToServer(command);
    }

    public int getplayerId() {
        return playerId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void createMyIdJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("playerId", playerId);
            File idFile = new File("$IdClient"+username+".json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(idFile, rootNode);
        } catch (IOException e) {
            ui.printUpdateMessage("Errore durante l'inizializzazione dell'ID: " + e.getMessage());
        }
    }
}