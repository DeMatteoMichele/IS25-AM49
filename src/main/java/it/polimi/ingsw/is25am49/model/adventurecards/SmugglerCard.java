package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;

import java.awt.*;
import java.util.List;

public class SmugglerCard extends AdventureCard {

    public SmugglerCard(String description, int daysLost, int level, int creditsEarned, int minimumFirePower, int lostAstronauts, List<CardEffect> listEffects) {
        super(description, daysLost, level, creditsEarned);
        this.minimumFirePower = minimumFirePower;
        this.lostAstronauts = lostAstronauts;
        this.listEffects.addAll(listEffects);
    }

    @Override
    public AdventureStatesTypes checkRequirements(Player player) {
        ShipBoard shipBoard=player.getShipBoard();
        double standardFirePower =shipBoard.getTotalFirePower();
        int numBatteries=shipBoard.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        List<Point> activatableCannons=shipBoard.getActivatableComponents(ComponentType.DOUBLE_CANNON);
        double firePowerUserCanActivateWithBatteries = activatableCannons.stream()
                .mapToDouble(point->shipBoard.getTile(point).getFirePower())
                .sorted()
                .limit(numBatteries)
                .sum();
        currentEffect=listEffects.getFirst();
        if (standardFirePower > minimumFirePower) {
            System.out.println("You have" + player.getShipBoard().getTotalFirePower() + " fire power, it is enough to defeat"+ getClass().getSimpleName());
            return AdventureStatesTypes.EFFECT;
        }
        else if (standardFirePower == minimumFirePower) {
            System.out.println("You have exactly the same fire power of"+ getClass().getSimpleName() +"! You can decide to activate some cannons or to mantain your ship as it is!");
            return AdventureStatesTypes.DECISION_BATTERIES; //THEN IT MUST GO TO DECISION.CANNON
        }
        else {
            if (standardFirePower + firePowerUserCanActivateWithBatteries < minimumFirePower) {
                System.out.println("You don't have enough batteries to defeat"+getClass().getSimpleName());
                return AdventureStatesTypes.EFFECT;
            }
            else if (standardFirePower + firePowerUserCanActivateWithBatteries == minimumFirePower) {
                System.out.println("You have enough batteries to draw with"+getClass().getSimpleName());
                return AdventureStatesTypes.DECISION_BATTERIES; //THEN IT MUST GO TO DECISION.CANNON
            }
            else {
                System.out.println("You have enough batteries to win against"+getClass().getSimpleName());
                return AdventureStatesTypes.DECISION_BATTERIES; //THEN IT MUST GO TO DECISION.CANNON
            }
        }
    }


    @Override
    public void applyEffect(Player player) {
        double totalFirePower=player.getShipBoard().getTotalFirePower();
        if(totalFirePower > minimumFirePower){
            System.out.println("You won against" + getClass().getSimpleName() + "! You win some goods");
            player.setDaysThisTurn(-daysLost);
            return AdventureStatesTypes.DECISION_GOODS;
        }
        else if(totalFirePower == minimumFirePower){
            System.out.println("You have drawn with"+ getClass().getSimpleName() +"! You don't win anything");
            player.setDaysThisTurn(-daysLost);
            return AdventureStatesTypes.CHECK_REQUIREMENTS; //WE MUST CHANGE PLAYER
        }
        else {
                System.out.println("You have been defeated by" + getClass().getSimpleName() + "! You lose some goods");
                listEffects.get(1).action(player); //lost effect
                System.out.println("You have been defeated by" + getClass().getSimpleName() + "! You lose " + lostAstronauts + " poor astronauts!");
                listEffects.getFirst().action(player); //lose astronauts; uso getFirst perchè schiavisti ha solo un effetto
                player.setDaysThisTurn(-daysLost);
                return AdventureStatesTypes.CHECK_REQUIREMENTS; //WE MUST CHANGE PLAYER
        }
    }

}
