package it.polimi.ingsw.is25am49.client.model;

import it.polimi.ingsw.is25am49.client.UserInterface;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalTimerMoveException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Timer {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final int lifetime;
    private int usageCounter;
    private boolean isActive;
    private final int level;
    private GameSession gameSession;
    private UserInterface ui;
    public int getLifetime() {
        return lifetime;
    }

    public int getUsageCounter() {
        return usageCounter;
    }

    public boolean isActive() {
        return isActive;
    }

    public Timer(GameMode level, GameSession gameSession, UserInterface ui) {
        this.lifetime = 30;
        this.usageCounter = 0;
        isActive = false;
        if(level.equals(GameMode.LEVEL_1))
            this.level = 2;
        else
            this.level = 3;
        this.gameSession = gameSession;
        this.ui = ui;
    }
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

    public void placeNoTime(Player player) {
        if(usageCounter < level) {
            usageCounter++;
        } else {
            throw new IllegalTimerMoveException("Illegal timer move");
        }
    }
    private void startTimer(int seconds) {
        scheduler.schedule(() -> {
            isActive = false;
            ui.printUpdateMessage("Timer[" +usageCounter+"] has expired! ");
        }, seconds, TimeUnit.SECONDS);
    }
    private void startFinalTimer(int seconds) {
        scheduler.schedule(() -> {
            isActive = false;
            gameSession.getState().timerIsDone();
        }, seconds, TimeUnit.SECONDS);
    }

    @Override
    public String toString() {
        return "Timer[" +usageCounter+"] - " + "lifetime=" + lifetime + " seconds";
    }
}
