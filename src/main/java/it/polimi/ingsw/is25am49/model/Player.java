package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.model.shipboard.Tile;

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


    public void set_handTile(Tile tile){
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

    public void add_lost_components(Integer numberOfLostComponents) {
        lostComponents = lostComponents + numberOfLostComponents;
    }

    public ShipBoard get_shipBoard() {
        return shipBoard;
    }

    public Integer get_score() {
        return finalsScore;
    }

    public void calculate_finalsScore() {

    }
    public void add_credit(int amount){
        totCredits = totCredits + amount;
    }

    public Tile pickTile(Point coordinates){
        return //shipBoard.getShip().getTile(coordinates);
    }
    public void place_tile(Point coordinates){

    }
    public void rotate_tile(){
        //handTile.rotate()
    }
    public Tile discard_tile(){

    }

    public Integer get_lost_components() {
        return lostComponents;
    }

    public Integer get_credits(){
        return totCredits;
    }
    public Color get_color() {
        return color;
    }
    

    public int get_days_this_turn() {
        return daysThisTurn;

    }


}
