package it.polimi.ingsw.is25am49.server.model;

import it.polimi.ingsw.is25am49.shared.enums.Color;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;

import java.awt.*;
import java.util.Optional;

public class Player {
    /**
     * Tracks the number of days consumed during the current turn for the player.
     */
    private int daysThisTurn;
    /**
     * The username of the player.
     */
    private String username;
    /**
     * Represents the shipboard area associated with the player.
     */
    private ShipBoard shipBoard;
    /**
     * Represents an optional tile currently held by the player.
     */
    private Optional<Tile> handTile;
    /**
     * Represents the final score of the player at the end of the game.
     */
    private int finalScore;
    /**
     * Represents the total number of credits the player has accumulated.
     */
    private int totCredits;
    /**
     * Represents the number of days spent traveling by the player.
     */
    private int travelDays;
    /**
     * Represents the player's assigned color in the game.
     */
    private Color color;
    /**
     * Represents the number of lost components for the player.
     */
    private int lostComponents;
    /**
     * Unique identifier for the player within the game.
     */
    private final int idPlayer;
    /**
     * Represents a reference to a small deck of AdventureCard objects currently being observed by the player.
     */
    private AdventureCardDeck smallAdventureDeckLooked;
    /**
     * Indicates whether the player is currently watching the game or not.
     */
    private boolean isWatching;
    /**
     * Indicates whether the crew has been placed on the player's ship.
     */
    private boolean theCrewHasBeenPlaced;
    /**
     * Indicates whether the player is currently connected.
     */
    private boolean connectionFlag;


    /**
     * Initializes a player with specified username, color, and ID.
     *
     * @param username the name of the player
     * @param color the color chosen by the player
     * @param idPlayer the unique ID assigned to the player
     */
    public Player(String username, Color color, int idPlayer) {
        this.username = username;
        this.color = color;
        daysThisTurn = 0;
        shipBoard = new ShipBoard();
        handTile = Optional.empty();
        finalScore = 0;
        totCredits = 0;
        travelDays = 0;
        lostComponents = 0;
        this.idPlayer = idPlayer;
        isWatching = false;
        connectionFlag = true;
    }

    /**
     * Sets the connection flag for the player.
     * @param connectionFlag the connection status to be set
     */
    public void setConnectionFlag(boolean connectionFlag) {
        this.connectionFlag = connectionFlag;
    }

    /**
     * Checks if the player is currently connected.
     * @return true if the player is connected, false otherwise
     */
    public boolean isConnected() {
        return connectionFlag;
    }

    /**
     * Retrieves the player's unique identifier.
     * @return the unique identifier of the player.
     */
    public int getIdPlayer() {
        return idPlayer;
    }
    /**
     * Ensures that there is a tile in the player's hand or throws an exception if no tile exists.
     */
    private void thereIsHandTile(){
        if(handTile.isEmpty())
            throw new IllegalStateException("There is no tile in hand");
    }
    /**
     * Checks if the shipboard is not completed and throws an exception if it is.
     */
    public void shipBoardIsNotCompletedYet(){
        if(shipBoard.isCompleted())
            throw new IllegalStateException("Shipboard is already completed");
    }
    /**
     * Checks if the player is currently watching.
     *
     * @return true if the player is watching, otherwise false.
     */
    public boolean isWatching(){
        return isWatching;
    }
    /**
     * Locks the last tile placed on the player's shipboard.
     */
    public void lockLastTile(){
        shipBoard.lockLastTile();
    }
    /**
     * Retrieves the small adventure card deck currently associated with the player.
     * @return the player's small adventure card deck
     */
    public AdventureCardDeck getSmallAdventureDeck(){
        return smallAdventureDeckLooked;
    }
    /**
     * Calculates the total score of goods tokens on the player's shipboard and removes all tokens afterward.
     * @return the total score of the goods tokens based on their respective values
     */
    public int scoreOfGoods(){
        int total= shipBoard.getTotalNumberOfaToken(Token.RED_GOODS)*4+
                shipBoard.getTotalNumberOfaToken(Token.YELLOW_GOODS)*3+
                shipBoard.getTotalNumberOfaToken(Token.GREEN_GOODS)*2+
                shipBoard.getTotalNumberOfaToken(Token.BLUE_GOODS);
        shipBoard.removeAllTokens();
        return total;
    }
    /**
     * Completes the player's shipboard to prepare it for flight.
     */
    public void completeShipboard(){
        shipBoard.completeShipBoard();
    }
    /**
     * Sets the hand tile for the player.
     * @param tile the tile to be set as the player's hand tile
     */
    public void setHandTile(Tile tile){
        handTile = Optional.of(tile);
    }

    /**
     * Retrieves the username of the player.
     * @return the username of the player
     */
    public String getUsername() {
        return username;
    }
//    public void setUsername(String username) {
//        this.username = username;
//    }

    /**
     * Retrieves the player's unique identification number.
     * @return the player's identification number as an Integer
     */
    public Integer getPlayerNumber() {
        return idPlayer;
    }

    /**
     * Retrieves the tile currently held in the player's hand, if any.
     * @return an Optional containing the hand tile if present, otherwise an empty Optional
     */
    public Optional<Tile> getHandTile(){
        return handTile;
    }
    /**
     * Updates the number of lost components by adding the given amount to the current total.
     * @param numberOfLostComponents the number of lost components to add to the existing total
     */
    public void addLostComponents(int numberOfLostComponents) {
        lostComponents = lostComponents + numberOfLostComponents;
    }

    /**
     * Retrieves the ShipBoard associated with the player.
     * @return the ShipBoard of the player.
     */
    public ShipBoard getShipBoard() {
        return shipBoard;
    }

    /**
     * Returns the final score of the player.
     *
     * @return the final score as an Integer
     */
    public Integer getScore() {
        return finalScore;
    }
//    public void setScore(int score) {
//        finalScore = score;
//    }

    /**
     * Adds credits to the player's total credits.
     *
     * @param amount the number of credits to add to the player's total
     */
    public void addCredits(int amount){
        totCredits = totCredits + amount;
    }
    /**
     * Picks a tile from the shipboard at the specified coordinates.
     * @param coordinates the location of the tile to be picked on the shipboard
     * @throws GameSettingsException if a tile is already in hand
     * @throws IllegalStateException if the shipboard is already completed
     */
    // da testare
    public void pickTile(Point coordinates){
        shipBoardIsNotCompletedYet();
        if(handTile.isEmpty())
            handTile = Optional.of(shipBoard.removeTile(coordinates));
        else
            throw new GameSettingsException("You cannot pick this tile");
    }
    /**
     * Places a tile on the shipboard at the specified coordinates if conditions are met.
     * @param coordinates the location on the shipboard where the tile is to be placed
     */
    // da testare
    public void placeTile(Point coordinates){
        thereIsHandTile();
        shipBoardIsNotCompletedYet();
        handTile.ifPresent(tile -> shipBoard.insertTile(coordinates, tile));
        handTile = Optional.empty();
    }
    /**
     * Rotates the currently held tile clockwise or counterclockwise based on the given parameter.
     * @param isClockwise true to rotate the tile clockwise, false to rotate it counterclockwise
     */
    // da testare e cambiare
    public void rotateTile(boolean isClockwise){
        shipBoardIsNotCompletedYet();
        if(isClockwise)
            handTile.ifPresent(Tile::rotateTileClockwise);
        else
            handTile.ifPresent(Tile::rotateTileCounterClockwise);
    }
    /**
     * Discards the current hand tile, if present.
     * @return an Optional containing the discarded Tile, or an empty Optional if no hand tile was present.
     */
    // da testare
    public Optional<Tile> discardTile(){
        thereIsHandTile();
        Optional<Tile> temp = handTile;
        handTile = Optional.empty();
        return temp;
    }
    /**
     * Retrieves the number of lost components for the player.
     * @return the number of lost components as an Integer
     */
    public Integer getLostComponents() {
        return lostComponents;
    }

    /**
     * Retrieves the total credits held by the player.
     *
     * @return the total number of credits as an Integer
     */
    public Integer getCredits(){
        return totCredits;
    }
    /**
     * Retrieves the color associated with the player.
     *
     * @return the player's color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the number of days spent during the current turn.
     * @param daysThisTurn the number of days to set for this turn
     */
    public void setDaysThisTurn(int daysThisTurn) {
        this.daysThisTurn = daysThisTurn;
    }

    /**
     * Retrieves the number of days spent in the current turn for the player.
     * @return the number of days spent in the current turn
     */
    public int getDaysThisTurn() {
        return daysThisTurn;
    }

    /**
     * Sets the ShipBoard instance for the player.
     *
     * @param shipBoard the ShipBoard instance to be assigned to the player
     */
    public void setShipBoard(ShipBoard shipBoard) {
        this.shipBoard = shipBoard;
    }
    /**
     * Sets the small adventure deck for the player and marks it as being watched.
     *
     * @param smallDeck the AdventureCardDeck to set as the small adventure deck
     */
    // da testare e cambiare
    public void setSmallDeck(AdventureCardDeck smallDeck) {
        smallAdventureDeckLooked = smallDeck;
        isWatching = true;

    }
    /**
     * Clears the small adventure deck reference and resets the watching state.
     */
    public void clearSmallDeck(){
        smallAdventureDeckLooked = null;
        isWatching = false;
    }
    /**
     * Determines if the shipboard's cabins are completely filled with tokens.
     *
     * @return true if all cabins on the shipboard are occupied by tokens, false otherwise.
     */
    public boolean shipBoardIsFullOfToken(){
        return shipBoard.cabineAreFullOfToken();
    }
}
