package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.enums.AdventureStatesTypes;

public class StarDustCard extends AdventureCard {

    public StarDustCard(GameBoard gameBoard, String description, int level) {
        super(gameBoard, description, level);
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
        for (int i = 1; i <= gameBoard.getLeaderBoard().size(); i++) {
            Player player = gameBoard.getLeaderBoard().getPlayerInPosition(i);
            gameBoard.setCurrentPlayer(player);
            int numExposedConnectors = player.getShipBoard().getNumberOfExposedConnectors();
            if (numExposedConnectors > 0) {
                player.setDaysThisTurn(-numExposedConnectors);
            }
        }
        currentState = AdventureStatesTypes.UPDATE;
        isFinished = true;
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
