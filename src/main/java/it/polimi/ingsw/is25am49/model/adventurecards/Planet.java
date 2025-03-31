package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectWinGoods;
import it.polimi.ingsw.is25am49.model.enums.Token;
import javafx.scene.effect.Effect;

import java.util.ArrayList;
import java.util.List;

public class Planet {
    private boolean isOccupied;
    private int playernumber;
    private CardEffect effect;
    public Planet(int redGoods, int yellowGoods, int greenGoods, int blueGoods) {
        this.isOccupied = false;

        this.effect = new EffectWinGoods("Win goods", redGoods, yellowGoods, greenGoods, blueGoods);

    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }
    public CardEffect getEffect(){
        return effect;
    }

    public int getPlayernumber() {
        return playernumber;
    }
    public void setPlayernumber(int playernumber) {
        this.playernumber = playernumber;
    }





}
