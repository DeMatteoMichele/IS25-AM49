package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;

import java.util.List;

public class MeteorRainCard extends AdventureCard {

    public MeteorRainCard(String description, int daysLost, int level, int creditsEarned, List<CardEffect> ListEffects) {
        super(description, daysLost, level, creditsEarned);
        listEffects.addAll(ListEffects);
    }

    @Override
    public AdventureStatesTypes checkRequirements(Player player) {
        return null;
    }

//    @Override
//    public void askForDecision(Player player) {
//
//    }

    @Override
    public void applyEffect(Player player) {
    return null;
    }
}
