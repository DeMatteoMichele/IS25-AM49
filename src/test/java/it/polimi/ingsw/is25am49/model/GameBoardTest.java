package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.is25am49.server.model.enums.Color;

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
    ShipBoard ship1, ship2, ship3, ship4;
    Tile tile1, tile2, tile3, tile4;
    Component component1, component2, component3, component4;

    @BeforeEach
    void init(){
        gameBoard = new GameBoard();
        gameBoard.setLeaderBoard(GameMode.LEVEL_1);
        player1 = new Player("pippo", Color.RED,3);
        player2 = new Player("pluto", Color.BLUE,2);
        player3 = new Player("marco", Color.GREEN,1);
        player4 = new Player("ci", Color.BLUE,12);
        player5 = new Player("rojo", Color.RED,8);
        player6 = new Player("verde", Color.BLUE,6);
        player7 = new Player("azul", Color.BLUE,9);
        gameBoard.addPlayerToLeaderboard(player1);
        gameBoard.addPlayerToLeaderboard(player2);
        gameBoard.addPlayerToLeaderboard(player3);
        gameBoard.addPlayerToLeaderboard(player4);
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
        gameBoard.calculateFinalCredits();
        assertEquals(6,player1.getCredits());
        assertEquals(5,player2.getCredits());
        assertEquals(3,player4.getCredits());
    }

    @Test
    void calculate_final_score() {
    }

    @Test
    void minimumExposedComponents() {
    }

    @Test
    void getCurrentPlayer() {
        assertEquals(player1.getColor(),gameBoard.getCurrentPlayer().getColor());
    }

    @Test
    void nextPlayer() {
        gameBoard.nextPlayer();
        assertEquals(player2.getUsername(),gameBoard.getCurrentPlayer().getUsername());
        gameBoard.nextPlayer();
        assertEquals(player3.getColor(), gameBoard.getCurrentPlayer().getColor());
    }

    @Test
    void setCurrentPlayer() {
        gameBoard.setCurrentPlayer(gameBoard.getLeader());
        assertEquals(player1.getUsername(),gameBoard.getCurrentPlayer().getUsername());
    }

    @Test
    void getNextCard() {

    }

    @Test
    void updateLeaderboard() {
        assertEquals(4,gameBoard.getLeaderBoard().getDays(player1));
        assertEquals(2,gameBoard.getLeaderBoard().getDays(player2));
        assertEquals(1,gameBoard.getLeaderBoard().getDays(player3));
        assertEquals(0,gameBoard.getLeaderBoard().getDays(player4));
        player1.setDaysThisTurn(-1);
        player2.setDaysThisTurn(-2);
        player3.setDaysThisTurn(+1);
        player4.setDaysThisTurn(0);
        gameBoard.updateLeaderboard();
        assertEquals(player3.getUsername(), gameBoard.getLeaderBoard().getPlayerInPosition(1).getUsername());
        assertEquals(player1.getUsername(),gameBoard.getLeaderBoard().getPlayerInPosition(2).getUsername());
        assertEquals(player4.getUsername(),gameBoard.getLeaderBoard().getPlayerInPosition(3).getUsername());
        assertEquals(0,gameBoard.getLeaderBoard().getDays(player4));
        assertEquals(3,gameBoard.getLeaderBoard().getDays(player3));
        assertEquals(-1,gameBoard.getLeaderBoard().getDays(player2));
        assertEquals(2,gameBoard.getLeaderBoard().getDays(player1));
        assertEquals(0,player1.getDaysThisTurn());
        assertEquals(0,player2.getDaysThisTurn());
        assertEquals(0,player3.getDaysThisTurn());
        assertEquals(0,player4.getDaysThisTurn());
    }

    @Test
    void checkIfIsLapped(){
        player1.setDaysThisTurn(15);
        player4.setDaysThisTurn(1);
        player2.setDaysThisTurn(5);
        player3.setDaysThisTurn(5);
        assertEquals(player1.getUsername(),gameBoard.getLeaderBoard().getPlayerInPosition(1).getUsername());

    }
}