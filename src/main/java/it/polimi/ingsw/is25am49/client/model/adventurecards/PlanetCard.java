package it.polimi.ingsw.is25am49.client.model.adventurecards;

import it.polimi.ingsw.is25am49.client.model.GameBoard;
import it.polimi.ingsw.is25am49.client.model.cardeffects.EffectWinGoods;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalPlanetToLand;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalSetOfNextPlayerException;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PlanetCard extends AdventureCard {

    protected final List<Planet> planets = new ArrayList<Planet>();
    protected Planet planet;

    private final int daysLost;

    public PlanetCard(String cardId, GameBoard gameBoard, String description, int level, String name, int daysLost, List<Planet> planets) {
        super(cardId, gameBoard, description, level, name);
        this.daysLost = daysLost;
        this.planets.addAll(planets);
        for (Planet planet : planets) {
            planet.getEffect().setUserInterface(userInterface);
        }
    }
    public List<Planet> getPlanets() {
        return planets;
    }

    public int getNumOfPlanets() {
        return planets.size();
    }

    @Override
    public void drawCard() {
        super.drawCard();
        userInterface.updateGenericCardMessage(genericIdForUpdates, "You have found a planet! You can land on it for some goods.");
        checkRequirements();
    }


    @Override
    public void checkRequirements() {
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "Do you wanna land on the planet for some goods?");
        currentState = AdventureStatesTypes.DECISION_LAND_PLANET;
        userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
        checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_LAND_PLANET);
    }

    @Override
    public void landingOnPlanet(boolean isLanded, int planetId) {
        super.landingOnPlanet(isLanded, planetId);
        if (isLanded) {
            planet = getPlanetById(planetId);
            if (!planets.contains(planet)) {
                throw new IllegalPlanetToLand("You cannot land on this planet, it doesnt exist or it is already occupied");
            }
            if (!planet.isOccupied()) {
                userInterface.updateLandOnPlanet(gameBoard.getCurrentPlayer().getIdPlayer(), true, planetId);
                planet.setOccupied(true);
                planets.remove(planet);
                planet.setPlayernumber(gameBoard.getCurrentPlayer().getPlayerNumber());
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You have to choose the position of the goods you won:");
                currentState = AdventureStatesTypes.DECISION_GOODS;
                userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
                currentEffect = planet.getEffect();
                userInterface.updateNumberOfWinGoods(gameBoard.getCurrentPlayer().getIdPlayer(),currentEffect.getRedGoods(), currentEffect.getYellowGoods(), currentEffect.getGreenGoods(), currentEffect.getBlueGoods());
                checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_GOODS);
            }
        } else {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "you have decided not to land on the planet");
            checkNextPlayer();
        }
    }

    public Planet getPlanetById(int planetId) {
        for (Planet planet : planets) {
            if (planet.getPlanetId() == planetId) {
                return planet;
            }
        }
        throw new IllegalPlanetToLand("You cannot land on this planet the id is not valid");
    }

    private void checkNextPlayer() {
        try {
            gameBoard.nextPlayer();
            checkRequirements();
        } catch (IllegalSetOfNextPlayerException e) {
            userInterface.updateGenericCardMessage(genericIdForUpdates, "It was the last player, end of the card");
            update();
        }
    }

    @Override
    public void finishDecision(List<Pair<Point, Token>> coordinates) {
        if (currentState == AdventureStatesTypes.DECISION_GOODS) {
            userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(), coordinates, coordinates.size(), false);
            currentEffect.setListOfCoordinates(coordinates);
            currentState = AdventureStatesTypes.EFFECT;
            userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
            applyEffect();
        } else {
            throw new IllegalMethodOnThisStateException("Wrong state");
        }
    }


    @Override
    public void applyEffect() {
        super.applyEffect();
        currentEffect.action(gameBoard.getCurrentPlayer());
        gameBoard.getCurrentPlayer().setDaysThisTurn(-daysLost);
        if (planets.isEmpty()) {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "No more planets to land on!");
            update();
            return;
        }
        checkNextPlayer();
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
