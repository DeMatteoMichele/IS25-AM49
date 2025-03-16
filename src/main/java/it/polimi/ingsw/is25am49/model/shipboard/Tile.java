package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.ConnectorType;
import it.polimi.ingsw.is25am49.model.enums.Orientation;
import it.polimi.ingsw.is25am49.model.enums.Token;

import java.util.HashMap;

public class Tile {
    private final int idTile;
    private final Component component;
    private Orientation tileRotation;
    HashMap<Integer, ConnectorType> connectors;
    private boolean isLocked;
    private boolean isCovered;

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
    public void addToken(Token token) {
        component.addToken(token);
    }
    public void removeToken(Token token) {component.removeToken(token);}
    public void removeToken(){component.removeToken();}
    public void turnOn(){component.turnOn();}
    public void turnOff() {component.turnOff();}

    public Orientation getTileRotation() {
        return tileRotation;
    }
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
}
