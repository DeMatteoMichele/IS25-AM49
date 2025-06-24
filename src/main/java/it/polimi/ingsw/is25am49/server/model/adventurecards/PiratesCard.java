package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectCannonata;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;

import java.util.ArrayList;
import java.util.List;

/**
 * PiratesCard represents an enemy card within the game, extending the base functionality
 * of the {@link Enemy} class. This card introduces the challenge of battling pirates,
 * handling cannonades, and rewarding the player upon victory or penalizing them upon defeat.
 *
 * The card includes properties such as credits earned, days lost upon defeat, and the ability
 * to handle complex multi-player battle scenarios. Additionally, it manages a sequence of effects
 * that players encounter during the battle phase.
 */
public class PiratesCard extends Enemy {

    /**
     * Represents the number of credits earned by the player during the course
     * of the game or a specific phase. This variable is used to track rewards
     * or achievements gained through successful gameplay actions.
     */
    protected int creditsEarned;

    /**
     * Constructs a PiratesCard object, representing an enemy card with specific attributes and effects.
     * This card interacts with the game board and represents a challenge to players utilizing its effects
     * and properties.
     *
     * @param gameBoard        The game board instance where the PiratesCard is used.
     * @param description      A description of the PiratesCard, detailing its effects or specifics.
     * @param level            The level of the PiratesCard, indicating its difficulty or strength.
     * @param name             The name of the PiratesCard.
     * @param dayslost         The number of days lost when encountering this card.
     * @param minimumFirePower The minimum firepower required to overcome this card.
     * @param creditsEarned    The credits awarded to players upon overcoming the card.
     * @param listEffects      A list of effects that this PiratesCard applies during the game.
     */
    public PiratesCard(GameBoard gameBoard, String description, int level, String name, int dayslost, int minimumFirePower, int creditsEarned, List<CardEffect> listEffects) {
        super(gameBoard, description, level, name, dayslost, minimumFirePower);
        this.listEffects.addAll(listEffects);
        this.creditsEarned = creditsEarned;
    }


    /**
     * A list containing the players who have been defeated during the adventure or battle.
     * Provides a way to track all defeated players in the game in the context
     * of the current card's execution.
     */
    private final List<Player> defeatedPlayers = new ArrayList<>();
    /**
     * Represents the current phase of the cannonades within the PiratesCard gameplay logic.
     * This variable indicates whether the game is currently in the cannonades phase or not.
     * In this phase, specific game mechanics related to cannon fire and ship battles are evaluated.
     *
     * Initialized to false, it should be set to true when transitioning to or during the cannonades phase.
     */
    private boolean cannonadesPhase = false;
    /**
     * Represents the level associated with the "Big Cannonade" phase in the game mechanics of the PiratesCard.
     * This constant defines the difficulty or magnitude of the cannonade event within the game.
     * It is expected to remain static and unchangeable throughout the execution of the game.
     */
    private static final int BIG_CANNONADE_LEVEL = 2;


    /**
     * Evaluates the outcome of a battle based on the ship's state and player's performance.
     * Adjusts the game state and components accordingly by determining whether the player wins, draws,
     * or loses the battle.
     *
     * @param shipBoard the current state of the player's ship board, used to assess the battle results
     */
    @Override
    protected void evaluateBattleOutcome(ShipBoard shipBoard) {
        if (checkWinFirePower(shipBoard)) {
            deactivateComponents(shipBoard, coordinatesOfActivable);
            currentState = AdventureStatesTypes.DECISION_REWARDS;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_REWARDS);
        } else if (checkDrawFirePower(shipBoard)) {
            deactivateComponents(shipBoard, coordinatesOfActivable);
            tryNextPlayerOrHandleDefeatedPlayers();
        } else {
            deactivateComponents(shipBoard, coordinatesOfActivable);
            defeatedPlayers.add(gameBoard.getCurrentPlayer());
            tryNextPlayerOrHandleDefeatedPlayers();
        }
    }

    /**
     * Determines the next adventure state based on the current situation of the cannonades phase.
     */
    @Override
    protected AdventureStatesTypes determineStateAfterBatteries() {
        return cannonadesPhase ? AdventureStatesTypes.DECISION_SHIELDS : AdventureStatesTypes.DECISION_CANNON;
    }

    /**
     * Handles the situation where a ship's battery is empty during a cannonades phase or otherwise.
     * Determines the next state or applies relevant effects based on the game conditions.
     *
     * @param shipBoard the current state and configuration of the ship under evaluation,
     *                  used to determine the appropriate actions or outcomes.
     */
    @Override
    protected void handleEmptyBatteryDecision(ShipBoard shipBoard) {
        if (!cannonadesPhase)
            evaluateBattleOutcome(shipBoard);
        else {
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
        }
    }

    /**
     * Handles the reward distribution and penalty application for the current player
     * after an adventure card event in the game.
     */
    @Override
    protected void handleRewards() {
        gameBoard.getCurrentPlayer().addCredits(creditsEarned);
        gameBoard.getCurrentPlayer().setDaysThisTurn(-daysLost);
        handleDefeatedPlayers();
    }

    /**
     * Handles the scenario when rewards are refused by the player(s) during the game.
     * This method invokes the logic for managing defeated players by delegating the process
     * to the {@code handleDefeatedPlayers} method. This may involve updating the game state
     * or setting up the next actions for players who have faced defeat in the current phase.
     */
    @Override
    protected void handleRefuseOfRewards() {
        handleDefeatedPlayers();
    }

    /**
     * Attempts to set the turn for the next player in the game. If an exception occurs while
     * advancing to the next player, it handles the defeated players in the game.
     */
    protected void tryNextPlayerOrHandleDefeatedPlayers() {
        try {
            gameBoard.nextPlayer();
            System.out.println("Now check on next player");
            checkRequirements();
        } catch (IllegalSetOfNextPlayerException e) {
            handleDefeatedPlayers();
        }
    }

    /**
     * Handles the situation when there are players marked as defeated in the game.
     *
     * If the list of defeated players is empty, the method performs an update and transitions
     * the game to the "UPDATE" state. Otherwise, it sets the first defeated player as the
     * current player and applies effects accordingly to handle their game state and transition
     * to the next phase.
     */
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
            checkConnectionOrDefaultEffect(AdventureStatesTypes.ROLL_DICES);
        }
    }

    /**
     * Handles the transition and effect application for the next defeated player in the game.
     *
     * This method determines whether there are remaining defeated players who need to
     * undergo the current effect or subsequent effects. Depending on the state of the
     * defeated players and the effect list, the method either:
     * 1. Moves to the next defeated player and applies the current effect.
     * 2. Restarts the effect sequence with the first defeated player if there are remaining effects.
     * 3. Updates the game state if all effects are completed.

     */
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
                System.out.println("Now the first defeated must roll the dices again!");
                currentState = AdventureStatesTypes.ROLL_DICES;
                checkConnectionOrDefaultEffect(AdventureStatesTypes.ROLL_DICES);
            }
        }
    }

    /**
     * Rolls the dice for the current adventure phase and resolves the outcome.
     * <ul>
     *   <li>Delegates the dice rolling logic to the parent class method.</li>
     *   <li>Checks the availability of batteries on the player's ship for potential shield usage.</li>
     * </ul>
     * This method manages the transition of the game state after dice rolling,
     * depending on the player's resources and the current phase of the adventure.
     */
    @Override
    public void rollDices() {
        super.rollDices();
        checkForBatteriesToUseForShields();
    }

    /**
     * Checks if the current player has batteries available to use for activating shields.
     * If batteries are available, it proceeds to verify the dimension of the cannonade.
     * Otherwise, it sets the current game state to apply the effect of the attack.
     *
     * The following actions are performed:
     * - If the player has battery tokens available on their ship board:
     *   - Calls {@link #checkDimensionOfCannonade()} to handle the cannonade defense process.
     * - If the player has no battery tokens available:
     *   - Updates the current state to {@link AdventureStatesTypes#EFFECT}.
     *   - Calls {@link #applyEffect()} to apply the effect resulting from the lack of defense.
     */
    private void checkForBatteriesToUseForShields() {
        boolean hasBatteries = gameBoard.getCurrentPlayer().getShipBoard().getTotalNumberOfaToken(Token.BATTERY_CELLS) > 0;
        if (hasBatteries) {  // player has batteries for shields
            checkDimensionOfCannonade();
        } else {  // player has no batteries for shields
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
        }
    }

    /**
     * Checks the dimension of the cannonade effect and determines the necessary actions
     * for the player based on the availability of shield components.
     *
     * This method evaluates whether the current cannonade effect corresponds to a big cannonade
     * level or if the player has activatable shields. Depending on the scenario, it transitions
     * the game state accordingly or applies the effect directly.
     *
     * If the cannonade level is identified as a big cannonade, the player is notified
     * that they cannot defend with shields, and the effect is applied immediately.
     *
     * If it is not a big cannonade, the method checks the player's ship board for available shields:
     *  - If shields are available, the player is prompted to choose shield components to activate.
     *  - If no shields are available, the cannonade effect is applied without a defense.
     *
     * The game state transitions to either allowing shield activation or directly applying the effect.
     */
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
            coordinatesOfActivable = currentShipBoard.getActivatableComponents(ComponentType.SHIELD);
        } else {
            System.out.println("Non hai scudi da attivare. Subisci l'effetto della cannonata.");
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
        }
    }

    /**
     * Applies the current effect of the card to the current player on the game board.
     */
    @Override
    public void applyEffect() {
        currentEffect.action(gameBoard.getCurrentPlayer());
        deactivateComponents(gameBoard.getCurrentPlayer().getShipBoard(), coordinatesOfActivable);
        coordinatesOfActivable = new ArrayList<>();
        tryNextPlayerDefeated();
    }

    /**
     * Retrieves the list of defeated players.
     *
     * @return a list of Player objects representing the players who have been defeated.
     */
    @Override
    public List<Player> getDefeatedPlayers() {
        return defeatedPlayers;
    }

    /**
     * Converts the current PiratesCard object into a formatted string representation.
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
        if(currentEffect!=null){
            EffectCannonata cannonata = (EffectCannonata) currentEffect;
            String levelCannonata = "| LEVEL: " + cannonata.getLevel();
            str.append(levelCannonata).append(" ".repeat(width - levelCannonata.length() - 1)).append("|\n");
            String orientationCannonata = "| ORIENTATION: " + cannonata.getOrientation();
            str.append(orientationCannonata).append(" ".repeat(width - orientationCannonata.length() - 1)).append("|\n");
            str.append(border);
        }
        for (CardEffect effect : listEffects){
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
