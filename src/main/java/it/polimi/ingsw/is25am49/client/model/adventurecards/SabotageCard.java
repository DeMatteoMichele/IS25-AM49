package it.polimi.ingsw.is25am49.client.model.adventurecards;

import it.polimi.ingsw.is25am49.client.model.GameBoard;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.client.model.exceptions.InvalidCoordinatesException;

import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SabotageCard extends AdventureCard {

    public SabotageCard(String cardId,GameBoard gameBoard, String description, int level, String name) {
        super(cardId, gameBoard, description, level, name);
    }

    //declare the row and column of the component to be removed and the counter of the dice roll
    private int column;
    private int row;
    private int  diceRollCount=0;


    @Override
    public void drawCard() {
        super.drawCard();
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You have found a Sabotage Card! Someone's gonna loose a component of the ship!");
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
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"you are the only player, you skip this card");
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
//        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),p.getKey().getUsername() + " " + getTotalCrew(p.getKey())));

        //setto a current player del gameboard il giocatore con meno equipaggio
        gameBoard.setCurrentPlayer(playerWithMinimumCrew);
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Player with the lowest crew is: " + gameBoard.getCurrentPlayer().getUsername()+" one of your components it will be destroyed");
        userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"you have to roll dices: ");

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
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"The dices have been rolled: " + firstDice + " and " + secondDice + ". Column: " + dicesResult);
            userInterface.updateThrowDices(gameBoard.getCurrentPlayer().getIdPlayer(), firstDice, secondDice);
            column = dicesResult;
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"you have set the column to: " + column + ", now roll the dices again to set the row");
        }
        else{
            //se è dispari lancio i dadi e setto la riga da cui rimuovere il componente e passo a apply effect
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"The dices have been rolled: " + firstDice + " and " + secondDice + ". Row: " + dicesResult);
            userInterface.updateThrowDices(gameBoard.getCurrentPlayer().getIdPlayer(), firstDice, secondDice);
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
            Set <Point> destroyedTile= new HashSet<>();
            destroyedTile.add(coordinate);
            userInterface.updateDestroyedTiles(gameBoard.getCurrentPlayer().getIdPlayer(), destroyedTile);
            gameBoard.getCurrentPlayer().addLostComponents(1);
            Set<Point> destroyedTiles= gameBoard.getCurrentPlayer().getShipBoard().destroyDisconnectedTiles();
            userInterface.updateDestroyedTiles(gameBoard.getCurrentPlayer().getIdPlayer(), destroyedTiles);
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"The component at row: " + row + " and column: " + column + " has been destroyed");
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"Your lostComponent has been added to your discard pile");
            currentState = AdventureStatesTypes.DRAW;
        }
        catch (InvalidCoordinatesException e){
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"The component at row: " + row + " and column: " + column + " doesn't exist or it's out of the ship");
            //se ho lanciato i dadi 6 volte allora ho finito
            if(diceRollCount == 6){
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"you have rolled the dices 6 times, you have to stop");
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"you've been lucky, neither one of your component has been destroyed");
                isFinished=true;
                currentState = AdventureStatesTypes.DRAW;
            }
            //rilancio i dadi
            else {
                userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(),"You have to roll the dices again");
                currentState= AdventureStatesTypes.ROLL_DICES;
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
