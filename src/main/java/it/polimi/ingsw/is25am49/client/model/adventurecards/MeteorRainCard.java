package it.polimi.ingsw.is25am49.client.model.adventurecards;

import it.polimi.ingsw.is25am49.client.model.GameBoard;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.client.model.cardeffects.EffectMeteoriti;
import it.polimi.ingsw.is25am49.client.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.client.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalNumOfActivableException;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MeteorRainCard extends AdventureCard {

    List<CardEffect> listEffects = new ArrayList<>();

    public MeteorRainCard(String cardId,GameBoard gameBoard, String description, int level, String name, List<CardEffect> ListEffects) {
        super(cardId, gameBoard, description, level, name);
        listEffects.addAll(ListEffects);
        for(CardEffect effect : listEffects){
            effect.setUserInterface(userInterface);
        }
    }

    private static final int BIG_METEORA_LEVEL = 1;
    private List<Pair<Point, Token>> coordinatesOfBatteries = new ArrayList<>();
    private List<Point> coordinatesOfActivable = new ArrayList<>();
    private final List<Player> listOfPlayers = new ArrayList<>();

    @Override
    public void drawCard() {
        super.drawCard();
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You have found a meteor rain! The leader must roll the dices");
        currentEffect = listEffects.removeFirst();
        listOfPlayers.addAll(gameBoard.getLeaderBoard().stream().map(Map.Entry::getKey).toList());
        currentState = AdventureStatesTypes.ROLL_DICES;
    }

    @Override
    public void rollDices() {
        super.rollDices();
        checkForBatteriesToUse();
    }

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

    private void checkDimensionOfMeteora() {
        ShipBoard currentShipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        if (currentEffect.getLevel() == BIG_METEORA_LEVEL) {//big meteora, player cant defend himself
            long availableCannons = currentShipBoard.getActivatableComponents(ComponentType.DOUBLE_CANNON).size();
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Meteorite grande! Non puoi difenderti con gli scudi! Puoi usare un cannone per abbatterlo!");
            if (availableCannons > 0) {
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Puoi attivare i cannoni. Scegli le coordinate delle batterie da utilizzare.");
                currentState = AdventureStatesTypes.DECISION_BATTERIES;
            } else {
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Non hai cannoni da attivare. Subisci l'effetto del meteorite.");
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
        } else {
            long availableShields = currentShipBoard.getActivatableComponents(ComponentType.SHIELD).size();
            if (availableShields > 0) {
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Puoi attivare gli scudi. Scegli le coordinate delle batterie da utilizzare.");
                currentState = AdventureStatesTypes.DECISION_BATTERIES;
            } else {
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Non hai scudi da attivare. Subisci l'effetto del meteorite.");
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
        }
    }

    @Override
    public void finishDecision(List<Pair<Point, Token>> coordinates) {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        switch (currentState) {
            case DECISION_BATTERIES -> {
                int maxActivatableComponents = getMaxActivatableComponents(shipBoard);
                if (coordinates.size() > maxActivatableComponents) {
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Hai selezionato troppe batterie! Puoi attivare al massimo " + maxActivatableComponents + " componenti.");
                    return; // Mantiene lo stato corrente
                }
                coordinatesOfBatteries = coordinates;
                if (coordinatesOfBatteries.isEmpty()) {
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You have decided not to use batteries");
                    currentState = AdventureStatesTypes.EFFECT;
                    applyEffect();
                    return;
                }
                userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(), coordinatesOfBatteries, coordinatesOfBatteries.size(), true);
                currentState = determineStateAfterBatteries();
            }
            default -> throw new IllegalMethodOnThisStateException("Invalid state: " + currentState);
        }
    }

    @Override
    public void finishDecision(Set<Point> coordinates) throws IllegalNumOfActivableException {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        switch (currentState) {
            case DECISION_CANNON -> {
                try {
                    coordinatesOfActivable = new ArrayList<>(coordinates);
                    List<Point> coordinatesOfCannons = new ArrayList<>(coordinates);
                    activateComponentWithBatteries(coordinatesOfCannons, shipBoard);
                    userInterface.updateFinishDecisionActivating(gameBoard.getCurrentPlayer().getIdPlayer(), coordinates);
                    currentState = AdventureStatesTypes.EFFECT;
                    applyEffect();
                } catch (IllegalNumOfActivableException e) {
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Rechoose correctly the number of cannons as the same as batteries");
                }
            }
            case DECISION_SHIELDS -> {
                try {
                    coordinatesOfActivable = new ArrayList<>(coordinates);
                    List<Point> coordinatesOfShield = new ArrayList<>(coordinates);
                    activateComponentWithBatteries(coordinatesOfShield, shipBoard);
                    userInterface.updateFinishDecisionActivating(gameBoard.getCurrentPlayer().getIdPlayer(), coordinates);
                    currentState = AdventureStatesTypes.EFFECT;
                    applyEffect();
                } catch (IllegalNumOfActivableException e) {
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Rechoose correctly the number of shields as the same as batteries");
                }
            }
            default -> throw new IllegalMethodOnThisStateException("Invalid state: " + currentState);
        }
    }

    private AdventureStatesTypes determineStateAfterBatteries() {
        if (currentEffect.getLevel() == BIG_METEORA_LEVEL) {
            return AdventureStatesTypes.DECISION_CANNON;
        } else {
            return AdventureStatesTypes.DECISION_SHIELDS;
        }
    }

    private int getMaxActivatableComponents(ShipBoard shipBoard) {
        if (currentEffect.getLevel() == BIG_METEORA_LEVEL) {
            return shipBoard.getActivatableComponents(ComponentType.DOUBLE_CANNON).size();
        } else {
            return shipBoard.getActivatableComponents(ComponentType.SHIELD).size();
        }
    }

    private void activateComponentWithBatteries(List<Point> coordinatesOfActivable, ShipBoard shipBoard) {
        if (coordinatesOfActivable.size() != coordinatesOfBatteries.size()) {
            throw new IllegalNumOfActivableException("You have to choose at max the same number of cannons as batteries");
        }
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You have decided the coord for the components");
        for (int i = 0; i < coordinatesOfActivable.size(); i++) {
            shipBoard.turnOnComponent(coordinatesOfActivable.get(i), coordinatesOfBatteries.get(i).getKey());
        }
    }

    protected void deactivateComponents(ShipBoard shipBoard) {
        coordinatesOfActivable.forEach(shipBoard::turnOff);
        userInterface.updateDeactivating(gameBoard.getCurrentPlayer().getIdPlayer(), coordinatesOfActivable);
    }

    private void tryNextPlayerDefeated() {
        int currentIndexOfDefeatedPlayer = listOfPlayers.indexOf(gameBoard.getCurrentPlayer());
        if (currentIndexOfDefeatedPlayer >= 0 && currentIndexOfDefeatedPlayer < listOfPlayers.size() - 1) { //i have to apply the currenteffect on all the players
            gameBoard.setCurrentPlayer(listOfPlayers.get(currentIndexOfDefeatedPlayer + 1));
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Now effect on the next player");
            checkForBatteriesToUse();
        } else {    //i have applied the effect on all the players
            if (listEffects.isEmpty()) {  //effects are terminated, card is finished
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"All the effects are finished, now to update");
                update();
            } else {      //effects are not finished, first player has to roll the dices on the next effect
                gameBoard.setCurrentPlayer(listOfPlayers.getFirst());
                currentEffect = listEffects.removeFirst();
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Next effect: " + currentEffect.getDescription());
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Now the leader must roll the dices again!");
                currentState = AdventureStatesTypes.ROLL_DICES;
            }
        }
    }

    @Override
    public void applyEffect() {
        currentEffect.action(gameBoard.getCurrentPlayer());
        deactivateComponents(gameBoard.getCurrentPlayer().getShipBoard());
        tryNextPlayerDefeated();
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
