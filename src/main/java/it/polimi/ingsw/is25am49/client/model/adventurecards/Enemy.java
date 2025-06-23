package it.polimi.ingsw.is25am49.client.model.adventurecards;

import it.polimi.ingsw.is25am49.client.model.GameBoard;
import it.polimi.ingsw.is25am49.client.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalNumOfActivableException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public abstract class Enemy extends AdventureCard {
    protected final int minimumFirePower;
    protected final int daysLost;
    protected final List<CardEffect> listEffects;

    public Enemy(String cardId, GameBoard gameBoard, String description, int level, String name, int daysLost, int minimumFirePower) {
        super(cardId, gameBoard, description, level, name);
        this.minimumFirePower = minimumFirePower;
        this.daysLost = daysLost;
        listEffects = new ArrayList<>();
    }

    protected boolean isDefeated = false;
    protected List<Pair<Point, Token>> coordinatesOfBatteries = new ArrayList<>();
    protected List<Point> coordinatesOfActivable = new ArrayList<>();

    @Override
    public void drawCard() {
        super.drawCard();
        userInterface.updateGenericCardMessage(genericIdForUpdates, "You have found a " + this.getClass().getSimpleName() + "! You can defeat them if you have enough firepower");
        checkRequirements();
    }

    @Override
    public void checkRequirements() {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        if (checkWinFirePower(shipBoard)) {
            evaluateBattleOutcome(shipBoard);
        } else {
            if (canDefeatOrDrawOnlyUsingBatteries(shipBoard)) {
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You have enough batteries to defeat/draw with a " + this.getClass().getSimpleName() + "! Choose what batteries do you want to use");
                currentState = AdventureStatesTypes.DECISION_BATTERIES;
                userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
                checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_BATTERIES);
            } else {
                if (checkDrawFirePower(shipBoard)) {
                    evaluateBattleOutcome(shipBoard);
                } else {
                    evaluateBattleOutcome(shipBoard);
                }
            }
        }
    }

    protected boolean canDefeatOrDrawOnlyUsingBatteries(ShipBoard shipBoard) {
        double firePowerUserCanActivateWithBatteries = shipBoard.getActivatableComponents(ComponentType.DOUBLE_CANNON)
                .stream()
                .map(point -> shipBoard.getTile(point).getPotentialFirePower())
                .sorted(Comparator.reverseOrder())
                .limit(shipBoard.getTotalNumberOfaToken(Token.BATTERY_CELLS))
                .mapToDouble(Double::doubleValue)
                .sum();
        return (((shipBoard.getTotalFirePower() + firePowerUserCanActivateWithBatteries) >= minimumFirePower)
                &&
                ((shipBoard.getTotalFirePower() < minimumFirePower)));
    }

    protected boolean checkWinFirePower(ShipBoard shipBoard) {
        double totalFirePower = shipBoard.getTotalFirePower();
        return totalFirePower > minimumFirePower;
    }

    protected boolean checkDrawFirePower(ShipBoard shipBoard) {
        double totalFirePower = shipBoard.getTotalFirePower();
        return totalFirePower == minimumFirePower;
    }

    protected abstract void evaluateBattleOutcome(ShipBoard shipBoard);

    protected void tryNextPlayer() {
        try {
            gameBoard.nextPlayer();
            checkRequirements();
        } catch (IllegalSetOfNextPlayerException e) {
            userInterface.updateGenericCardMessage(genericIdForUpdates, "No more players, now to update");
            update();
        }
    }

    protected abstract AdventureStatesTypes determineStateAfterBatteries();

    protected abstract void handleEmptyBatteryDecision(ShipBoard shipBoard);

    @Override
    public void finishDecision(List<Pair<Point, Token>> coordinates) {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        switch (currentState) {
            case DECISION_BATTERIES -> {
                coordinatesOfBatteries = coordinates;
                checkNumberOfBatteries(coordinatesOfBatteries, shipBoard);
                if (coordinatesOfBatteries.isEmpty()) {
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You have decided not to use batteries");
                    handleEmptyBatteryDecision(shipBoard);
                    return;
                }
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You have decided the coord for the batteries.");
                currentState = determineStateAfterBatteries();
                userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
                checkConnectionOrDefaultEffect(currentState);
            }
            case DECISION_GOODS -> {
                userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(), coordinates, coordinates.size(), false);
                currentEffect.setListOfCoordinates(coordinates);
                currentState = AdventureStatesTypes.EFFECT;
                userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
                applyEffect();
            }
            case DECISION_ASTRONAUTS_OR_ALIENS -> {
                userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(), coordinates, coordinates.size(), true);
                currentEffect.setListOfCoordinates(coordinates);
                currentState = AdventureStatesTypes.EFFECT;
                userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
                applyEffect();
            }
            default -> throw new IllegalMethodOnThisStateException("Invalid state: " + currentState);
        }
    }

    @Override
    public void finishDecision(Set<Point> coordinates) throws IllegalNumOfActivableException {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        switch (currentState) {
            case DECISION_CANNON -> {
                    coordinatesOfActivable = new ArrayList<>(coordinates);
                    activateComponentsWithBatteries(shipBoard,coordinatesOfActivable,coordinatesOfBatteries);
                    userInterface.updateFinishDecisionActivating(gameBoard.getCurrentPlayer().getIdPlayer(), coordinates);
                    evaluateBattleOutcome(shipBoard);
            }
            case DECISION_SHIELDS -> {
                    coordinatesOfActivable = new ArrayList<>(coordinates);
                    activateComponentsWithBatteries(shipBoard,coordinatesOfActivable,coordinatesOfBatteries);
                    userInterface.updateFinishDecisionActivating(gameBoard.getCurrentPlayer().getIdPlayer(), coordinates);
                    currentState = AdventureStatesTypes.EFFECT;
                    userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
                    applyEffect();
            }
            default -> throw new IllegalMethodOnThisStateException("Invalid state: " + currentState);
        }
    }

    @Override
    public void acceptPrizes(boolean wantsRewards) {
        if (currentState != AdventureStatesTypes.DECISION_REWARDS)
            throw new IllegalStateException("Wrong State");
        else if (wantsRewards) {
            userInterface.updateAcceptPrizes(gameBoard.getCurrentPlayer().getIdPlayer(), true);
            handleRewards();
        } else {
            userInterface.updateAcceptPrizes(gameBoard.getCurrentPlayer().getIdPlayer(), false);
            handleRefuseOfRewards();
        }
    }

    protected abstract void handleRewards();

    protected abstract void handleRefuseOfRewards();

}
