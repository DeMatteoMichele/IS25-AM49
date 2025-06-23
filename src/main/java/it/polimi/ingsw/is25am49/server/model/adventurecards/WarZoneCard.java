package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectCannonata;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectModifyCrew;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
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


    public WarZoneCard(GameBoard gameBoard, String description, int level, String name, int daysLost, int lostAstronauts, List<CardEffect> effects) {
        super(gameBoard, description, level, name);
        listEffects = new ArrayList<>();
        listEffects.add(new EffectModifyCrew("ModifyCrew", lostAstronauts));
        this.listEffects.addAll(effects);
        minimumEnginePower = 1;
        this.daysLost = daysLost;
        this.lostAstronauts = lostAstronauts;
    }

    @Override
    public void drawCard() {
        super.drawCard();
        playerWithMinimumEnginePower = gameBoard.getFirstPlayerConnected();
        playerWithMinimumFirePower = gameBoard.getFirstPlayerConnected();
        System.out.println("You have found a War Zone! Good luck, it will be tough to get out of it.");
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
            System.out.println("you are the only player, you skip this card");
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
        System.out.println("Player with minimum crew is " + gameBoard.getCurrentPlayer().getUsername());
        System.out.println("You lose" + daysLost + " travel days");
        gameBoard.getCurrentPlayer().setDaysThisTurn(-daysLost);
        gameBoard.updateLeaderboard();
        System.out.println("leader updated! now is : " + gameBoard.getFirstPlayerConnected().getUsername());
        gameBoard.setCurrentPlayer(gameBoard.getFirstPlayerConnected());
        checkRequirementsEngine();
    }

    public void checkRequirementsEngine() {
        System.out.println("you are in phase 2 of the war zone, now we will check the player with less engine power");
        System.out.println("i check if " + gameBoard.getCurrentPlayer().getUsername() + " can activate double motors");
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumberOfBatteries = ship.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        int totalNumberOfDoubleMotors = ship.getActivatableComponents(ComponentType.DOUBLE_ENGINE).size();
        if (totalNumberOfBatteries > 0 && totalNumberOfDoubleMotors > 0) {
            System.out.println("you have enough batteries to activate your double motors. One battery activates one motor. ");
                currentState = AdventureStatesTypes.DECISION_BATTERIES;
                checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_BATTERIES);
        } else {
            System.out.println("You can't activate any double motors");
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
            System.out.println("It wasn't the last player, go on with the next player");
            checkRequirementsEngine();
        } catch (IllegalSetOfNextPlayerException e) {
            System.out.println("It was the last player");
            System.out.println("Player with minimum engine power is " + playerWithMinimumEnginePower.getUsername());
            System.out.println(playerWithMinimumEnginePower.getUsername() + " you lose" + lostAstronauts + " crew members, you have to choose which ones");
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
        checkCorrectnessOfTokensAndPoints(currentState, coordinates, ship);
        switch (currentState) {
            case DECISION_BATTERIES -> {
                if (warZonePhase == 0) {
                    checkNumberOfBatteries(coordinates, ship);
                    System.out.println("you have chosen " + coordinates.size() + " batteries");
                    batteries = new ArrayList<>(coordinates);
                    System.out.println("Now you have to choose which motors you wanna activate");
                    currentState = AdventureStatesTypes.DECISION_MOTOR;
                    checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_MOTOR);
                } else if (warZonePhase == 1) {
                    checkNumberOfBatteries(coordinates, ship);
                    System.out.println("you have chosen " + coordinates.size() + " batteries");
                    batteries = new ArrayList<>(coordinates);
                    System.out.println("Now you have to choose which cannons you wanna activate");
                    currentState = AdventureStatesTypes.DECISION_CANNON;
                    checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_CANNON);
                } else {
                    checkNumberOfBatteries(coordinates, ship);
                    System.out.println("you have chosen " + coordinates.size() + " batteries to activate your shield");
                    batteries = new ArrayList<>(coordinates);
                    System.out.println("Now you have to choose which shields you wanna activate");
                    currentState = AdventureStatesTypes.DECISION_SHIELDS;
                    checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_SHIELDS);
                }
            }
            case DECISION_ASTRONAUTS_OR_ALIENS -> {
                currentEffect.setListOfCoordinates(coordinates);
                System.out.println("You have chosen the astronauts or aliens you wanna lose");
                currentEffect.action(gameBoard.getCurrentPlayer());
                System.out.println(gameBoard.getCurrentPlayer().getUsername() + " has lost his chosen crew members");
                gameBoard.setCurrentPlayer(gameBoard.getFirstPlayerConnected());
                checkRequirementsFirePower();
            }
            default -> throw new IllegalArgumentException("wrong state");
        }
    }

    public void checkRequirementsFirePower() {
        System.out.println("now i check which player has the minimum fire power");
        System.out.println("i check if " + gameBoard.getCurrentPlayer().getUsername() + " has enough batteries and double cannons");
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumberOfBatteries = ship.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        int totalNumberOfCannons = ship.getActivatableComponents(ComponentType.DOUBLE_CANNON).size();
        if (totalNumberOfBatteries > 0 && totalNumberOfCannons > 0) {
            System.out.println("you have enough batteries to activate your double Cannons. One battery activates one cannon. " +
                    "You can activate up to " + totalNumberOfBatteries + " double cannon");
            currentState = AdventureStatesTypes.DECISION_BATTERIES;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_BATTERIES);
        } else {
            System.out.println("You don't have enough batteries to activate your double cannons");
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
            System.out.println("It wasn't the last player, go on with the next player");
            checkRequirementsFirePower();
        } catch (IllegalSetOfNextPlayerException e) {
            System.out.println("It was the last player");
            System.out.println("Player with minimum fire power is " + playerWithMinimumFirePower.getUsername());
            currentEffect = listEffects.removeFirst();
            gameBoard.setCurrentPlayer(playerWithMinimumFirePower);
            warZonePhase++;
            System.out.println(playerWithMinimumFirePower.getUsername() + "has to roll the dices");
            currentState = AdventureStatesTypes.ROLL_DICES;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.ROLL_DICES);
        }
    }

    @Override
    public void finishDecision(Set<Point> coordinates) {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        checkCorrectnessOfActivable(currentState, coordinates, ship);
        switch (currentState) {
            case DECISION_MOTOR -> {
                coordinatesOfActivable = new ArrayList<>(coordinates);
                activateComponentsWithBatteries(ship, coordinatesOfActivable, batteries);
                System.out.println("You have decided the coordinates of the motors you wanna activate");
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
                System.out.println("You have decided the coordinates of the cannons you wanna activate");
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
        if (currentEffect.getLevel() == BIG_CANNONBALL_LEVEL) {//big cannonball, player can't do anything
            System.out.println("Big cannonball! You can't defend yourself with shields! You can't do nothing! Hope that the cannonball misses your ship!");
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
        } else {
            long availableShields = currentShipBoard.getActivatableComponents(ComponentType.SHIELD).size();
            if (availableShields > 0 && currentShipBoard.getTotalNumberOfaToken(Token.BATTERY_CELLS) > 0) {
                System.out.println("Puoi attivare gli scudi. Scegli le coordinate delle batterie da utilizzare.");
                currentState = AdventureStatesTypes.DECISION_BATTERIES;
                checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_BATTERIES);
            } else {
                System.out.println("You don't have shield to activate. You can't do nothing! Hope that the cannonball misses your ship!");
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
            System.out.println("You have finished the effects of the card");
            update();
        } else {
            currentEffect = listEffects.removeFirst();
            System.out.println("Next effect: " + currentEffect.getDescription());
            System.out.println("now you have to roll the dices again");
            currentState = AdventureStatesTypes.ROLL_DICES;
           checkConnectionOrDefaultEffect(AdventureStatesTypes.ROLL_DICES);
        }
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