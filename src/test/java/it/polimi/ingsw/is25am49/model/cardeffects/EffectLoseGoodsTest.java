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
    Point coordinate = new Point(3, 2);
    CardEffect card;

    @BeforeEach
    void init(){
        shipBoard = new ShipBoard();
        player = new Player("io",Color.RED);

        component = new ContainerComponent(ComponentType.STORAGE, 4);
        tile = new Tile(3, component );
        shipBoard.insertTile(coordinate, tile);
        shipBoard.addTokenToTile(coordinate, Token.YELLOW_GOODS);
        shipBoard.addTokenToTile(coordinate, Token.BLUE_GOODS);
        shipBoard.addTokenToTile(coordinate, Token.YELLOW_GOODS);
        shipBoard.addTokenToTile(coordinate, Token.GREEN_GOODS);
        card = new EffectLoseGoods("perdi", 2);

    }
    @Test
    void action() {
        card.action(player);
        assertEquals(0, shipBoard.getTotalNumberOfaToken(Token.GREEN_GOODS));

    }

    @Test
    void setPosition() {
        assertThrows(IllegalMethodOnThisCardException.class, ()->card.setPosition(2));
    }


}