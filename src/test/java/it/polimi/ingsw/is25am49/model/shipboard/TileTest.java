package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.server.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.server.model.enums.ConnectorType;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalTokenInComponentEcxeption;
import it.polimi.ingsw.is25am49.server.model.shipboard.BasicComponent;
import it.polimi.ingsw.is25am49.server.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.server.model.shipboard.SpecialComponent;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class TileTest {
    Tile t11;
    Tile t12;
    Tile t21;
    Tile t22;
    Tile t23;
    Tile t3;

    @BeforeEach
    void setUpClass() {
        t11= new Tile(22, new BasicComponent(ComponentType.FRAME_SECTION,0));
        t12= new Tile(22, new BasicComponent(ComponentType.CANNON,2));
        t21= new Tile(1, new ContainerComponent(ComponentType.CABINE,2));
        t22= new Tile(1, new ContainerComponent(ComponentType.BATTERY,3));
        t23= new Tile(1, new ContainerComponent(ComponentType.STORAGE,5));
        t3= new Tile(3, new SpecialComponent(ComponentType.SHIELD,1));

        t11.setConnectors(ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.DOUBLE, ConnectorType.SINGLE);
        t11.setTileOrientation(Orientation.NORTH);
        t12.setConnectors(ConnectorType.FLAT,ConnectorType.SINGLE,ConnectorType.SINGLE,ConnectorType.DOUBLE);
        t12.setTileOrientation(Orientation.EAST);
        t21.setConnectors(ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.DOUBLE, ConnectorType.SINGLE);
        t21.setTileOrientation(Orientation.NORTH);
        t22.setConnectors(ConnectorType.DOUBLE, ConnectorType.UNIVERSAL, ConnectorType.DOUBLE, ConnectorType.SINGLE);
        t22.setTileOrientation(Orientation.NORTH);
    }

    @Test
    void testGetTileRotation() {
        assertEquals(Orientation.NORTH, t11.getTileOrientation());
        assertNotEquals(Orientation.EAST, t12.getTileOrientation());
    }

    @Test
    void setTileRotation() {
        t11.setTileOrientation(Orientation.SOUTH);
        assertEquals(Orientation.SOUTH, t11.getTileOrientation());
        assertEquals(ConnectorType.UNIVERSAL, t11.getConnector(Orientation.WEST));
        assertEquals(ConnectorType.DOUBLE, t11.getConnector(Orientation.NORTH));
        assertEquals(ConnectorType.SINGLE, t11.getConnector(Orientation.EAST));
        assertEquals(ConnectorType.FLAT, t11.getConnector(Orientation.SOUTH));

        t11.setTileOrientation(Orientation.WEST);
        assertEquals(Orientation.WEST, t11.getTileOrientation());
        assertEquals(ConnectorType.FLAT, t11.getConnector(Orientation.WEST));
        assertEquals(ConnectorType.UNIVERSAL, t11.getConnector(Orientation.NORTH));
        assertEquals(ConnectorType.DOUBLE, t11.getConnector(Orientation.EAST));
        assertEquals(ConnectorType.SINGLE, t11.getConnector(Orientation.SOUTH));
    }

    @Test
    void testRotateTileClockwise() {
        t11.rotateTileClockwise();
        assertEquals(Orientation.EAST, t11.getTileOrientation());
        assertEquals(ConnectorType.DOUBLE, t11.getConnector(Orientation.WEST));
        assertEquals(ConnectorType.SINGLE, t11.getConnector(Orientation.NORTH));
        assertEquals(ConnectorType.FLAT, t11.getConnector(Orientation.EAST));
        assertEquals(ConnectorType.UNIVERSAL, t11.getConnector(Orientation.SOUTH));
    }
    @Test
    void testTileCounterClockwise() {
        t11.rotateTileCounterClockwise();
        assertEquals(Orientation.WEST, t11.getTileOrientation());
        assertEquals(ConnectorType.FLAT, t11.getConnector(Orientation.WEST));
        assertEquals(ConnectorType.UNIVERSAL, t11.getConnector(Orientation.NORTH));
        assertEquals(ConnectorType.DOUBLE, t11.getConnector(Orientation.EAST));
        assertEquals(ConnectorType.SINGLE, t11.getConnector(Orientation.SOUTH));
    }

    @Test
    void lockTile() {
        t22.lockTile();
        assertTrue(t22.isLocked());
        assertFalse(t11.isLocked());
    }

    @Test
    void testGetConnector() {
        assertEquals(ConnectorType.FLAT,t11.getConnector(Orientation.NORTH));
        assertEquals(ConnectorType.UNIVERSAL,t11.getConnector(1));
    }

    @Test
    void testGetFirePower() {
        assertEquals(0,t11.getFirePower());
        assertEquals(2,t12.getFirePower());
        t12.rotateTileCounterClockwise();
        assertEquals(1,t12.getFirePower());
    }
    @Test
    void testAddToken(){
        assertThrows(IllegalTokenInComponentEcxeption.class, ()->{t21.addToken(Token.BATTERY_CELLS);});
    }

    @Test
    void testGenerateTiles(){
        Map<Integer,Tile> tiles= Tile.generateTiles();
        for (Tile t: tiles.values()){
            String s= switch (t.getComponent()){
                case BasicComponent ignored -> s="BasicComponent";
                case SpecialComponent ignored -> s="SpecialComponent";
                case ContainerComponent ignored -> s="ContainerComponent";
            };
            System.out.println("TILE: "+t.getIdTile()+ "\t"+s+": "+t.getComponent().getType()+"\tlvl:"+t.getComponent().getLevel());
            System.out.println("\tconnectors: "+t.getConnector(0)+" "+t.getConnector(1)+" "+t.getConnector(2)+" "+t.getConnector(3)+"\n");
        }
        System.out.println(tiles.size());
    }

    @Test
    void testToString(){
        System.out.println(t11.toString());
        System.out.println(t12.toString());
        System.out.println(t21.toString());
        System.out.println(t22.toString());

    }

}