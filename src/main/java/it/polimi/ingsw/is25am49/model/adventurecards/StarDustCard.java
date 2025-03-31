package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalSetOfNextPlayerException;

import java.util.List;

public class StarDustCard extends AdventureCard {

    public StarDustCard(GameBoard gameBoard, String description, int daysLost, int level, int creditsEarned) {
        super(gameBoard, description, daysLost, level, creditsEarned);
    }


    @Override
    public void drawCard() {
        super.drawCard();
        System.out.println("You have found a Star Dust! You will lose traveldays as much as you have exposed connectors on you ship!");
        currentState = AdventureStatesTypes.EFFECT;
        applyEffect();
    }


    @Override
    public void checkRequirements() {
    }

    @Override
    public void applyEffect() {
        super.applyEffect();
        for (int i = 0; i < gameBoard.getLeaderBoard().size(); i++) {
            Player player = gameBoard.getLeaderBoard().get(i).getValue();
            gameBoard.setCurrentPlayer(player);
            int numExposedConnectors = player.getShipBoard().getNumberOfExposedConnectors();
            if (numExposedConnectors > 0) {
                player.setDaysThisTurn(-numExposedConnectors);
            }
        }
        currentState = AdventureStatesTypes.UPDATE;
    }


//    @Override
//    public void applyEffect() {
//        super.applyEffect();
//        boolean hasMorePlayers = true;
//        while(hasMorePlayers) {
//            Player player = gameBoard.getCurrentPlayer();
//            int numExposedConnectors = player.getShipBoard().getNumberOfExposedConnectors();
//            if(numExposedConnectors > 0) {
//                player.setDaysThisTurn(numExposedConnectors);
//            }
//            try {
//                gameBoard.nextPlayer();
//            } catch (IllegalSetOfNextPlayerException e) {
//                hasMorePlayers = false;
//            }
//        }
//        currentState = AdventureStatesTypes.UPDATE;
//    }

}
