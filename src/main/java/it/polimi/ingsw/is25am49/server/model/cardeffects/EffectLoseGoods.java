package it.polimi.ingsw.is25am49.server.model.cardeffects;

import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisCardException;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EffectLoseGoods extends CardEffect {
    private int numberOfTokensToLose;

    List<Point> coordinates = new ArrayList<>();
    public EffectLoseGoods(String name_effect, int numberOfTokensToLose) {

        super(name_effect);
        this.numberOfTokensToLose = numberOfTokensToLose;
    }
    public void action(Player player) {
        //rimuovo in ordine di importanza dal rosso finchè non sono finiti tutte le merci da rimuovere
        //oppure finchè non ci sono più merci di quel colore nella nave
        int redGoods = player.getShipBoard().getTotalNumberOfaToken(Token.RED_GOODS);
        int yellowGoods = player.getShipBoard().getTotalNumberOfaToken(Token.YELLOW_GOODS);
        int greenGoods = player.getShipBoard().getTotalNumberOfaToken(Token.GREEN_GOODS);
        int blueGoods = player.getShipBoard().getTotalNumberOfaToken(Token.BLUE_GOODS);

        List<Point> coordinatesOfRedGoods = player.getShipBoard().getPositionsOfaToken(Token.RED_GOODS);
        List<Point> coordinatesOfYellowGoods = player.getShipBoard().getPositionsOfaToken(Token.YELLOW_GOODS);
        List<Point> coordinatesOfGreenGoods = player.getShipBoard().getPositionsOfaToken(Token.GREEN_GOODS);
        List<Point> coordinatesOfBlueGoods = player.getShipBoard().getPositionsOfaToken(Token.BLUE_GOODS);
        while (redGoods != 0 && numberOfTokensToLose != 0) {
            player.getShipBoard().removeTokenFromTile(coordinatesOfRedGoods.removeFirst(),Token.RED_GOODS);
            redGoods--;
            numberOfTokensToLose--;
        }
        while (yellowGoods != 0 && numberOfTokensToLose != 0) {
            player.getShipBoard().removeTokenFromTile(coordinatesOfYellowGoods.removeFirst(),Token.YELLOW_GOODS);
            yellowGoods--;
            numberOfTokensToLose--;
        }
        while (greenGoods != 0 && numberOfTokensToLose != 0) {
            player.getShipBoard().removeTokenFromTile(coordinatesOfGreenGoods.removeFirst(),Token.GREEN_GOODS);
            greenGoods--;
            numberOfTokensToLose--;
        }
        while (blueGoods != 0 && numberOfTokensToLose != 0) {

            player.getShipBoard().removeTokenFromTile(coordinatesOfBlueGoods.removeFirst(),Token.BLUE_GOODS);
            blueGoods--;
            numberOfTokensToLose--;
        }
        if (numberOfTokensToLose != 0) {
            int batteryCells = player.getShipBoard().getTotalNumberOfaToken(Token.BATTERY_CELLS);
            List<Point> coordinatesOfBatteryCells = player.getShipBoard().getPositionsOfaToken(Token.BATTERY_CELLS);
            while ( batteryCells != 0 && numberOfTokensToLose != 0) {
                player.getShipBoard().removeTokenFromTile(coordinatesOfBatteryCells.removeFirst(),Token.BATTERY_CELLS);
                batteryCells--;
                numberOfTokensToLose--;
            }
        }

    }


    public void setPosition(int position) {
        throw new IllegalMethodOnThisCardException("Illegal Method On Effect LoseGoods");
    }

    public void setListOfCoordinates(List<Pair<Point,Token>> coordinates) {
        throw new IllegalMethodOnThisCardException("Illegal Method On Effect LoseGoods");
    }

    public void setCoordinate(Point coordinate) {
        throw new IllegalMethodOnThisCardException("Illegal Method On Effect LoseGoods");
    }

    public int getLooseGoods() {
        return numberOfTokensToLose;
    }
    public int getLostCrew() {
        throw new IllegalMethodOnThisCardException("Illegal Method On Effect LoseGoods");
    }

}
