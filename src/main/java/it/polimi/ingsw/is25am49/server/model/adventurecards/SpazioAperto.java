package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import javafx.util.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * A class representing the SpazioAperto adventure card.
 * Provides specific mechanics for processing the card's actions,
 * decision-making, and effects within the game.
 */
public class SpazioAperto extends AdventureCard {

    /**
     * Constructs a SpazioAperto object with the specified parameters.
     *
     * @param gameBoard the game board associated with the SpazioAperto instance
     * @param description a brief description of the SpazioAperto card
     * @param level the level of the SpazioAperto card
     * @param name the name of the SpazioAperto card
     */
    public SpazioAperto(GameBoard gameBoard, String description, int level, String name) {
        super(gameBoard, description, level, name);

    }

    /**
     * Represents a list of pairs, where each pair associates a specific point on the game board
     * with a token. In this context, it is used to track battery cell placements or related data.
     */
    private List<Pair<Point, Token>> batteries;
    /**
     * Represents a collection of motor positions on the game board.
     * Each motor is identified by a specific point.
     */
    private List<Point> motors;
    /**
     * Represents the process of handling or managing a list of players
     * within the SpazioAperto class context.
     */
    private List<Player> playersProcess;
    /**
     * Tracks the index of the current player in the game sequence.
     * Used to determine which player's turn it currently is.
     */
    private int currentPlayerIndex;
    /**
     * Maps each Player to their declared engine power.
     * Key: Player instance.
     * Value: Declared engine power as an Integer.
     * Used to track the engine power declared by each player during the game.
     */
    private Map<Player, Integer> declaredEnginePower=new HashMap<>();


    /**
     * Executes the process of drawing the card and handles the associated actions.
     */
    @Override
    public void drawCard() {
        super.drawCard();
        System.out.println("You have found an open space! You can choose if you wanna activate your double motors for travel days");
        playersProcess = gameBoard.getLeaderBoard().stream().map(Map.Entry::getKey).filter(player -> !gameBoard.getLeaderBoard().getDisconnectedPlayers().containsValue(player)).toList();
        currentPlayerIndex = 0;
        gameBoard.setCurrentPlayer(playersProcess.get(currentPlayerIndex));
        checkRequirements();

    }

    /**
     * Checks whether the current player's ship meets the requirements to activate double motors.
     * If sufficient batteries and double motors are available, allows the player to decide their usage.
     * Otherwise, processes travel days as a fallback.
     */
    @Override
    public void checkRequirements() {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumberOfBatteries = ship.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        int totalNumberOfDoubleMotors = ship.getActivatableComponents(ComponentType.DOUBLE_ENGINE).size();
        if (totalNumberOfBatteries > 0 && totalNumberOfDoubleMotors > 0) {
            System.out.println("you have enough batteries to activate your double motors. One battery activates one double motor. " +
                    "Now choose which batteries you wanna use and how much");
            currentState = AdventureStatesTypes.DECISION_BATTERIES;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_BATTERIES);
        } else {
            //non ho abbastanza batterie per attivare i doppi motori
            System.out.println("You don't have enough batteries to activate your double motors");
            processTravelDays(ship);
        }
    }

    /**
     * Processes the number of travel days for the current player based on the ship's engine power.
     * Updates the leaderboard and advances to the next player or concludes the round if the last player has been processed.
     *
     * @param ship the ship whose engine power determines the travel days earned by the player
     */
    private void processTravelDays(ShipBoard ship) {
        int daysEarned = ship.getTotalEnginePower();
        declaredEnginePower.put(gameBoard.getCurrentPlayer(), daysEarned);
        gameBoard.getCurrentPlayer().setDaysThisTurn(daysEarned);
        gameBoard.updateLeaderboard();
        System.out.println("player " + gameBoard.getCurrentPlayer().getUsername() + " has earned " + daysEarned + " travel days");
        System.out.println("now i check if it was the last player");
        //controllo se currplayer era l'ultimo, se no chiamo checkrequirements sul player successivo
        currentPlayerIndex++;
        if (currentPlayerIndex >= playersProcess.size()) {
            System.out.println("it was the last player, end of the card");
            update();
        } else {
            System.out.println("It wasn't the last player, go on with the next player");
            gameBoard.setCurrentPlayer(playersProcess.get(currentPlayerIndex));
            checkRequirements();
        }
    }

    /**
     * Completes the decision process based on the current adventure state and the provided coordinates.
     *
     * @param coordinates the list of pairs containing points and corresponding tokens chosen by the player.
     *                    This input is used to determine the progression of the decision-making workflow.
     */
    @Override
    public void finishDecision(List<Pair<Point, Token>> coordinates) {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        switch (currentState) {
            case DECISION_BATTERIES -> {
                checkNumberOfBatteries(coordinates, ship);
                System.out.println("you have choosen " + coordinates.size() + " batteries, so you will activate " + coordinates.size() + " motors");
                this.batteries = new ArrayList<>(coordinates);
                if(batteries.isEmpty()) {
                    processTravelDays(ship);
                    return;
                }
                System.out.println("now choose which motors you wanna activate");
                currentState = AdventureStatesTypes.DECISION_MOTOR;
                checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_MOTOR);
            }
            default -> throw new IllegalArgumentException("invalid state");
        }
    }

    /**
     * Completes the decision process for activating motors based on the provided coordinates.
     *
     * @param coordinates a set of points representing the coordinates of the motors to be activated
     * @throws IllegalArgumentException if the state is invalid or the number of motors exceeds the available batteries
     */
    @Override
    public void finishDecision(Set<Point> coordinates) {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        this.motors = new ArrayList<>(coordinates);
        checkCorrectnessOfActivable(currentState, coordinates, ship);
        switch (currentState) {
            case DECISION_MOTOR -> {
                System.out.println("you have decided the coordinates of the motors you wanna activate");
                if (batteries.size() < coordinates.size()) {
                    throw new IllegalArgumentException("you have to activate the same number of motors as batteries");
                }
                activateComponentsWithBatteries(ship, motors, batteries);
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
            default -> throw new IllegalArgumentException("invalid state");
        }
    }

    /**
     * Applies the effect specific to the SpazioAperto adventure card.
     *
     * This implementation calculates the travel days earned based on the power
     * of activated motors and deactivates the relevant components of the ship.
     * Calls the superclass applyEffect method to ensure proper state handling.
     */
    @Override
    public void applyEffect() {
        super.applyEffect();
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        System.out.println("you are in apply effect, now i will calculate the days you have earned");
        //calcolo i giorni guadagnati in base alla potenza dei motori attivati
        processTravelDays(ship);
        deactivateComponents(ship, motors);
    }

    /**
     * Updates the current state of the adventure card to {@code AdventureStatesTypes.UPDATE}.
     * Stops the flight for players with zero declared engine power.
     * Delegates additional update logic to the superclass implementation.
     */
    @Override
    protected void update() {
        currentState = AdventureStatesTypes.UPDATE;
        declaredEnginePower.forEach((player, power) -> { if (power == 0) gameBoard.userStopFlight(player); });
        super.update();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        final int width = 90;  // Larghezza standard del box
        String border = "+" + "-".repeat(width - 2) + "+\n";
        String separator = "|" + "-".repeat(width - 2) + "|\n";
        str.append(border);
        String nameText = "| NAME: " + name;
        str.append(nameText).append(" ".repeat(width - nameText.length() - 1)).append("|\n");
        str.append(separator);
        String descText = "| DESCRIPTION: " + description;
        appendTextWithWrapping(str, descText, width);
        String levelText = "| LEVEL: " + level;
        str.append(levelText).append(" ".repeat(width - levelText.length() - 1)).append("|\n");
        str.append(border);
        return str.toString();
    }
}
