package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectLoseGoods;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectModifyCrew;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;

public class SlaverCard extends EnemyCard {

    public SlaverCard(GameBoard gameBoard, String description, int daysLost, int level, int creditsEarned, int minimumFirePower, int lostAstronauts) {
        super(gameBoard, description, daysLost, level, creditsEarned);
        this.minimumFirePower = minimumFirePower;
        this.lostAstronauts = lostAstronauts;
        listEffects.add(new EffectModifyCrew("LoseAstronauts", lostAstronauts));
    }

    private static final int LOSE_CREW_INDEX = 0;


    @Override
    public void applyEffect() {
        super.applyEffect();
        Player currentPlayer = gameBoard.getCurrentPlayer();
        currentPlayer.setDaysThisTurn(-daysLost);
        if (isDefeated) {
            currentPlayer.addCredits(creditsEarned);
            currentState = AdventureStatesTypes.UPDATE;
        } else {
            currentEffect.action(currentPlayer);
            tryNextPlayer();
        }
    }

    @Override
    protected void evaluateBattleOutcome(ShipBoard shipBoard) {
        if (checkWinFirePower(shipBoard)) {
            System.out.println("You have more firepower to defeat the Slavers! You win some credits!");
            isDefeated = true;
            currentState=AdventureStatesTypes.EFFECT;
            applyEffect();
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
    protected AdventureStatesTypes determineStateAfterBatteries() {
        return AdventureStatesTypes.DECISION_CANNON;
    }

    @Override
    protected void handleEmptyBatteryDecision(ShipBoard shipBoard) {
        evaluateBattleOutcome(shipBoard);
    }
}
