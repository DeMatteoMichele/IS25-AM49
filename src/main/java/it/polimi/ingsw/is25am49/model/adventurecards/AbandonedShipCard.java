package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectModifyCrew;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalSetOfNextPlayerException;
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
        ShipBoard shipBoard = gameBoard.getCurrentPlayer().getShipBoard();
        int numAstronauts = shipBoard.getTotalNumberOfaToken(Token.ASTRONAUT);
        System.out.println("You have " + numAstronauts + " astronauts on your ship. If you decide to land on the ship, you win " + creditsEarned + " credits but if your crew is too small you have to quit");
        System.out.println("Decide if you want to land");
        currentState = AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP;
    }

    @Override
    public void landingOnStationOrShip() throws IllegalMethodOnThisStateException {
        super.landingOnStationOrShip();
        System.out.println("You have landed on the ship");
        System.out.println("Now you have to decide where you want to remove your astronauts");
        currentEffect = listEffects.getFirst();
        currentState = AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS;
    }

    @Override
    public void finishDecision() {
        switch (currentState) {
            case DECISION_ASTRONAUTS_OR_ALIENS -> {
                System.out.println("You have decided to remove astronauts");
                currentState = AdventureStatesTypes.EFFECT;
                applyEffect();
            }
            case DECISION_LAND_STATION_OR_SHIP -> {
                System.out.println("You have decided not to land on the ship");
                try {
                    gameBoard.nextPlayer();
                    System.out.println("Now check on next player");
                    checkRequirements();
                } catch (IllegalSetOfNextPlayerException e) {
                    System.out.println("No more players, now to update");
                    currentState = AdventureStatesTypes.UPDATE;
                }
            }
            default -> throw new IllegalMethodOnThisStateException("Wrong state");
        }
    }

    @Override
    public void applyEffect() {
        super.applyEffect();
        Player currentPlayer=gameBoard.getCurrentPlayer();
        System.out.println("You are in applyeffect, you lose astronauts");
        //currentEffect.action(currentPlayer);
        currentPlayer.addCredits(creditsEarned);
        System.out.println("You have earned " + creditsEarned + " credits");
        currentPlayer.setDaysThisTurn(daysLost);
        currentState= AdventureStatesTypes.UPDATE;
    }

}
