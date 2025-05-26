package it.polimi.ingsw.is25am49.client.model.shipboard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.ConnectorType;
import it.polimi.ingsw.is25am49.shared.enums.Orientation;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisComponentException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalTokenInComponentEcxeption;
import it.polimi.ingsw.is25am49.client.model.exceptions.InvalidCoordinatesException;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


public class ShipBoard {
    private final HashMap<Point,Optional<Tile>> grid;
    private Point lastPlacedPoint;
    private boolean isCompleted;
    private int destroyedTiles;

    public ShipBoard() {
        grid = new HashMap<>();
        lastPlacedPoint = new Point(7,7);
        isCompleted = false;
        destroyedTiles = 0;
    }
    public ShipBoard(int level, int playerID, Color color){
        grid = new HashMap<>();
        lastPlacedPoint =  new Point(7,7);
        isCompleted = false;
        destroyedTiles = 0;

        HashMap<Integer, Tile> tiles = new HashMap<>(Objects.requireNonNull(Tile.generateTiles()));
        var centerCabinTile = switch (color){
            case RED -> tiles.get(52);
            case BLUE -> tiles.get(33);
            case GREEN -> tiles.get(34);
            case YELLOW -> tiles.get(61);
        };
        centerCabinTile.lockTile();
        centerCabinTile.fillComponent(Token.ASTRONAUT);

        switch (level){
            case 0,1:
                try{
                    File file = new File("src/main/resources/it/polimi/ingsw/is25am49/shipboard_one_config.json");
                    if(!file.exists()){throw new FileNotFoundException("File(shipboard_two_config.json) non trovato: " + file.getAbsolutePath());}
                    else{
                        ObjectMapper objectMapper = new ObjectMapper();
                        Set<Point> points= objectMapper.readValue(file, new TypeReference<>(){});

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
                    Set<Point> points= objectMapper.readValue(file,new TypeReference<>(){});

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

//   -----------------------------------------------------------------------------------------------
//    Funzioni di Check Shipboard ↓
//  ------------------------------------------------------------------------------------------------

    /**
     * check if there is a tile at given Coordinates
     * @param coordinates
     */
    public void checkCoordinates(Point coordinates) {
        getTile(coordinates);
    }
    /**
     * check if i can insert a tile
     * @param coordinates indicating where to place the Tile
     */
    public void checkInsertTile(Point coordinates){
        if(grid.containsKey(coordinates)){
            if (grid.get(coordinates).isPresent()) throw new InvalidCoordinatesException("cannot INSERT a tile in point<" + coordinates.x + "," + coordinates.y + "> --> tile already present");
        } else throw new InvalidCoordinatesException("cannot INSERT a tile in point<" + coordinates.x + "," + coordinates.y + "> --> out of shipboard");
    }
    /**
     * check if i can remove a tile
     * @param coordinates indicating where to remove the Tile from
     */
    public void checkRemoveTile(Point coordinates) {
        try{
            Tile t = getTile(coordinates);
            if (t.isLocked())throw new InvalidCoordinatesException("cannot REMOVE a tile i<" + coordinates.x + "," + coordinates.y + "> --> tile is locked");
        } catch (InvalidCoordinatesException e) {
            throw new InvalidCoordinatesException("cannot REMOVE a tile in point<" + coordinates.x + "," + coordinates.y + "> --> "+e.getMessage());
        }
    }
    /**
     * check if the token can be added to the Tile
     * @param coordinates of the tile
     * @param token to add
     */
    public void checkAddTokenToTile(Point coordinates, Token token){
        try{
            Tile t= getTile(coordinates);
            try{
            t.getComponent().checkAddToken(token);
            } catch (IllegalTokenInComponentEcxeption | ArrayIndexOutOfBoundsException |
                     IllegalMethodOnThisComponentException e) {
                throw new RuntimeException("cannot add " + token + " to " + t.getComponentType() + " point<" + coordinates.x + "," + coordinates.y + "> --> " + e.getMessage());
            }
        } catch (InvalidCoordinatesException e){
            throw new InvalidCoordinatesException("cannot add "+token+ "  in point<" + coordinates.x + "," + coordinates.y + "> --> "+ e.getMessage());
        }
    }
    /**
     * check if it's possible to fill the Tile with the given token
     * @param coordinates of the Tile
     * @param token to fill the Tile with
     */
    public void checkFillTileComponent(Point coordinates, Token token){
        try {
            Tile t=getTile(coordinates);
            if (!t.getComponent().checkIfTokenIsValid(token)) throw new IllegalTokenInComponentEcxeption("cannot fill " + t.getComponentType() + " with token " + token);
        }catch (InvalidCoordinatesException e) {
            throw new InvalidCoordinatesException("cannot FILL point<" + coordinates.x + "," + coordinates.y + "> --> " + e.getMessage());
        }
    }
    /**
     * check if it's possible to remove the given token from the Tile
     * @param coordinates of the Tile
     * @param token to remove
     */
    public void checkRemoveTokenFromTile(Point coordinates, Token token){
        try{
            Tile t=getTile(coordinates);
            try{t.getComponent().checkRemoveToken(token);}
            catch (IllegalTokenInComponentEcxeption | ArrayIndexOutOfBoundsException |
                   IllegalMethodOnThisComponentException e) {
                throw new RuntimeException("cannot remove "+token+" from " + t.getComponentType()+ " point<"+coordinates.x+","+coordinates.y+"> --> "+e.getMessage());
            }
        } catch (InvalidCoordinatesException e){
            throw new InvalidCoordinatesException("cannot ADD "+token+ "  in point<" + coordinates.x + "," + coordinates.y + "> --> "+e.getMessage());
        }
    }
    /**
     * check if it's possible to activate the component in the Tile (coordinates)
     * using a battery from Tile(donator)
     * @param coordinates of the component to activate
     * @param donator point of the battery
     */
    public void checkTurnOnComponent(Point coordinates, Point donator){
        try{
            Tile t=getTile(coordinates);
            Tile d=getTile(donator);

            if(d.getComponentType().equals(ComponentType.BATTERY)) {
                try {
                    d.getComponent().checkRemoveToken(Token.BATTERY_CELLS);
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new RuntimeException("not enough BATTERY in " + t.getComponentType() + " point<" + donator.x + "," + donator.y + "> --> " + e.getMessage());
                }
            }else throw new IllegalMethodOnThisComponentException("cannot donate a BATTERY from" + t.getComponentType() + " point<" + donator.x + "," + donator.y+">");

            try{t.isActive();}
            catch(IllegalMethodOnThisComponentException e){
                throw new RuntimeException("cannot turn on " + t.getComponentType()+ "in point<"+coordinates.x+","+coordinates.y+"> -->  "+e.getMessage());
            }
        }catch (InvalidCoordinatesException e){
            throw new InvalidCoordinatesException("cannot turn ON point<" + coordinates.x + "," + coordinates.y +"> --> "+e.getMessage());
        }
    }
    /**
     * check if it's possible to turn off the component of a Tile
     * @param coordinates of the Tile
     */
    public void checkTurnOff(Point coordinates){
        try{
            Tile t=getTile(coordinates);
            try{t.isActive();}
            catch(IllegalMethodOnThisComponentException e){
                throw new RuntimeException("cannot turn off " + t.getComponentType()+ "in point<"+coordinates.x+","+coordinates.y+"> -->  "+e.getMessage());
            }
        } catch (InvalidCoordinatesException e){
            throw new InvalidCoordinatesException("cannot turn OFF point<" + coordinates.x + "," + coordinates.y +"> --> "+e.getMessage());
        }
    }
//  -----------------------------------------------------------------------------------------------
//    Funzioni di Update Shipboard ↓
//  ------------------------------------------------------------------------------------------------

    /**
     * Locks the last tile placed in the shipboard
     */
    public void lockLastTile(){
        grid.get(lastPlacedPoint).ifPresent(Tile::lockTile);
    }
    public Point getLastPlacedPoint() {return lastPlacedPoint;}
    public boolean isCompleted() {return isCompleted;}

    /**
     * this method prepare the shipboard for the flight setting isCompleted to True and destroying all the miss-placed tiles
     */
    public void completeShipBoard() {
        if(!isCompleted){
            isCompleted = true;
            afterBuildCheckShipboard();
            for(Point p: grid.keySet()){
                getTile(p).lockTile();
                try{
                    fillTileComponent(p, Token.BATTERY_CELLS);
                } catch(Exception _) {}
                try{
                    if (checkIfAlienArePossible(p).size() == 1) {
                        fillTileComponent(p, Token.ASTRONAUT);
                    }
                } catch(Exception _) {}
            }
        }
    }

    public void setDestroyedTiles(int n) {this.destroyedTiles = n;}
    public int getDestroyedTiles() {return destroyedTiles;}
    public void addDestroyedTiles(int n) {this.destroyedTiles += n;}

    /**
     * @return true only if the connectors given are matching
     * @param c1 first connector
     * @param c2 second connector
     */
    //Testato ✔
    public boolean connectorsMatch(ConnectorType c1, ConnectorType c2){
        if (c1.equals(ConnectorType.FLAT) || c2.equals(ConnectorType.FLAT))return false;
        if( c1.equals(ConnectorType.UNIVERSAL) || c2.equals(ConnectorType.UNIVERSAL))return true;
        return c1.equals(c2);
    }

    /**
     * returns the Tile at given coordinates if present
     * @param coordinates of the desired Tile
     * @return Tile
     */
    //Testato ✔
    public Tile getTile(Point coordinates){
        if(grid.containsKey(coordinates)) {
            return grid.get(coordinates).orElseThrow(()-> new InvalidCoordinatesException("tile is empty"));
        }else throw new InvalidCoordinatesException("out of shipboard");
    }


    /**
     * this method returns external point of the ShipBoard
     * @return an ArrayList of points that are the border of the Shipboard
     */
    //Testato ✔
    public ArrayList<Point> getExternalPoints(){
        ArrayList<Point> externalPoints = new ArrayList<>();
        for(Point p:grid.keySet()){
            if (grid.get(p).isPresent()){
                try{
                    getTile(new Point(p.x,p.y-1));
                    getTile(new Point(p.x+1,p.y));
                    getTile(new Point(p.x,p.y+1));
                    getTile(new Point(p.x-1,p.y));
                }catch (InvalidCoordinatesException _){
                    externalPoints.add(p);
                }
            }
        }
        return externalPoints;
    }

    /**
     * method that returns the point where the shipboard could be hit in a given position
     *
     */
    public Optional<Point> getExposedPoint(int position, Orientation orientation){
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

    /**
     * inserts a tile in the ShipBoard if the coordinates are free
     * @param coordinates indicating where to place the Tile
     * @param tile that has to be placed
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
                grid.put(coordinates,Optional.empty());
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
     * if an alien module is destroyed the corresponding alien will die
     * @param coordinates of the Tile to Destroy
     */
    public void destroyTile(Point coordinates){
        if(grid.containsKey(coordinates)){
            if(grid.get(coordinates).isPresent()){
                Tile t=getTile(coordinates);
                if(t.getComponentType().equals(ComponentType.BROWN_ALIEN_MODULE)||t.getComponentType().equals(ComponentType.PURPLE_ALIEN_MODULE)){
                    Token token= t.getComponentType().equals(ComponentType.BROWN_ALIEN_MODULE)? Token.BROWN_ALIENS: Token.PURPLE_ALIENS;
                    try {
                        this.removeTokenFromTile(new Point(coordinates.x, coordinates.y-1), token);
                    } catch (Exception _) {}
                    try {
                        this.removeTokenFromTile(new Point(coordinates.x, coordinates.y+1), token);
                    } catch (Exception _) {}
                    try {
                        this.removeTokenFromTile(new Point(coordinates.x-1, coordinates.y), token);
                    } catch (Exception _) {}
                    try {
                        this.removeTokenFromTile(new Point(coordinates.x+1, coordinates.y), token);
                    } catch (Exception _) {}
                }
                destroyedTiles++;
            }
            grid.remove(coordinates);
        } else {
            throw new InvalidCoordinatesException("cannot DESTROY a tile in point<" + coordinates.x + "," + coordinates.y + "> --> out of shipboard");
        }
    }

    /**
     * recursive algorithm that checks all the direction of a Tile
     */
    private void checkIfTileIsConnected(Point point, Orientation direction, Set<Point> connectedTiles){
        try {
            Tile centerTile= getTile(point);
            Tile sideTile;
            switch (direction) {
                case NORTH:
                    try {
                        Point p= new Point(point.x,point.y-1);
                        if(connectedTiles.contains(p)) return;
                        sideTile=getTile(p);
                        if(connectorsMatch(centerTile.getConnector(Orientation.NORTH),sideTile.getConnector(Orientation.SOUTH))){
                                connectedTiles.add(p);
                                checkIfTileIsConnected(p, Orientation.NORTH,connectedTiles);
                                checkIfTileIsConnected(p, Orientation.EAST,connectedTiles);
                                checkIfTileIsConnected(p, Orientation.WEST,connectedTiles);
                        }return;
                    }catch (InvalidCoordinatesException _){return;}
                case EAST:
                    try {
                        Point p= new Point(point.x+1,point.y);
                        if(connectedTiles.contains(p)) return;
                        sideTile=getTile(p);
                        if(connectorsMatch(centerTile.getConnector(Orientation.EAST),sideTile.getConnector(Orientation.WEST))){
                            connectedTiles.add(p);
                            checkIfTileIsConnected(p, Orientation.EAST,connectedTiles);
                            checkIfTileIsConnected(p, Orientation.SOUTH,connectedTiles);
                            checkIfTileIsConnected(p, Orientation.NORTH,connectedTiles);
                        }return;
                    }catch (InvalidCoordinatesException _){return;}
                case SOUTH:
                    try {
                        Point p= new Point(point.x,point.y+1);
                        if(connectedTiles.contains(p)) return;
                        sideTile=getTile(p);
                        if(connectorsMatch(centerTile.getConnector(Orientation.SOUTH),sideTile.getConnector(Orientation.NORTH))){
                            connectedTiles.add(p);
                            checkIfTileIsConnected(p, Orientation.SOUTH,connectedTiles);
                            checkIfTileIsConnected(p, Orientation.WEST,connectedTiles);
                            checkIfTileIsConnected(p, Orientation.EAST,connectedTiles);
                        }return;
                    }catch (InvalidCoordinatesException _){return;}
                case WEST:
                    try {
                        Point p= new Point(point.x-1,point.y);
                        if(connectedTiles.contains(p)) return;
                        sideTile=getTile(p);
                        if(connectorsMatch(centerTile.getConnector(Orientation.WEST),sideTile.getConnector(Orientation.EAST))){
                            connectedTiles.add(p);
                            checkIfTileIsConnected(p, Orientation.WEST,connectedTiles);
                            checkIfTileIsConnected(p, Orientation.NORTH,connectedTiles);
                            checkIfTileIsConnected(p, Orientation.SOUTH,connectedTiles);
                        }return;
                    }catch (InvalidCoordinatesException _){return;}
            }
        }catch (InvalidCoordinatesException _){return;}
    }

    /**
     * recursive algorithm that returns all the tiles connected to the given point
     * @param center starting point
     * @return a set containing all the points connected to the center
     */
    public Set<Point> getTilesConnectedTo(Point center){
        Set<Point> connectedTiles =new HashSet<>();
        if(!grid.containsKey(center)) throw new InvalidCoordinatesException("cannot checkTileConnectorsCascade --> out of shipboard"); //DA RIVEDERE L'ECCEZIONE
        connectedTiles.add(center);
        checkIfTileIsConnected(center, Orientation.NORTH,connectedTiles);
        checkIfTileIsConnected(center, Orientation.EAST,connectedTiles);
        checkIfTileIsConnected(center, Orientation.SOUTH,connectedTiles);
        checkIfTileIsConnected(center, Orientation.WEST,connectedTiles);
        //System.out.println(connectedTiles);
        return connectedTiles;
    }

    /**
     * this method destroys all the empty tiles or the ones that are not connected to the center - point<7,7>
     */
    public Set<Point> destroyDisconnectedTiles(){
        Set<Point> elimina = new HashSet<>(grid.keySet());
        elimina.removeAll(getTilesConnectedTo(new Point(7,7)));
        for(Point p: elimina){
            destroyTile(p);
        }
        return elimina;
    }

    /**
     * check all 4 sides of a cabin to verify if an alien can be present in the crew
     * @param coordinates of the cabin to check
     * @return a HashMap<\Point,Token> of the possible crew members of the cabin at given coordinates
     */
    public Set<Token> checkIfAlienArePossible(Point coordinates) {
        Set<Token> crew = new HashSet<>();
        try {
            Tile centerTile=getTile(coordinates);
            if(centerTile.getComponentType().equals(ComponentType.CABINE)) {
                crew.add(Token.ASTRONAUT);
                Tile sideTile;
                Point p;
                try {
                    p=new Point(coordinates.x, coordinates.y - 1);
                    sideTile = getTile(p);
                    switch (sideTile.getComponentType()){
                        case BROWN_ALIEN_MODULE -> crew.add(Token.BROWN_ALIENS);
                        case PURPLE_ALIEN_MODULE -> crew.add(Token.PURPLE_ALIENS);
                    }
                } catch (InvalidCoordinatesException _) {}
                try {
                    p=new Point(coordinates.x + 1, coordinates.y);
                    sideTile = getTile(p);
                    switch (sideTile.getComponentType()){
                        case BROWN_ALIEN_MODULE -> crew.add(Token.BROWN_ALIENS);
                        case PURPLE_ALIEN_MODULE -> crew.add(Token.PURPLE_ALIENS);
                    }
                } catch (InvalidCoordinatesException _) {}
                try {
                    p=new Point(coordinates.x, coordinates.y + 1);
                    sideTile = getTile(p);
                    switch (sideTile.getComponentType()){
                        case BROWN_ALIEN_MODULE -> crew.add(Token.BROWN_ALIENS);
                        case PURPLE_ALIEN_MODULE -> crew.add(Token.PURPLE_ALIENS);
                    }
                } catch (InvalidCoordinatesException _) {}
                try {
                    p=new Point(coordinates.x - 1, coordinates.y);
                    sideTile = getTile(p);
                    switch (sideTile.getComponentType()){
                        case BROWN_ALIEN_MODULE -> crew.add(Token.BROWN_ALIENS);
                        case PURPLE_ALIEN_MODULE -> crew.add(Token.PURPLE_ALIENS);
                    }
                } catch (InvalidCoordinatesException _) {}
            } else throw new InvalidCoordinatesException("cannot checkIfAlienArePossible --> point<"+coordinates.x+","+coordinates.y+"> is not a cabin");
        }catch (InvalidCoordinatesException e){
            System.out.println("cannot checkIfAlienArePossible ---> "+e.getMessage());
        }
        return crew;
    }

    public Optional<Point> getAlienPoint(){
        ArrayList<Point> aliens = new ArrayList<>(getComponentsPositions(ComponentType.CABINE));
        aliens.remove(new Point(7,7));
        return aliens.stream()
                .filter(p -> checkIfAlienArePossible(p).size() > 1 && getTile(p).getComponent().getNumberOfTokens() == 0)
                .findFirst();
    }

    /**
     * @return an integer indicating the number of exposed connectors in the shipboard
     */
    public int getNumberOfExposedConnectors(){
        Set<Point> external = new HashSet<>(getExternalPoints());
        int exposedConnectors=0;
        for (Point point:external){
            int n=4;
            try {
                Tile centerTile = getTile(point);
                Tile sideTile;
                try {
                    sideTile = this.getTile(new Point(point.x, point.y - 1));
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
                    sideTile = this.getTile(new Point(point.x, point.y + 1));
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
     * @param token to add to the container
     */
    public void addTokenToTile(Point coordinates, Token token){
        try{
            Tile t= getTile(coordinates);
            try {
                t.addToken(token);
            } catch (IllegalTokenInComponentEcxeption | ArrayIndexOutOfBoundsException |
                     IllegalMethodOnThisComponentException e) {
                //System.out.println("cannot add " + token + " to " + t.getComponentType() + " point<" + coordinates.x + "," + coordinates.y + "> --> " + e.getMessage());
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
                throw new RuntimeException("cannot fill " + t.getComponentType() + " with token " + token + " --> " + e.getMessage());
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
     * method to consume a battery of a tile
     * @param donator position of the battery in the shipboard
     */
    private void consumeComponentToken(Point donator){
        try{
            Tile t=getTile(donator);
            if(t.getComponentType().equals(ComponentType.BATTERY)) {
                try {
                    t.removeToken();
                } catch (ArrayIndexOutOfBoundsException e) {
                    //System.out.println("not enough BATTERY in " + t.getComponentType() + " point<" + donator.x + "," + donator.y + "> --> " + e.getMessage());
                    throw e;
                }
            }else throw new IllegalMethodOnThisComponentException("cannot donate a BATTERY from" + t.getComponentType() + " point<" + donator.x + "," + donator.y+">");

        } catch (InvalidCoordinatesException e){
            throw new InvalidCoordinatesException("cannot use a Battery from point<" + donator.x + "," + donator.y + "> --> "+e.getMessage());
        }
    }
    /**
     * method to turn on the specialComponent of a Tile
     * @param coordinates of the Tile
     */
    private void turnOnComponent(Point coordinates){
        try{
            Tile t=getTile(coordinates);
            try{t.turnOn();}
            catch(IllegalMethodOnThisComponentException e){
                //System.out.println("cannot turn on " + t.getComponentType()+ "in point<"+coordinates.x+","+coordinates.y+"> -->  "+e.getMessage());
                throw e;
            }
        } catch (InvalidCoordinatesException e){
            throw new InvalidCoordinatesException("cannot turn ON point<" + coordinates.x + "," + coordinates.y +"> --> "+e.getMessage());
        }
    }

    /**
     * activate the component in the Tile (coordinates) using a battery from Tile(donator)
     * @param coordinates of the component to activate
     * @param donator point of the battery
     */
    public void turnOnComponent(Point coordinates, Point donator){
        try {
            consumeComponentToken(donator);
        }catch (ArrayIndexOutOfBoundsException _){ return;}
        try {
            turnOnComponent(coordinates);
        }
        catch (InvalidCoordinatesException | IllegalMethodOnThisComponentException _){
            addTokenToTile(donator, Token.BATTERY_CELLS);
        }

    }
    /**
     * method to turn off the component of a Tile
     * @param coordinates of the Tile
     */
    public void
    turnOff(Point coordinates){
        try{
            Tile t=getTile(coordinates);
            try{t.turnOff();}
            catch(IllegalMethodOnThisComponentException e){
                //System.out.println("cannot turn off " + t.getComponentType()+ "in point<"+coordinates.x+","+coordinates.y+"> -->  "+e.getMessage());
            }
        } catch (InvalidCoordinatesException e){
            throw new InvalidCoordinatesException("cannot turn OFF point<" + coordinates.x + "," + coordinates.y +"> --> "+e.getMessage());
        }
    }

    /**
     * get the position of the specialComponents of the given type
     * @param type of the specialComponents to activate
     * @return a list of points of activatable components with the given type
     */
    public ArrayList<Point> getActivatableComponents(ComponentType type){
        if(type.equals(ComponentType.DOUBLE_CANNON) || type.equals(ComponentType.DOUBLE_ENGINE) || type.equals(ComponentType.SHIELD)){
            return getComponentsPositions(type);
        }else throw new IllegalMethodOnThisComponentException("cannot get activatable components of type " + type);
    }
    /**
     * get the position of the components of the given type
     * @param type of the components
     * @return a list of points of components with the given type
     */
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
        double d= grid.values()
                .stream()
                .flatMap(Optional::stream)
                .mapToDouble(Tile::getFirePower)
                .sum();
        if(d>0){
            d+=2*getTotalNumberOfaToken(Token.PURPLE_ALIENS);
        }
        return d;
    }
    /**
     * get the total engine power of the ShipBoard
     * @return a double that represent the engine power of the shipboard
     */
    public int getTotalEnginePower(){
        int n = grid.values()
                .stream()
                .flatMap(Optional::stream)
                .mapToInt(Tile::getEnginePower)
                .sum();
        if(n>0){
            n+=2*getTotalNumberOfaToken(Token.BROWN_ALIENS);
        }
        return n;
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
    /**
     * check if the cabins are full of tokens
     * @return true if all the cabins are full of tokens
     */
    public boolean cabineAreFullOfToken(){
        ArrayList<Point> listOfCabins = getComponentsPositions(ComponentType.CABINE);
        return listOfCabins.stream().noneMatch(point -> getTile(point).getComponent().getNumberOfTokens()==0);
    }

    /**
     * destroys all the tiles that are wrong positioned following the rules
     */
    private void afterBuildCheckShipboard(){
        //distruggo le tessere prenotate
        destroyTile(new Point(9,5));
        destroyTile(new Point(10,5));

        //System.out.println("RIMUOVO TUTTE LE TESSERE VUOTE O DISCONNESSE");
        destroyDisconnectedTiles();
        //printShipboardSpecial();

        //System.out.println("RIMUOVO CANNONI");
        Set<Point> checkCannon = new HashSet<>(getComponentsPositions(ComponentType.CANNON));
        checkCannon.addAll(getComponentsPositions(ComponentType.DOUBLE_CANNON));
        for(Point pCannon: checkCannon){
            Tile t= getTile(pCannon);
            switch (t.getTileOrientation()){
                case NORTH -> {
                    try{
                        getTile(new Point(pCannon.x,pCannon.y-1));
                        destroyTile(pCannon);
                    } catch (InvalidCoordinatesException _){
                        //System.out.println("ERRORE RIMOZIONE CANNONE");
                    }
                }
                case EAST -> {
                    try{
                        getTile(new Point(pCannon.x+1,pCannon.y));
                        destroyTile(pCannon);
                    } catch (InvalidCoordinatesException _){
                        //System.out.println("ERRORE RIMOZIONE CANNONE");
                    }
                }
                case SOUTH -> {
                    try{
                        getTile(new Point(pCannon.x,pCannon.y+1));
                        destroyTile(pCannon);
                    } catch (InvalidCoordinatesException _){
                        //System.out.println("ERRORE RIMOZIONE CANNONE");
                    }
                }
                case WEST -> {
                    try{
                        getTile(new Point(pCannon.x-1,pCannon.y));
                        destroyTile(pCannon);
                    } catch (InvalidCoordinatesException _){
                        //System.out.println("ERRORE RIMOZIONE CANNONE");
                    }
                }
            }
        } //if(!checkCannon.isEmpty())printShipboardSpecial();

        //System.out.println("RIMUOVO MOTORI");
        Set<Point> checkEngine = new HashSet<>(getComponentsPositions(ComponentType.ENGINE));
        checkEngine.addAll(getComponentsPositions(ComponentType.DOUBLE_ENGINE));
        for(Point pEngine: checkEngine){
            Point p = new Point(pEngine.x,pEngine.y);
            Tile t= getTile(p);
            if(!t.getTileOrientation().equals(Orientation.NORTH)){
                try{destroyTile(p);}
                catch (InvalidCoordinatesException _){
                    System.out.println("RIMOZIONE MOTORE");
                }
            }
            else{
                try{
                    getTile(new Point(pEngine.x,pEngine.y+1));
                    destroyTile(pEngine);
                } catch (InvalidCoordinatesException _){
                    //System.out.println("ERRORE RIMOZIONE MOTORE");
                }
            }
        }// if(!checkEngine.isEmpty())printShipboardSpecial();

        //elimino tessere disconnesse dopo la rimozione sopra
        destroyDisconnectedTiles();
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
            case SPECIAL_STORAGE -> System.out.print("\uD83E\uDDF0\t");
            case SHIELD ->  System.out.print("\uD83D\uDEE1️\t");
            case CANNON -> System.out.print("\uD83D\uDD95\t");
            case DOUBLE_CANNON -> System.out.print("\uD83E\uDD18\t");
            case ENGINE -> System.out.print("E\t");
            case DOUBLE_ENGINE -> System.out.print("DE\t");
            case BROWN_ALIEN_MODULE -> System.out.print("\uD83E\uDD0E\t");
            case PURPLE_ALIEN_MODULE -> System.out.print("\uD83D\uDC9C\t");
            case FRAME_SECTION -> System.out.print("\uD83E\uDDF1\t");
            default -> System.out.print("\uD83D\uDFE6\t");
        }
    }

    public static ShipBoard generateShipboard(int level, int nTiles){
        int n=level==1?19:28;
        if(nTiles>n)nTiles=n;

        ShipBoard ship=new ShipBoard(level,999, Color.RED);
        ArrayList<Tile> tiles=new ArrayList<>(Objects.requireNonNull(Tile.generateTiles()).values());
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        Orientation o;

        while (nTiles>0 && !tiles.isEmpty()) {
            int i=rand.nextInt(tiles.size());
            Tile t = tiles.get(i);
            o= Orientation.values()[rand.nextInt(Orientation.values().length)];
            Point p= new Point(rand.nextInt(7)+4,rand.nextInt(5)+5);
            try {
                ship.insertTile(p,t);
                ship.getTile(p).setTileOrientation(o);
                tiles.remove(i);
                nTiles--;
            }catch (Exception _){}
        }
        return ship;
    }

//------------------------------------------------------------------------------------
//
//    funzioni per la stampa della nave ↓
//
// ------------------------------------------------------------------------------------

    public ArrayList<String> tileToStringList(Optional<Tile> tile){
        ArrayList<String> rows = new ArrayList<>(9);
        if(tile.isEmpty()){
            rows.add("┌ ─ ─ ─ ─ ─ ┐");
            rows.add("│ •   •   • │");
            rows.add("│           │");
            rows.add("│ • empty • │");
            rows.add("│           │");
            rows.add("│ •   •   • │");
            rows.add("└ ─ ─ ─ ─ ─ ┘");
        }
        else {
            Tile t=tile.get();
            ComponentType c=t.getComponentType();
            Orientation o=t.getTileOrientation();
            ConnectorType n=t.getConnector(Orientation.NORTH);
            ConnectorType e=t.getConnector(Orientation.EAST);
            ConnectorType s=t.getConnector(Orientation.SOUTH);
            ConnectorType w=t.getConnector(Orientation.WEST);
            rows.add(String.format("┌ ─ %s ─ ┐", hConnToStr(n)));
            rows.add(String.format("│   %5s  %s│",compToStr(c),lockedToStr(t)));
            rows.add(String.format("%s     %s     %s",vConnToStr(w,1),orientToStr(o,0),vConnToStr(e,1)));
            rows.add(String.format("%s   %s %s %s   %s",vConnToStr(w,2),orientToStr(o,3),centerToStr(t),orientToStr(o,1),vConnToStr(e,2)));
            rows.add(String.format("%s     %s     %s",vConnToStr(w,3),orientToStr(o,2),vConnToStr(e,3)));
            rows.add(String.format("│   %5s   │",compDescToStr(t)));
            rows.add(String.format("└ ─ %s ─ ┘", hConnToStr(s)));
        }
        return rows;
    }
    private String hConnToStr(ConnectorType c){
        return switch(c){
            case FLAT -> "─ ─ ─";
            case SINGLE -> "─ ║ ─";
            case DOUBLE -> "║ ─ ║";
            case UNIVERSAL -> "║ ║ ║";
        };
    }
    private String vConnToStr(ConnectorType c, int row){
        return switch(c.ordinal()+":"+row){
            case "0:1"-> "│";
            case "0:2"-> "│";
            case "0:3" ->"│";

            case "1:1" ->"│";
            case "1:2" ->"═";
            case "1:3" ->"│";

            case "2:1" ->"═";
            case "2:2" ->"│";
            case "2:3" ->"═";

            case "3:1" ->"═";
            case "3:2" ->"═";
            case "3:3" ->"═";

            default -> "│";
        };
    }
    private String compToStr(ComponentType c){
        return switch (c){
            case CABINE -> " cab " ;
            case BATTERY ->  " bat ";
            case STORAGE -> " sto ";
            case SPECIAL_STORAGE -> "sp.st";
            case SHIELD ->  " shi ";
            case CANNON -> " can ";
            case DOUBLE_CANNON -> "db.ca";
            case ENGINE -> " eng ";
            case DOUBLE_ENGINE -> "db.en";
            case BROWN_ALIEN_MODULE -> "am.br";
            case PURPLE_ALIEN_MODULE -> "am.pu";
            case FRAME_SECTION -> "frame";
            default -> " ";
        };
    }
    private String compToEmoji(ComponentType c){
        return switch (c){
            case CABINE -> "֍";
            case BATTERY ->  "↯";
            case STORAGE -> "□";
            case SPECIAL_STORAGE -> "■";
            case SHIELD ->  "◉";
            case CANNON -> "Ϯ";
            case DOUBLE_CANNON -> "Џ";
            case ENGINE -> "Ѫ";
            case DOUBLE_ENGINE -> "Ѭ";
            case BROWN_ALIEN_MODULE -> "፠";
            case PURPLE_ALIEN_MODULE -> "፨";
            case FRAME_SECTION -> "ᚸ";
            default -> " ";
        };
    }
    private String centerToStr(Tile t){
        switch (t.getIdTile()){
            case 52 -> {
                return "\u001B[31m■\u001B[0m";
            }
            case 33 -> {
                return "\u001B[34m■\u001B[0m";
            }
            case 34 -> {
                return "\u001B[32m■\u001B[0m";
            }
            case 61 -> {
                return "\u001B[33m■\u001B[0m";
            }
            default -> {
                return "■";
            }
        }
    }

    private String orientToStr(Orientation o, int dir){
        if(o.ordinal()==dir){
            return switch(o){
                case NORTH -> "↑";
                case EAST -> "→";
                case SOUTH -> "↓";
                case WEST -> "←";
            };
        }else return " ";
    }
    private String compDescToStr(Tile t){
        switch (t.getComponent()){
            case BasicComponent ignored -> {
               return " ";
            }
            case SpecialComponent ignored -> {
                return t.getComponent().getIsActive()? "on  ■" : "off □";
            }
            case ContainerComponent ignored -> {
                return " "+t.getComponent().getNumberOfTokens()+"/"+t.getComponent().getLevel()+" ";
            }
        }
    }
    String lockedToStr(Tile t){
        return t.isLocked() ? "L" : "U";
    }

    @Override
    public String toString(){
        int dim=7; //numero di righe per tessera
        StringBuilder str = new StringBuilder();
        ArrayList<ArrayList<String>> ship = new ArrayList<>();
        for (int y=5;y<=9;y++){
            for (int x=4;x<=10;x++){
                if(grid.containsKey(new Point(x,y)))ship.add(tileToStringList(grid.get(new Point(x, y))));
                else {
                    ship.add( new ArrayList<>(List.of(  "× × × × × × ×",
                                                        "× × × × × × ×",
                                                        "× × × × × × ×",
                                                        "× × × × × × ×",
                                                        "× × × × × × ×",
                                                        "× × × × × × ×",
                                                        "× × × × × × ×"
                            ))
                    );
                }
            }
        }

        str.append("\t      4            5            6            7            8            9            10       \t \n\n");
        for (int k=0;k<5;k++){
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < 7; j++) {
                    if(i==3 && j==0) str.append(k + 5).append(" \t");
                    else if(j==0)str.append(" \t");
                    str.append(ship.get(j + k * 7).get(i));
                    if(i==3 && j==6) str.append("\t ").append(k + 5);
                    else if(j==6)str.append(" \t");
                }
                str.append("\n");
            }
        }
        str.append("\n\t      4            5            6            7            8            9            10       \t \n\n");
        return str.toString();
    }

}


