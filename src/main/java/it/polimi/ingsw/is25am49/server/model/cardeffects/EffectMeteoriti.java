package it.polimi.ingsw.is25am49.server.model.cardeffects;

import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import javafx.util.Pair;
import java.util.List;

import java.awt.*;

public class EffectMeteoriti extends CardEffect {
    private final int level;
    private final Orientation direction;
    private int position;


    public EffectMeteoriti(String name_effect, int level, Orientation direction) {
        super(name_effect);
        this.level = level;
        this.direction = direction;
    }
    public void action(Player player) {}
    public void setPosition(int position) {
        //serve per i meteoriti per selezionare la riga in cui viene colpito, rollando i dadi aggiorni set position
    }

    public void setListOfCoordinates(List<Pair<Point, Token>> listOfCoordinates) {

    }

    public void setCoordinate(Point coordinate) {

    }
}
