package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.shipboard.*;

import java.awt.*;

public class Player {
    private int daysThisTurn=0;
    private String username;
    private int playerNumber;
    private ShipBoard shipBoard;
    private Tile handTile;
    private int finalsScore=0;
    private int totCredits=0;
    private int travelDays=0;
    private Color color;
    private int lostComponents=0;


    public Player(int daysThisTurn, String username, int playerNumber, ShipBoard shipBoard, Tile handTile, int finalsScore,int totCredits,int travelDays,Color color,int lostComponents) {
        this.daysThisTurn = daysThisTurn;
        this.username = username;
        this.playerNumber = playerNumber;
        this.shipBoard = shipBoard;
        this.handTile = handTile;
        this.finalsScore = finalsScore;
        this.totCredits = totCredits;
        this.travelDays = travelDays;
        this.color = color;
        this.lostComponents = lostComponents;


    }
    public int scoreOfGoods(){
        int totale= shipBoard.getTotalNumberOfaToken(Token.RED_GOODS)*4+shipBoard.getTotalNumberOfaToken(Token.BLUE_GOODS)+shipBoard.getTotalNumberOfaToken(Token.YELLOW_GOODS)*3+shipBoard.getTotalNumberOfaToken(Token.GREEN_GOODS)*2;
        shipBoard.removeAllTokens();
        return totale;
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
        return finalsScore;
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
    public void rotateTile(){
        handTile.setTileRotation(handTile.getTileRotation());
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


    public int getDaysThisTurn() {
        return daysThisTurn;

    }


}
