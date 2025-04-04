package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectLoseGoods;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectModifyCrew;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;

public class SlaverCard extends EnemyCard {

    protected int lostAstronauts;
    protected int creditsEarned;
    public SlaverCard(GameBoard gameBoard, String description, int level, int dayslost, int minimumFirePower, int creditsEarned, int lostAstronauts) {
        super(gameBoard, description, level, dayslost, minimumFirePower);
        this.lostAstronauts = lostAstronauts;
        this.creditsEarned = creditsEarned;
        listEffects.add(new EffectModifyCrew("LoseAstronauts", lostAstronauts));
    }

    private static final int LOSE_CREW_INDEX = 0;

    @Override
    protected void evaluateBattleOutcome(ShipBoard shipBoard) {
        if (checkWinFirePower(shipBoard)) {
            System.out.println("You have more firepower to defeat the Slavers! You win some credits!");
            System.out.println("Now decide if you want to accept the rewards ");
            isDefeated = true;
            System.out.println("Now decide if you want to accept the rewards ");
            currentState= AdventureStatesTypes.DECISION_REWARDS;

        } else if (checkDrawFirePower(shipBoard)) {
            System.out.println("You have drawn with the Slavers!");
            tryNextPlayer();
        } else {
            System.out.println("You have been defeated by the Slavers! You lose some members of your crew,decide where you want to remove them");
            currentEffect = listEffects.get(LOSE_CREW_INDEX); //loseAstronauts effect
            currentState = AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS;
        }
    }

    @Override
    public void applyEffect() {
        super.applyEffect();
        Player currentPlayer = gameBoard.getCurrentPlayer();
        currentPlayer.setDaysThisTurn(-daysLost);
        if (isDefeated) {
            currentPlayer.addCredits(creditsEarned);
            currentState = AdventureStatesTypes.UPDATE;
            isFinished = true;
        } else {
            currentEffect.action(currentPlayer);
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
        currentState=AdventureStatesTypes.EFFECT;
        applyEffect();
    }

    @Override
    protected void handleRefuseOfRewards() {
        System.out.println("You have refused the rewards. The card is defeated and you don't lose days");
        currentState = AdventureStatesTypes.UPDATE;
        isFinished = true;
    }

    @Override
    protected int getMaxActivatableComponents(ShipBoard shipBoard) {
        return (int) shipBoard.getActivatableComponents(ComponentType.DOUBLE_CANNON).stream().count();
    }

}
