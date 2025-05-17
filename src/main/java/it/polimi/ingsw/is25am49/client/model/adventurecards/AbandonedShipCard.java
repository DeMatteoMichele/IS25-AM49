package it.polimi.ingsw.is25am49.client.model.adventurecards;

import it.polimi.ingsw.is25am49.client.model.GameBoard;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.client.model.cardeffects.EffectModifyCrew;
import it.polimi.ingsw.is25am49.client.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
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

    public AbandonedShipCard(GameBoard gameBoard, String description, int level, String name, int dayslost, int creditsEarned, int lostAstronauts) {
        super(gameBoard, description, level,name);
        this.lostAstronauts = lostAstronauts;
        this.creditsEarned = creditsEarned;
        this.daysLost = dayslost;
        listEffects.add(new EffectModifyCrew("Remove astronauts", lostAstronauts));
    }

    @Override
    public void drawCard() {
        super.drawCard();
        System.out.println("You have found an abandoned ship! You can sell it to your crew members for some credits");
        checkRequirements();
    }

    @Override
    public void checkRequirements() {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        int numCrew = shipBoard.getTotalNumberOfaToken(Token.ASTRONAUT)+shipBoard.getTotalNumberOfaToken(Token.BROWN_ALIENS)+shipBoard.getTotalNumberOfaToken(Token.PURPLE_ALIENS);
        if (numCrew >= lostAstronauts) {
            System.out.println("You have " + numCrew + " astronauts on your ship. If you decide to land on the ship, you win " + creditsEarned + " credits but if your crew is too small you have to quit");
            currentState = AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP;
        } else {
            System.out.println("You don't have enough astronauts to land on the ship");
            tryNextPlayer();
        }
    }

    @Override
    public void landingOnStationOrShip(boolean isLanded) throws IllegalMethodOnThisStateException {
        super.landingOnStationOrShip(isLanded);
        if (isLanded) {
            System.out.println("You have landed on the ship");
            System.out.println("Now you have to decide where you want to remove your astronauts");
            currentEffect = listEffects.getFirst();
            currentState = AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS;
        } else {
            System.out.println("You have decided not to land on the ship");
            tryNextPlayer();
        }
    }

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

    @Override
    public void finishDecision(List<Pair<Point, Token>> coordinates) {
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
    @Override
    public String toString(){
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
