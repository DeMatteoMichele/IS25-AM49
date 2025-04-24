package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.TileCardDeck;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TileCardDeckTest {
    TileCardDeck deck;
    Random rand;
    Player player1;
    Player player2;
    Player player3;
    Player player4;


    @BeforeEach
    void init(){
        rand = new Random();
        deck = new TileCardDeck();
        player1 = new Player("Maicols", Color.RED, 1);
        player2 = new Player("Device", Color.BLUE, 2);
        player3 = new Player("Compus", Color.GREEN, 3);
        player4 = new Player("Cefa", Color.YELLOW, 4);
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
}