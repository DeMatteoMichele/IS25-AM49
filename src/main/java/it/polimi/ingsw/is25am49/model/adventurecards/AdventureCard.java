package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisCardException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AdventureCard {
    protected String description;
    protected int daysLost;
    protected boolean isFinished;
    protected int creditsEarned;
    protected final List<CardEffect> listEffects;
    protected int level;
    protected int dicesResult;

    public AdventureCard(String description, int daysLost, int level, int creditsEarned, List<CardEffect> listEffects) {
        this.description = description;
        this.daysLost = daysLost;
        this.isFinished = false;
        this.creditsEarned = creditsEarned;
        this.level = level;
        this.listEffects = new ArrayList<>(listEffects);
    }

    public String getDescription() {
        return description;
    }
//
//    public void setDaysLost(int daysLost) {
//        this.daysLost = daysLost;
//    }
//
//    public void setFlagDecision(int flagDecision) {
//        this.flagDecision = flagDecision;
//    }


    // methods return an int based on what state we must go to
    // 0 = check requirements
    // 1 = decision
    // 2 = effect
    public abstract int checkRequirements(Player player);

    public void drawCard() {
        System.out.println("Hai pescato la carta: " + getClass().getSimpleName());
    }

    public void landingOnStationOrShip(){
        throw new IllegalMethodOnThisCardException("This card cannot be landed on");
    }

    public void landingOnPlanet(){
        throw new IllegalMethodOnThisCardException("This card cannot be landed on");
    }




    public void rollDices(){
            Random random = new Random();
            int firstDice = random.nextInt(6) + 1;
            int secondDice = random.nextInt(6) + 1;
            dicesResult = firstDice + secondDice;
            System.out.println("You have rolled the dices: " + firstDice + " and " + secondDice + ". Result: " + dicesResult);

        }




    //method return true if we must change player
    //false if we must remain in the same player
    public abstract boolean applyEffect(Player player);

    public int getLevel() {
        return level;
    }

}
