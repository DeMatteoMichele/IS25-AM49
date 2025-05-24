package it.polimi.ingsw.is25am49.client.model.cardeffects;

import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.util.List;

public abstract class CardEffect {
    protected final String name_effect;

    /**
     * Constructor of CardEffect
     * @param name_effect name of the effect
     */
    public CardEffect(String name_effect) {
        this.name_effect = name_effect;
    }

    /**
     * Method that applies the effect to the player
     * @param player player to which the effect is applied
     */
    public abstract void action(Player player);

    /**
     * Method that sets the position where the effect is applied
     * It sets the number of row or column (it depend on the orientation) where the effect is applied
     * @param position position of the effect
     */
    public abstract void setPosition(int position);

    /**
     * Method that sets the list of token and coordinates where the effect is applied
     * @param coordinates list of token and coordinates where the effect is applied
     */
    public abstract void setListOfCoordinates(List<Pair<Point, Token>> coordinates);

    /**
     * Method that sets the coordinate where the effect is applied
     * @param coordinate coordinate where the effect is applied
     */
    public abstract void setCoordinate(Point coordinate);

//    /**
//     * Method that gets the list of token and coordinates where the effect is applied
//     * @return list of token and coordinates where the effect is applied
//     */
//    public List<Pair<Point,Token>> getCoordinates(){
//        return null;
//    };

    /**
     * Method that gets the level of the effect
     * @return level of the effect
     */
    public int getLevel(){
        return 0;
    }

    /**
     * Method that gets the name of the effect
     * @return name of the effect
     */
    public String getDescription(){
        return name_effect;
    }
    /**
     * Method that gets the number of crew lost
     * @return number of crew lost
     */
    public abstract int getLostCrew();

}
