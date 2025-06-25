package it.polimi.ingsw.is25am49.server.model;

import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalTimerMoveException;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimerTest {

    @Test
    void getLifetime() {
        Timer timer = new Timer(GameMode.LEVEL_1, new GameSession());
        assertEquals(30, timer.getLifetime(), "Timer lifetime should be initialized to 30 seconds.");
    }

    @Test
    void getUsageCounter() {
        Timer timer = new Timer(GameMode.LEVEL_1, new GameSession());
        assertEquals(0, timer.getUsageCounter(), "Timer usageCounter should initialize to 0.");

        Player player = new Player("TestPlayer", Color.RED,5);
        timer.place(player);
        assertEquals(1, timer.getUsageCounter(), "Timer usageCounter should increment after place is called.");
    }

    @Test
    void isActive() {
        Timer timer = new Timer(GameMode.LEVEL_1, new GameSession());
        assertFalse(timer.isActive(), "Timer should not be active initially.");

        Player player = new Player("TestPlayer", Color.RED,5);
        timer.place(player);
        assertTrue(timer.isActive(), "Timer should be active after place is called.");
    }

    @Test
    void place() {
        Timer timer = new Timer(GameMode.LEVEL_1, new GameSession());
        Player incompletePlayer = new Player("TestPlayer",  Color.RED,5);
        Player completePlayer = new Player("TestPlayer",  Color.RED,5);

        timer.place(incompletePlayer);
        assertEquals(1, timer.getUsageCounter(), "UsageCounter should increment after a valid place call.");

        Timer finalTimer = timer;
        assertThrows(IllegalTimerMoveException.class, () -> finalTimer.place(incompletePlayer), "Should throw exception if attempting to place when already active.");

        timer = new Timer(GameMode.LEVEL_1, new GameSession());
        timer.place(completePlayer);
        assertEquals(1, timer.getUsageCounter(), "UsageCounter should handle completed shipBoard scenarios.");
    }
}