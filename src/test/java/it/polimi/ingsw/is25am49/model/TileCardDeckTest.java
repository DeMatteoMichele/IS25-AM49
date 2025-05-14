package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.server.model.*;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class TileCardDeckTest {
    TileCardDeck deck ;
    Random rand;
    Player player1;
    Player player2;
    Player player3;
    Player player4;
    ShipBoard ship1;
    GameBoard gameBoard;
    Game game;
    GameSession gameSession;




    @BeforeEach
    void init(){


        gameSession = new GameSession();



        player1 = new Player("Maicols", Color.RED, 1);
        player2 = new Player("Device", Color.BLUE, 2);
        player3 = new Player("Compus", Color.GREEN, 3);
        player4 = new Player("Cefa", Color.YELLOW, 4);
        ship1 = new ShipBoard(1,1);
        player1.setShipBoard(ship1);
        game = new Game(player1, gameSession, GameMode.DEFAULT,999,400);
        deck = game.getGameboard().getTilesDeck();
    }
    @Test
    void DrawAndAddDiscardTile() {

    }

    @Test
    void checkConstructor(){

        assertNotNull(deck);
        deck.stream().forEach(System.out::println);
        assertEquals(deck.getNumberOfTile(), deck.stream().count());
    }

    @Test
    void testPrintEntireDeck(){
        deck.stream().forEach((key) -> System.out.println(key.getValue()));
        //System.out.println(deck.toString());

    }
    @Test
    void testToString(){
        System.out.println(deck.toString());
        game.userDrawTile(player1, 95);
        //TODO se chiamo questo metodo lancia eccezzione perche non può pescare una carta che ha già pescato un altro
//        game.userDrawTile(player2, 95);
        player1.getHandTile().ifPresent(System.out::println);
        System.out.println(deck.toString());
        game.userDrawTile(player1,34);
        game.userDrawTile(player2,95);
        player1.getHandTile().ifPresent(System.out::println);

        System.out.println(deck.toString());
        game.userDrawTile(player1, 78);
        game.userDrawTile(player2, 98);
        game.userDrawTile(player3, 11);
        System.out.println(deck.toString());
        player1.getHandTile().ifPresent(tile -> deck.addDiscardTile(tile, player1));
        System.out.println(deck.toString());
        game.userDrawTile(player1, 23);
        game.userDrawTile(player2, 13);
        game.userDrawTile(player3, 67);
        System.out.println(deck.toString());
        System.out.println(deck.getTileById(95).toString());
        System.out.println(deck.getTileById(139).toString());
    }
}