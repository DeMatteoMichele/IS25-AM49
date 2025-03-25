package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
        gameBoard.setCurrentPlayer(gameBoard.getLeader());
    }

    public void landingOnStationOrShip(boolean isLanded) throws IllegalMethodOnThisStateException{
        if(currentState != AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP)
            throw new IllegalMethodOnThisStateException("Wrong state");
    }

    public void landingOnPlanet() throws IllegalMethodOnThisStateException{
        if(currentState != AdventureStatesTypes.DECISION_LAND_PLANET)
            throw new IllegalMethodOnThisStateException("Wrong state");
    }

    public void turnOnCannon() throws IllegalMethodOnThisStateException{
        if(currentState != AdventureStatesTypes.DECISION_CANNON)
            throw new IllegalMethodOnThisStateException("Wrong state");
    }

    public void turnOnMotor() throws IllegalMethodOnThisStateException{
        if(currentState != AdventureStatesTypes.DECISION_MOTOR)
            throw new IllegalMethodOnThisStateException("Wrong state");
    }

    public void turnOnShields() throws IllegalMethodOnThisStateException{
        if(currentState != AdventureStatesTypes.DECISION_SHIELDS)
            throw new IllegalMethodOnThisStateException("Wrong state");
    }


    public CardEffect getCurrentEffect(){
        return currentEffect;
    }

    public AdventureStatesTypes getCurrentState() {
        return currentState;
    }

    public void rollDices() throws IllegalMethodOnThisStateException{
            if(currentState != AdventureStatesTypes.ROLL_DICES)
                throw new IllegalMethodOnThisStateException("Wrong state");
            Random random = new Random();
            int firstDice = random.nextInt(6) + 1;
            int secondDice = random.nextInt(6) + 1;
            dicesResult = firstDice + secondDice;
            System.out.println("The dices have been rolled: " + firstDice + " and " + secondDice + ". Result: " + dicesResult);
            currentEffect.setPosition(dicesResult);
    }

    public void applyEffect(){
        if(currentState != AdventureStatesTypes.EFFECT)
            throw new IllegalMethodOnThisStateException("Wrong state");
    }

    public int getLevel() {
        return level;
    }

    public void finishDecision(List<Pair<Point, Token>> coordinates){

    }

    public void finishDecision(Set<Point> coordinates){

    }

}
