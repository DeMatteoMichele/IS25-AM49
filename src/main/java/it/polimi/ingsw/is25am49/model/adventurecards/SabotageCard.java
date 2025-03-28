package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.model.exceptions.InvalidCoordinatesException;
import javafx.util.Pair;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class SabotageCard extends AdventureCard {

    public SabotageCard(GameBoard gameBoard, String description, int daysLost, int level, int creditsEarned) {
        super(gameBoard,description, daysLost, level, creditsEarned);
    }

    //declare the row and column of the component to be removed and the counter of the dice roll
    private int column;
    private int row;
    private int  diceRollCount=0;


    @Override
    public void drawCard() {
        super.drawCard();
        System.out.println("You have found a Sabotage Card! Someone's gonna loose a component of the ship!");
        checkRequirements();
    }

    @Override
    public void checkRequirements() {
        //check which player has the lowest crew
        int min = 0;
        Player playerWithMinimumCrew = null;
        for(Pair<Integer,Player> p : gameBoard.getLeaderBoard()){
            int totalNumberOfCrew = p.getValue().getShipBoard().getTotalNumberOfaToken(Token.ASTRONAUT)+
                    p.getValue().getShipBoard().getTotalNumberOfaToken(Token.BROWN_ALIENS)+
                    p.getValue().getShipBoard().getTotalNumberOfaToken(Token.PURPLE_ALIENS);
            if(totalNumberOfCrew< min){
                min = totalNumberOfCrew;
                playerWithMinimumCrew= p.getValue();
            }

        }
        gameBoard.setCurrentPlayer(playerWithMinimumCrew);
        System.out.println("Player with the lowest crew is: " + gameBoard.getCurrentPlayer().toString() +"one of your components it will be destroyed");
        System.out.println("you have to roll dices: ");
        currentState=AdventureStatesTypes.ROLL_DICES;
        rollDices();

    }

    public void rollDices() {
        if(currentState != AdventureStatesTypes.ROLL_DICES)
            throw new IllegalMethodOnThisStateException("Wrong state");

        if(diceRollCount%2==0){
            System.out.println("Roll the dices to determine the column of the component to be removed");
            diceRollCount++;
            Random random = new Random();
            int firstDice = random.nextInt(6) + 1;
            int secondDice = random.nextInt(6) + 1;
            dicesResult = firstDice + secondDice;
            System.out.println("The dices have been rolled: " + firstDice + " and " + secondDice + ". Result: " + dicesResult);
            column = dicesResult;
            rollDices();

        }
        else{
            System.out.println("Roll the dices to determine the row of the component to be removed");
            diceRollCount++;
            Random random = new Random();
            int firstDice = random.nextInt(6) + 1;
            int secondDice = random.nextInt(6) + 1;
            dicesResult = firstDice + secondDice;
            System.out.println("The dices have been rolled: " + firstDice + " and " + secondDice + ". Result: " + dicesResult);
            row = dicesResult;
            currentState= AdventureStatesTypes.EFFECT;
            applyEffect();
        }
    }



    @Override
    public void applyEffect() {
        Point coordinate = new Point(row, column);
        try{
            gameBoard.getCurrentPlayer().getShipBoard().destroyTile(coordinate);
            gameBoard.getCurrentPlayer().addLostComponents(1);
            System.out.println("The component at row: " + row + " and column: " + column + " has been destroyed");
            System.out.println("Your lostComponent has been added to your discard pile");
            //TODO DEVO CONTROLLARE CHE IL COMPONENTE RIMOSSO NON FOSSE QUELLO AL CENTRO ALTRIMENTI LA NAVE SI DISTRUGGE TUTTA, ASPETTO METODO DI COMPA
            currentState = AdventureStatesTypes.DRAW;
        }
        catch (InvalidCoordinatesException e){
            System.out.println(e.getMessage());
            if(diceRollCount == 5){
                System.out.println("you hve rolled the dices 5 times, you have to stop");
                System.out.println("you've been lucky, neither one of your component has been destroyed");
                currentState = AdventureStatesTypes.DRAW;
            }
            else {
                System.out.println("You have to roll the dices again");
                currentState=AdventureStatesTypes.ROLL_DICES;
                rollDices();
            }
        }


    }

}
