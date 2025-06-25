package it.polimi.ingsw.is25am49.server.model.states;

import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Orientation;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

/**
 * Represents the state of the game when the scoring phase is completed.
 * Only specific actions are permitted in this state, while most user and admin actions
 * will throw an IllegalMethodOnThisStateException.
 * Extends the abstract base class State.
 */
public class FinishScoreState extends State {
    /**
     * Constructs a new FinishScoreState.
     *
     * @param gameSession the current game session associated with this state
     */
    public FinishScoreState(GameSession gameSession){
        super(gameSession);
    }
    /**
     * Handles the selection of a game mode by an administrator.
     * This method is not allowed in the current state and will throw an exception.
     *
     * @param playerId The ID of the player attempting the action.
     * @param gamemode The game mode selected by the administrator.
     * @throws IllegalMethodOnThisStateException if the method is called in an invalid state.
     */
    public void adminSelectGameMode(int playerId,GameMode gamemode) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    /**
     * Throws an exception as this method is not allowed in the current state.
     *
     * @param username the username of the player attempting to join the lobby
     * @param playerId the unique identifier of the player attempting to join the lobby
     * @throws IllegalMethodOnThisStateException if the method is called in this state
     */
    public void userJoinLobby(String username, int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }

    /**
     * Connects a player to the current game session.
     *
     * @param playerId the unique identifier of the player to be connected
     */
    @Override
    public void connectPlayer(int playerId) {
        gameSession.connectPlayer(playerId);
    }

    /**
     * Disconnects the player with the specified ID from the game session.
     *
     * @param playerId the unique identifier of the player to disconnect
     */
    @Override
    public void disconnectPlayer(int playerId) {
        gameSession.disconnectPlayer(playerId);
    }

    /**
     * Handles the creation of a new game by an admin. Throws an exception if invoked
     * in the current state as game creation is not allowed.
     *
     * @param playerId the ID of the admin attempting to create the game
     * @param gamemode the game mode to be set for the new game
     * @param maxPlayers the maximum number of players allowed in the game
     * @throws IllegalMethodOnThisStateException if called in an invalid state
     */
    public void adminCreateGame(int playerId, GameMode gamemode, int maxPlayers) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    /**
     * Handles the action of a player attempting to place a tile.
     * Not allowed in the current state and throws an exception.
     *
     * @param playerId the unique identifier of the player performing the action
     * @param coordinates the coordinates where the tile is attempted to be placed
     * @throws IllegalMethodOnThisStateException if the method is called in an invalid state
     */
    public void userPlaceTile(int playerId, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    /**
     * Handles a user's request to rotate a tile in the game. This method is not
     * allowed to be executed in the current game state and will always throw an exception.
     *
     * @param playerId the ID of the player attempting the action
     * @param orientation the desired orientation for the tile
     * @throws GameSettingsException always thrown due to the invalid state for this action
     */
    public void userRotateTile(int playerId, Orientation orientation) throws GameSettingsException {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    /**
     * Handles the action of a player trying to pick a tile during the game.
     * This action is not allowed in the current state and will throw an exception.
     *
     * @param playerId the unique identifier of the player attempting the action
     * @param coordinates the coordinates of the tile the player is trying to pick
     * @throws IllegalMethodOnThisStateException if the method is invoked in an invalid state
     */
    public void userPickTile(int playerId, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    /**
     * Handles the user action of discarding a tile during the game.
     * Not allowed in the current game state.
     *
     * @param playerId the ID of the player attempting to discard a tile
     * @throws IllegalMethodOnThisStateException if the method is invoked in an invalid state
     */
    public void userDiscardTile(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    /**
     * Handles the action of a user attempting to draw a tile. This method is not valid
     * in the current game state and will throw an exception if invoked.
     *
     * @param playerId the unique identifier of the player attempting to perform the action
     * @param idTile the unique identifier of the tile the player wants to draw
     * @throws IllegalMethodOnThisStateException if this method is invoked in an invalid state
     */
    public void userDrawTile(int playerId, int idTIle) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    /**
     * Throws an IllegalMethodOnThisStateException, as this method
     * is not allowed in the current state (FinishScoreState).
     *
     * @param playerId the ID of the player attempting to finish shipboard
     *                 in an invalid state
     */
    public void userFinishShipboard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    /**
     * Allows the player to look into a small adventure card deck during their turn.
     * This method is restricted in the FinishScoreState and will throw an exception if invoked.
     *
     * @param playerId the ID of the player making the action
     * @param smallDeckIndex index of the small adventure card deck to be viewed
     * @throws IllegalMethodOnThisStateException if called in an invalid state
     */
    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    /**
     * Throws an exception as this method is not allowed in the current game state.
     *
     * @param playerId the unique identifier of the player attempting to stop looking at the small adventure card deck
     * @param smallDeckIndex the index of the small adventure card deck being referenced
     */
    public void userStopLookSmallAdventureCardDeck(int playerId,int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    /**
     * Handles the request for placing a timer by a user in the current state.
     * This method is not allowed in the FinishScoreState and will throw an exception.
     *
     * @param playerId the unique identifier of the player attempting the action
     * @throws IllegalMethodOnThisStateException if this method is invoked in an invalid state
     */
    public void userPlaceTimer(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    /**
     * Throws an exception as this method is not allowed in the current state (FinishScoreState).
     * Specifically, this operation is restricted and cannot be executed in this state.
     *
     * @throws IllegalMethodOnThisStateException if invoked in the FinishScoreState.
     */
    public void timerIsDone(){
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    /**
     * Handles the action of a user selecting a type of token in the game.
     * This method is not allowed in the current state and will throw an exception.
     *
     * @param playerId the ID of the player attempting the action
     * @param token the type of token the player selects
     * @param coordinates the coordinates where the token is to be placed
     * @throws IllegalMethodOnThisStateException if the method is called in an illegal game state
     */
    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    /**
     * Throws an exception as this method is not allowed to be called in the FinishScoreState.
     *
     * @param playerid the ID of the player attempting to perform the action
     * @throws IllegalMethodOnThisStateException when invoked in the current state
     */
    public void userThrowDices(int playerid) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    /**
     * Handles the attempt of a user to draw an adventure card during the FinishScoreState.
     * This operation is not allowed in this state and will throw an exception.
     *
     * @param playerId the unique identifier of the player attempting the action
     * @throws IllegalMethodOnThisStateException if the method is invoked in the current state
     */
    public void userDrawAdventureCard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    /**
     * Handles the user's attempt to play an adventure card during the finish score state.
     * This action is not allowed in the current state and will throw an exception.
     *
     * @param playerId the ID of the player attempting to play the adventure card
     * @throws IllegalMethodOnThisStateException if called in the finish score state
     */
    public void userPlayAdventureCard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    /**
     * Notifies the completion of the player's decision activation process.
     * This method is illegal in the current state and will throw an exception if invoked.
     *
     * @param playerId the ID of the player trying to finish the decision activation
     * @param coordinates the set of coordinates related to the decision activation
     * @throws IllegalMethodOnThisStateException if the method is invoked in an invalid state
     */
    public void userFinishDecisionActivating(int playerId, Set<Point> coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }

    /**
     * Handles the process of completing a user's decision regarding tokens.
     * This method is not valid in the current state.
     *
     * @param playerId the unique identifier of the player making the decision
     * @param listOfTokens a list of pairs containing the position and type of tokens selected by the player
     * @throws IllegalMethodOnThisStateException if this method is invoked in an invalid state
     */
    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }

    /**
     * Handles the action of a user attempting to land on a planet.
     * This method is not allowed to be called in the current state.
     *
     * @param playerId the unique ID of the player attempting to land on the planet
     * @param isLanded a boolean indicating whether the landing action is successful
     * @param planetId the unique ID of the targeted planet
     * @throws IllegalMethodOnThisStateException if called in an invalid state
     */
    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }


    /**
     * Handles the action of a user attempting to land on a station or a ship.
     * This method is not allowed in the current game state.
     *
     * @param playerId the ID of the player attempting the action
     * @param isLanded specifies whether the player has landed
     * @throws IllegalMethodOnThisStateException if the method is called in an invalid state
     */
    public void userLandOnStationOrShip(int playerId, boolean isLanded) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }

    /**
     * Attempts to handle the acceptance or refusal of prizes by a user in the current state.
     * This method is not allowed in FinishScoreState and will throw an exception.
     *
     * @param playerId the ID of the player attempting to accept or refuse prizes
     * @param isAccepted a boolean indicating whether the prizes are accepted (true) or refused (false)
     * @throws IllegalMethodOnThisStateException if the method is invoked in the FinishScoreState
     */
    public void userAcceptPrizes(int playerId, boolean isAccepted){
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }

    /**
     * Handles the action for stopping a flight for the specified player.
     *
     * @param playerId the unique identifier of the player stopping their flight
     */
    public void userStopFlight(int playerId) {

    }

    /**
     * Calculates and updates the score for a specific player in the game session.
     * This method is intended to be used by administrators.
     *
     * @param playerId the unique identifier of the player whose score needs to be calculated
     * @throws GameSettingsException if the game settings prevent score calculation
     */
    public void adminCalculateScore(int playerId) throws GameSettingsException {
        gameSession.calculateScore(playerId);
    }
    /**
     * Ends the game for the user identified by the given player ID.
     *
     * @param playerId the unique identifier of the player ending the game
     * @throws GameSettingsException if the game is not finished yet
     */
    public void userEndGame(int playerId) throws GameSettingsException {
        gameSession.userEndGame(playerId);
    }
}
