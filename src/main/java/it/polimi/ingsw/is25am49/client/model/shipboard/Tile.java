package it.polimi.ingsw.is25am49.client.model.shipboard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am49.client.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.client.model.enums.ConnectorType;
import it.polimi.ingsw.is25am49.client.model.enums.Orientation;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisComponentException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Tile {
    private final int idTile;
    private final Component component;
    private Orientation tileOrientation;
    HashMap<Integer, ConnectorType> connectors;
    private boolean isLocked;
    private boolean isCovered;

    public Tile(){
        this.idTile = 0;
        component= new BasicComponent(ComponentType.SKY,0);
        connectors= new HashMap<>(0);
        tileOrientation = Orientation.NORTH;
        isLocked= true;
        isCovered=false;
        connectors.put(0, ConnectorType.FLAT);
        connectors.put(1, ConnectorType.FLAT);
        connectors.put(2, ConnectorType.FLAT);
        connectors.put(3, ConnectorType.FLAT);
    }
    public Tile(int idTile, Component component) {
        this.idTile = idTile;
        this.component = component;
        this.connectors = new HashMap<>(4);
        this.tileOrientation = Orientation.NORTH;
        this.isLocked= false;
        this.isCovered= true;
    }
    public Tile(int idTile, Component component, ConnectorType c1, ConnectorType c2, ConnectorType c3, ConnectorType c4) {
        this.idTile = idTile;
        this.component = component;
        this.connectors = new HashMap<>(4);
        connectors.put(0,c1);
        connectors.put(1,c2);
        connectors.put(2,c3);
        connectors.put(3,c4);
        this.tileOrientation = Orientation.NORTH;
        this.isLocked= false;
        this.isCovered= true;
    }
    public int getIdTile() {
        return idTile;
    }
    public Component getComponent() {
        return component;
    }
    public ComponentType getComponentType() {return component.getType();}

    public void addToken(Token token){component.addToken(token);}
    public void removeToken(Token token) {component.removeToken(token);}
    public void removeToken(){component.removeToken();}
    public void fillComponent(Token token){component.fillComponent(token);}
    public void clearComponent(){component.clearComponent();}
    public Orientation getTileOrientation() {return tileOrientation;}
    int getNumberOfaToken(Token token) {
        try{return component.getNumberOfaToken(token);}
        catch (IllegalMethodOnThisComponentException _){return 0;}
    }

    public void turnOn(){
        try {
            component.turnOn();
        }catch (IllegalMethodOnThisComponentException e){
            System.out.println("cannot turn on "+component.type+" --> "+e.getMessage());
        }
    }
    public void turnOff() {
        try {
            component.turnOff();
        }catch (IllegalMethodOnThisComponentException e){
            System.out.println("cannot turn off "+component.type+" --> "+e.getMessage());
        }
    }
    public boolean isActive(){
        try {
            return component.getIsActive();
        }catch (IllegalMethodOnThisComponentException e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    //this method rotates the map of connector to match the component orientation, using the Juggling algorithm
    public void setTileOrientation(Orientation tileOrientation) {
        int oldOrientation = this.tileOrientation.ordinal();
        int newOrientation = tileOrientation.ordinal();

        this.tileOrientation = tileOrientation;
        int diff= Math.abs(newOrientation-oldOrientation);

        //calc GCD
        int numOfCycles=4;
        int k=diff;
        while (k != 0) {
            int temp = k;
            k = numOfCycles % k;
            numOfCycles = temp;
        }

        //Rotation cycles
        for (int i = 0; i < numOfCycles; i++) {
            int currIndex=i;
            ConnectorType currType=connectors.get(currIndex);
            //System.out.println("ciclo="+i +"\nNorth: " + getConnector(Orientation.NORTH)+"\nEast: "+getConnector(Orientation.EAST)+"\nSouth: "+getConnector(Orientation.SOUTH)+"\nWest: "+getConnector(Orientation.WEST));
            do {
                int newIndex=(currIndex+diff)%4;
                ConnectorType newType=connectors.get(newIndex);
                connectors.put(newIndex, currType);
                currType=newType;

                currIndex=newIndex;
            }while(currIndex!=i);
        }
    }
    public void rotateTileClockwise(){
        Orientation newTileRotation= Orientation.values()[(tileOrientation.ordinal()+1)%4];
        setTileOrientation(newTileRotation);
    }
    public void rotateTileCounterClockwise(){
        int index = ((tileOrientation.ordinal()-1 % 4) + 4) % 4; //modulo per numeri negativi
        Orientation newTileRotation= Orientation.values()[index];
        setTileOrientation(newTileRotation);
    }

    public boolean isLocked() {
        return isLocked;
    }
    public void lockTile() {
        isLocked = true;
    }
    public void uncoverTile() {
        isCovered = false;
    }

    public void setConnectors(ConnectorType c1, ConnectorType c2, ConnectorType c3, ConnectorType c4) {
        connectors.put(0,c1);
        connectors.put(1,c2);
        connectors.put(2,c3);
        connectors.put(3,c4);
    }
    public ConnectorType getConnector(Orientation orientation){
        return connectors.get(orientation.ordinal());
    }
    public ConnectorType getConnector(int index){
        return connectors.get(index);
    }

    //restituisce la potenza di fuoco di un cannone, verifica che sia acceso
    public double getFirePower(){return component.getFirePower()*(tileOrientation.equals(Orientation.NORTH)?1:0.5);}
    public int getEnginePower(){return component.getEnginePower();}

    public static Map<Integer,Tile> generateTiles(){
        try{
            File file = new File("src/main/resources/it/polimi/ingsw/is25am49/tiles_config.json");
            if(!file.exists()){throw new FileNotFoundException("File(tiles_config.json) non trovato: " + file.getAbsolutePath());}
            else{
                Map<Integer,Tile> tiles = new HashMap<>();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootArray = objectMapper.readTree(file);

                for (JsonNode node : rootArray) {
                    int idTile = node.get("idTile").asInt();
                    ComponentType type= ComponentType.valueOf(node.get("component").asText());
                    int level = node.get("level").asInt();
                    ConnectorType c1= ConnectorType.valueOf(node.get("c1").asText());
                    ConnectorType c2= ConnectorType.valueOf(node.get("c2").asText());
                    ConnectorType c3= ConnectorType.valueOf(node.get("c3").asText());
                    ConnectorType c4= ConnectorType.valueOf(node.get("c4").asText());
                    //System.out.println(idTile + " " + type+ " "+ level+ " "+ c1+ " "+ c2+ " "+ c3+ " "+ c4);
                    Component component = switch (type) {
                        case ENGINE, CANNON, FRAME_SECTION, SKY, PURPLE_ALIEN_MODULE ,BROWN_ALIEN_MODULE -> new BasicComponent(type, level);
                        case DOUBLE_CANNON, DOUBLE_ENGINE, SHIELD -> new SpecialComponent(type, level);
                        default -> new ContainerComponent(type, level);
                    };
                    tiles.put(idTile,new Tile(idTile,component,c1,c2,c3,c4));
                }
                return tiles;
            }
        }catch (FileNotFoundException e) {
            System.err.println("Errore: Il file di configurazione non è stato trovato. -->" + e.getMessage());
        }catch (IllegalArgumentException e) {
            System.err.println("Errore nella conversione dell'enum!"+e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public String toString(){
        String tileString = "";
        tileString = tileString + Integer.toString(idTile);
        return tileString;
    }
}
