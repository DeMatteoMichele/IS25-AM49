package it.polimi.ingsw.is25am49.server.model;

import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.server.model.shipboard.*;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;

import java.awt.*;
import java.util.Optional;

public class Player {
    private int daysThisTurn;
    private String username;
    private ShipBoard shipBoard;
    private Optional<Tile> handTile;
    private int finalScore;
    private int totCredits;
    private int travelDays;
    private Color color;
    private int lostComponents;
    private final int idPlayer;
    private AdventureCardDeck smallAdventureDeckLooked;
    private boolean isWatching;
    private boolean theCrewHasBeenPlaced;


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
    }

    private void thereIsHandTile(){
        if(handTile.isEmpty())
            throw new IllegalStateException("There is no tile in hand");
    }
    public void shipBoardIsNotCompletedYet(){
        if(shipBoard.isCompleted())
            throw new IllegalStateException("Shipboard is already completed");
    }
    public boolean isWatching(){
        return isWatching;
    }
    public void lockLastTile(){
        shipBoard.lockLastTile();
    }
    public AdventureCardDeck getSmallAdventureDeck(){
        return smallAdventureDeckLooked;
    }
    public int scoreOfGoods(){
        int total= shipBoard.getTotalNumberOfaToken(Token.RED_GOODS)*4+
                shipBoard.getTotalNumberOfaToken(Token.YELLOW_GOODS)*3+
                shipBoard.getTotalNumberOfaToken(Token.GREEN_GOODS)*2+
                shipBoard.getTotalNumberOfaToken(Token.BLUE_GOODS);
        shipBoard.removeAllTokens();
        return total;
    }
    public void completeShipboard(){
        shipBoard.completeShipBoard();
    }
    public void setHandTile(Tile tile){
        handTile = Optional.of(tile);
    }

    public String getUsername() {
        return username;
    }
//    public void setUsername(String username) {
//        this.username = username;
//    }

    public Integer getPlayerNumber() {
        return idPlayer;
    }

    public Optional<Tile> getHandTile(){
        return handTile;
    }
    public void addLostComponents(int numberOfLostComponents) {
        lostComponents = lostComponents + numberOfLostComponents;
    }

    public ShipBoard getShipBoard() {
        return shipBoard;
    }

    public Integer getScore() {
        return finalScore;
    }
//    public void setScore(int score) {
//        finalScore = score;
//    }

    public void addCredits(int amount){
        totCredits = totCredits + amount;
    }
    // da testare
    public void pickTile(Point coordinates){
        shipBoardIsNotCompletedYet();
        if(handTile.isEmpty())
            handTile = Optional.of(shipBoard.removeTile(coordinates));
        else
            throw new GameSettingsException("You cannot pick this tile");
    }
    // da testare
    public void placeTile(Point coordinates){
        thereIsHandTile();
        shipBoardIsNotCompletedYet();
        handTile.ifPresent(tile -> shipBoard.insertTile(coordinates, tile));
        handTile = Optional.empty();
    }
    // da testare e cambiare
    public void rotateTile(boolean isClockwise){
        shipBoardIsNotCompletedYet();
        if(isClockwise)
            handTile.ifPresent(Tile::rotateTileClockwise);
        else
            handTile.ifPresent(Tile::rotateTileCounterClockwise);
    }
    // da testare
    public Optional<Tile> discardTile(){
        thereIsHandTile();
        Optional<Tile> temp = handTile;
        handTile = Optional.empty();
        return temp;
    }
    public Integer getLostComponents() {
        return lostComponents;
    }

    public Integer getCredits(){
        return totCredits;
    }
    public Color getColor() {
        return color;
    }

    public void setDaysThisTurn(int daysThisTurn) {
        this.daysThisTurn = daysThisTurn;
    }

    public int getDaysThisTurn() {
        return daysThisTurn;
    }

    public void setShipBoard(ShipBoard shipBoard) {
        this.shipBoard = shipBoard;
    }
    // da testare e cambiare
    public void setSmallDeck(AdventureCardDeck smallDeck) {
        smallAdventureDeckLooked = smallDeck;
        isWatching = true;

    }
    public void clearSmallDeck(){
        smallAdventureDeckLooked = null;
        isWatching = false;
    }

}
