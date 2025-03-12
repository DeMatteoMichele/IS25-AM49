package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SmugglerCard extends AdventureCard {
    private final int minimumFirePower;
    private final List<Token> LostTokens=new ArrayList<>();
    private final List<Token> Prize=new ArrayList<>();

    public SmugglerCard(String description, int daysLost, int level, int creditsEarned, int minimumFirePower, List<CardEffect> ListEffects, List<Token> LostTokens, List<Token> Prize) {
        super(description, daysLost, level, creditsEarned, ListEffects);
        this.minimumFirePower = minimumFirePower;
        this.LostTokens.addAll(LostTokens);
        this.Prize.addAll(Prize);
    }


    @Override
    public int checkRequirements(Player player) {
        double standardFirePower =player.getShipBoard().getTotalFirePower();
        ShipBoard shipBoard=player.getShipBoard();
        int numBatteries=shipBoard.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        List<Point> activatableCannons=shipBoard.getActivatableComponents(ComponentType.CANNON);
        double firePowerUserCanActivateWithBatteries = activatableCannons.stream()
                .mapToDouble(point->shipBoard.getTile(point).getFirePower())
                .sorted()
                .limit(numBatteries)
                .sum();
        if (standardFirePower >minimumFirePower) {
            System.out.println("Hai" + player.getShipBoard().getTotalFirePower() + " potenza di fuoco, puoi sconfiggere i contrabbandieri!");
            return 2; //we must go to state effect
        }
        else if (standardFirePower ==minimumFirePower) {
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


    @Override
    public void applyEffect(Player player) {
        double totalFirePower=player.getShipBoard().getTotalFirePower();
        if(totalFirePower > minimumFirePower){
            System.out.println("Hai sconfitto i contrabbandieri con la tua potenza di fuoco! Vinci delle casse per averli sconfitti");
        }
    }

    @Override
    public List<Token> getLostTokens() {
        return LostTokens;
    }

    @Override
    public List<Token> getPrize() {
        return Prize;
    }

}
