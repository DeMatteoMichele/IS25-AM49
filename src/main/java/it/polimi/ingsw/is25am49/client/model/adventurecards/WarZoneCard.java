package it.polimi.ingsw.is25am49.client.model.adventurecards;

import it.polimi.ingsw.is25am49.client.model.GameBoard;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.client.model.cardeffects.EffectCannonata;
import it.polimi.ingsw.is25am49.client.model.cardeffects.EffectModifyCrew;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WarZoneCard extends AdventureCard {
    Player playerWithMinimumEnginePower;
    Player playerWithMinimumFirePower;
    private int minimumEnginePower;
    private final List<CardEffect> listEffects;
    private List<Pair<Point, Token>> batteries;
    private int warZonePhase = 0;
    private final int daysLost;
    private double minimumFirePower = 1;
    private static final int BIG_CANNONBALL_LEVEL = 1;
    private List<Point> coordinatesOfActivable = new ArrayList<>();
    private int lostAstronauts;


    public WarZoneCard(String cardId, GameBoard gameBoard, String description, int level, String name, int daysLost, int lostAstronauts, List<CardEffect> effects) {
        super(cardId, gameBoard, description, level, name);
        listEffects = new ArrayList<>();
        listEffects.add(new EffectModifyCrew("ModifyCrew", lostAstronauts));
        this.listEffects.addAll(effects);
        minimumEnginePower = 1;
        this.daysLost = daysLost;
        this.lostAstronauts = lostAstronauts;
        for (CardEffect effect : listEffects) {
            effect.setUserInterface(userInterface);
        }
    }

    @Override
    public void drawCard() {
        super.drawCard();
        playerWithMinimumEnginePower = gameBoard.getFirstPlayerConnected();
        userInterface.updateGenericCardMessage(genericIdForUpdates, "You have found a War Zone! Good luck, it will be tough to get out of it.");
        checkRequirements();
    }

    private int getTotalCrew(Player p) {
        return p.getShipBoard().getTotalNumberOfaToken(Token.ASTRONAUT) +
                p.getShipBoard().getTotalNumberOfaToken(Token.BROWN_ALIENS) +
                p.getShipBoard().getTotalNumberOfaToken(Token.PURPLE_ALIENS);
    }


    @Override
    public void checkRequirements() {
        if (gameBoard.getLeaderBoard().sizeOfConnected() == 1) {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "you are the only player, you skip this card");
            update();
        }
        Player playerWithMinimumCrew = gameBoard.getLeaderBoard().stream()
                .map(Map.Entry::getKey)
                .filter(player -> !gameBoard.getLeaderBoard().getDisconnectedPlayers().containsValue(player))
                .min((p1, p2) -> Integer.compare(
                        getTotalCrew(p1),
                        getTotalCrew(p2)))
                .orElse(gameBoard.getCurrentPlayer());
        gameBoard.setCurrentPlayer(playerWithMinimumCrew);
        userInterface.updateGenericCardMessage(genericIdForUpdates, "Player with less crew than others is " + gameBoard.getCurrentPlayer().getUsername());
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You lose" +daysLost +" travel days");
        gameBoard.getCurrentPlayer().setDaysThisTurn(-daysLost);
        gameBoard.updateLeaderboard();
        userInterface.updateLeaderBoard(gameBoard.getLeaderBoard());
        gameBoard.setCurrentPlayer(gameBoard.getFirstPlayerConnected());
        checkRequirementsEngine();
    }

    public void checkRequirementsEngine() {
        userInterface.updateGenericCardMessage(genericIdForUpdates, "You are in phase 2 of the war zone, now we will check the player with less engine power");
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumberOfBatteries = ship.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        int totalNumberOfDoubleMotors = ship.getActivatableComponents(ComponentType.DOUBLE_ENGINE).size();
        if (totalNumberOfBatteries > 0 && totalNumberOfDoubleMotors > 0) {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "you have enough batteries to activate your double motors. One battery activates one motor. ");
            currentState = AdventureStatesTypes.DECISION_BATTERIES;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_BATTERIES);
        } else {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You don't have enough batteries to activate your double motors");
            int enginePower = ship.getTotalEnginePower();
            if (enginePower < minimumEnginePower) {
                minimumEnginePower = enginePower;
                playerWithMinimumEnginePower = gameBoard.getCurrentPlayer();
            }
            handlePlayersEngine();
        }
    }

    public void handlePlayersEngine() {
        try {
            gameBoard.nextPlayer();
            userInterface.updateGenericCardMessage(genericIdForUpdates,"It wasn't the last player, go on with the next player");
            checkRequirementsEngine();
        } catch (IllegalSetOfNextPlayerException e) {
            userInterface.updateGenericCardMessage(genericIdForUpdates,"Player with minimum engine power is " + playerWithMinimumEnginePower.getUsername());
            userInterface.updateGenericCardMessage(playerWithMinimumEnginePower.getIdPlayer(),playerWithMinimumEnginePower.getUsername() + " you lose" + lostAstronauts + " crew members, you have to choose which ones");
            currentEffect = listEffects.removeFirst();
            warZonePhase++;
            gameBoard.setCurrentPlayer(playerWithMinimumEnginePower);
            currentState = AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS);
        }
    }

    @Override
    public void finishDecision(List<Pair<Point, Token>> coordinates) {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        switch (currentState) {
            case DECISION_BATTERIES -> {
                if (warZonePhase == 0) {
                    checkNumberOfBatteries(coordinates, ship);
                    userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(), coordinates, coordinates.size(), true);
                    batteries = new ArrayList<>(coordinates);
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "Now you have to choose which motors you wanna activate");
                    currentState = AdventureStatesTypes.DECISION_MOTOR;
                    checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_MOTOR);
                } else if (warZonePhase == 1) {
                    checkNumberOfBatteries(coordinates, ship);
                    userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(), coordinates, coordinates.size(), true);
                    batteries = new ArrayList<>(coordinates);
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "Now you have to choose which cannons you wanna activate");
                    currentState = AdventureStatesTypes.DECISION_CANNON;
                    checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_CANNON);
                } else {
                    checkNumberOfBatteries(coordinates, ship);
                    userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(), coordinates, coordinates.size(), true);
                    batteries = new ArrayList<>(coordinates);
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "Now you have to choose which shields you wanna activate");
                    currentState = AdventureStatesTypes.DECISION_SHIELDS;
                    checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_SHIELDS);
                }
            }
            case DECISION_ASTRONAUTS_OR_ALIENS -> {
                currentEffect.setListOfCoordinates(coordinates);
                userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(), coordinates, coordinates.size(), true);
                currentEffect.action(gameBoard.getCurrentPlayer());
                gameBoard.setCurrentPlayer(gameBoard.getFirstPlayerConnected());
                checkRequirementsFirePower();
            }
            default -> throw new IllegalArgumentException("wrong state");
        }
    }

    public void checkRequirementsFirePower() {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumberOfBatteries = ship.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        int totalNumberOfCannons = ship.getActivatableComponents(ComponentType.DOUBLE_CANNON).size();
        if (totalNumberOfBatteries > 0 && totalNumberOfCannons > 0) {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "you have enough batteries to activate your double Cannons. One battery activates one cannon. " +
                    "You can activate up to " + totalNumberOfBatteries + " double cannon");
            currentState = AdventureStatesTypes.DECISION_BATTERIES;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_BATTERIES);
        } else {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You don't have enough batteries or double cannons");
            double firePower = ship.getTotalFirePower();
            if (firePower < minimumFirePower) {
                minimumFirePower = firePower;
                playerWithMinimumFirePower = gameBoard.getCurrentPlayer();
            }
            handlePlayersFirePower();
        }
    }

    public void handlePlayersFirePower() {
        try {
            gameBoard.nextPlayer();
            checkRequirementsFirePower();
        } catch (IllegalSetOfNextPlayerException e) {
            userInterface.updateGenericCardMessage(genericIdForUpdates,"Player with minimum fire power is " + playerWithMinimumFirePower.getUsername());
            currentEffect = listEffects.removeFirst();
            gameBoard.setCurrentPlayer(playerWithMinimumFirePower);
            warZonePhase++;
            userInterface.updateGenericCardMessage(genericIdForUpdates,playerWithMinimumFirePower.getUsername() + "has to roll the dices");
            currentState = AdventureStatesTypes.ROLL_DICES;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.ROLL_DICES);
        }
    }

    @Override
    public void finishDecision(Set<Point> coordinates) {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        switch (currentState) {
            case DECISION_MOTOR -> {
                coordinatesOfActivable = new ArrayList<>(coordinates);
                activateComponentsWithBatteries(ship, coordinatesOfActivable, batteries);
                int enginePower = ship.getTotalEnginePower();
                if (enginePower < minimumEnginePower) {
                    minimumEnginePower = enginePower;
                    playerWithMinimumEnginePower = gameBoard.getCurrentPlayer();
                }
                deactivateComponents(ship,coordinatesOfActivable);
                handlePlayersEngine();
            }
            case DECISION_CANNON -> {
                coordinatesOfActivable = new ArrayList<>(coordinates);
                activateComponentsWithBatteries(ship, coordinatesOfActivable, batteries);
                double firePower = ship.getTotalFirePower();
                if (firePower < minimumFirePower) {
                    minimumFirePower = firePower;
                    playerWithMinimumFirePower = gameBoard.getCurrentPlayer();
                }
                deactivateComponents(ship,coordinatesOfActivable);
                handlePlayersFirePower();
            }
            case DECISION_SHIELDS -> {
                coordinatesOfActivable = new ArrayList<>(coordinates);
                activateComponentsWithBatteries(ship, coordinatesOfActivable, batteries);
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
            default -> throw new IllegalArgumentException("wrong state");
        }
    }

    @Override
    public void rollDices() {
        super.rollDices();
        checkForBatteriesToUse();
    }

    private void checkForBatteriesToUse() {
        boolean hasBatteries = gameBoard.getCurrentPlayer().getShipBoard().getTotalNumberOfaToken(Token.BATTERY_CELLS) > 0;
        if (hasBatteries) {
            checkDimensionOfCannonBall();
        } else {
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
        }
    }

    private void checkDimensionOfCannonBall() {
        ShipBoard currentShipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        if (currentEffect.getLevel() == BIG_CANNONBALL_LEVEL) {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "Big cannonball! You can't defend yourself with shields! You can't do nothing! Hope that the cannonball misses your ship!");
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
        } else {
            long availableShields = currentShipBoard.getActivatableComponents(ComponentType.SHIELD).size();
            if (availableShields > 0 && currentShipBoard.getTotalNumberOfaToken(Token.BATTERY_CELLS) > 0) {
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You can activate shields. Choose the coordinates of the batteries.");
                currentState = AdventureStatesTypes.DECISION_BATTERIES;
                checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_BATTERIES);
            } else {
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You don't have shield to activate. You can't do nothing! Hope that the cannonball misses your ship!");
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
        }
    }

    @Override
    public void applyEffect() {
        currentEffect.action(gameBoard.getCurrentPlayer());
        deactivateComponents(gameBoard.getCurrentPlayer().getShipBoard(),coordinatesOfActivable);
        tryAgain();
    }

    private void tryAgain() {
        if (listEffects.isEmpty()) {
            userInterface.updateGenericCardMessage(genericIdForUpdates, "You have finished the effects of the card");
            update();
        } else {
            currentEffect = listEffects.removeFirst();
            userInterface.updateGenericCardMessage(genericIdForUpdates, "Now the leader must roll the dices again!");
            currentState = AdventureStatesTypes.ROLL_DICES;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.ROLL_DICES);
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        final int width = 90;
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
        String daysLostText = "| DAYS LOST: " + daysLost;
        String lostAstronautsText = "| LOST ASTRONAUTS: " + ((EffectModifyCrew) listEffects.get(0)).getLostCrew();
        str.append(lostAstronautsText).append(" ".repeat(width - lostAstronautsText.length() - 1)).append("|\n");
        str.append(separator);
        str.append("| CANNONADE EFFECTS: ").append(" ".repeat(width - "| CANNONADE EFFECTS: ".length() - 1)).append("|\n");
        for (int i = 1; i < listEffects.size(); i++) {
            CardEffect effect = listEffects.get(i);
            if (effect instanceof EffectCannonata cannonata) {
                String effectLevel = "|   -EFFECT LEVEL: " + cannonata.getLevel();
                str.append(effectLevel).append(" ".repeat(width - effectLevel.length() - 1)).append("|\n");
                String effectOrientation = "|   -EFFECT ORIENTATION: " + cannonata.getOrientation();
                str.append(effectOrientation).append(" ".repeat(width - effectOrientation.length() - 1)).append("|\n");
            }
            str.append(separator);
        }
        return str.toString();
    }
}