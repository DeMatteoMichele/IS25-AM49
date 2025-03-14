package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectModifyCrew;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;

import java.util.List;

public class AbandonedShipCard extends AdventureCard {

    int lostAstronauts;
    boolean isLanded = false;

    public AbandonedShipCard(String description, int daysLost, int level, int creditsEarned, int lostAstronauts, List<CardEffect> ListEffects){
        super(description,daysLost,level,creditsEarned,ListEffects );
        this.lostAstronauts = lostAstronauts;
        ListEffects.add(new EffectModifyCrew("EffectModifyCrew",lostAstronauts));
    }

    // methods return an int based on what state we must go to
    // 0 = check requirements
    // 1 = decision
    // 2 = effect
    @Override
    public int checkRequirements(Player player) {
        ShipBoard shipBoard =player.getShipBoard();
        int totalNumCrewMembers = shipBoard.getTotalNumberOfaToken(Token.ASTRONAUT)+shipBoard.getTotalNumberOfaToken(Token.BROWN_ALIENS)+shipBoard.getTotalNumberOfaToken(Token.PURPLE_ALIENS);
        if (totalNumCrewMembers > lostAstronauts) {
            System.out.println("You found an abandoned ship! You can sell it to your crew members for some credits");
            return 1; //user must decide if he wants to use the card
        }
        else {
            System.out.println("You don't have enough astronauts to play this card");
            return 0; //we must remain in check requirements and see the next player
        }
    }

    @Override
    public void landingOnStationOrShip() {
        System.out.println("You have sell the abandoned ship to your crew members. Goodbye!");
        isLanded = true;
    }

    //this method below returns true if we must change player
    //false if we must remain in the same player
    //isFinished is set to true if the card is finished and a new card has to be drawn
    @Override
    public boolean applyEffect(Player player) {
        if (isLanded) {
            System.out.println("You win " + creditsEarned + " credits");
            listEffects.getFirst().action(player);
            player.addCredit( creditsEarned);
            isFinished = true;
            return false;
        }
        return false;
    }




}
