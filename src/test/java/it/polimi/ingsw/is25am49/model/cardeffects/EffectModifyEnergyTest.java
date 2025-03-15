package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisCardException;
import it.polimi.ingsw.is25am49.model.shipboard.*;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.shipboard.Component;
import it.polimi.ingsw.is25am49.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.model.shipboard.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class EffectModifyEnergyTest {
    Player player;
    ShipBoard shipBoard;
    Tile tile;
    Tile tile2;
    Component component;
    Point coordinate = new Point(0, 0);
    Point coordinate2 = new Point(1, 1);
    CardEffect cardEffect;
    Component component2;

    @BeforeEach
    void init(){
        shipBoard = new ShipBoard();
        player = new Player("io",Color.RED);

        component = new ContainerComponent(ComponentType.BATTERY, 4);
        component2 = new ContainerComponent(ComponentType.BATTERY,4);
        tile = new Tile(1, component );
        tile2 = new Tile(2,component2);
        player.getShipBoard().insertTile(coordinate, tile);
        player.getShipBoard().insertTile(coordinate2, tile2);
        player.getShipBoard().addTokenToTile(coordinate2,Token.BATTERY_CELLS);
        player.getShipBoard().addTokenToTile(coordinate2,Token.BATTERY_CELLS);
        player.getShipBoard().addTokenToTile(coordinate2,Token.BATTERY_CELLS);
        player.getShipBoard().addTokenToTile(coordinate,Token.BATTERY_CELLS);
        player.getShipBoard().addTokenToTile(coordinate,Token.BATTERY_CELLS);
        player.getShipBoard().addTokenToTile(coordinate,Token.BATTERY_CELLS);
        player.getShipBoard().addTokenToTile(coordinate,Token.BATTERY_CELLS);

        cardEffect = new EffectModifyEnergy("perdi energia", 5);
    }

    @Test
    void action() {
        //when(player.getShipBoard().getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(1);
        cardEffect.action(player);
        assertEquals(2, player.getShipBoard().getTotalNumberOfaToken(Token.BATTERY_CELLS));
    }

    @Test
    void setPosition() {
        assertNotNull(shipBoard.getTile(coordinate), "La Tile non è stata registrata correttamente!");
        //assertThrows(IllegalMethodOnThisCardException.class, () -> cardEffect.setPosition(2));
    }
}