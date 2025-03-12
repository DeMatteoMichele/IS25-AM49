package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;

import java.awt.*;
import java.util.List;

public class SmugglerCard extends AdventureCard {
    private final int minimumFirePower;

    public SmugglerCard(String description, int daysLost, int level, int creditsEarned, int minimumFirePower, List<CardEffect> ListEffects) {
        super(description, daysLost, level, creditsEarned, ListEffects);
        this.minimumFirePower = minimumFirePower;
    }

    // methods return an int based on what state we must go to
    // 0 = check requirements
    // 1 = decision
    // 2 = effect
    @Override
    public int checkRequirements(Player player) { // methods return an int based on what state we must go to
        double standardFirePower =player.getShipBoard().getTotalFirePower();
        ShipBoard shipBoard=player.getShipBoard();
        int numBatteries=shipBoard.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        List<Point> activatableCannons=shipBoard.getActivatableComponents(ComponentType.CANNON);
        double firePowerUserCanActivateWithBatteries = activatableCannons.stream()
                .mapToDouble(point->shipBoard.getTile(point).getFirePower())
                .sorted()
                .limit(numBatteries)
                .sum();
        if (standardFirePower > minimumFirePower) {
            System.out.println("Hai" + player.getShipBoard().getTotalFirePower() + " potenza di fuoco, puoi sconfiggere i contrabbandieri!");
            return 2; //we must go to state effect
        }
        else if (standardFirePower == minimumFirePower) {
            System.out.println("Hai esattamente la stessa potenza di fuoco dei contrabbandieri! Pareggio!");
            return 0; //we remain in check requirements to check the next player
        }
        else {
            if (standardFirePower + firePowerUserCanActivateWithBatteries < minimumFirePower) {
            System.out.println("Non hai abbastanza batterie per sconfiggere i Contrabbandieri!");
            return 2; //we must go to state effect
            }   
            else if (standardFirePower + firePowerUserCanActivateWithBatteries == minimumFirePower) {
            System.out.println("Hai abbastanza batterie per pareggiare con i contrabbandieri");
            return 1; //we must go to state decision
            } 
            else {
            System.out.println("Hai abbastanza batterie per sconfiggere i contrabbandieri");
            return 1; //we must go to state decision
            }
        }
    }

//    @Override
//    public void askForDecision(Player player) {
//
//    }


    //method return true if we must change player
    //false if we must remain in the same player
    @Override
    public boolean applyEffect(Player player) {
        double totalFirePower=player.getShipBoard().getTotalFirePower();
        if(totalFirePower > minimumFirePower){
            System.out.println("Hai sconfitto i contrabbandieri con la tua potenza di fuoco! Vinci delle casse per averli sconfitti");
            listEffects.get(0).action(player); //effetto wingoods
        }
        if(totalFirePower == minimumFirePower){
            System.out.println("Hai pareggiato con i contrabbandieri! Non vinci nulla");
        }
        if(totalFirePower < minimumFirePower){
            System.out.println("Hai perso contro i contrabbandieri! Perdi delle casse");
            listEffects.get(1).action(player); //effetto losegoods
        }
        return true;//we must go to state check requirements to check the next player
    }




}
