package it.polimi.ingsw.is25am49.server.model;

import it.polimi.ingsw.is25am49.server.model.states.FinishScoreState;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import it.polimi.ingsw.is25am49.server.ServerController;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.server.model.states.BuildState;
import it.polimi.ingsw.is25am49.server.model.states.InitState;
import it.polimi.ingsw.is25am49.server.model.states.State;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalUsernameException;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

/**
 * A class that manages a game session with gameplay functionalities and player interactions.
 */
public class GameSession {
    private Optional<Game> game;
    private Optional<Player> admin;
    private State currentState;
    private final int seedForGeneration;
    private ServerController serverController;

    /**
     * Initializes a new instance of the GameSession class with default configurations.
     */
    public GameSession() {
        seedForGeneration = (int) System.currentTimeMillis();
        game = Optional.empty();
        admin = Optional.empty();
        currentState = new InitState(this);
    }

    /**
     * Retrieves the current game instance if available.
     *
     * @return an Optional containing the current Game, or an empty Optional if no game is set.
     */
    public Optional<Game> getGame() {
        return game;
    }

    /**
     * Retrieves the current state of the game session.
     *
     * @return the current State object representing the game's state.
     */
    public State getState() {
        return currentState;
    }

    /**
     * Retrieves the server controller associated with the game session.
     *
     * @return the instance of ServerController currently associated with this game session.
     */
    public ServerController getServerController() {
        return serverController;
    }

    /**
     * Sets the server controller for the game session.
     * @param serverController the ServerController to be associated with the game session
     */
    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }

    /**
     * Retrieves the current admin of the game session, if present.
     *
     * @return an Optional containing the admin Player if available, otherwise an empty Optional
     */
    public Optional<Player> getAdmin() {
        return admin;
    }

    /**
     * Retrieves a player by their unique identifier.
     *
     * @param id the unique identifier of the player to retrieve
     * @return an Optional containing the Player if found, otherwise an empty Optional
     */
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

    /**
     * Updates the current state of the game session.
     *
     * @param nextState the next state to transition to
     */
    public void setState(State nextState) {
        currentState = nextState;
    }

    /**
     * Returns the selected color based on the game's current state.
     *
     * @return the default color if no game exists, or the color corresponding to the number of players in the game
     */
    public Color getColor() {
        if (game.isEmpty()) {
            return Color.values()[0];
        } else return Color.values()[game.get().numOfPlayers()];
    }

    /**
     * Creates a new game session with the specified game mode and maximum number of players.
     *
     * @param gameMode the mode of the game to be created
     * @param maxPlayers the maximum number of players allowed in the game
     * @throws GameSettingsException if a game session already exists
     */
    public void createNewGame(GameMode gameMode, int maxPlayers) throws GameSettingsException {
        if (game.isEmpty()) {
            game = Optional.of(new Game(admin.get(), this, gameMode, maxPlayers, seedForGeneration));
            game.get().setTimer(new Timer(gameMode, this));
        } else throw new GameSettingsException("Game already exists!");
    }

    /**
     * Sets the game mode for the current game if present.
     *
     * @param mode the new game mode to be set
     */
    //probabilmente non serve
    public void setGameMode(GameMode mode) {
        game.ifPresent(g -> g.setGameMode(mode));
    }

    /**
     * Validates if the specified player is the admin of the game.
     *
     * @param playerId the ID of the player to check for admin rights
     * @throws GameSettingsException if there is no admin or if the player is not the admin
     */
    public void checkIsAdmin(int playerId) throws GameSettingsException {
        if (admin.isEmpty())
            throw new GameSettingsException("There is no admin");
        if (admin.get().getIdPlayer() != playerId)
            throw new GameSettingsException("You are not the admin of this game");
    }

    /**
     * Checks if all players meet the required conditions in the game.
     * @return true if all players meet the conditions, false otherwise.
     */
    public boolean checkPlayers() {
        return game.map(Game::checkPlayers).orElse(false);
    }

    /**
     * Checks if the current game mode is set to the default mode.
     *
     * @return true if the game mode is DEFAULT, false otherwise
     */
    public boolean checkGameMode() {
        return game.map(value -> value.getMode().equals(GameMode.DEFAULT)).orElse(false);
    }

    /**
     * Allows a user to join the game lobby as a player.
     *
     * @param username the username of the player attempting to join the lobby
     * @param playerID the unique identifier of the player attempting to join
     * @throws GameSettingsException if the user cannot join due to lobby conditions or restrictions
     */
    public void userJoinLobby(String username, int playerID) throws GameSettingsException {
        checkJoinLobby(playerID, username);
        Player tempPlayer = new Player(username, getColor(), playerID);
        if (admin.isEmpty()) {
            admin = Optional.of(tempPlayer);
        } else if (game.isPresent()) {
            if (checkPlayers()) {
                game.get().addPlayer(tempPlayer);
            } else throw new GameSettingsException("too many players");
        } else throw new GameSettingsException("Game is not ready yet");
    }

    /**
     * Validates if a player can join the lobby based on game and username conditions.
     *
     * @param playerId The unique identifier of the player attempting to join.
     * @param username The username of the player attempting to join.
     * @throws GameSettingsException If the game is not ready, the lobby is full, or the player has already joined.
     * @throws IllegalUsernameException If the provided username is already taken by another player.
     */
    public void checkJoinLobby(int playerId, String username) throws GameSettingsException {
        if (admin.isPresent()) {
            if (game.isPresent()) {
                if (checkPlayers()) {
                    if (checkIsInLobby(playerId)) throw new GameSettingsException("you already joined the lobby");
                    if (checkUsernameAlreadyTaken(username))
                        throw new IllegalUsernameException("username already taken");
                } else throw new GameSettingsException("too many players");
            } else throw new GameSettingsException("game is not ready yet");
        }
    }

    /**
     * Checks if the given username is already taken by any player in the game.
     *
     * @param username the username to check for availability
     * @return true if the username is already taken, false otherwise
     */
    private boolean checkUsernameAlreadyTaken(String username) {
        return game.map(g -> g.getListPlayers().stream().anyMatch(p -> p.getUsername().equals(username)))
                .orElse(false);
    }

    /**
     * Checks if the player is currently in the game lobby.
     *
     * @param playerId the unique identifier of the player to check
     * @return true if the player is in the lobby, false otherwise
     * @throws GameSettingsException if there is an issue accessing the game settings
     */
    public boolean checkIsInLobby(int playerId) throws GameSettingsException {
        return game.map(g -> g.getListPlayers().stream().anyMatch(p -> p.getIdPlayer() == playerId))
                .orElse(false);
    }

    /**
     * Allows the specified player to discard their currently held tile in the game.
     *
     * @param player the player discarding their tile
     */
    public void userDiscardTile(Player player) {
        game.ifPresent(g -> g.discardTile(player));
    }

    /**
     * Allows a player to draw a tile with a specific ID in the game.
     *
     * @param player the player who is drawing the tile
     * @param idTile the ID of the tile to be drawn
     */
    public void userDrawTile(Player player, int idTile) {
        game.ifPresent(g -> g.userDrawTile(player, idTile));
    }

    /**
     * Allows a player to finish their shipboard, completing their setup.
     *
     * @param player the player who has finished their shipboard
     * @return true if all players have completed their shipboards, false otherwise
     */
    public boolean userFinishShipboard(Player player) {
        return game.map(value -> value.userFinishShipboard(player)).orElse(false);
    }

    /**
     * Allows the specified player to view a small adventure card deck at the given index.
     *
     * @param player the player attempting to view the small adventure card deck.
     * @param index the index of the small adventure card deck to be viewed.
     * @throws GameSettingsException if viewing the deck is not allowed or invalid conditions occur.
     */
    public void userLookSmallAdventureCardDeck(Player player, int index) throws GameSettingsException {
        game.ifPresent(g -> g.userLookSmallAdventureCardDeck(player, index));
    }

    /**
     * Stops a player from viewing a small adventure card deck.
     *
     * @param player the player who is requesting to stop viewing the deck
     * @param index the identifier of the small adventure card deck
     * @throws GameSettingsException if the player is not viewing the specified deck or if the deck does not exist
     */
    public void userStopLookSmallAdventureCardDeck(Player player, int index) throws GameSettingsException {
        game.ifPresent(g -> g.userStopLookSmallAdventureCardDeck(player, index));
    }

    /**
     * Executes the userPlaceTimer operation for the given player within the current game session.
     *
     * @param player the player for whom the timer is being placed
     */
    public void userPlaceTimer(Player player) {
        game.ifPresent(g -> g.userPlaceTimer(player));
    }

    /**
     * Allows the specified player to roll the dices during their turn in the current game session.
     * @param player the player initiating the dice roll
     */
    public void userThrowDices(Player player) {
        game.ifPresent(g -> g.userThrowDices(player));
    }

    /**
     * Initiates the process for a player to draw an adventure card during the game session.
     *
     * @param player the player who is drawing an adventure card
     * @throws GameSettingsException if the operation fails due to invalid game settings
     */
    public void userDrawAdventureCard(Player player) throws GameSettingsException {
        game.ifPresent(g -> g.userDrawAdventureCard(player));
    }

    /**
     * Handles the action of a user landing on a specific planet in the game.
     *
     * @param isLanded Indicates if the user successfully landed on the planet.
     * @param planetId The ID of the planet the user is landing on.
     */
    public void userLandOnPlanet(boolean isLanded, int planetId) {
        game.ifPresent(g -> g.userLandOnPlanet(isLanded, planetId));
    }

    /**
     * Finalizes the token assignment decision process for the user.
     *
     * @param listOfTokens a list of pairs where each pair consists of a Point and its associated Token.
     */
    public void userFinishDecisionToken(ArrayList<Pair<Point, Token>> listOfTokens) {
        game.ifPresent(game -> game.userFinishDecisionToken(listOfTokens));
    }

    /**
     * Allows the user to land on a station or a ship within the game session.
     *
     * @param isLanded a boolean indicating whether the user has successfully landed (true) or not (false)
     */
    public void userLandOnStationOrShip(boolean isLanded) {
        game.ifPresent(game -> game.userLandOnStationOrShip(isLanded));
    }

    /**
     * Finalizes the user's decision using a provided set of coordinates.
     *
     * @param coordinates a set of points representing the user's decision inputs.
     */
    public void userFinishDecisionActivating(Set<Point> coordinates) {
        game.ifPresent(game -> game.userFinishDecisionActivating(coordinates));
    }

    /**
     * Allows the user to accept or decline the prizes in the current game session.
     *
     * @param isAccepted true if the user accepts the prizes, false otherwise.
     */
    public void userAcceptPrizes(boolean isAccepted) {
        game.ifPresent(game -> game.userAcceptPrizes(isAccepted));
    }


    /**
     * Calculates the score for a specific player in the current game session.
     *
     * @param playerId the unique identifier of the player for whom the score is calculated
     * @throws GameSettingsException if the game settings are invalid or the operation is not permitted
     */
    public void calculateScore(int playerId) throws GameSettingsException {
        //checkIsAdmin(playerId);
        game.ifPresent(g -> g.calculateScore(game.get().getPlayer(playerId)));
    }

    /**
     * Configures the flight phase of the game and transitions to the BuildState.
     */
    public void configurateFlight() {
        game.ifPresent(Game::configurateFlight);
        setState(new BuildState(this));
    }

    /**
     * Ends the game if it has been completed and resets the game state.
     *
     * @param playerId the ID of the player attempting to end the game
     * @throws GameSettingsException if the game is not finished yet
     */
    public void userEndGame(int playerId) throws GameSettingsException {
        if (game.map(Game::gameIsFinished).orElse(false)) {
            //checkIsAdmin(playerId);
            game = Optional.empty();
            setState(new InitState(this));
        } else
            throw new GameSettingsException("The game is not finished yet");
    }

    /**
     * Allows a player to select a specific type of token at a given coordinate.
     *
     * @param player the Player making the selection
     * @param token the Token type to be selected
     * @param coordinates the Point where the selection is made
     * @return true if the selection is successful, false otherwise
     */
    public boolean userSelectTypeOfToken(Player player, Token token, Point coordinates) {
        return game.map(game -> game.userSelectTypeOfToken(player, token, coordinates)).orElse(false);
    }

    /**
     * Adds all players of the current game to the leaderboard if a game is present.
     */
    public void addPlayersToLeaderBoard() {
        game.ifPresent(Game::addPlayersToLeaderBoard);
    }

    /**
     * Merges small adventure card decks in the game if present.
     */
    public void mergeSmallDecks() {
        game.ifPresent(Game::mergeSmallDecks);
    }

    /**
     * Retrieves the seed used for generation in the game session.
     * @return the seed for game generation in integer format
     */
    public int getSeedForGeneration() {
        return seedForGeneration;
    }

    /**
     * Notifies the server controller that the game has started.
     */
    public void signalToControllerThatGameStarted() {
        serverController.signalThatGameStarted();
    }

    /**
     * Stops the flight of the specified player in the current game session.
     *
     * @param player the player whose flight is to be stopped
     */
    public void userStopFlight(Player player) {
        game.ifPresent(g -> g.userStopFlight(player));
    }

    /**
     * Connects a player to the game using their unique identifier.
     *
     * @param playerId the unique identifier of the player to connect
     */
    public void connectPlayer(int playerId) {
        game.ifPresent(g -> g.connectPlayer(playerId));
    }

    /**
     * Disconnects a player from the current game session.
     *
     * @param playerId the ID of the player to disconnect
     */
    public void disconnectPlayer(int playerId) {
        game.ifPresent(g -> g.disconnectPlayer(playerId));
    }

    /**
     * Transitions the game session to the final score state.
     */
    public void finishGame() {
        setState(new FinishScoreState(this));
    }

    /**
     * Ends the game and transitions to the finish score state due to a player disconnection.
     */
    public void finishGameDueToDisconnection() {
        setState(new FinishScoreState(this));
        serverController.changeState("FinishScoreState");
    }
}
