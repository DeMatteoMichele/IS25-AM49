package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisCardException;
import it.polimi.ingsw.is25am49.model.shipboard.Component;
import it.polimi.ingsw.is25am49.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.model.shipboard.Tile;
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
    Point coordinate = new Point(1, 0);
    Point coordinate2 = new Point(0, 1);
    CardEffect cardEffect;
    ArrayList<Pair<Point,Token>> coordinates;

    @BeforeEach
    void init(){
        shipBoard = new ShipBoard();
        player = new Player("io", Color.RED,2);
        coordinates = new ArrayList<>();
        coordinates.add(new Pair<>(coordinate,Token.ASTRONAUT));
        coordinates.add(new Pair<>(coordinate,Token.ASTRONAUT));
        coordinates.add(new Pair<>(coordinate,Token.ASTRONAUT));
        coordinates.add(new Pair<>(coordinate2,Token.BROWN_ALIENS));

        component = new ContainerComponent(ComponentType.CABINE, 5);
        component2 = new ContainerComponent(ComponentType.BROWN_ALIEN_MODULE, 2);
        tile = new Tile(3, component );
        tile2 = new Tile(3, component2);
        player.getShipBoard().insertTile(coordinate, tile);
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