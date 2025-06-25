package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalTokensException;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalNumOfActivableException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Represents an abstract class defining the behavior and properties of an enemy card
 * in the adventure-based game. An enemy has specific requirements for defeat,
 * effects when encountered, and can prompt different decision-making processes for the players.
 * This class is meant to be extended by specific types of enemy cards, each implementing its
 * own unique behavior and effects.
 */
public abstract class Enemy extends AdventureCard {
    protected final int minimumFirePower;
    protected final int daysLost;
    protected final List<CardEffect> listEffects;

    public Enemy(GameBoard gameBoard, String description, int level, String name, int daysLost, int minimumFirePower) {
        super(gameBoard, description, level, name);
        this.minimumFirePower = minimumFirePower;
        this.daysLost = daysLost;
        listEffects = new ArrayList<>();
    }

    /**
     * Indicates whether the enemy has been defeated during the course of the game or battle.
     * The state is represented as a boolean value, where {@code false} signifies that the
     * enemy is still active, and {@code true} denotes that the enemy has been defeated.
     * This variable is used in determining the overall game state and managing
     * interactions involving the enemy.
     */
    protected boolean isDefeated = false;
    /**
     * A list containing pairs of coordinates and tokens that represent the positions of batteries on a game board.
     * Each element of the list is a pair where the first value is a `Point` representing the coordinates,
     * and the second value is a `Token` indicating the type of item (in this case, batteries) located at that position.
     * This field is used to track the battery locations needed for gameplay mechanics in the context of enemy-related operations.
     */
    protected List<Pair<Point, Token>> coordinatesOfBatteries = new ArrayList<>();
    /**
     * A list holding the coordinates of activable points that the enemy can utilize during its actions.
     * Each point in the list represents a specific position that can be activated to trigger corresponding effects or actions.
     */
    protected List<Point> coordinatesOfActivable = new ArrayList<>();

    /**
     * Executes the process of drawing an enemy card in the game. It invokes the base behavior
     * of drawing a card from the superclass and then provides additional functionality specific to the Enemy class.
     *
     * This method prints a message to notify the player about the type of enemy encountered.
     * It also checks the requirements needed to handle the encounter with the enemy, such as determining
     * whether the player has sufficient firepower or necessary resources to engage effectively.
     * Specific outcomes of the encounter are handled within the requirements check.
     */
    @Override
    public void drawCard() {
        super.drawCard();
        System.out.println("You have found a " + this.getClass().getSimpleName() + "! You can defeat them if you have enough firepower");
        checkRequirements();
    }

    /**
     * Checks if the current state of the game board satisfies the requirements to proceed
     * in the adventure against an enemy or evaluates the battle outcome.
     */
    @Override
    public void checkRequirements() {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        if (checkWinFirePower(shipBoard)) {
            evaluateBattleOutcome(shipBoard);
        } else {
            if (canDefeatOrDrawOnlyUsingBatteries(shipBoard)) {
                System.out.println("You have enough batteries to defeat/draw with a " + this.getClass().getSimpleName() + "! Choose what batteries do you want to use");
                currentState = AdventureStatesTypes.DECISION_BATTERIES;
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

    /**
     * Determines if the player's ship can either defeat an enemy or draw the battle outcome
     * solely based on the currently available battery cells.
     *
     * @param shipBoard the player's ship board containing the current state of tiles, components,
     *                  and resources, including battery cells and activatable components such as cannons.
     * @return true if the ship's combined firepower, including firepower from battery-activated components,
     *         is sufficient to meet or exceed the minimum firepower requirement for defeat or to match it for a draw;
     *         false otherwise.
     */
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

    /**
     * Checks if the player's total firepower exceeds the required minimum firepower
     * to win against the enemy.
     *
     * @param shipBoard the current state of the player's ship, including its firepower.
     * @return true if the total firepower of the ship exceeds the enemy's minimum firepower required to win, false otherwise.
     */
    protected boolean checkWinFirePower(ShipBoard shipBoard) {
        double totalFirePower = shipBoard.getTotalFirePower();
        return totalFirePower > minimumFirePower;
    }

    /**
     * Checks whether the firepower values on the given ship board qualify for a draw condition.
     *
     * @param shipBoard the board of the ship whose firepower is being analyzed
     * @return true if the total firepower of the ship board equals the minimum required firepower for a draw, false otherwise
     */
    protected boolean checkDrawFirePower(ShipBoard shipBoard) {
        double totalFirePower = shipBoard.getTotalFirePower();
        return totalFirePower == minimumFirePower;
    }

    /**
     * Evaluates the outcome of a battle, determining the effects or consequences
     * based on the given state of the ship board.
     *
     * @param shipBoard the current state of the ship board, representing the player's
     *                  ship and its attributes during the battle phase
     */
    protected abstract void evaluateBattleOutcome(ShipBoard shipBoard);

    /**
     * Attempts to switch the gameplay to the next player in the game.
     */
    protected void tryNextPlayer() {
        try {
            gameBoard.nextPlayer();
            System.out.println("Now check on next player");
            checkRequirements();
        } catch (IllegalSetOfNextPlayerException e) {
            System.out.println("No more players, now to update");
            update();
        }
    }

    /**
     * Determines the next state of the adventure after evaluating the battery-related actions or decisions.
     *
     * @return The appropriate state of the adventure as an instance of {@code AdventureStatesTypes},
     *         reflecting the result of processing battery-related operations.
     */
    protected abstract AdventureStatesTypes determineStateAfterBatteries();

    /**
     * Handles the decision-making process when the batteries are empty on the ship board.
     * This method is intended to be implemented in subclasses to define specific behavior
     * for managing such scenarios.
     *
     * @param shipBoard the current state of the ship board, which contains the game's
     *                  relevant data to determine how the empty battery situation should
     *                  be handled.
     */
    protected abstract void handleEmptyBatteryDecision(ShipBoard shipBoard);

    /**
     * Processes the decision specified by the current state and the provided coordinates.
     * Depending on the current state, this method validates the coordinates and adjusts
     * the game progress accordingly by transitioning to the next state or handling specific effects.
     *
     * @param coordinates a list of pairs where each pair consists of a point on the board
     *                    and a token representing an action or item to be used at that point
     *                    during the decision-making process.
     *                    - For the DECISION_BATTERIES state, it represents battery cells' coordinates.
     *                    - For the DECISION_GOODS state, it represents goods to be processed.
     *                    - For the DECISION_ASTRONAUTS_OR_ALIENS state, it represents crew coordinates.
     *
     * @throws IllegalMethodOnThisStateException if the method is invoked when the current
     *         state is not one of the valid decision-making states (DECISION_BATTERIES,
     *         DECISION_GOODS, or DECISION_ASTRONAUTS_OR_ALIENS).
     */
    @Override
    public void finishDecision(List<Pair<Point, Token>> coordinates) {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        checkCorrectnessOfTokensAndPoints(currentState, coordinates, shipBoard);
        switch (currentState) {
            case DECISION_BATTERIES -> {
                coordinatesOfBatteries = coordinates;
                checkNumberOfBatteries(coordinatesOfBatteries, shipBoard);
                if (coordinatesOfBatteries.isEmpty()) {
                    System.out.println("You have decided not to use batteries");
                    handleEmptyBatteryDecision(shipBoard);
                    return;
                }
                System.out.println("You have decided the coord for the batteries.");
                currentState = determineStateAfterBatteries();
                checkConnectionOrDefaultEffect(currentState);
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
            default -> throw new IllegalMethodOnThisStateException("Invalid state: " + currentState);
        }
    }

    /**
     * Completes the player's decision based on their current state and the chosen set of coordinates.
     *
     * @param coordinates the set of coordinates selected by the player for activation.
     * @throws IllegalNumOfActivableException if the number of activable components specified is invalid
     *         for the current state or violates game rules.
     */
    @Override
    public void finishDecision(Set<Point> coordinates) throws IllegalNumOfActivableException {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        checkCorrectnessOfActivable(currentState, coordinates, shipBoard);
        switch (currentState) {
            case DECISION_CANNON -> {
                    coordinatesOfActivable = new ArrayList<>(coordinates);
                    activateComponentsWithBatteries(shipBoard,coordinatesOfActivable,coordinatesOfBatteries);
                    evaluateBattleOutcome(shipBoard);
            }
            case DECISION_SHIELDS -> {
                    coordinatesOfActivable = new ArrayList<>(coordinates);
                    activateComponentsWithBatteries(shipBoard,coordinatesOfActivable,coordinatesOfBatteries);
                    currentState = AdventureStatesTypes.EFFECT;
                    applyEffect();
            }
            default -> throw new IllegalMethodOnThisStateException("Invalid state: " + currentState);
        }
    }

    /**
     * Handles the acceptance or refusal of rewards based on the provided input.
     * Ensures the method is invoked only in the appropriate game state and triggers
     * the corresponding reward or refusal handling logic.
     *
     * @param wantsRewards a boolean indicating if the rewards should be accepted
     *                     (true to accept rewards, false to refuse them).
     * @throws IllegalStateException if the method is called in an incorrect state.
     */
    @Override
    public void acceptPrizes(boolean wantsRewards) {
        if (currentState != AdventureStatesTypes.DECISION_REWARDS)
            throw new IllegalStateException("Wrong State");
        else if (wantsRewards) {
            System.out.println("You have accepted the rewards. You lose " + daysLost + " days");
            handleRewards();
        } else {
            System.out.println("You have refused the rewards");
            handleRefuseOfRewards();
        }
    }

    /**
     * Handles the distribution of rewards after an encounter with the enemy.
     */
    protected abstract void handleRewards();

    /**
     * Handles the logic for when a player refuses the rewards associated with the enemy.
     */
    protected abstract void handleRefuseOfRewards();

}
