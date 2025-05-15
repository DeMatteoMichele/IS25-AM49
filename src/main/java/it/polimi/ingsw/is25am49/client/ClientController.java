package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.enums.Color;
import it.polimi.ingsw.is25am49.client.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.client.model.states.*;
import it.polimi.ingsw.is25am49.server.RemoteClient;
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
    public int playerId;
    private GameSession gameSession;
    private RemoteServer serverMethods;
    private ServerHandler serverHandler;
    private UserInterface ui;
    private String username; //todo da togliere, bisogna aggiungere username al playerjoin


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

    public void notifyClient(String msg) {

    }

    public void registerClientCallback(int playerId) {
        this.playerId = playerId;
        ui.setClientId(playerId);
        //System.out.println("CONNESSIONE RIUSCTIA. IL TUO PLAYER ID È " + playerId);
    }

    public void adminSetupCallback(int playerId, int numberOfPlayers, GameMode mode) {
        gameSession.getState().adminCreateGame(playerId, mode, numberOfPlayers);
        ui.printUpdateMessage("ADMIN SETUP"); //TODO non va il callback
    }

    public void playerJoinCallback(int playerId, String username,int seedForGeneration) {
        gameSession.getState().userJoinLobby(Color.values()[playerId], username, playerId);
    }

    public void playerErrorJoinCallback(int playerId, String typeOfError) {
        ui.printUpdateMessage(typeOfError + " manca un modo per ripristinare il clientModel");
    }

    public void gameStartedCallback() {

    }

    public void userDrawTileCallback(int playerId, int tileId) {

    }

    public void userErrorDrawTileCallback(int playerId, int tileId) {

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

    public void userErrorLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) {

    }

    public void userStopLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) {

    }

    public void userPlaceTimerCallback(int playerId) {

    }

    public void userErrorPlaceTimerCallback(int playerId) {

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
     *
     *                 //     * @throws RemoteException if there is an error during the remote call
     */
    public void registerClientSocket() throws RemoteException {
        CommandClient commandClient = new CommandClient("registerClientSocket", this);
        commandClient.addParameter(playerId);
        serverMethods.sendCommand(commandClient);
        serverHandler.connectCommand();
    }


    public void adminSetup(int numberOfPlayers, GameMode mode) {
        try {
            CommandClient commandClient = new CommandClient("adminSetup", this);
            commandClient.addParameter(playerId);
            commandClient.addParameter(numberOfPlayers);
            commandClient.addParameter(mode);
            serverMethods.sendCommand(commandClient);
        } catch (RemoteException e) {
            ui.printUpdateMessage(e.getMessage());
        }
    }

    public void playerJoin(String username) {
        try {
            this.username = username;//todo da togliere
            CommandClient commandClient = new CommandClient("playerJoin", this);
            commandClient.addParameter(playerId);
            commandClient.addParameter(username);
            commandClient.addParameter(Color.values()[playerId]);
            serverMethods.sendCommand(commandClient);
        } catch (RemoteException | GameSettingsException e) {
            throw new RuntimeException(e);
        }
    }

  public void userDrawTile(int playerId, int tileId) throws RemoteException {
      CommandClient commandClient = new CommandClient("userDrawTile", this);
      commandClient.addParameter(playerId);
      commandClient.addParameter(tileId);
      serverMethods.sendCommand(commandClient);
  }

  public void userPlaceTile(int playerId, Point coordinates, Orientation orientation) throws RemoteException {
      CommandClient commandClient = new CommandClient("userPlaceTile", this);
      commandClient.addParameter(playerId);
      commandClient.addParameter(coordinates);
      commandClient.addParameter(orientation);
      serverMethods.sendCommand(commandClient);
  }

  public void userPickTile(int playerId, Point coordinates) throws RemoteException {
      CommandClient commandClient = new CommandClient("userPickTile", this);
      commandClient.addParameter(playerId);
      commandClient.addParameter(coordinates);
      serverMethods.sendCommand(commandClient);
  }

  public void userDiscardTile(int playerId) throws RemoteException {
      CommandClient commandClient = new CommandClient("userDiscardTile", this);
      commandClient.addParameter(playerId);
      serverMethods.sendCommand(commandClient);
  }

  public void userFinishShipboard(int playerId) throws RemoteException {
      CommandClient commandClient = new CommandClient("userFinishShipboard", this);
      commandClient.addParameter(playerId);
      serverMethods.sendCommand(commandClient);
  }

  public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) throws RemoteException {
      CommandClient commandClient = new CommandClient("userLookSmallAdventureCardDeck", this);
      commandClient.addParameter(playerId);
      commandClient.addParameter(smallDeckIndex);
      serverMethods.sendCommand(commandClient);
  }

  public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) throws RemoteException {
      CommandClient commandClient = new CommandClient("userStopLookSmallAdventureCardDeck", this);
      commandClient.addParameter(playerId);
      commandClient.addParameter(smallDeckIndex);
      serverMethods.sendCommand(commandClient);
  }

  public void userPlaceTimer(int playerId) throws RemoteException {
      CommandClient commandClient = new CommandClient("userPlaceTimer", this);
      commandClient.addParameter(playerId);
      serverMethods.sendCommand(commandClient);
  }

  public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) throws RemoteException {
      CommandClient commandClient = new CommandClient("userSelectTypeOfToken", this);
      commandClient.addParameter(playerId);
      commandClient.addParameter(token);
      commandClient.addParameter(coordinates);
      serverMethods.sendCommand(commandClient);
  }

  public void userThrowDices(int playerId) throws RemoteException {
      CommandClient commandClient = new CommandClient("userThrowDices", this);
      commandClient.addParameter(playerId);
      serverMethods.sendCommand(commandClient);
  }

  public void userDrawAdventureCard(int playerId) throws RemoteException {
      CommandClient commandClient = new CommandClient("userDrawAdventureCard", this);
      commandClient.addParameter(playerId);
      serverMethods.sendCommand(commandClient);
  }

  public void userFinishDecisionActivating(int playerId, HashSet<Point> coordinates) throws RemoteException {
      CommandClient commandClient = new CommandClient("userFinishDecisionActivating", this);
      commandClient.addParameter(playerId);
      commandClient.addParameter(coordinates);
      serverMethods.sendCommand(commandClient);
  }

  public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) throws RemoteException {
      CommandClient commandClient = new CommandClient("userFinishDecisionToken", this);
      commandClient.addParameter(playerId);
      commandClient.addParameter(listOfTokens);
      serverMethods.sendCommand(commandClient);
  }

  public void userAcceptPrizes(int playerId, boolean wantsRewards) throws RemoteException {
      CommandClient commandClient = new CommandClient("userAcceptPrizes", this);
      commandClient.addParameter(playerId);
      commandClient.addParameter(wantsRewards);
      serverMethods.sendCommand(commandClient);
  }

  public void userLandOnPlanet(int playerId, boolean isLanded, Planet planet) throws RemoteException {
      CommandClient commandClient = new CommandClient("userLandOnPlanet", this);
      commandClient.addParameter(playerId);
      commandClient.addParameter(isLanded);
      commandClient.addParameter(planet);
      serverMethods.sendCommand(commandClient);
  }

  public void userLandOnStationOrShip(int playerId, boolean isLanded) throws RemoteException {
      CommandClient commandClient = new CommandClient("userLandOnStationOrShip", this);
      commandClient.addParameter(playerId);
      commandClient.addParameter(isLanded);
      serverMethods.sendCommand(commandClient);
  }

  public void userStopFlight(int playerId) throws RemoteException {
      CommandClient commandClient = new CommandClient("userStopFlight", this);
      commandClient.addParameter(playerId);
      serverMethods.sendCommand(commandClient);
  }

  public void adminCalculateScore(int playerId) throws RemoteException {
      CommandClient commandClient = new CommandClient("adminCalculateScore", this);
      commandClient.addParameter(playerId);
      serverMethods.sendCommand(commandClient);
  }

  public void userEndGame(int playerId) throws RemoteException {
      CommandClient commandClient = new CommandClient("userEndGame", this);
      commandClient.addParameter(playerId);
      serverMethods.sendCommand(commandClient);
  }

    public int getplayerId() {
        return playerId;
    }
}