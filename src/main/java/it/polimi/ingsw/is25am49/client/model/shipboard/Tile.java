package it.polimi.ingsw.is25am49.client.model.shipboard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.ConnectorType;
import it.polimi.ingsw.is25am49.shared.enums.Orientation;
import it.polimi.ingsw.is25am49.shared.enums.Token;
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
    public boolean getIsLocked() {
        return isLocked;
    }

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
        int diff = (newOrientation - oldOrientation + 4) % 4;

        if (diff == 0) {
            this.tileOrientation = tileOrientation;
            return;
        }

        HashMap<Integer, ConnectorType> newConnectors = new HashMap<>(4);
        for (int i = 0; i < 4; i++) {
            newConnectors.put((i + diff) % 4, connectors.get(i));
        }
        connectors = newConnectors;
        this.tileOrientation = tileOrientation;
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
    public boolean getIsCovered() {
        return isCovered;
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
    public double getPotentialFirePower(){return component.getLevel()*(tileOrientation.equals(Orientation.NORTH)?1:0.5);}
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
        StringBuilder str = new StringBuilder();
        final int width = 32;
        String border = "+" + "-".repeat(width - 2) + "+\n";
        String separator = "|" + "-".repeat(width - 2) + "|\n";
        str.append(border);
        String idTileText = "| ID TILE: " + idTile;
        str.append(idTileText).append(" ".repeat(width - idTileText.length() - 1)).append("|\n");
        str.append(separator);

        String levelText = "| LEVEL: " + component.getLevel();
        str.append(levelText).append(" ".repeat(width - levelText.length() - 1)).append("|\n");
        String OrientationText = "| ORIENTATION: " + tileOrientation;
        str.append(OrientationText).append(" ".repeat(width - OrientationText.length() - 1)).append("|\n");
        str.append(separator);
        String space = "| ";

        String northConnector = switch(connectors.get(0)){
            case FLAT -> "";
            case SINGLE -> "↑";
            case DOUBLE -> "⇈";
            case UNIVERSAL -> "U";
        };
        // calcolo degli spazi per centrare il componente
        int availableSpace = width - 2;
        int symbolLength = northConnector.length();
        int leftPadding = (availableSpace - symbolLength) / 2;
        int rightPadding = availableSpace - symbolLength - leftPadding;

        str.append("|").append(" ".repeat(leftPadding))
                .append(northConnector)
                .append(" ".repeat(rightPadding)).append("|\n");
        str.append(space).append(" ".repeat(width-space.length()-1)).append("|\n");
        String eastConnector = switch(connectors.get(1)){
            case FLAT -> "";
            case SINGLE -> "→";
            case DOUBLE -> "⇉";
            case UNIVERSAL -> "⊂";
        };
        String westConnector = switch(connectors.get(3)){
            case FLAT -> "";
            case SINGLE -> "←";
            case DOUBLE -> "⇇";
            case UNIVERSAL -> "⊃";
        };
        String componentSymbol = switch (component.getType()){
           case CABINE -> "\uD83D\uDEDE";
           case BATTERY -> "\uD83D\uDD0B";
           case SKY -> "⬛";
           case STORAGE -> "\uD83D\uDCE6";
           case SPECIAL_STORAGE -> "\uD83E\uDDF0";
           case SHIELD -> "\uD83D\uDEE1️";
           case CANNON -> "\uD83D\uDD95";
           case DOUBLE_CANNON -> "\uD83E\uDD18";
           case ENGINE -> "E";
           case DOUBLE_ENGINE -> "DE";
           case BROWN_ALIEN_MODULE -> "\uD83E\uDD0E";
           case PURPLE_ALIEN_MODULE -> "\uD83D\uDC9C";
           case FRAME_SECTION -> "\uD83E\uDDF1";// calcolo degli spazi considerando connettori est e ovest
           default -> "\uD83D\uDFE6";
       };

        availableSpace = width - 2 - westConnector.length() - eastConnector.length();
        symbolLength = componentSymbol.length();
        leftPadding = (availableSpace - symbolLength) / 2;
        rightPadding = availableSpace - symbolLength - leftPadding;

        str.append("|")
                .append("  ")
                .append(westConnector)
                .append(" ".repeat(Math.max(0, leftPadding-2)))
                .append(componentSymbol)
                .append(" ".repeat(Math.max(0, rightPadding-2)))
                .append(eastConnector)
                .append("  ")
                .append("|\n");
        String southConnector = switch(connectors.get(2)) {
            case FLAT -> "";
            case SINGLE -> "↓";
            case DOUBLE -> "⇊";
            case UNIVERSAL -> "∩";
        };

        String componentText = "" + component.getType();
        availableSpace = width - 2;
        symbolLength = componentText.length();
        leftPadding = (availableSpace - symbolLength) / 2;
        rightPadding = availableSpace - symbolLength - leftPadding;
        str.append("|").append(" ".repeat(leftPadding))
                .append(componentText)
                .append(" ".repeat(rightPadding)).append("|\n");
        str.append(space).append(" ".repeat(width-space.length()-1)).append("|\n");
        str.append(space).append(" ".repeat(width-space.length()-1)).append("|\n");
        availableSpace = width - 2;
        symbolLength = southConnector.length();
        leftPadding = (availableSpace - symbolLength) / 2;
        rightPadding = availableSpace - symbolLength - leftPadding;
        str.append("|").append(" ".repeat(leftPadding))
                .append(southConnector)
                .append(" ".repeat(rightPadding)).append("|\n");
        str.append(border);
        return str.toString();
    }
}
