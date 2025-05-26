package it.polimi.ingsw.is25am49.client.model.adventurecards;

import it.polimi.ingsw.is25am49.client.model.GameBoard;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.client.model.cardeffects.EffectLoseGoods;
import it.polimi.ingsw.is25am49.client.model.cardeffects.EffectWinGoods;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;

import java.util.ArrayList;


public class SmugglerCard extends Enemy {

    public SmugglerCard(String cardId,GameBoard gameBoard, String description, int level, String name, int dayslost, int minimumFirePower, int redGoods, int yellowGoods, int greenGoods, int blueGoods, int lostGoods) {
        super(cardId, gameBoard, description, level, name, dayslost, minimumFirePower);
        listEffects.add(new EffectWinGoods("WinGoods", redGoods, yellowGoods, greenGoods, blueGoods));
        listEffects.add(new EffectLoseGoods("LoseGoods", lostGoods));
        for(CardEffect effect : listEffects){
            effect.setUserInterface(userInterface);
        }
    }


    private static final int WIN_GOODS_INDEX = 0;
    private static final int LOSE_GOODS_INDEX = 1;

    @Override
    protected void evaluateBattleOutcome(ShipBoard shipBoard) {
        if (checkWinFirePower(shipBoard)) {
            deactivateComponents(shipBoard);
            coordinatesOfActivable = new ArrayList<>();
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You have more firepower to defeat the smugglers! Choose where you want to put your new goods");
            isDefeated = true;
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Now decide if you want to accept the rewards ");
            currentState = AdventureStatesTypes.DECISION_REWARDS;
        } else if (checkDrawFirePower(shipBoard)) {
            deactivateComponents(shipBoard);
            coordinatesOfActivable = new ArrayList<>();
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You have drawn with the smugglers!");
            tryNextPlayer();
        } else {
            deactivateComponents(shipBoard);
            coordinatesOfActivable = new ArrayList<>();
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You have been defeated by the smugglers! You lose some goods");
            currentEffect = listEffects.get(LOSE_GOODS_INDEX); //lost effect
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
        }

    }

    @Override
    public void applyEffect() {
        super.applyEffect();
        Player currentPlayer = gameBoard.getCurrentPlayer();
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You are in applyeffect, yur goods will be modified");
        currentEffect.action(currentPlayer);
        currentPlayer.setDaysThisTurn(-daysLost);
        if (isDefeated) {
            update();
        } else {
            tryNextPlayer();
        }
    }

    @Override
    protected AdventureStatesTypes determineStateAfterBatteries() {
        return AdventureStatesTypes.DECISION_CANNON;
    }

    @Override
    protected void handleEmptyBatteryDecision(ShipBoard shipBoard) {
        evaluateBattleOutcome(shipBoard);
    }

    @Override
    protected void handleRewards() {
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You have accepted the rewards! You can now choose where to put your new goods");
        currentEffect = listEffects.get(WIN_GOODS_INDEX); //the curreffect is wingoods
        currentState = AdventureStatesTypes.DECISION_GOODS;
    }

    @Override
    protected void handleRefuseOfRewards() {
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You have refused the rewards. The card is defeated and you don't lose days");
        update();
    }

    @Override
    protected int getMaxActivatableComponents(ShipBoard shipBoard) {
        return shipBoard.getActivatableComponents(ComponentType.DOUBLE_CANNON).size();
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
