package it.polimi.ingsw.is25am49.server.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TileCardDeckTest {
    TileCardDeck deck;
    Random rand = new Random();

    @BeforeEach
    void init(){

    }
    @Test
    void addDiscardTile() {
    }

    @Test
    void draw() {
    }

    @Test
    void checkConstructor(){
        deck = new TileCardDeck();
        assertNotNull(deck);
        deck.stream().forEach(System.out::println);
        System.out.println(deck.stream().count());
    }
}