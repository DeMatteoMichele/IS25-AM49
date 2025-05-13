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
    private RemoteClient remoteClient;

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

    public void serverTestConnection(int playerId) {
        System.out.println("server - trying to callback [ID:" + playerId + "] ...");
    }

    public void registerClientRMI(RemoteClient client) {

    }

    public void registerClientSocket(int playerId) {

    }

    public void adminSetup(int playerId, int numberOfPlayers, GameMode mode) {
        try {
            try {
                gameSession.getState().adminCreateGame(gameSession.getPlayer(playerId).get(),mode,numberOfPlayers);
                //serverSerializer.admin Todo manca callbak
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
                serverSerializer.playerJoinCallback(playerId,3);
            } catch (GameSettingsException e) {
                serverSerializer.playerErrorJoinCallback(playerId,e.getMessage());
            }
        } catch (RemoteException _) {
            //todo sus per RMI
        }

    }

    public void userDrawTile(int playerId, int tileId) {
            //gameSession.getState().userDrawTile(player, tileId);
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
    }


    public void userPickTile(int playerId, Point coordinates) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userPickTile(player, coordinates);
//        });
    }

    public void userDiscardTile(int playerId) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userDiscardTile(player);
//        });
    }

    public void userFinishShipboard(int playerId) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userFinishShipboard(player);
//        });
    }

    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userLookSmallAdventureCardDeck(player, smallDeckIndex);
//        });
    }

    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userStopLookSmallAdventureCardDeck(player, smallDeckIndex);
//        });
    }

    public void userPlaceTimer(int playerId) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userPlaceTimer(player);
//        });
    }

    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userSelectTypeOfToken(player, token, coordinates);
//        });
    }

    public void userThrowDices(int playerId) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userThrowDices(player);
//        });
    }

    public void userDrawAdventureCard(int playerId) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userDrawAdventureCard(player);
//        });
    }

    public void userFinishDecisionActivating(int playerId, HashSet<Point> coordinates) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userFinishDecisionActivating(player, coordinates);
//        });
    }
    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userFinishDecisionToken(player, listOfTokens);
//        });
    }

    public void userAcceptPrizes(int playerId, boolean wantsRewards) {

    }

    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userLandOnPlanet(player, isLanded, planetId);
//        });

    }

    public void userLandOnStationOrShip(int playerId, boolean isLanded) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userLandOnStationOrShip(player, isLanded);
//        });
    }

    public void userStopFlight(int playerId) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userStopFlight(player);
//        });

    }

    public void adminCalculateScore(int playerId) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().adminCalculateScore(player);
//        });
    }

    public void userEndGame(int playerId) {
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().userEndGame(player);
//        });
    }
}
