package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
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
    protected CardEffect currentEffect;
    protected int minimumFirePower;
    protected int lostAstronauts;

    public AdventureCard(String description, int daysLost, int level, int creditsEarned) {
        this.description = description;
        this.daysLost = daysLost;
        this.isFinished = false;
        this.creditsEarned = creditsEarned;
        this.level = level;
        this.listEffects = new ArrayList<>();
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
    public abstract AdventureStatesTypes checkRequirements(Player player);

    public void drawCard() {
        System.out.println("Hai pescato la carta: " + getClass().getSimpleName());
    }

    public void landingOnStationOrShip(){
        throw new IllegalMethodOnThisCardException("This card cannot be landed on");
    }

    public void landingOnPlanet(){
        throw new IllegalMethodOnThisCardException("This card cannot be landed on");
    }

    public CardEffect getCurrentEffect(){
        return currentEffect;
    }




    public void rollDices(){
            Random random = new Random();
            int firstDice = random.nextInt(6) + 1;
            int secondDice = random.nextInt(6) + 1;
            dicesResult = firstDice + secondDice;
            System.out.println("The dices have been rolled: " + firstDice + " and " + secondDice + ". Result: " + dicesResult);
            currentEffect.setPosition(dicesResult);
        }




    //method return true if we must change player
    //false if we must remain in the same player
    public abstract AdventureStatesTypes applyEffect(Player player);

    public int getLevel() {
        return level;
    }

}
