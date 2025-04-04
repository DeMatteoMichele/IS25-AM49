package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectModifyEnergy;
import it.polimi.ingsw.is25am49.server.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisCardException;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.shipboard.Component;
import it.polimi.ingsw.is25am49.server.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;
import java.util.ArrayList;

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
    ArrayList<Pair<Point,Token>> coordinates;

    @BeforeEach
    void init(){
        shipBoard = new ShipBoard();
        player = new Player("io",Color.RED,2);
        coordinates = new ArrayList<>();
        coordinates.add(new Pair<>(coordinate,Token.BATTERY_CELLS));
        coordinates.add(new Pair<>(coordinate,Token.BATTERY_CELLS));
        coordinates.add(new Pair<>(coordinate,Token.BATTERY_CELLS));
        coordinates.add(new Pair<>(coordinate2,Token.BATTERY_CELLS));
        coordinates.add(new Pair<>(coordinate2,Token.BATTERY_CELLS));


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
        cardEffect.setListOfCoordinates(coordinates);
    }

    @Test
    void action() {
        //when(player.getShipBoard().getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(1);
        cardEffect.action(player);
        assertEquals(2, player.getShipBoard().getPositionsOfaToken(Token.BATTERY_CELLS).size());
    }

    @Test
    void setPosition() {

        assertThrows(IllegalMethodOnThisCardException.class, () -> cardEffect.setPosition(2));
    }
}