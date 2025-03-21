package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbandonedStationCardTest {
    GameBoard gameBoard;
    Player player1;
    Player player2;
    AdventureCardDeck deck;
    ShipBoard shipboard1;
    ShipBoard shipboard2;


    @BeforeEach
    void init(){
        //inizializzo la gameboard e il player,
        gameBoard = new GameBoard();
        player1 = new Player("Marco", Color.RED,2);
        player2 = new Player("Juan", Color.BLUE,3);
        //aggiungo i player alla gameboard e i loro rispettivi giorni di viaggio
        gameBoard.addPlayerToLeaderboard(10,player1);
        gameBoard.addPlayerToLeaderboard(9,player2);
        //ordino la leaderboard
        gameBoard.sortLeaderBoard();
        //inizializzo il deck e ci aggiungo la carta che devo testare
        deck = new AdventureCardDeck();
        deck.add(new AbandonedStationCard(gameBoard,"Abandoned Station",3,1,0,2));
        deck.add(new AbandonedShipCard(gameBoard,"abandoned ship",2,1,100,2));

        gameBoard.setMainAdventureDeck(deck);


    }

    @Test
    void drawCard() {
        gameBoard.drawAdventureCard();
        assertEquals(1,deck.size());
        assertEquals("Abandoned Station",gameBoard.getCurrentCard().getDescription());
        assertEquals(player1,gameBoard.getLeader());
        assertEquals(gameBoard.getLeader(),gameBoard.getCurrentPlayer());
    }

    @Test
    void checkRequirements() {
    }

    @Test
    void landingOnStationOrShip() {
    }

    @Test
    void finishDecision() {
    }

    @Test
    void applyEffect() {
    }
}