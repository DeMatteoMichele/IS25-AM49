package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.model.*;
import it.polimi.ingsw.is25am49.model.enums.Orientation;

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

    public void setListOfCoordinates(List listOfCoordinates) {

    }

    public void setCoordinate(Point coordinate) {

    }
}
