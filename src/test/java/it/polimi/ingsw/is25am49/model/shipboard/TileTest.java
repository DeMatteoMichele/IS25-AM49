package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.ConnectorType;
import it.polimi.ingsw.is25am49.model.enums.Orientation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

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

        t11.setConnectors(ConnectorType.FLAT, ConnectorType.UNIVERSAL, ConnectorType.DOUBLE, ConnectorType.SINGLE);
    }
    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetTileRotation() {
        assertEquals(Orientation.NORTH, t11.getTileRotation());
        assertNotEquals(Orientation.EAST, t12.getTileRotation());
    }

    @Test
    void setTileRotation() {
        t11.setTileRotation(Orientation.SOUTH);
        assertEquals(Orientation.SOUTH, t11.getTileRotation());
        assertEquals(ConnectorType.UNIVERSAL, t11.getConnector(Orientation.WEST));
        assertEquals(ConnectorType.DOUBLE, t11.getConnector(Orientation.NORTH));
        assertEquals(ConnectorType.SINGLE, t11.getConnector(Orientation.EAST));
        assertEquals(ConnectorType.FLAT, t11.getConnector(Orientation.SOUTH));

        t11. setTileRotation(Orientation.WEST);
        assertEquals(Orientation.WEST, t11.getTileRotation());
        assertEquals(ConnectorType.FLAT, t11.getConnector(Orientation.WEST));
        assertEquals(ConnectorType.UNIVERSAL, t11.getConnector(Orientation.NORTH));
        assertEquals(ConnectorType.DOUBLE, t11.getConnector(Orientation.EAST));
        assertEquals(ConnectorType.SINGLE, t11.getConnector(Orientation.SOUTH));
    }

    @Test
    void rotateTileClockwise() {
        t11.rotateTileClockwise();
        assertEquals(Orientation.EAST, t11.getTileRotation());
        assertEquals(ConnectorType.UNIVERSAL, t11.getConnector(Orientation.WEST));
        assertEquals(ConnectorType.DOUBLE, t11.getConnector(Orientation.NORTH));
        assertEquals(ConnectorType.SINGLE, t11.getConnector(Orientation.EAST));
        assertEquals(ConnectorType.FLAT, t11.getConnector(Orientation.SOUTH));
    }

    @Test
    void rotateTileCounterClockwise() {
    }

    @Test
    void lockTile() {
    }

    @Test
    void testGetConnector() {
        assertEquals(ConnectorType.FLAT,t11.getConnector(Orientation.NORTH));
        assertEquals(ConnectorType.UNIVERSAL,t11.getConnector(1));
    }

    @Test
    void getFirePower() {
    }

    @Test
    void addToken() {
    }
}