package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.client.RemoteServer;
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
import java.util.List;

public class ServerController {
    private final GameSession gameSession;
    private ServerSerializer serverSerializer;
    private final HashMap<Integer, Optional<Player>> players;
    public ServerController(GameSession gameSession) {
        this.gameSession = gameSession;
        this.players = new HashMap<>();
    }
    public void setSerializer(ServerSerializer serverSerializer) {
        this.serverSerializer = serverSerializer;
    }
    public void passInformationToClientHandler(String method, List<Object> parameters) {
        serverSerializer.serializeObjectFromServer(method, parameters);
    }

    public void serverTestConnection(Integer playerId) {
        System.out.println("server - trying to callback [ID:"+playerId+"] ...");
    }

    public void addPlayerToLobby(int playerId, Player player) {
        players.put(playerId, Optional.of(player));
    }


    public void serverTestConnection(int playerId) throws RemoteException {

    }


    public void registerClient(RemoteClient client, int playerId) throws RemoteException {
        players.put(players.size(), Optional.empty());

    }


    public void adminSetup(int playerId, int numberOfPlayers, GameMode mode) throws RemoteException {

    }


    public void playerJoin(int playerId, String username, Color color) throws RemoteException {

    }


    public void userDrawTile(int playerId, int tileId) throws RemoteException {

    }


    public void userPlaceTile(int playerId, Point coordinates, Orientation orientation) throws RemoteException {
        //devo cercare il player corrispondente in base al player id
        Optional<Player> playerOpt = players.get(playerId);
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            gameSession.getState().userPlaceTile(player, coordinates);
        } else {
            throw new IllegalArgumentException("Player con ID " + playerId + " non trovato");
        }
        //chiamare callback su remoteclient
    }


    public void userPickTile(int playerId, Point coordinates) throws RemoteException {
        //gameSession.getState().userPickTile(player,coordinates);
    }


    public void userDiscardTile(int playerId) throws RemoteException {

    }


    public void userFinishShipboard(int playerId) throws RemoteException {

    }


    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) throws RemoteException {

    }


    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) throws RemoteException {

    }


    public void userPlaceTimer(int playerId) throws RemoteException {

    }


    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) throws RemoteException {

    }


    public void userThrowDices(int playerId) throws RemoteException {

    }


    public void userDrawAdventureCard(int playerId) throws RemoteException {

    }


    public void userFinishDecisionActivating(int playerId, Set<Point> coordinates) throws RemoteException {

    }


    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) throws RemoteException {

    }


    public void userAcceptPrizes(int playerId, boolean wantsRewards) throws RemoteException {

    }


    public void userLandOnPlanet(int playerId, boolean isLanded, Planet planet) throws RemoteException {

    }


    public void userLandOnStationOrShip(int playerId, boolean isLanded) throws RemoteException {

    }


    public void userStopFlight(int playerId) throws RemoteException {

    }


    public void adminCalculateScore(int playerId) throws RemoteException {

    }


    public void userEndGame(int playerId) throws RemoteException {

    }
}
