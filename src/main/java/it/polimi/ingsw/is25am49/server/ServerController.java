package it.polimi.ingsw.is25am49.server;

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
            //gameSession.getState().userDrawTile(player, tileId);
        gameSession.getState().userDrawTile(playerId, tileId);
    }

    public void userPlaceTile(int playerId, Point coordinates, Orientation orientation) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userPlaceTile(player, coordinates);
//        });
//        try {
//            remoteClient.userPlaceTileCallback(playerId, coordinates,orientation);
//        } catch (RemoteException e) {
//            System.err.println("Error during the notification to the client: " + e.getMessage());
//        }
        gameSession.getState().userPlaceTile(playerId,coordinates);
    }


    public void userPickTile(int playerId, Point coordinates) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userPickTile(player, coordinates);
//        });
        gameSession.getState().userPickTile(playerId, coordinates);
    }

    public void userDiscardTile(int playerId) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userDiscardTile(player);
//        });
        gameSession.getState().userDiscardTile(playerId);
    }

    public void userFinishShipboard(int playerId) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userFinishShipboard(player);
//        });
        gameSession.getState().userFinishShipboard(playerId);
    }

    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userLookSmallAdventureCardDeck(player, smallDeckIndex);
//        });
        gameSession.getState().userLookSmallAdventureCardDeck(playerId, smallDeckIndex);
    }

    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userStopLookSmallAdventureCardDeck(player, smallDeckIndex);
//        });
        gameSession.getState().userStopLookSmallAdventureCardDeck(playerId, smallDeckIndex);
    }

    public void userPlaceTimer(int playerId) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userPlaceTimer(player);
//        });
        gameSession.getState().userPlaceTimer(playerId);
    }

    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userSelectTypeOfToken(player, token, coordinates);
//        });
        gameSession.getState().userSelectTypeOfToken(playerId, token, coordinates);
    }

    public void userThrowDices(int playerId) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userThrowDices(player);
//        });
        gameSession.getState().userThrowDices(playerId);
    }

    public void userDrawAdventureCard(int playerId) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userDrawAdventureCard(player);
//        });
        gameSession.getState().userDrawAdventureCard(playerId);
    }

    public void userFinishDecisionActivating(int playerId, HashSet<Point> coordinates) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userFinishDecisionActivating(player, coordinates);
//        });
        gameSession.getState().userFinishDecisionActivating(playerId, coordinates);
    }
    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userFinishDecisionToken(player, listOfTokens);
//        });
        gameSession.getState().userFinishDecisionToken(playerId, listOfTokens);
    }

    public void userAcceptPrizes(int playerId, boolean wantsRewards) {

    }

    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userLandOnPlanet(player, isLanded, planetId);
//        });
        gameSession.getState().userLandOnPlanet(playerId, isLanded, planetId);
    }

    public void userLandOnStationOrShip(int playerId, boolean isLanded) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userLandOnStationOrShip(player, isLanded);
//        });
        gameSession.getState().userLandOnStationOrShip(playerId, isLanded);
    }

    public void userStopFlight(int playerId) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userStopFlight(player);
//        });
        gameSession.getState().userStopFlight(playerId);
    }

    public void adminCalculateScore(int playerId) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().adminCalculateScore(player);
//        });
        gameSession.getState().adminCalculateScore(playerId);
    }

    public void userEndGame(int playerId) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userEndGame(player);
//        });
        gameSession.getState().userEndGame(playerId);
    }

    public void setServerRMI(ServerRMI serverRMI) {
        this.serverRMI= serverRMI;
    }
}
