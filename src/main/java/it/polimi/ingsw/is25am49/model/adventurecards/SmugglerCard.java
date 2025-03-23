package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;

import java.awt.*;
import java.util.List;

public class SmugglerCard extends AdventureCard {

    public SmugglerCard(GameBoard gameBoard, String description, int daysLost, int level, int creditsEarned, int minimumFirePower, int lostAstronauts, List<CardEffect> listEffects) {
        super(gameBoard,description, daysLost, level, creditsEarned);
        this.minimumFirePower = minimumFirePower;
        this.lostAstronauts = lostAstronauts;
        this.listEffects.addAll(listEffects);
    }

    private List<Point> coordinatesOfBatteries;
    private List<Point> coordinatesOfCannons;


    @Override
    public void drawCard() {
        super.drawCard();
        System.out.println("You have found a ship full of smugglers! You can defeat them to win some goods");
        checkRequirements();
    }

    @Override
    public void checkRequirements() {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        if (checkFirePower(shipBoard)) {
            System.out.println("You have enough fire power to defeat the smugglers! Choose where you want to put your new goods");
            currentEffect = listEffects.getFirst(); //the curreffect is wingoods
            currentState = AdventureStatesTypes.DECISION_GOODS;
        } else {
            if(canDefeatUsingBatteries(shipBoard)){
                System.out.println("You have enough batteries to defeat the smugglers! Choose what batteries do you want to use");
                currentState = AdventureStatesTypes.DECISION_BATTERIES;
            }
            else {
                System.out.println("You don't have enough fire power nor batteries to defeat the smugglers! You have to decide where you want to remove them");
                currentEffect = listEffects.get(1); //lose goods effect
                currentState = AdventureStatesTypes.DECISION_GOODS;

            }
        }

    }

    private boolean canDefeatUsingBatteries(ShipBoard shipBoard) {
        int numBatteries = shipBoard.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        List<Point> activatableCannons = shipBoard.getActivatableComponents(ComponentType.DOUBLE_CANNON);
        double firePowerUserCanActivateWithBatteries = activatableCannons.stream()
                .mapToDouble(point -> shipBoard.getTile(point).getFirePower())
                .sorted()
                .limit(numBatteries)
                .sum();
        return ((shipBoard.getTotalFirePower() + firePowerUserCanActivateWithBatteries) > minimumFirePower);
    }

    private boolean checkFirePower(ShipBoard shipBoard) {
        double totalFirePower = shipBoard.getTotalFirePower();
        return totalFirePower > minimumFirePower;
    }

    private void secondCheck(ShipBoard shipBoard){
        double totalFirePower = shipBoard.getTotalFirePower();
        if (totalFirePower > minimumFirePower) {
            System.out.println("You have now more firepower to defeat the smugglers! Choose where you want to put your new goods");
            currentEffect = listEffects.getFirst(); //the curreffect is wingoods
            currentState = AdventureStatesTypes.DECISION_GOODS;
        } else if(totalFirePower == minimumFirePower) {
            System.out.println("Now you have drawn with the smugglers!");
            try {
                gameBoard.nextPlayer();
                System.out.println("Now check on next player");
                checkRequirements();
            } catch (IllegalSetOfNextPlayerException e) {
                System.out.println("No more players, now to update");
                currentState = AdventureStatesTypes.UPDATE;
            }
        }
        else {
            System.out.println("You have been defeated by the smugglers! You lose some goods");
            System.out.println("You have to decide where to remove your goods");
            currentEffect = listEffects.get(1); //lost effect
            currentState = AdventureStatesTypes.DECISION_GOODS;
        }
    }

    @Override
    public void finishDecision(List<Point> coordinates) {
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        switch (currentState) {
            case DECISION_BATTERIES -> {
                System.out.println("You have decided the coord for the batteries. Now decide which cannons");
                coordinatesOfBatteries = coordinates;
                currentState= AdventureStatesTypes.DECISION_CANNON;
            }
            case DECISION_CANNON -> {
                System.out.println("You have decided the coord for the cannons");
                coordinatesOfCannons = coordinates;
                for (int i=0; i<coordinatesOfBatteries.size(); i++) {
                    shipBoard.turnOnComponent(coordinatesOfCannons.get(i), coordinatesOfBatteries.get(i));
                }
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
            case DECISION_GOODS -> {
                System.out.println("you have decided the coord for the goods");
                currentEffect.setListOfCoordinates(coordinates);
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
            default -> throw new IllegalArgumentException("Invalid state: " + currentState);
        }
    }



    @Override
    public void applyEffect() {
        super.applyEffect();
        Player currentPlayer=gameBoard.getCurrentPlayer();
        System.out.println("You are in applyeffect, yur goods will be modified");
        currentEffect.action(currentPlayer);
//        currentPlayer.addCredits(creditsEarned);
//        System.out.println("You have earned " + creditsEarned + " credits");
        currentPlayer.setDaysThisTurn(daysLost);
        currentState= AdventureStatesTypes.UPDATE;
    }

//    @Override
//    public AdventureStatesTypes checkRequirements(Player player) {
//        ShipBoard shipBoard=player.getShipBoard();
//        double standardFirePower =shipBoard.getTotalFirePower();
//        int numBatteries=shipBoard.getTotalNumberOfaToken(Token.BATTERY_CELLS);
//        List<Point> activatableCannons=shipBoard.getActivatableComponents(ComponentType.DOUBLE_CANNON);
//        double firePowerUserCanActivateWithBatteries = activatableCannons.stream()
//                .mapToDouble(point->shipBoard.getTile(point).getFirePower())
//                .sorted()
//                .limit(numBatteries)
//                .sum();
//        currentEffect=listEffects.getFirst();
//        if (standardFirePower > minimumFirePower) {
//            System.out.println("You have" + player.getShipBoard().getTotalFirePower() + " fire power, it is enough to defeat"+ getClass().getSimpleName());
//            return AdventureStatesTypes.EFFECT;
//        }
//        else if (standardFirePower == minimumFirePower) {
//            System.out.println("You have exactly the same fire power of"+ getClass().getSimpleName() +"! You can decide to activate some cannons or to mantain your ship as it is!");
//            return AdventureStatesTypes.DECISION_BATTERIES; //THEN IT MUST GO TO DECISION.CANNON
//        }
//        else {
//            if (standardFirePower + firePowerUserCanActivateWithBatteries < minimumFirePower) {
//                System.out.println("You don't have enough batteries to defeat"+getClass().getSimpleName());
//                return AdventureStatesTypes.EFFECT;
//            }
//            else if (standardFirePower + firePowerUserCanActivateWithBatteries == minimumFirePower) {
//                System.out.println("You have enough batteries to draw with"+getClass().getSimpleName());
//                return AdventureStatesTypes.DECISION_BATTERIES; //THEN IT MUST GO TO DECISION.CANNON
//            }
//            else {
//                System.out.println("You have enough batteries to win against"+getClass().getSimpleName());
//                return AdventureStatesTypes.DECISION_BATTERIES; //THEN IT MUST GO TO DECISION.CANNON
//            }
//        }
//    }
//
//
//    @Override
//    public void applyEffect() {
//        double totalFirePower=player.getShipBoard().getTotalFirePower();
//        if(totalFirePower > minimumFirePower){
//            System.out.println("You won against" + getClass().getSimpleName() + "! You win some goods");
//            player.setDaysThisTurn(-daysLost);
//            return AdventureStatesTypes.DECISION_GOODS;
//        }
//        else if(totalFirePower == minimumFirePower){
//            System.out.println("You have drawn with"+ getClass().getSimpleName() +"! You don't win anything");
//            player.setDaysThisTurn(-daysLost);
//            return AdventureStatesTypes.CHECK_REQUIREMENTS; //WE MUST CHANGE PLAYER
//        }
//        else {
//                System.out.println("You have been defeated by" + getClass().getSimpleName() + "! You lose some goods");
//                listEffects.get(1).action(player); //lost effect
//                System.out.println("You have been defeated by" + getClass().getSimpleName() + "! You lose " + lostAstronauts + " poor astronauts!");
//                listEffects.getFirst().action(player); //lose astronauts; uso getFirst perchè schiavisti ha solo un effetto
//                player.setDaysThisTurn(-daysLost);
//                return AdventureStatesTypes.CHECK_REQUIREMENTS; //WE MUST CHANGE PLAYER
//        }
//    }

}
