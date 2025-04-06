package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.server.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;

import java.util.ArrayList;
import java.util.List;

public class PiratesCard extends Enemy {

    protected int creditsEarned;

    public PiratesCard(GameBoard gameBoard, String description, int level, int dayslost, int minimumFirePower, int creditsEarned, List<CardEffect> listEffects) {
        super(gameBoard, description, level, dayslost, minimumFirePower);
        this.listEffects.addAll(listEffects);
        this.creditsEarned = creditsEarned;
    }


    private final List<Player> defeatedPlayers = new ArrayList<>();
    private boolean cannonadesPhase = false;
    private static final int BIG_CANNONADE_LEVEL = 2;


    @Override
    protected void evaluateBattleOutcome(ShipBoard shipBoard) {
        if (checkWinFirePower(shipBoard)) {
            deactivateComponents(shipBoard);
            coordinatesOfActivable = new ArrayList<>();
            System.out.println("You have defeated the pirates!");
            System.out.println("Now decide if you want to win or stay as you are");
            currentState = AdventureStatesTypes.DECISION_REWARDS;
        } else if (checkDrawFirePower(shipBoard)) {
            deactivateComponents(shipBoard);
            coordinatesOfActivable = new ArrayList<>();
            System.out.println("You have drawn with the pirates!");
            tryNextPlayerOrHandleDefeatedPlayers();
        } else {
            deactivateComponents(shipBoard);
            coordinatesOfActivable = new ArrayList<>();
            System.out.println("You have lost against the pirates!");
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
            applyEffect();
        }
    }

    @Override
    protected void handleRewards() {
        gameBoard.getCurrentPlayer().addCredits(creditsEarned);
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
            System.out.println("Now check on next player");
            checkRequirements();
        } catch (IllegalSetOfNextPlayerException e) {
            handleDefeatedPlayers();
        }
    }

    private void handleDefeatedPlayers() {
        if (defeatedPlayers.isEmpty()) {
            System.out.println("No defeated players, now to update");
            update();
        } else {
            System.out.println("No more players, now the first defeated must roll the dices!");
            gameBoard.setCurrentPlayer(defeatedPlayers.getFirst());
            for (Player player : defeatedPlayers) {
                player.setDaysThisTurn(-daysLost);
            }
            currentEffect = listEffects.removeFirst();
            currentState = AdventureStatesTypes.ROLL_DICES;
            cannonadesPhase = true;
        }
    }

    private void tryNextPlayerDefeated() {
        int currentIndexOfDefeatedPlayer = defeatedPlayers.indexOf(gameBoard.getCurrentPlayer());
        if (currentIndexOfDefeatedPlayer >= 0 && currentIndexOfDefeatedPlayer < defeatedPlayers.size() - 1) { //i have to apply the currenteffect on all the players
            gameBoard.setCurrentPlayer(defeatedPlayers.get(currentIndexOfDefeatedPlayer + 1));
            System.out.println("Now effect on the next player");
            checkForBatteriesToUseForShields();
        } else {    //i have applied the effect on all the players
            if (listEffects.isEmpty()) {  //effects are terminated, card is finished
                System.out.println("All the effects are finished, now to update");
                update();
            } else {      //effects are not finished, first player has to roll the dices on the next effect
                gameBoard.setCurrentPlayer(defeatedPlayers.getFirst());
                currentEffect = listEffects.removeFirst();
                System.out.println("Next effect: " + currentEffect.getDescription());
                System.out.println("Now the first defeated must roll the dices again!");
                currentState = AdventureStatesTypes.ROLL_DICES;
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
            applyEffect();
        }
    }

    private void checkDimensionOfCannonade() {
        if (currentEffect.getLevel() == BIG_CANNONADE_LEVEL) {//big cannonade, player cant defend himself
            System.out.println("Cannonata grande! Non puoi difenderti con gli scudi!");
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
            return;
        }
        ShipBoard currentShipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        long availableShields = currentShipBoard.getActivatableComponents(ComponentType.SHIELD).stream().count();

        if (availableShields > 0) {
            System.out.println("Puoi attivare gli scudi. Scegli le coordinate delle batterie da utilizzare.");
            currentState = AdventureStatesTypes.DECISION_BATTERIES;
        } else {
            System.out.println("Non hai scudi da attivare. Subisci l'effetto della cannonata.");
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
        }
    }

    @Override
    protected int getMaxActivatableComponents(ShipBoard shipBoard) {
        if (cannonadesPhase) { //we are in the effect phase, when players are already defeated
            return shipBoard.getActivatableComponents(ComponentType.SHIELD).size();
        } else {
            return shipBoard.getActivatableComponents(ComponentType.DOUBLE_CANNON).size();
        }
    }

    @Override
    public void applyEffect() {
        currentEffect.action(gameBoard.getCurrentPlayer());
        deactivateComponents(gameBoard.getCurrentPlayer().getShipBoard());
        coordinatesOfActivable = new ArrayList<>();
        tryNextPlayerDefeated();
    }

    @Override
    public List<Player> getDefeatedPlayers() {
        return defeatedPlayers;
    }
}
