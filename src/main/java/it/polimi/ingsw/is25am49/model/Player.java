package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.shipboard.*;

import java.awt.*;

public class Player {
    private int daysThisTurn;
    private String username;
    private int playerNumber;
    private ShipBoard shipBoard;
    private Tile handTile;
    private int finalScore;
    private int totCredits;
    private int travelDays;
    private Color color;
    private int lostComponents;
    private int idPlayer;


    public Player(String username, Color color, int idPlayer) {
        this.username = username;
        this.color = color;
        daysThisTurn = 0;
        playerNumber = 0;
        shipBoard = new ShipBoard();
        handTile = null;
        finalScore = 0;
        totCredits = 0;
        travelDays = 0;
        lostComponents = 0;
        this.idPlayer = idPlayer;
    }
    //Qua bisogna calcolare lo Score finale
    public void calculateFinalScore(){

    }
    public int scoreOfGoods(){
        int total= shipBoard.getTotalNumberOfaToken(Token.RED_GOODS)*4+shipBoard.getTotalNumberOfaToken(Token.BLUE_GOODS)+shipBoard.getTotalNumberOfaToken(Token.YELLOW_GOODS)*3+shipBoard.getTotalNumberOfaToken(Token.GREEN_GOODS)*2;
        shipBoard.removeAllTokens();
        return total;
    }
    public void completeShipboard(){
        getShipBoard().completeShipBoard();
    }
    public void setHandTile(Tile tile){
        this.handTile = tile;
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

    public void setPlayerNumber(Integer playerNumber) {
        this.playerNumber = playerNumber;
    }
    public Tile getHandTile(){
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


    public void addCredit(int amount){
        totCredits = totCredits + amount;
    }

    public void pickTile(Point coordinates){
        handTile = shipBoard.removeTile(coordinates);
    }
    public void placeTile(Point coordinates){
        shipBoard.insertTile(coordinates, handTile);
    }
    public void rotateTile(boolean isClockwise){
        if(isClockwise)
            handTile.rotateTileClockwise();
        else
            handTile.rotateTileCounterClockwise();
    }
    public Tile discardTile(){
        Tile temp = handTile;
        handTile = null;
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


}
