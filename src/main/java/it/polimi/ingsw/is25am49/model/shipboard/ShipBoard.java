package it.polimi.ingsw.is25am49.model.shipboard;
import it.polimi.ingsw.is25am49.model.enums.Token;
import javafx.util.Pair;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;

public class ShipBoard {
    private final HashMap<Point,Tile> grid;
    private Point lastPlacedPoint;
    private boolean isCompleted;
    private int destroyedTiles;

    public ShipBoard(){
        grid = new HashMap<>();
        lastPlacedPoint = null;
        isCompleted = false;
        destroyedTiles = 0;
    }

    public Point getLastPlacedPoint() {
        return lastPlacedPoint;
    }
    public void setLastPlacedPoint(Point lastPlacedPoint) {
        this.lastPlacedPoint = lastPlacedPoint;
    }
    public boolean isCompleted() {
        return isCompleted;
    }
    public void completeShipBoard() {
        isCompleted = true;
    }

    public void setDestroyedTiles(int n) {
        this.destroyedTiles = n;
    }
    public int getDestroyedTiles() {
        return destroyedTiles;
    }
    public void addDestroyedTiles(int n) {
        this.destroyedTiles += n;
    }

    public Tile getTile(Point coordinates){
        return grid.get(coordinates);
    }
    public void insertTile(Point coordinates, Tile tile){
        grid.put(coordinates, tile);
    }
    public Tile removeTile(Point coordinates){
        return grid.remove(coordinates);
    }
    public void destroyTile(Point coordinates){
        destroyedTiles++;
        grid.remove(coordinates);
    }
    public void destroyTilesCascade(){
        //algoritmo mistico
    }
    public void lockTile(){
        grid.get(lastPlacedPoint).lockTile();
    }

    public void checkTileConnectors(Point point){
        //algoritmo mega mistico
    }
    public void checkTileConnectorsCascade(Point point){
        //non so se vale la pena implementarla
    }
    public int getNumberOfExposedConnectors(){
        //algoritmo mistico
        return 0;
    }

    public void addTokenToTile(Point coordinates, Token  token){
        grid.get(coordinates).getComponent().addToken(token);
    }
    public void removeTokenFromTile(Point coordinates, Token token){
        grid.get(coordinates).getComponent().removeToken(token);
    }

    public void consumeComponentToken(Point donator){
        if(grid.get(donator).getComponent().getNumberOfToken()>0) {
            grid.get(donator).getComponent().removeToken();
        }
    }
    public void turnOnComponent(Point coordinates){
        grid.get(coordinates).getComponent().turnOn();
    }
    public void turnOnComponent(Point coordinates, Point donator){
        turnOnComponent(coordinates);
        consumeComponentToken(donator);
    }

    public int getTotalFirePower(){
        //algoritmo mistico
        return 0;
    }
    public int getTotalRocketPower(){
        //algoritmo mistico
        return 0;
    }

    public int getTotalNumberOfaToken (Token token){
        //scorro griglia e sommo
        return 0;
    }



    public void checkRocketTile(Point coordinates){}
    public void checkCannonTile(Point coordinates){}
    public void afterBuildCheckShipboard(){
        //algoritmo mega mistico
    }
}
