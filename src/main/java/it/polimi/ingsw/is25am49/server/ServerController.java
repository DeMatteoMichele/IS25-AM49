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
//        players.get(playerId).ifPresent(player -> {
//            gameSession.getState().adminSelectGameMode(player, mode);
//            gameSession.getState().adminCreateGame(player);
//            gameSession.getState().adminStartGame(player);
//        });
    }

    public void playerJoin(int playerId, String username, Color color) {
        addPlayerToLobby(playerId);
        try {
            gameSession.getState().userJoinLobby(color, username, playerId);
        } catch (GameSettingsException e) {
            serverSerializer.playerErrorJoinCallback();
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

    }

    public void userDiscardTile(int playerId) {

    }

    public void userFinishShipboard(int playerId) {

    }

    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {

    }

    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {

    }

    public void userPlaceTimer(int playerId) {

    }

    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) {

    }

    public void userThrowDices(int playerId) {

    }

    public void userDrawAdventureCard(int playerId) {

    }

    public void userFinishDecisionActivating(int playerId, HashSet<Point> coordinates) {

    }
    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {

    }

    public void userAcceptPrizes(int playerId, boolean wantsRewards) {

    }

    public void userLandOnPlanet(int playerId, boolean isLanded, Planet planet) {

    }

    public void userLandOnStationOrShip(int playerId, boolean isLanded) {
    }

    public void userStopFlight(int playerId) {

    }

    public void adminCalculateScore(int playerId) {

    }

    public void userEndGame(int playerId) {

    }
}
