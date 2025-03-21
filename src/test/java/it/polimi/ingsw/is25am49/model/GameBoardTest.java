package it.polimi.ingsw.is25am49.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.is25am49.model.enums.Color;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {
    GameBoard gameBoard;
    Player player1;
    Player player2;

    @BeforeEach
    void init(){
        gameBoard = new GameBoard();
        player1 = new Player("pippo", Color.RED,3);
        player2 = new Player("pluto", Color.BLUE,2);
        gameBoard.addPlayerToLeaderboard(5,player1);
        gameBoard.addPlayerToLeaderboard(2,player2);
        gameBoard.setCurrentPlayer(player1);
    }

    @Test
    void configurateFlight() {
    }

    @Test
    void checkStatus() {
    }

    @Test
    void setFlightMode() {
    }

    @Test
    void getSmallAdventureDeck() {
    }

    @Test
    void mergeDecks() {
    }

    @Test
    void getLeaderBoard() {
    }

    @Test
    void setCurrentCard() {
    }

    @Test
    void getCurrentCard() {
    }

    @Test
    void drawAdventureCard() {
    }

    @Test
    void addPlayerToLeaderboard() {
    }

    @Test
    void addDiscardedTile() {
    }

    @Test
    void drawTile() {
    }

    @Test
    void getLeader() {
    }

    @Test
    void calculateFinalCredits() {
    }

    @Test
    void calculate_final_score() {
    }

    @Test
    void minimumExposedComponents() {
    }

    @Test
    void getCurrentPlayer() {
    }

    @Test
    void nextPlayer() {
    }

    @Test
    void setCurrentPlayer() {
    }

    @Test
    void getNextCard() {
        gameBoard.getNextCard();
        assertEquals(player2,gameBoard.getCurrentPlayer());
    }

    @Test
    void updateLeaderboard() {
    }
}