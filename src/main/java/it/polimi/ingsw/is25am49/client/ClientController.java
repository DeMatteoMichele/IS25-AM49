package it.polimi.ingsw.is25am49.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalUsernameException;
import it.polimi.ingsw.is25am49.client.model.states.*;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.enums.Orientation;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import it.polimi.ingsw.is25am49.shared.Command;
import it.polimi.ingsw.is25am49.shared.RemoteServer;
import javafx.util.Pair;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ClientController {
    public int myId;
    private GameSession gameSession;
    private RemoteServer serverMethods;
    private ServerHandler serverHandler;
    private UserInterface ui;
    private String username;

    private String error = "⚠ Error : ";

    /**
     * Solo per i test
     */
    public ClientController(GameSession gameSession) {
        myId = -1;  //non ancora assegnato
        this.gameSession = gameSession;
    }

    public ClientController(GameSession gameSession, UserInterface ui) {
        myId = -1;  //non ancora assegnato
        this.gameSession = gameSession;
        this.ui = ui;
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
        if (myId == playerId) {
            ui.printUpdateMessage(error + errorMessage);
        }
    }

    public void registerClientCallback(int playerId) {
        ui.printUpdateMessage("DEBUG: In registerClientCallback - playerId: " + playerId);
        this.myId = playerId;
        ui.setClientId(playerId);
        if (username == null || username.trim().isEmpty()) {
            ui.printUpdateMessage(error + "Username is not set. Cannot save ID file.");
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("playerId", playerId);
            File idFile = new File("$IdClient" + username + ".json");
            if (idFile.exists()) {
                idFile.delete();
            }
            idFile = new File("$IdClient" + username + ".json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(idFile, rootNode);
        } catch (IOException e) {
            ui.printUpdateMessage(error + "Error while saving ID: " + e.getMessage());
        }
    }

    public void adminSetupCallback(int playerId, int numberOfPlayers, GameMode mode) {
        try {
            gameSession.getState().adminCreateGame(playerId, mode, numberOfPlayers);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error during game setup --> " + e.getMessage());
        }
    }

    public void playerJoinCallback(int playerId, String username, int seedForGeneration) {
        try{
            gameSession.getState().userJoinLobby(gameSession.getColor(),username,playerId);
            gameSession.setSeedForGeneration(seedForGeneration);
        } catch(Exception e){
            ui.printUpdateMessage(error + "Error during player join --> " + e.getMessage());
        }

    }


    public void userDrawTileCallback(int playerId, int tileId) {
        try{
            gameSession.getState().userDrawTile(playerId, tileId);
        } catch (Exception e) {
            ui.printUpdateMessage(error + "Error while drawing a card: --> " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void userPlaceTileCallback(int playerId, Point coordinates,Orientation orientation) {
        try{
            gameSession.getState().userPlaceTile(playerId,coordinates);
        }catch (Exception e){
            ui.printUpdateMessage(error + "Error while placing a tile: --> " + e.getMessage());
        }
    }

    public void userPickTileCallback(int playerId, Point coordinates) {
        try{
            gameSession.getState().userPickTile(playerId,coordinates);
        }catch (Exception e){
            ui.printUpdateMessage(error + "Error while picking a tile: --> " + e.getMessage());
        }
    }

    public void userDiscardTileCallback(int playerId) {
        try{
            gameSession.getState().userDiscardTile(playerId);
        }catch (Exception e){
            ui.printUpdateMessage(error + "Error while discarding a tile: --> " + e.getMessage());
        }
    }

    public void userFinishShipboardCallback(int playerId) {
        try{
            gameSession.getState().userFinishShipboard(playerId);
        } catch(Exception e){
            ui.printUpdateMessage(error + "Error while finishing shipboard: --> " + e.getMessage());
        }
    }

    public void userLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) {
        try{
            gameSession.getState().userLookSmallAdventureCardDeck(playerId, smallDeckIndex);
        }catch (Exception e){
            ui.printUpdateMessage(error + "Error while looking a small deck: --> " + e.getMessage());
        }
    }

    public void userStopLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) {
        try{
            gameSession.getState().userStopLookSmallAdventureCardDeck(playerId, smallDeckIndex);
        } catch (Exception e){
            ui.printUpdateMessage(error + "Error while stopping looking a small deck: --> " + e.getMessage());
        }
    }

    public void userPlaceTimerCallback(int playerId) {
        try{
            gameSession.getState().userPlaceTimer(playerId);
        }catch (Exception e){
            ui.printUpdateMessage(error + "Error while placing a timer: --> " + e.getMessage());
        }
    }

    public void userSelectTypeOfTokenCallback(int playerId, Token token, Point coordinates) {
        try{
            gameSession.getState().userSelectTypeOfToken(playerId, token, coordinates);
        }catch(Exception e){
            ui.printUpdateMessage(error + "Error while selecting a token: --> " + e.getMessage());
        }
    }

    public void userThrowDicesCallback(int playerId, int result) {
        try{
            gameSession.getState().userThrowDices(playerId);
        }catch(Exception e){
            ui.printUpdateMessage(error + "Error while throwing dices: --> " + e.getMessage());
        }
    }

    public void userDrawAdventureCardCallback(int playerId) {
        try{
            gameSession.getState().userDrawAdventureCard(playerId);
        }catch (Exception e){
            ui.printUpdateMessage(error + "Error while drawing an adventure card: --> " + e.getMessage());
        }
    }

    public void userFinishDecisionActivatingCallback(int playerId, Set<Point> coordinates) {
        try{
            gameSession.getState().userFinishDecisionActivating(playerId, coordinates);
        }catch (Exception e){
            ui.printUpdateMessage(error + "Error while finishing decision activating: --> " + e.getMessage());
        }
    }

    public void userFinishDecisionTokenCallback(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
        try{
            gameSession.getState().userFinishDecisionToken(playerId, listOfTokens);
        }catch (Exception e){
            ui.printUpdateMessage(error + "Error while finishing decision token: --> " + e.getMessage());
        }
    }

    public void userAcceptPrizesCallback(int playerId, boolean isAccepted) {
        try{
            gameSession.getState().userAcceptPrizes(playerId, isAccepted);
        }catch (Exception e){
            ui.printUpdateMessage(error + "Error while accepting prizes: --> " + e.getMessage());
        }
    }

    public void userLandOnPlanetCallback(int playerId, boolean isLanded, int planetId) {
        try{
            gameSession.getState().userLandOnPlanet(playerId, isLanded, planetId);
        }catch (Exception e){
            ui.printUpdateMessage(error + "Error while landing on planet: --> " + e.getMessage());
        }
    }

    public void userLandOnStationOrShipCallback(int playerId, boolean isLanded) {
        try{
            gameSession.getState().userLandOnStationOrShip(playerId, isLanded);
        }catch (Exception e){
            ui.printUpdateMessage(error + "Error while landing on station or ship: --> " + e.getMessage());
        }
    }

    public void userStopFlightCallback(int playerId) {
        try{
            gameSession.getState().userStopFlight(playerId);
        }catch (Exception e){
            ui.printUpdateMessage(error + "Error while stopping flight: --> " + e.getMessage());
        }
    }

    public void adminCalculateScoreCallback(int playerId) {
        try{
            gameSession.getState().adminCalculateScore(playerId);
        }catch (Exception e){
            ui.printUpdateMessage(error + "Error while calculating score: --> " + e.getMessage());
        }
    }

    public void adminEndGameCallback(int playerId) {
        try{
            gameSession.getState().adminEndGame(playerId);
        } catch (Exception e){
            ui.printUpdateMessage(error + "Error while ending game: --> " + e.getMessage());
        }
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

    public void registerClientRMI(RemoteServer client) throws RemoteException, IOException {
        Command command = new Command("registerClientRMI", this);
        ObjectMapper mapper = new ObjectMapper();
        File idFile = new File("$IdClient" + username + ".json");
        myId = mapper.readTree(idFile).get("playerId").asInt();
        command.addParameter(client);
        serverHandler.connectCommand();
        serverMethods.command_FromClientToServer(command);
    }

    /**
     * This method is used to register the client socket on the server.
     * It creates a temporary file with the playerId and puts it in the outgoing queue.
     */
    public void registerClientSocket() throws RemoteException, IOException {
        Command command = new Command("registerClientSocket", this);
        ObjectMapper mapper = new ObjectMapper();
        File idFile = new File("$IdClient" + username + ".json");
        myId = mapper.readTree(idFile).get("playerId").asInt();
        ui.printUpdateMessage("DEBUG: In registerClientSocket - myId: " + myId+" using idFile: "+idFile.getAbsolutePath());
        command.addParameter(myId);
        serverHandler.connectCommand();
        serverMethods.command_FromClientToServer(command);
    }


    public void adminSetup(int numberOfPlayers, int mode) {
        try {
            gameSession.getState().checkAdminCreateGame(myId, mode, numberOfPlayers);
            Command command = new Command("adminSetup", this);
            command.addParameter(myId);
            command.addParameter(numberOfPlayers);
            command.addParameter(GameMode.values()[mode]);
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException | GameSettingsException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void playerJoin(String username) {
        try {
            gameSession.getState().checkUserJoinLobby(myId, username);
            Command command = new Command("playerJoin", this);
            command.addParameter(myId);
            command.addParameter(username);
            serverMethods.command_FromClientToServer(command);
        } catch (RemoteException | GameSettingsException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IllegalUsernameException e) {
            throw new IllegalUsernameException(e.getMessage());
        }
    }

    public void userDrawTile(int playerId, int tileId) throws RemoteException {
        try {
            gameSession.getState().checkUserDrawTile(playerId, tileId);
            Command command = new Command("userDrawTile", this);
            command.addParameter(playerId);
            command.addParameter(tileId);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userPlaceTile(int playerId, Point coordinates, Orientation orientation) throws RemoteException {
        try {
            gameSession.getState().checkUserPlaceTile(playerId, coordinates);
            Command command = new Command("userPlaceTile", this);
            command.addParameter(playerId);
            command.addParameter(coordinates);
            command.addParameter(orientation);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userPickTile(int playerId, Point coordinates) throws RemoteException {
        try {
            gameSession.getState().checkUserPickTile(playerId, coordinates);
            Command command = new Command("userPickTile", this);
            command.addParameter(playerId);
            command.addParameter(coordinates);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }
    }

    public void userDiscardTile(int playerId) throws RemoteException {
        try {
            gameSession.getState().checkUserDiscardTile(playerId);
            Command command = new Command("userDiscardTile", this);
            command.addParameter(playerId);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userFinishShipboard(int playerId) throws RemoteException {
        try {
            gameSession.getState().checkUserFinishShipboard(playerId);
            Command command = new Command("userFinishShipboard", this);
            command.addParameter(playerId);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) throws RemoteException {
        try {
            gameSession.getState().checkUserLookSmallAdventureCardDeck(playerId, smallDeckIndex);
            Command command = new Command("userLookSmallAdventureCardDeck", this);
            command.addParameter(playerId);
            command.addParameter(smallDeckIndex);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) throws RemoteException {
        try {
            gameSession.getState().checkUserStopLookSmallAdventureCardDeck(playerId, smallDeckIndex);
            Command command = new Command("userStopLookSmallAdventureCardDeck", this);
            command.addParameter(playerId);
            command.addParameter(smallDeckIndex);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userPlaceTimer(int playerId) throws RemoteException {
        try {
            gameSession.getState().checkUserPlaceTimer(playerId);
            Command command = new Command("userPlaceTimer", this);
            command.addParameter(playerId);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) throws RemoteException {
        try {
            gameSession.getState().checkUserSelectTypeOfToken(playerId, token, coordinates);
            Command command = new Command("userSelectTypeOfToken", this);
            command.addParameter(playerId);
            command.addParameter(token);
            command.addParameter(coordinates);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userThrowDices(int playerId) throws RemoteException {
        try {
            gameSession.getState().checkUserThrowDices(playerId);
            Command command = new Command("userThrowDices", this);
            command.addParameter(playerId);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userDrawAdventureCard(int playerId) throws RemoteException {
        try {
            gameSession.getState().checkUserDrawAdventureCard(playerId);
            Command command = new Command("userDrawAdventureCard", this);
            command.addParameter(playerId);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userFinishDecisionActivating(int playerId, HashSet<Point> coordinates) throws RemoteException {
        try {
            gameSession.getState().checkUserFinishDecisionActivating(playerId, coordinates);
            Command command = new Command("userFinishDecisionActivating", this);
            command.addParameter(playerId);
            command.addParameter(coordinates);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) throws RemoteException {
        try {
            gameSession.getState().checkUserFinishDecisionToken(playerId, listOfTokens);
            Command command = new Command("userFinishDecisionToken", this);
            command.addParameter(playerId);
            command.addParameter(listOfTokens);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userAcceptPrizes(int playerId, boolean wantsRewards) throws RemoteException {
        try {
            gameSession.getState().checkUserAcceptPrizes(playerId, wantsRewards);
            Command command = new Command("userAcceptPrizes", this);
            command.addParameter(playerId);
            command.addParameter(wantsRewards);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }
    }

    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) throws RemoteException {
        try {
            gameSession.getState().checkUserLandOnPlanet(playerId, isLanded, planetId);
            Command command = new Command("userLandOnPlanet", this);
            command.addParameter(playerId);
            command.addParameter(isLanded);
            command.addParameter(planetId);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }
    }

    public void userLandOnStationOrShip(int playerId, boolean isLanded) throws RemoteException {
        try {
            gameSession.getState().checkUserLandOnStationOrShip(playerId, isLanded);
            Command command = new Command("userLandOnStationOrShip", this);
            command.addParameter(playerId);
            command.addParameter(isLanded);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }
    }

    public void userQuitGame(int playerId) throws RemoteException {
        try {
            Command command = new Command("userQuitGame", this);
            command.addParameter(playerId);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void adminCalculateScore(int playerId) throws RemoteException {
        try {
            gameSession.getState().checkAdminCalculateScore(playerId);
            Command command = new Command("adminCalculateScore", this);
            command.addParameter(playerId);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }
    }

    public void adminEndGame(int playerId) throws RemoteException {
        try {
            gameSession.getState().checkAdminEndGame(playerId);
            Command command = new Command("adminEndGame", this);
            command.addParameter(playerId);
            serverMethods.command_FromClientToServer(command);
        } catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public int getplayerId() {
        return myId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void createMyIdJson() {
        ui.printUpdateMessage("DEBUG: In createMyIdJson - Tentativo di scrivere myId: " + myId + " per username: " + username + " nel file: $IdClient" + username + ".json");
        File idFile = new File("$IdClient" + username + ".json");
        try {
            if (idFile.exists()) {
                idFile.delete();
            }
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("playerId", myId);
            idFile = new File("$IdClient" + username + ".json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(idFile, rootNode);
            ui.printUpdateMessage("DEBUG: File " + idFile.getAbsolutePath() + " scritto con successo in createMyIdJson.");
        } catch (IOException e) {
            ui.printUpdateMessage(error + "Errore durante l'inizializzazione dell'ID (scrittura file): " + e.getMessage() + " per file: " + idFile.getAbsolutePath());
        }
    }
    public void clientReset(){
        myId = -1;
        gameSession = new GameSession(ui);
        ui.setGameSession(gameSession);
    }
}