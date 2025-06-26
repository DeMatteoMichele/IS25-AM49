package it.polimi.ingsw.is25am49.server.model;

import it.polimi.ingsw.is25am49.server.model.exceptions.NotEnoughPlayerException;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
import javafx.util.Pair;

import java.awt.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Represents a game session involving players, a game board, and various gameplay functionalities.
 * This class manages the core logic for initializing, managing players, tracking game progress,
 * and handling various aspects of the game flow.
 */
public class Game {
    /**
     * Represents the current game session associated with the game.
     * Manages the ongoing state and flow of gameplay.
     */
    private GameSession session;
    /**
     * Represents the current mode of the game.
     * Determines the behavior and rules applied during gameplay.
     */
    private GameMode mode;
    /**
     * The game board used to represent the state and layout of the current game.
     * Immutable and shared among game operations.
     */
    private final GameBoard board;
    /**
     * Stores the list of players currently participating in the game.
     * This list is managed and modified only through the methods of the Game class.
     * The players are represented as instances of the Player class.
     */
    private final ArrayList<Player> players;
    /**
     * Represents the winner of the game, if the game has concluded.
     * The value is an Optional containing the Player who won, or empty if the game
     * has not concluded or there is no winner.
     */
    private Optional<Player> winner;
    /**
     * Represents a timer used within the game to manage time-based actions.
     * Controls timing for player moves and triggers specific actions
     * when the timer expires.
     */
    private Timer timer;
    /**
     * Represents the maximum number of players allowed in a game.
     */
    private int maxPlayers;
    /**
     * Represents the current number of players in the game.
     * Used to track the total count of connected players at any given moment.
     */
    private int numPlayers;
    /**
     * Indicates whether the current game session is finished.
     * Used to track and manage the state of the game lifecycle.
     */
    private boolean isFinished;
    /**
     * Represents the seed used for random number generation in the game.
     * Used to ensure reproducibility or variation in gameplay scenarios.
     */
    private int seedForGeneration;
    /**
     * Scheduler used to manage timed operations within the game.
     * Handles scheduling tasks for various game-related timers.
     */
    private final ScheduledExecutorService scheduler;


    /**
     * Creates a new game instance with the specified admin player, session, game mode, maximum number of players,
     * and a seed for board generation.
     *
     * @param admin The Player who is the administrator of the game.
     * @param session The GameSession associated with this game.
     * @param mode The GameMode to be used for the game setup.
     * @param maxPlayers The maximum number of players allowed in the game, adjusted to a minimum of 2 and a maximum of 4.
     * @param seedForGeneration The seed for random generation processes in the game, such as board setup.
     */
    public Game(Player admin, GameSession session, GameMode mode, int maxPlayers, int seedForGeneration) {
        this.session = session;
        board = new GameBoard(seedForGeneration);
        board.setGame(this);
        players = new ArrayList<>();
        this.mode = mode;
        winner = Optional.empty();
        timer = null;
        isFinished = false;
        scheduler= Executors.newScheduledThreadPool(1);
        players.add(admin);
        numPlayers = 1;
        this.maxPlayers = Math.max(Math.min(4, maxPlayers), 2);
    }

    /**
     * Retrieves the current game mode of the game.
     *
     * @return the current GameMode set for the game
     */
    public GameMode getMode() {
        return mode;
    }

    /**
     * Retrieves a Player object with the specified ID.
     *
     * @param id the unique identifier of the player to retrieve
     * @return the Player object corresponding to the given ID
     * @throws IllegalArgumentException if no player with the specified ID is found
     */
    public Player getPlayer(int id) throws IllegalArgumentException {
        for (Player p : players) {
            if (p.getIdPlayer() == id) return p;
        }
        throw new IllegalArgumentException("Player not found");
    }

    /**
     * Disconnects the specified player from the game.
     * Updates the player's connection flag, handles leaderboard disconnection,
     * and schedules game termination if minimum player requirements are not met.
     *
     * @param playerId the ID of the player to disconnect
     * @throws IllegalArgumentException if the player with the given ID is not found
     */
    public void disconnectPlayer(int playerId) {
        Player player;
        try {
            player = getPlayer(playerId);
            player.setConnectionFlag(false);

            player.getShipBoard().disconnectedWhileChoosingAliens();
            try {
                session.getState().userFinishShipboard(playerId);
            } catch (Exception _) {}

            try {
                board.getLeaderBoard().disconnectPlayer(player);
            } catch (NotEnoughPlayerException e) {
//                scheduler.schedule(() -> {
//                    if (board.getLeaderBoard().size()- board.getLeaderBoard().sizeOfDisconnected() <= 1) {
//                        finishGameDueToDisconnection();
//                    }
//                }, 30, java.util.concurrent.TimeUnit.SECONDS);
            }
            board.disconnectionWarningToCard(player);
        } catch (IllegalArgumentException e) {
            //nothing to do if player not found
        }
    }

    /**
     * Connects a player to the game by their ID. Updates the player's connection status
     * and integrates them into the leaderboard.
     *
     * @param playerId the unique identifier of the player to be connected
     */
    public void connectPlayer(int playerId) {
        Player player;
        try {
            player = getPlayer(playerId);
            player.setConnectionFlag(true);
            board.getLeaderBoard().connectPlayer(player);
        } catch (IllegalArgumentException e) {
            //nothing to do if player not found
        }
    }

    /**
     * Retrieves the current game board used in the game.
     *
     * @return the current GameBoard instance representing the active game board.
     */
    public GameBoard getGameboard() {
        return board;
    }

    /**
     * Retrieves the maximum number of players allowed in the game.
     *
     * @return the maximum number of players.
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Returns the current number of players in the game.
     *
     * @return the number of players currently connected to the game
     */
    public int numOfPlayers() {
        return numPlayers;
    }

    /**
     * Sets the game mode for the current game instance and updates the timer accordingly.
     *
     * @param mode the new game mode to be set; determines the game's behavior and corresponding timer configuration
     */
    public void setGameMode(GameMode mode) {
        this.mode = mode;
        timer = new Timer(mode, session);
    }

    /**
     * Updates the current timer for the game.
     *
     * @param timer the Timer object to set, used to manage time-related functionality in the game.
     */
    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    /**
     * Checks if the current number of players is less than the maximum allowed.
     *
     * @return true if the current number of players is less than the maximum, otherwise false.
     */
    public boolean checkPlayers() {
        return numPlayers < maxPlayers;
    }

    /**
     * Adds a player to the game. Configures the flight and signals the game start
     * if the maximum number of players is reached.
     *
     * @param player the player to be added to the game
     */
    public void addPlayer(Player player) {
        players.add(player);
        numPlayers++;
        if (numPlayers == maxPlayers) {
            session.configurateFlight();
            session.signalToControllerThatGameStarted();
        }
    }

    /**
     * Retrieves the list of players currently in the game.
     *
     * @return an ArrayList containing all Player objects participating in the game
     */
    public ArrayList<Player> getListPlayers() {
        return players;
    }

    /**
     * Calculates and determines the final score of the winner based on players' credits.
     * Throws an exception if the final score has already been calculated.
     *
     * @param player The player for whom the score calculation is performed.
     * @throws GameSettingsException If the final score is already calculated.
     */
    public void calculateScore(Player player) throws GameSettingsException {
        if (winner.isEmpty()) {
            board.calculateFinalCredits();
            winner = players.stream().max((p1, p2) -> Integer.compare(p1.getCredits(), p2.getCredits()));
        } else throw new GameSettingsException("Final score already calculated");
    }

    /**
     * Configures the flight settings for the game by setting up the players'
     * ship boards and initializing the game board based on the specified game mode.
     *
     * @throws GameSettingsException if the game mode is invalid.
     */
    public void configurateFlight() throws GameSettingsException {
        board.configurateFlight(mode, players);
        board.initialize(mode);
    }

    /**
     * Checks if the game has finished.
     *
     * @return true if the game is finished, false otherwise.
     */
    public boolean gameIsFinished() {
        return isFinished;
    }

    /**
     * Marks the game as finished and updates the session state to reflect the game's conclusion.
     * This method sets the `isFinished` flag to true and invokes the `finishGame` method on the session.
     */
    public void finishGame() {
        isFinished = true;
        session.finishGame();
    }
    /**
     * Ends the game due to a player's disconnection.
     * Sets the game as finished and delegates additional
     * disconnection handling to the session.
     */
    public void finishGameDueToDisconnection() {
        isFinished = true;
        session.finishGameDueToDisconnection();
    }

    /**
     * Determines if the current game mode is set to FLIGHT_MODE.
     *
     * @return true if the game mode is FLIGHT_MODE, false otherwise.
     */
    public boolean isTestFlight() {
        return mode == GameMode.FLIGHT_MODE;
    }

    /**
     * Places a timer for a given player unless the game is in test flight mode.
     * If the game is in test flight mode, an exception is thrown.
     *
     * @param player the player for whom the timer is being placed
     * @throws GameSettingsException if the game is in test flight mode
     */
    public void userPlaceTimer(Player player) throws GameSettingsException {
        if (!isTestFlight())
            timer.place(player);
        else
            throw new GameSettingsException("This is a Test Flight");
    }

    /**
     * Discards the tile currently held by the specified player and updates the game board.
     *
     * @param player the player discarding their held tile
     */
    public void discardTile(Player player) {
        board.addDiscardedTile(player);
    }

    /**
     * Allows a player to draw a tile by its ID. If the player already has a tile in hand,
     * the tile is discarded. The player's last placed tile might also be locked based on conditions.
     *
     * @param player the player performing the tile draw
     * @param idTile the ID of the tile to be drawn
     */
    public void userDrawTile(Player player, int idTile) {
        if (player.getHandTile().isPresent()) {
            board.addDiscardedTile(player);
        }
        if (!player.getShipBoard().getLastPlacedPoint().equals(new Point(9, 5)) && !player.getShipBoard().getLastPlacedPoint().equals(new Point(10, 5))) {
            player.lockLastTile();
        }
        player.setHandTile(board.drawTile(idTile, player));
    }

    /**
     * Handles the completion of a player's shipboard and checks if all players have completed theirs.
     *
     * @param player the player who has finished their shipboard
     * @return true if the number of players who completed their shipboards equals the total number of players, false otherwise
     */
    public boolean userFinishShipboard(Player player) {
        return board.userFinishShipboard(player) == numPlayers;
    }

    /**
     * Allows a player to view a small adventure card deck at the specified index.
     *
     * @param player the player attempting to view the small adventure card deck.
     * @param index the index of the small adventure card deck to be viewed.
     * @throws GameSettingsException if the deck is already being watched or the player is watching another deck.
     */
    public void userLookSmallAdventureCardDeck(Player player, int index) throws GameSettingsException {
        board.userLookSmallAdventureCardDeck(player, index);
    }

    /**
     * Stops a player from viewing a small adventure card deck.
     *
     * @param player the player requesting to stop viewing the deck
     * @param index the identifier of the small adventure card deck
     * @throws GameSettingsException if the player is not watching the specified deck or the deck does not exist
     */
    public void userStopLookSmallAdventureCardDeck(Player player, Integer index) throws GameSettingsException {
        board.userStopLookSmallAdventureCardDeck(player, index);
    }

    /**
     * Adds all players to the leaderboard after completing their shipboards.
     *
     * Each player's shipboard is marked as complete, and then the player is added
     * to the game leaderboard.
     */
    public void addPlayersToLeaderBoard() {
        players.forEach(Player::completeShipboard);
        players.forEach(board::addPlayerToLeaderboard);
    }

    /**
     * Allows a user to perform a landing action on a specified planet within the game.
     * This method delegates the landing logic to the current card of the game board.
     *
     * @param isLanded Specifies whether the user successfully landed on the planet.
     * @param planetId The unique identifier of the planet being landed on.
     */
    public void userLandOnPlanet(boolean isLanded, int planetId) {
        board.getCurrentCard().landingOnPlanet(isLanded, planetId);
    }

    /**
     * Finalizes the token assignment decision process by validating and applying
     * the provided coordinates and their associated tokens on the current card.
     *
     * @param listOfTokens a list of pairs where each pair contains a Point representing
     *                     coordinates and a Token associated with that point.
     */
    public void userFinishDecisionToken(ArrayList<Pair<Point, Token>> listOfTokens) {
        board.getCurrentCard().finishDecision(listOfTokens);
    }

    /**
     * Allows the user to perform a landing action on either a station or a ship.
     *
     * @param isLanded a boolean indicating if the user has successfully landed (true) or not (false)
     */
    public void userLandOnStationOrShip(boolean isLanded) {
        board.getCurrentCard().landingOnStationOrShip(isLanded);
    }

    /**
     * Allows the user to decide whether to accept or decline the prizes/rewards
     * associated with the current card in the game.
     *
     * @param isAccepted true if the user chooses to accept the prizes; false to decline.
     */
    public void userAcceptPrizes(boolean isAccepted) {
        board.getCurrentCard().acceptPrizes(isAccepted);
    }

    /**
     * Triggers the finalization of a user's decision-making process
     * using the provided set of coordinates.
     *
     * @param coordinates a set of points representing the inputs
     *                    required to complete the decision.
     */
    public void userFinishDecisionActivating(Set<Point> coordinates) {
        board.getCurrentCard().finishDecision(coordinates);
    }


    /**
     * Stops the specified player's flight. The player's flight status is updated based on the current state of the game.
     *
     * @param player the player whose flight is to be stopped
     */
    public void userStopFlight(Player player) {
        board.userStopFlight(player);
    }

    /**
     * Merges all small adventure decks into the main adventure deck and shuffles the main deck.
     * This prepares the game's main deck for consistent card distribution.
     */
    public void mergeSmallDecks() {
        board.mergeDecks();
    }

    /**
     * Allows the specified player to draw an adventure card during the game.
     *
     * @param player the player who is drawing the adventure card
     * @throws GameSettingsException if the operation violates game settings or rules
     */
    public void userDrawAdventureCard(Player player) throws GameSettingsException {
        board.drawAdventureCard(player);
    }

    /**
     * Allows the specified player to roll the dices during their turn.
     * Delegates the action to the game board's dice-rolling mechanism.
     *
     * @param player the player initiating the dice roll
     */
    public void userThrowDices(Player player) {
        board.userThrowDices(player);
    }

    /**
     * Handles the selection and placement of a specific type of token on the player's shipboard
     * based on the given coordinates and token type. Verifies placement validity and updates the
     * shipboard accordingly.
     *
     * @param player      the player performing the token placement
     * @param token       the type of token to be placed
     * @param coordinates the coordinates where the token will be placed on the shipboard
     * @return true if all shipboard cabins are filled with tokens, false otherwise
     * @throws GameSettingsException if the placement is invalid or violates game rules
     */
    public boolean userSelectTypeOfToken(Player player, Token token, Point coordinates) {
        if (player.getShipBoard().getTile(coordinates).getComponentType().equals(ComponentType.CABINE)) {
            if (token.equals(Token.ASTRONAUT)) {
                player.getShipBoard().fillTileComponent(coordinates, token);
            } else if (token.equals(Token.BROWN_ALIENS) || token.equals(Token.PURPLE_ALIENS)) {
                if (player.getShipBoard().checkIfAlienArePossible(coordinates).contains(token))
                    player.getShipBoard().addTokenToTile(coordinates, token);
                else throw new GameSettingsException("Token not allowed here");
            } else throw new GameSettingsException("Token not allowed");
        } else throw new GameSettingsException("This is not a cabin");
        return checkIfShipboardsAreFullOfToken();
    }

    /**
     * Checks if all players' shipboards are completely filled with tokens.
     *
     * @return true if every player's shipboard is full of tokens, false otherwise.
     */
    public boolean checkIfShipboardsAreFullOfToken() {
        return players.stream().allMatch(Player::shipBoardIsFullOfToken);
    }

    public Timer getTimer() {
        return timer;
    }
}
