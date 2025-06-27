package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectCannonata;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectModifyCrew;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a "War Zone" card in the game. This card introduces challenges
 * related to engine power, firepower, and various penalties for players. It
 * includes logic for handling specific effects, applying penalties, and
 * determining player statuses during the war zone phase.
 */
public class WarZoneCard extends AdventureCard {
    /**
     * Represents the player with the lowest engine power in the current context.
     * This is determined based on specific criteria defined by the game logic.
     */
    Player playerWithMinimumEnginePower;
    /**
     * Represents the player with the lowest firepower among all players in the war zone.
     */
    Player playerWithMinimumFirePower;
    /**
     * Represents the minimum required engine power for a specific condition
     * or action in the WarZoneCard context.
     */
    private int minimumEnginePower;
    /**
     * A list of {@link CardEffect} objects associated with the WarZoneCard.
     * Represents the effects applied when the card is activated during gameplay.
     */
    private final List<CardEffect> listEffects;
    /**
     * Represents a list of batteries in the WarZoneCard. Each battery is defined as a pair combining
     * a Point, representing its location, and a Token, indicating its type or purpose.
     */
    private List<Pair<Point, Token>> batteries;
    /**
     * Represents the current phase of the war zone in the game.
     * Initialized to 0 and updated as the game progresses.
     */
    private int warZonePhase = 0;
    /**
     * Represents the number of days lost by players due to the effects of this
     * WarZoneCard.
     * This value impacts gameplay by delaying players' progress.
     */
    private final int daysLost;
    /**
     * Represents the minimum firepower required to meet certain conditions or pass
     * requirements in the WarZoneCard. Used in gameplay mechanics related to firepower checks.
     */
    private double minimumFirePower = 100;
    /**
     * Represents the base level required for activating "Big Cannonball" effects in the WarZoneCard.
     * This constant is used in determining the dimension of the cannonball to be utilized.
     */
    private static final int BIG_CANNONBALL_LEVEL = 1;
    /**
     * Represents a list of points on the game board that can be activated
     * by players during the War Zone phase of the game.
     */
    private List<Point> coordinatesOfActivable = new ArrayList<>();
    /**
     * Represents the number of astronauts lost during a war zone phase.
     * Used to track the impact of specific card effects or in-game events.
     */
    private int lostAstronauts;


    /**
     * Constructs a WarZoneCard with specified attributes and effects.
     *
     * @param gameBoard the game board where the card belongs
     * @param description the description of the card
     * @param level the difficulty level of the card
     * @param name the name of the card
     * @param daysLost the number of days lost when this card is used
     * @param lostAstronauts the number of astronauts lost when this card is used
     * @param effects the list of additional card effects
     */
    public WarZoneCard(GameBoard gameBoard, String description, int level, String name, int daysLost, int lostAstronauts, List<CardEffect> effects) {
        super(gameBoard, description, level, name);
        listEffects = new ArrayList<>();
        listEffects.add(new EffectModifyCrew("ModifyCrew", lostAstronauts));
        this.listEffects.addAll(effects);
        minimumEnginePower = 100;
        this.daysLost = daysLost;
        this.lostAstronauts = lostAstronauts;
    }

    /**
     * Executes the action of drawing the "War Zone" card.
     * Displays a message indicating that the "War Zone" has been encountered.
     * Invokes the  checkRequirements method to validate and process specific conditions.
     * Overrides the base implementation from the parent class.
     */
    @Override
    public void drawCard() {
        super.drawCard();
        System.out.println("You have found a War Zone! Good luck, it will be tough to get out of it.");
        checkRequirements();
    }

    /**
     * Calculates the total crew size of a player's shipboard based on the quantity
     * of astronauts and alien tokens.
     *
     * @param p the player whose total crew is to be calculated
     * @return the total number of astronauts, brown aliens, and purple aliens on the player's shipboard
     */
    private int getTotalCrew(Player p) {
        return p.getShipBoard().getTotalNumberOfaToken(Token.ASTRONAUT) +
                p.getShipBoard().getTotalNumberOfaToken(Token.BROWN_ALIENS) +
                p.getShipBoard().getTotalNumberOfaToken(Token.PURPLE_ALIENS);
    }


    /**
     * Evaluates and enforces various requirements for progressing the game when this card is activated.
     * Delegates engine-specific requirements to checkRequirementsEngine().
     */
    @Override
    public void checkRequirements() {
        if (gameBoard.getLeaderBoard().sizeOfConnected() == 1) {
            System.out.println("you are the only player, you skip this card");
            update();
            return;
        }
        Player playerWithMinimumCrew = gameBoard.getLeaderBoard().stream()
                .map(Map.Entry::getKey)
                .filter(player -> !gameBoard.getLeaderBoard().getDisconnectedPlayers().containsValue(player))
                .min((p1, p2) -> Integer.compare(
                        getTotalCrew(p1),
                        getTotalCrew(p2)))
                .orElse(gameBoard.getCurrentPlayer());
        gameBoard.setCurrentPlayer(playerWithMinimumCrew);
        System.out.println("Player with minimum crew is " + gameBoard.getCurrentPlayer().getUsername());
        System.out.println("You lose" + daysLost + " travel days");
        gameBoard.getCurrentPlayer().setDaysThisTurn(-daysLost);
        gameBoard.updateLeaderboard();
        System.out.println("leader updated! now is : " + gameBoard.getFirstPlayerConnected().getUsername());
        gameBoard.setCurrentPlayer(gameBoard.getFirstPlayerConnected());
        checkRequirementsEngine();
    }

    /**
     * Checks if the current player can activate double motors based on available batteries
     * and updates the game state accordingly. If the player cannot activate motors, it checks
     * the player's engine power and determines if they are the player with the minimum engine power.
     * Executes further actions based on these evaluations.
     */
    public void checkRequirementsEngine() {
        System.out.println("you are in phase 2 of the war zone, now we will check the player with less engine power");
        System.out.println("i check if " + gameBoard.getCurrentPlayer().getUsername() + " can activate double motors");
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumberOfBatteries = ship.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        int totalNumberOfDoubleMotors = ship.getActivatableComponents(ComponentType.DOUBLE_ENGINE).size();
        if (totalNumberOfBatteries > 0 && totalNumberOfDoubleMotors > 0) {
            System.out.println("you have enough batteries to activate your double motors. One battery activates one motor. ");
                currentState = AdventureStatesTypes.DECISION_BATTERIES;
                checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_BATTERIES);
        } else {
            System.out.println("You can't activate any double motors");
            int enginePower = ship.getTotalEnginePower();
            if (enginePower < minimumEnginePower) {
                minimumEnginePower = enginePower;
                playerWithMinimumEnginePower = gameBoard.getCurrentPlayer();
            }
            handlePlayersEngine();
        }
    }

    /**
     * Manages the progression of players during the engine-handling phase of the war zone.
     * Executes player connection validation and state updates as necessary.
     *
     * Throws:
     * - IllegalSetOfNextPlayerException if the next player cannot be set.
     */
    public void handlePlayersEngine() {
        try {
            gameBoard.nextPlayer();
            System.out.println("It wasn't the last player, go on with the next player");
            checkRequirementsEngine();
        } catch (IllegalSetOfNextPlayerException e) {
            System.out.println("It was the last player");
            System.out.println("Player with minimum engine power is " + playerWithMinimumEnginePower.getUsername());
            System.out.println(playerWithMinimumEnginePower.getUsername() + " you lose" + lostAstronauts + " crew members, you have to choose which ones");
            currentEffect = listEffects.removeFirst();
            warZonePhase++;
            gameBoard.setCurrentPlayer(playerWithMinimumEnginePower);
            currentState = AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS);
        }
    }

    /**
     * Executes the finish decision logic based on the current state and given coordinates.
     *
     * @param coordinates a list of pair objects where each pair contains a point on the board
     *                    and its associated token, representing the player's decision.
     */
    @Override
    public void finishDecision(List<Pair<Point, Token>> coordinates) {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        checkCorrectnessOfTokensAndPoints(currentState, coordinates, ship);
        switch (currentState) {
            case DECISION_BATTERIES -> {
                if (warZonePhase == 0) {
                    checkNumberOfBatteries(coordinates, ship);
                    System.out.println("you have chosen " + coordinates.size() + " batteries");
                    batteries = new ArrayList<>(coordinates);
                    System.out.println("Now you have to choose which motors you wanna activate");
                    currentState = AdventureStatesTypes.DECISION_MOTOR;
                    checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_MOTOR);
                } else if (warZonePhase == 1) {
                    checkNumberOfBatteries(coordinates, ship);
                    System.out.println("you have chosen " + coordinates.size() + " batteries");
                    batteries = new ArrayList<>(coordinates);
                    System.out.println("Now you have to choose which cannons you wanna activate");
                    currentState = AdventureStatesTypes.DECISION_CANNON;
                    checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_CANNON);
                } else {
                    checkNumberOfBatteries(coordinates, ship);
                    System.out.println("you have chosen " + coordinates.size() + " batteries to activate your shield");
                    batteries = new ArrayList<>(coordinates);
                    System.out.println("Now you have to choose which shields you wanna activate");
                    currentState = AdventureStatesTypes.DECISION_SHIELDS;
                    checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_SHIELDS);
                }
            }
            case DECISION_ASTRONAUTS_OR_ALIENS -> {
                currentEffect.setListOfCoordinates(coordinates);
                System.out.println("You have chosen the astronauts or aliens you wanna lose");
                currentEffect.action(gameBoard.getCurrentPlayer());
                System.out.println(gameBoard.getCurrentPlayer().getUsername() + " has lost his chosen crew members");
                gameBoard.setCurrentPlayer(gameBoard.getFirstPlayerConnected());
                checkRequirementsFirePower();
            }
            default -> throw new IllegalArgumentException("wrong state");
        }
    }

    /**
     * Checks the player's ability to meet the firepower requirements using batteries and double cannons.
     * - Evaluates if the current player has sufficient batteries and double cannons to activate them.
     * - Updates the game state if the player has enough batteries to proceed.
     * - If not sufficient, calculates and compares the player's firepower with the minimum threshold.
     * - Updates the player with the minimum firepower if applicable and handles firepower-specific rules.
     */
    public void checkRequirementsFirePower() {
        System.out.println("now i check which player has the minimum fire power");
        System.out.println("i check if " + gameBoard.getCurrentPlayer().getUsername() + " has enough batteries and double cannons");
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumberOfBatteries = ship.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        int totalNumberOfCannons = ship.getActivatableComponents(ComponentType.DOUBLE_CANNON).size();
        if (totalNumberOfBatteries > 0 && totalNumberOfCannons > 0) {
            System.out.println("you have enough batteries to activate your double Cannons. One battery activates one cannon. " +
                    "You can activate up to " + totalNumberOfBatteries + " double cannon");
            currentState = AdventureStatesTypes.DECISION_BATTERIES;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_BATTERIES);
        } else {
            System.out.println("You don't have enough batteries to activate your double cannons");
            double firePower = ship.getTotalFirePower();
            if (firePower < minimumFirePower) {
                minimumFirePower = firePower;
                playerWithMinimumFirePower = gameBoard.getCurrentPlayer();
            }
            handlePlayersFirePower();
        }
    }

    /**
     * Manages the player's firepower handling during the game.
     * Moves to the next player or updates the current phase and state
     * when the last player in the round has been processed.
     * If an exception is thrown due to lack of a next player, determines
     * the player with minimum firepower, sets it as the current player,
     * and transitions to the dice-rolling phase.
     * Verifies requirements for firepower handling via `checkRequirementsFirePower`.
     */
    public void handlePlayersFirePower() {
        try {
            gameBoard.nextPlayer();
            System.out.println("It wasn't the last player, go on with the next player");
            checkRequirementsFirePower();
        } catch (IllegalSetOfNextPlayerException e) {
            System.out.println("It was the last player");
            System.out.println("Player with minimum fire power is " + playerWithMinimumFirePower.getUsername());
            currentEffect = listEffects.removeFirst();
            gameBoard.setCurrentPlayer(playerWithMinimumFirePower);
            warZonePhase++;
            System.out.println(playerWithMinimumFirePower.getUsername() + "has to roll the dices");
            currentState = AdventureStatesTypes.ROLL_DICES;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.ROLL_DICES);
        }
    }

    /**
     * Handles the completion of a decision phase based on the current state and the provided coordinates.
     * This method processes actions related to motor, cannon, or shield activation, updating game state accordingly.
     *
     * @param coordinates a set of points representing the selected components to activate in the decision phase.
     */
    @Override
    public void finishDecision(Set<Point> coordinates) {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        checkCorrectnessOfActivable(currentState, coordinates, ship);
        switch (currentState) {
            case DECISION_MOTOR -> {
                coordinatesOfActivable = new ArrayList<>(coordinates);
                activateComponentsWithBatteries(ship, coordinatesOfActivable, batteries);
                System.out.println("You have decided the coordinates of the motors you wanna activate");
                int enginePower = ship.getTotalEnginePower();
                if (enginePower < minimumEnginePower) {
                    minimumEnginePower = enginePower;
                    playerWithMinimumEnginePower = gameBoard.getCurrentPlayer();
                }
                deactivateComponents(ship,coordinatesOfActivable);
                handlePlayersEngine();
            }
            case DECISION_CANNON -> {
                coordinatesOfActivable = new ArrayList<>(coordinates);
                activateComponentsWithBatteries(ship, coordinatesOfActivable, batteries);
                System.out.println("You have decided the coordinates of the cannons you wanna activate");
                double firePower = ship.getTotalFirePower();
                if (firePower < minimumFirePower) {
                    minimumFirePower = firePower;
                    playerWithMinimumFirePower = gameBoard.getCurrentPlayer();
                }
                deactivateComponents(ship,coordinatesOfActivable);
                handlePlayersFirePower();
            }
            case DECISION_SHIELDS -> {
                coordinatesOfActivable = new ArrayList<>(coordinates);
                activateComponentsWithBatteries(ship, coordinatesOfActivable, batteries);
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
            default -> throw new IllegalArgumentException("wrong state");
        }
    }

    /**
     * Rolls two six-sided dices and applies their result to the current effect, then checks if batteries
     * are available for additional gameplay decisions. Delegates dice rolling to the parent class implementation.
     *
     * Throws IllegalMethodOnThisStateException if invoked in an invalid game state.
     */
    @Override
    public void rollDices() {
        super.rollDices();
        checkForBatteriesToUse();
    }

    /**
     * Checks if the current player has any battery cells available on their ship.
     * If battery cells are available, proceeds to check the dimension of the cannonball.
     * Otherwise, transitions to the EFFECT state and applies the current effect.
     */
    private void checkForBatteriesToUse() {
        boolean hasBatteries = gameBoard.getCurrentPlayer().getShipBoard().getTotalNumberOfaToken(Token.BATTERY_CELLS) > 0;
        if (hasBatteries) {
            checkDimensionOfCannonBall();
        } else {
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
        }
    }

    /**
     * Handles the logic to determine the player’s actions or consequences based on the cannonball's size.
     * If the cannonball is classified as "big," no defensive actions are allowed, and the effect is applied.
     * Otherwise, checks if shields and battery cells are available to possibly defend against the cannonball.
     * Updates the game state accordingly.
     */
    private void checkDimensionOfCannonBall() {
        ShipBoard currentShipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        if (currentEffect.getLevel() == BIG_CANNONBALL_LEVEL) {//big cannonball, player can't do anything
            System.out.println("Big cannonball! You can't defend yourself with shields! You can't do nothing! Hope that the cannonball misses your ship!");
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
        } else {
            long availableShields = currentShipBoard.getActivatableComponents(ComponentType.SHIELD).size();
            if (availableShields > 0 && currentShipBoard.getTotalNumberOfaToken(Token.BATTERY_CELLS) > 0) {
                System.out.println("Puoi attivare gli scudi. Scegli le coordinate delle batterie da utilizzare.");
                currentState = AdventureStatesTypes.DECISION_BATTERIES;
                checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_BATTERIES);
            } else {
                System.out.println("You don't have shield to activate. You can't do nothing! Hope that the cannonball misses your ship!");
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
        }
    }

    /**
     * Applies the current effect of the card to the current player in the game.
     * Deactivates components on the player's ship board and resets activable coordinates.
     * Initiates the process for handling subsequent effects if applicable.
     */
    @Override
    public void applyEffect() {
        currentEffect.action(gameBoard.getCurrentPlayer());
        deactivateComponents(gameBoard.getCurrentPlayer().getShipBoard(),coordinatesOfActivable);
        tryAgain();
    }

    /**
     * Handles the continuation of effects or updates if no effects remain.
     *
     * If the list of effects is empty, it triggers the completion update
     * process. Otherwise, it processes the next effect, switches the state
     * to rolling dices, and checks player connection or applies a default effect.
     */
    private void tryAgain() {
        if (listEffects.isEmpty()) {
            System.out.println("You have finished the effects of the card");
            update();
        } else {
            currentEffect = listEffects.removeFirst();
            System.out.println("Next effect: " + currentEffect.getDescription());
            System.out.println("now you have to roll the dices again");
            currentState = AdventureStatesTypes.ROLL_DICES;
           checkConnectionOrDefaultEffect(AdventureStatesTypes.ROLL_DICES);
        }
    }

    /**
     * Converts the WarZoneCard object into a formatted string representation.
     * The representation includes a detailed table-like format displaying
     * the card's attributes, such as name, description, level, days lost,
     * lost astronauts, and effects.
     *
     * @return a string representation of the WarZoneCard object
     */
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
        str.append(separator);
        String daysLostText = "| DAYS LOST: " + daysLost;
        String lostAstronautsText = "| LOST ASTRONAUTS: " + ((EffectModifyCrew) listEffects.get(0)).getLostCrew();
        str.append(lostAstronautsText).append(" ".repeat(width - lostAstronautsText.length() - 1)).append("|\n");
        str.append(separator);
        str.append("| CANNONADE EFFECTS: ").append(" ".repeat(width - "| CANNONADE EFFECTS: ".length() - 1)).append("|\n");
        for (int i = 1; i < listEffects.size(); i++) {
            CardEffect effect = listEffects.get(i);
            if (effect instanceof EffectCannonata cannonata) {
                String effectLevel = "|   -EFFECT LEVEL: " + cannonata.getLevel();
                str.append(effectLevel).append(" ".repeat(width - effectLevel.length() - 1)).append("|\n");
                String effectOrientation = "|   -EFFECT ORIENTATION: " + cannonata.getOrientation();
                str.append(effectOrientation).append(" ".repeat(width - effectOrientation.length() - 1)).append("|\n");
            }
            str.append(separator);
        }
        return str.toString();
    }
}