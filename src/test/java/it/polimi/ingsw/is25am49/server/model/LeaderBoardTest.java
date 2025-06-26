package it.polimi.ingsw.is25am49.server.model;

import it.polimi.ingsw.is25am49.server.model.LeaderBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.server.model.exceptions.NotEnoughPlayerException;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LeaderBoardTest {
    Player player1;
    Player player2;
    Player player3;
    Player player4;
    Player player5;
    LeaderBoard leaderBoard1;
    LeaderBoard leaderBoard2;
    @BeforeEach
    void init() {
        leaderBoard1 = new LeaderBoard(GameMode.LEVEL_1);
        leaderBoard2 = new LeaderBoard(GameMode.LEVEL_2);
        player1 = new Player("Davis", Color.RED, 1);
        player2 = new Player("Compa", Color.BLUE, 2);
        player3 = new Player("Confa", Color.YELLOW, 3);
        player4 = new Player("Michele", Color.GREEN, 4);
        player5 = new Player("Sean", Color.GREEN, 5);
    }
    @Test
    void add() {
        leaderBoard1.add(player1);
        leaderBoard1.add(player2);
        leaderBoard1.add(player3);
        leaderBoard1.add(player4);
        leaderBoard2.add(player1);
        leaderBoard2.add(player2);
        leaderBoard2.add(player3);
        leaderBoard2.add(player4);
        assertThrows(GameSettingsException.class, () -> leaderBoard1.add(player5));
        assertEquals(4, leaderBoard1.getDays(player1));
        assertEquals(2, leaderBoard1.getDays(player2));
        assertEquals(1, leaderBoard1.getDays(player3));
        assertEquals(0, leaderBoard1.getDays(player4));
        assertEquals(6, leaderBoard2.getDays(player1));
        assertEquals(3, leaderBoard2.getDays(player2));
        assertEquals(1, leaderBoard2.getDays(player3));
        assertEquals(0, leaderBoard2.getDays(player4));
    }
    @Test
    void modifyFlightDays() {
        leaderBoard1.add(player1);
        leaderBoard1.add(player2);
        leaderBoard1.add(player3);
        leaderBoard1.add(player4);
        leaderBoard1.modifyFlightDays(player4,6 );
        leaderBoard1.modifyFlightDays(player1,-5);
        leaderBoard1.modifyFlightDays(player2,-4);
        leaderBoard1.modifyFlightDays(player3,3);
        leaderBoard1.modifyFlightDays(player4,-2 );
        leaderBoard1.modifyFlightDays(player1,3);
        leaderBoard1.modifyFlightDays(player2,-4);
        leaderBoard1.modifyFlightDays(player3,1);
        leaderBoard2.add(player1);
        leaderBoard2.add(player2);
        leaderBoard2.add(player3);
        leaderBoard2.add(player4);
        leaderBoard2.modifyFlightDays(player4, 6);
        leaderBoard2.modifyFlightDays(player1, -5);
        leaderBoard2.modifyFlightDays(player2,-4);
        leaderBoard2.modifyFlightDays(player3,3);
        leaderBoard2.modifyFlightDays(player4,-2);
        leaderBoard2.modifyFlightDays(player1,3);
        leaderBoard2.modifyFlightDays(player2,-4);
        leaderBoard2.modifyFlightDays(player3,1);
        assertTrue(leaderBoard1.isSorted());
        assertTrue(leaderBoard2.isSorted());
        assertEquals(0, leaderBoard1.getDays(player1));
        assertEquals(-8, leaderBoard1.getDays(player2));
        assertEquals(5, leaderBoard1.getDays(player3));
        assertEquals(7, leaderBoard1.getDays(player4));
        assertEquals(2, leaderBoard2.getDays(player1));
        assertEquals(-7, leaderBoard2.getDays(player2));
        assertEquals(5, leaderBoard2.getDays(player3));
        assertEquals(7, leaderBoard2.getDays(player4));


    }

    @Test
    void remove() {
        leaderBoard1.add(player1);
        leaderBoard1.add(player2);
        leaderBoard1.add(player3);
        leaderBoard1.add(player4);
        leaderBoard1.remove(player1);
        assertTrue(leaderBoard1.isSorted());
        leaderBoard1.remove(player2);
        assertTrue(leaderBoard1.isSorted());
        leaderBoard1.remove(player3);
        assertTrue(leaderBoard1.isSorted());
        assertThrows(NotEnoughPlayerException.class,()->leaderBoard1.remove(player4));
        assertTrue(leaderBoard1.isEmpty());
        leaderBoard2.add(player1);
        leaderBoard2.add(player2);
        leaderBoard2.add(player3);
        leaderBoard2.add(player4);
        leaderBoard2.remove(player1);
        assertTrue(leaderBoard2.isSorted());
        leaderBoard2.remove(player2);
        assertTrue(leaderBoard2.isSorted());
        leaderBoard2.remove(player3);
        assertTrue(leaderBoard2.isSorted());
        assertThrows(NotEnoughPlayerException.class,()->leaderBoard2.remove(player4));
        assertTrue(leaderBoard2.isEmpty());
    }

    @Test
    void getNumberOfCells() {
        assertEquals(18, leaderBoard1.getNumberOfCells());
        assertEquals(24, leaderBoard2.getNumberOfCells());
    }

    @Test
    void getLeader() {
        leaderBoard1.add(player1);
        leaderBoard1.add(player2);
        leaderBoard1.add(player3);
        leaderBoard1.add(player4);
        assertEquals(player1, leaderBoard1.getLeader());
        leaderBoard1.modifyFlightDays(player1, -30);
        assertEquals(player2, leaderBoard1.getLeader());
        leaderBoard2.add(player1);
        leaderBoard2.add(player2);
        leaderBoard2.add(player3);
        leaderBoard2.add(player4);
        assertEquals(player1, leaderBoard2.getLeader());
        leaderBoard2.modifyFlightDays(player1, -30);
        assertEquals(player2, leaderBoard2.getLeader());
    }

    @Test
    void getPos() {
        leaderBoard1.add(player1);
        leaderBoard1.add(player2);
        leaderBoard1.add(player3);
        leaderBoard1.add(player4);
        leaderBoard1.modifyFlightDays(player4,6 );
        leaderBoard1.modifyFlightDays(player1,-5);
        leaderBoard1.modifyFlightDays(player2,-4);
        leaderBoard1.modifyFlightDays(player3,3);
        leaderBoard1.modifyFlightDays(player4,-2 );
        leaderBoard1.modifyFlightDays(player1,3);
        leaderBoard1.modifyFlightDays(player2,-4);
        leaderBoard1.modifyFlightDays(player3,1);
        assertEquals(1, leaderBoard1.getPos(player4));
        assertEquals(3, leaderBoard1.getPos(player1));
        assertEquals(2, leaderBoard1.getPos(player3));
        assertEquals(4, leaderBoard1.getPos(player2));

        leaderBoard2.add(player1);
        leaderBoard2.add(player2);
        leaderBoard2.add(player3);
        leaderBoard2.add(player4);
        leaderBoard2.modifyFlightDays(player4, 6);
        leaderBoard2.modifyFlightDays(player1, -5);
        leaderBoard2.modifyFlightDays(player2, -4);
        leaderBoard2.modifyFlightDays(player3, 3);
        leaderBoard2.modifyFlightDays(player4, -2);
        leaderBoard2.modifyFlightDays(player1, 3);
        leaderBoard2.modifyFlightDays(player2, -4);
        leaderBoard2.modifyFlightDays(player3, 1);
        assertEquals(1, leaderBoard2.getPos(player4));
        assertEquals(3, leaderBoard2.getPos(player1));
        assertEquals(2, leaderBoard2.getPos(player3));
        assertEquals(4, leaderBoard2.getPos(player2));

    }
    @Test
    void getPlayerInPosition() {
        leaderBoard1.add(player1);
        leaderBoard1.add(player2);
        leaderBoard1.add(player3);
        leaderBoard1.add(player4);
        assertEquals(player1, leaderBoard1.getPlayerInPosition(1));
        assertEquals(player2, leaderBoard1.getPlayerInPosition(2));
        assertEquals(player3, leaderBoard1.getPlayerInPosition(3));
        assertEquals(player4, leaderBoard1.getPlayerInPosition(4));

        leaderBoard2.add(player1);
        leaderBoard2.add(player2);
        leaderBoard2.add(player3);
        leaderBoard2.add(player4);
        assertEquals(player1, leaderBoard2.getPlayerInPosition(1));
        assertEquals(player2, leaderBoard2.getPlayerInPosition(2));
        assertEquals(player3, leaderBoard2.getPlayerInPosition(3));
        assertEquals(player4, leaderBoard2.getPlayerInPosition(4));
    }

    @Test
    void testGetNextPlayerConnectedStartingFromThisPosition() {

        leaderBoard1.add(player1);
        leaderBoard1.add(player2);
        leaderBoard1.add(player3);
        leaderBoard1.add(player4);

        leaderBoard1.disconnectPlayer(player2);

        assertEquals(player3, leaderBoard1.getPlayerConnectedStartingFromThisPosition(2));

        leaderBoard1.disconnectPlayer(player3);

        assertEquals(player4, leaderBoard1.getPlayerConnectedStartingFromThisPosition(2));

        assertDoesNotThrow(()->leaderBoard1.disconnectPlayer(player1));
        assertDoesNotThrow(()->leaderBoard1.disconnectPlayer(player4));
        assertThrows(IllegalSetOfNextPlayerException.class,
                () -> leaderBoard1.getPlayerConnectedStartingFromThisPosition(1));
    }

    @Test
    void testRefreshConnection() {
        leaderBoard1.add(player1);
        leaderBoard1.add(player2);
        leaderBoard1.add(player3);
        leaderBoard1.add(player4);

        leaderBoard1.disconnectPlayer(player2);
        assertTrue(leaderBoard1.isDisconnected(player2));

        leaderBoard1.refreshConnections();
        assertFalse(leaderBoard1.isDisconnected(player2));

        leaderBoard1.disconnectPlayer(player3);
        assertTrue(leaderBoard1.isDisconnected(player3));

        leaderBoard1.refreshConnections();
        assertFalse(leaderBoard1.isDisconnected(player3));
    }
}