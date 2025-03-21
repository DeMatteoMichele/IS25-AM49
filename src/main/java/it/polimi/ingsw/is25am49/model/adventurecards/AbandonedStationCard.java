package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectModifyCrew;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisCardException;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;

import java.util.ArrayList;
import java.util.List;

public class AbandonedStationCard extends AdventureCard {
    protected final ArrayList<Token> Prize = new ArrayList<>();

    public AbandonedStationCard(GameBoard gameBoard, String description, int daysLost, int level, int creditsEarned, int lostAstronauts){
        super(gameBoard,description,daysLost,level,creditsEarned);
        this.lostAstronauts = lostAstronauts;
        listEffects.add(new EffectModifyCrew("Remove astronauts", lostAstronauts));
    }

    public void drawCard() {
        super.drawCard();
        System.out.println("You have found an Abandoned Station! You can land on it for some goods.");
        checkRequirements();
    }


    @Override
    public void checkRequirements() {
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumCrewMembers = ship.getTotalNumberOfaToken(Token.ASTRONAUT)+ship.getTotalNumberOfaToken(Token.BROWN_ALIENS)+ship.getTotalNumberOfaToken(Token.PURPLE_ALIENS);
        if(totalNumCrewMembers >= lostAstronauts) {
            System.out.println("Do you wanna land on the station for some goods?");
            currentState=AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP;
        }
        else{
            System.out.println("You don't have enough members to land on the station!");
            try{
                gameBoard.nextPlayer();
                System.out.println("Now check on next player");
                checkRequirements();
            }
            catch(IllegalSetOfNextPlayerException e){
                currentState = AdventureStatesTypes.UPDATE;
            }
        }
    }

    @Override
    public void landingOnStationOrShip() {
        super.landingOnStationOrShip();
        System.out.println("You have landed on A station");
        System.out.println("Nw you have to decide where you want to add your win goods");
        currentState= AdventureStatesTypes.DECISION_GOODS;
        currentEffect = listEffects.getFirst();
    }
    @Override
    public void finishDecision(){
        switch(currentState){
            case DECISION_GOODS -> {
                System.out.println("You have decided your goods to add ");
                applyEffect();
            }
            case DECISION_LAND_STATION_OR_SHIP -> {
                System.out.println("You have decided not to land on the station ");
                try{
                    gameBoard.nextPlayer();
                    System.out.println("Now check on next player");
                    checkRequirements();
                }
                catch(IllegalSetOfNextPlayerException e){
                    currentState = AdventureStatesTypes.UPDATE;
                }
            }
            default -> throw  new IllegalMethodOnThisCardException("Wrong state");
        }

    }

    @Override
    public void applyEffect() {
        Player player = gameBoard.getCurrentPlayer();
        System.out.println("You are winning goods but you lost travel days");
        currentEffect.action(player);
        player.setDaysThisTurn(daysLost);

    }

}
