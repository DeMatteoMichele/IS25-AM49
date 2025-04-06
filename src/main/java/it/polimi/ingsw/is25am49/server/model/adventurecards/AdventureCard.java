package it.polimi.ingsw.is25am49.server.model.adventurecards;
import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;
import javafx.util.Pair;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class AdventureCard {

    protected String description;
    protected boolean isFinished;
    protected int level;
    protected int dicesResult;
    protected CardEffect currentEffect;
    protected AdventureStatesTypes currentState;
    protected GameBoard gameBoard;

    public AdventureCard(GameBoard gameBoard, String description, int level) {
        this.gameBoard = gameBoard;
        this.description = description;
        this.isFinished = false;
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void checkRequirements(){}

    public void drawCard() {
        currentState = AdventureStatesTypes.DRAW;
        System.out.println("Hai pescato la carta: " + getClass().getSimpleName());
        gameBoard.setCurrentPlayer(gameBoard.getLeader());
    }

    public void landingOnStationOrShip(boolean isLanded) throws IllegalMethodOnThisStateException {
        if (currentState != AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP)
            throw new IllegalMethodOnThisStateException("Wrong state");
    }

    public void landingOnPlanet(boolean isLanded, Planet planet) throws IllegalMethodOnThisStateException{
        if(currentState != AdventureStatesTypes.DECISION_LAND_PLANET)
            throw new IllegalMethodOnThisStateException("Wrong state");
    }

    public CardEffect getCurrentEffect() {
        return currentEffect;
    }

    public AdventureStatesTypes getCurrentState() {
        return currentState;
    }

    public void rollDices() throws IllegalMethodOnThisStateException {
        if (currentState != AdventureStatesTypes.ROLL_DICES)
            throw new IllegalMethodOnThisStateException("Wrong state");
        Random random = new Random();
        int firstDice = random.nextInt(6) + 1;
        int secondDice = random.nextInt(6) + 1;
        dicesResult = firstDice + secondDice;
        System.out.println("The dices have been rolled. Result: " + dicesResult);
        currentEffect.setPosition(dicesResult);
    }

    public void applyEffect() {
        if (currentState != AdventureStatesTypes.EFFECT)
            throw new IllegalMethodOnThisStateException("Wrong state");
    }

    public int getLevel() {
        return level;
    }

    public void finishDecision(List<Pair<Point, Token>> coordinates) {
    }

    public void finishDecision(Set<Point> coordinates) {
    }

    public void acceptPrizes(boolean wantsRewards){
    }

    public List<Player> getDefeatedPlayers() {
        return null;
    }

    public boolean getIsFinished() {
        return isFinished;
    }
    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    protected void update(){
        currentState = AdventureStatesTypes.UPDATE;
        gameBoard.checkIfSomeoneHasZeroAstronauts();
        gameBoard.finishUpdateLeaderboard();
        isFinished= true;
    }

}
