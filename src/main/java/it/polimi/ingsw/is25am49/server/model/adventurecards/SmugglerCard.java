package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectLoseGoods;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectWinGoods;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;

import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;

import java.util.ArrayList;


/**
 * Represents the SmugglerCard, a specific type of enemy in the game.
 * This card handles battles and effects for winning or losing goods.
 * Extends the Enemy class.
 */
public class SmugglerCard extends Enemy {

    /**
     * Constructs a SmugglerCard object with the specified parameters and initializes its effects.
     *
     * @param gameBoard the game board associated with this card
     * @param description a brief description of the card
     * @param level the difficulty level of the card
     * @param name the name of the card
     * @param dayslost the number of days lost when this card is encountered
     * @param minimumFirePower the minimum firepower required to face this card
     * @param redGoods the amount of red goods won if the effect is triggered
     * @param yellowGoods the amount of yellow goods won if the effect is triggered
     * @param greenGoods the amount of green goods won if the effect is triggered
     * @param blueGoods the amount of blue goods won if the effect is triggered
     * @param lostGoods the amount of goods lost if the effect is triggered
     */
    public SmugglerCard(GameBoard gameBoard, String description, int level, String name, int dayslost, int minimumFirePower, int redGoods, int yellowGoods, int greenGoods, int blueGoods, int lostGoods) {
        super(gameBoard, description, level,name, dayslost, minimumFirePower);
        listEffects.add(new EffectWinGoods("WinGoods", redGoods, yellowGoods, greenGoods, blueGoods));
        listEffects.add(new EffectLoseGoods("LoseGoods", lostGoods));
    }


    /**
     * Index representing the position associated with rewarding goods
     * when the battle outcome is a win in the SmugglerCard game logic.
     */
    private static final int WIN_GOODS_INDEX = 0;
    /**
     * Represents the index corresponding to goods lost as a result of a smuggling encounter.
     * Used within the SmugglerCard class to track penalties.
     */
    private static final int LOSE_GOODS_INDEX = 1;

    /**
     * Evaluates the outcome of a battle based on the ship's firepower.
     * Changes the game state, applies effects, and determines the next step in the game.
     *
     * @param shipBoard the current state of the ship's board, including its firepower and components.
     */
    @Override
    protected void evaluateBattleOutcome(ShipBoard shipBoard) {
        if (checkWinFirePower(shipBoard)) {
            deactivateComponents(shipBoard, coordinatesOfActivable);
            System.out.println("You have more firepower to defeat the smugglers!");
            isDefeated = true;
            System.out.println("Now decide if you want to accept the rewards ");
            currentState = AdventureStatesTypes.DECISION_REWARDS;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_REWARDS);
        } else if (checkDrawFirePower(shipBoard)) {
            deactivateComponents(shipBoard, coordinatesOfActivable);
            System.out.println("You have drawn with the smugglers!");
            tryNextPlayer();
        } else {
            deactivateComponents(shipBoard, coordinatesOfActivable);
            System.out.println("You have been defeated by the smugglers! You lose some goods");
            currentEffect = listEffects.get(LOSE_GOODS_INDEX); //lost effect
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
        }

    }

    /**
     * Applies the effect of the smuggler card to the current player.

     */
    @Override
    public void applyEffect() {
        super.applyEffect();
        Player currentPlayer = gameBoard.getCurrentPlayer();
        System.out.println("You are in applyeffect, yur goods will be modified");
        currentEffect.action(currentPlayer);
        currentPlayer.setDaysThisTurn(-daysLost);
        if (isDefeated) {
            update();
        } else {
            tryNextPlayer();
        }
    }

    /**
     * Determines and returns the next state in the adventure sequence after processing batteries.
     *
     * @return The next adventure state, specifically DECISION_CANNON.
     */
    @Override
    protected AdventureStatesTypes determineStateAfterBatteries() {
        return AdventureStatesTypes.DECISION_CANNON;
    }

    /**
     * Handles the decision-making process when the shipboard has empty batteries.
     *
     * @param shipBoard the ShipBoard instance representing the current state of the ship.
     */
    @Override
    protected void handleEmptyBatteryDecision(ShipBoard shipBoard) {
        evaluateBattleOutcome(shipBoard);
    }

    /**
     * Handles the reward acceptance process in the adventure.
     * Sets the current effect to winning goods and updates the state
     * to decision-making for goods placement.
     * Verifies player connection or applies a default effect if disconnected.
     */
    @Override
    protected void handleRewards() {
        System.out.println("You have accepted the rewards! You can now choose where to put your new goods");
        currentEffect = listEffects.get(WIN_GOODS_INDEX); //the curreffect is wingoods
        currentState = AdventureStatesTypes.DECISION_GOODS;
        checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_GOODS);
    }

    /**
     * Handles the player's refusal of the rewards associated with the SmugglerCard.
     *
     * Upon refusal, the card is marked as defeated without any loss of days,
     * and the card's state is updated.
     */
    @Override
    protected void handleRefuseOfRewards() {
        System.out.println("You have refused the rewards. The card is defeated and you don't lose days");
        update();
    }


    /**
     * Override toString method to print the card in a box format
     * @return a string representation of the card
     */

    @Override
    public String toString(){
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
        str.append(daysLostText).append(" ".repeat(width - daysLostText.length() - 1)).append("|\n");
        String minFirePowerText = "| MINIMUM FIREPOWER: " + minimumFirePower;
        str.append(minFirePowerText).append(" ".repeat(width - minFirePowerText.length() - 1)).append("|\n");
        str.append(separator);
        String redGoodsText = "| RED GOODS: " + ((EffectWinGoods) listEffects.get(WIN_GOODS_INDEX)).getRedGoods();
        str.append(redGoodsText).append(" ".repeat(width - redGoodsText.length() - 1)).append("|\n");
        String yellowGoodsText = "| YELLOW GOODS: " + ((EffectWinGoods) listEffects.get(WIN_GOODS_INDEX)).getYellowGoods();
        str.append(yellowGoodsText).append(" ".repeat(width - yellowGoodsText.length() - 1)).append("|\n");
        String greenGoodsText = "| GREEN GOODS: " + ((EffectWinGoods) listEffects.get(WIN_GOODS_INDEX)).getGreenGoods();
        str.append(greenGoodsText).append(" ".repeat(width - greenGoodsText.length() - 1)).append("|\n");
        String blueGoodsText = "| BLUE GOODS: " + ((EffectWinGoods) listEffects.get(WIN_GOODS_INDEX)).getBlueGoods();
        str.append(blueGoodsText).append(" ".repeat(width - blueGoodsText.length() - 1)).append("|\n");
        str.append(separator);
        String lostGoodsText = "| LOST GOODS: " + ((EffectLoseGoods) listEffects.get(LOSE_GOODS_INDEX)).getLooseGoods();
        str.append(lostGoodsText).append(" ".repeat(width - lostGoodsText.length() - 1)).append("|\n");
        str.append(border);
        return str.toString();

    }
}
