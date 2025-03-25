package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.shipboard.*;

import java.awt.*;
import java.util.Optional;

public class Player {
    private int daysThisTurn;
    private String username;
    private int playerNumber;
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


    public Player(String username, Color color, int idPlayer) {
        this.username = username;
        this.color = color;
        daysThisTurn = 0;
        playerNumber = 0;
        shipBoard = new ShipBoard();
        handTile = Optional.empty();
        finalScore = 0;
        totCredits = 0;
        travelDays = 0;
        lostComponents = 0;
        this.idPlayer = idPlayer;
        isWatching = false;
    }
    //Qua bisogna calcolare lo Score finale
    public void calculateFinalScore(){

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
        int total= shipBoard.getTotalNumberOfaToken(Token.RED_GOODS)*4+shipBoard.getTotalNumberOfaToken(Token.BLUE_GOODS)+shipBoard.getTotalNumberOfaToken(Token.YELLOW_GOODS)*3+shipBoard.getTotalNumberOfaToken(Token.GREEN_GOODS)*2;
        shipBoard.removeAllTokens();
        return total;
    }
    public void completeShipboard(){
        shipBoard.completeShipBoard();
    }
    public void setHandTile(Tile tile){
        this.handTile = Optional.of(tile);
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getPlayerNumber() {
        return playerNumber;
    }

    public Optional<Tile> getHandTile(){
        return handTile;
    }
    public void addLostComponents(Integer numberOfLostComponents) {
        lostComponents = lostComponents + numberOfLostComponents;
    }

    public ShipBoard getShipBoard() {
        return shipBoard;
    }

    public Integer getScore() {
        return finalScore;
    }


    public void addCredits(int amount){
        totCredits = totCredits + amount;
    }

    public void pickTile(Point coordinates){
        handTile = Optional.of(shipBoard.removeTile(coordinates));
    }
    public void placeTile(Point coordinates){
        handTile.ifPresent(tile -> shipBoard.insertTile(coordinates, tile));
        handTile = Optional.empty();
    }
    public void rotateTile(boolean isClockwise){
        if(isClockwise)
            handTile.ifPresent(Tile::rotateTileClockwise);
        else
            handTile.ifPresent(Tile::rotateTileCounterClockwise);
    }
    public Optional<Tile> discardTile(){
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
    public void setSmallDeck(AdventureCardDeck smallDeck) {
        smallAdventureDeckLooked = smallDeck;
        isWatching = true;
    }
    public void clearSmallDeck(){
        smallAdventureDeckLooked = null;
        isWatching = false;
    }

}
