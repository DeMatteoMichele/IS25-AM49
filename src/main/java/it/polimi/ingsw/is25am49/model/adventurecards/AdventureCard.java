package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class AdventureCard {
    protected String description;
    protected int daysLost;
    protected int flagDecision;
    protected int creditsEarned;
    protected final List<CardEffect> ListEffects;
    protected int level;

    public AdventureCard(String description, int daysLost, int level, int creditsEarned, List<CardEffect> ListEffects) {
        this.description = description;
        this.daysLost = daysLost;
        this.flagDecision = 0;
        this.creditsEarned = creditsEarned;
        this.level = level;
        this.ListEffects = new ArrayList<>(ListEffects);
    }

    public String getDescription() {
        return description;
    }

    public void setDaysLost(int daysLost) {
        this.daysLost = daysLost;
    }

    public void setFlagDecision(int flagDecision) {
        this.flagDecision = flagDecision;
    }

    public abstract void checkRequirements(Player player);

    public void addEffect(CardEffect effect) {
        ListEffects.add(effect);
    }

    public void rollDices(){}

    public abstract void askForDecision(Player player);

    public abstract void applyEffect(ArrayList<Pair<Integer,Player>> leaderboard);

    public int getLevel() {
        return level;
    }



}
