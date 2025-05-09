package it.polimi.ingsw.is25am49.client.model.adventurecards;

import it.polimi.ingsw.is25am49.client.model.GameBoard;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.client.model.exceptions.InvalidCoordinatesException;

import java.awt.*;
import java.util.Map;

public class SabotageCard extends AdventureCard {

    public SabotageCard(GameBoard gameBoard, String description, int level, String name) {
        super(gameBoard,description, level,name);
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

//    method to calculate the total crew of a player
    private int getTotalCrew(Player p){
        return p.getShipBoard().getTotalNumberOfaToken(Token.ASTRONAUT)+
                p.getShipBoard().getTotalNumberOfaToken(Token.BROWN_ALIENS)+
                p.getShipBoard().getTotalNumberOfaToken(Token.PURPLE_ALIENS);
    }

    @Override
    public void checkRequirements() {
        if(gameBoard.getLeaderBoard().size()==1){
            update();
            System.out.println("you are the only player, you skip this card");
        }
        //con uno stream trovo il giocatore che ha meno equipaggio
        Player playerWithMinimumCrew = gameBoard.getLeaderBoard().stream()
                .map(Map.Entry::getKey)
                .min((p1, p2) -> Integer.compare(
                        getTotalCrew(p1),
                        getTotalCrew(p2)))
                .orElse(gameBoard.getCurrentPlayer());

        //debug per stampare per ogni giocatore il totale dell'equipaggio

//        gameBoard.getLeaderBoard().stream().forEach(p ->
//        System.out.println(p.getKey().getUsername() + " " + getTotalCrew(p.getKey())));

        //setto a current player del gameboard il giocatore con meno equipaggio
        gameBoard.setCurrentPlayer(playerWithMinimumCrew);
        System.out.println("Player with the lowest crew is: " + gameBoard.getCurrentPlayer().getUsername()+" one of your components it will be destroyed");
        System.out.println("you have to roll dices: ");

        //passo a roll dices
        currentState= AdventureStatesTypes.ROLL_DICES;


    }

    public void rollDices() {
        if(currentState != AdventureStatesTypes.ROLL_DICES)
            throw new IllegalMethodOnThisStateException("Wrong state");

        diceRollCount++;
        int firstDice = random.nextInt(6) + 1;
        int secondDice = random.nextInt(6) + 1;
        dicesResult = firstDice + secondDice;
        //se è pari lancio i dadi e setto la colonna da cui rimuovere il componente e poi rilancio i dadi
        if(diceRollCount%2==0){
            System.out.println("The dices have been rolled: " + firstDice + " and " + secondDice + ". Column: " + dicesResult);
            column = dicesResult;
            System.out.println("you have set the column to: " + column + ", now roll the dices again to set the row");
        }
        else{
            //se è dispari lancio i dadi e setto la riga da cui rimuovere il componente e passo a apply effect
            System.out.println("The dices have been rolled: " + firstDice + " and " + secondDice + ". Row: " + dicesResult);
            row = dicesResult;
            currentState= AdventureStatesTypes.EFFECT;
            applyEffect();
        }
    }



    @Override
    public void applyEffect() {
        //imposto le coordinate
        Point coordinate = new Point(row, column);
        try{
            //rimuovo il componente, se non lancia eccezioni lo ho rimosso altrimenti catturo l'eccezione
            gameBoard.getCurrentPlayer().getShipBoard().destroyTile(coordinate);
            gameBoard.getCurrentPlayer().addLostComponents(1);
            gameBoard.getCurrentPlayer().getShipBoard().destroyDisconnectedTiles();
            System.out.println("The component at row: " + row + " and column: " + column + " has been destroyed");
            System.out.println("Your lostComponent has been added to your discard pile");
            currentState = AdventureStatesTypes.DRAW;
        }
        catch (InvalidCoordinatesException e){
            System.out.println("The component at row: " + row + " and column: " + column + " doesn't exist or it's out of the ship");
            //se ho lanciato i dadi 6 volte allora ho finito
            if(diceRollCount == 6){
                System.out.println("you have rolled the dices 6 times, you have to stop");
                System.out.println("you've been lucky, neither one of your component has been destroyed");
                isFinished=true;
                currentState = AdventureStatesTypes.DRAW;
            }
            //rilancio i dadi
            else {
                System.out.println("You have to roll the dices again");
                currentState= AdventureStatesTypes.ROLL_DICES;
                rollDices();
            }
        }


    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        final int width = 90;  // Larghezza standard del box
        String border = "+" + "-".repeat(width - 2) + "+\n";
        String separator = "|" + "-".repeat(width - 2) + "|\n";
        str.append(border);
        String nameText = "| NAME: " + name;
        str.append(nameText).append(" ".repeat(width - nameText.length() - 1)).append("|\n");
        str.append(separator);
        String descText = "| DESCRIPTION: " + description;
        appendTextWithWrapping(str, descText, width);
        String levelText = "| LEVEL: " + level;
        str.append(levelText).append(" ".repeat(width - levelText.length() - 1)).append("|\n");
        str.append(border);
        return str.toString();
    }

}
