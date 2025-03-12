package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;

import java.awt.*;
import java.util.List;

public class SlaverCard extends AdventureCard {

    int lostAstronauts;
    int minimumFirePower;

    public SlaverCard(String description, int daysLost, int level, int creditsEarned, int lostAstronauts, int minimumFirePower, List<CardEffect> listEffects) {
        super(description, daysLost, level, creditsEarned, listEffects);
        this.lostAstronauts = lostAstronauts;
        this.minimumFirePower = minimumFirePower;
    }

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
            System.out.println("Hai" + player.getShipBoard().getTotalFirePower() + " potenza di fuoco, puoi sconfiggere gli schiavisti!!");
            return 2; //we must go to state effect
        }
        else if (standardFirePower == minimumFirePower) {
            System.out.println("Hai esattamente la stessa potenza di fuoco degli schiavisti! Pareggio!");
            return 0; //we remain in check requirements to check the next player
        }
        else {
            if (standardFirePower + firePowerUserCanActivateWithBatteries < minimumFirePower) {
                System.out.println("Non hai abbastanza batterie per sconfiggere gli Schiavisti!");
                return 2; //we must go to state effect
            }
            else if (standardFirePower + firePowerUserCanActivateWithBatteries == minimumFirePower) {
                System.out.println("Hai abbastanza batterie per pareggiare con gli schiavisti!");
                return 1; //we must go to state decision
            }
            else {
                System.out.println("Hai abbastanza batterie per sconfiggere gli schiavisti");
                return 1; //we must go to state decision
            }
        }
    }

//    @Override
//    public void askForDecision(Player player) {
//
//    }

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
        player.setDaysThisTurn(-daysLost);
        return true;//we must go to state check requirements to check the next player

    }

}
