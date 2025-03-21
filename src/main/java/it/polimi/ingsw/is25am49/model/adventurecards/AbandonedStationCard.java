package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.enums.Token;

import java.util.ArrayList;
import java.util.List;

public class AbandonedStationCard extends AdventureCard {
    protected final ArrayList<Token> Prize = new ArrayList<>();

    public AbandonedStationCard(String description, int daysLost, int level, int creditsEarned, List<CardEffect> ListEffects) {
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
    public void applyEffect() {
        return null;

    }

    public void addAllPrizes(ArrayList<Token> Prize) {
        this.Prize.addAll(Prize);
    }
}
