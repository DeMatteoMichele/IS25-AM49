package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisCardException;
import it.polimi.ingsw.is25am49.model.shipboard.Component;
import it.polimi.ingsw.is25am49.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.model.shipboard.Tile;
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
    Point coordinate = new Point(3, 2);
    Point coordinate2 = new Point(3, 4);
    CardEffect card;
    Tile tile2;

    @BeforeEach
    void init(){
        shipBoard = new ShipBoard();
        player = new Player("io",Color.RED,3);
        component2 = new ContainerComponent(ComponentType.BATTERY,4);
        component = new ContainerComponent(ComponentType.SPECIAL_STORAGE, 5);
        tile = new Tile(3, component );
        tile2 = new Tile(2,component2);
        player.getShipBoard().insertTile(coordinate, tile);
        player.getShipBoard().insertTile(coordinate2, tile2);
        player.getShipBoard().addTokenToTile(coordinate2,Token.BATTERY_CELLS);
        player.getShipBoard().addTokenToTile(coordinate2,Token.BATTERY_CELLS);
        player.getShipBoard().addTokenToTile(coordinate2,Token.BATTERY_CELLS);
        player.getShipBoard().addTokenToTile(coordinate, Token.RED_GOODS);
        player.getShipBoard().addTokenToTile(coordinate, Token.RED_GOODS);
        player.getShipBoard().addTokenToTile(coordinate, Token.BLUE_GOODS);
        player.getShipBoard().addTokenToTile(coordinate, Token.YELLOW_GOODS);
        player.getShipBoard().addTokenToTile(coordinate, Token.GREEN_GOODS);
        card = new EffectLoseGoods("perdi", 2);

    }
    @Test
    void action() {
        card.action(player);
        assertEquals(1, player.getShipBoard().getPositionsOfaToken(Token.GREEN_GOODS).size());

    }

    @Test
    void setPosition() {
        assertThrows(IllegalMethodOnThisCardException.class, ()->card.setPosition(2));
    }


}