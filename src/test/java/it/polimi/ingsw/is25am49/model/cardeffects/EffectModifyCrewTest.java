package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectModifyCrew;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisCardException;
import it.polimi.ingsw.is25am49.server.model.shipboard.Component;
import it.polimi.ingsw.is25am49.server.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EffectModifyCrewTest {
    Player player;
    ShipBoard shipBoard;
    Tile tile;
    Component component;
    Component component2;
    Tile tile2;
    Point coordinate = new Point(6, 7);
    Point coordinate2 = new Point(7, 8);
    CardEffect cardEffect;
    List<Pair<Point,Token>> coordinates;

    @BeforeEach
    void init(){
        player = new Player("io", Color.RED,2);
        shipBoard = new ShipBoard(1,2,Color.RED);
        coordinates = new ArrayList<>();
        coordinates.add(new Pair<>(coordinate,Token.ASTRONAUT));
        coordinates.add(new Pair<>(coordinate,Token.ASTRONAUT));
        coordinates.add(new Pair<>(coordinate,Token.ASTRONAUT));
        coordinates.add(new Pair<>(coordinate2,Token.BROWN_ALIENS));
        player.setShipBoard(shipBoard);
        component = new ContainerComponent(ComponentType.CABINE, 5);
        component2 = new ContainerComponent(ComponentType.BROWN_ALIEN_MODULE, 2);
        tile = new Tile(3, component );
        tile2 = new Tile(3, component2);
        shipBoard.insertTile(coordinate, tile);
        player.getShipBoard().insertTile(coordinate2, tile2);
        player.getShipBoard().addTokenToTile(coordinate, Token.ASTRONAUT);
        player.getShipBoard().addTokenToTile(coordinate, Token.ASTRONAUT);
        player.getShipBoard().addTokenToTile(coordinate, Token.ASTRONAUT);
        player.getShipBoard().addTokenToTile(coordinate, Token.ASTRONAUT);
        player.getShipBoard().addTokenToTile(coordinate2, Token.BROWN_ALIENS);
        player.getShipBoard().addTokenToTile(coordinate2, Token.BROWN_ALIENS);
        cardEffect = new EffectModifyCrew("perdi astronauti", 4);
        cardEffect.setListOfCoordinates(coordinates);

    }
    @Test
    void action() {
        cardEffect.action(player);
        assertEquals(2, player.getShipBoard().getPositionsOfaToken(Token.ASTRONAUT).size()+player.getShipBoard().getPositionsOfaToken(Token.BROWN_ALIENS).size());
    }

    @Test
    void setPosition() {
        assertThrows(IllegalMethodOnThisCardException.class,()->cardEffect.setPosition(2) );
    }
}