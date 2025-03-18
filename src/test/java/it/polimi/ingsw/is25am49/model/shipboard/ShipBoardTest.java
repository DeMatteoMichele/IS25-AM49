package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Orientation;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisComponentException;
import it.polimi.ingsw.is25am49.model.exceptions.InvalidCoordinatesException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ShipBoardTest {
    ShipBoard ship1;
    ShipBoard ship2;

    @BeforeEach
    void setUp() {
        ship1 = new ShipBoard(1,501);
        ship2 = new ShipBoard(2,502);

        Map<Integer,Tile> tiles= Tile.generateTiles();
        ship1.insertTile(new Point(7,6), tiles.get(151));
        ship1.insertTile(new Point(6,7), tiles.get(11));
        ship1.getTile(new Point(6,7)).setTileOrientation(Orientation.EAST);
        ship1.insertTile(new Point(5,9), tiles.get(11));
        ship1.insertTile(new Point(7,8), tiles.get(17));
    }

    @Test
    void insertTile() {
    }

    @Test
    void removeTile() {
    }

    @Test
    void destroyTile() {
    }

    @Test
    void destroyTilesCascade() {
    }

    @Test
    void checkTileConnectors() {
    }

    @Test
    void checkTileConnectorsCascade() {
    }

    @Test
    void getNumberOfExposedConnectors() {
    }

    @Test
    void testAddFillTokenToTiles() {
        ship1.addTokenToTile(new Point(7,7), Token.BATTERY_CELLS);
        ship1.fillTileComponent(new Point(7,7), Token.RED_GOODS);
        ship1.fillTileComponent(new Point(7,7), Token.ASTRONAUT);
        ship1.addTokenToTile(new Point(7,7), Token.ASTRONAUT);
        ship1.addTokenToTile(new Point(5,7), Token.ASTRONAUT);
        ship1.fillTileComponent(new Point(5,7), Token.ASTRONAUT);
        assertThrows(InvalidCoordinatesException.class, ()-> ship1.addTokenToTile(new Point(4,5), Token.ASTRONAUT));
        assertThrows(InvalidCoordinatesException.class, ()-> ship1.fillTileComponent(new Point(4,4), Token.ASTRONAUT));

    }

    @Test
    void testRemoveTokenFromTile() {
        //ship1.printShipboardSpecial();
        ship1.removeTokenFromTile(new Point(7,7), Token.ASTRONAUT);
        ship1.removeTokenFromTile(new Point(7,7), Token.BLUE_GOODS);
        ship1.removeTokenFromTile(new Point(5,7), Token.ASTRONAUT);
        assertThrows(InvalidCoordinatesException.class, ()-> ship1.removeTokenFromTile(new Point(4,5), Token.ASTRONAUT));
        ship1.fillTileComponent(new Point(7,8),Token.YELLOW_GOODS);
        ship1.addTokenToTile(new Point(7,8), Token.BLUE_GOODS);
        ship1.removeTokenFromTile(new Point(7,8), Token.YELLOW_GOODS);
        ship1.addTokenToTile(new Point(7,8), Token.BLUE_GOODS);
        assertEquals(1, ship1.getTotalNumberOfaToken(Token.YELLOW_GOODS));
        assertEquals(1, ship1.getTotalNumberOfaToken(Token.BLUE_GOODS));
    }

    @Test
    void testConsumeComponentToken() {
        ship1.printShipboardSpecial();


    }

    @Test
    void turnOnComponent() {
    }

    @Test
    void testTurnOnComponent() {
    }

    @Test
    void testGetActivatableComponents() {
        ship1.printShipboardSpecial();
        assertThrows(IllegalMethodOnThisComponentException.class, ()->ship1.getActivatableComponents(ComponentType.CANNON));
        ArrayList<Point> points= ship1.getActivatableComponents(ComponentType.SHIELD);
        System.out.println(points);
        ship1.addTokenToTile(points.getFirst(),Token.YELLOW_GOODS);
    }

    @Test
    void testGetProtectedSides() {
        Set<Orientation> sides = ship1.getProtectedSides();
        System.out.println(sides);
        assertEquals(0,sides.size());
    }

    @Test
    void getTotalFirePower() {
    }

    @Test
    void getEnginePower() {
    }

    @Test
    void getTotalNumberOfaToken() {
    }

    @Test
    void getPositionsOfaToken() {
    }

    @Test
    void removeAllTokens() {
    }

    @Test
    void checkRocketTile() {
    }

    @Test
    void checkCannonTile() {
    }

    @Test
    void afterBuildCheckShipboard() {
    }

    @Test
    void testCheckValidCoordinates() {
        ship1.printShipboard();
        assertTrue(ship1.checkValidCoordinates(new Point(7,7)));
        assertFalse(ship1.checkValidCoordinates(new Point(1,1)));
        assertFalse(ship1.checkValidCoordinates(new Point(5,6)));
        assertTrue(ship1.checkValidCoordinates(new Point(6,6)));
        assertFalse(ship1.checkValidCoordinates(new Point(13,24)));
    }


    @Test
    void testRemoveAllTokens() {
    }

    @Test
    void testGetNumberOfExposedConnectors() {
        ship1.afterBuildCheckShipboard();
        ship1.printShipboardSpecial();
        //System.out.println(ship1.getNumberOfExposedConnectors());
    }


}