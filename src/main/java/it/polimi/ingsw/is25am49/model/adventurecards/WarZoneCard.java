package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WarZoneCard extends AdventureCard {


     //TODO da aggiungere contatore e fare checkrequirements3
    //TODO devo modficare il check sulla dimensione delle batterie e metterlo in decisionbatteries controllando il numero di componenti effettivamente attivabili (getactivatablecomponent)
    Player playerWithMinimumEnginePower;
    private int minimumEnginePower;
    private final List<CardEffect> listEffects;
    private List<Pair<Point,Token>> batteries;
    private List<Point> cannons;
    private int number;

    public WarZoneCard(GameBoard gameBoard,String description, int level, List<CardEffect>effects) {
        super(gameBoard,description, level);
        listEffects = new ArrayList<>();
        this.listEffects.addAll(effects);
        minimumEnginePower = 0;


    }

    @Override
    public void drawCard(){
        super.drawCard();
        playerWithMinimumEnginePower = gameBoard.getLeader();
        System.out.println("You have found a War Zone! Good luck, it will be tough to get out of it.");
        checkRequirements();
    }


    //    method to calculate the total crew of a player
    private int getTotalCrew(Player p){
        return p.getShipBoard().getTotalNumberOfaToken(Token.ASTRONAUT)+
                p.getShipBoard().getTotalNumberOfaToken(Token.BROWN_ALIENS)+
                p.getShipBoard().getTotalNumberOfaToken(Token.PURPLE_ALIENS);
    }


    @Override
    public void checkRequirements() {
        //check the player with minimum equipment starting from the leader
        Player playerWithMinimumCrew = gameBoard.getLeaderBoard().stream()
                .map(Map.Entry :: getKey)
                .min((p1,p2)->Integer.compare(
                        getTotalCrew(p1),
                        getTotalCrew(p2)))
                .orElse(gameBoard.getCurrentPlayer());
        //set the current player to the one with minimum crew
        gameBoard.setCurrentPlayer(playerWithMinimumCrew);
        System.out.println("Player with minimum crew is "+ gameBoard.getCurrentPlayer().getUsername());
        System.out.println("You lost 3 travel days");
        gameBoard.getCurrentPlayer().setDaysThisTurn(-3);
        gameBoard.updateLeaderboard();
        System.out.println("leader updated! now is : " + gameBoard.getLeader().getUsername());

        //now current player is the leader again
        gameBoard.setCurrentPlayer(gameBoard.getLeader());
        checkRequirements2();

    }

    public void checkRequirements2(){



        //check the player with less engine power
        System.out.println("you are in phase 2 of the war zone, now we will check the player with less engine power");
        System.out.println("i check if "+ gameBoard.getCurrentPlayer().getUsername() + " has enough batteries to activate double motors");
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        int totalNumberOfBatteries = ship.getTotalNumberOfaToken(Token.BATTERY_CELLS);
        if(totalNumberOfBatteries>0){
            System.out.println("you have enough batteries to activate your double motors. One battery activates one motor. " +
                    "You can activate up to "+ totalNumberOfBatteries + " double motors");
            //I change state to decison batteries to decide how much to activate
            currentState = AdventureStatesTypes.DECISION_BATTERIES;
        }
        else{
            //non ho abbastanza batterie
            System.out.println("You don't have enough batteries to activate your double motors");
            int enginePower = ship.getTotalEnginePower();
            //System.out.println(enginePower);
            if(enginePower<minimumEnginePower){
                //System.out.println("aggiornamento minimum power" + enginePower);
                minimumEnginePower = enginePower;
                playerWithMinimumEnginePower = gameBoard.getCurrentPlayer();
                //System.out.println(playerWithMinimumEnginePower.getUsername());
            }
            System.out.println("Now i check if it was the last player");
            try{
                //controllo se il current player era l'ultimo o no
                //se non è l'ultimo richiamo check requirements sul player dopo
                gameBoard.nextPlayer();
                System.out.println("It wasn't the last player, go on with the next player");
                checkRequirements2();
            }
            catch(IllegalSetOfNextPlayerException e){
                //se era l'ultimo catturo l'eccezione propagata verso l'alto da nextPlayer
                System.out.println("It was the last player");
                System.out.println("Player with minimum fire power is "+ playerWithMinimumEnginePower.getUsername());
                System.out.println(playerWithMinimumEnginePower.getUsername() + " you lost 2 crew members, you have to choose which ones");
                currentEffect = listEffects.removeFirst();
                //i go to decision astronaut state
                currentState = AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS;
            }

        }
    }

    @Override
    public void finishDecision(List<Pair<Point,Token>> coordinates){
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        switch(currentState){
            case DECISION_BATTERIES ->{
                if(coordinates.size()>ship.getTotalNumberOfaToken(Token.BATTERY_CELLS)){
                    throw new IllegalArgumentException("you have chosen more batteries than you have");
                }
               else if(coordinates.size() > ship.getActivatableComponents(ComponentType.DOUBLE_ENGINE).size()){
                    throw new IllegalArgumentException("you have chosen" + coordinates.size() + "but you have less engine ");
                }
                System.out.println("you have chosen "+ coordinates.size() + " batteries");
                this.batteries = new ArrayList<>(coordinates);
                System.out.println("Now you have to choose which motors you wanna activate");
                currentState=AdventureStatesTypes.DECISION_MOTOR;
            }
            case DECISION_ASTRONAUTS_OR_ALIENS -> {
                //i have to choose which astroauts or aliens to lose
                gameBoard.setCurrentPlayer(playerWithMinimumEnginePower);
                currentEffect.setListOfCoordinates(coordinates);
                System.out.println("You have chosen the astronauts or aliens you wanna lose");
                currentEffect.action(gameBoard.getCurrentPlayer());
                System.out.println(gameBoard.getCurrentPlayer().getUsername() + " has lost two crew members");
                gameBoard.setCurrentPlayer(gameBoard.getLeader());
                checkRequirements3();

            }
            default -> throw new IllegalArgumentException("wrong state");


    }}

    @Override
    public void finishDecision(Set<Point> coordinates){
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        switch(currentState){

            case DECISION_MOTOR -> {
                System.out.println("You have decided the coordinates of the motors you wanna activate");
//                if(batteries.size()!=coordinates.size()){
//                    throw new IllegalArgumentException("you have chosen a different number of batteries and motors");
//                }
                cannons = new ArrayList<>(coordinates);
                //i activte all the cannons
                for(int i=0; i< coordinates.size();i++){
                    ship.turnOnComponent(cannons.get(i), batteries.get(i).getKey());
                    System.out.println("turning on the motor at " + cannons.get(i).x + ", " + cannons.get(i).y);
                }
                int enginePower = ship.getTotalEnginePower();
                if(enginePower<minimumEnginePower){
                    minimumEnginePower = enginePower;
                    playerWithMinimumEnginePower = gameBoard.getCurrentPlayer();
                }
                //turn off the motors
                for(int i=0; i< coordinates.size();i++){
                    ship.turnOff(cannons.get(i));
                    System.out.println("turning off the motor at " + cannons.get(i).x + ", " + cannons.get(i).y);
                }
                System.out.println("Now i check if it was the last player");
                try{
                    //controllo se il current player era l'ultimo o no
                    //se non è l'ultimo richiamo check requirements sul player dopo
                    gameBoard.nextPlayer();
                    System.out.println("It wasn't the last player, go on with the next player");
                    checkRequirements2();
                }
                catch(IllegalSetOfNextPlayerException e){
                    //se era l'ultimo catturo l'eccezione propagata verso l'alto da nextPlayer
                    System.out.println("It was the last player");
                    System.out.println("Player with minimum fire power is "+ playerWithMinimumEnginePower.getUsername());
                    System.out.println(playerWithMinimumEnginePower.getUsername() + " you lost 2 crew members, you have to choose which ones");
                    currentEffect = listEffects.removeFirst();
                    //i go to decision astronaut state
                    currentState = AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS;
                }

            }
            default -> throw new IllegalArgumentException("wrong state");
        }
    }

    public void checkRequirements3(){
        System.out.println("now i check which player has the minimum fire power");
    }


    @Override
    public void applyEffect() {


    }


}
