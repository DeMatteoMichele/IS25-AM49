package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectCannonata;
import it.polimi.ingsw.is25am49.server.model.enums.*;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.shipboard.*;
import it.polimi.ingsw.is25am49.server.model.shipboard.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class EffectCannonataTest {
    Player player;
    ShipBoard ship;
    CardEffect cardEffect;
    Tile tile1,tile2,tile3,tile4,tile5,tile6,tile7;
    Component component1,component2,component3,component4,component5,component6,component7;

    @BeforeEach
    void init(){
        player = new Player("Marco", Color.RED, 2);
        ship = new ShipBoard(1,2);
        player.setShipBoard(ship);

        component1 = new ContainerComponent(ComponentType.STORAGE,2);
        tile1 = new Tile(1,component1);
        component2 = new ContainerComponent(ComponentType.STORAGE,2);
        tile2 = new Tile(2,component2);
        component3 = new SpecialComponent(ComponentType.SHIELD,2);
        tile3 = new Tile(3,component3);
        component4 = new ContainerComponent(ComponentType.BATTERY,2);
        tile4 = new Tile(4,component4);
        tile1.setConnectors(ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL);
        tile2.setConnectors(ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL);
        tile3.setConnectors(ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL);
        tile4.setConnectors(ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL);
        player.getShipBoard().insertTile(new Point(6,7), tile1);
        player.getShipBoard().insertTile(new Point(6,6), tile2);
        player.getShipBoard().insertTile(new Point(7,6), tile3);
        player.getShipBoard().insertTile(new Point(8,6), tile4);
        player.getShipBoard().addTokenToTile(new Point(8,6), Token.BATTERY_CELLS);

        cardEffect = new EffectCannonata("effect cannonata", 0, Orientation.NORTH);
        cardEffect.setPosition(6);
        player.getShipBoard().completeShipBoard();

    }

    @Test
    void action() {
        player.getShipBoard().turnOnComponent(new Point(7,6), new Point(8,6));
        cardEffect.action(player);
        ship.printShipboardSpecial();

    }
    @Test
    void action2(){
        cardEffect.action(player);
        ship.printShipboardSpecial();

    }
    @Test
    void setPosition() {
    }

    @Test
    void setListOfCoordinates() {
    }

    @Test
    void setCoordinate() {
    }

    @Test
    void getLevel() {
    }
}