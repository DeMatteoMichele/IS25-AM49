package it.polimi.ingsw.is25am49.client.model.adventurecards;

import it.polimi.ingsw.is25am49.client.model.GameBoard;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.client.model.cardeffects.EffectWinGoods;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisCardException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AbandonedStationCard extends AdventureCard {
    protected final ArrayList<Token> Prize = new ArrayList<>();

    private final List<CardEffect> listEffects;
    private final int daysLost;
    private final int lostAstronauts;

    public AbandonedStationCard(String cardId, GameBoard gameBoard, String description, int level, String name, int daysLost, int minimumAstronauts, int redGoods, int yellowGoods, int greenGoods, int blueGoods) {
        super(cardId, gameBoard, description, level, name);
        this.daysLost = daysLost;
        this.lostAstronauts = minimumAstronauts;
        listEffects = new ArrayList<>();
        listEffects.add(new EffectWinGoods("Win Goods", redGoods, yellowGoods, greenGoods, blueGoods));
        for (CardEffect effect : listEffects) {
            effect.setUserInterface(userInterface);
        }
    }

    @Override
    public void drawCard() {
        //chiamo il draw di adventure card
        super.drawCard();
        userInterface.updateGenericCardMessage(genericIdForUpdates, "You have found an Abandoned Station! You can land on it for some goods.");
        //passo alla richiesta di requisiti
        checkRequirements();
    }


    @Override
    public void checkRequirements() {

        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumCrewMembers = ship.getTotalNumberOfaToken(Token.ASTRONAUT) + ship.getTotalNumberOfaToken(Token.BROWN_ALIENS) + ship.getTotalNumberOfaToken(Token.PURPLE_ALIENS);
        //se ho abbastanza astronauti devo decidere se atterrare e passo allo stato apposito
        if (totalNumCrewMembers >= lostAstronauts) {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "Do you wanna land on the station for some goods?");
            currentState = AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP;
        } else {
            //non ho abastanza membri per atterrare
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You don't have enough members to land on the station!");
            try {
                //controllo se il current player era l'ultimo o no
                //se non è l'ultimo richiamo check requirements sul player dopo
                gameBoard.nextPlayer();
                checkRequirements();
            } catch (IllegalSetOfNextPlayerException e) {
                //se era l'ultimo catturo l'eccezione propagata verso l'alto da nextPlayer
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "It goes to update state");
                update();
            }
        }
    }

    @Override
    public void landingOnStationOrShip(boolean IsLanded) throws IllegalMethodOnThisStateException {

        //se questo metodo è chiamato il player ha scelto di scendere sulla stazione
        //prendo il primo effetto (l'unico in questa carta)
        //vado nello stato dove decido le merci da caricare e le rispettive posizioni
        super.landingOnStationOrShip(IsLanded);
        if (IsLanded) {
            userInterface.updateLandOnStationOrShip(gameBoard.getCurrentPlayer().getIdPlayer(), true, cardId);
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "Now you have to decide where you want to add your goods and which ones");
            currentState = AdventureStatesTypes.DECISION_GOODS;
            currentEffect = listEffects.getFirst();
        } else {
            userInterface.updateLandOnStationOrShip(gameBoard.getCurrentPlayer().getIdPlayer(), false, cardId);
            try {
                gameBoard.nextPlayer();
                checkRequirements();
            } catch (IllegalSetOfNextPlayerException e) {
                userInterface.updateGenericCardMessage(genericIdForUpdates, "No more players, now to update");
                update();
            }
        }

    }

    @Override
    public void finishDecision(Set<Point> coordinates) {
        throw new IllegalMethodOnThisCardException("This method is not allowed in this card");
    }


    @Override
    public void finishDecision(List<Pair<Point, Token>> coordinates) {
        switch (currentState) {
            case DECISION_GOODS -> {
                userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(), coordinates, coordinates.size(), false);
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
        Player player = gameBoard.getCurrentPlayer();
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You are winning goods but you lost " + daysLost + " travel days");
        currentEffect.action(player);
        player.setDaysThisTurn(-daysLost);
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
