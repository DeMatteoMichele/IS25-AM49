package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.server.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.server.model.enums.ConnectorType;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisComponentException;
import it.polimi.ingsw.is25am49.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
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

        Map<Integer, Tile> tiles= Tile.generateTiles();
        ship1.insertTile(new Point(7,6), tiles.get(151));
        ship1.insertTile(new Point(6,7), tiles.get(11));
        ship1.getTile(new Point(6,7)).setTileOrientation(Orientation.EAST);
        ship1.insertTile(new Point(5,9), tiles.get(11));
        ship1.insertTile(new Point(7,8), tiles.get(17));
    }

    @Test
   void testConnectorMatch(){
        assertFalse(ship1.connectorsMatch(ConnectorType.FLAT,ConnectorType.FLAT));
        assertFalse(ship1.connectorsMatch(ConnectorType.FLAT,ConnectorType.UNIVERSAL));
        assertFalse(ship1.connectorsMatch(ConnectorType.SINGLE,ConnectorType.FLAT));
        assertFalse(ship1.connectorsMatch(ConnectorType.DOUBLE,ConnectorType.SINGLE));
        assertTrue(ship1.connectorsMatch(ConnectorType.DOUBLE,ConnectorType.DOUBLE));
        assertTrue(ship1.connectorsMatch(ConnectorType.SINGLE,ConnectorType.SINGLE));
        assertTrue(ship1.connectorsMatch(ConnectorType.UNIVERSAL,ConnectorType.DOUBLE));
        assertTrue(ship1.connectorsMatch(ConnectorType.SINGLE,ConnectorType.UNIVERSAL));
    }
    @Test
    void testGetTile() {
        ship1.printShipboard();
        assertEquals(11, ship1.getTile(new Point(5,9)).getIdTile());
        assertEquals(151, ship1.getTile(new Point(7,6)).getIdTile());
        assertThrows(InvalidCoordinatesException.class, ()->ship1.getTile(new Point(9,5)));
        assertThrows(InvalidCoordinatesException.class, ()->ship1.getTile(new Point(6,6)));
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
    void testIfTileIsConnected() {
        Map<Integer,Tile> tiles= Tile.generateTiles();
        ship2.insertTile(new Point(6,5), tiles.get(2));
        ship2.insertTile(new Point(6,6), tiles.get(4));
        ship2.insertTile(new Point(7,6), tiles.get(6));
        ship2.getTile(new Point(7,6)).setTileOrientation(Orientation.WEST);
        ship2.insertTile(new Point(8,6), tiles.get(6));
        ship2.getTile(new Point(8,6)).setTileOrientation(Orientation.EAST);
        ship2.printShipboardSpecial();
        ship2.completeShipBoard();
        ship2.printShipboardSpecial();
    }

    @Test
    void testGetTilesConnectedTo() {
        for (int i = 0; i <10 ; i++) {
            System.out.println(i+1 +") Generazione");
            ship2=ShipBoard.generateShipboard(2,30);
            ship2.printShipboard();
            ship2.printShipboardSpecial();
            ship2.completeShipBoard();
            ship2.printShipboardSpecial();
        }

    }

    @Test
    void getNumberOfExposedConnectors() {
        for (int i = 0; i <10 ; i++) {
            System.out.println(i+1 +") Generazione");
            ship2=ShipBoard.generateShipboard(2,30);
            ship2.completeShipBoard();
            ship2.printShipboardSpecial();
            System.out.println("CONNETTORI ESPOSTI: "+ship2.getNumberOfExposedConnectors()+ " --> conta siano giusti");
        }
    }

    @Test
    void testAddFillTokenToTiles() {
        ship1.addTokenToTile(new Point(7,7), Token.BATTERY_CELLS);
        ship1.fillTileComponent(new Point(7,7), Token.RED_GOODS);
        ship1.fillTileComponent(new Point(7,7), Token.ASTRONAUT);
        ship1.addTokenToTile(new Point(7,7), Token.ASTRONAUT);

        assertThrows(InvalidCoordinatesException.class, ()-> ship1.addTokenToTile(new Point(5,7), Token.ASTRONAUT));
        assertThrows(InvalidCoordinatesException.class, ()-> ship1.addTokenToTile(new Point(4,5), Token.ASTRONAUT));
        assertThrows(InvalidCoordinatesException.class, ()-> ship1.fillTileComponent(new Point(4,4), Token.ASTRONAUT));
        assertThrows(InvalidCoordinatesException.class, ()-> ship1.fillTileComponent(new Point(5,7), Token.ASTRONAUT));
    }

    @Test
    void testRemoveTokenFromTile() {
        //ship1.printShipboardSpecial();
        ship1.removeTokenFromTile(new Point(7,7), Token.ASTRONAUT);
        assertThrows(InvalidCoordinatesException.class, ()-> ship1.removeTokenFromTile(new Point(4,5), Token.ASTRONAUT));
        assertThrows(InvalidCoordinatesException.class, ()-> ship1.removeTokenFromTile(new Point(5,7), Token.ASTRONAUT));
        ship1.fillTileComponent(new Point(7,8),Token.BATTERY_CELLS);
        ship1.removeTokenFromTile(new Point(7,8), Token.BLUE_GOODS);
        ship1.removeTokenFromTile(new Point(7,8), Token.BATTERY_CELLS);
        assertEquals(2, ship1.getTotalNumberOfaToken(Token.BATTERY_CELLS));
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
        assertEquals(0,sides.size());
        ship1.turnOnComponent(new Point(7,6), new Point(7,8));
        assertEquals(0,sides.size());
        ship1.fillTileComponent(new Point(7,8), Token.BATTERY_CELLS);
        ship1.turnOnComponent(new Point(7,6), new Point(7,8));
        sides = ship1.getProtectedSides();
        System.out.println(sides);
        assertEquals(2,sides.size());
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
    void testRemoveAllTokens() {
    }

    @Test
    void testGetExternalPoints() {
        ship1.printShipboardSpecial();
        var externalPoints= ship1.getExternalPoints();
        assertEquals(5,externalPoints.size());
        System.out.println(externalPoints);

        ship1.insertTile(new Point(8,7),ship1.removeTile(new Point(5,9)));
        ship1.completeShipBoard();
        ship1.printShipboardSpecial();
        externalPoints= ship1.getExternalPoints();
        assertEquals(4,externalPoints.size());
        System.out.println(externalPoints);
    }
    @Test
    void testConnectorsMatch() {
    }

    @Test
    void testGetExposedPoint() {
    }


}