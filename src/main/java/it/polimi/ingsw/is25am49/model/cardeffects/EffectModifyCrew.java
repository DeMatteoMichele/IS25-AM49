package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisCardException;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EffectModifyCrew extends CardEffect {
    private int lostCrew;
    private List<Pair<Point,Token>> coordinates = new ArrayList<>();
    public EffectModifyCrew(String nameEffect, int lostCrew) {
        super(nameEffect);
        this.lostCrew = lostCrew;
    }

    public void action(Player player) {
        //per tutte le coordinate nella lista rimuovo il rispettivo Token
        for(Pair<Point, Token> coordinate : coordinates) {
            player.getShipBoard().removeTokenFromTile(coordinate.getKey(), coordinate.getValue());
        }
    }

    public void setPosition(int position) {
        throw new IllegalMethodOnThisCardException("Illegal Method on Effect ModifyCrew");
    }

    public void setCoordinate(Point coordinate) {
        throw new IllegalMethodOnThisCardException("Illegal Method on Effect ModifyCrew");
    }

    @Override
    public void setListOfCoordinates(ArrayList<Pair<Point, Token>> coordinates) {
        this.coordinates = coordinates;
    }
}
