package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectLoseGoods;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectWinGoods;
import it.polimi.ingsw.is25am49.server.model.enums.AdventureStatesTypes;

import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.enums.ComponentType;

import java.util.ArrayList;


public class SmugglerCard extends Enemy {

    public SmugglerCard(GameBoard gameBoard, String description, int level, String name, int dayslost, int minimumFirePower, int redGoods, int yellowGoods, int greenGoods, int blueGoods, int lostGoods) {
        super(gameBoard, description, level,name, dayslost, minimumFirePower);
        listEffects.add(new EffectWinGoods("WinGoods", redGoods, yellowGoods, greenGoods, blueGoods));
        listEffects.add(new EffectLoseGoods("LoseGoods", lostGoods));
    }


    private static final int WIN_GOODS_INDEX = 0;
    private static final int LOSE_GOODS_INDEX = 1;

    @Override
    protected void evaluateBattleOutcome(ShipBoard shipBoard) {
        if (checkWinFirePower(shipBoard)) {
            deactivateComponents(shipBoard);
            coordinatesOfActivable = new ArrayList<>();
            System.out.println("You have more firepower to defeat the smugglers! Choose where you want to put your new goods");
            isDefeated = true;
            System.out.println("Now decide if you want to accept the rewards ");
            currentState = AdventureStatesTypes.DECISION_REWARDS;
        } else if (checkDrawFirePower(shipBoard)) {
            deactivateComponents(shipBoard);
            coordinatesOfActivable = new ArrayList<>();
            System.out.println("You have drawn with the smugglers!");
            tryNextPlayer();
        } else {
            deactivateComponents(shipBoard);
            coordinatesOfActivable = new ArrayList<>();
            System.out.println("You have been defeated by the smugglers! You lose some goods");
            currentEffect = listEffects.get(LOSE_GOODS_INDEX); //lost effect
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
        }

    }

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
        System.out.println("You have accepted the rewards! You can now choose where to put your new goods");
        currentEffect = listEffects.get(WIN_GOODS_INDEX); //the curreffect is wingoods
        currentState = AdventureStatesTypes.DECISION_GOODS;
    }

    @Override
    protected void handleRefuseOfRewards() {
        System.out.println("You have refused the rewards. The card is defeated and you don't lose days");
        update();
    }

    @Override
    protected int getMaxActivatableComponents(ShipBoard shipBoard) {
        return shipBoard.getActivatableComponents(ComponentType.DOUBLE_CANNON).size();
    }

}
