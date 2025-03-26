package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.Orientation;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisCardException;
import javafx.util.Pair;
import java.util.List;

import java.awt.*;

public class EffectCannonata extends CardEffect {
    private final int level;
    private final Orientation direction;
    private int position;


    public EffectCannonata(String name_effect, int level, Orientation direction) {
        super(name_effect);
        this.level = level;
        this.direction = direction;
    }
    public void action(Player player) {

    }

    public void setPosition(int position) {
        this.position = position;
        //indica dove colpisce il cannone
    }

    public void setListOfCoordinates(List<Pair<Point, Token>> listOfCoordinates) {
        throw  new IllegalMethodOnThisCardException("Illegal Method On This Card");
    }
    public void setCoordinate(Point coordinate) {
        throw  new IllegalMethodOnThisCardException("Illegal Method On This Card");
    }
}
