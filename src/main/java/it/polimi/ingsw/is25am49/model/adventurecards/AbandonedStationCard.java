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
        //chiamo il draw di adventure card
        super.drawCard();
        System.out.println("You have found an Abandoned Station! You can land on it for some goods.");
        //passo alla richiesta di requisiti
        checkRequirements();
    }


    @Override
    public void checkRequirements() {

        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumCrewMembers = ship.getTotalNumberOfaToken(Token.ASTRONAUT)+ship.getTotalNumberOfaToken(Token.BROWN_ALIENS)+ship.getTotalNumberOfaToken(Token.PURPLE_ALIENS);
       //se ho abbastanza astronauti devo decidere se atterrare e passo allo stato apposito
        if(totalNumCrewMembers >= lostAstronauts) {
            System.out.println("Do you wanna land on the station for some goods?");
            currentState=AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP;
        }
        else{
            //non ho abastanza membri per atterrare
            System.out.println("You don't have enough members to land on the station!");
            try{
                //controllo se il current player era l'ultimo o no
                //se non è l'ultimo richiamo check requirements sul player dopo
                gameBoard.nextPlayer();
                System.out.println("Now check on next player");
                checkRequirements();
            }
            catch(IllegalSetOfNextPlayerException e){
                //se era l'ultimo catturo l'eccezione propagata verso l'alto da nextPlayer
                currentState = AdventureStatesTypes.UPDATE;
            }
        }
    }

    @Override
    public void landingOnStationOrShip() {
        //se questo metodo è chiamato il player ha scelto di scendere sulla stazione
        //prendo il primo effetto (l'unico in questa carta)
        //vado nello stato dove decido le merci da caricare e le rispettive posizioni
        super.landingOnStationOrShip();
        System.out.println("You have landed on A station");
        System.out.println("Nw you have to decide where you want to add your win goods and which ones");
        currentState= AdventureStatesTypes.DECISION_GOODS;
        currentEffect = listEffects.getFirst();
    }
    @Override
    public void finishDecision(){
        switch(currentState){
            //se viene chiamato (dallo user) e sono in decisiongoods allora chiamo applyeffect dove inserirò le merci, cambio stato
            case DECISION_GOODS -> {
                System.out.println("You have decided your goods to add ");
                applyEffect();
                currentState= AdventureStatesTypes.EFFECT;
            }
            //se è chiamato (dallo user) e sono in decisionLand allora vuol dire che lo user ha scelto di non scendere sulla stazione
            //faccio il controllo su next player come in checkrequirements
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
        //se non sono nello stato corretto non posso farlo
        if(currentState != AdventureStatesTypes.EFFECT){
            throw new IllegalMethodOnThisCardException("Wrong state");
        }

        //applico l'effetto, aggiorno il player con i giorni persi
        //vado nello stato update dove verrà aggiornata la leaderboard
        Player player = gameBoard.getCurrentPlayer();
        System.out.println("You are winning goods but you lost travel days");
        currentEffect.action(player);
        player.setDaysThisTurn(daysLost);
        currentState = AdventureStatesTypes.UPDATE;

    }

}
