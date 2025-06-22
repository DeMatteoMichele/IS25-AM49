package it.polimi.ingsw.is25am49.client.model.adventurecards;

import it.polimi.ingsw.is25am49.client.model.GameBoard;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.client.model.cardeffects.EffectModifyCrew;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AbandonedShipCard extends AdventureCard {

    protected int lostAstronauts;
    protected int creditsEarned;
    protected int daysLost;
    List<CardEffect> listEffects = new ArrayList<>();

    public AbandonedShipCard(String cardId, GameBoard gameBoard, String description, int level, String name, int dayslost, int creditsEarned, int lostAstronauts) {
        super(cardId, gameBoard, description, level, name);
        this.lostAstronauts = lostAstronauts;
        this.creditsEarned = creditsEarned;
        this.daysLost = dayslost;
        listEffects.add(new EffectModifyCrew("Remove astronauts", lostAstronauts));
        for (CardEffect effect : listEffects) {
            effect.setUserInterface(userInterface);
        }
    }

    @Override
    public void drawCard() {
        super.drawCard();
        userInterface.updateGenericCardMessage(genericIdForUpdates, "You have found an abandoned ship! You can sell it to your crew members for some credits");
        checkRequirements();
    }

    @Override
    public void checkRequirements() {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        int numCrew = shipBoard.getTotalNumberOfaToken(Token.ASTRONAUT) + shipBoard.getTotalNumberOfaToken(Token.BROWN_ALIENS) + shipBoard.getTotalNumberOfaToken(Token.PURPLE_ALIENS);
        if (numCrew >= lostAstronauts) {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You have " + numCrew + " astronauts on your ship. If you decide to land on the ship, you win " + creditsEarned + " credits but if your crew is too small you have to quit");
            currentState = AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP;
            userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP);
        } else {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You don't have enough astronauts to land on the ship");
            tryNextPlayer();
        }
    }

    @Override
    public void landingOnStationOrShip(boolean isLanded) throws IllegalMethodOnThisStateException {
        super.landingOnStationOrShip(isLanded);
        if (isLanded) {
            userInterface.updateLandOnStationOrShip(gameBoard.getCurrentPlayer().getIdPlayer(), true, cardId);
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "Now you have to decide where you want to remove your astronauts");
            currentEffect = listEffects.getFirst();
            currentState = AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS;
            userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS);
        } else {
            userInterface.updateLandOnStationOrShip(gameBoard.getCurrentPlayer().getIdPlayer(), false, cardId);
            tryNextPlayer();
        }
    }

    private void tryNextPlayer() {
        try {
            gameBoard.nextPlayer();
            checkRequirements();
        } catch (IllegalSetOfNextPlayerException e) {
            userInterface.updateGenericCardMessage(genericIdForUpdates, "No more players, now to update");
            update();
        }
    }

    @Override
    public void finishDecision(List<Pair<Point, Token>> coordinatesOfAstronauts) {
        switch (currentState) {
            case DECISION_ASTRONAUTS_OR_ALIENS -> {
                userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(), coordinatesOfAstronauts, lostAstronauts, true);
                currentEffect.setListOfCoordinates(coordinatesOfAstronauts);
                currentState = AdventureStatesTypes.EFFECT;
                userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
                applyEffect();
            }
            default -> throw new IllegalMethodOnThisStateException("Wrong state");
        }
    }

    @Override
    public void applyEffect() {
        super.applyEffect();
        Player currentPlayer = gameBoard.getCurrentPlayer();
        currentEffect.action(currentPlayer);
        currentPlayer.addCredits(creditsEarned);
        userInterface.updateEarnedCredits(gameBoard.getCurrentPlayer().getIdPlayer(), creditsEarned);
        currentPlayer.setDaysThisTurn(-daysLost);
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
