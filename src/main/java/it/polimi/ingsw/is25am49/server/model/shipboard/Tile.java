package it.polimi.ingsw.is25am49.server.model.shipboard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.ConnectorType;
import it.polimi.ingsw.is25am49.shared.enums.Orientation;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisComponentException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Represents a Tile entity with properties such as ID, component, orientation,
 * connectors, locking state, and coverage state. Provides functionality for
 * manipulating orientation, connectors, and component interactions.
 */
public class Tile {
    /**
     * Represents the unique identifier of a Tile instance.
     * This value is immutable and defines the specific identity of the tile.
     */
    private final int idTile;
    /**
     * Represents the core spacecraft component associated with a tile.
     * Holds the functional details and properties of the component.
     * This field is immutable and set during the tile's construction.
     */
    private final Component component;
    /**
     * Represents the orientation of the tile on the game board.
     * It determines the direction the tile is facing (e.g., NORTH, EAST, SOUTH, WEST).
     */
    private Orientation tileOrientation;
    /**
     * Represents a mapping of connector indices to their respective types.
     * Each connector is identified by an integer key and associated with a {@link ConnectorType}.
     * Used to define the connection points for a Tile.
     */
    HashMap<Integer, ConnectorType> connectors;
    /**
     * Indicates whether the tile is locked, restricting any modifications or interactions.
     * A locked state typically prevents rotation and other state changes.
     */
    private boolean isLocked;
    /**
     * Indicates whether the tile is currently covered.
     * When true, the tile is in a covered state and may not be directly accessible or interactable.
     */
    private boolean isCovered;

    /**
     * Constructor for the Tile class.
     * Initializes the tile with default values:
     * - idTile is set to 0.
     * - Component is initialized with type SKY and level 0.
     * - Connectors are set to FLAT for all orientations.
     * - Tile orientation is set to NORTH.
     * - Tile is locked by default.
     * - Tile is uncovered by default.
     */
    public Tile(){
        this.idTile = 0;
        component= new BasicComponent(ComponentType.SKY,0);
        connectors= new HashMap<>(0);
        tileOrientation = Orientation.NORTH;
        isLocked= true;
        isCovered=false;
        connectors.put(0,ConnectorType.FLAT);
        connectors.put(1,ConnectorType.FLAT);
        connectors.put(2,ConnectorType.FLAT);
        connectors.put(3,ConnectorType.FLAT);
    }
    /**
     * Constructs a new Tile instance with specified ID and component.
     *
     * @param idTile the unique identifier for the tile
     * @param component the component associated with the tile, defining its functionality
     */
    public Tile(int idTile, Component component) {
        this.idTile = idTile;
        this.component = component;
        this.connectors = new HashMap<>(4);
        this.tileOrientation = Orientation.NORTH;
        this.isLocked= false;
        this.isCovered= true;
    }
    /**
     * Constructs a Tile with specified attributes and connectors.
     *
     * @param idTile the unique identifier of the tile
     * @param component the component associated with the tile
     * @param c1 the connector type at position 0
     * @param c2 the connector type at position 1
     * @param c3 the connector type at position 2
     * @param c4 the connector type at position 3
     */
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
    /**
     * Retrieves the ID of the tile.
     *
     * @return the integer identifier of the tile.
     */
    public int getIdTile() {
        return idTile;
    }
    /**
     * Retrieves the component associated with the tile.
     *
     * @return the {@code Component} assigned to the tile.
     */
    public Component getComponent() {
        return component;
    }
    /**
     * Retrieves the type of the component associated with the tile.
     *
     * @return the {@code ComponentType} of the component.
     */
    public ComponentType getComponentType() {return component.getType();}

    /**
     * Adds a specified token to the associated component of the tile.
     *
     * @param token the token to be added; must be an instance of the {@code Token} enum
     */
    public void addToken(Token token){component.addToken(token);}
    /**
     * Removes a specified token from the component associated with this tile.
     *
     * @param token the token to be removed from the component
     */
    public void removeToken(Token token) {component.removeToken(token);}
    /**
     * Removes a default token from the associated component of this tile.
     * Delegates the token removal operation to the component's implementation.
     */
    public void removeToken(){component.removeToken();}
    /**
     * Fills the associated component with the specified token.
     * Delegates the operation to the `component` object.
     *
     * @param token the token to be added to the component; must be valid for the component.
     */
    public void fillComponent(Token token){component.fillComponent(token);}
    /**
     * Clears the state and tokens of the associated component.
     * Delegates the operation to the component's clearComponent method.
     */
    public void clearComponent(){component.clearComponent();}
    /**
     * Retrieves the orientation of the tile.
     *
     * @return the current {@code Orientation} of the tile.
     */
    public Orientation getTileOrientation() {return tileOrientation;}
    /**
     * Calculates the number of occurrences of a specified token within the tile's component.
     * If the method is unsupported by the component, returns 0.
     *
     * @param token the specific token whose occurrences are to be counted
     * @return the number of occurrences of the specified token, or 0 if the method is not supported
     */
    int getNumberOfaToken(Token token) {
        try{return component.getNumberOfaToken(token);}
        catch (IllegalMethodOnThisComponentException _){return 0;}
    }

    /**
     * Attempts to activate the component associated with this tile.
     * If the operation is not allowed for the component type, the exception is caught,
     * and an error message is printed to the standard output.
     */
    public void turnOn(){
        try {
            component.turnOn();
        }catch (IllegalMethodOnThisComponentException e){
            System.out.println("cannot turn on "+component.type+" --> "+e.getMessage());
        }
    }
    /**
     * Attempts to deactivate the associated component by invoking its turnOff method.
     * If the operation is not supported for the specific component type, an error message
     * is logged indicating the exception and component details.
     */
    public void turnOff() {
        try {
            component.turnOff();
        }catch (IllegalMethodOnThisComponentException e){
            System.out.println("cannot turn off "+component.type+" --> "+e.getMessage());
        }
    }
    /**
     * Checks if the current tile's component is active.
     *
     * @return true if the component is active, false if inactive or an error occurs.
     */
    public boolean isActive(){
        try {
            return component.getIsActive();
        }catch (IllegalMethodOnThisComponentException e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Updates the orientation of the tile and rotates its connectors accordingly.
     * The rotation is performed using the Juggling algorithm.
     *
     * @param tileOrientation the new orientation for the tile, represented as an {@code Orientation} enum value.
     */
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
    /**
     * Rotates the tile's orientation 90 degrees clockwise.
     * Updates the tile's internal orientation state.
     */
    public void rotateTileClockwise(){
        Orientation newTileRotation= Orientation.values()[(tileOrientation.ordinal()+1)%4];
        setTileOrientation(newTileRotation);
    }
    /**
     * Rotates the tile's orientation 90 degrees counterclockwise.
     * Adjusts the orientation cyclically within the four cardinal directions.
     * Updates the tile's orientation by calling {@code setTileOrientation}.
     */
    public void rotateTileCounterClockwise(){
        int index = ((tileOrientation.ordinal()-1 % 4) + 4) % 4; //modulo per numeri negativi
        Orientation newTileRotation= Orientation.values()[index];
        setTileOrientation(newTileRotation);
    }

    /**
     * Checks whether the tile is currently locked.
     *
     * @return true if the tile is locked, false otherwise.
     */
    public boolean isLocked() {
        return isLocked;
    }
    /**
     * Locks the tile by setting its locked status to true.
     * Prevents further actions that require an unlocked state.
     */
    public void lockTile() {
        isLocked = true;
    }
    /**
     * Uncovers the tile by setting its covered status to false.
     * Updates the internal state indicating the tile is no longer hidden.
     */
    public void uncoverTile() {
        isCovered = false;
    }
    /**
     * Indicates whether the tile is currently covered.
     *
     * @return true if the tile is covered, false otherwise.
     */
    public boolean getIsCovered() {
        return isCovered;
    }
    /**
     * Sets the connectors for the tile with the specified types.
     *
     * @param c1 the connector type for the first connector
     * @param c2 the connector type for the second connector
     * @param c3 the connector type for the third connector
     * @param c4 the connector type for the fourth connector
     */
    public void setConnectors(ConnectorType c1, ConnectorType c2, ConnectorType c3, ConnectorType c4) {
        connectors.put(0,c1);
        connectors.put(1,c2);
        connectors.put(2,c3);
        connectors.put(3,c4);
    }
    /**
     * Retrieves the connector associated with a specified orientation.
     *
     * @param orientation the specified orientation from which to retrieve the connector.
     * @return the corresponding ConnectorType for the given orientation.
     */
    public ConnectorType getConnector(Orientation orientation){
        return connectors.get(orientation.ordinal());
    }
    /**
     * Retrieves the connector at the specified index.
     *
     * @param index the index of the connector to retrieve.
     * @return the {@code ConnectorType} at the given index.
     */
    public ConnectorType getConnector(int index){
        return connectors.get(index);
    }

    /**
     * Calculates the firepower of the tile's component, adjusting based on tile orientation.
     * If the orientation is NORTH, the full firepower of the component is returned.
     * For other orientations, the firepower is halved.
     *
     * @return the calculated firepower as a double value.
     */
    //restituisce la potenza di fuoco di un cannone, verifica che sia acceso
    public double getFirePower(){return component.getFirePower()*(tileOrientation.equals(Orientation.NORTH)?1:0.5);}
    /**
     * Calculates the potential firepower of a tile based on its component level
     * and its orientation. Firepower is higher if the tile is oriented NORTH.
     *
     * @return the potential firepower as a double. Equal to the component level
     *         if the orientation is NORTH, or half the level otherwise.
     */
    public double getPotentialFirePower(){return component.getLevel()*(tileOrientation.equals(Orientation.NORTH)?1:0.5);}
    /**
     * Retrieves the engine power of the tile's associated component.
     *
     * @return the engine power of the component; 0 if the component is not of type ENGINE or DOUBLE_ENGINE.
     */
    public int getEnginePower(){return component.getEnginePower();}

    /**
     * Generates a map of tiles by reading configuration data from a JSON file.
     * The configuration specifies details such as component type, level, and connectors.
     *
     * @return a map where keys are tile IDs and values are Tile objects, or null if an exception occurs
     */
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


    /**
     * Returns a string representation of the Tile object in a formatted structure.
     * The format includes the Tile ID, component details, orientation,
     * and graphical representations of connectors and component state.
     *
     * @return a formatted string representation of the Tile.
     */
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
