package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalNumOfCannonsException;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;
import javafx.util.Pair;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public abstract class EnemyCard extends AdventureCard {
    protected final int minimumFirePower;
    protected final int daysLost;
    protected final List<CardEffect> listEffects;

    public EnemyCard(GameBoard gameBoard, String description,int level,  int daysLost, int minimumFirePower) {
        super(gameBoard, description, level);
        this.minimumFirePower = minimumFirePower;
        this.daysLost = daysLost;
        listEffects = new ArrayList<>();
    }

    protected boolean isDefeated = false;
    protected List<Pair<Point, Token>> coordinatesOfBatteries;

    @Override
    public void drawCard() {
        super.drawCard();
        System.out.println("You have found a " + this.getClass().getSimpleName() + "! You can defeat them if you have enough firepower");
        checkRequirements();
    }

    @Override
    public void checkRequirements() {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        if (checkWinFirePower(shipBoard)) {
            evaluateBattleOutcome(shipBoard);
        } else {
            if (canDefeatOrDrawOnlyUsingBatteries(shipBoard)) {
                System.out.println("You have enough batteries to defeat/draw with a " + this.getClass().getSimpleName() + "! Choose what batteries do you want to use");
                currentState = AdventureStatesTypes.DECISION_BATTERIES;
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
                .map(point -> shipBoard.getTile(point).getFirePower())
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

    private void activateComponentWithBatteries(List<Point> coordinatesOfActivable, ShipBoard shipBoard) {
        if (coordinatesOfActivable.size() != coordinatesOfBatteries.size()) {
            throw new IllegalNumOfCannonsException("You have to choose at max the same number of cannons as batteries");
        }
        System.out.println("You have decided the coord for the components");
        for (int i = 0; i < coordinatesOfActivable.size(); i++) {
            shipBoard.turnOnComponent(coordinatesOfActivable.get(i), coordinatesOfBatteries.get(i).getKey());
        }
    }

    protected abstract void evaluateBattleOutcome(ShipBoard shipBoard);

    protected void tryNextPlayer() {
        try {
            gameBoard.nextPlayer();
            System.out.println("Now check on next player");
            checkRequirements();
        } catch (IllegalSetOfNextPlayerException e) {
            System.out.println("No more players, now to update");
            currentState = AdventureStatesTypes.UPDATE;
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
                if (coordinatesOfBatteries.isEmpty()) {
                    System.out.println("You have decided not to use batteries");
                    handleEmptyBatteryDecision(shipBoard);
                    return;
                }
                System.out.println("You have decided the coord for the batteries.");
                currentState = determineStateAfterBatteries();
            }
            case DECISION_GOODS -> {
                System.out.println("You have decided the coord for the goods");
                currentEffect.setListOfCoordinates(coordinates);
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
            case DECISION_ASTRONAUTS_OR_ALIENS -> {
                System.out.println("You have decided the coord for the crew");
                currentEffect.setListOfCoordinates(coordinates);
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
            default -> throw new IllegalArgumentException("Invalid state: " + currentState);
        }
    }

    @Override
    public void finishDecision(Set<Point> coordinates) throws IllegalNumOfCannonsException {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        switch (currentState) {
            case DECISION_CANNON -> {
                try {
                    List<Point> coordinatesOfCannons = new ArrayList<>(coordinates);
                    activateComponentWithBatteries(coordinatesOfCannons, shipBoard);
                    evaluateBattleOutcome(shipBoard);
                }
                catch (IllegalNumOfCannonsException e){
                    System.out.println("Rechoose correctly the number of cannons as the same as batteries");
                }
            }
            case DECISION_SHIELDS -> {
                try {
                    List<Point> coordinatesOfShield = new ArrayList<>(coordinates);
                    activateComponentWithBatteries(coordinatesOfShield, shipBoard);
                    applyEffect();
                }
                catch(IllegalNumOfCannonsException e){
                    System.out.println("Rechoose correctly the number of shields as the same as batteries");
                }
            }
            default -> throw new IllegalArgumentException("Invalid state: " + currentState);
        }
    }


}
