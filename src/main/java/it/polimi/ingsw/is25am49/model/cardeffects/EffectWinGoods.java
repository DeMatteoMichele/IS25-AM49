package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.model.Player;

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

    }
}
