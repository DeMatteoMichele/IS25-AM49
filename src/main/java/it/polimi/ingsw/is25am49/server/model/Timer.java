package it.polimi.ingsw.is25am49.server.model;

import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalTimerMoveException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The Timer class represents a countdown timer used in the game.
 * It manages activation, usage limits, and expiration behavior.
 */
public class Timer {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final int lifetime;
    private int usageCounter;
    private boolean isActive;
    private final int level;
    private GameSession gameSession;
    public int getLifetime() {
        return lifetime;
    }
    /**
     * Gets the current usage count of the timer.
     * @return The number of times the timer has been used.
     */
    public int getUsageCounter() {
        return usageCounter;
    }
    /**
     * Checks if the timer is active.
     * @return True if the timer is active, false otherwise.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Constructs a Timer object with the specified game mode and game session.
     * @param level The game mode determining the timer's behavior.
     * @param gameSession The current game session.
     */
    public Timer(GameMode level, GameSession gameSession) {
        this.lifetime = 30;
        this.usageCounter = 0;
        isActive = false;
        if(level.equals(GameMode.LEVEL_1))
            this.level = 2;
        else
            this.level = 3;
        this.gameSession = gameSession;
    }

    /**
     * Places the timer for the specified player, activating it if conditions are met.
     * @param player The player attempting to place the timer.
     * @throws IllegalTimerMoveException If the timer cannot be placed due to invalid conditions.
     */
    public synchronized void place(Player player) {
        if(!isActive && usageCounter < level - 1) {
            usageCounter++;
            isActive = true;
            startTimer(lifetime);
        }
        else if(!isActive && usageCounter == level - 1 && player.getShipBoard().isCompleted()){
            usageCounter++;
            isActive = true;
            startFinalTimer(lifetime);
        }
        else {
            throw new IllegalTimerMoveException("Illegal timer move");
        }
    }

    /**
     * Starts a regular timer that deactivates after the specified duration.
     * @param seconds The duration of the timer in seconds.
     */
    private void startTimer(int seconds) {
        scheduler.schedule(() -> {
            isActive = false;
        }, seconds, TimeUnit.SECONDS);
    }

    /**
     * Starts the final timer that deactivates and triggers state updates after expiration.
     * @param seconds The duration of the timer in seconds.
     */
    private void startFinalTimer(int seconds) {
        scheduler.schedule(() -> {
            isActive = false;
            gameSession.getState().timerIsDone();
        }, seconds, TimeUnit.SECONDS);
    }
}
