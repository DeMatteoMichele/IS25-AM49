package it.polimi.ingsw.is25am49.server.model;

import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalUsernameException;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.ServerController;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.server.model.states.BuildState;
import it.polimi.ingsw.is25am49.server.model.states.InitState;
import it.polimi.ingsw.is25am49.server.model.states.State;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

public class GameSession {
    private Optional<Game> game;
    private Optional<Player> admin;
    private State currentState;
    private final int seedForGeneration;
    private ServerController serverController;

    public GameSession() {
        seedForGeneration = (int) System.currentTimeMillis();
        game = Optional.empty();
        admin = Optional.empty();
        currentState = new InitState(this);
    }

    public Optional<Game> getGame() {
        return game;
    }

    public State getState() {
        return currentState;
    }

    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }

    public Optional<Player> getAdmin() {
        return admin;
    }

    public Optional<Player> getPlayer(int id) {
        if (game.isEmpty()) {
            if (admin.isPresent() && admin.get().getIdPlayer() == id) return admin;
        } else try {
            return Optional.of(game.get().getPlayer(id));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    public void setState(State nextState) {
        currentState = nextState;
    }

    public Color getColor() {
        if (game.isEmpty()) {
            return Color.values()[0];
        } else return Color.values()[game.get().numOfPlayers()];
    }

    public void createNewGame(GameMode gameMode, int maxPlayers) throws GameSettingsException {
        if (game.isEmpty()) {
            game = Optional.of(new Game(admin.get(), this, gameMode, maxPlayers, seedForGeneration));
            game.get().setTimer(new Timer(gameMode, this));
        } else throw new GameSettingsException("Game already exists!");
    }

    //probabilmente non serve
    public void setGameMode(GameMode mode) {
        game.ifPresent(g -> g.setGameMode(mode));
    }

    public void checkIsAdmin(int playerId) throws GameSettingsException {
        if (admin.isEmpty())
            throw new GameSettingsException("There is no admin");
        if (admin.get().getIdPlayer() != playerId)
            throw new GameSettingsException("You are not the admin of this game");
    }

    public boolean checkPlayers() {
        return game.map(Game::checkPlayers).orElse(false);
    }

    /**
     * method used to control that the admin has set the GameMode
     *
     * @return returns true only if the mode is not DEFAULT
     */
    public boolean checkGameMode() {
        return game.map(value -> value.getMode().equals(GameMode.DEFAULT)).orElse(false);
    }

    public void userJoinLobby(String username, int playerID) throws GameSettingsException {
        checkJoinLobby(playerID,username);
        Player tempPlayer = new Player(username, getColor(), playerID);
        if (admin.isEmpty()) {
            admin = Optional.of(tempPlayer);
        } else if (game.isPresent()) {
            if (checkPlayers()) {
                game.get().addPlayer(tempPlayer);
            } else throw new GameSettingsException("too many players");
        } else throw new GameSettingsException("Game is not ready yet");
    }

    public void checkJoinLobby(int playerId,String username) throws GameSettingsException {
        if (admin.isPresent()) {
            if (game.isPresent()) {
                if (checkPlayers()) {
                    if(checkIsInLobby(playerId))throw new GameSettingsException("you already joined the lobby");
                    if(checkUsernameAlreadyTaken(username))throw new IllegalUsernameException("username already taken");
                } else throw new GameSettingsException("too many players");
            }else throw new GameSettingsException("game is not ready yet");
        }
    }

    private boolean checkUsernameAlreadyTaken(String username) {
        return game.map(g -> g.getListPlayers().stream().anyMatch(p -> p.getUsername().equals(username)))
                .orElse(false);
    }

    public boolean checkIsInLobby(int playerId) throws GameSettingsException {
        return game.map(g -> g.getListPlayers().stream().anyMatch(p -> p.getIdPlayer() == playerId))
                .orElse(false);
    }

    public void userDiscardTile(Player player) {
        game.ifPresent(g -> g.discardTile(player));
    }

    public void userDrawTile(Player player, int idTile) {
        game.ifPresent(g -> g.userDrawTile(player, idTile));
    }

    public boolean userFinishShipboard(Player player) {
        return game.map(value -> value.userFinishShipboard(player)).orElse(false);
    }

    public void userLookSmallAdventureCardDeck(Player player, int index) throws GameSettingsException {
        game.ifPresent(g -> g.userLookSmallAdventureCardDeck(player, index));
    }

    public void userStopLookSmallAdventureCardDeck(Player player, int index) throws GameSettingsException {
        game.ifPresent(g -> g.userStopLookSmallAdventureCardDeck(player, index));
    }

    public void userPlaceTimer(Player player) {
        game.ifPresent(g -> g.userPlaceTimer(player));
    }

    public void userThrowDices(Player player) {
        game.ifPresent(g -> g.userThrowDices(player));
    }

    public void userDrawAdventureCard(Player player) throws GameSettingsException {
        game.ifPresent(g -> g.userDrawAdventureCard(player));
    }

    public void userLandOnPlanet(boolean isLanded, int planetId) {
        game.ifPresent(g -> g.userLandOnPlanet(isLanded, planetId));
    }

    public void userFinishDecisionToken(ArrayList<Pair<Point, Token>> listOfTokens) {
        game.ifPresent(game -> game.userFinishDecisionToken(listOfTokens));
    }

    public void userLandOnStationOrShip(boolean isLanded) {
        game.ifPresent(game -> game.userLandOnStationOrShip(isLanded));
    }

    public void userFinishDecisionActivating(Set<Point> coordinates) {
        game.ifPresent(game -> game.userFinishDecisionActivating(coordinates));
    }

    public void userAcceptPrizes(boolean isAccepted) {
        game.ifPresent(game -> game.userAcceptPrizes(isAccepted));
    }

    public void userQuitGame(Player player) {
        game.ifPresent(game -> game.userQuitGame(player));
    }

    public void calculateScore(int playerId) throws GameSettingsException {
        checkIsAdmin(playerId);
        game.ifPresent(g -> g.calculateScore(game.get().getPlayer(playerId)));
    }

    public void configurateFlight() {
        game.ifPresent(Game::configurateFlight);
        setState(new BuildState(this));
    }

    public void userEndGame(int playerId) throws GameSettingsException {
        if (game.map(Game::gameIsFinished).orElse(false)) {
            checkIsAdmin(playerId);
            game = Optional.empty();
            setState(new InitState(this));
        } else
            throw new GameSettingsException("The game is not finished yet");
    }

    public Optional<Player> userShowWinner(int playerId) throws GameSettingsException {
        checkIsAdmin(playerId);
        return game.flatMap(Game::userShowWinner);
    }

    public boolean userSelectTypeOfToken(Player player, Token token, Point coordinates) {
        return game.map(game -> game.userSelectTypeOfToken(player, token, coordinates)).orElse(false);
    }

    public void addPlayersToLeaderBoard() {
        game.ifPresent(Game::addPlayersToLeaderBoard);
    }

    public void mergeSmallDecks() {
        game.ifPresent(Game::mergeSmallDecks);
    }

    public int getSeedForGeneration() {
        return seedForGeneration;
    }

    public void signalToControllerThatGameStarted() {
        serverController.signalThatGameStarted();
    }
    public void userStopFlight(Player player) {
        game.ifPresent(g -> g.userStopFlight(player));
    }

}
