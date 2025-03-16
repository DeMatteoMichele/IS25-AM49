package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class ShipBoardTest {
    ShipBoard ship1;
    ShipBoard ship2;

    @BeforeEach
    void setUp() {
        ship1 = new ShipBoard(1,1);
        ship2 = new ShipBoard(2,2);
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
    void testAddTokenToTile() {
        ship1.addTokenToTile(new Point(7,7), Token.BATTERY_CELLS);
        ship1.addTokenToTile(new Point(7,7), Token.ASTRONAUT);
        ship1.addTokenToTile(new Point(7,7), Token.ASTRONAUT);
        ship1.addTokenToTile(new Point(7,7), Token.ASTRONAUT);
        ship1.addTokenToTile(new Point(5,7), Token.ASTRONAUT);

    }

    @Test
    void removeTokenFromTile() {
    }

    @Test
    void consumeComponentToken() {
    }

    @Test
    void turnOnComponent() {
    }

    @Test
    void testTurnOnComponent() {
    }

    @Test
    void getActivatableComponents() {
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
}