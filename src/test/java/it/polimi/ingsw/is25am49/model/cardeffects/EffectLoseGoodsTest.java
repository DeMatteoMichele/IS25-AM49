package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectLoseGoods;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisCardException;
import it.polimi.ingsw.is25am49.server.model.shipboard.Component;
import it.polimi.ingsw.is25am49.server.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EffectLoseGoodsTest {
    Player player;
    ShipBoard shipBoard;
    Tile tile;
    Component component;
    Component component2;
    Component component3;
    Point coordinate = new Point(7, 8);
    Point coordinate2 = new Point(6, 8);
    Point coordinate3 = new Point(6, 7);
    CardEffect card;
    Tile tile2;
    Tile tile3;

    @BeforeEach
    void init(){

        player = new Player("io",Color.RED,3);
        shipBoard = new ShipBoard(1,3,Color.RED);
        player.setShipBoard(shipBoard);
        component3 = new ContainerComponent(ComponentType.SPECIAL_STORAGE, 2);
        component2 = new ContainerComponent(ComponentType.BATTERY,4);
        component = new ContainerComponent(ComponentType.SPECIAL_STORAGE, 5);
        tile = new Tile(3, component );
        tile2 = new Tile(2,component2);
        tile3 = new Tile(4,component3);
        player.getShipBoard().insertTile(coordinate, tile);
        player.getShipBoard().insertTile(coordinate2, tile2);
        player.getShipBoard().insertTile(coordinate3, tile3);
        player.getShipBoard().addTokenToTile(coordinate3, Token.RED_GOODS);
        player.getShipBoard().addTokenToTile(coordinate3, Token.RED_GOODS);
        player.getShipBoard().addTokenToTile(coordinate2,Token.BATTERY_CELLS);
        player.getShipBoard().addTokenToTile(coordinate2,Token.BATTERY_CELLS);
        player.getShipBoard().addTokenToTile(coordinate2,Token.BATTERY_CELLS);
        player.getShipBoard().addTokenToTile(coordinate, Token.RED_GOODS);
        player.getShipBoard().addTokenToTile(coordinate, Token.RED_GOODS);
        player.getShipBoard().addTokenToTile(coordinate, Token.GREEN_GOODS);
        player.getShipBoard().addTokenToTile(coordinate, Token.YELLOW_GOODS);
        player.getShipBoard().addTokenToTile(coordinate, Token.GREEN_GOODS);
        card = new EffectLoseGoods("perdi", 8);

    }
    @Test
    void action() {
        card.action(player);
        assertEquals(0, player.getShipBoard().getTotalNumberOfaToken(Token.RED_GOODS));
        assertEquals(0, player.getShipBoard().getPositionsOfaToken(Token.GREEN_GOODS).size());
        assertEquals(2, player.getShipBoard().getPositionsOfaToken(Token.BATTERY_CELLS).size());
    }

    @Test
    void setPosition() {
        assertThrows(IllegalMethodOnThisCardException.class, ()->card.setPosition(2));
    }


}