package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;

import java.util.List;

public class SlaverCard extends AdventureCard {

    int lostAstronauts;
    int minimumFirePower;

    public SlaverCard(String description, int daysLost, int level, int creditsEarned, List<CardEffect> ListEffects) {
        super(description, daysLost, level, creditsEarned, ListEffects);
    }

    @Override
    public int checkRequirements(Player player) {
        return 0;
    }

//    @Override
//    public void askForDecision(Player player) {
//
//    }

    @Override
    public boolean applyEffect(Player player) {
        return true;

    }

    public void setMinimumFirePower(int minimumFirePower) {
        this.minimumFirePower = minimumFirePower;
    }

    public void setLostAstronauts(int lostAstronauts) {
        this.lostAstronauts = lostAstronauts;
    }

}
