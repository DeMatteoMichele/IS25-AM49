package it.polimi.ingsw.is25am49.client.model.adventurecards;

import it.polimi.ingsw.is25am49.client.model.GameBoard;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EpidemyCard extends AdventureCard {

    public EpidemyCard(String cardId, GameBoard gameBoard, String description, int level, String name) {
        super(cardId, gameBoard, description, level, name);
    }

    List<Point> coordinatesOfAllCabins = new ArrayList<>();

    @Override
    public void drawCard() {
        super.drawCard();
        userInterface.updateGenericCardMessage(genericIdForUpdates, "You have found an epidemy card! For each player remove one crew member for each cabin interconnected to another cabin");
        checkRequirements();
    }

    public void checkInterconnectedCabin() {
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
    @Override
    public void checkRequirements() {
        checkInterconnectedCabin();
        if (coordinatesOfAllCabins.isEmpty()) {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You dont have not interconnected cabins, no crew members will be removed");
            checkNextPlayer();
        } else {
            userInterface.updateGenericCardMessage(gameBoard.getCurrentPlayer().getIdPlayer(), "You have interconnected cabins and for each one you will lose one crew memeber");
            currentState = AdventureStatesTypes.EFFECT;
            applyEffect();
        }
    }

    private void checkNextPlayer() {
        try {
            gameBoard.nextPlayer();
            checkRequirements();
        } catch (IllegalSetOfNextPlayerException e) {
            userInterface.updateGenericCardMessage(genericIdForUpdates, "It was the last player, end of the turn");
            update();
        }
    }

    @Override
    public void applyEffect() {
        super.applyEffect();
        ShipBoard ship = gameBoard.getCurrentPlayer().getShipBoard();
        coordinatesOfAllCabins.forEach(point -> {
            if (ship.getPositionsOfaToken(Token.ASTRONAUT).contains(point)) {
                ship.removeTokenFromTile(point, Token.ASTRONAUT);
                userInterface.updateEffectModifyCrew(gameBoard.getCurrentPlayer().getIdPlayer(), point, Token.ASTRONAUT);
            } else if (ship.getPositionsOfaToken(Token.BROWN_ALIENS).contains(point)) {
                ship.removeTokenFromTile(point, Token.BROWN_ALIENS);
                userInterface.updateEffectModifyCrew(gameBoard.getCurrentPlayer().getIdPlayer(), point, Token.BROWN_ALIENS);
            } else {
                ship.removeTokenFromTile(point, Token.PURPLE_ALIENS);
                userInterface.updateEffectModifyCrew(gameBoard.getCurrentPlayer().getIdPlayer(), point, Token.PURPLE_ALIENS);
            }
        });
        coordinatesOfAllCabins.clear();
        checkNextPlayer();
    }

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



