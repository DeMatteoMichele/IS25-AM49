package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.model.Player;

public abstract class CardEffect {
    protected final String name_effect;
    CardEffect(String name_effect) {
        this.name_effect = name_effect;
    }
    public void action(Player player) {}
}
