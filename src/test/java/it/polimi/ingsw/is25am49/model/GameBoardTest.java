package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisCardException;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalSetOfNextPlayerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.is25am49.model.enums.Color;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {
    GameBoard gameBoard;
    Player player1;
    Player player2;
    Player player3;
    Player player4;
    Player player5;
    Player player6;
    Player player7;

    @BeforeEach
    void init(){
        gameBoard = new GameBoard();
        player1 = new Player("pippo", Color.RED,3);
        player2 = new Player("pluto", Color.BLUE,2);
        player3 = new Player("marco", Color.GREEN,1);
        player4 = new Player("ci", Color.BLUE,12);
        player5 = new Player("rojo", Color.RED,8);
        player6 = new Player("verde", Color.BLUE,6);
        player7 = new Player("azul", Color.BLUE,9);
        gameBoard.addPlayerToLeaderboard(5,player1);
        gameBoard.addPlayerToLeaderboard(9,player2);
        gameBoard.addPlayerToLeaderboard(1,player3);
        gameBoard.addPlayerToLeaderboard(11,player4);
        gameBoard.addPlayerToLeaderboard(3,player5);
        gameBoard.addPlayerToLeaderboard(10,player6);
        gameBoard.addPlayerToLeaderboard(7,player7);
        gameBoard.sortLeaderBoard();
        gameBoard.setCurrentPlayer(gameBoard.getLeader());
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
    void sortLeaderBoard() {
        assertEquals(player4,gameBoard.getLeaderBoard().get(0).getValue());
        assertEquals(player6,gameBoard.getLeaderBoard().get(1).getValue());
        assertEquals(player2,gameBoard.getLeaderBoard().get(2).getValue());
        assertEquals(player7,gameBoard.getLeaderBoard().get(3).getValue());
        assertEquals(player1,gameBoard.getLeaderBoard().get(4).getValue());
        assertEquals(player5,gameBoard.getLeaderBoard().get(5).getValue());
        assertEquals(player3,gameBoard.getLeaderBoard().get(6).getValue());
    }

    @Test
    void getCurrentPlayer() {
        assertEquals(player2.getColor(),gameBoard.getCurrentPlayer().getColor());
    }

    @Test
    void nextPlayer() {
        gameBoard.nextPlayer();
        assertEquals(player1.getUsername(),gameBoard.getCurrentPlayer().getUsername());
        gameBoard.nextPlayer();
        assertEquals(player3.getColor(), gameBoard.getCurrentPlayer().getColor());
    }

    @Test
    void setCurrentPlayer() {
        gameBoard.setCurrentPlayer(gameBoard.getLeader());
        assertEquals(player2.getUsername(),gameBoard.getCurrentPlayer().getUsername());
    }

    @Test
    void getNextCard() {

    }

    @Test
    void updateLeaderboard() {
    }
}