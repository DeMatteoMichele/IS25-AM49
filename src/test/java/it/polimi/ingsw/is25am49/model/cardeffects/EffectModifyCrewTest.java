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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class EffectModifyCrewTest {
    Player player;
    ShipBoard shipBoard;
    Tile tile;
    Component component;
    Point coordinate = new Point(1, 0);
    CardEffect cardEffect;

    @BeforeEach
    void init(){
        shipBoard = new ShipBoard();
        player = new Player("io", Color.RED);

        component = new ContainerComponent(ComponentType.CABINE, 5);
        tile = new Tile(3, component );
        shipBoard.insertTile(coordinate, tile);
        shipBoard.addTokenToTile(coordinate, Token.ASTRONAUT);
        shipBoard.addTokenToTile(coordinate, Token.ASTRONAUT);
        shipBoard.addTokenToTile(coordinate, Token.ASTRONAUT);
        shipBoard.addTokenToTile(coordinate, Token.ASTRONAUT);
        cardEffect = new EffectModifyCrew("perdi astronauti", 3);

    }
    @Test
    void action() {
        cardEffect.action(player);
        assertEquals(0, shipBoard.getTotalNumberOfaToken(Token.ASTRONAUT));
    }

    @Test
    void setPosition() {
        assertThrows(IllegalMethodOnThisCardException.class,()->cardEffect.setPosition(2) );
    }
}