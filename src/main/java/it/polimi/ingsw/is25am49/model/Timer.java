package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalTimerMoveException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Timer {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final int lifetime;
    private int usageCounter;
    private boolean isActive;
    private final int level;
    public int getLifetime() {
        return lifetime;
    }

    public int getUsageCounter() {
        return usageCounter;
    }

    public boolean isActive() {
        return isActive;
    }

    public Timer(GameMode level) {
        this.lifetime = 30;
        this.usageCounter = 0;
        this.isActive = false;
        if(level.equals(GameMode.LEVEL_1))
            this.level = 2;
        else
            this.level = 3;
    }
    public void checkTimer(){

    }
    public void place() {
        if(!isActive && usageCounter < level) {
            usageCounter++;
            isActive = true;
            startTimer(lifetime);
        }
        else {
            throw new IllegalTimerMoveException("Illegal timer move");
        }
    }

    public void startTimer(int seconds) {
        scheduler.schedule(() -> {
            isActive = false;
            notifyAll();
        }, seconds, TimeUnit.SECONDS);
    }
}
