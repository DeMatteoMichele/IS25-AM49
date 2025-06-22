package it.polimi.ingsw.is25am49.client.model.adventurecards;

import it.polimi.ingsw.is25am49.client.model.GameBoard;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.client.model.cardeffects.EffectCannonata;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;

import java.util.ArrayList;
import java.util.List;

public class PiratesCard extends Enemy {

    protected int creditsEarned;

    public PiratesCard(String cardId, GameBoard gameBoard, String description, int level, String name, int dayslost, int minimumFirePower, int creditsEarned, List<CardEffect> listEffects) {
        super(cardId, gameBoard, description, level, name, dayslost, minimumFirePower);
        this.listEffects.addAll(listEffects);
        this.creditsEarned = creditsEarned;
        for (CardEffect effect : listEffects) {
            effect.setUserInterface(userInterface);
        }
    }


    private final List<Player> defeatedPlayers = new ArrayList<>();
    private boolean cannonadesPhase = false;
    private static final int BIG_CANNONADE_LEVEL = 2;


    @Override
    protected void evaluateBattleOutcome(ShipBoard shipBoard) {
        if (checkWinFirePower(shipBoard)) {
            deactivateComponents(shipBoard, coordinatesOfActivable);
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You have defeated the pirates!");
            userInterface.updateEnemyDefeated(gameBoard.getCurrentPlayer().getIdPlayer(), "Pirates", cardId);
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "Now decide if you want to win or stay as you are");
            currentState = AdventureStatesTypes.DECISION_REWARDS;
            userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_REWARDS);
        } else if (checkDrawFirePower(shipBoard)) {
            deactivateComponents(shipBoard, coordinatesOfActivable);
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You have drawn with the pirates!");
            tryNextPlayerOrHandleDefeatedPlayers();
        } else {
            deactivateComponents(shipBoard, coordinatesOfActivable);
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You have lost against the pirates!");
            defeatedPlayers.add(gameBoard.getCurrentPlayer());
            tryNextPlayerOrHandleDefeatedPlayers();
        }
    }

    @Override
    protected AdventureStatesTypes determineStateAfterBatteries() {
        return cannonadesPhase ? AdventureStatesTypes.DECISION_SHIELDS : AdventureStatesTypes.DECISION_CANNON;
    }

    @Override
    protected void handleEmptyBatteryDecision(ShipBoard shipBoard) {
        if (!cannonadesPhase)
            evaluateBattleOutcome(shipBoard);
        else {
            currentState = AdventureStatesTypes.EFFECT;
            userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
            applyEffect();
        }
    }

    @Override
    protected void handleRewards() {
        gameBoard.getCurrentPlayer().addCredits(creditsEarned);
        userInterface.updateEarnedCredits(gameBoard.getCurrentPlayer().getIdPlayer(), creditsEarned);
        gameBoard.getCurrentPlayer().setDaysThisTurn(-daysLost);
        handleDefeatedPlayers();
    }

    @Override
    protected void handleRefuseOfRewards() {
        handleDefeatedPlayers();
    }

    protected void tryNextPlayerOrHandleDefeatedPlayers() {
        try {
            gameBoard.nextPlayer();
            checkRequirements();
        } catch (IllegalSetOfNextPlayerException e) {
            handleDefeatedPlayers();
        }
    }

    private void handleDefeatedPlayers() {
        if (defeatedPlayers.isEmpty()) {
            userInterface.updateGenericCardMessage(genericIdForUpdates, "No defeated players, now to update");
            update();
        } else {
            userInterface.updateGenericCardMessage(genericIdForUpdates, "No more players, now" + defeatedPlayers.getFirst().getUsername() + "must roll the dices!");
            gameBoard.setCurrentPlayer(defeatedPlayers.getFirst());
            for (Player player : defeatedPlayers) {
                player.setDaysThisTurn(-daysLost);
            }
            currentEffect = listEffects.removeFirst();
            currentState = AdventureStatesTypes.ROLL_DICES;
            userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
            checkConnectionOrDefaultEffect(AdventureStatesTypes.ROLL_DICES);
        }
    }

    private void tryNextPlayerDefeated() {
        int currentIndexOfDefeatedPlayer = defeatedPlayers.indexOf(gameBoard.getCurrentPlayer());
        if (currentIndexOfDefeatedPlayer >= 0 && currentIndexOfDefeatedPlayer < defeatedPlayers.size() - 1) { //i have to apply the currenteffect on all the players
            gameBoard.setCurrentPlayer(defeatedPlayers.get(currentIndexOfDefeatedPlayer + 1));
            checkForBatteriesToUseForShields();
        } else {    //i have applied the effect on all the players
            if (listEffects.isEmpty()) {  //effects are terminated, card is finished
                userInterface.updateGenericCardMessage(genericIdForUpdates, "All the cannonades are finished, now to update");
                update();
            } else {      //effects are not finished, first player has to roll the dices on the next effect
                gameBoard.setCurrentPlayer(defeatedPlayers.getFirst());
                currentEffect = listEffects.removeFirst();
                userInterface.updateGenericCardMessage(genericIdForUpdates, "Another meteora! Now" + defeatedPlayers.getFirst().getUsername() + "must roll the dices again!");
                currentState = AdventureStatesTypes.ROLL_DICES;
                userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
                checkConnectionOrDefaultEffect(AdventureStatesTypes.ROLL_DICES);
            }
        }
    }

    @Override
    public void rollDices() {
        super.rollDices();
        checkForBatteriesToUseForShields();
    }

    private void checkForBatteriesToUseForShields() {
        boolean hasBatteries = gameBoard.getCurrentPlayer().getShipBoard().getTotalNumberOfaToken(Token.BATTERY_CELLS) > 0;
        if (hasBatteries) {  // player has batteries for shields
            checkDimensionOfCannonade();
        } else {  // player has no batteries for shields
            currentState = AdventureStatesTypes.EFFECT;
            userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
            applyEffect();
        }
    }

    private void checkDimensionOfCannonade() {
        if (currentEffect.getLevel() == BIG_CANNONADE_LEVEL) {//big cannonade, player cant defend himself
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "Big cannonade! You cannot defend yourself with shields!");
            currentState = AdventureStatesTypes.EFFECT;
            userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
            applyEffect();
            return;
        }
        ShipBoard currentShipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        long availableShields = currentShipBoard.getActivatableComponents(ComponentType.SHIELD).stream().count();
        if (availableShields > 0) {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You can activate the shields. Choose the coordinates of the batteries to use.");
            currentState = AdventureStatesTypes.DECISION_BATTERIES;
            userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
            coordinatesOfActivable = currentShipBoard.getActivatableComponents(ComponentType.SHIELD);
        } else {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You have no shields to activate. You suffer the effect of the cannonade.");
            currentState = AdventureStatesTypes.EFFECT;
            userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
            applyEffect();
        }
    }

    @Override
    public void applyEffect() {
        currentEffect.action(gameBoard.getCurrentPlayer());
        deactivateComponents(gameBoard.getCurrentPlayer().getShipBoard(), coordinatesOfActivable);
        coordinatesOfActivable = new ArrayList<>();
        tryNextPlayerDefeated();
    }

    @Override
    public List<Player> getDefeatedPlayers() {
        return defeatedPlayers;
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
        str.append(separator);
        String creditsText = "| CREDITS: " + creditsEarned;
        str.append(creditsText).append(" ".repeat(width - creditsText.length() - 1)).append("|\n");
        String daysText = "| DAYS LOST: " + daysLost;
        str.append(daysText).append(" ".repeat(width - daysText.length() - 1)).append("|\n");
        String minFireText = "| MINIMUM FIREPOWER: " + minimumFirePower;
        str.append(minFireText).append(" ".repeat(width - minFireText.length() - 1)).append("|\n");
        str.append(separator);
        str.append("| CANNONADES:").append(" ".repeat(width - "| CANNONADES:".length() - 1)).append("|\n");
        if (currentEffect != null) {
            EffectCannonata cannonata = (EffectCannonata) currentEffect;
            String levelCannonata = "| LEVEL: " + cannonata.getLevel();
            str.append(levelCannonata).append(" ".repeat(width - levelCannonata.length() - 1)).append("|\n");
            String orientationCannonata = "| ORIENTATION: " + cannonata.getOrientation();
            str.append(orientationCannonata).append(" ".repeat(width - orientationCannonata.length() - 1)).append("|\n");
            str.append(border);
        }
        for (CardEffect effect : listEffects) {
            EffectCannonata cannonata = (EffectCannonata) effect;
            String levelCannonata = "| LEVEL: " + cannonata.getLevel();
            str.append(levelCannonata).append(" ".repeat(width - levelCannonata.length() - 1)).append("|\n");
            String orientationCannonata = "| ORIENTATION: " + cannonata.getOrientation();
            str.append(orientationCannonata).append(" ".repeat(width - orientationCannonata.length() - 1)).append("|\n");
            str.append(border);
        }
        return str.toString();
    }
}
