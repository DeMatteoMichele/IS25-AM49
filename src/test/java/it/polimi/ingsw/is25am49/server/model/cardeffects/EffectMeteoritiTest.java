package it.polimi.ingsw.is25am49.server.model.cardeffects;

import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectMeteoriti;
import it.polimi.ingsw.is25am49.shared.enums.*;
import it.polimi.ingsw.is25am49.server.model.shipboard.*;
import it.polimi.ingsw.is25am49.server.model.shipboard.Component;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

class EffectMeteoritiTest {
    Player player;
    ShipBoard ship;
    CardEffect cardEffect;
    Component component1,component2,component3,component4,component5,component6,component7;
    Tile tile1,tile2,tile3,tile4,tile5,tile6,tile7;

    @BeforeEach
    void init(){
        player = new Player("Marco", Color.RED, 2);
        ship = new ShipBoard(1,2,Color.RED);
        player.setShipBoard(ship);
        component1 = new ContainerComponent(ComponentType.STORAGE,2);
        component2 = new SpecialComponent(ComponentType.SHIELD,2);
        component3 = new ContainerComponent(ComponentType.BATTERY,2);
        component4 = new SpecialComponent(ComponentType.DOUBLE_CANNON,2);
        tile1 = new Tile(1,component1);
        tile2 = new Tile(2,component2);
        tile3 = new Tile(3,component3);
        tile4 = new Tile(4,component4);
        player.getShipBoard().insertTile(new Point(7,6), tile1);
        player.getShipBoard().insertTile(new Point(6,7), tile2);
        player.getShipBoard().insertTile(new Point(8,7), tile3);
        player.getShipBoard().insertTile(new Point(5,7), tile4);
        player.getShipBoard().addTokenToTile(new Point(8,7), Token.BATTERY_CELLS);
        player.getShipBoard().addTokenToTile(new Point(8,7), Token.BATTERY_CELLS);
        tile1.setConnectors(ConnectorType.FLAT,
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
        tile4.setTileOrientation(Orientation.NORTH);
        ship.completeShipBoard();
    }


    @Test
    void action() {

        cardEffect = new EffectMeteoriti("effect meteroiti", 0, Orientation.NORTH);
        cardEffect.setPosition(7);
        //prova per vedere se rimbalza
        cardEffect.action(player);
        ship.printShipboardSpecial();
    }
    @Test
    void action2(){
        cardEffect = new EffectMeteoriti("effect meteroiti", 0, Orientation.NORTH);
        cardEffect.setPosition(7);
        //prova per vedere se scudi attivi non fa nulla
        player.getShipBoard().turnOnComponent(new Point(6,7), new Point(8,7));
        cardEffect.action(player);
    }
    //test per meteorite grande
    @Test
    void action3(){
        cardEffect = new EffectMeteoriti("effect meteroiti", 1, Orientation.NORTH);
        cardEffect.setPosition(7);
        cardEffect.action(player);
        ship.printShipboardSpecial();
    }
    //test per vedere se i cannoni distruggono meteorite
    @Test
    void action4(){
        cardEffect = new EffectMeteoriti("effect meteroiti", 1, Orientation.NORTH);
        cardEffect.setPosition(6);
        player.getShipBoard().turnOnComponent(new Point(5,7), new Point(8,7));
        cardEffect.action(player);
        ship.printShipboardSpecial();

    }

}