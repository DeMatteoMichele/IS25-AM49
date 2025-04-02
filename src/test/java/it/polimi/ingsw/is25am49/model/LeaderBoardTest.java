package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LeaderBoardTest {
    Player player1;
    Player player2;
    Player player3;
    Player player4;
    LeaderBoard leaderBoard;
    @BeforeEach
    void init() {
        leaderBoard = new LeaderBoard(GameMode.LEVEL_1);
        player1 = new Player("Davis", Color.RED, 1);
        player2 = new Player("Compa", Color.BLUE, 2);
        player3 = new Player("Confa", Color.YELLOW, 3);
        player4 = new Player("Michele", Color.GREEN, 4);
    }
    @Test
    void add() {

    }

    @Test
    void getDays() {
        leaderBoard.add(player1);
        leaderBoard.add(player2);
        leaderBoard.add(player3);
        leaderBoard.add(player4);
        assertEquals(4,leaderBoard.getDays(player1));
        assertEquals(2,leaderBoard.getDays(player2));
        assertEquals(1,leaderBoard.getDays(player3));
        assertEquals(0,leaderBoard.getDays(player4));
    }

    @Test
    void modifyFlightDays() {
    }

    @Test
    void remove() {
    }

    @Test
    void getNumberOfCells() {
    }

    @Test
    void getFirst() {
    }

    @Test
    void getPos() {
    }

    @Test
    void stream() {
    }

    @Test
    void size() {
    }

    @Test
    void getPlayerInPosition() {
        leaderBoard.add(player1);
        leaderBoard.add(player2);
        leaderBoard.add(player3);
        leaderBoard.add(player4);
        assertEquals(player1, leaderBoard.getPlayerInPosition(1));
        assertEquals(player2, leaderBoard.getPlayerInPosition(2));
        assertEquals(player3, leaderBoard.getPlayerInPosition(3));
        assertEquals(player4, leaderBoard.getPlayerInPosition(4));
    }

    @Test
    void isEmpty() {
    }
}