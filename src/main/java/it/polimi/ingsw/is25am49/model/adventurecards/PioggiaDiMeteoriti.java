package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class PioggiaDiMeteoriti extends AdventureCard {

    public PioggiaDiMeteoriti(String description, int daysLost, int level, int creditsEarned, List<CardEffect> ListEffects) {
        super(description, daysLost, level, creditsEarned, ListEffects);
    }

    @Override
    public void checkRequirements(Player player) {

    }

    @Override
    public void askForDecision(Player player) {

    }

    @Override
    public void applyEffect(ArrayList<Pair<Integer, Player>> leaderboard) {

    }
}
