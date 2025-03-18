package it.polimi.ingsw.is25am49.model.shipboard;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.ConnectorType;
import it.polimi.ingsw.is25am49.model.enums.Orientation;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisComponentException;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalTokenInComponentEcxeption;
import it.polimi.ingsw.is25am49.model.exceptions.InvalidCoordinatesException;

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
        centerCabinTile.setConnectors(ConnectorType.UNIVERSAL,ConnectorType.UNIVERSAL,ConnectorType.UNIVERSAL,ConnectorType.UNIVERSAL);

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

    public Point getLastPlacedPoint() {return lastPlacedPoint;}
    public boolean isCompleted() {return isCompleted;}
    public void completeShipBoard() {isCompleted = true;}

    public void setDestroyedTiles(int n) {this.destroyedTiles = n;}
    public int getDestroyedTiles() {return destroyedTiles;}
    public void addDestroyedTiles(int n) {this.destroyedTiles += n;}

    public boolean checkValidCoordinates(Point coordinates) {
        return grid.containsKey(coordinates);
    }
    public boolean connectorsMatch(ConnectorType c1, ConnectorType c2){
        if( c1.equals(ConnectorType.UNIVERSAL) || c2.equals(ConnectorType.UNIVERSAL))return true;
        if (c1.equals(ConnectorType.FLAT) || c2.equals(ConnectorType.FLAT))return false;
        return c1.equals(c2);
    }
    public Tile getTile(Point coordinates){
        if(checkValidCoordinates(coordinates)){
            return grid.get(coordinates);
        } else {
            throw new InvalidCoordinatesException("there is no tile in point<" + coordinates.x + "," + coordinates.y + "> --> out of shipboard");
        }
    }
    //TODO non sono sicuro servano
    public boolean checkTileIsActive(Point coordinates){return false; }
    public void turnOnTile(Point coordinates){}
    public void turnOffTile(Point coordinates){}

    //metodo che restituisce il punto della nave  più esterno in una determinata posizione
    private Optional<Point> getExternalTilePoint(int position, Orientation orientation){
        return switch (orientation) {
            case NORTH -> grid.keySet().stream()
                    .filter((Point p) -> p.x == position)
                    .min(Comparator.comparing(p -> p.y));
            case EAST -> grid.keySet().stream()
                    .filter((Point p) -> p.y == position)
                    .max(Comparator.comparing(p -> p.x));
            case SOUTH -> grid.keySet().stream()
                    .filter((Point p) -> p.x == position)
                    .max(Comparator.comparing(p -> p.y));
            case WEST -> grid.keySet().stream()
                    .filter((Point p) -> p.y == position)
                    .min(Comparator.comparing(p -> p.x));
        };
    }
    //metodo che restituisce la tesserao della nave  più esterna in una determinata posizione
    public Optional<Tile> getExternalTile(int position, Orientation orientation){
        return getExternalTilePoint(position,orientation).flatMap(point -> Optional.ofNullable(grid.get(point)));
    }
    //se inserisco una Tile nella nave aggiorno lastPlacedPoint
    public void insertTile(Point coordinates, Tile tile) throws InvalidCoordinatesException {
        if(checkValidCoordinates(coordinates)){
            grid.put(coordinates, tile);
            lastPlacedPoint=coordinates;
        } else {
            throw new InvalidCoordinatesException("cannot INSERT a tile in point<" + coordinates.x + "," + coordinates.y + "> --> out of shipboard");
        }
    }
    public Tile removeTile(Point coordinates){
        if(checkValidCoordinates(coordinates)){
            if (grid.get(coordinates).isLocked()) return grid.remove(coordinates);
            else throw new InvalidCoordinatesException("cannot REMOVE a tile i<" + coordinates.x + "," + coordinates.y + "> --> tile is LOKED");
        }throw new InvalidCoordinatesException("cannot REMOVE a tile in point<" + coordinates.x + "," + coordinates.y + "> --> out of shipboard");
    }
    public void destroyTile(Point coordinates){
        if(checkValidCoordinates(coordinates)){
            destroyedTiles++;
            grid.remove(coordinates);
        } else {
            throw new InvalidCoordinatesException("cannot DESTROY a tile in point<" + coordinates.x + "," + coordinates.y + "> --> out of shipboard");
        }
    }
    public void destroyTilesCascade(){
        //TODO algoritmo mistico
    }
    //public void lockTile(Point coordinates){} secondo me non serve

    //restituisce il numero di connettori che matchano in una tessera
    public int checkTileConnectors(Point p){
        int n=0;
        Tile t;
        try{
            t= this.getTile(new Point(p.x,p.y+1));
            if(connectorsMatch(grid.get(p).getConnector(0),t.getConnector(2))) n++;
        }
        catch (InvalidCoordinatesException _){}
        try{
            t= this.getTile(new Point(p.x+1,p.y));
            if(connectorsMatch(grid.get(p).getConnector(1),t.getConnector(3))) n++;
        }
        catch (InvalidCoordinatesException _){}
        try{
            t= this.getTile(new Point(p.x,p.y-1));
            if(connectorsMatch(grid.get(p).getConnector(2),t.getConnector(0))) n++;
        }
        catch (InvalidCoordinatesException _){}
        try{
            t= this.getTile(new Point(p.x-1,p.y));
            if(connectorsMatch(grid.get(p).getConnector(3),t.getConnector(1))) n++;
        }
        catch (InvalidCoordinatesException _){}
        return n;
    }
    public void checkTileConnectorsCascade(Point point){
        //TODO non so se vale la pena implementarla
    }

    //ricerca connettori esposti
    public int getNumberOfExposedConnectors(){
        Set<Point> external = new HashSet<>();
        //TODO sistema non trovo tutte le tessere che mi servono
        for (int i = 4; i <= 10; i++) {
            this.getExternalTilePoint(i,Orientation.NORTH).ifPresent(external::add);
            this.getExternalTilePoint(i,Orientation.SOUTH).ifPresent(external::add);
        }
        for (int i = 5; i <=9 ; i++) {
            this.getExternalTilePoint(i,Orientation.EAST).ifPresent(external::add);
            this.getExternalTilePoint(i,Orientation.WEST).ifPresent(external::add);
        }

        int exposedConnectors=0;
        for (Point p:external){
            exposedConnectors+=4-checkTileConnectors(p);
        }
        return exposedConnectors;
    }
    public void addTokenToTile(Point coordinates, Token  token){
        if(checkValidCoordinates(coordinates)) {
            try {
                grid.get(coordinates).addToken(token);
            } catch (IllegalTokenInComponentEcxeption | ArrayIndexOutOfBoundsException |
                     IllegalMethodOnThisComponentException e) {
                System.out.println("cannot add " + token + " to " + grid.get(coordinates).getComponent().getType() + " point<" + coordinates.x + "," + coordinates.y + "> --> " + e.getMessage());
            }
        }else throw new InvalidCoordinatesException("cannot add "+token+ "  in point<" + coordinates.x + "," + coordinates.y + "> --> out of shipboard");
    }
    public void fillTileComponent(Point coordinates, Token token){
        if(checkValidCoordinates(coordinates)) {
            try{grid.get(coordinates).fillComponent(token);}
            catch (IllegalMethodOnThisComponentException | IllegalTokenInComponentEcxeption e){
                System.out.println("cannot fill " + grid.get(coordinates).getComponent().getType() + " with token " + token + ": " + e.getMessage());
            }
        }else throw new InvalidCoordinatesException("cannot FILL point<" + coordinates.x + "," + coordinates.y + "> --> out of shipboard");
    }
    //method to remove a specific token from a tile
    public void removeTokenFromTile(Point coordinates, Token token){
        if(checkValidCoordinates(coordinates)) {
            try{grid.get(coordinates).removeToken(token);}
            catch (IllegalTokenInComponentEcxeption | ArrayIndexOutOfBoundsException |
                   IllegalMethodOnThisComponentException e) {
                System.out.println("cannot remove "+token+" from " + grid.get(coordinates).getComponent().getType()+ " point<"+coordinates.x+","+coordinates.y+"> --> "+e.getMessage());
            }
        }else throw new InvalidCoordinatesException("cannot ADD "+token+ "  in point<" + coordinates.x + "," + coordinates.y + "> --> out of shipboard");
    }

    //method to consume a token of a tile (BATTERY)
    private void consumeComponentToken(Point donator){
        if(checkValidCoordinates(donator)) {
            if(grid.get(donator).getComponent().getType().equals(ComponentType.BATTERY)) {
                try {
                    grid.get(donator).removeToken();
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("not enough BATTERY in " + grid.get(donator).getComponent().getType() + " point<" + donator.x + "," + donator.y + "> --> " + e.getMessage());
                }
            }else throw new IllegalMethodOnThisComponentException("cannot donate a BATTERY" + grid.get(donator).getComponent().getType() + " point<" + donator.x + "," + donator.y+">");
        }else throw new InvalidCoordinatesException("cannot use a Battery from point<" + donator.x + "," + donator.y + "> --> out of shipboard");

    }
    private void turnOnComponent(Point coordinates){
        if(checkValidCoordinates(coordinates)) {
            try{grid.get(coordinates).turnOn();}
            catch(IllegalMethodOnThisComponentException e){
                System.out.println("cannot turn on " + grid.get(coordinates).getComponent().getType()+ " point<"+coordinates.x+","+coordinates.y+">\n"+e);
            }
        }else throw new InvalidCoordinatesException("cannot turn ON " + grid.get(coordinates).getComponent().getType()+ " point<" + coordinates.x + "," + coordinates.y + "> --> out of shipboard");
    }
    //metodo che rimuove una batteria dal donator e attiva coordinates
    public void turnOnComponent(Point coordinates, Point donator){
        turnOnComponent(coordinates);
        consumeComponentToken(donator);
    }
    public void turnOff(Point coordinates){
        if(checkValidCoordinates(coordinates)) {
            grid.get(coordinates).turnOff();
        }else throw new InvalidCoordinatesException("cannot turn OFF " + grid.get(coordinates).getComponent().getType()+ " point<" + coordinates.x + "," + coordinates.y + "> --> out of shipboard");
    }
    public ArrayList<Point> getActivatableComponents(ComponentType type){
        if(type.equals(ComponentType.DOUBLE_CANNON) || type.equals(ComponentType.DOUBLE_ENGINE) || type.equals(ComponentType.SHIELD)){
            return grid.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().getComponent().getType().equals(type))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toCollection(ArrayList::new));
        }else throw new IllegalMethodOnThisComponentException("cannot get activatable components of type " + type);
    }

    public double getTotalFirePower(){
        return grid.values()
                .stream()
                .filter((Tile t) -> t.getComponent().getType().equals(ComponentType.CANNON)
                        || (t.getComponent().getType().equals(ComponentType.DOUBLE_CANNON) && t.getComponent().getIsActive()))
                .mapToDouble(Tile::getFirePower)
                .sum();
    }
    public int getEnginePower(){
        return grid.values()
                .stream()
                .filter((Tile t) -> t.getComponent().getType().equals(ComponentType.ENGINE)
                        || (t.getComponent().getType().equals(ComponentType.DOUBLE_ENGINE) && t.getComponent().getIsActive()))
                .mapToInt(t->t.getComponent().getLevel())
                .sum();
    }

    public int getTotalNumberOfaToken (Token token){
        return getPositionsOfaToken(token).size();
    }
    public ArrayList<Point> getPositionsOfaToken(Token token){
        return grid.entrySet().stream()
                .filter(entry -> entry.getValue().getNumberOfaToken(token)>0)
                .flatMap(entry -> IntStream.range(0, entry.getValue().getNumberOfaToken(token))
                        .mapToObj(i -> entry.getKey()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
    public void removeAllTokens(){
        for (Tile t: grid.values()){
            try{t.clearComponent();}
            catch (IllegalMethodOnThisComponentException _){}
        }
    }

    //metodo che restituisce un set di orientation che indica dove la nave è protetta
    public Set<Orientation> getProtectedSides(){
        Set<Orientation> sides= new HashSet<>();
        ArrayList<Point> shields= this.getActivatableComponents(ComponentType.SHIELD);
        for(Point p: shields){
            if(sides.size()==4)break;
            if(grid.get(p).isActive()) {
                switch (grid.get(p).getTileOrientation()) {
                    case NORTH -> {
                        sides.add(Orientation.NORTH);
                        sides.add(Orientation.EAST);
                    }
                    case EAST -> {
                        sides.add(Orientation.EAST);
                        sides.add(Orientation.SOUTH);
                    }
                    case SOUTH -> {
                        sides.add(Orientation.SOUTH);
                        sides.add(Orientation.WEST);
                    }
                    case WEST -> {
                        sides.add(Orientation.WEST);
                        sides.add(Orientation.NORTH);
                    }
                }
            }
        }
        return sides;
    }
    public void checkRocketTile(Point coordinates){}
    public void checkCannonTile(Point coordinates){}
    public void afterBuildCheckShipboard(){
        //TODO algoritmo mega mistico
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
    public void printShipboardSpecial(){
        int row;
        for (int i = 5; i <= 9; i++) {
            if(i==5) System.out.println("\t\t4\t\t\t5\t\t\t6\t\t\t7\t\t\t8\t\t\t9\t\t\t10\t\t\t");
            row=0;
            while(row<3) {
                for (int j = 4; j <= 10; j++) {
                    if(j==4 && row==1) System.out.print(i+"\t");
                    else if (j==4) System.out.print("\t");

                    var p = new Point(j, i);
                    if (grid.containsKey(p)){
                        var t= grid.get(p);
                        if(row==0) {
                            System.out.print("\uD83D\uDFE6\t");
                            printConnector(t.getConnector(Orientation.NORTH));
                            System.out.print("\uD83D\uDFE6\t");
                        }
                        if (row==1){
                            printConnector(t.getConnector(Orientation.WEST));
                            printComponent(t.getComponent().type);
                            printConnector(t.getConnector(Orientation.EAST));
                        }
                        if (row==2){
                            System.out.print("\uD83D\uDFE6\t");
                            printConnector(t.getConnector(Orientation.SOUTH));
                            System.out.print("\uD83D\uDFE6\t");
                        }
                    } else {
                        System.out.print("\uD83D\uDFE6\t");
                        System.out.print("\uD83D\uDFE6\t");
                        System.out.print("\uD83D\uDFE6\t");
                    }
                }
                if(row==1) System.out.println("  "+i);
                else System.out.println("   ");
                row++;
            }
        }
        System.out.println("\t\t4\t\t\t5\t\t\t6\t\t\t7\t\t\t8\t\t\t9\t\t\t10\t\t\t\n");
    }
    private void printConnector(ConnectorType c){
        switch (c){
            case FLAT -> System.out.print("\uD83D\uDFE6\t");
            case UNIVERSAL ->  System.out.print("⚂\t");
            case DOUBLE ->  System.out.print("⚁\t");
            case SINGLE ->   System.out.print("⚀\t");
        }
    }
    private void printComponent(ComponentType c){
        switch (c){
            case CABINE -> System.out.print("\uD83D\uDEDE\t");
            case BATTERY ->  System.out.print("\uD83D\uDD0B\t");
            case SKY ->  System.out.print("⬛\t");
            case STORAGE -> System.out.print("\uD83D\uDCE6\t");
            case SHIELD ->  System.out.print("\uD83D\uDEE1\uFE0F\t");
            default -> System.out.print("\uD83D\uDFE6\t");
        }
    }
}
