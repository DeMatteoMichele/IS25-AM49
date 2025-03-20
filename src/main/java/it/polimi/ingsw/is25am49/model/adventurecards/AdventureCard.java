package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;

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
    protected AdventureStatesTypes currentState;
    protected GameBoard gameBoard;

    public AdventureCard(GameBoard gameBoard, String description, int daysLost, int level, int creditsEarned) {
        this.gameBoard = gameBoard;
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

    public abstract void checkRequirements();

    public void drawCard() {
        currentState= AdventureStatesTypes.DRAW;
        System.out.println("Hai pescato la carta: " + getClass().getSimpleName());
    }

    public void landingOnStationOrShip() throws IllegalMethodOnThisStateException{
    }

    public void landingOnPlanet() throws IllegalMethodOnThisStateException{
    }

    public void turnOnCannon() throws IllegalMethodOnThisStateException{
    }

    public void turnOnMotor() throws IllegalMethodOnThisStateException{
    }

    public void turnOnShields() throws IllegalMethodOnThisStateException{
    }


    public CardEffect getCurrentEffect(){
        return currentEffect;
    }

    public void rollDices() throws IllegalMethodOnThisStateException{
            Random random = new Random();
            int firstDice = random.nextInt(6) + 1;
            int secondDice = random.nextInt(6) + 1;
            dicesResult = firstDice + secondDice;
            System.out.println("The dices have been rolled: " + firstDice + " and " + secondDice + ". Result: " + dicesResult);
            currentEffect.setPosition(dicesResult);
        }

    public abstract void applyEffect(Player player);

    public int getLevel() {
        return level;
    }

}
