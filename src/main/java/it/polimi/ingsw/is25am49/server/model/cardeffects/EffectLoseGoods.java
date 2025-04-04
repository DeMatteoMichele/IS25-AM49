package it.polimi.ingsw.is25am49.server.model.cardeffects;

import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisCardException;
import javafx.util.Pair;

import java.awt.*;
import java.util.List;

public class EffectLoseGoods extends CardEffect {
    private int numberOfTokensToLose;


    public EffectLoseGoods(String name_effect, int numberOfTokensToLose) {

        super(name_effect);
        this.numberOfTokensToLose = numberOfTokensToLose;
    }
    public void action(Player player) {
        //rimuovo in ordine di importanza dal rosso finchè non sono finiti tutte le merci da rimuovere
        //oppure finchè non ci sono più merci di quel colore nella nave
        while (player.getShipBoard().getTotalNumberOfaToken(Token.RED_GOODS) != 0 && numberOfTokensToLose != 0) {

            List<Point> coordinates = player.getShipBoard().getPositionsOfaToken(Token.RED_GOODS);
            player.getShipBoard().removeTokenFromTile(coordinates.getFirst(),Token.RED_GOODS);
            numberOfTokensToLose--;
        }
        while (player.getShipBoard().getTotalNumberOfaToken(Token.YELLOW_GOODS) != 0 && numberOfTokensToLose != 0) {
            List<Point> coordinates = player.getShipBoard().getPositionsOfaToken(Token.YELLOW_GOODS);
            player.getShipBoard().removeTokenFromTile(coordinates.getFirst(),Token.YELLOW_GOODS);
            numberOfTokensToLose--;
        }
        while (player.getShipBoard().getTotalNumberOfaToken(Token.GREEN_GOODS) != 0 && numberOfTokensToLose != 0) {
            List<Point> coordinates = player.getShipBoard().getPositionsOfaToken(Token.GREEN_GOODS);
            player.getShipBoard().removeTokenFromTile(coordinates.getFirst(),Token.GREEN_GOODS);
            numberOfTokensToLose--;
        }
        while (player.getShipBoard().getTotalNumberOfaToken(Token.BLUE_GOODS) != 0 && numberOfTokensToLose != 0) {
            List<Point> coordinates = player.getShipBoard().getPositionsOfaToken(Token.BLUE_GOODS);
            player.getShipBoard().removeTokenFromTile(coordinates.getFirst(),Token.BLUE_GOODS);
            numberOfTokensToLose--;
        }
        if (numberOfTokensToLose != 0) {
            while (player.getShipBoard().getTotalNumberOfaToken(Token.BATTERY_CELLS) != 0 && numberOfTokensToLose != 0) {
                List<Point> coordinates = player.getShipBoard().getPositionsOfaToken(Token.BATTERY_CELLS);
                player.getShipBoard().removeTokenFromTile((coordinates.getFirst()),Token.BATTERY_CELLS);
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

}
