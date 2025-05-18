package it.polimi.ingsw.is25am49.client.model;

import it.polimi.ingsw.is25am49.client.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

public class Game {
    private GameSession session;
    private GameMode mode;
    private final GameBoard board;
    private final ArrayList<Player> players;
    private Optional<Player> winner;
    private Timer timer;
    private int maxPlayers;
    private int numPlayers;
    private boolean isFinished;
    private int seedForGeneration;

    public Game(Player admin, GameSession session, GameMode mode, int maxPlayers, int seedForGeneration) {
        this.session = session;
        this.seedForGeneration = seedForGeneration;
        board = new GameBoard(seedForGeneration);
        board.setSeedForGeneration(seedForGeneration);
        players = new ArrayList<>();
        this.mode = mode;
        winner = Optional.empty();
        timer = null;
        isFinished = false;

        players.add(admin);
        numPlayers = 1;
        this.maxPlayers = Math.max(Math.min(4, maxPlayers), 2);
    }

    public GameMode getMode() {
        return mode;
    }

    public Player getPlayer(int id) throws IllegalArgumentException {
        for (Player p : players) {
            if (p.getIdPlayer() == id) return p;
        }
        throw new IllegalArgumentException("Player not found");
    }

    public GameBoard getGameboard() {
        return board;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int numOfPlayers() {
        return numPlayers;
    }

    public void setGameMode(GameMode mode) {
        this.mode = mode;
        timer = new Timer(mode, session);
    }

    public boolean checkPlayers() {
        return players.size() < maxPlayers;
    }

    public void addPlayer(Player player) {
        players.add(player);
        numPlayers++;
        if (numPlayers == maxPlayers) {
            session.configurateFlight();
        }
    }

    public ArrayList<Player> getListPlayers() {
        return players;
    }

    public void checkCalculateScore(Player player) {
        if (winner.isEmpty()) {
        } else throw new GameSettingsException("Final score already calculated");
    }

    public void calculateScore(Player player) {
        if (winner.isEmpty()) {
            board.calculateFinalCredits();
            winner = players.stream().max((p1, p2) -> Integer.compare(p1.getCredits(), p2.getCredits()));
        } else throw new GameSettingsException("Final score already calculated");
    }

    public void configurateFlight() {
        board.configurateFlight(mode, players);
        board.initialize(mode);
    }

    public boolean gameIsFinished() {
        return isFinished;
    }

    public Optional<Player> userShowWinner() {
        isFinished = true;
        return winner;
    }

    public boolean isTestFlight() {
        return mode == GameMode.FLIGHT_MODE;
    }

    public void userPlaceTimer(Player player) {
        if (!isTestFlight())
            timer.place(player);
        else
            throw new GameSettingsException("This is a Test Flight");
    }

    public void discardTile(Player player) {
        board.addDiscardedTile(player);
    }

    public void userDrawTile(Player player, int idTile) {
        if (player.getHandTile().isPresent()) {
            board.addDiscardedTile(player);
        }
        player.lockLastTile();
        player.setHandTile(board.drawTile(idTile, player));
    }

    public boolean userFinishShipboard(Player player) {
        return board.userFinishShipboard(player) == numPlayers;
    }

    public void userLookSmallAdventureCardDeck(Player player, int index) {
        board.userLookSmallAdventureCardDeck(player, index);
    }

    public void userStopLookSmallAdventureCardDeck(Player player, Integer index) {
        board.userStopLookSmallAdventureCardDeck(player, index);
    }

    public void addPlayersToLeaderBoard() {
        players.forEach(Player::completeShipboard);
        players.forEach(board::addPlayerToLeaderboard);
    }

    public void mergeSmallDecks() {
        board.mergeDecks();
    }

    public void userDrawAdventureCard(Player player) {
        board.drawAdventureCard(player);
    }

    public void userThrowDices(Player player) {
        board.userThrowDices(player);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("+----------------------------------------------\n");
        str.append("| LOBBY : Players - ").append(numOfPlayers());
        str.append("/").
                append(getMaxPlayers()).append(" \tMode: ").
                append(getMode()).append("\n");
        int i = 0;
        for (Player player : getListPlayers()) {
            str.append("| ").append(player.getIdPlayer()).append(")\t").
                    append("[").append(player.getColor()).append("]\t").
                    append(player.getUsername());
            if (i == 0) str.append(" - ADMIN\n");
            else str.append("\n");
            i++;
        }
        str.append("+----------------------------------------------");
        return str.toString();
    }


    public void userLandOnPlanet(boolean isLanded, int planetId) {
        board.getCurrentCard().landingOnPlanet(isLanded, planetId);
    }

    public void userFinishDecisionToken(ArrayList<Pair<Point, Token>> listOfTokens) {
        board.getCurrentCard().finishDecision(listOfTokens);
    }

    public void userLandOnStationOrShip(boolean isLanded) {
        board.getCurrentCard().landingOnStationOrShip(isLanded);
    }

    public void userFinishDecisionActivating(Set<Point> coordinates) {
        board.getCurrentCard().finishDecision(coordinates);
    }

    public void userAcceptPrizes(boolean isAccepted) {
        board.getCurrentCard().acceptPrizes(isAccepted);
    }

    public void userQuitGame(Player player) {
        board.userQuitGame(player);
    }

    public boolean userSelectTypeOfToken(Player player, Token token, Point coordinates) {
        if (player.getShipBoard().getTile(coordinates).getComponent().getType().equals(ComponentType.CABINE)) {
            if (token.equals(Token.ASTRONAUT)) {
                player.getShipBoard().fillTileComponent(coordinates, token);
            } else if (token.equals(Token.BROWN_ALIENS) || token.equals(Token.PURPLE_ALIENS)) {
                if (player.getShipBoard().checkIfAlienArePossible(coordinates).contains(token))
                    player.getShipBoard().addTokenToTile(coordinates, token);
            }
        }
        return checkIfShipboardsAreFullOfToken();
    }

    public boolean checkIfShipboardsAreFullOfToken() {
        return players.stream().allMatch(Player::shipBoardIsFullOfToken);
    }

    public void checkUserDrawAdventureCard(int playerId) throws GameSettingsException {
        Player player = getPlayer(playerId);
        board.checkDrawAdventureCard(player);
    }

    public void checkFinishDecisionActivatingStates() {
        board.getCurrentCard().checkFinishDecisionActivatingStates();
    }

    public void checkThrowDicesState() {
        board.getCurrentCard().checkThrowDicesState();
    }

    public void checkFinishDecisionActivatingCoordinates(Set<Point> coordinates) {
        board.getCurrentCard().checkFinishDecisionActivatingCoordinates(coordinates);
    }

    public void checkFinishDecisionTokenStates() {
        board.getCurrentCard().checkFinishDecisionTokenStates();
    }

    public void checkFinishDecisionTokenCoordinates(ArrayList<Pair<Point, Token>> listOfTokens) {
        board.getCurrentCard().checkFinishDecisionTokenCoordinates(listOfTokens);
    }

    public void checkLandOnPlanetState() {
        board.getCurrentCard().checkLandOnPlanetState();
    }

    public void checkLandOnStationOrShipState() {
        board.getCurrentCard().checkLandOnStationOrShipState();
    }

    public void checkAcceptPrizesState() {
        board.getCurrentCard().checkAcceptPrizesState();
    }
}
