package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;

import java.util.List;

public class SpazioAperto extends AdventureCard {

    public SpazioAperto(String description, int daysLost, int level, int creditsEarned, List<CardEffect> ListEffects) {
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
    public void applyEffect(Player player) {

    }
}
