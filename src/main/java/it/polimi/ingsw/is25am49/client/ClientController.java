package it.polimi.ingsw.is25am49.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.enums.Color;
import it.polimi.ingsw.is25am49.client.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalUsernameException;
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
    public int myId;
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
        myId = -1;  //non ancora assegnato
        this.gameSession = gameSession;
    }

    public ClientController(GameSession gameSession, UserInterface ui) {
        myId = -1;  //non ancora assegnato
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
        if(playerId==myId)ui.printUpdateMessage(error + errorMessage);
    }

    public void registerClientCallback(int playerId) {
        this.myId = playerId;
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
        gameSession.getState().userJoinLobby(gameSession.getColor(), username, playerId);
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

    public void adminEndGameCallback(int playerId) {

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
        Command command = new Command("registerClientRMI", this);
        try {
            ObjectMapper mapper = new ObjectMapper();
            File idFile = new File("$IdClient"+username+".json");
            myId = mapper.readTree(idFile).get("playerId").asInt();
        } catch (IOException e) {
            System.out.println("Errore durante la lettura dell'ID: " + e.getMessage());
        }
        command.addParameter(myId);
        serverMethods.command_FromClientToServer(command);
        serverHandler.connectCommand();
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
            myId = mapper.readTree(idFile).get("playerId").asInt();
        } catch (IOException e) {
            System.out.println("Errore durante la lettura dell'ID: " + e.getMessage());
        }
        command.addParameter(myId);
        serverMethods.command_FromClientToServer(command);
        serverHandler.connectCommand();
    }


    public void adminSetup(int numberOfPlayers, int mode) {
        try {
            gameSession.getState().checkAdminCreateGame(myId,mode ,numberOfPlayers );
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
        }
        catch (IllegalUsernameException e) {
            throw new IllegalUsernameException(e.getMessage());
        }
    }

    public void userDrawTile(int playerId, int tileId) throws RemoteException {
        try{
            gameSession.getState().checkUserDrawTile(playerId, tileId);
            Command command = new Command("userDrawTile", this);
            command.addParameter(playerId);
            command.addParameter(tileId);
            serverMethods.command_FromClientToServer(command);
        }catch(Exception e){
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userPlaceTile(int playerId, Point coordinates, Orientation orientation) throws RemoteException {
        try{
            gameSession.getState().checkUserPlaceTile(playerId, coordinates);
            Command command = new Command("userPlaceTile", this);
            command.addParameter(playerId);
            command.addParameter(coordinates);
            command.addParameter(orientation);
            serverMethods.command_FromClientToServer(command);
        }catch (Exception e){
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userPickTile(int playerId, Point coordinates) throws RemoteException {
        try{
            gameSession.getState().checkUserPickTile(playerId, coordinates);
            Command command = new Command("userPickTile", this);
            command.addParameter(playerId);
            command.addParameter(coordinates);
            serverMethods.command_FromClientToServer(command);
        }catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }
    }

    public void userDiscardTile(int playerId) throws RemoteException {
        try{
            gameSession.getState().checkUserDiscardTile(playerId);
            Command command = new Command("userDiscardTile", this);
            command.addParameter(playerId);
            serverMethods.command_FromClientToServer(command);
        }catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userFinishShipboard(int playerId) throws RemoteException {
        try{
            gameSession.getState().checkUserFinishShipboard(playerId);
            Command command = new Command("userFinishShipboard", this);
            command.addParameter(playerId);
            serverMethods.command_FromClientToServer(command);
        }catch (Exception e) {
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) throws RemoteException {
        try{
            gameSession.getState().checkUserLookSmallAdventureCardDeck(playerId, smallDeckIndex);
            Command command = new Command("userLookSmallAdventureCardDeck", this);
            command.addParameter(playerId);
            command.addParameter(smallDeckIndex);
            serverMethods.command_FromClientToServer(command);
        }catch (Exception e){
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) throws RemoteException {
        try{
            gameSession.getState().checkUserStopLookSmallAdventureCardDeck(playerId, smallDeckIndex);
            Command command = new Command("userStopLookSmallAdventureCardDeck", this);
            command.addParameter(playerId);
            command.addParameter(smallDeckIndex);
            serverMethods.command_FromClientToServer(command);
        }catch (Exception e){
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userPlaceTimer(int playerId) throws RemoteException {
        try{
            gameSession.getState().checkUserPlaceTimer(playerId);
            Command command = new Command("userPlaceTimer", this);
            command.addParameter(playerId);
            serverMethods.command_FromClientToServer(command);
        }catch (Exception e){
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) throws RemoteException {
        try{
            gameSession.getState().checkUserSelectTypeOfToken(playerId, token, coordinates);
            Command command = new Command("userSelectTypeOfToken", this);
            command.addParameter(playerId);
            command.addParameter(token);
            command.addParameter(coordinates);
            serverMethods.command_FromClientToServer(command);
        }catch (Exception e){
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userThrowDices(int playerId) throws RemoteException {
        try{
            gameSession.getState().checkUserThrowDices(playerId);
            Command command = new Command("userThrowDices", this);
            command.addParameter(playerId);
            serverMethods.command_FromClientToServer(command);
        }catch(Exception e){
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userDrawAdventureCard(int playerId) throws RemoteException {
        try{
            gameSession.getState().checkUserDrawAdventureCard(playerId);
            Command command = new Command("userDrawAdventureCard", this);
            command.addParameter(playerId);
            serverMethods.command_FromClientToServer(command);
        }catch(Exception e){
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userFinishDecisionActivating(int playerId, HashSet<Point> coordinates) throws RemoteException {
        try{
            gameSession.getState().checkUserFinishDecisionActivating(playerId, coordinates);
            Command command = new Command("userFinishDecisionActivating", this);
            command.addParameter(playerId);
            command.addParameter(coordinates);
            serverMethods.command_FromClientToServer(command);
        }catch (Exception e){
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) throws RemoteException {
        try{
            gameSession.getState().checkUserFinishDecisionToken(playerId, listOfTokens);
            Command command = new Command("userFinishDecisionToken", this);
            command.addParameter(playerId);
            command.addParameter(listOfTokens);
            serverMethods.command_FromClientToServer(command);
        }catch(Exception e){
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userAcceptPrizes(int playerId, boolean wantsRewards) throws RemoteException {
        try{
            gameSession.getState().checkUserAcceptPrizes(playerId, wantsRewards);
            Command command = new Command("userAcceptPrizes", this);
            command.addParameter(playerId);
            command.addParameter(wantsRewards);
            serverMethods.command_FromClientToServer(command);
        }catch (Exception e){
            ui.printUpdateMessage(e.getMessage());
        }
    }

    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) throws RemoteException {
        try{
            gameSession.getState().checkUserLandOnPlanet(playerId,isLanded,planetId);
            Command command = new Command("userLandOnPlanet", this);
            command.addParameter(playerId);
            command.addParameter(isLanded);
            command.addParameter(planetId);
            serverMethods.command_FromClientToServer(command);
        }catch (Exception e){
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void userLandOnStationOrShip(int playerId, boolean isLanded) throws RemoteException {
        try{
            gameSession.getState().checkUserLandOnStationOrShip(playerId,isLanded);
            Command command = new Command("userLandOnStationOrShip", this);
            command.addParameter(playerId);
            command.addParameter(isLanded);
            serverMethods.command_FromClientToServer(command);
        }catch(Exception e){
            ui.printUpdateMessage(e.getMessage());
        }
    }

    public void userQuitGame(int playerId) throws RemoteException {
        try{
            Command command = new Command("userQuitGame", this);
            command.addParameter(playerId);
            serverMethods.command_FromClientToServer(command);
        }catch (Exception e){
            ui.printUpdateMessage(e.getMessage());
        }

    }

    public void adminCalculateScore(int playerId) throws RemoteException {
        try{
            gameSession.getState().checkAdminCalculateScore(playerId);
            Command command = new Command("adminCalculateScore", this);
            command.addParameter(playerId);
            serverMethods.command_FromClientToServer(command);
        }catch (Exception e){
            ui.printUpdateMessage(e.getMessage());
        }
    }

    public void adminEndGame(int playerId) throws RemoteException {
        try{
            gameSession.getState().checkAdminEndGame(playerId);
            Command command = new Command("adminEndGame", this);
            command.addParameter(playerId);
            serverMethods.command_FromClientToServer(command);
        }catch(Exception e){
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
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("playerId", myId);
            File idFile = new File("$IdClient"+username+".json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(idFile, rootNode);
        } catch (IOException e) {
            ui.printUpdateMessage("Errore durante l'inizializzazione dell'ID: " + e.getMessage());
        }
    }
    public void clientReset(){
        myId = -1;
        gameSession = new GameSession(ui);
        ui.setGamesession(gameSession);
        serverMethods.setGamesession(gameSession);
    }
}