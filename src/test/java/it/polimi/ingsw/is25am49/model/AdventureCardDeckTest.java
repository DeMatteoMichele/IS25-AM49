package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.server.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.server.model.adventurecards.EpidemyCard;
import it.polimi.ingsw.is25am49.server.model.adventurecards.Planet;
import it.polimi.ingsw.is25am49.server.model.adventurecards.PlanetCard;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectCannonata;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.exceptions.FinishDeckExceptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdventureCardDeckTest {
    private GameBoard gameBoard;
    private AdventureCardDeck deck2;
    private AdventureCard card;
    private AdventureCard card2;
    private List<CardEffect> effects = new ArrayList<>();
    private EffectCannonata cannonata;
    private AdventureCard cardDrawn;
    private List<Planet> planets = new ArrayList<>();
    Player player1;

    @BeforeEach
    void init(){
        player1 = new Player("pippo", Color.RED, 1);
        gameBoard = new GameBoard();
        gameBoard.setLeaderBoard(GameMode.LEVEL_1);
        gameBoard.addPlayerToLeaderboard(player1);
        deck2 = new AdventureCardDeck();
        effects.add(cannonata);
        card = new PlanetCard(gameBoard,"pianeti",3,1,planets);
        card2 = new EpidemyCard(gameBoard,"epidemia", 2,3,effects);
        gameBoard.setMainAdventureDeck(deck2);


    }


    @Test
    void drawDeckIsEmpty() {
        assertThrows(FinishDeckExceptions.class, ()->deck2.draw()); // per testare l'eccezione, non faccio nessuna add

    }

    @Test
    void draw(){
        deck2.add(card);
        deck2.add(card2);
        //il resto per testare se rimuove la prima adventure card inserita
        gameBoard.drawAdventureCard(gameBoard.getLeaderBoard().getLeader());
        cardDrawn = gameBoard.getCurrentCard();
        assertEquals("pianeti",cardDrawn.getDescription() );
        assertEquals(card.getLevel(),cardDrawn.getLevel());
        gameBoard.getCurrentCard().setFinished(true);
        gameBoard.drawAdventureCard(gameBoard.getLeaderBoard().getLeader());
        cardDrawn = gameBoard.getCurrentCard();
        assertEquals(card2.getDescription(),cardDrawn.getDescription());
    }



    @Test
    void generateDeck() {

    }

    @Test
    void shuffle() {
    }

    @Test
    void showCards() {
    }
}