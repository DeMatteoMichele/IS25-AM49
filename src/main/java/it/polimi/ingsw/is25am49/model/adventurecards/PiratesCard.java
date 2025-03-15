package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;

import java.awt.*;
import java.util.List;

public class PiratesCard extends EnemyCard {

    int index;
    public PiratesCard(String description, int daysLost, int level, int creditsEarned, int minimumFirePower, int lostAstronauts, List<CardEffect> ListEffects) {
        super(description, daysLost, level, creditsEarned, minimumFirePower, lostAstronauts, ListEffects);
        index=0;
    }


    // methods return an int based on what state we must go to
    // 0 = check requirements
    // 1 = decision
    // 2 = effect
    @Override
    public int checkRequirements(Player player) {
        double standardFirePower =player.getShipBoard().getTotalFirePower();
        ShipBoard shipBoard=player.getShipBoard();
        int numBatteries=shipBoard.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        List<Point> activatableCannons=shipBoard.getActivatableComponents(ComponentType.DOUBLE_CANNON);
        double firePowerUserCanActivateWithBatteries = activatableCannons.stream()
                .mapToDouble(point->shipBoard.getTile(point).getFirePower())
                .sorted()
                .limit(numBatteries)
                .sum();
        if (standardFirePower > minimumFirePower) {
            System.out.println("You have" + player.getShipBoard().getTotalFirePower() + " fire power, it is enough to defeat"+ getClass().getSimpleName());
            return 2; //we must go to state effect
        }
        else if (standardFirePower == minimumFirePower) {
            System.out.println("You have exactly the same fire power of"+ getClass().getSimpleName() +"! You can decide to activate some cannons or to mantain your ship as it is!");
            return 1; //we must go to state decision
        }
        else {
            if ((standardFirePower + firePowerUserCanActivateWithBatteries < minimumFirePower)&&index==0) {
                System.out.println("You don't have enough batteries to defeat"+getClass().getSimpleName());
                rollDices();
                index++;
                return 1; //we must go to state decision
            }
            else if ((standardFirePower + firePowerUserCanActivateWithBatteries < minimumFirePower)) {
                rollDices();
                return 1; //we must go to state decision
            }
            else if ((standardFirePower + firePowerUserCanActivateWithBatteries == minimumFirePower)&&index==0) {
                System.out.println("You have enough batteries to draw with"+getClass().getSimpleName());
                rollDices();
                index++;
                return 1; //we must go to state decision
            }
            else if ((standardFirePower + firePowerUserCanActivateWithBatteries == minimumFirePower)) {
                rollDices();
                return 1; //we must go to state decision
            }
            else if ((standardFirePower + firePowerUserCanActivateWithBatteries > minimumFirePower)&&index==0) {
                System.out.println("You have enough batteries to win against"+getClass().getSimpleName());
                rollDices();
                index++;
                return 1; //we must go to state decision
            }
            else {
                rollDices();
                return 1; //we must go to state decision
            }
        }
    }



    //this method below returns true if we must change player
    //false if we must remain in the same player
    //THIS METHOD OVVERIDE THE METHOD IN ENEMYCARD
    @Override
    public boolean applyEffect(Player player) {
        double totalFirePower=player.getShipBoard().getTotalFirePower();
        if(totalFirePower>minimumFirePower){
            System.out.println("You have defeated" + getClass().getSimpleName() + "! You win " + creditsEarned + " credits");
            player.addCredit( creditsEarned);
            player.setDaysThisTurn(-daysLost);
            isFinished=true;
            return false;
        }
        else if(totalFirePower==minimumFirePower){
            System.out.println("You have drawn with" + getClass().getSimpleName() + "! You are safe from the attack, but you don't win anything!");
            player.setDaysThisTurn(-daysLost);
            return true;
        }
        else if(totalFirePower<minimumFirePower&&!listEffects.isEmpty()){
            listEffects.getFirst().action(player);
            listEffects.removeFirst();
            currentEffect=listEffects.getFirst();
            return false;
        }
        else {
            System.out.println("You have been defeated by" + getClass().getSimpleName() + "! The cannonades are terminated!");
            player.setDaysThisTurn(-daysLost);
            isFinished=true;
            return false;
        }

        //TODO LOGIC OF ROLL DICES AND DECISION
    }
}
