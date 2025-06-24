package it.polimi.ingsw.is25am49.server.model.states;

import it.polimi.ingsw.is25am49.shared.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

/**
 * Represents the initial state of the game, where setup and configuration are performed.
 * Allows administrative actions such as selecting the game mode and creating the game,
 * as well as enabling players to join the lobby.
 * Illegal operations trigger exceptions, ensuring state-specific behavior is maintained.
 */
public class InitState extends State {

    /**
     * Constructs an InitState object and initializes it with the given GameSession.
     *
     * @param gameSession the GameSession instance associated with this state
     */
    public InitState(GameSession gameSession){
        super(gameSession);
    }

    /**
     * Allows the admin to select the game mode for the current session.
     *
     * @param playerId the ID of the player attempting to set the game mode
     * @param gamemode the game mode to be set
     * @throws GameSettingsException if the player is not an admin or if no admin is defined
     */
    //To change
    public void adminSelectGameMode(int playerId, GameMode gamemode) throws GameSettingsException {
        gameSession.checkIsAdmin(playerId);
        gameSession.setGameMode(gamemode);
    }
    /**
     * Allows a player to join the game lobby.
     *
     * @param username the name of the player joining the lobby
     * @param playerId the unique identifier of the player
     * @throws GameSettingsException if the lobby is full or the game is not ready
     */
    public void userJoinLobby(String username, int playerId) throws GameSettingsException {
        gameSession.userJoinLobby(username, playerId);
    }

    /**
     * Connects a player to the current game session.
     *
     * @param playerId the unique identifier of the player to connect
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
     * Allows the admin to create a new game with the specified game mode and maximum number of players.
     *
     * @param playerId the ID of the player attempting to create the game
     * @param gamemode the game mode to be set for the new game
     * @param maxPlayers the maximum number of players allowed in the game
     * @throws GameSettingsException if the player is not an admin or if a game already exists
     */
    public void adminCreateGame(int playerId, GameMode gamemode, int maxPlayers) throws GameSettingsException{
        gameSession.checkIsAdmin(playerId);
        gameSession.createNewGame(gamemode, maxPlayers);
    }
    /**
     * Attempts to rotate a tile for the user during an illegal game state.
     * Throws an exception indicating the method is not allowed in the current state.
     *
     * @param playerId the ID of the player attempting to rotate the tile
     * @param orientation the desired orientation for the tile (e.g., NORTH, EAST, SOUTH, WEST)
     * @throws GameSettingsException if the action is invoked in an inappropriate state
     */
    //Illegal
    public void userRotateTile(int playerId, Orientation orientation) throws GameSettingsException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    /**
     * Attempts to place a tile for a user in the game.
     * This operation is not allowed while the game is in the InitState and will throw an exception.
     *
     * @param playerId the ID of the player attempting to place the tile
     * @param coordinates the coordinates where the tile is intended to be placed
     * @throws IllegalMethodOnThisStateException if the method is invoked while in InitState
     */
    public void userPlaceTile(int playerId, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    /**
     * Handles the user's tile selection attempt during the game.
     * This method will throw an exception if invoked in an illegal state.
     *
     * @param playerId the ID of the player attempting to pick a tile
     * @param coordinates the coordinates of the tile the player wants to pick
     * @throws IllegalMethodOnThisStateException if this method is called in the current game state
     */
    public void userPickTile(int playerId, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    /**
     * Handles the request of a player to discard a tile.
     * This method is not allowed in the current state and will throw an exception.
     *
     * @param playerId the unique identifier of the player attempting the action
     * @throws IllegalMethodOnThisStateException if the method is invoked in an illegal state
     */
    public void userDiscardTile(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    /**
     * Handles a player's attempt to draw a tile in the current game state.
     * In the InitState, this method is not allowed and will throw an exception.
     *
     * @param playerId the ID of the player attempting to draw a tile
     * @param idTIle the ID of the tile the player is attempting to draw
     * @throws IllegalMethodOnThisStateException if the method is invoked in an invalid state
     */
    public void userDrawTile(int playerId, int idTIle) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    /**
     * Throws an exception indicating that finishing a shipboard action
     * is not allowed in the current state.
     *
     * @param playerId the ID of the player attempting to finish the shipboard action
     */
    public void userFinishShipboard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    /**
     * Handles the player's request to view a specified small adventure card deck.
     * This method is illegal in the current state and will throw an exception if invoked.
     *
     * @param playerId          the unique identifier of the player making the request
     * @param smallDeckIndex    the index of the small adventure card deck to view
     * @throws IllegalMethodOnThisStateException if called while in an inappropriate state
     */
    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    /**
     * Handles the stop of a player looking at a small adventure card deck.
     * This method is not allowed in the InitState and will throw an exception if called.
     *
     * @param playerId the unique identifier of the player attempting the action
     * @param smallDeckIndex the index of the small adventure card deck the player is interacting with
     * @throws IllegalMethodOnThisStateException when invoked in an invalid state
     */
    public void userStopLookSmallAdventureCardDeck(int playerId,int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    /**
     * Represents a method invoked when a user tries to place a timer.
     * Not allowed in the current InitState and throws an exception.
     *
     * @param playerId the ID of the player attempting to place a timer
     * @throws IllegalMethodOnThisStateException if called in an illegal state
     */
    public void userPlaceTimer(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    /**
     * Indicates that the timer has completed its duration while in the InitState.
     * This method is considered illegal in the current state and will throw an exception.
     *
     * @throws IllegalMethodOnThisStateException if called in the InitState, as this action
     * is not allowed in the current game state.
     */
    public void timerIsDone(){
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    /**
     * Handles the user action of selecting a specific type of token at given coordinates.
     * This method is not allowed in the current state and will throw an exception.
     *
     * @param playerId the ID of the player attempting the action
     * @param token the type of token selected by the player
     * @param coordinates the coordinates where the action is attempted
     * @throws IllegalMethodOnThisStateException if the method is called in an invalid state
     */
    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    /**
     * Handles a dice-throwing action triggered by a user. This action is not allowed in the
     * current state (InitState), and calling this method will result in an exception.
     *
     * @param playerId the unique identifier of the player trying to throw the dice
     * @throws IllegalMethodOnThisStateException if the method is invoked in an invalid state
     */
    public void userThrowDices(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }


    /**
     * Throws an exception indicating that drawing an adventure card is not allowed in the current state.
     *
     * @param playerId the ID of the player attempting to draw an adventure card
     * @throws IllegalMethodOnThisStateException always thrown as this method is illegal in InitState
     */
    public void userDrawAdventureCard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    /**
     * Handles the user's completion of an activation decision process.
     * This method is not allowed in the current state and will throw an exception.
     *
     * @param playerId the ID of the player attempting to make the decision
     * @param coordinates the set of coordinates related to the decision
     * @throws IllegalMethodOnThisStateException if the method is invoked in an invalid state
     */
    public void userFinishDecisionActivating(int playerId, Set<Point> coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    /**
     * Handles the user decision to finalize token-related actions.
     * Throws an exception if invoked in an inappropriate state.
     *
     * @param playerId the ID of the player making the decision
     * @param listOfTokens a list of token and position pairs involved in the decision
     * @throws IllegalMethodOnThisStateException if the method is called outside of a valid state
     */
    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    /**
     * Handles the action of a user attempting to land on a specified planet.
     * This method is not valid in the context of the current state and will throw an exception.
     *
     * @param playerId the identifier of the player attempting the action
     * @param isLanded a flag indicating whether the player is landing or not
     * @param planetId the identifier of the planet the player is interacting with
     * @throws IllegalMethodOnThisStateException if the method is invoked in an invalid state
     */
    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    /**
     * Handles a user action to land on a station or ship. Not allowed in the InitState.
     *
     * @param playerId the ID of the player attempting the action
     * @param isLanded whether the landing operation is to be confirmed
     * @throws IllegalMethodOnThisStateException if the method is called while in InitState
     */
    public void userLandOnStationOrShip(int playerId, boolean isLanded) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    /**
     * Handles the acceptance or rejection of prizes by a player.
     * This method is illegal in the InitState and will throw an exception if invoked.
     *
     * @param playerId    the ID of the player attempting to accept or reject prizes
     * @param isAccepted  a boolean indicating whether the prizes are accepted (true) or rejected (false)
     * @throws IllegalMethodOnThisStateException if this method is called in an invalid state
     */
    public void userAcceptPrizes(int playerId, boolean isAccepted) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }


    /**
     * Handles the user request to stop a flight during gameplay.
     * This method is considered illegal in the InitState and will throw an exception if invoked.
     *
     * @param playerId the ID of the player attempting to stop the flight
     * @throws IllegalMethodOnThisStateException if called while in InitState
     */
    public void userStopFlight(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    /**
     * Throws an exception indicating that this method cannot be invoked in the current state.
     *
     * @param playerId the identifier of the player attempting to perform this action
     * @throws IllegalMethodOnThisStateException if the method is invoked in an inappropriate state
     */
    public void adminCalculateScore(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    /**
     * Handles the user request to end the game. This operation is not
     * allowed in the current state and will throw an exception.
     *
     * @param playerId the unique ID of the player attempting to end the game
     * @throws IllegalMethodOnThisStateException if invoked in the InitState
     */
    public void userEndGame(int playerId)  {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
}
