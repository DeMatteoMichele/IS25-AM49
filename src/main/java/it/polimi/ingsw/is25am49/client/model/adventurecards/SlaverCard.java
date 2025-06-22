package it.polimi.ingsw.is25am49.client.model.adventurecards;

import it.polimi.ingsw.is25am49.client.model.GameBoard;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.client.model.cardeffects.EffectModifyCrew;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;

import java.util.ArrayList;


public class SlaverCard extends Enemy {

    protected int lostAstronauts;
    protected int creditsEarned;

    public SlaverCard(String cardId, GameBoard gameBoard, String description, int level, String name, int dayslost, int minimumFirePower, int creditsEarned, int lostAstronauts) {
        super(cardId, gameBoard, description, level, name, dayslost, minimumFirePower);
        this.lostAstronauts = lostAstronauts;
        this.creditsEarned = creditsEarned;
        listEffects.add(new EffectModifyCrew("LoseAstronauts", lostAstronauts));
        for (CardEffect effect : listEffects) {
            effect.setUserInterface(userInterface);
        }
    }

    private static final int LOSE_CREW_INDEX = 0;

    @Override
    protected void evaluateBattleOutcome(ShipBoard shipBoard) {
        if (checkWinFirePower(shipBoard)) {
            deactivateComponents(shipBoard, coordinatesOfActivable);
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You have more firepower to defeat the Slavers! You win some credits!");
            userInterface.updateEnemyDefeated(gameBoard.getCurrentPlayer().getIdPlayer(), "Slavers", cardId);
            isDefeated = true;
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "Now decide if you want to accept the rewards ");
            currentState = AdventureStatesTypes.DECISION_REWARDS;
            userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_REWARDS);

        } else if (checkDrawFirePower(shipBoard)) {
            deactivateComponents(shipBoard, coordinatesOfActivable);
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You have drawn with the Slavers!");
            tryNextPlayer();
        } else {
            deactivateComponents(shipBoard, coordinatesOfActivable);
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You have been defeated by the Slavers! You lose some members of your crew,decide where you want to remove them");
            currentEffect = listEffects.get(LOSE_CREW_INDEX); //loseAstronauts effect
            currentState = AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS;
            userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS);
        }
    }

    @Override
    public void applyEffect() {
        super.applyEffect();
        Player currentPlayer = gameBoard.getCurrentPlayer();
        currentPlayer.setDaysThisTurn(-daysLost);
        if (isDefeated) {
            currentPlayer.addCredits(creditsEarned);
            userInterface.updateEarnedCredits(gameBoard.getCurrentPlayer().getIdPlayer(), creditsEarned);
            update();
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
        currentState = AdventureStatesTypes.EFFECT;
        userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
        applyEffect();
    }

    @Override
    protected void handleRefuseOfRewards() {
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You have refused the rewards. The card is defeated and you don't lose days");
        update();
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
