package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectWinGoods;

public class Planet {
    private boolean isOccupied;
    private int playernumber;
    private CardEffect effect;
    private int planetId;
    public Planet(int redGoods, int yellowGoods, int greenGoods, int blueGoods,int planetID) {
        this.isOccupied = false;
        this.effect = new EffectWinGoods("Win goods", redGoods, yellowGoods, greenGoods, blueGoods);
        this.planetId = planetID;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;

    }

    public int getPlanetId() {
        return planetId;
    }

    public CardEffect getEffect() {
        return effect;
    }

    public int getPlayernumber() {
        return playernumber;
    }

    public void setPlayernumber(int playernumber) {
        this.playernumber = playernumber;
    }


}
