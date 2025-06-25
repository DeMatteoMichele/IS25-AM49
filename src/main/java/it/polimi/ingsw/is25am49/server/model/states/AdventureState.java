package it.polimi.ingsw.is25am49.server.model.states;

import it.polimi.ingsw.is25am49.server.model.Game;
import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.exceptions.NotEnoughPlayerException;
import it.polimi.ingsw.is25am49.server.model.exceptions.OutOfDeckExceptions;
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
 * Represents the AdventureState in the game state machine.
 * Handles behaviors specific to the "Adventure" phase of the game.
 * Most administrative and setup-related methods are illegal in this state.
 */
public class AdventureState extends State {
    /**
     * Constructs an instance of AdventureState.
     *
     * @param gameSession the current game session associated with this state
     */
    public AdventureState(GameSession gameSession){
        super(gameSession);
    }
    /**
     * Triggers an exception as this functionality is not allowed in the current AdventureState.
     *
     * @param player   the Player attempting to select a game mode
     * @param gamemode the GameMode the admin is trying to select
     */
    public void adminSelectGameMode(Player player, GameMode gamemode) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    /**
     * Handles the user's attempt to join the lobby in the current state.
     * In AdventureState, this operation is not allowed and will throw an exception.
     *
     * @param username the name of the user attempting to join the lobby
     * @param playerId the unique identifier of the player
     * @throws IllegalMethodOnThisStateException if the method is invoked in an invalid state
     */
    public void userJoinLobby(String username, int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
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
     * Disconnects a player from the current game session.
     *
     * @param playerId the unique identifier of the player to be disconnected.
     */
    @Override
    public void disconnectPlayer(int playerId) {
        gameSession.disconnectPlayer(playerId);
    }

    /**
     * Allows the admin to select a game mode for a player.
     * Throws an exception if called in an invalid state.
     *
     * @param playerId the ID of the player for whom the game mode is being set
     * @param gamemode the game mode to be selected
     * @throws IllegalMethodOnThisStateException if called while in AdventureState
     */
    public void adminSelectGameMode(int playerId, GameMode gamemode) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    /**
     * Attempts to create a new game with the specified parameters.
     * This operation is not allowed in the current state and will throw an exception.
     *
     * @param playerId The ID of the player attempting to create the game.
     * @param gamemode The game mode selected for the new game.
     * @param maxPlayers The maximum number of players allowed in the game.
     * @throws IllegalMethodOnThisStateException Always thrown, as this method is illegal in the current state.
     */
    public void adminCreateGame(int playerId, GameMode gamemode, int maxPlayers) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
   /**
    * Attempts to start the game in the current state. This action is not allowed
    * in the AdventureState and will throw an exception.
    *
    * @param playerId the ID of the player attempting to start the game
    * @throws IllegalMethodOnThisStateException if the method is invoked in an invalid state
    */
   public void adminStartGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    /**
     * Rotates a tile for the user during their turn in the game.
     *
     * @param playerId the ID of the player attempting the action
     * @param orientation the desired orientation to rotate the tile to
     * @throws GameSettingsException if the operation violates game settings
     * @throws IllegalMethodOnThisStateException if the method is invoked in an invalid game state
     */
    public void userRotateTile(int playerId, Orientation orientation) throws GameSettingsException {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    /**
     * Indicates that a player is attempting to place a tile on the board.
     * In the AdventureState, this action is not allowed and will throw an exception.
     *
     * @param playerId the ID of the player attempting the action
     * @param coordinates the coordinates where the player intends to place the tile
     * @throws IllegalMethodOnThisStateException if the method is executed in an invalid state
     */
    public void userPlaceTile(int playerId, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    /**
     * Handles the action of a player attempting to pick a tile during the game.
     * This action is not allowed in the current AdventureState.
     *
     * @param playerId the identifier of the player trying to pick a tile
     * @param coordinates the position of the tile to be picked
     * @throws IllegalMethodOnThisStateException if the method is called in an illegal state
     */
    public void userPickTile(int playerId, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    /**
     * Throws an exception indicating that discarding a tile is not allowed
     * in the current state of the game.
     *
     * @param playerId the ID of the player attempting to discard a tile
     * @throws IllegalMethodOnThisStateException if the method is invoked in an invalid state
     */
    public void userDiscardTile(int playerId){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    /**
     * Attempts to allow a user to draw a tile identified by its ID.
     *
     * This method is not allowed in the current state and will throw an exception.
     *
     * @param playerId the ID of the player attempting to draw the tile
     * @param idTIle the ID of the tile the player wants to draw
     * @throws IllegalMethodOnThisStateException if invoked in an invalid state
     */
    public void userDrawTile(int playerId, int idTIle) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    /**
     * Terminates the shipboard phase for the specified player.
     * This method is not allowed in the current AdventureState and will throw an exception.
     *
     * @param playerId the ID of the player attempting to finish the shipboard phase
     * @throws IllegalMethodOnThisStateException if this method is called in the AdventureState
     */
    public void userFinishShipboard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    /**
     * Allows the user to view the cards in a small adventure card deck.
     *
     * @param playerId       the ID of the player performing the action
     * @param smallDeckIndex the index of the small adventure card deck to be viewed
     * @throws IllegalMethodOnThisStateException if the method is invoked in an invalid state
     */
    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    /**
     * Handles the action of a player stopping to look at a small adventure card deck.
     * This operation is not valid in the current state and will result in an exception.
     *
     * @param playerId the ID of the player attempting the action
     * @param smallDeckIndex the index of the small adventure card deck
     * @throws IllegalMethodOnThisStateException if called in an invalid state
     */
    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    /**
     * Handles the user request to place a timer in the current game state.
     * This method is illegal in the AdventureState and will throw an exception.
     *
     * @param playerId the unique identifier of the player attempting to place the timer
     * @throws IllegalMethodOnThisStateException if called in an invalid game state
     */
    public void userPlaceTimer(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    /**
     * Indicates that the timer has completed its countdown.
     * Throws an exception as this operation is not allowed in the AdventureState.
     *
     * @throws IllegalMethodOnThisStateException if invoked while in AdventureState
     */
    public void timerIsDone(){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    /**
     * Handles the selection of a specific type of token by the user.
     * This method is not allowed in the current AdventureState and will throw an exception if invoked.
     *
     * @param playerId the unique identifier of the player performing the action
     * @param token the type of token selected by the user
     * @param coordinates the coordinates associated with the token's placement or selection
     * @throws IllegalMethodOnThisStateException if the method is called in an invalid state
     */
    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    /**
     * Allows the current player to throw the dices during their turn.
     * Throws an exception if the player invoking the method is not the current player.
     *
     * @param playerId the ID of the player attempting to throw the dices
     * @throws IllegalMethodOnThisStateException if the player is not the current player
     */
    public void userThrowDices(int playerId){
        Player player = getPlayerById(playerId);
        Player currentPlayer = getCurrentPlayer();
        if(player.equals(currentPlayer)){
            gameSession.userThrowDices(player);
        }
        else{
            throw new IllegalMethodOnThisStateException("You are not the current player");
        }
    }
    /**
     * Allows a player to draw an adventure card. If the adventure card deck is empty, the game ends.
     *
     * @param playerId the unique identifier of the player attempting to draw an adventure card
     * @throws GameSettingsException if there is an issue with the game settings during the drawing process
     */
    public void userDrawAdventureCard(int playerId) throws GameSettingsException {
        Player player = getPlayerById(playerId);
        try {
            gameSession.userDrawAdventureCard(player);
        }catch (OutOfDeckExceptions _){
            gameSession.getGame().ifPresent(Game::finishGame);
        }

    }
    /**
     * Allows the current player to finalize their decision by activating specific coordinates
     * on the game board.
     *
     * @param playerId the ID of the player attempting to finalize their decision
     * @param coordinates a set of coordinates representing the areas to be activated
     */
    public void userFinishDecisionActivating(int playerId, Set<Point> coordinates){
        Player player = getPlayerById(playerId);
        Player currentplayer = getCurrentPlayer();
        if(player.equals(currentplayer)){
            gameSession.userFinishDecisionActivating(coordinates);
        }

    }
    /**
     * Allows the current player to finalize their token placement decisions in a game.
     * Throws an exception if called by a player who is not the current player.
     *
     * @param playerId the unique identifier of the player attempting the action
     * @param listOfTokens a list containing pairs of positions and tokens to finalize
     * @throws IllegalMethodOnThisStateException if the player is not the current player
     */
    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens){
        Player player = getPlayerById(playerId);
        Player currentPlayer = getCurrentPlayer();
        if(player.equals(currentPlayer)){
            gameSession.userFinishDecisionToken(listOfTokens);
        }
        else {
            throw new IllegalMethodOnThisStateException("You are not the current player");
        }
    }

    /**
     * Allows the current player to land on a planet during their turn.
     * Ensures that only the current player can invoke this action.
     *
     * @param playerId the ID of the player attempting the action
     * @param isLanded indicates whether the player successfully lands on the planet
     * @param planetId the ID of the planet to land on
     * @throws IllegalMethodOnThisStateException if the player is not the current player
     */
    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        Player player = getPlayerById(playerId);
        //controllo se il player che chiama il metodo è il current player
        //se lo è smetto di passare il player perché gli effetti della carta sono sul current player
        Player currentPlayer = getCurrentPlayer();
        if(player.equals(currentPlayer)){
            gameSession.userLandOnPlanet(isLanded, planetId);
        }
        else{
            throw new IllegalMethodOnThisStateException("You are not the current player");
        }

    }
    /**
     * Handles the action of a user landing on a station or ship.
     * Ensures the operation is performed only by the current player.
     *
     * @param playerId the unique identifier of the player attempting the action
     * @param isLanded a flag indicating whether the player has landed
     * @throws IllegalMethodOnThisStateException if the player is not the current player
     */
    public void userLandOnStationOrShip(int playerId, boolean isLanded) {
        Player player = getPlayerById(playerId);
        Player currentPlayer = getCurrentPlayer();
        if(player.equals(currentPlayer)){
            gameSession.userLandOnStationOrShip(isLanded);
        }
        else{
            throw new IllegalMethodOnThisStateException("You are not the current player");
        }

    }
    /**
     * Allows the current player to accept or reject prizes.
     * Throws an exception if called by a player who is not the current player.
     *
     * @param playerId    the unique ID of the player attempting the action
     * @param isAccepted  true if the player accepts the prizes, false otherwise
     * @throws IllegalMethodOnThisStateException if the player is not the current player
     */
    public void userAcceptPrizes(int playerId, boolean isAccepted){
        Player player = getPlayerById(playerId);
        Player currentPlayer = getCurrentPlayer();
        if(player.equals(currentPlayer)){
            gameSession.userAcceptPrizes(isAccepted);
        }
        else{
            throw new IllegalMethodOnThisStateException("You are not the current player");
        }
    }

    /**
     * Handles the player's action to stop their flight during the game.
     *
     * @param playerId the unique identifier of the player stopping their flight
     */
    public void userStopFlight(int playerId) {
        Player player = getPlayerById(playerId);
//        try {
            gameSession.userStopFlight(player);
//        }catch (NotEnoughPlayerException _){//lanciata in remove player della leaderboard (se ho un giocatore solo finish)
//            gameSession.getGame().ifPresent(Game::finishGame);
//            transition(new FinishScoreState(gameSession));
//        }
    }

    /**
     * Calculates the score for the specified player. This method is intended
     * for administrative operations and is not allowed in the current state.
     * Throws an exception if invoked.
     *
     * @param playerId the unique identifier of the player whose score is being calculated
     * @throws IllegalMethodOnThisStateException if the method is invoked in an illegal state
     */
    public void adminCalculateScore(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    /**
     * Ends the game for the specified player. This method is not allowed
     * in the current state and will throw an exception.
     *
     * @param playerId the ID of the player attempting to end the game
     * @throws IllegalMethodOnThisStateException if this method is called in an invalid state
     */
    public void userEndGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    /**
     * Retrieves the current player from the active game session if available.
     *
     * @return the current player in the game, or null if no game session or current player is set.
     */
    public Player getCurrentPlayer(){
        return gameSession.getGame()
                .map(g->g.getGameboard().getCurrentPlayer())
                .orElse(null);
    }
}
