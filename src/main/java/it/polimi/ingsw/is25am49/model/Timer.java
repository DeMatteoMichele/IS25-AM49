package it.polimi.ingsw.is25am49.model;

public class Timer {
    private int lifetime;
    private int usageCounter;
    private boolean isActive;

    public int getLifetime(){
        return lifetime;
    }

    public int getUsageCounter(){
        return usageCounter;
    }

    public boolean isActive(){
        return isActive;
    }

    public Timer(int lifetime, int usageCounter, boolean isActive) {
        this.lifetime = lifetime;
        this.usageCounter = 0;
        this.isActive = false;
    }

    public void place(){
        usageCounter++;
        isActive=true;

    }



}
