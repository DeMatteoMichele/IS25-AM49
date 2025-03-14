package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;

import java.awt.*;
import java.util.List;

public abstract class EnemyCard extends AdventureCard {
    protected final int minimumFirePower;
    protected final int lostAstronauts;

    public EnemyCard(String description, int daysLost, int level, int creditsEarned, int minimumFirePower, int lostAstronauts, List<CardEffect> listEffects) {
        super(description, daysLost, level, creditsEarned, listEffects);
        this.minimumFirePower = minimumFirePower;
        this.lostAstronauts = lostAstronauts;
        isFinished=false;
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
            if (standardFirePower + firePowerUserCanActivateWithBatteries < minimumFirePower) {
                System.out.println("You don't have enough batteries to defeat"+getClass().getSimpleName());
                return 2; //we must go to state effect
            }
            else if (standardFirePower + firePowerUserCanActivateWithBatteries == minimumFirePower) {
                System.out.println("You have enough batteries to draw with"+getClass().getSimpleName());
                return 1; //we must go to state decision
            }
            else {
                System.out.println("You have enough batteries to win against"+getClass().getSimpleName());
                return 1; //we must go to state decision
            }
        }
    }


    //this method below returns true if we must change player
    //false if we must remain in the same player OR IF THE CARD ISFINISHED
    //isFinished is set to true if the card is finished and a new card has to be drawn

    //****************************************************************************************

    //THIS METHOD IS INHERITED ONLY BY SMUGGLERSCARD AND SLAVERCARD

    //PIRATESCARD OVERRIDE THIS METHOD

    //****************************************************************************************
    @Override
    public boolean applyEffect(Player player) {
        double totalFirePower=player.getShipBoard().getTotalFirePower();
        if(totalFirePower > minimumFirePower){
            if (creditsEarned == 0) {
                System.out.println("You won against" + getClass().getSimpleName() + "! You win some goods");
                listEffects.getFirst().action(player); //wingoods effect
            } else {
                System.out.println("You won against" + getClass().getSimpleName() + "! You win " + creditsEarned + " credits");
                player.addCredit( creditsEarned);
            }
            player.setDaysThisTurn(-daysLost);
            isFinished = true; //card has finished, now we go to draw state
            return false;
        }
        else if(totalFirePower == minimumFirePower){
            System.out.println("You have drawn with"+ getClass().getSimpleName() +"! You don't win anything");
            player.setDaysThisTurn(-daysLost);
            return true;
        }
        else {
            if (creditsEarned == 0) {
                System.out.println("You have been defeated by" + getClass().getSimpleName() + "! You lose some goods");
                listEffects.get(1).action(player); //lost effect
            } else {
                System.out.println("You have been defeated by" + getClass().getSimpleName() + "! You lose " + lostAstronauts + " poor astronauts!");
                listEffects.getFirst().action(player); //lose astronauts; uso getFirst perchè schiavisti ha solo un effetto
            }
            player.setDaysThisTurn(-daysLost);
            return true;
        }
    }

}
