package it.polimi.ingsw.is25am49.model.shipboard;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisComponentException;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalTokenInComponentEcxeption;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

        switch (level){
            case 1:
                try{
                    File file = new File("src/main/resources/it/polimi/ingsw/is25am49/shipboard_one_config.json");
                    if(!file.exists()){throw new FileNotFoundException("File(shipboard_two_config.json) non trovato: " + file.getAbsolutePath());}
                    else{
                        ObjectMapper objectMapper = new ObjectMapper();
                        Set<Point> points= objectMapper.readValue(file, new TypeReference<Set<Point>>(){});

                        for(Point p:points){grid.put(p,new Tile());}
                        grid.put(new Point(7,7),centerCabinTile);
                        //this.printShipboard();
                    }
                }catch (FileNotFoundException e) {
                    System.err.println("Errore: Il file di configurazione non è stato trovato.");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 2:
                try{
                    ObjectMapper objectMapper = new ObjectMapper();
                    File file = new File("src/main/resources/it/polimi/ingsw/is25am49/shipboard_two_config.json");
                    Set<Point> points= objectMapper.readValue(file,new TypeReference<Set<Point>>(){});

                    for(Point p:points){grid.put(p,new Tile());}
                    grid.put(new Point(7,7),centerCabinTile);
                    //this.printShipboard();

                }catch (FileNotFoundException e) {
                    System.err.println("Errore: Il file di configurazione non è stato trovato.");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break ;
        }
    }

    public void printShipboard(){
        for (int i = 5; i <= 9; i++) {
            if(i==5) System.out.println("\t4\t5\t6\t7\t8\t9\t10");
            for (int j = 4; j <=10 ; j++) {
                if(j==4) System.out.print(i+"\t");
                var p= new Point(j,i);
                if(grid.containsKey(p)) System.out.print(grid.get(p).getIdTile()+"\t");
                else System.out.print("•\t");
            }
            System.out.print(i+"\n");
        }
        System.out.println("\t4\t5\t6\t7\t8\t9\t10\n");
    }

    public Point getLastPlacedPoint() {return lastPlacedPoint;}
    public void setLastPlacedPoint(Point lastPlacedPoint) {this.lastPlacedPoint = lastPlacedPoint;}
    public boolean isCompleted() {return isCompleted;}
    public void completeShipBoard() {isCompleted = true;}

    //TODO metodo che restituisce la tessera + esterna per quando viene colpite
    //TODO metodo che restituisce un array di orientation che indica dove la nave è protetta

    public void setDestroyedTiles(int n) {this.destroyedTiles = n;}
    public int getDestroyedTiles() {return destroyedTiles;}
    public void addDestroyedTiles(int n) {this.destroyedTiles += n;}

    public boolean checkValidCoordinates(Point coordinates) {
        return grid.containsKey(coordinates);
    }
    public Tile getTile(Point coordinates){

        return grid.get(coordinates);
    }
    public void insertTile(Point coordinates, Tile tile){grid.put(coordinates, tile);}
    public Tile removeTile(Point coordinates){
        if (grid.get(coordinates).isLocked()) return grid.remove(coordinates);
        else return null;// eccezione
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
        catch(IllegalTokenInComponentEcxeption | ArrayIndexOutOfBoundsException e){
            System.out.println("cannot add "+token+" to " + grid.get(coordinates).getComponent().getType()+ " point<"+coordinates.x+","+coordinates.y+"> --> "+e.getMessage());
        }
        catch(IllegalMethodOnThisComponentException e){
            System.out.println("cannot add "+token+" to " + grid.get(coordinates).getComponent().getType()+ " point<"+coordinates.x+","+coordinates.y+"> -->"+e.getMessage());
        }
    }

    //method to remove a specific token from a tile
    public void removeTokenFromTile(Point coordinates, Token token){
        try{grid.get(coordinates).removeToken(token);}
        catch(IllegalMethodOnThisComponentException | ArrayIndexOutOfBoundsException e){
            System.out.println("cannot remove "+token+" from " + grid.get(coordinates).getComponent().getType()+ " point<"+coordinates.x+","+coordinates.y+"> --> "+e.getMessage());
        }
    }

    //method to consume a token of a tile (BATTERY)
    private void consumeComponentToken(Point donator){
        try{grid.get(donator).removeToken();}
        catch(IllegalMethodOnThisComponentException e){
            System.out.println("cannot donate a BATTERY" + grid.get(donator).getComponent().getType()+ " point<"+donator.x+","+donator.y+"> --> "+e.getMessage());
        }
        catch(ArrayIndexOutOfBoundsException e){
            System.out.println("not enough BATTERY in " + grid.get(donator).getComponent().getType()+ " point<"+donator.x+","+donator.y+"> --> "+e.getMessage());
        }
    }
    private void turnOnComponent(Point coordinates){
        try{grid.get(coordinates).getComponent().turnOn();}
        catch(IllegalMethodOnThisComponentException e){
            System.out.println("cannot turn on " + grid.get(coordinates).getComponent().getType()+ " point<"+coordinates.x+","+coordinates.y+">\n"+e);
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
        }else return null;//eccezione per pezzo sbagliato?
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
                .filter(entry -> entry.getValue().getComponent().getNumberOfaToken(token)>0)
                .flatMap(entry -> IntStream.range(0, entry.getValue().getComponent().getNumberOfaToken(token))
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
