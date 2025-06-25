package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectModifyCrew;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an adventure card that depicts an abandoned ship scenario.
 * This card determines specific game mechanics when drawn,
 * affecting astronauts, credits, and days lost during gameplay.
 */
public class AbandonedShipCard extends AdventureCard {

    /**
     * Represents the number of astronauts lost when an abandoned ship card effect is applied.
     * This field is used in the context of the card's gameplay to determine the impact of the card on the game state.
     */
    protected int lostAstronauts;
    /**
     * Represents the number of credits a player has earned from the encounter with the abandoned ship.
     * This value is associated with the rewards or outcomes tied to the specific card.
     */
    protected int creditsEarned;
    /**
     * Represents the number of days lost as a consequence of an event or action
     * described in the context of the card. This field indicates a penalty in terms
     * of game progression measured by a decrease in available in-game days.
     */
    protected int daysLost;
    /**
     * Represents a list of effects associated with a card in the game.
     * Each effect is an instance of the {@link CardEffect} class and encapsulates a specific game-related action
     * or event that can be applied to a player or game state. The list allows for multiple effects to be
     * associated with a single card, enabling complex interactions and layered game mechanics.
     */
    List<CardEffect> listEffects = new ArrayList<>();

    /**
     * Constructs an AbandonedShipCard with its associated effects.
     *
     * @param gameBoard       The game board to which the card belongs.
     * @param description     A description of the abandoned ship card.
     * @param level           The difficulty level or type of the card.
     * @param name            The name of the abandoned ship card.
     * @param dayslost        The number of days lost when encountering this card.
     * @param creditsEarned   The amount of credits earned from this card.
     * @param lostAstronauts  The number of astronauts lost due to this card.
     */
    public AbandonedShipCard(GameBoard gameBoard, String description, int level, String name, int dayslost, int creditsEarned, int lostAstronauts) {
        super(gameBoard, description, level, name);
        this.lostAstronauts = lostAstronauts;
        this.creditsEarned = creditsEarned;
        this.daysLost = dayslost;
        listEffects.add(new EffectModifyCrew("Remove astronauts", lostAstronauts));
    }

    /**
     * Executes the drawCard behavior specific to the AbandonedShipCard.
     * This method overrides the base drawCard method of the AdventureCard class.
     */
    @Override
    public void drawCard() {
        super.drawCard();
        System.out.println("You have found an abandoned ship! You can sell it to your crew members for some credits");
        checkRequirements();
    }

    /**
     * Checks if the current player meets the requirements to proceed with the action on the Abandoned Ship adventure card.
     * Specifically, it determines if the player has a sufficient number of crew members
     * (including astronauts, brown aliens, and purple aliens) to land on the ship.
     *
     * If the player has enough crew members, the function updates the game state to allow the player
     * to decide whether to land on the ship. A message is displayed informing the player of their crew count,
     * the potential credits earned, and the option to land.
     * The function also checks for possible effects or connections related to the specified game state.
     *
     * If the player does not have enough crew members, a message is displayed, and the turn is passed to the next player.
     */
    @Override
    public void checkRequirements() {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        int numCrew = shipBoard.getTotalNumberOfaToken(Token.ASTRONAUT) + shipBoard.getTotalNumberOfaToken(Token.BROWN_ALIENS) + shipBoard.getTotalNumberOfaToken(Token.PURPLE_ALIENS);
        if (numCrew >= lostAstronauts) {
            System.out.println("You have " + numCrew + " astronauts on your ship. If you decide to land on the ship, you win " + creditsEarned + " credits but if your crew is too small you have to quit");
            currentState = AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP);
        } else {
            System.out.println("You don't have enough astronauts to land on the ship");
            tryNextPlayer();
        }
    }

    /**
     * Manages the landing decision on a station or ship and performs corresponding actions
     * depending on the player's choice. If the player decides to land, the state of the
     * game is updated to allow further decision-making. If the player decides not to land,
     * the game advances to the next player's turn.
     *
     * @param isLanded a boolean indicating whether the player has chosen to land on the
     *                 station or ship (true if they choose to land, false otherwise)
     * @throws IllegalMethodOnThisStateException if the method is called in an incorrect
     *                                           state of the game
     */
    @Override
    public void landingOnStationOrShip(boolean isLanded) throws IllegalMethodOnThisStateException {
        super.landingOnStationOrShip(isLanded);
        if (isLanded) {
            System.out.println("You have landed on the ship");
            System.out.println("Now you have to decide where you want to remove your astronauts");
            currentEffect = listEffects.getFirst();
            currentState = AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS);
        } else {
            System.out.println("You have decided not to land on the ship");
            tryNextPlayer();
        }
    }

    /**
     * Attempts to switch to the next player in the game. If the transition is successful,
     * the game proceeds to check the requirements for the new player. If an exception is
     * thrown due to the inability to set the next player, the update process is triggered.
     */

    private void tryNextPlayer() {
        try {
            gameBoard.nextPlayer();
            System.out.println("Now check on next player");
            checkRequirements();
        } catch (IllegalSetOfNextPlayerException e) {
            System.out.println("No more players, now to update");
            update();
        }
    }

    /**
     * Processes and finalizes the decision involving selected coordinates and tokens on the ship board.
     * Depending on the current state, validates the input data and transitions the game state accordingly.
     * Applies the associated effect when transitioning to the effect state.
     *
     * @param coordinates the list of pairs consisting of points and tokens representing the chosen locations
     *                    and items on the ship board for the current decision.
     * @throws IllegalMethodOnThisStateException if the method is invoked in an invalid game state.
     */
    @Override
    public void finishDecision(List<Pair<Point, Token>> coordinates) {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        checkCorrectnessOfTokensAndPoints(currentState, coordinates, shipBoard);
        switch (currentState) {
            case DECISION_ASTRONAUTS_OR_ALIENS -> {
                System.out.println("You have decided to remove astronauts");
                currentEffect.setListOfCoordinates(coordinates);
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
            default -> throw new IllegalMethodOnThisStateException("Wrong state");
        }
    }

    /**
     * Applies the effect of the "Abandoned Ship Card" to the current player.
     */
    @Override
    public void applyEffect() {
        super.applyEffect();
        Player currentPlayer = gameBoard.getCurrentPlayer();
        System.out.println("You are in applyeffect, you lose astronauts");
        currentEffect.action(currentPlayer);
        currentPlayer.addCredits(creditsEarned);
        System.out.println("You have earned " + creditsEarned + " credits");
        currentPlayer.setDaysThisTurn(-daysLost);
        update();
    }

    /**
     * Returns a formatted string representation of the object, displaying its attributes
     * in a structured box layout with proper alignment and text wrapping for descriptions.
     *
     * @return a string representation of the object with formatted attributes such as
     *         name, description, level, lost astronauts, credits earned, and days lost.
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
        String lostAstronautsText = "| LOST ASTRONAUTS: " + lostAstronauts;
        str.append(lostAstronautsText).append(" ".repeat(width - lostAstronautsText.length() - 1)).append("|\n");
        String creditsText = "| CREDITS EARNED: " + creditsEarned;
        str.append(creditsText).append(" ".repeat(width - creditsText.length() - 1)).append("|\n");
        String daysLostText = "| DAYS LOST: " + daysLost;
        str.append(daysLostText).append(" ".repeat(width - daysLostText.length() - 1)).append("|\n");
        str.append(border);
        return str.toString();
    }

}
