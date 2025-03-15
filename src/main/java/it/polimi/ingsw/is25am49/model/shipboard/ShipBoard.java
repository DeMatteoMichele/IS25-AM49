package it.polimi.ingsw.is25am49.model.shipboard;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisComponentException;

import java.awt.Point;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class ShipBoard {
    private final HashMap<Point,Tile> grid;
    private Point lastPlacedPoint;
    private boolean isCompleted;
    private int destroyedTiles;

    public ShipBoard() {
        grid = new HashMap<>();
        lastPlacedPoint = null;
        isCompleted = false;
        destroyedTiles = 0;
    }
    public ShipBoard(int level, int playerID){
        grid = new HashMap<>();
        lastPlacedPoint = null;
        isCompleted = false;
        destroyedTiles = 0;

        var centerCabinTile =new Tile(playerID, new ContainerComponent(ComponentType.CABINE,2));
        grid.put(new Point(7,7),centerCabinTile);

        switch (level){
            case 1:
                break;
            case 2:
                break ;
        }
    }

    public Point getLastPlacedPoint() {
        return lastPlacedPoint;
    }
    public void setLastPlacedPoint(Point lastPlacedPoint) {this.lastPlacedPoint = lastPlacedPoint;}
    public boolean isCompleted() {
        return isCompleted;
    }
    public void completeShipBoard() {
        isCompleted = true;
    }

    public void setDestroyedTiles(int n) {this.destroyedTiles = n;}
    public int getDestroyedTiles() {
        return destroyedTiles;
    }
    public void addDestroyedTiles(int n) {
        this.destroyedTiles += n;
    }

    public void checkValidPosition(Point p){

    }
    public Tile getTile(Point coordinates){
        return grid.get(coordinates);
    }
    public void insertTile(Point coordinates, Tile tile){grid.put(coordinates, tile);}
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
        try{grid.get(coordinates).addToken(token);}
        catch(IllegalMethodOnThisComponentException | ArrayIndexOutOfBoundsException e){
            System.out.println("cannot add "+token+" from " + grid.get(coordinates).getComponent()+ " point<"+coordinates.x+","+coordinates.y+">\n"+e);
        }
    }

    //method to remove a specific token from a tile
    public void removeTokenFromTile(Point coordinates, Token token){
        try{grid.get(coordinates).removeToken(token);}
        catch(IllegalMethodOnThisComponentException | ArrayIndexOutOfBoundsException e){
            System.out.println("cannot remove "+token+" from " + grid.get(coordinates).getComponent()+ " point<"+coordinates.x+","+coordinates.y+">\n"+e);
        }
    }

    //method to consume a token of a tile (BATTERY)
    private void consumeComponentToken(Point donator){
        try{grid.get(donator).removeToken();}
        catch(IllegalMethodOnThisComponentException e){
            System.out.println("cannot donate a BATTERY" + grid.get(donator).getComponent()+ " point<"+donator.x+","+donator.y+">\n"+e);
        }
        catch(ArrayIndexOutOfBoundsException e){
            System.out.println("not enough BATTERY in " + grid.get(donator).getComponent()+ " point<"+donator.x+","+donator.y+">\n"+e);
        }
    }
    private void turnOnComponent(Point coordinates){
        try{grid.get(coordinates).getComponent().turnOn();}
        catch(IllegalMethodOnThisComponentException e){
            System.out.println("cannot turn on " + grid.get(coordinates).getComponent()+ " point<"+coordinates.x+","+coordinates.y+">\n"+e);
        }
    }
    public void turnOnComponent(Point coordinates, Point donator){
        turnOnComponent(coordinates);
        consumeComponentToken(donator);
    }
    public ArrayList<Point> getActivatableComponents(ComponentType type){
        if(type.equals(ComponentType.DOUBLE_CANNON) || type.equals(ComponentType.DOUBLE_ENGINE) || type.equals(ComponentType.SHIELD)){
            return grid.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().getComponent().getType().equals(type))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toCollection(ArrayList::new));
        }else return null;//eccezione?
    }

    public double getTotalFirePower(){
        double sum=0;
        sum+= grid.values()
                .stream()
                .filter((Tile t) -> t.getComponent().getType().equals(ComponentType.CANNON))
                .mapToDouble(Tile::getFirePower)
                .sum();
        sum+=grid.values()
                .stream()
                .filter((Tile t) -> t.getComponent().getType().equals(ComponentType.DOUBLE_CANNON))
                .filter((Tile t) -> t.getComponent().getIsActive())
                .mapToDouble(Tile::getFirePower)
                .sum();
        return sum;
    }
    public int getEnginePower(){
        int sum=0;
        sum+= grid.values()
                .stream()
                .filter((Tile t) -> t.getComponent().getType().equals(ComponentType.ENGINE))
                .mapToInt(t->t.getComponent().getLevel())
                .sum();
        sum+= grid.values()
                .stream()
                .filter((Tile t) -> t.getComponent().getType().equals(ComponentType.DOUBLE_ENGINE))
                .filter((Tile t) -> t.getComponent().getIsActive())
                .mapToInt(t->t.getComponent().getLevel())
                .sum();
        return sum;
    }

    public int getTotalNumberOfaToken (Token token){
        return getPositionsOfaToken(token).size();
    }
    public ArrayList<Point> getPositionsOfaToken(Token token){
        return grid.entrySet().stream()
                .filter(entry -> entry.getValue().getComponent().checkIfTokenIsValid(token))
                .filter(entry -> entry.getValue().getComponent().getNumberOfTokens()>0)
                .flatMap(entry -> IntStream.range(0, entry.getValue().getComponent().getNumberOfTokens())
                        .mapToObj(i -> entry.getKey()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
    public void removeAllTokens(){}

    public void checkRocketTile(Point coordinates){}
    public void checkCannonTile(Point coordinates){}
    public void afterBuildCheckShipboard(){
        //algoritmo mega mistico
    }
}
