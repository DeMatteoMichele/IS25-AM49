package it.polimi.ingsw.is25am49.model.shipboard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.ConnectorType;
import it.polimi.ingsw.is25am49.model.enums.Orientation;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisComponentException;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalTokenInComponentEcxeption;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Tile {
    private final int idTile;
    private final Component component;
    private Orientation tileRotation;
    HashMap<Integer, ConnectorType> connectors;
    private boolean isLocked;
    private boolean isCovered;

    public Tile(){
        this.idTile = 0;
        component= new BasicComponent(ComponentType.SKY,0);
        connectors= new HashMap<>(0);
        tileRotation= Orientation.NORTH;
        isLocked= true;
        isCovered=false;
        connectors.put(0,ConnectorType.FLAT);
        connectors.put(1,ConnectorType.FLAT);
        connectors.put(2,ConnectorType.FLAT);
        connectors.put(3,ConnectorType.FLAT);
    }
    public Tile(int idTile, Component component) {
        this.idTile = idTile;
        this.component = component;
        this.connectors = new HashMap<>(4);
        this.tileRotation= Orientation.NORTH;
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
        this.tileRotation= Orientation.NORTH;
        this.isLocked= false;
        this.isCovered= true;
    }

    public int getIdTile() {
        return idTile;
    }
    public Component getComponent() {
        return component;
    }
    int getNumberOfaToken(Token token) {return component.getNumberOfaToken(token);}
    public void addToken(Token token){component.addToken(token);}
    public void removeToken(Token token) {component.removeToken(token);}
    public void removeToken(){component.removeToken();}
    public void turnOn(){component.turnOn();}
    public void turnOff() {component.turnOff();}
    public void fillComponent(Token token){
        try{component.fillComponent(token);}
        catch (IllegalMethodOnThisComponentException | IllegalTokenInComponentEcxeption e){
            System.out.println("cannot fill component with token " + token + ": " + e.getMessage());
        }
    }
    public void clearComponent(){component.clearComponent();}

    public Orientation getTileRotation() {return tileRotation;}

    //this method rotates the map of connector to match the component orientation, using the Juggling algorithm
    public void setTileRotation(Orientation tileRotation) {
        int oldOrientation = this.tileRotation.ordinal();
        int newOrientation = tileRotation.ordinal();

        this.tileRotation = tileRotation;
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
        Orientation newTileRotation= Orientation.values()[(tileRotation.ordinal()+1)%4];
        setTileRotation(newTileRotation);
    }
    public void rotateTileCounterClockwise(){
        int index = ((tileRotation.ordinal()-1 % 4) + 4) % 4; //modulo per numeri negativi
        Orientation newTileRotation= Orientation.values()[index];
        setTileRotation(newTileRotation);
    }

    public boolean isLocked() {
        return isLocked;
    }
    public void lockTile() {
        isLocked = true;
    }
    public boolean isCovered() {
        return isCovered;
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

    public double getFirePower(){
        if(component.getType().equals(ComponentType.DOUBLE_CANNON)||component.getType().equals(ComponentType.CANNON))
        return tileRotation.equals(Orientation.NORTH)?this.getComponent().level:this.getComponent().level*0.5;
        else return 0;
    }


    public static ArrayList<Tile> generateTiles(){
        try{
            File file = new File("src/main/resources/it/polimi/ingsw/is25am49/tiles_config.json");
            if(!file.exists()){throw new FileNotFoundException("File(tiles_config.json) non trovato: " + file.getAbsolutePath());}
            else{
                ArrayList<Tile> tiles = new ArrayList<>();
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
                    Component component;
                    switch (type){
                        case ENGINE,CANNON,FRAME_SECTION,SKY:
                            component= new BasicComponent(type,level);
                            break;
                        case DOUBLE_CANNON,DOUBLE_ENGINE,SHIELD:
                            component= new SpecialComponent(type,level);
                        default:
                            component= new ContainerComponent(type,level);
                    }
                    tiles.add(new Tile(idTile,component,c1,c2,c3,c4));
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

}
