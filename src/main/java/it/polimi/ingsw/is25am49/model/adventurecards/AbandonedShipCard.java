package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectModifyCrew;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;

public class AbandonedShipCard extends AdventureCard {

    int lostAstronauts;
    boolean isLanded = false;

    public AbandonedShipCard(GameBoard gameBoard, String description, int daysLost, int level, int creditsEarned, int lostAstronauts){
        super(gameBoard,description,daysLost,level,creditsEarned);
        this.lostAstronauts = lostAstronauts;
        listEffects.add(new EffectModifyCrew("Remove astronauts", lostAstronauts));
    }

    @Override
    public void drawCard() {
        super.drawCard();
        System.out.println("You have found an abandoned ship! You can sell it to your crew members for some credits");
        checkRequirements();
    }

    @Override
    public void checkRequirements() {
        ShipBoard shipBoard =player.getShipBoard();
        int totalNumCrewMembers = shipBoard.getTotalNumberOfaToken(Token.ASTRONAUT)+shipBoard.getTotalNumberOfaToken(Token.BROWN_ALIENS)+shipBoard.getTotalNumberOfaToken(Token.PURPLE_ALIENS);
        currentEffect = listEffects.getFirst();
        if (totalNumCrewMembers > lostAstronauts) {
            System.out.println("You found an abandoned ship! You can sell it to your crew members for some credits");
            return null; //user must decide if he wants to use the card
        }
        else {
            System.out.println("You don't have enough astronauts. If you sell the ship to them you have to quit the game!");
            return null; //we must remain in check requirements and see the next player
        }
    }

    @Override
    public void landingOnStationOrShip() throws IllegalMethodOnThisStateException {
        System.out.println("You have sell the abandoned ship to your crew members");
        isLanded = true;
    }

    //this method below returns true if we must change player
    //false if we must remain in the same player OR IF THE CARD ISFINISHED
    //isFinished is set to true if the card is finished and a new card has to be drawn
    @Override
    public void applyEffect(Player player) {
        if (isLanded) {
            System.out.println("You win " + creditsEarned + " credits");
            player.addCredit( creditsEarned);
            listEffects.getFirst().action(player);
            isFinished = true;
            int totalNumAstronauts = player.getShipBoard().getTotalNumberOfaToken(Token.ASTRONAUT);
            if(totalNumAstronauts==0){
                System.out.println("You have no more astronauts, you have to quit the game");
                //TODO LOGICA QUIT GAME
            }
            return null;
        }
        return null;
    }




}
