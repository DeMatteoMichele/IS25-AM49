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


public class ShipBoard {
    private final HashMap<Point,Optional<Tile>> grid;
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

                        for(Point p:points){grid.put(p,Optional.empty());}
                        grid.put(new Point(7,7),Optional.of(centerCabinTile));
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

                    for(Point p:points){grid.put(p,Optional.empty());}
                    grid.put(new Point(7,7),Optional.of(centerCabinTile));
                    //this.printShipboard();

                }catch (FileNotFoundException e) {
                    System.err.println("Errore: Il file di configurazione non è stato trovato.");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break ;
        }
    }

    /**
     * Locks the last tile placed in the shipboard
     */
    public void lockLastTile(){
        grid.get(lastPlacedPoint).ifPresent(Tile::lockTile);
    }
    public Point getLastPlacedPoint() {return lastPlacedPoint;}
    public boolean isCompleted() {return isCompleted;}
    public void completeShipBoard() {
        isCompleted = true;
        Set<Point> elimina = grid.entrySet().stream().filter(entry -> entry.getValue().isEmpty() )
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        for(Point p: elimina){
            destroyTile(p);
        }
        for(Point p: grid.keySet()){
            //TODO LOKKA TUTTE LE TESSERE PER SICUREZZA
        }
    }

    public void setDestroyedTiles(int n) {this.destroyedTiles = n;}
    public int getDestroyedTiles() {return destroyedTiles;}
    public void addDestroyedTiles(int n) {this.destroyedTiles += n;}

    public boolean connectorsMatch(ConnectorType c1, ConnectorType c2){
        if (c1.equals(ConnectorType.FLAT) || c2.equals(ConnectorType.FLAT))return false;
        if( c1.equals(ConnectorType.UNIVERSAL) || c2.equals(ConnectorType.UNIVERSAL))return true;
        return c1.equals(c2);
    }

    /**
     * returns the Tile at given coordinates if present
     * @param coordinates of the desired Tile
     * @return Tile
     *///Testato ✔
    public Tile getTile(Point coordinates){
        if(grid.containsKey(coordinates)) {
            return grid.get(coordinates).orElseThrow(()-> new InvalidCoordinatesException("tile is empty"));
        }else throw new InvalidCoordinatesException("out of shipboard");
    }

    //TODO fare un metodo tessere esterne che prende tutti i punti a contatto con l'aria, cambiare il metodo
    //TODO sotto in exposedTiles sfruttando i punti ricavati sopra

    /**
     * this method returns external point of the ShipBoard
     * @return an ArrayList of points that are the border of the Shipboard
     *///Testato ✔
    public ArrayList<Point> getExternalPoints(){
        ArrayList<Point> externalPoints = new ArrayList<>();
        for(Point p:grid.keySet()){
            if (grid.get(p).isPresent()){
                try{
                    getTile(new Point(p.x,p.y+1));
                    getTile(new Point(p.x+1,p.y));
                    getTile(new Point(p.x,p.y-1));
                    getTile(new Point(p.x-1,p.y));
                }catch (InvalidCoordinatesException _){
                    externalPoints.add(p);
                }
            }
        }
        return externalPoints;
    }

    /**
     * methotd that return the point
     *
     */
    private Optional<Point> getExposedPoint(int position, Orientation orientation){
        Set<Point> points = new HashSet<>(getExternalPoints());
        return switch (orientation) {
            case NORTH -> points.stream()
                    .filter((Point p) -> p.x == position)
                    .min(Comparator.comparing(p -> p.y));
            case EAST -> points.stream()
                    .filter((Point p) -> p.y == position)
                    .max(Comparator.comparing(p -> p.x));
            case SOUTH -> points.stream()
                    .filter((Point p) -> p.x == position)
                    .max(Comparator.comparing(p -> p.y));
            case WEST -> points.stream()
                    .filter((Point p) -> p.y == position)
                    .min(Comparator.comparing(p -> p.x));
        };
    }
    //metodo che restituisce la tessera della nave più esterna in una determinata posizione
    public Optional<Tile> getExposedTile(int position, Orientation orientation){
        return getExposedPoint(position,orientation).flatMap(grid::get);
    }

    /**
     * inserts a tile in the ShipBoard if the coordinates are free
     * @param coordinates indicating where to place the Tile
     * @param tile to place in the SipBoard
     */
    public void insertTile(Point coordinates, Tile tile) {
        if(grid.containsKey(coordinates)){
            if(grid.get(coordinates).isEmpty()){
                grid.put(coordinates, Optional.of(tile));
                lastPlacedPoint=coordinates;
            }else throw new InvalidCoordinatesException("cannot INSERT a tile in point<" + coordinates.x + "," + coordinates.y + "> --> tile already present");
        } else throw new InvalidCoordinatesException("cannot INSERT a tile in point<" + coordinates.x + "," + coordinates.y + "> --> out of shipboard");
    }

    /**
     * removes the tile at given coordinates from the SipBoard if present
     * @param coordinates of the Tile to remove
     * @return the removed Tile
     */
    public Tile removeTile(Point coordinates){
        try{
            Tile t = getTile(coordinates);
            if (!t.isLocked()){
                grid.remove(coordinates);
                return t;
            }
            else throw new InvalidCoordinatesException("cannot REMOVE a tile i<" + coordinates.x + "," + coordinates.y + "> --> tile is locked");
        } catch (InvalidCoordinatesException e) {
            throw new InvalidCoordinatesException("cannot REMOVE a tile in point<" + coordinates.x + "," + coordinates.y + "> --> "+e.getMessage());
        }
    }

    /**
     * Destroy a Tile in the ShipBoard
     * removes the tile at given coordinates from the map and increase destroyedTiles attribute
     * @param coordinates of the Tile to Destroy
     */
    public void destroyTile(Point coordinates){
        if(grid.containsKey(coordinates)){
            if(grid.get(coordinates).isPresent()) destroyedTiles++;
            grid.remove(coordinates);
        } else {
            throw new InvalidCoordinatesException("cannot DESTROY a tile in point<" + coordinates.x + "," + coordinates.y + "> --> out of shipboard");
        }
    }
    /**
     *
     */
    private void checkIfTileIsConnected(Point point, Orientation direction, Set<Point> connectedTiles){
        try {
            Tile centerTile= getTile(point);
            Tile sideTile;
            switch (direction) {
                case NORTH:
                    try {
                        Point p= new Point(point.x,point.y+1);
                        if(connectedTiles.contains(p)) return;
                        sideTile=getTile(p);
                        if(connectorsMatch(centerTile.getConnector(Orientation.NORTH),sideTile.getConnector(Orientation.SOUTH))){
                                connectedTiles.add(p);
                                checkIfTileIsConnected(p,Orientation.NORTH,connectedTiles);
                                checkIfTileIsConnected(p,Orientation.EAST,connectedTiles);
                                checkIfTileIsConnected(p,Orientation.WEST,connectedTiles);
                        }return;
                    }catch (InvalidCoordinatesException _){return;}
                case EAST:
                    try {
                        Point p= new Point(point.x+1,point.y);
                        if(connectedTiles.contains(p)) return;
                        sideTile=getTile(p);
                        if(connectorsMatch(centerTile.getConnector(Orientation.EAST),sideTile.getConnector(Orientation.WEST))){
                            connectedTiles.add(p);
                            checkIfTileIsConnected(p,Orientation.EAST,connectedTiles);
                            checkIfTileIsConnected(p,Orientation.SOUTH,connectedTiles);
                            checkIfTileIsConnected(p,Orientation.NORTH,connectedTiles);
                        }return;
                    }catch (InvalidCoordinatesException _){return;}
                case SOUTH:
                    try {
                        Point p= new Point(point.x,point.y-1);
                        if(connectedTiles.contains(p)) return;
                        sideTile=getTile(p);
                        if(connectorsMatch(centerTile.getConnector(Orientation.SOUTH),sideTile.getConnector(Orientation.NORTH))){
                            connectedTiles.add(p);
                            checkIfTileIsConnected(p,Orientation.SOUTH,connectedTiles);
                            checkIfTileIsConnected(p,Orientation.WEST,connectedTiles);
                            checkIfTileIsConnected(p,Orientation.EAST,connectedTiles);
                        }return;
                    }catch (InvalidCoordinatesException _){return;}
                case WEST:
                    try {
                        Point p= new Point(point.x-1,point.y);
                        if(connectedTiles.contains(p)) return;
                        sideTile=getTile(p);
                        if(connectorsMatch(centerTile.getConnector(Orientation.WEST),sideTile.getConnector(Orientation.EAST))){
                            connectedTiles.add(p);
                            checkIfTileIsConnected(p,Orientation.WEST,connectedTiles);
                            checkIfTileIsConnected(p,Orientation.NORTH,connectedTiles);
                            checkIfTileIsConnected(p,Orientation.SOUTH,connectedTiles);
                        }return;
                    }catch (InvalidCoordinatesException _){return;}
            }
        }catch (InvalidCoordinatesException _){return;}
    }
    public Set<Point> getTilesConnectedTo(Point center){
        Set<Point> connectedTiles =new HashSet<>();
        if(!grid.containsKey(center)) throw new InvalidCoordinatesException("cannot checkTileConnectorsCascade --> out of shipboard"); //DA RIVEDERE L'ECCEZIONE
        connectedTiles.add(center);
        checkIfTileIsConnected(center,Orientation.NORTH,connectedTiles);
        checkIfTileIsConnected(center,Orientation.EAST,connectedTiles);
        checkIfTileIsConnected(center,Orientation.SOUTH,connectedTiles);
        checkIfTileIsConnected(center,Orientation.WEST,connectedTiles);
        //System.out.println(connectedTiles);
        return connectedTiles;
    }

    //ricerca connettori esposti
    public int getNumberOfExposedConnectors(){
        Set<Point> external = new HashSet<>(getExternalPoints());
        int exposedConnectors=0;
        for (Point point:external){
            int n=4;
            try {
                Tile centerTile = getTile(point);
                Tile sideTile;
                try {
                    sideTile = this.getTile(new Point(point.x, point.y + 1));
                    if (connectorsMatch(centerTile.getConnector(0), sideTile.getConnector(2)))n--;
                } catch (InvalidCoordinatesException _) {
                    if(centerTile.getConnector(0).equals(ConnectorType.FLAT))n--;
                }
                try {
                    sideTile = this.getTile(new Point(point.x + 1, point.y));
                    if (connectorsMatch(centerTile.getConnector(1), sideTile.getConnector(3))) n--;
                } catch (InvalidCoordinatesException _) {
                    if(centerTile.getConnector(1).equals(ConnectorType.FLAT))n--;
                }
                try {
                    sideTile = this.getTile(new Point(point.x, point.y - 1));
                    if (connectorsMatch(centerTile.getConnector(2), sideTile.getConnector(0))) n--;
                } catch (InvalidCoordinatesException _) {
                    if(centerTile.getConnector(2).equals(ConnectorType.FLAT))n--;
                }
                try {
                    sideTile = this.getTile(new Point(point.x - 1, point.y));
                    if (connectorsMatch(centerTile.getConnector(3), sideTile.getConnector(1))) n--;
                } catch (InvalidCoordinatesException _) {
                    if(centerTile.getConnector(3).equals(ConnectorType.FLAT))n--;
                }
            }catch (InvalidCoordinatesException _) {n=0;}
            exposedConnectors+=n;
        }
        return exposedConnectors;
    }

    /**
     * adds the given token to the container in the Tile
     * @param coordinates of the container
     * @param token to add
     */
    public void addTokenToTile(Point coordinates, Token  token){
        try{
            Tile t= getTile(coordinates);
            try {
                t.addToken(token);
            } catch (IllegalTokenInComponentEcxeption | ArrayIndexOutOfBoundsException |
                     IllegalMethodOnThisComponentException e) {
                System.out.println("cannot add " + token + " to " + t.getComponentType() + " point<" + coordinates.x + "," + coordinates.y + "> --> " + e.getMessage());
            }
        } catch (InvalidCoordinatesException e){
            throw new InvalidCoordinatesException("cannot add "+token+ "  in point<" + coordinates.x + "," + coordinates.y + "> --> "+ e.getMessage());
        }

    }

    /**
     * fills container in the Tile with the given token
     * @param coordinates of the container
     * @param token to fill the container with
     */
    public void fillTileComponent(Point coordinates, Token token){
        try {
            Tile t=getTile(coordinates);
            try {
                t.fillComponent(token);
            } catch (IllegalMethodOnThisComponentException | IllegalTokenInComponentEcxeption e) {
                System.out.println("cannot fill " + t.getComponentType() + " with token " + token + " --> " + e.getMessage());
            }
        }catch (InvalidCoordinatesException e) {
            throw new InvalidCoordinatesException("cannot FILL point<" + coordinates.x + "," + coordinates.y + "> --> " + e.getMessage());
        }
    }
    /**
     * removes the given token from the container in the Tile
     * @param coordinates of the container
     * @param token to remove
     */
    public void removeTokenFromTile(Point coordinates, Token token){
        try{
            Tile t=getTile(coordinates);
            try{t.removeToken(token);}
            catch (IllegalTokenInComponentEcxeption | ArrayIndexOutOfBoundsException |
                   IllegalMethodOnThisComponentException e) {
                System.out.println("cannot remove "+token+" from " + t.getComponentType()+ " point<"+coordinates.x+","+coordinates.y+"> --> "+e.getMessage());
            }
        } catch (InvalidCoordinatesException e){
            throw new InvalidCoordinatesException("cannot ADD "+token+ "  in point<" + coordinates.x + "," + coordinates.y + "> --> "+e.getMessage());
        }
    }



    /**
     * //method to consume a token of a tile (BATTERY)
     * @param donator point of the battery
     */
    private void consumeComponentToken(Point donator){
        try{
            Tile t=getTile(donator);
            if(t.getComponentType().equals(ComponentType.BATTERY)) {
                try {
                    t.removeToken();
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("not enough BATTERY in " + t.getComponentType() + " point<" + donator.x + "," + donator.y + "> --> " + e.getMessage());
                }
            }else throw new IllegalMethodOnThisComponentException("cannot donate a BATTERY from" + t.getComponentType() + " point<" + donator.x + "," + donator.y+">");

        } catch (InvalidCoordinatesException e){
            throw new InvalidCoordinatesException("cannot use a Battery from point<" + donator.x + "," + donator.y + "> --> "+e.getMessage());
        }
    }
    /**
     * //method to turn on the component of a Tile
     * @param coordinates of the Tile
     */
    private void turnOnComponent(Point coordinates){
        try{
            Tile t=getTile(coordinates);
            try{t.turnOn();}
            catch(IllegalMethodOnThisComponentException e){
                System.out.println("cannot turn on " + t.getComponentType()+ "in point<"+coordinates.x+","+coordinates.y+"> -->  "+e.getMessage());
            }
        } catch (InvalidCoordinatesException e){
            throw new InvalidCoordinatesException("cannot turn ON point<" + coordinates.x + "," + coordinates.y +"> --> "+e.getMessage());
        }
    }

    /**
     * activate the component in the Tile (coordinates) using a battery from Tile(donator)
     * @param coordinates of the container
     * @param donator point of the battery
     */
    public void turnOnComponent(Point coordinates, Point donator){
        turnOnComponent(coordinates);
        consumeComponentToken(donator);
    }
    /**
     * //method to turn off the component of a Tile
     * @param coordinates of the Tile
     */
    public void turnOff(Point coordinates){
        try{
            Tile t=getTile(coordinates);
            try{t.turnOff();}
            catch(IllegalMethodOnThisComponentException e){
                System.out.println("cannot turn off " + t.getComponentType()+ "in point<"+coordinates.x+","+coordinates.y+"> -->  "+e.getMessage());
            }
        } catch (InvalidCoordinatesException e){
            throw new InvalidCoordinatesException("cannot turn OFF point<" + coordinates.x + "," + coordinates.y +"> --> "+e.getMessage());
        }
    }

    /**
     * get the position of the components to activate
     * @param type to activate
     * @return a list of points of activatable components with the given type
     */
    public ArrayList<Point> getActivatableComponents(ComponentType type){
        if(type.equals(ComponentType.DOUBLE_CANNON) || type.equals(ComponentType.DOUBLE_ENGINE) || type.equals(ComponentType.SHIELD)){
            return grid.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue()
                            .map(tile -> tile.getComponentType().equals(type))
                            .orElse(false))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toCollection(ArrayList::new));
        }else throw new IllegalMethodOnThisComponentException("cannot get activatable components of type " + type);
    }
    public ArrayList<Point> getComponentsPositions(ComponentType type){
        return grid.entrySet()
                .stream()
                .filter(entry -> entry.getValue()
                        .map(tile -> tile.getComponentType().equals(type))
                        .orElse(false))
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * get the total firepower of the ShipBoard
     * @return a double that represent the firepower of the shipboard
     */
    public double getTotalFirePower(){
        return grid.values()
                .stream()
                .flatMap(Optional::stream)
                .mapToDouble(Tile::getFirePower)
                .sum();
    }
    /**
     * get the total engine power of the ShipBoard
     * @return a double that represent the engine power of the shipboard
     */
    public int getEnginePower(){
        return grid.values()
                .stream()
                .flatMap(Optional::stream)
                .mapToInt(Tile::getEnginePower)
                .sum();
    }

    public int getTotalNumberOfaToken (Token token){
        return getPositionsOfaToken(token).size();
    }
    public ArrayList<Point> getPositionsOfaToken(Token token){
        return grid.entrySet().stream()
                .filter(entry -> entry.getValue()
                        .map(value -> value.getNumberOfaToken(token) > 0)
                        .orElse(false))
                .flatMap(entry -> Collections.nCopies(entry.getValue().get().getNumberOfaToken(token), entry.getKey()).stream())
                .collect(Collectors.toCollection(ArrayList::new));
    }
    public void removeAllTokens(){
        for (Optional<Tile> t: grid.values()){
            try{t.ifPresent(Tile::clearComponent);}
            catch (IllegalMethodOnThisComponentException _){}
        }
    }

    //metodo che restituisce un set di orientation che indica dove la nave è protetta
    public Set<Orientation> getProtectedSides(){
        Set<Orientation> sides= new HashSet<>();
        ArrayList<Point> shields= this.getActivatableComponents(ComponentType.SHIELD);
        for(Point p: shields){
            Tile t= getTile(p);
            if(sides.size()==4)break;
            if(t.isActive()) {
                switch (t.getTileOrientation()) {
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
        //TODO questo primo pezzo va spostato in complete shipboard
       Set<Point> elimina = grid.entrySet().stream().filter(entry -> entry.getValue().isEmpty() )
               .map(Map.Entry::getKey)
               .collect(Collectors.toSet());
       for(Point p: elimina){
           destroyTile(p);
       }
    }

    public void printShipboard(){
        for (int i = 5; i <= 9; i++) {
            if(i==5) System.out.println("\t4\t5\t6\t7\t8\t9\t10");
            for (int j = 4; j <=10 ; j++) {
                if(j==4) System.out.print(i+"\t");
                var p= new Point(j,i);
                if(grid.containsKey(p) && grid.get(p).isPresent()) System.out.print(grid.get(p).get().getIdTile()+"\t");
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
                    if (grid.containsKey(p) && grid.get(p).isPresent()){
                        var t= grid.get(p).get();
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
                    } else if(grid.containsKey(p) && row==1){
                        System.out.print("\uD83D\uDFE6\t");
                        System.out.print("⬛\t");
                        System.out.print("\uD83D\uDFE6\t");
                    }
                    else {
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
            case SHIELD ->  System.out.print("\uD83D\uDEE1️\t");
            default -> System.out.print("\uD83D\uDFE6\t");
        }
    }
}
