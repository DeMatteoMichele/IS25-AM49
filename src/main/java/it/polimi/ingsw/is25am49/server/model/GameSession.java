package it.polimi.ingsw.is25am49.server.model;

import it.polimi.ingsw.is25am49.server.RemoteClient;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.server.model.states.InitState;
import it.polimi.ingsw.is25am49.server.model.states.State;

import java.awt.*;
import java.util.Optional;

public class GameSession {
    private Optional<Game> game;
    private Optional<Player> admin;
    private State currentState;

    public GameSession() {

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

    public Optional<Player> getAdmin() {
        return admin;
    }

    public Optional<Player> getPlayer(int id) { //TODO sus
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

    public void createNewGame(GameMode gameMode, int maxPlayers) {
        if (game.isEmpty()) {
            game = Optional.of(new Game(admin.get(), this, gameMode, maxPlayers));
        } else throw new GameSettingsException("Game already exists!");
    }

    //probabilmente non serve
    public void setGameMode(GameMode mode) {
        game.ifPresent(g -> g.setGameMode(mode));
    }

    public void checkIsAdmin(Player player) {
        if (admin.isEmpty())
            throw new GameSettingsException("There is no admin");
        if (!admin.get().equals(player))
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

    public void userJoinLobby(Color playerColor, String username, int playerID) {
        Player tempPlayer = new Player(username, playerColor, playerID);
        if (admin.isEmpty()) {
            admin = Optional.of(tempPlayer);
        } else if (game.isPresent()) {
            if (checkPlayers()) {
                game.get().addPlayer(tempPlayer);
            } else throw new GameSettingsException("too many players");
        } else throw new GameSettingsException("Game is not ready yet");
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

    public void userLookSmallAdventureCardDeck(Player player, int index) {
        game.ifPresent(g -> g.userLookSmallAdventureCardDeck(player, index));
    }

    public void userStopLookSmallAdventureCardDeck(Player player, int index) {
        game.ifPresent(g -> g.userStopLookSmallAdventureCardDeck(player, index));
    }

    public void userPlaceTimer(Player player) {
        game.ifPresent(g -> g.userPlaceTimer(player));
    }

    public void userThrowDices(Player player) {
        game.ifPresent(g -> g.userThrowDices(player));
    }

    public void userDrawAdventureCard(Player player) {
        game.ifPresent(g -> g.userDrawAdventureCard(player));
    }

    public void userPlayAdventureCard(Player player) {
    }

    public void userEffectDecision(Player player) {
    }

    public void userActivateComponent(Player player, Point coordinates) {
    }

    public void userActivateDaysUpdate() {
    }

    public void userQuitGame(Player player) {
    }

    public void calculateScore(Player player) {
        checkIsAdmin(player);
        game.ifPresent(g -> g.calculateScore(player));
    }

    public void configurateFlight() {
        game.ifPresent(Game::configurateFlight);
    }

    public void userEndGame(Player player) {
        if (game.map(Game::gameIsFinished).orElse(false)) {
            checkIsAdmin(player);
            game = Optional.empty();
            setState(new InitState(this));
        } else
            throw new GameSettingsException("The game is not finished yet");
    }

    public Optional<Player> userShowWinner(Player player) {
        checkIsAdmin(player);
        return game.flatMap(Game::userShowWinner);
    }

    public void addPlayersToLeaderBoard() {
        game.ifPresent(Game::addPlayersToLeaderBoard);
    }

    public void mergeSmallDecks() {
        game.ifPresent(Game::mergeSmallDecks);
    }
}
