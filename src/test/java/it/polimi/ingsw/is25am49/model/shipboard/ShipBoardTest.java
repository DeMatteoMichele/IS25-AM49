package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.ConnectorType;
import it.polimi.ingsw.is25am49.shared.enums.Orientation;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.shared.enums.Color;
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
    ShipBoard shipTest;

    @BeforeEach
    void setUp() {
        ship1 = new ShipBoard(1,501, Color.RED);
        ship2 = new ShipBoard(2,502,Color.BLUE);
        shipTest= new ShipBoard(2,555,Color.GREEN);

        Map<Integer, Tile> tiles= Tile.generateTiles();
        ship1.insertTile(new Point(7,6), tiles.get(151));
        ship1.insertTile(new Point(6,7), tiles.get(11));
        ship1.getTile(new Point(6,7)).setTileOrientation(Orientation.EAST);
        ship1.insertTile(new Point(5,9), tiles.get(11));
        ship1.insertTile(new Point(7,8), tiles.get(17));

        shipTest.insertTile(new Point(7,6), tiles.get(153));
        shipTest.insertTile(new Point(6,6), tiles.get(152));
        shipTest.getTile(new Point(6,6)).setTileOrientation(Orientation.WEST);
        shipTest.insertTile(new Point(8,6), tiles.get(16));
        shipTest.getTile(new Point(8,6)).setTileOrientation(Orientation.WEST);
        shipTest.insertTile(new Point(6,7), tiles.get(46));
        shipTest.insertTile(new Point(6,8), tiles.get(143));
        shipTest.insertTile(new Point(8,5), tiles.get(126));
        shipTest.insertTile(new Point(9,6), tiles.get(116));
        shipTest.insertTile(new Point(5,7), tiles.get(137));
        shipTest.insertTile(new Point(5,8), tiles.get(104));
        shipTest.getTile(new Point(5,8)).setTileOrientation(Orientation.WEST);
        shipTest.insertTile(new Point(7,8), tiles.get(75));
        shipTest.insertTile(new Point(8,8), tiles.get(96));
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
    void removeTile() {
    }

    @Test
    void destroyCenterTileTest() {
        shipTest=ShipBoard.generateShipboard(2,30);
        System.out.println(shipTest);
        shipTest.destroyTile(new Point(7,7));
        shipTest.destroyDisconnectedTiles();
        System.out.println(shipTest);
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
            System.out.println(ship2);
            ship2.completeShipBoard();
            System.out.println(ship2);
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
       assertThrows(RuntimeException.class,()->ship1.addTokenToTile(new Point(7,7), Token.BATTERY_CELLS));
        assertThrows(RuntimeException.class,()->ship1.fillTileComponent(new Point(7,7), Token.RED_GOODS));
       ship1.fillTileComponent(new Point(7,7), Token.ASTRONAUT);
        assertThrows(RuntimeException.class,()->ship1.addTokenToTile(new Point(7,7), Token.ASTRONAUT));

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
        assertThrows(RuntimeException.class,()->ship1.removeTokenFromTile(new Point(7,8), Token.BLUE_GOODS));
        ship1.removeTokenFromTile(new Point(7,8), Token.BATTERY_CELLS);
        assertEquals(2, ship1.getTotalNumberOfaToken(Token.BATTERY_CELLS));
    }


    @Test
    void testTurnOnComponent() {
        ship1.completeShipBoard();
        System.out.println(ship1);
        ship1.turnOnComponent(new Point(21,6),new Point(6,7));
        assertEquals(0,ship1.getProtectedSides().size());
        ship1.turnOnComponent(new Point(7,6),new Point(6,7));
        assertEquals(2,ship1.getProtectedSides().size());
        assertEquals(1,ship1.getTotalNumberOfaToken(Token.BATTERY_CELLS));
        ship1.turnOnComponent(new Point(7,6),new Point(6,7));
        assertEquals(2,ship1.getProtectedSides().size());
    }

    @Test
    void testGetActivatableComponents() {
        ship1.printShipboardSpecial();
        assertThrows(IllegalMethodOnThisComponentException.class, ()->ship1.getActivatableComponents(ComponentType.CANNON));
        ArrayList<Point> points= ship1.getActivatableComponents(ComponentType.SHIELD);
        System.out.println(points);
        assertThrows(RuntimeException.class,()->ship1.addTokenToTile(points.getFirst(),Token.YELLOW_GOODS));
    }

    @Test
    void testGetProtectedSides() {
        shipTest.fillTileComponent(new Point(8,6),Token.BATTERY_CELLS);
        shipTest.printShipboardSpecial();

        assertEquals(0,shipTest.getProtectedSides().size());
        shipTest.turnOnComponent(new Point(7,6),new Point(8,6));
        assertEquals(2,shipTest.getProtectedSides().size());
        System.out.println(shipTest.getProtectedSides());
        shipTest.turnOff(new Point(7,6));

        shipTest.turnOnComponent(new Point(6,6),new Point(8,6));
        assertEquals(2,shipTest.getProtectedSides().size());
        System.out.println(shipTest.getProtectedSides());

        shipTest.turnOnComponent(new Point(7,6),new Point(8,6));
        assertEquals(3,shipTest.getProtectedSides().size());
        System.out.println(shipTest.getProtectedSides());
    }

    @Test
    void testCheckIfAlienArePossible() {
        shipTest.printShipboardSpecial();
        assertEquals(1,shipTest.checkIfAlienArePossible(new Point(7,7)).size());
        assertTrue(shipTest.checkIfAlienArePossible(new Point(7,7)).contains(Token.ASTRONAUT));
        assertEquals(2,shipTest.checkIfAlienArePossible(new Point(6,7)).size());
        assertTrue(shipTest.checkIfAlienArePossible(new Point(6,7)).contains(Token.PURPLE_ALIENS));
    }
    @Test
    void getTotalFirePower() {
        shipTest.printShipboardSpecial();
        shipTest.fillTileComponent(new Point(8,6),Token.BATTERY_CELLS);

        assertEquals(1.5,shipTest.getTotalFirePower());
        shipTest.turnOnComponent(new Point(8,5),new Point(8,6));
        assertEquals(3.5,shipTest.getTotalFirePower());
        shipTest.addTokenToTile(new Point(6,7),Token.PURPLE_ALIENS);
        assertEquals(5.5,shipTest.getTotalFirePower());
    }

    @Test
    void testGetTotalEnginePower() {
        System.out.println(shipTest);
        shipTest.fillTileComponent(new Point(8,6),Token.BATTERY_CELLS);
        assertEquals(1,shipTest.getTotalEnginePower());
        shipTest.turnOnComponent(new Point(8,8),new Point(8,6));
        assertEquals(3,shipTest.getTotalEnginePower());
        shipTest.addTokenToTile(new Point(6,7),Token.BROWN_ALIENS);
        assertEquals(5,shipTest.getTotalEnginePower());
    }

    @Test
    void getAlienPointTest(){
        System.out.println(shipTest);
        assertEquals(new Point(6,7),shipTest.getAlienPoint().get());
        shipTest.addTokenToTile(new Point(6,7),Token.BROWN_ALIENS);
        assertEquals(Optional.empty(),shipTest.getAlienPoint());

    }


    @Test
    void getPositionsOfaToken() {
        shipTest.printShipboardSpecial();
        shipTest.fillTileComponent(new Point(8,6),Token.BATTERY_CELLS);
        shipTest.fillTileComponent(new Point(7,7),Token.ASTRONAUT);
        shipTest.addTokenToTile(new Point(6,7),Token.PURPLE_ALIENS);
        System.out.println("purple aliens: "+shipTest.getPositionsOfaToken(Token.PURPLE_ALIENS));
        System.out.println("BatteryCells: "+shipTest.getPositionsOfaToken(Token.BATTERY_CELLS));
    }

    @Test
    void testRemoveAllTokens() {
        shipTest.fillTileComponent(new Point(8,6),Token.BATTERY_CELLS);
        shipTest.fillTileComponent(new Point(7,7),Token.ASTRONAUT);
        shipTest.addTokenToTile(new Point(6,7),Token.PURPLE_ALIENS);
        shipTest.removeAllTokens();
        assertEquals(0,shipTest.getTotalNumberOfaToken(Token.BATTERY_CELLS));
        assertEquals(0,shipTest.getTotalNumberOfaToken(Token.ASTRONAUT));
        assertEquals(0,shipTest.getTotalNumberOfaToken(Token.PURPLE_ALIENS));
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
        assertEquals(3,externalPoints.size());
        System.out.println(externalPoints);
    }

    @Test
    void testGetExposedPoint() {
        shipTest.printShipboardSpecial();
        assertEquals(new Point(5,7),shipTest.getExposedPoint(5,Orientation.NORTH).get());
        assertEquals(new Point(8,5),shipTest.getExposedPoint(5,Orientation.WEST).get());
        assertEquals(new Point(6,6),shipTest.getExposedPoint(6,Orientation.WEST).get());
        assertEquals(new Point(8,6),shipTest.getExposedPoint(8,Orientation.SOUTH).get());
    }

    @Test
    void getAvailableSpaceForAToken() {
        shipTest= ShipBoard.generateShipboard(2,30);
        System.out.println(shipTest);
        System.out.println("battery: "+shipTest.getAvailableSpaceForAToken(Token.BATTERY_CELLS));
        System.out.println("astronatus: "+shipTest.getAvailableSpaceForAToken(Token.ASTRONAUT));
        System.out.println("redgoods: "+shipTest.getAvailableSpaceForAToken(Token.RED_GOODS));
        System.out.println("bluegoods: "+shipTest.getAvailableSpaceForAToken(Token.BLUE_GOODS));
    }

}