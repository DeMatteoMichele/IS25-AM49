package it.polimi.ingsw.is25am49.client.model.shipboard;

import it.polimi.ingsw.is25am49.client.model.enums.Color;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.client.model.shipboard.Tile;
import it.polimi.ingsw.is25am49.client.model.enums.Orientation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ShipBoardTest {
    ShipBoard ship;
    ShipBoard shipTest;


    @Test
    void tileToStringList() {
        ship=new ShipBoard();
        Map<Integer, Tile> tiles= Tile.generateTiles();
        for (Tile t: tiles.values()){
            ArrayList<String> list= new ArrayList<>(ship.tileToStringList(Optional.of(t)));
            for (String s: list){
                System.out.println(s);
            }
        }
    }

    @Test
    void testToStringShipBoard(){
        ship = new ShipBoard(1, 501, Color.RED);
        ship= ShipBoard.generateShipboard(1,56);
        ship.printShipboardSpecial();
        ship.fillTileComponent(new Point(7,7), Token.ASTRONAUT);
        System.out.println(ship);
        ship.completeShipBoard();
        System.out.println(ship);

        ship = new ShipBoard(2, 501,Color.RED);
        ship= ShipBoard.generateShipboard(2,10);
        ship.printShipboardSpecial();
        ship.fillTileComponent(new Point(7,7), Token.ASTRONAUT);
        System.out.println(ship);
        ship.completeShipBoard();
        System.out.println(ship);
    }
    @Test
    void testGetAlienPoint(){
        shipTest = new ShipBoard(2, 501, Color.RED);
        Map<Integer, Tile> tiles= Tile.generateTiles();

        shipTest.insertTile(new Point(7,6), tiles.get(153));
        shipTest.insertTile(new Point(6,6), tiles.get(152));
        shipTest.getTile(new Point(6,6)).setTileOrientation(Orientation.WEST);
        shipTest.insertTile(new Point(8,6), tiles.get(16));
        shipTest.getTile(new Point(8,6)).setTileOrientation(Orientation.WEST);
        shipTest.insertTile(new Point(6,7), tiles.get(46));

        shipTest.insertTile(new Point(8,7), tiles.get(43));
        shipTest.insertTile(new Point(9,7), tiles.get(138));
        shipTest.insertTile(new Point(8,8), tiles.get(148));
        shipTest.insertTile(new Point(7,8), tiles.get(143));

        shipTest.insertTile(new Point(6,8), tiles.get(137));
        shipTest.insertTile(new Point(8,5), tiles.get(126));
        shipTest.insertTile(new Point(9,6), tiles.get(116));
        shipTest.insertTile(new Point(5,7), tiles.get(104));
        shipTest.getTile(new Point(5,7)).setTileOrientation(Orientation.WEST);

        System.out.println(shipTest);
        System.out.println(shipTest.getAlienPoint());
        shipTest.addTokenToTile(shipTest.getAlienPoint().get(), Token.PURPLE_ALIENS);
        System.out.println(shipTest);
        System.out.println(shipTest.getAlienPoint());
        shipTest.addTokenToTile(shipTest.getAlienPoint().get(), Token.BROWN_ALIENS);
        System.out.println(shipTest);
        System.out.println(shipTest.getAlienPoint());
        shipTest.printShipboard();
    }
}