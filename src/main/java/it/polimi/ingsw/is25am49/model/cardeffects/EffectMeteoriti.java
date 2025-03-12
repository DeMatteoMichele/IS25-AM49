package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.model.*;
import it.polimi.ingsw.is25am49.model.enums.Orientation;

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
    public void setPosition(int position) {}
}
