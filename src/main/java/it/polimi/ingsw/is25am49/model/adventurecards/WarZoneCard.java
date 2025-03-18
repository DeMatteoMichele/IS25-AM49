package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;

import java.util.List;

public class WarZoneCard extends AdventureCard {


    public WarZoneCard(String description, int daysLost, int level, int creditsEarned, List<CardEffect> listEffects) {
        super(description, daysLost, level, creditsEarned);
        listEffects.addAll(listEffects);
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
    public AdventureStatesTypes applyEffect(Player player) {
        return null;

    }


}
