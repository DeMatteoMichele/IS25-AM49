package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a specific type of adventure card called "Epidemy Card".
 * Triggers events related to the removal of crew members from cabins
 * based on interconnectivity of cabin tiles on the game board.
 */
public class EpidemyCard extends AdventureCard {

    /**
     * Constructs an EpidemyCard instance by initializing it with the specified game board, description, level, and name.
     *
     * @param gameBoard the board where the game is being played.
     * @param description a short summary or explanation of this card.
     * @param level the intensity level of the card.
     * @param name the name of the card.
     */
    public EpidemyCard(GameBoard gameBoard, String description,int level,String name) {
        super(gameBoard,description, level,name);
    }
    /**
     * Stores the coordinates of all cabins related to the EpidemyCard.
     * Each cabin's position is represented as a Point object.
     */
    List<Point> coordinatesOfAllCabins = new ArrayList<>();

    /**
     * Overrides the drawCard method to perform additional functionality specific to the EpidemyCard.
     * Calls the superclass method to change the game state and update the current player.
     * Prints a message indicating the card drawn and its effects on the game.
     * Invokes the checkRequirements method to assess the impact of interconnected cabins on crew members.
     */
    @Override
    public void drawCard() {
        super.drawCard();
        System.out.println("You have found an epidemy card! For each player remove one crew member for each cabin interconnected to another cabin");
        checkRequirements();
    }

    /**
     * Checks and updates the list of cabins on a ship to ensure only interconnected cabins remain.
     * Identifies isolated cabins that lack any neighboring cabins (north, south, east, or west)
     * and removes them from the list of cabin coordinates.
     */
    public void checkInterconnectedCabin(){
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        coordinatesOfAllCabins.addAll(ship.getComponentsPositions(ComponentType.CABINE));
        List<Point> pointsToRemove = new ArrayList<>();
        coordinatesOfAllCabins
                .forEach(point -> {
                    Point north = new Point(point.x, point.y - 1);
                    Point south = new Point(point.x, point.y + 1);
                    Point east = new Point(point.x + 1, point.y);
                    Point west = new Point(point.x - 1, point.y);
                    if (!coordinatesOfAllCabins.contains(north) && !coordinatesOfAllCabins.contains(south) && !coordinatesOfAllCabins.contains(east) && !coordinatesOfAllCabins.contains(west)) {
                        pointsToRemove.add(point);
                    }
                });
        coordinatesOfAllCabins.removeAll(pointsToRemove);
    }
    /**
     * Verifies whether the current player's ship has interconnected cabins.
     * If cabins are not interconnected, proceeds to check the next player.
     * If cabins are interconnected, sets the state to apply the effect and triggers it.
     * Outputs the process and decisions to the console.
     */
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

    /**
     * Determines whether the game should proceed with the next player's turn or move to the update phase.
     *
     * If there is a next player available, the method allows the turn to continue
     * by switching to the next player and checking the game requirements.
     * If there are no more players left for the turn, it triggers the update phase.
     *
     * Exceptions:
     * - Catches and handles {@code IllegalSetOfNextPlayerException} when no next player is available.
     */
    private void checkNextPlayer() {
        try {
            gameBoard.nextPlayer();
            System.out.println("it wasn't the last player, go on");
            checkRequirements();
        } catch (IllegalSetOfNextPlayerException e) {
            System.out.println("It was the last player, end of the turn");
            System.out.println("it goes to update state");
            update();
        }
    }

    /**
     * Removes specific tokens (astronaut, brown aliens, purple aliens) from the tiles
     * on the current player's ship board, based on the coordinates stored in
     * {@code coordinatesOfAllCabins}. Clears the coordinates after processing and
     * transitions to the next player.
     *
     * Overrides the base implementation to apply the custom effect of the EpidemyCard.
     *
     * Validation on the current state is handled by the superclass.
     */
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

    /**
     * Generates a string representation of the EpidemyCard object in a formatted box with details such as name,
     * description, and level.
     *
     * @return a formatted string representing the EpidemyCard object
     */
    @Override
    public String toString() {
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



