package it.polimi.ingsw.is25am49.model.adventurecards;

import org.mockito.Mock;
import it.polimi.ingsw.is25am49.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.shipboard.Component;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.model.shipboard.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class SmugglerCardTest {

    GameBoard gameBoard;
    AdventureCardDeck adventureDeck;
    Player player1;
    Player player2;
    ShipBoard shipBoard1;
    List<CardEffect> listEffects = new ArrayList<>();
    AdventureCard card1;
    Component shipBoardComponent;
    Tile tile;


    @BeforeEach
    void init(){
        // Initialize the game board and player1
        gameBoard = new GameBoard();
        player1 = new Player("TestPlayer", Color.RED, 1);
        player2 = new Player("TestPlayer2", Color.BLUE, 2);
        gameBoard.addPlayerToLeaderboard(0, player1);
        gameBoard.addPlayerToLeaderboard(3, player2);
        gameBoard.sortLeaderBoard();
        adventureDeck = new AdventureCardDeck();
        card1=new SmugglerCard(gameBoard, "SmugglerCardTest", 2, 1, 100, 3, 0, listEffects);
        adventureDeck.add(card1);
        gameBoard.setMainAdventureDeck(adventureDeck);
    }

    @Test
    void checkRequirements() {

    }

    @Test
    void applyEffect() {
    }

    @Test
    void drawCard() {
        gameBoard.drawAdventureCard();
        assertEquals(0, adventureDeck.size());
        assertEquals(gameBoard.getLeaderBoard().getFirst().getValue(),player2);
        assertEquals(gameBoard.getLeader(), gameBoard.getCurrentPlayer());
        //assertEquals(AdventureStatesTypes., gameBoard.getCurrentCard().getCurrentState());
    }

    @Test
    void testCheckRequirements() {
    }

    @Test
    void finishDecision() {
    }

    @Test
    void testFinishDecision() {
    }

    @Test
    void testApplyEffect() {
    }
}