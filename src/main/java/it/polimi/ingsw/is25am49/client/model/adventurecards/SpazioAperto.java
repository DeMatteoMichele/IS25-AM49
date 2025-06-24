package it.polimi.ingsw.is25am49.client.model.adventurecards;

import it.polimi.ingsw.is25am49.client.model.GameBoard;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;
import javafx.util.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SpazioAperto extends AdventureCard {

    public SpazioAperto(String cardId, GameBoard gameBoard, String description, int level, String name) {
        super(cardId, gameBoard, description, level, name);
    }

    private List<Pair<Point, Token>> batteries;
    private List<Point> motors;
    private List<Player> playersProcess;
    private int currentPlayerIndex;
    private Map<Player, Integer> declaredEnginePower=new HashMap<>();


    @Override
    public void drawCard() {
        super.drawCard();
        userInterface.updateGenericCardMessage(genericIdForUpdates, "You have found an open space! You can choose if you wanna activate your double motors for travel days");
        playersProcess = gameBoard.getLeaderBoard().stream().map(Map.Entry::getKey).filter(player -> !gameBoard.getLeaderBoard().getDisconnectedPlayers().containsValue(player)).toList();
        currentPlayerIndex = 0;
        gameBoard.setCurrentPlayer(playersProcess.get(currentPlayerIndex));
        userInterface.updateChangeTurn(gameBoard.getCurrentPlayer().getIdPlayer());
        checkRequirements();
    }

    @Override
    public void checkRequirements() {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumberOfBatteries = ship.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        int totalNumberOfDoubleMotors = ship.getActivatableComponents(ComponentType.DOUBLE_ENGINE).size();
        if (totalNumberOfBatteries > 0 && totalNumberOfDoubleMotors > 0) {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "you have enough batteries to activate your double motors. One battery activates one motor. " + "Now choose which batteries you wanna use and how much");
            currentState = AdventureStatesTypes.DECISION_BATTERIES;
            userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_BATTERIES);
        } else {
            //non ho abbastanza batterie per attivare i doppi motori
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You don't have enough batteries or double motors");
            processTravelDays(ship);
        }
    }

    private void processTravelDays(ShipBoard ship) {
        int daysEarned = ship.getTotalEnginePower();
        declaredEnginePower.put(gameBoard.getCurrentPlayer(), daysEarned);
        gameBoard.getCurrentPlayer().setDaysThisTurn(daysEarned);
        gameBoard.updateLeaderboard();
        userInterface.updateLeaderBoard(gameBoard.getLeaderBoard());
        userInterface.updateGenericCardMessage(genericIdForUpdates, gameBoard.getCurrentPlayer().getUsername() + " has earned " + daysEarned + " travel days");
        currentPlayerIndex++;
        if (currentPlayerIndex >= playersProcess.size()) {
            userInterface.updateGenericCardMessage(genericIdForUpdates, "it was the last player, end of the card");
            update();
        } else {
            gameBoard.setCurrentPlayer(playersProcess.get(currentPlayerIndex));
            userInterface.updateChangeTurn(gameBoard.getCurrentPlayer().getIdPlayer());
            checkRequirements();
        }
    }

    @Override
    public void finishDecision(List<Pair<Point, Token>> coordinates) {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        switch (currentState) {
            case DECISION_BATTERIES -> {
                checkNumberOfBatteries(coordinates, ship);
                userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(), coordinates, coordinates.size(), true);
                this.batteries = new ArrayList<>(coordinates);
                if (batteries.isEmpty()) {
                    processTravelDays(ship);
                    return;
                }
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "now choose which motors you wanna activate");
                currentState = AdventureStatesTypes.DECISION_MOTOR;
                userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
                checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_MOTOR);
            }
            default -> throw new IllegalArgumentException("invalid state");
        }
    }

    @Override
    public void finishDecision(Set<Point> coordinates) {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        this.motors = new ArrayList<>(coordinates);
        switch (currentState) {
            case DECISION_MOTOR -> {
                if (batteries.size() < coordinates.size()) {
                    throw new IllegalArgumentException("you have to activate the same number of motors as batteries");
                }
                activateComponentsWithBatteries(ship, motors, batteries);
                currentState = AdventureStatesTypes.EFFECT;
                userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
                applyEffect();
            }
            default -> throw new IllegalArgumentException("invalid state");
        }
    }

    @Override
    public void applyEffect() {
        super.applyEffect();
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        //calcolo i giorni guadagnati in base alla potenza dei motori attivati
        processTravelDays(ship);
        deactivateComponents(ship, motors);
    }

    @Override
    protected void update() {
        currentState = AdventureStatesTypes.UPDATE;
        userInterface.updateCardState(gameBoard.getCurrentPlayer().getIdPlayer(), currentState);
        declaredEnginePower.forEach((player, power) -> { if (power == 0) gameBoard.userStopFlight(player); });
        super.update();
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
        str.append(border);
        return str.toString();
    }
}
