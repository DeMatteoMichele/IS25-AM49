package it.polimi.ingsw.is25am49.client.model.shipboard;

import it.polimi.ingsw.is25am49.shared.enums.Color;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

class ShipBoardTest {
    ShipBoard ship;


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
}