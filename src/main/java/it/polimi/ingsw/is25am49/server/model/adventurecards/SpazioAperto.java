package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import javafx.util.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SpazioAperto extends AdventureCard {

    public SpazioAperto(GameBoard gameBoard, String description, int level, String name) {
        super(gameBoard, description, level, name);

    }

    private List<Pair<Point, Token>> batteries;
    private List<Point> motors;
    private List<Player> playersProcess;
    private int currentPlayerIndex;
    private Map<Player, Integer> declaredEnginePower=new HashMap<>();


    @Override
    public void drawCard() {
        //chiamo il super
        super.drawCard();
        System.out.println("You have found an open space! You can choose if you wanna activate your double motors for travel days");
        //passo alla richiesta di requisiti
        currentState = AdventureStatesTypes.CHECK_REQUIREMENTS;
        playersProcess = gameBoard.getLeaderBoard().stream().map(Map.Entry::getKey).filter(player -> !gameBoard.getLeaderBoard().getDisconnectedPlayers().containsValue(player)).toList();
        currentPlayerIndex = 0;
        gameBoard.setCurrentPlayer(playersProcess.get(currentPlayerIndex));
        checkRequirements();

    }

    @Override
    public void checkRequirements() {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumberOfBatteries = ship.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        int totalNumberOfDoubleMotors = ship.getActivatableComponents(ComponentType.DOUBLE_ENGINE).size();
        if (totalNumberOfBatteries > 0 && totalNumberOfDoubleMotors > 0) {
            System.out.println("you have enough batteries to activate your double motors. One battery activates one double motor. " +
                    "Now choose which batteries you wanna use and how much");
            currentState = AdventureStatesTypes.DECISION_BATTERIES;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_BATTERIES);
        } else {
            //non ho abbastanza batterie per attivare i doppi motori
            System.out.println("You don't have enough batteries to activate your double motors");
            processTravelDays(ship);
        }
    }

    private void processTravelDays(ShipBoard ship) {
        int daysEarned = ship.getTotalEnginePower();
        declaredEnginePower.put(gameBoard.getCurrentPlayer(), daysEarned);
        gameBoard.getCurrentPlayer().setDaysThisTurn(daysEarned);
        gameBoard.updateLeaderboard();
        System.out.println("player " + gameBoard.getCurrentPlayer().getUsername() + " has earned " + daysEarned + " travel days");
        System.out.println("now i check if it was the last player");
        //controllo se currplayer era l'ultimo, se no chiamo checkrequirements sul player successivo
        currentPlayerIndex++;
        if (currentPlayerIndex >= playersProcess.size()) {
            System.out.println("it was the last player, end of the card");
            update();
        } else {
            System.out.println("It wasn't the last player, go on with the next player");
            gameBoard.setCurrentPlayer(playersProcess.get(currentPlayerIndex));
            checkRequirements();
        }
    }

    @Override
    public void finishDecision(List<Pair<Point, Token>> coordinates) {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        switch (currentState) {
            case DECISION_BATTERIES -> {
                checkNumberOfBatteries(coordinates, ship);
                System.out.println("you have choosen " + coordinates.size() + " batteries, so you will activate " + coordinates.size() + " motors");
                this.batteries = new ArrayList<>(coordinates);
                if(batteries.isEmpty()) {
                    processTravelDays(ship);
                    return;
                }
                System.out.println("now choose which motors you wanna activate");
                currentState = AdventureStatesTypes.DECISION_MOTOR;
                checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_MOTOR);
            }
            default -> {
                throw new IllegalArgumentException("invalid state");
            }
        }
    }

    @Override
    public void finishDecision(Set<Point> coordinates) {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        this.motors = new ArrayList<>(coordinates);
        checkCorrectnessOfActivable(currentState, coordinates, ship);
        switch (currentState) {
            case DECISION_MOTOR -> {
                System.out.println("you have decided the coordinates of the motors you wanna activate");
                if (batteries.size() < coordinates.size()) {
                    throw new IllegalArgumentException("you have to activate the same number of motors as batteries");
                }
                activateComponentsWithBatteries(ship, motors, batteries);
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
            default -> throw new IllegalArgumentException("invalid state");
        }
    }


    @Override
    public void applyEffect() {
        super.applyEffect();
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        System.out.println("you are in apply effect, now i will calculate the days you have earned");
        //calcolo i giorni guadagnati in base alla potenza dei motori attivati
        processTravelDays(ship);
        deactivateComponents(ship,motors);
    }

    @Override
    protected void update() {
        currentState = AdventureStatesTypes.UPDATE;
        declaredEnginePower.forEach((player, power) -> { if (power == 0) gameBoard.playerNoMoreInFlight(player); });
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
