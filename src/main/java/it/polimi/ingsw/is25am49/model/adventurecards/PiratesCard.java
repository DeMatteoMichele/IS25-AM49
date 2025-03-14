package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;

import java.awt.*;
import java.util.List;

public class PiratesCard extends EnemyCard {

    int currentEffectIndex;
    public PiratesCard(String description, int daysLost, int level, int creditsEarned, int minimumFirePower, int lostAstronauts, List<CardEffect> ListEffects) {
        super(description, daysLost, level, creditsEarned, minimumFirePower, lostAstronauts, ListEffects);
        currentEffectIndex=0;
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
        else{
            System.out.println("You can't defeat" + getClass().getSimpleName() + "! Now they attack you with cannonades!");
            System.out.println("Before every cannonade you have to throw the dices! You can decide to activate some shield or to mantain your ship as it is!");
            System.out.println("Remember: you can't defend your ship from big cannonades!");
            return false;
        }
        //TODO LOGIC OF ROLL DICES AND DECISION
    }



}
