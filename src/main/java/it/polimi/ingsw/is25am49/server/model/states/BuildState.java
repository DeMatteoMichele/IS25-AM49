package it.polimi.ingsw.is25am49.server.model.states;

import it.polimi.ingsw.is25am49.server.model.Game;
import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.server.model.Player;
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
 * Represents the Build phase in the game's state machine where players construct their ships.
 * Implements specific logic and restrictions for this state.
 */
public class BuildState extends State {
    /**
     * Constructs a BuildState object and initializes it with the provided GameSession.
     *
     * @param gameSession the current game session this state is associated with
     */
    public BuildState(GameSession gameSession){
        super(gameSession);
    }
    /**
     * Allows an admin to select a game mode. This method is illegal in the BuildState and will throw an exception.
     *
     * @param playerId the ID of the player attempting to select the game mode
     * @param gamemode the game mode to be selected
     * @throws IllegalMethodOnThisStateException if called in the BuildState
     */
    //illegal
    public void adminSelectGameMode(int playerId, GameMode gamemode) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    /**
     * Handles the attempt of a user to join the lobby in the current state.
     * This method is not allowed in the BuildState and will throw an exception.
     *
     * @param username the name of the user attempting to join the lobby
     * @param playerId the unique identifier of the player
     * @throws IllegalMethodOnThisStateException if the method is invoked in an invalid state
     */
    public void userJoinLobby(String username, int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
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
     * Attempts to create a game with the specified parameters.
     * This method is not allowed in the current state and will throw an exception.
     *
     * @param playerId the ID of the player initiating the game creation
     * @param gamemode the game mode to be set for the new game
     * @param maxPlayers the maximum number of players allowed in the game
     * @throws IllegalMethodOnThisStateException if the method is called in an invalid state
     */
    public void adminCreateGame(int playerId, GameMode gamemode, int maxPlayers) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    /**
     * Rotates a player's tile to the specified orientation by performing a series of clockwise rotations.
     *
     * @param playerId the unique identifier of the player requesting the rotation
     * @param orientation the desired final orientation for the player's tile
     * @throws GameSettingsException if the game settings do not allow the tile rotation
     */
    public void userRotateTile(int playerId, Orientation orientation) throws GameSettingsException {
        Player player = getPlayerById(playerId);
        int i;
        for(i = 0; i<orientation.ordinal(); i++){
            userRotateTileClockwise(player);
        }
    }

    /**
     * Allows a player to place a tile onto their shipboard at the specified coordinates.
     *
     * @param playerId the unique identifier of the player placing the tile
     * @param coordinates the coordinates on the shipboard where the tile will be placed
     */
    //ok
    public void userPlaceTile(int playerId, Point coordinates) {
        Player player = getPlayerById(playerId);
        player.placeTile(coordinates);
    }

    /**
     * Rotates the player's current tile clockwise.
     *
     * @param player the player whose tile will be rotated clockwise
     */
    public void userRotateTileClockwise(Player player) {
        player.rotateTile(true);
    }
    /**
     * Allows a user to pick a tile from their shipboard at the specified coordinates.
     *
     * @param playerId The unique identifier of the player who is picking the tile.
     * @param coordinates The coordinates of the tile to be picked on the player's shipboard.
     */
    //ok
    public void userPickTile(int playerId, Point coordinates){
        Player player = getPlayerById(playerId);
        player.pickTile(coordinates);
    }
    /**
     * Allows a player to discard their current tile during the game,
     * ensuring the shipboard is not yet completed.
     *
     * @param playerId the unique identifier of the player performing the discard action
     */
    public void userDiscardTile(int playerId){
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        gameSession.userDiscardTile(player);
    }
    /**
     * Allows a player to draw a tile during the game.
     *
     * @param playerId the unique identifier of the player performing the action
     * @param idTIle the identifier of the tile the player wants to draw
     */
    public void userDrawTile(int playerId, int idTIle) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        gameSession.userDrawTile(player, idTIle);
    }
    /**
     * Allows a player to complete their shipboard setup and triggers necessary game state transitions.
     * Merges small decks if the shipboard setup is completed and checks conditions
     * to transition to the AdventureState when all eligible players are prepared.
     *
     * @param playerId the unique identifier of the player who has finished their shipboard setup
     */
    public void userFinishShipboard(int playerId) {
        Player player = getPlayerById(playerId);
        if (gameSession.userFinishShipboard(player)) {
            gameSession.mergeSmallDecks();
            if(gameSession.getGame().isPresent()) {
                Game game = gameSession.getGame().get();
                for (Player p : game.getListPlayers()) {
                    if(p.getShipBoard().getAlienPoint().isPresent()) return;
                }
                transition(new AdventureState(gameSession));
            }
        }
    }
    /**
     * Allows a player to view a small adventure card deck specified by its index.
     * Ensures the player's shipboard is not completed before proceeding.
     *
     * @param playerId the unique identifier of the player requesting to view the deck
     * @param smallDeckIndex the index of the small adventure card deck to be viewed
     */
    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        gameSession.userLookSmallAdventureCardDeck(player, smallDeckIndex);
    }
    /**
     * Allows a player to stop viewing the specified small adventure card deck.
     *
     * @param playerId The unique identifier of the player.
     * @param smallDeckIndex The index of the small adventure card deck the player stops viewing.
     * @throws GameSettingsException If the operation cannot be completed due to game rules or state.
     */
    public void userStopLookSmallAdventureCardDeck(int playerId,int smallDeckIndex) throws GameSettingsException {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        gameSession.userStopLookSmallAdventureCardDeck(player, smallDeckIndex);
    }
    /**
     * Allows the specified player to place their game timer.
     *
     * @param playerId the unique identifier of the player placing the timer
     */
    public void userPlaceTimer(int playerId) {
        Player player = getPlayerById(playerId);
        gameSession.userPlaceTimer(player);
    }
    /**
     * Handles the actions to be taken when the timer expires in the game session.
     * Merges small adventure decks and processes the completion of the shipboard
     * for all players in the game, if a game is present.
     *
     * @throws RuntimeException if no game is currently set in the session.
     */
    public void timerIsDone(){
        gameSession.mergeSmallDecks();
        if (gameSession.getGame().isPresent()) {
            for (Player p: gameSession.getGame().get().getListPlayers()) {
                try {
                    gameSession.getState().userFinishShipboard(p.getIdPlayer());
                }catch (Exception _){}
            }
        }else throw new RuntimeException("timer is done GAME NON SETTATO non so perch'");
    }
    /**
     * Allows the user to select a specific type of token and places it at the specified coordinates on the game board.
     * If the action completes successfully, proceeds to finalize the player's shipboard setup.
     *
     * @param playerId the unique identifier of the player making the selection
     * @param token the type of token to be selected and placed
     * @param coordinates the point on the game board where the token should be placed
     */
    //riempi tutti i component, anche le batterie, ecc.
    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates){
        Player player = getPlayerById(playerId);
        if(gameSession.userSelectTypeOfToken(player, token, coordinates)) {
            userFinishShipboard(playerId);
        }
    }
    /**
     * Throws an exception indicating that the method is not allowed in the current state.
     *
     * @param playerId the unique identifier of the player attempting to throw dices
     * @throws IllegalMethodOnThisStateException if the method is called in an illegal game state
     */
    //illegal
    public void userThrowDices(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    /**
     * Prevents the user from drawing an adventure card in the current state.
     * This operation is not allowed in the BuildState.
     *
     * @param playerId the ID of the player attempting to draw the adventure card
     * @throws IllegalMethodOnThisStateException if the method is invoked in BuildState
     */
    public void userDrawAdventureCard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    /**
     * Handles the action of a user attempting to play an adventure card during the BuildState phase.
     * This action is not allowed in this state and will throw an exception.
     *
     * @param playerId the unique identifier of the player attempting this action
     * @throws IllegalMethodOnThisStateException if the method is invoked in the current state
     */
    public void userPlayAdventureCard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    /**
     * Handles the user's decision to finish activating a set of components.
     * This method is illegal in the current state and will throw an exception.
     *
     * @param playerId the ID of the player making the decision
     * @param coordinates the set of coordinates related to the activation decision
     * @throws IllegalMethodOnThisStateException if invoked in an invalid state
     */
    public void userFinishDecisionActivating(int playerId, Set<Point> coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    /**
     * Handles the user's decision to finalize the placement of tokens.
     * This operation is illegal in the current state and will throw an exception.
     *
     * @param playerId the ID of the player attempting this action
     * @param listOfTokens a list containing pairs of token positions and the tokens themselves
     * @throws IllegalMethodOnThisStateException always, as this method is not allowed in this state
     */
    @Override
    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
        throw  new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    /**
     * Attempts to activate a component at the specified coordinates for the player,
     * but this action is illegal in the current BuildState.
     *
     * @param playerId the ID of the player attempting the action
     * @param coordinates the coordinates of the component to be activated
     * @throws IllegalMethodOnThisStateException if the method is invoked in the BuildState
     */
    public void userActivateComponent(int playerId, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    /**
     * Handles the action of a user attempting to land on a planet.
     * This method is not allowed in the BuildState and will throw an exception when called.
     *
     * @param playerId the unique identifier of the player attempting the action
     * @param isLanded indicates if the player is attempting to land or not
     * @param planetId the unique identifier of the planet being targeted for landing
     * @throws IllegalMethodOnThisStateException if this method is called in an invalid state
     */
    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    /**
     * Handles the action of a user attempting to land on a station or ship.
     * This method is not allowed to be executed in the current {@code BuildState}.
     * Throws {@code IllegalMethodOnThisStateException}.
     *
     * @param playerId the unique identifier of the player attempting to perform the action
     * @param isLanded indicates whether the player is landing (true) or not (false)
     * @throws IllegalMethodOnThisStateException if the method is invoked in an invalid state
     */
    public void userLandOnStationOrShip(int playerId,boolean isLanded) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    /**
     * Updates the state of activated days for a user.
     * Throws IllegalMethodOnThisStateException if invoked in the current state.
     * This method is not allowed in the BuildState.
     */
    public void userActivateDaysUpdate() {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    /**
     * Handles the user's acceptance or rejection of prizes in the game.
     * This method is not allowed in the current state and will throw an exception.
     *
     * @param playerId   The unique identifier of the player attempting to accept prizes.
     * @param isAccepted A boolean indicating whether the prizes are accepted (true) or rejected (false).
     * @throws IllegalMethodOnThisStateException if invoked in an invalid state.
     */
    public void userAcceptPrizes(int playerId, boolean isAccepted){
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    /**
     * Stops the ongoing flight for the specified player.
     * This method is not allowed in the current state and will throw an exception.
     *
     * @param playerId the unique identifier of the player attempting to stop the flight
     * @throws IllegalMethodOnThisStateException if invoked in an unsupported state
     */
    public void userStopFlight(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    /**
     * Attempts to calculate the score for a player in the admin context.
     * This method is illegal to invoke in the current BuildState.
     *
     * @param playerId The unique identifier of the player.
     * @throws IllegalMethodOnThisStateException if invoked in the BuildState.
     */
    public void adminCalculateScore(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    /**
     * Handles the user's attempt to end the game during the BuildState.
     * This method is not allowed in this state and will throw an exception.
     *
     * @param playerId the unique ID of the player attempting to end the game
     * @throws IllegalMethodOnThisStateException if called during an illegal state
     */
    public void userEndGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
}
