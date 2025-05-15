package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.client.CommandClient;
import it.polimi.ingsw.is25am49.client.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.adventurecards.Planet;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.*;

public class ServerController {
    private final GameSession gameSession;
    private ServerSerializer serverSerializer;
    private final ArrayList<Integer> players;
    private ServerRMI serverRMI;

    public ServerController(GameSession gameSession) {
        this.gameSession = gameSession;
        this.players = new ArrayList<>();
    }

    public void setSerializer(ServerSerializer serverSerializer) {
        this.serverSerializer = serverSerializer;
    }

    public void addPlayerToLobby(int playerId) {
        players.add(playerId);
    }

    public void registerClientRMI(RemoteClient client) {

    }

    public void adminSetup(int playerId, int numberOfPlayers, GameMode mode) {
        try {
            try {
                gameSession.getState().adminCreateGame(playerId,mode,numberOfPlayers);
                CommandServer serObjectTemp = new CommandServer("adminSetupCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(numberOfPlayers);
                serObjectTemp.addParameter(mode);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }catch (GameSettingsException e) {
                serverSerializer.playerErrorJoinCallback(playerId,e.getMessage()); //todo manca callback error
            }
        } catch (RemoteException _) {
        }

    }

    public void playerJoin(int playerId, String username, Color color) {
        addPlayerToLobby(playerId);
        try {
            try {
                gameSession.getState().userJoinLobby(color, username, playerId);
                CommandServer serObjectTemp = new CommandServer("playerJoinCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(username);
                serObjectTemp.addParameter(gameSession.getSeedForGeneration());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (GameSettingsException e) {
                serverSerializer.playerErrorJoinCallback(playerId,e.getMessage());
            }
        } catch (RemoteException _) {
            System.out.println("Pippo");
        }

    }

    public void userDrawTile(int playerId, int tileId) {
        try{
            try{
                gameSession.getState().userDrawTile(playerId, tileId);
                CommandServer serObjectTemp = new CommandServer("userDrawTileCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(tileId);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch(Exception e){
                CommandServer serObjectTemp = new CommandServer("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Draw tile isn't worked: " +e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);

            }
        } catch (RemoteException e) {
            System.err.println("Error during the notification to the client in drawTile: " + e.getMessage());
        }

    }

    public void userPlaceTile(int playerId, Point coordinates, Orientation orientation) {
        try{
            try{
                gameSession.getState().userPlaceTile(playerId, coordinates);
                CommandServer serObjectTemp = new CommandServer("userPlaceTileCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(coordinates);
                serObjectTemp.addParameter(orientation);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch(Exception e){
                CommandServer serObjectTemp = new CommandServer("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Place tile isn't worked: " +e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        }catch (RemoteException e){
            System.err.println("Error during the notification to the client in placeTile: " + e.getMessage());
        }
    }


    public void userPickTile(int playerId, Point coordinates) {
        try{
            try{
                gameSession.getState().userPickTile(playerId, coordinates);
                CommandServer serObjectTemp = new CommandServer("userPickTileCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(coordinates);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch(Exception e){
                CommandServer serObjectTemp = new CommandServer("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Pick tile isn't worked: " +e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        }catch (RemoteException e){
            System.out.println("Error during the notification to the client in pickTile: " + e.getMessage());
        }
    }

    public void userDiscardTile(int playerId) {
        try {
            try {
                gameSession.getState().userDiscardTile(playerId);
                CommandServer serObjectTemp = new CommandServer("userDiscardTile", this, playerId);
                serObjectTemp.addParameter(playerId);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                CommandServer serObjectTemp = new CommandServer("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Discard tile isn't worked: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        }catch (RemoteException e){
                System.out.println("Error during the notification to the client in discardTile: " + e.getMessage());
        }
    }

    public void userFinishShipboard(int playerId) {
        try{
            try{
                gameSession.getState().userFinishShipboard(playerId);
                CommandServer serObjectTemp = new CommandServer("userFinishShipboardCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch(Exception e){
                CommandServer serObjectTemp = new CommandServer("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Finish shipboard isn't worked: " +e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        }catch (RemoteException e){
            System.out.println("Error during the notification to the client in finishShipboard: " + e.getMessage());
        }
    }

    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        try{
            try{
                gameSession.getState().userLookSmallAdventureCardDeck(playerId, smallDeckIndex);
                CommandServer serObjectTemp = new CommandServer("userLookSmallAdventureCardDeckCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(smallDeckIndex);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch(Exception e){
                CommandServer serObjectTemp = new CommandServer("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Look small adventure card deck isn't worked: " +e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        }catch (RemoteException e){
            System.out.println("Error during the notification to the client in lookSmallAdventureCardDeck: " + e.getMessage());
        }
    }

    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        try{
            try{
                gameSession.getState().userStopLookSmallAdventureCardDeck(playerId, smallDeckIndex);
                CommandServer serObjectTemp = new CommandServer("userStopLookSmallAdventureCardDeckCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(smallDeckIndex);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch(Exception e){
                CommandServer serObjectTemp = new CommandServer("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Stop look small adventure card deck isn't worked: " +e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        }catch (RemoteException e){
            System.out.println("Error during the notification to the client in stopLookSmallAdventureCardDeck: " + e.getMessage());
        }
    }

    public void userPlaceTimer(int playerId) {
        try {
            try{
                gameSession.getState().userPlaceTimer(playerId);
                CommandServer serObjectTemp = new CommandServer("userPlaceTimerCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                CommandServer serObjectTemp = new CommandServer("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Place timer isn't worked: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        }catch (RemoteException e){
            System.out.println("Error during the notification to the client in placeTimer: " + e.getMessage());
        }
    }

    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) {

        try{
            try{
                gameSession.getState().userSelectTypeOfToken(playerId, token, coordinates);
                CommandServer serObjectTemp = new CommandServer("userSelectTypeOfTokenCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(token);
                serObjectTemp.addParameter(coordinates);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }catch (Exception e){
                CommandServer serObjectTemp = new CommandServer("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Select type of token isn't worked: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        }catch (RemoteException e){
            System.out.println("Error during the notification to the client in selectTypeOfToken: " + e.getMessage());
        }
    }

    public void userThrowDices(int playerId) {
        try{
            try{
                gameSession.getState().userThrowDices(playerId);
                CommandServer serObjectTemp = new CommandServer("userThrowDicesCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                CommandServer serObjectTemp = new CommandServer("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Throw dices isn't worked: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        }catch (RemoteException e){
            System.out.println("Error during the notification to the client in throwDices: " + e.getMessage());
        }
    }

    public void userDrawAdventureCard(int playerId) {
        try{
            try{
                gameSession.getState().userDrawAdventureCard(playerId);
                CommandServer serObjectTemp = new CommandServer("userDrawAdventureCardCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                CommandServer serObjectTemp = new CommandServer("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Draw adventure card isn't worked: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        }catch (RemoteException e){
            System.out.println("Error during the notification to the client in drawAdventureCard: " + e.getMessage());
        }
    }

    public void userFinishDecisionActivating(int playerId, HashSet<Point> coordinates) {
        try{
            try{
                gameSession.getState().userFinishDecisionActivating(playerId, coordinates);
                CommandServer serObjectTemp = new CommandServer("userFinishDecisionActivatingCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(coordinates);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                CommandServer serObjectTemp = new CommandServer("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Finish decision activating isn't worked: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        }catch (RemoteException e){
            System.out.println("Error during the notification to the client in finishDecisionActivating: " + e.getMessage());
        }
    }
    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
        try {
            try {
                gameSession.getState().userFinishDecisionToken(playerId, listOfTokens);
                CommandServer serObjectTemp = new CommandServer("userFinishDecisionTokenCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(listOfTokens);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                CommandServer serObjectTemp = new CommandServer("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Finish decision token isn't worked: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        }catch (RemoteException e){
            System.out.println("Error during the notification to the client in finishDecisionToken: " + e.getMessage());
        }
    }

    public void userAcceptPrizes(int playerId, boolean wantsRewards) {
        try{
            try{
                gameSession.getState().userAcceptPrizes(playerId, wantsRewards);
                CommandServer serObjectTemp = new CommandServer("userAcceptPrizesCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(wantsRewards);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                CommandServer serObjectTemp = new CommandServer("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Accept prizes isn't worked: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        }catch (RemoteException e){
            System.out.println("Error during the notification to the client in acceptPrizes: " + e.getMessage());
        }
    }

    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        try{
            try{
                gameSession.getState().userLandOnPlanet(playerId, isLanded, planetId);
                CommandServer serObjectTemp = new CommandServer("userLandOnPlanetCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(isLanded);
                serObjectTemp.addParameter(planetId);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                CommandServer serObjectTemp = new CommandServer("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Land on planet isn't worked: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        }catch (RemoteException e){
        System.out.println("Error during the notification to the client in landOnPlanet: " + e.getMessage());}
    }

    public void userLandOnStationOrShip(int playerId, boolean isLanded) {
        try{
            try{
                gameSession.getState().userLandOnStationOrShip(playerId, isLanded);
                CommandServer serObjectTemp = new CommandServer("userLandOnStationOrShipCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter(isLanded);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                CommandServer serObjectTemp = new CommandServer("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Land on station or ship isn't worked: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        }catch (RemoteException e){
            System.out.println("Error during the notification to the client in landOnStationOrShip: " + e.getMessage());
        }
    }

    public void userStopFlight(int playerId) {
    //????
    }
    //todo commentato, c'è da aggiungere userquitgame callback a commandclient
//    public void userQuitGame(int playerId) {
//        try{
//            try{
//                gameSession.getState().userQuitGame(playerId);
//                CommandServer serObjectTemp = new CommandServer("userQuitGameCallback", this, playerId);
//                serObjectTemp.addParameter(playerId);
//                serverSerializer.sendCommand(serObjectTemp);
//                serverRMI.sendCommand(serObjectTemp);
//            } catch (Exception e) {
//                CommandServer serObjectTemp = new CommandServer("errorCallback", this, playerId);
//                serObjectTemp.addParameter(playerId);
//                serObjectTemp.addParameter("Quit game isn't worked: " + e.getMessage());
//                serverSerializer.sendCommand(serObjectTemp);
//                serverRMI.sendCommand(serObjectTemp);
//            }
//        }catch (RemoteException e){
//            System.out.println("Error during the notification to the client in quitGame: " + e.getMessage());
//        }
//    }
    public void adminCalculateScore(int playerId) {
        try {
            try {
                gameSession.getState().adminCalculateScore(playerId);
                CommandServer serObjectTemp = new CommandServer("adminCalculateScoreCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                CommandServer serObjectTemp = new CommandServer("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("Calculate score isn't worked: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        }catch (RemoteException e){
            System.out.println("Error during the notification to the client in calculateScore: " + e.getMessage());
        }
    }

    public void userEndGame(int playerId) {
        try{
            try{
                gameSession.getState().userEndGame(playerId);
                CommandServer serObjectTemp = new CommandServer("userEndGameCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            } catch (Exception e) {
                CommandServer serObjectTemp = new CommandServer("errorCallback", this, playerId);
                serObjectTemp.addParameter(playerId);
                serObjectTemp.addParameter("End game isn't worked: " + e.getMessage());
                serverSerializer.sendCommand(serObjectTemp);
                serverRMI.sendCommand(serObjectTemp);
            }
        }catch (RemoteException e){
            System.out.println("Error during the notification to the client in endGame: " + e.getMessage());
        }
    }

    public void setServerRMI(ServerRMI serverRMI) {
        this.serverRMI= serverRMI;
    }
}
