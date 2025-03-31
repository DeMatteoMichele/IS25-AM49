package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectLoseGoods;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectWinGoods;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;

import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;


public class SmugglerCard extends EnemyCard {

    public SmugglerCard(GameBoard gameBoard, String description, int daysLost, int level, int creditsEarned, int minimumFirePower, int redGoods, int yellowGoods, int greenGoods, int blueGoods, int lostGoods) {
        super(gameBoard, description, daysLost, level, creditsEarned);
        this.minimumFirePower = minimumFirePower;
        listEffects.add(new EffectWinGoods("WinGoods", redGoods, yellowGoods, greenGoods, blueGoods));
        listEffects.add(new EffectLoseGoods("LoseGoods", lostGoods));
    }


    private static final int WIN_GOODS_INDEX = 0;
    private static final int LOSE_GOODS_INDEX = 1;

    @Override
    protected void evaluateBattleOutcome(ShipBoard shipBoard) {
        if (checkWinFirePower(shipBoard)) {
            System.out.println("You have more firepower to defeat the smugglers! Choose where you want to put your new goods");
            isDefeated = true;
            currentEffect = listEffects.get(WIN_GOODS_INDEX); //the curreffect is wingoods
            currentState = AdventureStatesTypes.DECISION_GOODS;
        } else if (checkDrawFirePower(shipBoard)) {
            System.out.println("You have drawn with the smugglers!");
            tryNextPlayer();
        } else {
            System.out.println("You have been defeated by the smugglers! You lose some goods");
            currentEffect = listEffects.get(LOSE_GOODS_INDEX); //lost effect
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
            //currentState = AdventureStatesTypes.DECISION_GOODS; we dont go anymore in decision goods cause losing is automatic
        }
    }

    @Override
    public void applyEffect() {
        super.applyEffect();
        Player currentPlayer = gameBoard.getCurrentPlayer();
        System.out.println("You are in applyeffect, yur goods will be modified");
        currentEffect.action(currentPlayer);
        currentPlayer.setDaysThisTurn(-daysLost);
        if (isDefeated)
            currentState = AdventureStatesTypes.UPDATE;
        else {
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
}
