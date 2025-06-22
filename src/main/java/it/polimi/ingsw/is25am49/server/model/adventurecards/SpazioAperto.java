package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpazioAperto extends AdventureCard {

    public SpazioAperto(GameBoard gameBoard, String description, int level, String name) {
        super(gameBoard, description, level, name);

    }

    private List<Pair<Point, Token>> batteries;
    private List<Point> motors;
    private int index = 0;
    private List<Player> playersProcess;
    private int currentPlayerIndex;
    private final List<Player> playersNoEnginePower = new ArrayList<>();


    @Override
    public void drawCard() {
        //chiamo il super
        super.drawCard();
        System.out.println("You have found an open space! You can choose if you wanna activate your double motors for travel days");
        //passo alla richiesta di requisiti
        currentState = AdventureStatesTypes.CHECK_REQUIREMENTS;
        playersProcess = gameBoard.getLeaderBoard().stream().map(Map.Entry::getKey).filter(player -> !gameBoard.getLeaderBoard().getDisconnectedPlayers().containsValue(player)).toList();
        currentPlayerIndex = 0;
        checkRequirements();

    }

    @Override
    public void checkRequirements() {
        if (currentState != AdventureStatesTypes.CHECK_REQUIREMENTS) {
            throw new IllegalArgumentException("wrong state");
        }
        for (Player player : gameBoard.getLeaderBoard().stream().map(Map.Entry::getKey).toList()) {
            if (player.getShipBoard().getTotalEnginePower() == 0 && player.getShipBoard().getTotalNumberOfaToken(Token.BATTERY_CELLS) == 0) {
                playersNoEnginePower.add(player);
            }
        }
        iterateToFindAConnectedPlayer();
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
            int daysEarned = ship.getTotalEnginePower();
            //imposto i giorni che gudagna il player alla potenza dei motori singoli e aggiorno la leaderboard
            gameBoard.getCurrentPlayer().setDaysThisTurn(daysEarned);
            gameBoard.updateLeaderboard();
            System.out.println("player " + gameBoard.getCurrentPlayer().getUsername() + " has earned " + daysEarned + " travel days");
            System.out.println("now i check if it was the last player");
            //controllo se currplayer era l'ultimo, se no chiamo checkrequirements sul player successivo
            currentPlayerIndex++;
            if (currentPlayerIndex >= playersProcess.size()) {
                System.out.println("it was the last player, end of the card");
                System.out.println("calling update in checkRequirements");
                update();
            } else {
                System.out.println("It wasn't the last player, go on with the next player");
                checkRequirements();
            }
        }
    }

    private void iterateToFindAConnectedPlayer() {
        for (int j = currentPlayerIndex; j < playersProcess.size(); j++) {
            Player currentPlayer = playersProcess.get(j);
            if (currentPlayer.isConnected()) {
                gameBoard.setCurrentPlayer(currentPlayer);
                break;
            }
            currentPlayerIndex++;
        }
    }

    @Override
    public void finishDecision(List<Pair<Point, Token>> coordinates) {
        Player currentPlayer = playersProcess.get(currentPlayerIndex);
        gameBoard.setCurrentPlayer(currentPlayer);
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        switch (currentState) {
            case DECISION_BATTERIES -> {
                if (coordinates.size() > ship.getTotalNumberOfaToken(Token.BATTERY_CELLS)) {
                    throw new IllegalArgumentException("you have chosen more batteries than you have");
                }
                //ho deciso quali batterie attivare, setto la lista di coordinate della batterie con quella passata
                //dall'utente e passo allo stato in cui in base alle batterie decide i cannoni da attivare
                System.out.println("you have choosen " + coordinates.size() + " batteries, so you will activate " + coordinates.size() + " motors");
                this.batteries = new ArrayList<>(coordinates);
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
        Player currentPlayer = playersProcess.get(currentPlayerIndex);
        gameBoard.setCurrentPlayer(currentPlayer);
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        this.motors = new ArrayList<>(coordinates);
        checkCorrectnessOfActivable(currentState, coordinates, ship);
        switch (currentState) {
            case DECISION_MOTOR -> {
                System.out.println("you have decided the coordinates of the motors you wanna activate");
                if (batteries.size() < coordinates.size()) {
                    throw new IllegalArgumentException("you have to activate the same number of motors as batteries");
                }
                for (int i = 0; i < coordinates.size(); i++) {
                    ship.turnOnComponent(motors.get(i), batteries.get(i).getKey());
                    System.out.println("turning on the motor at " + motors.get(i).x + " " + motors.get(i).y);
                }
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
            default -> throw new IllegalArgumentException("invalid state");
        }
    }


    @Override
    public void applyEffect() {
        super.applyEffect();
        Player currentPlayer = playersProcess.get(currentPlayerIndex);
        gameBoard.setCurrentPlayer(currentPlayer);
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        System.out.println("you are in apply effect, now i will calculate the days you have earned");
        //calcolo i giorni guadagnati in base alla potenza dei motori attivati
        int daysEarned = ship.getTotalEnginePower();
        System.out.println("you have earned " + daysEarned + " travel days");
        //spengo i motori
        for (int i = 0; i < motors.size(); i++) {
            System.out.println("turning off the motor at " + motors.get(i).x + " " + motors.get(i).y);
            ship.turnOff(motors.get(i));
        }
        gameBoard.getCurrentPlayer().setDaysThisTurn(daysEarned);
        gameBoard.updateLeaderboard();
        System.out.println("now i check if it was the last player");
        currentPlayerIndex++;
        if (currentPlayerIndex >= playersProcess.size()) {
            System.out.println("it was the last player, end of the card");
            System.out.println("chiamo update in applyEffect");
            update();
        } else {
            System.out.println("It wasn't the last player, go on with the next player");
            currentState = AdventureStatesTypes.CHECK_REQUIREMENTS;
            checkRequirements();
        }
    }

    @Override
    protected void update() {
        System.out.println("IN UPDATE, NUMERO" +index);
        currentState = AdventureStatesTypes.UPDATE;
        playersNoEnginePower.forEach(gameBoard::playerNoMoreInFlight);
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
