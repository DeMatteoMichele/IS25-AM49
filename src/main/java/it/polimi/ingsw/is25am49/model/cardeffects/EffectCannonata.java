package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.Orientation;

public class EffectCannonata extends CardEffect {
    private final int level;
    private final Orientation direction;
    private int position;

    public EffectCannonata(String name_effect, int level, Orientation direction) {
        super(name_effect);
        this.level = level;
        this.direction = direction;
    }
    public void action(Player player) {}
    public void setPosition(int position) {}
}
