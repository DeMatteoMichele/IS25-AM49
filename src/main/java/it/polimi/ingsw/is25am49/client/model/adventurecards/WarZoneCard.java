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
    private int number = 0;
    private final int daysLost;
    private double minimumFirePower = 1;
    private static final int BIG_CANNONBALL_LEVEL = 1;
    private List<Point> coordinatesOfActivable = new ArrayList<>();


    public WarZoneCard(String cardId,GameBoard gameBoard, String description, int level, String name, int daysLost, int lostAstronauts, List<CardEffect> effects) {
        super(cardId, gameBoard, description, level, name);
        listEffects = new ArrayList<>();
        listEffects.add(new EffectModifyCrew("ModifyCrew", lostAstronauts));
        this.listEffects.addAll(effects);
        minimumEnginePower = 1;
        this.daysLost = daysLost;
        for(CardEffect effect : listEffects){
            effect.setUserInterface(userInterface);
        }
    }

    @Override
    public void drawCard() {
        super.drawCard();
        playerWithMinimumEnginePower = gameBoard.getLeader();
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You have found a War Zone! Good luck, it will be tough to get out of it.");
        checkRequirements();
    }


    //    method to calculate the total crew of a player
    private int getTotalCrew(Player p) {
        return p.getShipBoard().getTotalNumberOfaToken(Token.ASTRONAUT) +
                p.getShipBoard().getTotalNumberOfaToken(Token.BROWN_ALIENS) +
                p.getShipBoard().getTotalNumberOfaToken(Token.PURPLE_ALIENS);
    }


    @Override
    public void checkRequirements() {
        if (gameBoard.getLeaderBoard().size() == 1) {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"you are the only player, you skip this card");
            update();
        }
        //check the player with minimum equipment starting from the leader
        Player playerWithMinimumCrew = gameBoard.getLeaderBoard().stream()
                .map(Map.Entry::getKey)
                .min((p1, p2) -> Integer.compare(
                        getTotalCrew(p1),
                        getTotalCrew(p2)))
                .orElse(gameBoard.getCurrentPlayer());
        //set the current player to the one with minimum crew
        gameBoard.setCurrentPlayer(playerWithMinimumCrew);
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Player with minimum crew is " + gameBoard.getCurrentPlayer().getUsername());
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You lost 3 travel days");
        gameBoard.getCurrentPlayer().setDaysThisTurn(-daysLost);
        gameBoard.updateLeaderboard();
        //now current player is the leader again
        gameBoard.setCurrentPlayer(gameBoard.getLeader());
        checkRequirements2();

    }

    public void checkRequirements2() {

        //check the player with less engine power
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"you are in phase 2 of the war zone, now we will check the player with less engine power");
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"i check if " + gameBoard.getCurrentPlayer().getUsername() + " has enough batteries to activate double motors");
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumberOfBatteries = ship.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        if (totalNumberOfBatteries > 0) {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"you have enough batteries to activate your double motors. One battery activates one motor. " +
                    "You can activate up to " + totalNumberOfBatteries + " double motors");
            //I change state to decison batteries to decide how much to activate
            currentState = AdventureStatesTypes.DECISION_BATTERIES;
        } else {
            //non ho abbastanza batterie
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You don't have enough batteries to activate your double motors");
            int enginePower = ship.getTotalEnginePower();
            //userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),enginePower);
            if (enginePower < minimumEnginePower) {
                //userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"aggiornamento minimum power" + enginePower);
                minimumEnginePower = enginePower;
                playerWithMinimumEnginePower = gameBoard.getCurrentPlayer();
                //userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),playerWithMinimumEnginePower.getUsername());
            }
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Now i check if it was the last player");
            try {
                //controllo se il current player era l'ultimo o no
                //se non è l'ultimo richiamo check requirements sul player dopo
                gameBoard.nextPlayer();
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"It wasn't the last player, go on with the next player");
                checkRequirements2();
            } catch (IllegalSetOfNextPlayerException e) {
                //se era l'ultimo catturo l'eccezione propagata verso l'alto da nextPlayer
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"It was the last player");
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Player with minimum engine power is " + playerWithMinimumEnginePower.getUsername());
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),playerWithMinimumEnginePower.getUsername() + " you lost 2 crew members, you have to choose which ones");
                currentEffect = listEffects.removeFirst();
                number++;
                currentState = AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS;
            }

        }
    }

    @Override
    public void finishDecision(List<Pair<Point, Token>> coordinates) {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        switch (currentState) {
            case DECISION_BATTERIES -> {
                if (number == 0) {
                    if (coordinates.size() > ship.getTotalNumberOfaToken(Token.BATTERY_CELLS)) {
                        throw new IllegalArgumentException("you have chosen more batteries than you have");
                    } else if (coordinates.size() > ship.getActivatableComponents(ComponentType.DOUBLE_ENGINE).size()) {
                        throw new IllegalArgumentException("you have chosen" + coordinates.size() + "but you have less engine ");
                    }
                    userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(),coordinates, coordinates.size(),true);
                    this.batteries = new ArrayList<>(coordinates);
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Now you have to choose which motors you wanna activate");
                    currentState = AdventureStatesTypes.DECISION_MOTOR;

                } else if (number == 1) {
                    if (coordinates.size() > ship.getTotalNumberOfaToken(Token.BATTERY_CELLS)) {
                        throw new IllegalArgumentException("you have chosen more batteries than you have");
                    }
                    userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(),coordinates, coordinates.size(),true);
                    this.batteries = new ArrayList<>(coordinates);
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Now you have to choose which cannons you wanna activate");
                    currentState = AdventureStatesTypes.DECISION_CANNON;
                } else {
                    if (coordinates.size() > ship.getTotalNumberOfaToken(Token.BATTERY_CELLS)) {
                        throw new IllegalArgumentException("you have chosen more batteries than you have");
                    }
                    userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(),coordinates, coordinates.size(),true);
                    this.batteries = new ArrayList<>(coordinates);
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Now you have to choose which shields you wanna activate");
                    currentState = AdventureStatesTypes.DECISION_SHIELDS;
                }
            }
            case DECISION_ASTRONAUTS_OR_ALIENS -> {
                //i have to choose which astroauts or aliens to lose
                gameBoard.setCurrentPlayer(playerWithMinimumEnginePower);
                currentEffect.setListOfCoordinates(coordinates);
                userInterface.updateFinishDecisionToken(gameBoard.getCurrentPlayer().getIdPlayer(),coordinates, coordinates.size(),true);
                currentEffect.action(gameBoard.getCurrentPlayer());
                gameBoard.setCurrentPlayer(gameBoard.getLeader());
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"The leader is " + gameBoard.getLeader().getUsername());
                checkRequirements3();

            }
            default -> throw new IllegalArgumentException("wrong state");


        }
    }

    public void checkRequirements3() {
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"now i check which player has the minimum fire power");
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"i check if " + gameBoard.getCurrentPlayer().getUsername() + " has enough batteries to activate double cannons");
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumberOfBatteries = ship.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        if (totalNumberOfBatteries > 0 && ship.getActivatableComponents(ComponentType.DOUBLE_CANNON).size() > 0) {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"you have enough batteries to activate your double Cannons. One battery activates one cannon. " +
                    "You can activate up to " + totalNumberOfBatteries + " double cannon");
            //I change state to decison batteries to decide how much to activate
            currentState = AdventureStatesTypes.DECISION_BATTERIES;
        } else {
            //non ho abbastanza batterie
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You don't have enough batteries to activate your double cannons");
            double firePower = ship.getTotalFirePower();
            //userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),enginePower);
            if (firePower < minimumFirePower) {
                //userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"aggiornamento minimum power" + enginePower);
                minimumFirePower = firePower;
                playerWithMinimumFirePower = gameBoard.getCurrentPlayer();
                //userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),playerWithMinimumEnginePower.getUsername());
            }
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Now i check if it was the last player");
            try {
                //controllo se il current player era l'ultimo o no
                //se non è l'ultimo richiamo check requirements sul player dopo
                gameBoard.nextPlayer();
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"It wasn't the last player, go on with the next player");
                checkRequirements3();
            } catch (IllegalSetOfNextPlayerException e) {
                //se era l'ultimo catturo l'eccezione propagata verso l'alto da nextPlayer
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"It was the last player");
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Player with minimum fire power is " + playerWithMinimumFirePower.getUsername());
                currentEffect = listEffects.removeFirst();
                gameBoard.setCurrentPlayer(playerWithMinimumFirePower);
                number++;
                currentState = AdventureStatesTypes.ROLL_DICES;
            }

        }
    }


    @Override
    public void finishDecision(Set<Point> coordinates) {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        switch (currentState) {

            case DECISION_MOTOR -> {
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You have decided the coordinates of the motors you wanna activate");
//                if(batteries.size()!=coordinates.size()){
//                    throw new IllegalArgumentException("you have chosen a different number of batteries and motors");
//                }
                motors = new ArrayList<>(coordinates);
                //i activte all the cannons
                for (int i = 0; i < coordinates.size(); i++) {
                    ship.turnOnComponent(motors.get(i), batteries.get(i).getKey());
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"turning on the motor at " + motors.get(i).x + ", " + motors.get(i).y);
                }
                int enginePower = ship.getTotalEnginePower();
                if (enginePower < minimumEnginePower) {
                    minimumEnginePower = enginePower;
                    playerWithMinimumEnginePower = gameBoard.getCurrentPlayer();
                }
                //turn off the motors
                for (int i = 0; i < coordinates.size(); i++) {
                    ship.turnOff(motors.get(i));
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"turning off the motor at " + motors.get(i).x + ", " + motors.get(i).y);
                }
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Now i check if it was the last player");
                try {
                    //controllo se il current player era l'ultimo o no
                    //se non è l'ultimo richiamo check requirements sul player dopo
                    gameBoard.nextPlayer();
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"It wasn't the last player, go on with the next player");
                    checkRequirements2();
                } catch (IllegalSetOfNextPlayerException e) {
                    //se era l'ultimo catturo l'eccezione propagata verso l'alto da nextPlayer
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"It was the last player");
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Player with minimum fire power is " + playerWithMinimumEnginePower.getUsername());
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),playerWithMinimumEnginePower.getUsername() + " you lost crew members, you have to choose which ones");
                    currentEffect = listEffects.removeFirst();
                    //i go to decision astronaut state
                    number++;
                    currentState = AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS;
                }

            }
            case DECISION_CANNON -> {
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You have decided the coordinates of the cannons you wanna activate");
                cannons = new ArrayList<>(coordinates);
                for (int i = 0; i < coordinates.size(); i++) {
                    ship.turnOnComponent(cannons.get(i), batteries.get(i).getKey());
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"turning on the cannon at " + cannons.get(i).x + ", " + cannons.get(i).y);
                }
                double firePower = ship.getTotalFirePower();
                if (firePower < minimumFirePower) {
                    minimumFirePower = firePower;
                    playerWithMinimumFirePower = gameBoard.getCurrentPlayer();
                }
                //turn off the cannons
                for (int i = 0; i < coordinates.size(); i++) {
                    ship.turnOff(cannons.get(i));
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"turning off the cannon at " + cannons.get(i).x + ", " + cannons.get(i).y);
                }

                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Now i check if it was the last player");
                try {
                    //controllo se il current player era l'ultimo o no
                    //se non è l'ultimo richiamo check requirements sul player dopo
                    gameBoard.nextPlayer();
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"It wasn't the last player, go on with the next player");
                    checkRequirements3();
                } catch (IllegalSetOfNextPlayerException e) {
                    //se era l'ultimo catturo l'eccezione propagata verso l'alto da nextPlayer
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"It was the last player");
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Player with minimum fire power is " + playerWithMinimumFirePower.getUsername());
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),playerWithMinimumFirePower.getUsername() + " you are gonna be hit by some cannons");
                    currentEffect = listEffects.removeFirst();
                    gameBoard.setCurrentPlayer(playerWithMinimumFirePower);
                    number++;
                    currentState = AdventureStatesTypes.ROLL_DICES;
                }
            }
            case DECISION_SHIELDS -> {
                try {
                    List<Point> coordinatesOfShield = new ArrayList<>(coordinates);
                    activateComponentWithBatteries(coordinatesOfShield, ship);
                    currentState = AdventureStatesTypes.EFFECT;
                    applyEffect();
                } catch (IllegalNumOfActivableException e) {
                    userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Rechoose correctly the number of shields as the same as batteries");
                }
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
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Big cannonball! You can't defend yourself with shields! You can't do nothing! Hope you have been lucky with the dices!");
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
        } else {
            long availableShields = currentShipBoard.getActivatableComponents(ComponentType.SHIELD).size();
            if (availableShields > 0) {
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Puoi attivare gli scudi. Scegli le coordinate delle batterie da utilizzare.");
                currentState = AdventureStatesTypes.DECISION_BATTERIES;
            } else {
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You don't have shield to activate. You can't do nothing! Hope you have been lucky with the dices!");
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
        }
    }


    private void activateComponentWithBatteries(List<Point> coordinatesOfActivable, ShipBoard shipBoard) {
        if (coordinatesOfActivable.size() != batteries.size()) {
            throw new IllegalNumOfActivableException("You have to choose at max the same number of cannons as batteries");
        }
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You have decided the coord for the components");
        for (int i = 0; i < coordinatesOfActivable.size(); i++) {
            shipBoard.turnOnComponent(coordinatesOfActivable.get(i), batteries.get(i).getKey());
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
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You have finished the effects of the card");
            update();
        } else {
            currentEffect = listEffects.removeFirst();
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Next effect: " + currentEffect.getDescription());
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Now the leader must roll the dices again!");
            currentState = AdventureStatesTypes.ROLL_DICES;
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