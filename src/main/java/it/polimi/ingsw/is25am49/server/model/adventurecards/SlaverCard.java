package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectModifyCrew;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;

import java.util.ArrayList;


/**
 * Represents a SlaverCard, a type of enemy encountered in the game.
 * It includes specific attributes and behaviors related to its interactions.
 */
public class SlaverCard extends Enemy {

    /**
     * Represents the number of astronauts lost during the encounter.
     * Used as a part of the outcome evaluation in the SlaverCard context.
     */
    protected int lostAstronauts;
    /**
     * Represents the number of credits earned after completing specific actions or events
     * within the context of the SlaverCard gameplay events.
     */
    protected int creditsEarned;

    /**
     * Constructs a SlaverCard with the given parameters.
     *
     * @param gameBoard the game board associated with this card
     * @param description a brief description of the card
     * @param level the difficulty level of the card
     * @param name the name of the card
     * @param dayslost the number of days lost when encountering this card
     * @param minimumFirePower the minimum firepower required to overcome the card
     * @param creditsEarned the credits earned by defeating this card
     * @param lostAstronauts the number of astronauts lost when encountering this card
     */
    public SlaverCard(GameBoard gameBoard, String description, int level, String name, int dayslost, int minimumFirePower, int creditsEarned, int lostAstronauts) {
        super(gameBoard, description, level, name, dayslost, minimumFirePower);
        this.lostAstronauts = lostAstronauts;
        this.creditsEarned = creditsEarned;
        listEffects.add(new EffectModifyCrew("LoseAstronauts", lostAstronauts));
    }

    /**
     * Index constant representing the action to lose crew members.
     * Used to differentiate possible outcomes or actions in the context of enemy interactions.
     */
    private static final int LOSE_CREW_INDEX = 0;

    /**
     * Evaluates the outcome of a battle based on the firepower comparison between the player's ship and the enemy.
     * Adjusts the game state and effects accordingly.
     *
     * @param shipBoard the player's current ship board to evaluate firepower, apply effects, and update state
     */
    @Override
    protected void evaluateBattleOutcome(ShipBoard shipBoard) {
        if (checkWinFirePower(shipBoard)) {
            deactivateComponents(shipBoard, coordinatesOfActivable);
            System.out.println("You have more firepower to defeat the Slavers! You win some credits!");
            System.out.println("Now decide if you want to accept the rewards ");
            isDefeated = true;
            System.out.println("Now decide if you want to accept the rewards ");
            currentState = AdventureStatesTypes.DECISION_REWARDS;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_REWARDS);

        } else if (checkDrawFirePower(shipBoard)) {
            deactivateComponents(shipBoard, coordinatesOfActivable);
            System.out.println("You have drawn with the Slavers!");
            tryNextPlayer();
        } else {
            deactivateComponents(shipBoard, coordinatesOfActivable);
            System.out.println("You have been defeated by the Slavers! You lose some members of your crew,decide where you want to remove them");
            currentEffect = listEffects.get(LOSE_CREW_INDEX); //loseAstronauts effect
            currentState = AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS);
        }
    }

    /**
     * Applies the effect of the SlaverCard to the current player.
     *
     * The effect modifies the player's days for the current turn.
     * If the player is defeated, credits are awarded, and the game state is updated.
     * Otherwise, the current effect is applied to the player, and the turn moves to the next player.
     */
    @Override
    public void applyEffect() {
        super.applyEffect();
        Player currentPlayer = gameBoard.getCurrentPlayer();
        currentPlayer.setDaysThisTurn(-daysLost);
        if (isDefeated) {
            currentPlayer.addCredits(creditsEarned);
            update();
        } else {
            currentEffect.action(currentPlayer);
            tryNextPlayer();
        }
    }

    /**
     * Determines the next state of the adventure after completing battery-related decisions.
     *
     * @return the next state of the adventure, specifically DECISION_CANNON
     */
    @Override
    protected AdventureStatesTypes determineStateAfterBatteries() {
        return AdventureStatesTypes.DECISION_CANNON;
    }

    /**
     * Handles the scenario where the player's ship has depleted batteries,
     * determining the consequences for the current state of the battle.
     *
     * @param shipBoard the current state of the player's ship including its status and resources.
     */
    @Override
    protected void handleEmptyBatteryDecision(ShipBoard shipBoard) {
        evaluateBattleOutcome(shipBoard);
    }

    /**
     * Handles the rewards phase by transitioning the state to EFFECT
     * and applying the relevant effect of the card.
     */
    @Override
    protected void handleRewards() {
        currentState = AdventureStatesTypes.EFFECT;
        applyEffect();
    }

    /**
     * Handles the scenario where rewards from the SlaverCard are refused by the player.
     * Outputs a message indicating the refusal, marks the card as defeated, and ensures
     * no loss of days occurs. Triggers a state update via the {@code update} method.
     */
    @Override
    protected void handleRefuseOfRewards() {
        System.out.println("You have refused the rewards. The card is defeated and you don't lose days");
        update();
    }

    /**
     * Generates a formatted string representation of the SlaverCard object.
     * @return a string containing a formatted representation of the SlaverCard.
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
        str.append(separator);
        str.append(levelText).append(" ".repeat(width - levelText.length() - 1)).append("|\n");
        String daysLostText = "| DAYS LOST: " + daysLost;
        str.append(daysLostText).append(" ".repeat(width - daysLostText.length() - 1)).append("|\n");
        String creditsEarnedText = "| CREDITS EARNED: " + creditsEarned;
        str.append(creditsEarnedText).append(" ".repeat(width - creditsEarnedText.length() - 1)).append("|\n");
        String lostAstronautsText = "| LOST ASTRONAUTS: " + lostAstronauts;
        str.append(lostAstronautsText).append(" ".repeat(width - lostAstronautsText.length() - 1)).append("|\n");
        String minimumFirePowerText = "| MINIMUM FIREPOWER: " + minimumFirePower;
        str.append(minimumFirePowerText).append(" ".repeat(width - minimumFirePowerText.length() - 1)).append("|\n");
        str.append(border);
        return str.toString();
    }

}
