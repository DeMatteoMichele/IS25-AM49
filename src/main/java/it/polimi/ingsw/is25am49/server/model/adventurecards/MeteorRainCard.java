package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectMeteoriti;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalNumOfActivableException;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents the "Meteor Rain" adventure card in the game that triggers a sequence of effects
 * where players must defend their ship against falling meteors using available batteries, shields, or cannons.
 */
public class MeteorRainCard extends AdventureCard {

    /**
     * A list containing all the card effects associated with the MeteorRainCard.
     * Each effect defines specific actions impacting the game state or player behavior.
     */
    List<CardEffect> listEffects = new ArrayList<>();

    /**
     * Constructs a MeteorRainCard with the specified parameters.
     *
     * @param gameBoard the game board associated with this card
     * @param description the description of the card
     * @param level the level of the card
     * @param name the name of the card
     * @param ListEffects the list of card effects to be added to this card
     */
    public MeteorRainCard(GameBoard gameBoard, String description, int level,String name, List<CardEffect> ListEffects) {
        super(gameBoard, description, level,name);
        listEffects.addAll(ListEffects);
    }

    /**
     * Represents the base level for the "Big Meteora" effect in the MeteorRainCard.
     * Used to determine the impact or size of the meteora during gameplay.
     */
    private static final int BIG_METEORA_LEVEL = 1;
    /**
     * Represents a list of pairs that associates a specific point on the game board
     * with a token, particularly used to track battery positions and their related tokens.
     */
    private List<Pair<Point, Token>> coordinatesOfBatteries = new ArrayList<>();
    /**
     * Stores the coordinates of activable points during the game's meteor rain card effect execution.
     * Used to track positions that can trigger actions or effects following specific conditions.
     */
    private List<Point> coordinatesOfActivable = new ArrayList<>();
    /**
     * Represents the list of players participating in the game.
     * This collection maintains the state of all players interacting with the card.
     * Immutable and final to ensure thread safety and consistency.
     */
    private final List<Player> listOfPlayers = new ArrayList<>();

    /**
     * Executes the "draw card" action for the MeteorRainCard. Triggers the meteor rain effect,
     * requiring the leader to roll dices. Updates the game state and handles connected players.
     * Removes the first effect from the effect list to apply it, changes the current state,
     * and ensures proper handling if a player is disconnected.
     */
    @Override
    public void drawCard() {
        super.drawCard();
        System.out.println("You have found a meteor rain! The leader must roll the dices");
        currentEffect = listEffects.removeFirst();
        listOfPlayers.addAll(gameBoard.getLeaderBoard().stream().map(Map.Entry::getKey).filter(player -> !gameBoard.getLeaderBoard().getDisconnectedPlayers().containsValue(player)).toList());
        currentState = AdventureStatesTypes.ROLL_DICES;
        checkConnectionOrDefaultEffect(AdventureStatesTypes.ROLL_DICES);
    }

    /**
     * Overrides the rollDices method to roll two dice and handle additional logic specific to the MeteorRainCard.
     * After invoking the superclass's rollDices, it checks if batteries are available to influence the outcome.
     */
    @Override
    public void rollDices() {
        super.rollDices();
        checkForBatteriesToUse();
    }

    /**
     * Checks if the current player has battery cells available to counteract
     * a potential threat (e.g., a meteor impact).
     * If batteries are available, it proceeds to further evaluation by checking
     * the dimension of the meteor. If not, the effect of the meteor is directly applied.
     *
     * Updates the state of the game based on the player's resources.
     */
    private void checkForBatteriesToUse() {
        coordinatesOfActivable = new ArrayList<>();
        coordinatesOfBatteries = new ArrayList<>();
        boolean hasBatteries = gameBoard.getCurrentPlayer().getShipBoard().getTotalNumberOfaToken(Token.BATTERY_CELLS) > 0;
        if (hasBatteries) {  // player has batteries
            checkDimensionOfMeteora();
        } else {  // player has no batteries
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
        }
    }

    /**
     * Evaluates the size of the meteor and determines the player's defensive options.
     *
     * If the meteor is classified as "big" (BIG_METEORA_LEVEL), the player cannot use shields
     * and must rely on cannons to defend. If cannons are available, prompts the player to choose
     * batteries to activate. Otherwise, applies the meteor's effect.
     *
     * If the meteor is not "big", the player can defend using shields if available. Prompts the
     * player to activate shields or applies the meteor's effect if no shields are present.
     *
     * Updates the game's current state based on the player's options and system's responses.
     */
    private void checkDimensionOfMeteora() {
        ShipBoard currentShipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        if (currentEffect.getLevel() == BIG_METEORA_LEVEL) {//big meteora, player cant defend himself
            long availableCannons = currentShipBoard.getActivatableComponents(ComponentType.DOUBLE_CANNON).size();
            System.out.println("Meteorite grande! Non puoi difenderti con gli scudi! Puoi usare un cannone per abbatterlo!");
            if (availableCannons > 0) {
                System.out.println("Puoi attivare i cannoni. Scegli le coordinate delle batterie da utilizzare.");
                currentState = AdventureStatesTypes.DECISION_BATTERIES;
                checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_BATTERIES);
            } else {
                System.out.println("Non hai cannoni da attivare. Subisci l'effetto del meteorite.");
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
        } else {
            long availableShields = currentShipBoard.getActivatableComponents(ComponentType.SHIELD).size();
            if (availableShields > 0) {
                System.out.println("Puoi attivare gli scudi. Scegli le coordinate delle batterie da utilizzare.");
                currentState = AdventureStatesTypes.DECISION_BATTERIES;
                checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_BATTERIES);
            } else {
                System.out.println("Non hai scudi da attivare. Subisci l'effetto del meteorite.");
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
        }
    }

    /**
     * Executes the final steps of the decision-making process based on the current game state
     * and provided coordinates. Handles specific logic for the DECISION_BATTERIES state.
     *
     * @param coordinates the list of pairs where each pair contains a point and a token,
     *                    representing the chosen tokens and their corresponding board positions.
     */
    @Override
    public void finishDecision(List<Pair<Point, Token>> coordinates) {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        checkCorrectnessOfTokensAndPoints(currentState,coordinates,shipBoard);
        switch (currentState) {
            case DECISION_BATTERIES -> {
                coordinatesOfBatteries = coordinates;
                checkNumberOfBatteries(coordinatesOfBatteries, shipBoard);
                if (coordinatesOfBatteries.isEmpty()) {
                    System.out.println("You have decided not to use batteries");
                    currentState = AdventureStatesTypes.EFFECT;
                    applyEffect();
                    return;
                }
                System.out.println("You have decided the coord for the batteries.");
                currentState = determineStateAfterBatteries();
                checkConnectionOrDefaultEffect(currentState);
            }
            default -> throw new IllegalMethodOnThisStateException("Invalid state: " + currentState);
        }
    }

    /**
     * Finalizes the decision for activating components based on the current state.
     * Handles specific actions for the DECISION_CANNON or DECISION_SHIELDS states
     * and transitions to the EFFECT state after activating components.
     *
     * @param coordinates the set of coordinates indicating the components to be activated
     * @throws IllegalNumOfActivableException if the number of activable components is not valid
     *        for the current state
     */
    @Override
    public void finishDecision(Set<Point> coordinates) throws IllegalNumOfActivableException {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        checkCorrectnessOfActivable(currentState,coordinates,shipBoard);
        switch (currentState) {
            case DECISION_CANNON -> {
                coordinatesOfActivable = new ArrayList<>(coordinates);
                activateComponentsWithBatteries(shipBoard, coordinatesOfActivable, coordinatesOfBatteries);
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
            case DECISION_SHIELDS -> {
                coordinatesOfActivable = new ArrayList<>(coordinates);
                activateComponentsWithBatteries(shipBoard, coordinatesOfActivable, coordinatesOfBatteries);
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
            default -> throw new IllegalMethodOnThisStateException("Invalid state: " + currentState);
        }
    }

    /**
     * Determines the next state of the adventure based on the current effect's level.
     * If the level corresponds to a big meteora, the state changes to DECISION_CANNON.
     * Otherwise, the state changes to DECISION_SHIELDS.
     *
     * @return the next adventure state as an AdventureStatesTypes enum value
     */
    private AdventureStatesTypes determineStateAfterBatteries() {
        if (currentEffect.getLevel() == BIG_METEORA_LEVEL) {
            return AdventureStatesTypes.DECISION_CANNON;
        } else {
            return AdventureStatesTypes.DECISION_SHIELDS;
        }
    }

    /**
     * Manages the transition to the next player or effect during the gameplay.
     * If the current player is not the last in the list, it proceeds to the next player.
     * Otherwise, checks the status of remaining effects:
     * - If no effects remain, triggers the update phase.
     * - If effects remain, sets the first player as the current player and initializes the next effect.
     * Adjusts the game state accordingly and may trigger dice rolls or battery checks.
     */
    private void tryNextPlayer() {
        int currentIndexPlayer = listOfPlayers.indexOf(gameBoard.getCurrentPlayer());
        if (currentIndexPlayer >= 0 && currentIndexPlayer < listOfPlayers.size() - 1) { //i have to apply the currenteffect on all the players
            gameBoard.setCurrentPlayer(listOfPlayers.get(currentIndexPlayer + 1));
            System.out.println("Now effect on the next player");
            checkForBatteriesToUse();
        } else {    //i have applied the effect on all the players
            if (listEffects.isEmpty()) {  //effects are terminated, card is finished
                System.out.println("All the effects are finished, now to update");
                update();
            } else {      //effects are not finished, first player has to roll the dices on the next effect
                gameBoard.setCurrentPlayer(listOfPlayers.getFirst());
                currentEffect = listEffects.removeFirst();
                System.out.println("Next effect: " + currentEffect.getDescription());
                System.out.println("Now the leader must roll the dices again!");
                currentState = AdventureStatesTypes.ROLL_DICES;
            }
        }
    }

    /**
     * Applies the current card effect to the current player, deactivates specified components
     * on their ship board, and progresses the game to the next player or effect.
     */
    @Override
    public void applyEffect() {
        currentEffect.action(gameBoard.getCurrentPlayer());
        deactivateComponents(gameBoard.getCurrentPlayer().getShipBoard(),coordinatesOfActivable);
        tryNextPlayer();
    }


    /**
     * Builds and returns a string representation of the MeteorRainCard object, formatted as a
     * structured box with aligned fields and optional wrapped text. Includes details
     * of the name, description, level, current effect, and additional card effects.
     *
     * @return a formatted string representing the state and attributes of the card
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
        str.append("| METEREORS:").append(" ".repeat(width - "| METEREORS:".length() - 1)).append("|\n");
        // Se c'è un effetto corrente, mostralo per primo
        if (currentEffect != null) {
            EffectMeteoriti meteoriti = (EffectMeteoriti) currentEffect;
            String levelMetereor = "|   - Meteor Level: " + meteoriti.getLevel();
            str.append(levelMetereor).append(" ".repeat(width - levelMetereor.length() - 1)).append("|\n");
            String orientationMetereor = "|   - Meteor Orientation: " + meteoriti.getOrientation();
            str.append(orientationMetereor).append(" ".repeat(width - orientationMetereor.length() - 1)).append("|\n");
            str.append(separator);
        }
        // Poi mostra gli effetti rimanenti
        for (CardEffect effect : listEffects) {
            EffectMeteoriti meteoriti = (EffectMeteoriti) effect;
            String levelMetereor = "|   - Meteor Level: " + meteoriti.getLevel();
            str.append(levelMetereor).append(" ".repeat(width - levelMetereor.length() - 1)).append("|\n");
            String orientationMetereor = "|   - Meteor Orientation: " + meteoriti.getOrientation();
            str.append(orientationMetereor).append(" ".repeat(width - orientationMetereor.length() - 1)).append("|\n");
            str.append(separator);
        }
        return str.toString();
    }

}
