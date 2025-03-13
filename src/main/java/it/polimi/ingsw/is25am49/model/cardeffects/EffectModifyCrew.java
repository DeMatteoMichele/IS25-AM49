package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.model.Player;

public class EffectModifyCrew extends CardEffect {
    private int lostAstronauts;
    public EffectModifyCrew(String nameEffect, int lostAstronauts) {

        super(nameEffect);
        this.lostAstronauts = lostAstronauts;
    }
    public void action(Player player) {}
    public void setPosition(int position) {}
}
