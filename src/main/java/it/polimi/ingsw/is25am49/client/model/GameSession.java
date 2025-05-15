package it.polimi.ingsw.is25am49.client.model;

import it.polimi.ingsw.is25am49.client.UserInterface;
import it.polimi.ingsw.is25am49.client.model.enums.Color;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.client.model.states.InitState;
import it.polimi.ingsw.is25am49.client.model.states.State;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

public class GameSession {
    private final UserInterface ui;
    private Optional<Game> game;
    private Optional<Player> admin;
    private State currentState;
    private int seedForGeneration;

    public GameSession(UserInterface userInterface) {
        this.ui = userInterface;
        game = Optional.empty();
        admin = Optional.empty();
        currentState = new InitState(this, ui);
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

    public void createNewGame(GameMode gameMode, int maxPlayers) throws GameSettingsException {
        if (game.isEmpty()) {
            game = Optional.of(new Game(admin.get(), this, gameMode, maxPlayers,seedForGeneration));
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

    public void userJoinLobby(Color playerColor, String username, int playerID) throws GameSettingsException {
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
        game.ifPresent(game->game.userQuitGame(player));
    }

    public void calculateScore(int playerId) {
        checkIsAdmin(playerId);
        game.ifPresent(g -> g.calculateScore(game.get().getPlayer(playerId)));
    }

    public void configurateFlight() {
        game.ifPresent(Game::configurateFlight);
    }

    public void userEndGame(int playerId) throws GameSettingsException {
        if (game.map(Game::gameIsFinished).orElse(false)) {
            checkIsAdmin(playerId);
            game = Optional.empty();
            setState(new InitState(this, ui));
        } else
            throw new GameSettingsException("The game is not finished yet");
    }

    public Optional<Player> userShowWinner(int playerId) {
        checkIsAdmin(playerId);
        return game.flatMap(Game::userShowWinner);
    }

    public void addPlayersToLeaderBoard() {
        game.ifPresent(Game::addPlayersToLeaderBoard);
    }

    public void mergeSmallDecks() {
        game.ifPresent(Game::mergeSmallDecks);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("+-----------------------------------------------\n");
        str.append("| Aviable lobby:\n");
        if (getGame().isPresent()) {
            str.append("| 01) TestMatch - users ").append(getGame().get().numOfPlayers()).
                    append("/").append(getGame().get().getMaxPlayers());
            if (!checkPlayers()) str.append(" - full\n");
        } else if (getAdmin().isPresent()) str.append("| 01) TestMatch - users 1/4\n");
        else str.append("| 01) TestMatch - users 0/4 - CREATE LOBBY\n");
        str.append("+-----------------------------------------------\n");

        return str.toString();
    }

    public void setSeedForGeneration(int seedForGeneration) {
        this.seedForGeneration = seedForGeneration;
    }


    public void userLandOnPlanet(boolean isLanded, int planetId) {
        game.ifPresent(g -> g.userLandOnPlanet(isLanded, planetId));
    }

    public void userFinishDecisionToken(ArrayList<Pair<Point, Token>> listOfTokens){
        game.ifPresent(game-> game.userFinishDecisionToken(listOfTokens));
    }

    public void userLandOnStationOrShip(boolean isLanded){
        game.ifPresent(game->game.userLandOnStationOrShip(isLanded));
    }
    public void userFinishDecisionActivating(Set<Point> coordinates){
        game.ifPresent(game->game.userFinishDecisionActivating(coordinates));
    }
}