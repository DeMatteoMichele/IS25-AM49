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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpazioAperto extends AdventureCard {

    public SpazioAperto(String cardId, GameBoard gameBoard, String description, int level, String name) {
        super(cardId, gameBoard, description, level, name);
    }

    private List<Pair<Point, Token>> batteries;
    private List<Point> motors;

    private List<Player> playersProcess;
    private int currentPlayerIndex;
    private final List<Player> playersNoEnginePower = new ArrayList<>();


    @Override
    public void drawCard() {
        //chiamo il super
        super.drawCard();
        userInterface.updateGenericCardMessage(genericIdForUpdates, "You have found an open space! You can choose if you wanna activate your double motors for travel days");
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
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "you have enough batteries to activate your double motors. One battery activates one motor. " + "Now choose which batteries you wanna use and how much");
            currentState = AdventureStatesTypes.DECISION_BATTERIES;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_BATTERIES);
        } else {
            //non ho abbastanza batterie per attivare i doppi motori
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You don't have enough batteries to activate your double motors");
            int daysEarned = ship.getTotalEnginePower();
            //imposto i giorni che gudagna il player alla potenza dei motori singoli e aggiorno la leaderboard
            gameBoard.getCurrentPlayer().setDaysThisTurn(daysEarned);
            gameBoard.updateLeaderboard();
            userInterface.updateLeaderBoard(gameBoard.getLeaderBoard());
            userInterface.updateGenericCardMessage(genericIdForUpdates, gameBoard.getCurrentPlayer().getUsername() + "has earned " + daysEarned + " travel days");
            //controllo se currplayer era l'ultimo, se no chiamo checkrequirements sul player successivo
            currentPlayerIndex++;
            if (currentPlayerIndex >= playersProcess.size()) {
                userInterface.updateGenericCardMessage(genericIdForUpdates, "it was the last player, end of the card");
                update();
            } else {
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
                userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(), coordinates, coordinates.size(), true);
                this.batteries = new ArrayList<>(coordinates);
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "now choose which motors you wanna activate");
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
        switch (currentState) {
            case DECISION_MOTOR -> {
                if (batteries.size() < coordinates.size()) {
                    throw new IllegalArgumentException("you have to activate the same number of motors as batteries");
                }
                //attivo i cannoni e passo allo stato in cui applico l'effetto
                for (int i = 0; i < coordinates.size(); i++) {
                    ship.turnOnComponent(motors.get(i), batteries.get(i).getKey());
                    userInterface.updateFinishDecisionActivating(gameBoard.getCurrentPlayer().getIdPlayer(), coordinates);
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
        //calcolo i giorni guadagnati in base alla potenza dei motori attivati
        int daysEarned = ship.getTotalEnginePower();
        userInterface.updateGenericCardMessage(genericIdForUpdates, gameBoard.getCurrentPlayer().getUsername() + "has earned " + daysEarned + " travel days");
        //spengo i motori
        for (int i = 0; i < motors.size(); i++) {
            ship.turnOff(motors.get(i));
        }
        userInterface.updateDeactivating(gameBoard.getCurrentPlayer().getIdPlayer(), motors);
        gameBoard.getCurrentPlayer().setDaysThisTurn(daysEarned);
        //aggiorno la leaderboard
        gameBoard.updateLeaderboard();
        userInterface.updateLeaderBoard(gameBoard.getLeaderBoard());
        //controllo se era l'ultimo giocatore
        currentPlayerIndex++;
        if (currentPlayerIndex >= playersProcess.size()) {
            userInterface.updateGenericCardMessage(genericIdForUpdates, "it was the last player, end of the card");
            update();
        } else {
            currentState = AdventureStatesTypes.CHECK_REQUIREMENTS;
            checkRequirements();
        }
    }

    @Override
    protected void update() {
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
