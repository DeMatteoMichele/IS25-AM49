package it.polimi.ingsw.is25am49.client.model.cardeffects;

import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.util.List;

public abstract class CardEffect {
    protected final String name_effect;

    public CardEffect(String name_effect) {
        this.name_effect = name_effect;
    }
    public abstract void action(Player player);
    public abstract void setPosition(int position);

    //public abstract void setListOfCoordinates(List<Point> coordinates);
    public abstract void setListOfCoordinates(List<Pair<Point, Token>> coordinates);
    public abstract void setCoordinate(Point coordinate);
    public List<Pair<Point, Token>> getCoordinates(){
        return null;
    };
    public int getLevel(){
        return 0;
    }
    public String getDescription(){
        return name_effect;
    }


}
