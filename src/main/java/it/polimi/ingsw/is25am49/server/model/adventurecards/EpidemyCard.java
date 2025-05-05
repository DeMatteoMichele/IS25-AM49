package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.server.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EpidemyCard extends AdventureCard {

    public EpidemyCard(GameBoard gameBoard, String description,int level,String name) {
        super(gameBoard,description, level,name);
    }
    List<Point> coordinatesOfAllCabins = new ArrayList<>();

    @Override
    public void drawCard() {
        super.drawCard();
        System.out.println("You have found an epidemy card! For each player remove one crew member for each cabin interconnected to another cabin");
        checkRequirements();
    }

    public void checkInterconnectedCabin(){
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        //metto in una lista tutte le pisizioni di cabine, moduli alieni viola e moduli alienni marroni
        coordinatesOfAllCabins.addAll(ship.getComponentsPositions(ComponentType.CABINE));
        List<Point> pointsToRemove = new ArrayList<>();
        coordinatesOfAllCabins
                .forEach(point ->{
                    Point north = new Point(point.x, point.y - 1);
                    Point south = new Point(point.x, point.y + 1);
                    Point east = new Point(point.x + 1, point.y);
                    Point west = new Point(point.x - 1, point.y);
                    if(!coordinatesOfAllCabins.contains(north) && !coordinatesOfAllCabins.contains(south) && !coordinatesOfAllCabins.contains(east) && !coordinatesOfAllCabins.contains(west)){
                        pointsToRemove.add(point);
                    }
                });
        coordinatesOfAllCabins.removeAll(pointsToRemove);
    }
    @Override
    public void checkRequirements() {
        System.out.println("Now i check if "+ gameBoard.getCurrentPlayer().getUsername() + " has interconnected cabins");
        checkInterconnectedCabin();
        if(coordinatesOfAllCabins.isEmpty()){
            System.out.println(gameBoard.getCurrentPlayer().getUsername() + " has not interconnected cabins, no crew members will be removed");
            System.out.println("Now i check on next player");
            checkNextPlayer();

        }
        else{
            System.out.println(gameBoard.getCurrentPlayer().getUsername() + " it has interconnected cabins and for each one you will lose one crew memeber");
            currentState= AdventureStatesTypes.EFFECT;
            applyEffect();

        }
    }
    private void checkNextPlayer() {
        try {
            gameBoard.nextPlayer();
            System.out.println("it wasn't the last player, go on");
            currentState = AdventureStatesTypes.DRAW;
            checkRequirements();
        } catch (IllegalSetOfNextPlayerException e) {
            System.out.println("It was the last player, end of the turn");
            System.out.println("it goes to update state");
            update();
        }
    }

    @Override
    public void applyEffect(){
        super.applyEffect();
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        coordinatesOfAllCabins.forEach(point ->{
            if(ship.getPositionsOfaToken(Token.ASTRONAUT).contains(point)){
                ship.removeTokenFromTile(point, Token.ASTRONAUT);
            }
            else if(ship.getPositionsOfaToken(Token.BROWN_ALIENS).contains(point)){
                ship.removeTokenFromTile(point, Token.BROWN_ALIENS);

            }
            else{
                ship.removeTokenFromTile(point, Token.PURPLE_ALIENS);
            }

        });
        coordinatesOfAllCabins.clear();
        checkNextPlayer();
    }

}



