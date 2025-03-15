package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisCardException;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.IllegalFormatWidthException;

public class EffectWinGoods extends CardEffect {
    private int redGoods;
    private int yellowGoods;
    private int greenGoods;
    private int blueGoods;

    public EffectWinGoods(String name_effect, int redGoods, int yellowGoods, int greenGoods, int blueGoods) {
        super(name_effect);
        this.redGoods = redGoods;
        this.yellowGoods = yellowGoods;
        this.greenGoods = greenGoods;
        this.blueGoods = blueGoods;
    }
    public void action(Player player) {


    }
    public void setPosition(int position) {
        throw new IllegalMethodOnThisCardException("Illegal Method On This Card");
    }

    public void setListOfCoordinates(ArrayList<Pair<Point, Token>> positions) {

    }

    @Override
    public void setCoordinate(Point coordinate) {

    }
}
