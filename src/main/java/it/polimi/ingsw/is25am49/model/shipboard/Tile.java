package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.ConnectorType;
import it.polimi.ingsw.is25am49.model.enums.Orientation;

import java.util.ArrayList;
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

    public Orientation getTileRotation() {
        return tileRotation;
    }
    public void setTileRotation(Orientation tileRotation) {
        int oldOrientation = this.tileRotation.ordinal();
        int newOrientation = tileRotation.ordinal();
        this.tileRotation = tileRotation;

        for (int i = 0; i < 4; i++) {
            int oldIndex=(oldOrientation+i)%4;
            int newIndex=(newOrientation+i)%4;
            ConnectorType tempConnector = connectors.get(newIndex);
            connectors.put(newIndex, connectors.get(oldIndex));
            connectors.put(oldIndex, tempConnector);
        }
    }
    public void rotateTileClockwise(){
        Orientation newTileRotation= Orientation.values()[(tileRotation.ordinal()+1)%4];
        setTileRotation(newTileRotation);
    }
    public void rotateTileCounterClockwise(){
        Orientation newTileRotation= Orientation.values()[(tileRotation.ordinal()-1)%4];
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
        connectors.put(1,c1);
        connectors.put(2,c2);
        connectors.put(3,c3);
        connectors.put(4,c4);
    }
    public void setConnectors(ArrayList<ConnectorType> connectors) {
        for(int i=0;i<connectors.size();i++) {
            this.connectors.put(i,connectors.get(i));
        }
    }
    public void setConnector(int i,ConnectorType c){
        connectors.put(i,c);
    }
    public ConnectorType getConnector(Orientation orientation){
        return connectors.get(orientation.ordinal());
    }

    public double getFirePower(){
        return tileRotation.equals(Orientation.NORTH)?this.getComponent().level:this.getComponent().level*0.5;
    }
}
