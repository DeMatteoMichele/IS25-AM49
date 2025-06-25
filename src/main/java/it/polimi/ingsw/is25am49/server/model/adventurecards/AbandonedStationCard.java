package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectWinGoods;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisCardException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Represents an adventure card for an abandoned station encounter in the game.
 *
 * The behavior of this card includes drawing, checking requirements, managing
 * landing decisions, applying effects tied to winning goods, and updating the game state
 * accordingly based on the player's decisions and card effects.
 *
 * Inherits from {@link AdventureCard}.
 */
public class AbandonedStationCard extends AdventureCard {
    protected final ArrayList<Token> Prize = new ArrayList<>();

    private final List<CardEffect> listEffects;
    private final int daysLost;
    private final int lostAstronauts;

    /**
     * Constructs an AbandonedStationCard with the specified parameters.
     *
     * @param gameBoard the game board on which the card operates
     * @param description the description of the card
     * @param level the level of the card
     * @param name the name of the card
     * @param daysLost the number of days lost when this card is used
     * @param minimumAstronauts the minimum number of astronauts required
     * @param redGoods the number of red goods rewarded
     * @param yellowGoods the number of yellow goods rewarded
     * @param greenGoods the number of green goods rewarded
     * @param blueGoods the number of blue goods rewarded
     */
    public AbandonedStationCard(GameBoard gameBoard, String description, int level, String name, int daysLost, int minimumAstronauts, int redGoods, int yellowGoods, int greenGoods, int blueGoods) {
        super(gameBoard, description, level, name);
        this.daysLost = daysLost;
        this.lostAstronauts = minimumAstronauts;
        listEffects = new ArrayList<>();
        listEffects.add(new EffectWinGoods("Win Goods", redGoods, yellowGoods, greenGoods, blueGoods));
    }

    /**
     * Draws the current card and performs specific actions for the Abandoned Station card.
     */
    @Override
    public void drawCard() {
        super.drawCard();
        System.out.println("You have found an Abandoned Station! You can land on it for some goods.");
        checkRequirements();
    }


    /**
     * Checks if the current player meets the requirements to proceed with the abandoned station card action.
     * The method evaluates the total number of crew members (including astronauts, brown aliens, and purple aliens)
     * on the player's shipboard to determine whether the player can land on the station. If the number of crew members
     * is equal to or greater than the required amount, it transitions the game state to allow the player
     * to decide whether to land on the station or the ship. Otherwise, it moves to the next player.
     */
    @Override
    public void checkRequirements() {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumCrewMembers = ship.getTotalNumberOfaToken(Token.ASTRONAUT) + ship.getTotalNumberOfaToken(Token.BROWN_ALIENS) + ship.getTotalNumberOfaToken(Token.PURPLE_ALIENS);
        System.out.println("player " + gameBoard.getCurrentPlayer().getUsername() + " has " + totalNumCrewMembers + " crew members");
        if (totalNumCrewMembers >= lostAstronauts) {
            System.out.println("Do you wanna land on the station for some goods?");
            currentState = AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP);
        } else {
            System.out.println("You don't have enough members to land on the station!");
            try {
                gameBoard.nextPlayer();
                System.out.println("Now check on next player");
                checkRequirements();
            } catch (IllegalSetOfNextPlayerException e) {
                System.out.println("It goes to update state");
                update();
            }
        }
    }

    /**
     * Handles the decision process for landing on a station or ship during the game.
     * This method updates the game state and executes the appropriate actions based
     * on whether the player decides to land or not.
     *
     * @param IsLanded boolean indicating the player's decision to land on the station or ship.
     *                 If true, the player lands; if false, the player skips the landing.
     * @throws IllegalMethodOnThisStateException if this method is called in an incorrect game state.
     */
    @Override
    public void landingOnStationOrShip(boolean IsLanded) throws IllegalMethodOnThisStateException {
        super.landingOnStationOrShip(IsLanded);
        if (IsLanded) {
            System.out.println("You have landed on A station");
            System.out.println("Now you have to decide where you want to add your win goods and which ones");
            currentState = AdventureStatesTypes.DECISION_GOODS;
            currentEffect = listEffects.getFirst();
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_GOODS);
        } else {
            System.out.println("You have decided not to land on the station ");
            try {
                gameBoard.nextPlayer();
                System.out.println("Now check on next player");
                checkRequirements();
            } catch (IllegalSetOfNextPlayerException e) {
                System.out.println("No more players, now to update");
                update();
            }
        }
    }

    /**
     * Completes the decision-making process for the current state of the game.
     * This method validates the provided coordinates and updates the game state accordingly.
     *
     * @param coordinates a list of pairs where each pair consists of a {@code Point} and a {@code Token}, representing the selected coordinates and their respective tokens in the
     *  decision-making process.
     * @throws IllegalMethodOnThisStateException if the method is invoked in an incorrect game state.
     */
    @Override
    public void finishDecision(List<Pair<Point, Token>> coordinates) {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        checkCorrectnessOfTokensAndPoints(currentState, coordinates, shipBoard);
        switch (currentState) {
            case DECISION_GOODS -> {
                System.out.println("You have decided your goods to add ");
                currentEffect.setListOfCoordinates(coordinates);
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
            default -> throw new IllegalMethodOnThisStateException("Wrong state");
        }
    }

    /**
     * Applies the effect of the AbandonedStationCard to the current player in the game.
     * This method overrides the `applyEffect` method from its superclass and executes
     * additional functionality specific to the AbandonedStationCard.
     */
    @Override
    public void applyEffect() {
        super.applyEffect();
        Player player = gameBoard.getCurrentPlayer();
        System.out.println("You are winning goods but you lost " + daysLost + " travel days");
        currentEffect.action(player);
        player.setDaysThisTurn(-daysLost);
        update();
    }

    /**
     * Converts the AbandonedStationCard object into a formatted string representation.
     * The string representation includes details such as the name, description, level,
     * minimum astronauts, days lost, and potential rewards in a structured box layout.
     *
     * @return a string containing a formatted representation of the card's details
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
        String astronautText = "| MINIMUM ASTRONAUTS: " + lostAstronauts;
        str.append(astronautText).append(" ".repeat(width - astronautText.length() - 1)).append("|\n");
        String daysText = "| DAYS LOST: " + daysLost;
        str.append(daysText).append(" ".repeat(width - daysText.length() - 1)).append("|\n");
        str.append(separator);
        str.append("| WIN GOODS:").append(" ".repeat(width - "| WIN GOODS:".length() - 1)).append("|\n");
        if (!listEffects.isEmpty() && listEffects.get(0) instanceof EffectWinGoods) {
            EffectWinGoods goodsEffect = (EffectWinGoods) listEffects.get(0);
            String redText = "|   - Red goods: " + goodsEffect.getRedGoods();
            str.append(redText).append(" ".repeat(width - redText.length() - 1)).append("|\n");
            String yellowText = "|   - Yellow goods: " + goodsEffect.getYellowGoods();
            str.append(yellowText).append(" ".repeat(width - yellowText.length() - 1)).append("|\n");
            String greenText = "|   - Green goods: " + goodsEffect.getGreenGoods();
            str.append(greenText).append(" ".repeat(width - greenText.length() - 1)).append("|\n");
            String blueText = "|   - Blue goods: " + goodsEffect.getBlueGoods();
            str.append(blueText).append(" ".repeat(width - blueText.length() - 1)).append("|\n");
            str.append(border);
        }
        return str.toString();
    }
}
