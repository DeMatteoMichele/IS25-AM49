package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LeaderBoardTest {
    @BeforeEach
    void init() {
        Player player1 = new Player("Davis", Color.RED, 1);
        Player player2 = new Player("Compa", Color.BLUE, 2);
        Player player3 = new Player("Confa", Color.YELLOW, 3);
        Player player4 = new Player("Michele", Color.GREEN, 4);
        LeaderBoard leaderBoard = new LeaderBoard(GameMode.LEVEL_1);
        GameBoard gameBoard = new GameBoard();

    }
    @Test
    void add() {
    }

    @Test
    void getDays() {
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
    }

    @Test
    void isEmpty() {
    }
}