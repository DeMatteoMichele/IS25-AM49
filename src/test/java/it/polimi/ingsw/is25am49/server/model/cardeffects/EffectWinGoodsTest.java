package it.polimi.ingsw.is25am49.server.model.cardeffects;

import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.shipboard.Component;
import it.polimi.ingsw.is25am49.server.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class EffectWinGoodsTest {
    Player player;
    ShipBoard ship;
    Tile tile1,tile2;
    Component component1,component2;
    CardEffect cardEffect;
    List<Pair<Point, Token>> coordinates;

    @BeforeEach
    void init(){
        player = new Player("Marco", Color.RED,2);
        ship = new ShipBoard(1,2);
        player.setShipBoard(ship);

        coordinates = new ArrayList<>();
        coordinates.add(new Pair<>(new Point(7, 8), Token.YELLOW_GOODS));
        coordinates.add(new Pair<>(new Point(6, 6), Token.YELLOW_GOODS));
        coordinates.add(new Pair<>(new Point(7, 8), Token.GREEN_GOODS));

        component1 = new ContainerComponent(ComponentType.STORAGE, 4);
        component2 = new ContainerComponent(ComponentType.SPECIAL_STORAGE, 4);
        tile1 = new Tile(1, component1);
        tile2 = new Tile(2, component2);
        player.getShipBoard().insertTile(new Point(7, 8), tile1);
        player.getShipBoard().insertTile(new Point(6, 6), tile2);
        player.getShipBoard().addTokenToTile(new Point(7, 8), Token.YELLOW_GOODS);
        player.getShipBoard().addTokenToTile(new Point(7, 8), Token.YELLOW_GOODS);
        player.getShipBoard().addTokenToTile(new Point(7, 8), Token.BLUE_GOODS);
        player.getShipBoard().addTokenToTile(new Point(7, 8), Token.GREEN_GOODS);
        player.getShipBoard().addTokenToTile(new Point(6, 6), Token.RED_GOODS);
        player.getShipBoard().addTokenToTile(new Point(6, 6), Token.YELLOW_GOODS);
        player.getShipBoard().addTokenToTile(new Point(6, 6), Token.GREEN_GOODS);

        cardEffect = new EffectWinGoods("EffectWinGoods", 0, 2, 1, 0);
        cardEffect.setListOfCoordinates(coordinates);
    }

    @Test
    void action() {
        cardEffect.action(player);
        assertEquals(5, player.getShipBoard().getTotalNumberOfaToken(Token.YELLOW_GOODS));
        assertEquals(0, player.getShipBoard().getTotalNumberOfaToken(Token.BLUE_GOODS));
        assertEquals(2, player.getShipBoard().getTotalNumberOfaToken(Token.GREEN_GOODS));

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
}