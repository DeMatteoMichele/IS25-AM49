package it.polimi.ingsw.is25am49.server.model;

import it.polimi.ingsw.is25am49.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.is25am49.server.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Orientation;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void setConnectionFlag() {
        Player player = new Player("TestUser", Color.BLUE, 1);
        player.setConnectionFlag(false);
        assertFalse(player.isConnected());

        player.setConnectionFlag(true);
        assertTrue(player.isConnected());
    }

    @Test
    void isConnected() {
    }

    @Test
    void getIdPlayer() {
    }

    @Test
    void shipBoardIsNotCompletedYet() {
    }

    @Test
    void isWatching() {
    }

    @Test
    void lockLastTile() {
    }

    @Test
    void getSmallAdventureDeck() {
    }

    @Test
    void scoreOfGoods() {
        Player player = new Player("TestUser", Color.BLUE, 1);
        player.setShipBoard(new ShipBoard(1, 1, Color.BLUE));
        ShipBoard shipBoard = player.getShipBoard();
        shipBoard.insertTile(new java.awt.Point(8, 7), new Tile(12,new ContainerComponent(ComponentType.SPECIAL_STORAGE,4)));

        shipBoard.addTokenToTile(new Point(8, 7),Token.RED_GOODS);
        shipBoard.addTokenToTile(new Point(8, 7), Token.YELLOW_GOODS);
        shipBoard.addTokenToTile(new Point(8, 7), Token.GREEN_GOODS);
        shipBoard.addTokenToTile(new Point(8, 7), Token.BLUE_GOODS);

            assertEquals(10, player.scoreOfGoods());
    }

    @Test
    void completeShipboard() {
        Player player = new Player("TestUser", Color.BLUE, 1);
        player.setShipBoard(new ShipBoard(1, 1, Color.BLUE));
        player.completeShipboard();
        assertTrue(player.getShipBoard().isCompleted());
    }

    @Test
    void setHandTile() {
    }

    @Test
    void getUsername() {
    }

    @Test
    void getPlayerNumber() {
    }

    @Test
    void getHandTile() {
    }

    @Test
    void addLostComponents() {
        Player player = new Player("TestUser", Color.BLUE, 1);
        player.addLostComponents(2);
        assertEquals(2, player.getLostComponents());

        player.addLostComponents(3);
        assertEquals(5, player.getLostComponents());
    }

    @Test
    void getShipBoard() {
    }

    @Test
    void getScore() {
    }

    @Test
    void addCredits() {
        Player player = new Player("TestUser", Color.BLUE, 1);
        player.addCredits(100);
        assertEquals(100, player.getCredits());

        player.addCredits(50);
        assertEquals(150, player.getCredits());
    }

    @Test
    void pickTile() {
        Player player = new Player("TestUser", Color.BLUE, 1);
        player.setShipBoard(new ShipBoard(1, 1, Color.BLUE));
        Point point = new Point(8, 7);

        Tile tile = new Tile();
        player.setHandTile(tile);
        player.placeTile(point);
        assertThrows(InvalidCoordinatesException.class,()->player.pickTile(point));
        assertFalse(player.getHandTile().isPresent());
        assertEquals(Optional.empty(), player.getHandTile());
    }

    @Test
    void placeTile() {
        Player player = new Player("TestUser", Color.BLUE, 1);
        player.setShipBoard(new ShipBoard(1, 1, Color.BLUE));
        Point point = new Point(8, 7);

        Tile tile = new Tile();
        player.setHandTile(tile);

        player.placeTile(point);
        assertDoesNotThrow(()->(player.getShipBoard().getTile(point)));
        assertEquals(tile, player.getShipBoard().getTile(point));
        assertTrue(player.getHandTile().isEmpty());
    }

    @Test
    void rotateTile() {
        Player player = new Player("TestUser", Color.BLUE, 1);
        Tile tile = new Tile();
        player.setHandTile(tile);

        Orientation initialOrientation = tile.getTileOrientation();

        player.rotateTile(true);
        assertNotEquals(initialOrientation, tile.getTileOrientation());

        player.rotateTile(false);
        assertEquals(initialOrientation, tile.getTileOrientation());
    }

    @Test
    void discardTile() {
        Player player = new Player("TestUser", Color.BLUE, 1);
        Tile tile = new Tile();
        player.setHandTile(tile);

        Optional<Tile> discardedTile = player.discardTile();
        assertTrue(discardedTile.isPresent());
        assertEquals(tile, discardedTile.get());
        assertTrue(player.getHandTile().isEmpty());
    }

    @Test
    void getLostComponents() {
    }

    @Test
    void getCredits() {
    }

    @Test
    void getColor() {
    }

    @Test
    void setDaysThisTurn() {
        Player player = new Player("TestUser", Color.BLUE, 1);
        player.setDaysThisTurn(2);
        assertEquals(2, player.getDaysThisTurn());
    }

    @Test
    void getDaysThisTurn() {
    }

    @Test
    void setShipBoard() {
    }

    @Test
    void setSmallDeck() {
    }

    @Test
    void clearSmallDeck() {
        Player player = new Player("TestUser", Color.BLUE, 1);
        AdventureCardDeck deck = new AdventureCardDeck();
        player.setSmallDeck(deck);

        assertNotNull(player.getSmallAdventureDeck());
        assertTrue(player.isWatching());

        player.clearSmallDeck();
        assertNull(player.getSmallAdventureDeck());
        assertFalse(player.isWatching());
    }

    @Test
    void shipBoardIsFullOfToken() {
    }
}