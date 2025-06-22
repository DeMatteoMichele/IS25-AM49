package it.polimi.ingsw.is25am49.client.model.adventurecards;

import it.polimi.ingsw.is25am49.client.model.GameBoard;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.client.model.cardeffects.EffectCannonata;
import it.polimi.ingsw.is25am49.client.model.cardeffects.EffectModifyCrew;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalNumOfActivableException;
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
    private List<Point> cannons;
    private List<Point> motors;
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
        //check the player with minimum equipment starting from the leader
        Player playerWithMinimumCrew = gameBoard.getLeaderBoard().stream()
                .map(Map.Entry::getKey)
                .filter(player -> !gameBoard.getLeaderBoard().getDisconnectedPlayers().containsValue(player))
                .min((p1, p2) -> Integer.compare(
                        getTotalCrew(p1),
                        getTotalCrew(p2)))
                .orElse(gameBoard.getCurrentPlayer());
        gameBoard.setCurrentPlayer(playerWithMinimumCrew);
        userInterface.updateGenericCardMessage(genericIdForUpdates, "Player with less crew than others is " + gameBoard.getCurrentPlayer().getUsername());
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You lose 3 travel days");
        gameBoard.getCurrentPlayer().setDaysThisTurn(-daysLost);
        gameBoard.updateLeaderboard();
        userInterface.updateLeaderBoard(gameBoard.getLeaderBoard());
        //now current player is the leader again
        gameBoard.setCurrentPlayer(gameBoard.getFirstPlayerConnected());
        checkRequirements2();

    }

    public void checkRequirements2() {

        //check the player with less engine power
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "you are in phase 2 of the war zone, now we will check the player with less engine power");
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumberOfBatteries = ship.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        int totalNumberOfDoubleMotors = ship.getActivatableComponents(ComponentType.DOUBLE_ENGINE).size();
        if (totalNumberOfBatteries > 0 && totalNumberOfDoubleMotors > 0) {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "you have enough batteries to activate your double motors. One battery activates one motor. " +
                    "You can activate up to " + totalNumberOfBatteries + " double motors");
            //I change state to decison batteries to decide how much to activate
            currentState = AdventureStatesTypes.DECISION_BATTERIES;
            checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_BATTERIES);
//            else {
//                gameBoard.nextPlayer();
//                handlePlayers2();
//            }
        } else {
            //non ho abbastanza batterie
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You don't have enough batteries to activate your double motors");
            int enginePower = ship.getTotalEnginePower();
            //userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),enginePower);
            if (enginePower < minimumEnginePower) {
                //userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"aggiornamento minimum power" + enginePower);
                minimumEnginePower = enginePower;
                playerWithMinimumEnginePower = gameBoard.getCurrentPlayer();
                //userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),playerWithMinimumEnginePower.getUsername());
            }
            System.out.println("Now i check if it was the last player");
            handlePlayers2();
        }
    }

    public void handlePlayers2() {
        try {
            gameBoard.nextPlayer();
            System.out.println("It wasn't the last player, go on with the next player");
            checkRequirements2();
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
        switch (currentState) {
            case DECISION_BATTERIES -> {
                if (warZonePhase == 0) {
                    checkNumberOfBatteries(coordinates, ship);
                    userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(), coordinates, coordinates.size(), true);
                    this.batteries = new ArrayList<>(coordinates);
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "Now you have to choose which motors you wanna activate");
                    currentState = AdventureStatesTypes.DECISION_MOTOR;
                    checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_MOTOR);
                } else if (warZonePhase == 1) {
                    checkNumberOfBatteries(coordinates, ship);
                    userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(), coordinates, coordinates.size(), true);
                    this.batteries = new ArrayList<>(coordinates);
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "Now you have to choose which cannons you wanna activate");
                    currentState = AdventureStatesTypes.DECISION_CANNON;
                    checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_CANNON);
                } else {
                    if (coordinates.size() > ship.getTotalNumberOfaToken(Token.BATTERY_CELLS)) {
                        throw new IllegalArgumentException("you have chosen more batteries than you have");
                    }
                    userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(), coordinates, coordinates.size(), true);
                    this.batteries = new ArrayList<>(coordinates);
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "Now you have to choose which shields you wanna activate");
                    currentState = AdventureStatesTypes.DECISION_SHIELDS;
                    checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_SHIELDS);
                }
            }
            case DECISION_ASTRONAUTS_OR_ALIENS -> {
                //i have to choose which astroauts or aliens to lose
                gameBoard.setCurrentPlayer(playerWithMinimumEnginePower);
                currentEffect.setListOfCoordinates(coordinates);
                userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(), coordinates, coordinates.size(), true);
                currentEffect.action(gameBoard.getCurrentPlayer());
                gameBoard.setCurrentPlayer(gameBoard.getLeader());
                userInterface.updateGenericCardMessage(genericIdForUpdates, "The leader is " + gameBoard.getLeader().getUsername());
                checkRequirements3();
            }
            default -> throw new IllegalArgumentException("wrong state");
        }
    }

    public void checkRequirements3() {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumberOfBatteries = ship.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        int totalNumberOfCannons = ship.getActivatableComponents(ComponentType.DOUBLE_CANNON).size();
        if (totalNumberOfBatteries > 0 && totalNumberOfCannons > 0) {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "you have enough batteries to activate your double Cannons. One battery activates one cannon. " +
                    "You can activate up to " + totalNumberOfBatteries + " double cannon");
                currentState = AdventureStatesTypes.DECISION_BATTERIES;
                checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_BATTERIES);
//            else {
//                gameBoard.nextPlayer();
//                handlePlayers3();
//            }
        } else {
            //non ho abbastanza batterie
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You don't have enough batteries to activate your double cannons");
            double firePower = ship.getTotalFirePower();
            if (firePower < minimumFirePower) {
                minimumFirePower = firePower;
                playerWithMinimumFirePower = gameBoard.getCurrentPlayer();
            }
            handlePlayers3();
        }
    }

    public void handlePlayers3() {
        try {
            gameBoard.nextPlayer();
            System.out.println("It wasn't the last player, go on with the next player");
            checkRequirements3();
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
        switch (currentState) {
            case DECISION_MOTOR -> {
                if (coordinates.size() > batteries.size()) {
                    throw new IllegalArgumentException("too much motors for the batteries you have chosen, retry");
                }
                motors = new ArrayList<>(coordinates);
                activateComponentWithBatteries(motors, batteries, ship);
                int enginePower = ship.getTotalEnginePower();
                if (enginePower < minimumEnginePower) {
                    minimumEnginePower = enginePower;
                    playerWithMinimumEnginePower = gameBoard.getCurrentPlayer();
                }
                for (int i = 0; i < coordinates.size(); i++) {
                    ship.turnOff(motors.get(i));
                }
                System.out.println("Now i check if it was the last player");
                handlePlayers2();
            }
            case DECISION_CANNON -> {
                if (coordinates.size() > batteries.size()) {
                    throw new IllegalArgumentException("too much cannons for the batteries you have chosen, retry");
                }
                cannons = new ArrayList<>(coordinates);
                activateComponentWithBatteries(cannons, batteries, ship);
                double firePower = ship.getTotalFirePower();
                if (firePower < minimumFirePower) {
                    minimumFirePower = firePower;
                    playerWithMinimumFirePower = gameBoard.getCurrentPlayer();
                }
                //turn off the cannons
                for (int i = 0; i < coordinates.size(); i++) {
                    ship.turnOff(cannons.get(i));
                }
                System.out.println("Now i check if it was the last player");
                handlePlayers3();
            }
            case DECISION_SHIELDS -> {
                if (coordinates.size() > batteries.size()) {
                    throw new IllegalArgumentException("too much shields for the batteries you have chosen, retry");
                }
                List<Point> coordinatesOfShield = new ArrayList<>(coordinates);
                activateComponentWithBatteries(coordinatesOfShield, batteries, ship);
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
            default -> throw new IllegalArgumentException("wrong state");
        }
    }

    private void activateComponentWithBatteries(List<Point> activable, List<Pair<Point, Token>> batteries, ShipBoard ship) {
        for (int i = 0; i < activable.size(); i++) {
            ship.turnOnComponent(cannons.get(i), batteries.get(i).getKey());
            System.out.println("turning on the cannon at " + cannons.get(i).x + ", " + cannons.get(i).y);
        }
    }

    @Override
    public void rollDices() {
        super.rollDices();
        checkForBatteriesToUse();
    }

    private void checkForBatteriesToUse() {
        coordinatesOfActivable = new ArrayList<>();
        batteries = new ArrayList<>();
        boolean hasBatteries = gameBoard.getCurrentPlayer().getShipBoard().getTotalNumberOfaToken(Token.BATTERY_CELLS) > 0;
        if (hasBatteries) {  // player has batteries
            checkDimensionOfCannonBall();
        } else {  // player has no batteries
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
        }
    }

    private void checkDimensionOfCannonBall() {
        ShipBoard currentShipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        if (currentEffect.getLevel() == BIG_CANNONBALL_LEVEL) {//big cannonball, player can't do anything
            long availableCannons = currentShipBoard.getActivatableComponents(ComponentType.DOUBLE_CANNON).size();
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "Big cannonball! You can't defend yourself with shields! You can't do nothing! Hope in the dices!");
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
        } else {
            long availableShields = currentShipBoard.getActivatableComponents(ComponentType.SHIELD).size();
            if (availableShields > 0) {
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You can activate shields. Choose the coordinates.");
                currentState = AdventureStatesTypes.DECISION_BATTERIES;
               checkConnectionOrDefaultEffect(AdventureStatesTypes.DECISION_BATTERIES);
            } else {
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You don't have shield to activate. You can't do nothing! Hope in the dices!");
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
        }
    }

    protected void deactivateComponents(ShipBoard shipBoard) {
        coordinatesOfActivable.forEach(shipBoard::turnOff);
    }

    @Override
    public void applyEffect() {
        currentEffect.action(gameBoard.getCurrentPlayer());
        deactivateComponents(gameBoard.getCurrentPlayer().getShipBoard());
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

        // Skip the first effect (ModifyCrew) as it's already displayed
        for (int i = 1; i < listEffects.size(); i++) {
            CardEffect effect = listEffects.get(i);
            if (effect instanceof EffectCannonata cannonata) {
                String effectLevel = "|   -EFFECT LEVEL: " + cannonata.getLevel();
                str.append(effectLevel).append(" ".repeat(width - effectLevel.length() - 1)).append("|\n");
                String effectOrientation = "|   -EFFECT ORIENTATION: " + cannonata.getOrientation();
                str.append(effectOrientation).append(" ".repeat(width - effectOrientation.length() - 1)).append("|\n");
            }
            // Qui puoi aggiungere altri tipi di effetti se necessario
            str.append(separator);
        }
        return str.toString();


    }
}