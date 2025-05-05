package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectModifyCrew;
import it.polimi.ingsw.is25am49.server.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.server.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalNumOfActivableException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import javafx.util.Pair;

import javax.xml.transform.Source;
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
    private int number=0;
    private final int daysLost;
    private double minimumFirePower = 1;
    private static final int BIG_CANNONBALL_LEVEL = 1;
    private List<Point> coordinatesOfActivable = new ArrayList<>();


    public WarZoneCard(GameBoard gameBoard, String description, int level, String  name,int daysLost, int lostAstronauts, List<CardEffect> effects) {
        super(gameBoard, description, level,name);
        listEffects = new ArrayList<>();
        listEffects.add(new EffectModifyCrew("ModifyCrew", lostAstronauts));
        this.listEffects.addAll(effects);
        minimumEnginePower = 1;
        this.daysLost = daysLost;


    }

    @Override
    public void drawCard() {
        super.drawCard();
        playerWithMinimumEnginePower = gameBoard.getLeader();
        System.out.println("You have found a War Zone! Good luck, it will be tough to get out of it.");
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
            System.out.println("you are the only player, you skip this card");
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
        System.out.println("Player with minimum crew is " + gameBoard.getCurrentPlayer().getUsername());
        System.out.println("You lost 3 travel days");
        gameBoard.getCurrentPlayer().setDaysThisTurn(-daysLost);
        gameBoard.updateLeaderboard();
        System.out.println("leader updated! now is : " + gameBoard.getLeader().getUsername());

        //now current player is the leader again
        gameBoard.setCurrentPlayer(gameBoard.getLeader());
        checkRequirements2();

    }

    public void checkRequirements2() {

        //check the player with less engine power
        System.out.println("you are in phase 2 of the war zone, now we will check the player with less engine power");
        System.out.println("i check if " + gameBoard.getCurrentPlayer().getUsername() + " has enough batteries to activate double motors");
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumberOfBatteries = ship.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        if (totalNumberOfBatteries > 0) {
            System.out.println("you have enough batteries to activate your double motors. One battery activates one motor. " +
                    "You can activate up to " + totalNumberOfBatteries + " double motors");
            //I change state to decison batteries to decide how much to activate
            currentState = AdventureStatesTypes.DECISION_BATTERIES;
        } else {
            //non ho abbastanza batterie
            System.out.println("You don't have enough batteries to activate your double motors");
            int enginePower = ship.getTotalEnginePower();
            //System.out.println(enginePower);
            if (enginePower < minimumEnginePower) {
                //System.out.println("aggiornamento minimum power" + enginePower);
                minimumEnginePower = enginePower;
                playerWithMinimumEnginePower = gameBoard.getCurrentPlayer();
                //System.out.println(playerWithMinimumEnginePower.getUsername());
            }
            System.out.println("Now i check if it was the last player");
            try {
                //controllo se il current player era l'ultimo o no
                //se non è l'ultimo richiamo check requirements sul player dopo
                gameBoard.nextPlayer();
                System.out.println("It wasn't the last player, go on with the next player");
                checkRequirements2();
            } catch (IllegalSetOfNextPlayerException e) {
                //se era l'ultimo catturo l'eccezione propagata verso l'alto da nextPlayer
                System.out.println("It was the last player");
                System.out.println("Player with minimum engine power is " + playerWithMinimumEnginePower.getUsername());
                System.out.println(playerWithMinimumEnginePower.getUsername() + " you lost 2 crew members, you have to choose which ones");
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
                    System.out.println("you have chosen " + coordinates.size() + " batteries");
                    this.batteries = new ArrayList<>(coordinates);
                    System.out.println("Now you have to choose which motors you wanna activate");
                    currentState = AdventureStatesTypes.DECISION_MOTOR;

                }
                else if (number==1){
                    if(coordinates.size() > ship.getTotalNumberOfaToken(Token.BATTERY_CELLS)) {
                        throw new IllegalArgumentException("you have chosen more batteries than you have");
                    }
                    System.out.println("you have chosen " + coordinates.size() + " batteries");
                    this.batteries = new ArrayList<>(coordinates);
                    System.out.println("Now you have to choose which cannons you wanna activate");
                    currentState = AdventureStatesTypes.DECISION_CANNON;

                }
                else{
                    if(coordinates.size() > ship.getTotalNumberOfaToken(Token.BATTERY_CELLS)) {
                        throw new IllegalArgumentException("you have chosen more batteries than you have");
                    }
                    System.out.println("you have chosen " + coordinates.size() + " batteries to activate your shield");
                    this.batteries = new ArrayList<>(coordinates);
                    System.out.println("Now you have to choose which shields you wanna activate");
                    currentState = AdventureStatesTypes.DECISION_SHIELDS;
                }
            }
            case DECISION_ASTRONAUTS_OR_ALIENS -> {
                //i have to choose which astroauts or aliens to lose
                gameBoard.setCurrentPlayer(playerWithMinimumEnginePower);
                currentEffect.setListOfCoordinates(coordinates);
                System.out.println("You have chosen the astronauts or aliens you wanna lose");
                currentEffect.action(gameBoard.getCurrentPlayer());
                System.out.println(gameBoard.getCurrentPlayer().getUsername() + " has lost two crew members");
                gameBoard.setCurrentPlayer(gameBoard.getLeader());
                System.out.println("The leader is " + gameBoard.getLeader().getUsername());
                checkRequirements3();

            }
            default -> throw new IllegalArgumentException("wrong state");


        }
    }
    public void checkRequirements3() {
        System.out.println("now i check which player has the minimum fire power");
        System.out.println("i check if " + gameBoard.getCurrentPlayer().getUsername() + " has enough batteries to activate double cannons");
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumberOfBatteries = ship.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        if(totalNumberOfBatteries>0 && ship.getActivatableComponents(ComponentType.DOUBLE_CANNON).size()>0){
            System.out.println("you have enough batteries to activate your double Cannons. One battery activates one cannon. " +
                    "You can activate up to " + totalNumberOfBatteries + " double cannon");
            //I change state to decison batteries to decide how much to activate
            currentState = AdventureStatesTypes.DECISION_BATTERIES;
        } else {
            //non ho abbastanza batterie
            System.out.println("You don't have enough batteries to activate your double cannons");
            double firePower = ship.getTotalFirePower();
            //System.out.println(enginePower);
            if (firePower < minimumFirePower) {
                //System.out.println("aggiornamento minimum power" + enginePower);
                minimumFirePower = firePower;
                playerWithMinimumFirePower = gameBoard.getCurrentPlayer();
                //System.out.println(playerWithMinimumEnginePower.getUsername());
            }
            System.out.println("Now i check if it was the last player");
            try {
                //controllo se il current player era l'ultimo o no
                //se non è l'ultimo richiamo check requirements sul player dopo
                gameBoard.nextPlayer();
                System.out.println("It wasn't the last player, go on with the next player");
                checkRequirements3();
            } catch (IllegalSetOfNextPlayerException e) {
                //se era l'ultimo catturo l'eccezione propagata verso l'alto da nextPlayer
                System.out.println("It was the last player");
                System.out.println("Player with minimum fire power is " + playerWithMinimumFirePower.getUsername());
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
                System.out.println("You have decided the coordinates of the motors you wanna activate");
//                if(batteries.size()!=coordinates.size()){
//                    throw new IllegalArgumentException("you have chosen a different number of batteries and motors");
//                }
                motors = new ArrayList<>(coordinates);
                //i activte all the cannons
                for (int i = 0; i < coordinates.size(); i++) {
                    ship.turnOnComponent(motors.get(i), batteries.get(i).getKey());
                    System.out.println("turning on the motor at " + motors.get(i).x + ", " + motors.get(i).y);
                }
                int enginePower = ship.getTotalEnginePower();
                if (enginePower < minimumEnginePower) {
                    minimumEnginePower = enginePower;
                    playerWithMinimumEnginePower = gameBoard.getCurrentPlayer();
                }
                //turn off the motors
                for (int i = 0; i < coordinates.size(); i++) {
                    ship.turnOff(motors.get(i));
                    System.out.println("turning off the motor at " + motors.get(i).x + ", " + motors.get(i).y);
                }
                System.out.println("Now i check if it was the last player");
                try {
                    //controllo se il current player era l'ultimo o no
                    //se non è l'ultimo richiamo check requirements sul player dopo
                    gameBoard.nextPlayer();
                    System.out.println("It wasn't the last player, go on with the next player");
                    checkRequirements2();
                } catch (IllegalSetOfNextPlayerException e) {
                    //se era l'ultimo catturo l'eccezione propagata verso l'alto da nextPlayer
                    System.out.println("It was the last player");
                    System.out.println("Player with minimum fire power is " + playerWithMinimumEnginePower.getUsername());
                    System.out.println(playerWithMinimumEnginePower.getUsername() + " you lost crew members, you have to choose which ones");
                    currentEffect = listEffects.removeFirst();
                    //i go to decision astronaut state
                    number++;
                    currentState = AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS;
                }

            }
            case DECISION_CANNON -> {
                System.out.println("You have decided the coordinates of the cannons you wanna activate");
                cannons = new ArrayList<>(coordinates);
                for(int i=0; i<coordinates.size(); i++){
                    ship.turnOnComponent(cannons.get(i), batteries.get(i).getKey());
                    System.out.println("turning on the cannon at " + cannons.get(i).x + ", " + cannons.get(i).y);
                }
                double firePower = ship.getTotalFirePower();
                if(firePower < minimumFirePower){
                    minimumFirePower = firePower;
                    playerWithMinimumFirePower = gameBoard.getCurrentPlayer();
                }
                //turn off the cannons
                for(int i=0; i<coordinates.size(); i++){
                    ship.turnOff(cannons.get(i));
                    System.out.println("turning off the cannon at " + cannons.get(i).x + ", " + cannons.get(i).y);
                }

                System.out.println("Now i check if it was the last player");
                try {
                    //controllo se il current player era l'ultimo o no
                    //se non è l'ultimo richiamo check requirements sul player dopo
                    gameBoard.nextPlayer();
                    System.out.println("It wasn't the last player, go on with the next player");
                    checkRequirements3();
                } catch (IllegalSetOfNextPlayerException e) {
                    //se era l'ultimo catturo l'eccezione propagata verso l'alto da nextPlayer
                    System.out.println("It was the last player");
                    System.out.println("Player with minimum fire power is " + playerWithMinimumFirePower.getUsername());
                    System.out.println(playerWithMinimumFirePower.getUsername() + " you are gonna be hit by some cannons");
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
                    System.out.println("Rechoose correctly the number of shields as the same as batteries");
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
            System.out.println("Big cannonball! You can't defend yourself with shields! You can't do nothing! Hope you have been lucky with the dices!");
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
        } else {
            long availableShields = currentShipBoard.getActivatableComponents(ComponentType.SHIELD).size();
            if (availableShields > 0) {
                System.out.println("Puoi attivare gli scudi. Scegli le coordinate delle batterie da utilizzare.");
                currentState = AdventureStatesTypes.DECISION_BATTERIES;
            } else {
                System.out.println("You don't have shield to activate. You can't do nothing! Hope you have been lucky with the dices!");
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
        }
    }


    private void activateComponentWithBatteries(List<Point> coordinatesOfActivable, ShipBoard shipBoard) {
        if (coordinatesOfActivable.size() != batteries.size()) {
            throw new IllegalNumOfActivableException("You have to choose at max the same number of cannons as batteries");
        }
        System.out.println("You have decided the coord for the components");
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
            System.out.println("You have finished the effects of the card");
            update();
        } else {
            currentEffect = listEffects.removeFirst();
            System.out.println("Next effect: " + currentEffect.getDescription());
            System.out.println("Now the leader must roll the dices again!");
            currentState = AdventureStatesTypes.ROLL_DICES;
        }
    }


}
