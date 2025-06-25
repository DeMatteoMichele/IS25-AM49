package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectWinGoods;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalPlanetToLand;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalSetOfNextPlayerException;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PlanetCard extends AdventureCard {

    /**
     * A list of planets associated with the card.
     * Each planet contains specific properties such as
     * effects, occupation status, and unique identifiers.
     * This list is immutable and cannot be reassigned.
     */
    protected final List<Planet> planets = new ArrayList<Planet>();
    /**
     * The planet associated with the current PlanetCard.
     * Represents a celestial body with specific effects,
     * occupation state, and a unique identifier in the game.
     */
    protected Planet planet;

    /**
     * Represents the number of days lost, typically due to an in-game event or effect.
     */
    private final int daysLost;

    /**
     * Constructs a PlanetCard with the specified parameters.
     *
     * @param gameBoard the game board associated with the card
     * @param description a brief description of the card
     * @param level the difficulty level of the card
     * @param name the name of the card
     * @param daysLost the number of days lost when the card is triggered
     * @param planets a list of planets associated with the card
     */
    public PlanetCard(GameBoard gameBoard, String description, int level, String name, int daysLost, List<Planet> planets) {
        super(gameBoard, description, level, name);
        this.daysLost = daysLost;
        this.planets.addAll(planets);
    }

    /**
     * Overrides the drawCard method to display a unique message and initiate a planetary event.
     * Calls the superclass's drawCard implementation to update game state and current player.
     * Triggers additional steps by invoking checkRequirements to determine further actions.
     */
    @Override
    public void drawCard() {
        super.drawCard();
        System.out.println("You have found a planet! You can land on it for some goods.");
        checkRequirements();
    }


    /**
     * Checks the requirements for proceeding in the adventure.
     * Updates the current state to prompt a decision about landing on a planet.
     * Invokes a connection check or applies a default effect
     * based on the DECISION_LAND_PLANET state.
     */
    @Override
    public void checkRequirements() {
        System.out.println("Do you wanna land on the planet for some goods?");
        currentState = AdventureStatesTypes.DECISION_LAND_PLANET;
        checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_LAND_PLANET);
    }

    /**
     * Handles the action of landing on a planet, updating the game state
     * depending on whether the player lands or decides not to land.
     *
     * @param isLanded Indicates whether the player has chosen to land on the planet.
     * @param planetId The unique identifier of the planet being landed on.
     * @throws IllegalPlanetToLand If the planet does not exist or is already occupied.
     */
    @Override
    public void landingOnPlanet(boolean isLanded, int planetId) {
        super.landingOnPlanet(isLanded, planetId);
        if (isLanded) {
            planet = getPlanetById(planetId);
            if (!planets.contains(planet)) {
                throw new IllegalPlanetToLand("You cannot land on this planet, it doesnt exist or it is already occupied");
            }
            if (!planet.isOccupied()) {
                System.out.println(gameBoard.getCurrentPlayer().getUsername() + " You have landed on the planet!");
                planet.setOccupied(true);
                planets.remove(planet);
                planet.setPlayernumber(gameBoard.getCurrentPlayer().getPlayerNumber());
                System.out.println("You have to choose the position of the goods you won:");
                currentState = AdventureStatesTypes.DECISION_GOODS;
                currentEffect = planet.getEffect();
                checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_GOODS);
            }
        } else {
            System.out.println("you have decided not to land on the planet");
            System.out.println("Now check on next player");
            checkNextPlayer();
        }
    }

    /**
     * Retrieves a planet from the list of planets by its unique identifier.
     *
     * @param planetId the unique identifier of the planet to be retrieved
     * @return the Planet object matching the given identifier
     * @throws IllegalPlanetToLand if no planet with the specified identifier exists
     */
    public Planet getPlanetById(int planetId) {
        for (Planet planet : planets) {
            if (planet.getPlanetId() == planetId) {
                return planet;
            }
        }
        throw new IllegalPlanetToLand("You cannot land on this planet the id is not valid");
    }

    /**
     * Checks and transitions to the next player in the game.
     * If there are remaining players, continues the turn and validates requirements.
     * If the last player has acted, transitions to the update phase.
     *
     * Handles potential exceptions when setting the next player.
     */
    private void checkNextPlayer() {
        try {
            gameBoard.nextPlayer();
            System.out.println("it wasn't the last player, go on");
            checkRequirements();
        } catch (IllegalSetOfNextPlayerException e) {
            System.out.println("It was the last player, end of the turn");
            System.out.println("it goes to update state");
            update();
        }
    }

    /**
     * Handles the decision-making process for goods placement during the game.
     * This method validates the provided coordinates and updates the game state accordingly.
     *
     * @param coordinates a list of pairs, where each pair consists of a Point indicating
     *                    the position and a Token representing the associated item or entity.
     * @throws IllegalMethodOnThisStateException if the method is called while in an invalid state.
     */
    @Override
    public void finishDecision(List<Pair<Point, Token>> coordinates) {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        checkCorrectnessOfTokensAndPoints(currentState, coordinates, shipBoard);
        if (currentState == AdventureStatesTypes.DECISION_GOODS) {
            System.out.println("You have decided the position of the goods");
            currentEffect.setListOfCoordinates(coordinates);
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
        } else {
            throw new IllegalMethodOnThisStateException("Wrong state");
        }
    }


    /**
     * Applies the effect specific to the PlanetCard for the current player.

     */
    @Override
    public void applyEffect() {
        super.applyEffect();
        System.out.println("You are in apply effect of planet card");
        currentEffect.action(gameBoard.getCurrentPlayer());
        gameBoard.getCurrentPlayer().setDaysThisTurn(-daysLost);
        System.out.println("You have lost " + daysLost + " travel days");
        if (planets.isEmpty()) {
            System.out.println("You have no more planets to land on, you have to go on with the game");
            update();
            return;
        }
        checkNextPlayer();
    }


    /**
     * Constructs and returns a formatted string representation of the PlanetCard.
     * Includes details such as name, description, level, days lost, and
     * information about associated planets and their effects.
     *
     * @return a string representation of the PlanetCard with formatted details.
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
        String daysLostText = "| DAYS LOST: " + daysLost;
        str.append(daysLostText).append(" ".repeat(width - daysLostText.length() - 1)).append("|\n");
        str.append(separator);
        for (Planet planet : planets) {
            String planetText = "| PLANET: " + planet.getPlanetId();
            str.append(planetText).append(" ".repeat(width - planetText.length() - 1)).append("|\n");
            EffectWinGoods effect = (EffectWinGoods) planet.getEffect();
            String redGoods = "| RED GOODS: " + effect.getRedGoods();
            str.append(redGoods).append(" ".repeat(width - redGoods.length() - 1)).append("|\n");
            String yellowGoods = "| YELLOW GOODS: " + effect.getYellowGoods();
            str.append(yellowGoods).append(" ".repeat(width - yellowGoods.length() - 1)).append("|\n");
            String blueGoods = "| BLUE GOODS: " + effect.getBlueGoods();
            str.append(blueGoods).append(" ".repeat(width - blueGoods.length() - 1)).append("|\n");
            String greenGoods = "| GREEN GOODS: " + effect.getGreenGoods();
            str.append(greenGoods).append(" ".repeat(width - greenGoods.length() - 1)).append("|\n");
            str.append(border);
        }
        return str.toString();
    }


}
