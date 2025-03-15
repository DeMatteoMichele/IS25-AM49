package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.model.Player;

import java.awt.*;
import java.util.ArrayList;

public abstract class CardEffect {
    protected final String name_effect;

    public CardEffect(String name_effect) {
        this.name_effect = name_effect;
    }
    public abstract void action(Player player);
    public abstract void setPosition(int position);
    public abstract void setListOfCoordinates(ArrayList<Point> coordinates);

}
