package it.polimi.ingsw.is25am49.server.model.cardeffects;

import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisCardException;
import javafx.util.Pair;
import java.util.List;
import java.awt.*;

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

    public void setListOfCoordinates(List<Pair<Point, Token>> positions) {

    }

    @Override
    public void setCoordinate(Point coordinate) {

    }
}
