package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisCardException;

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
//        this.flagDecision = 0;
        this.creditsEarned = creditsEarned;
        this.level = level;
        this.ListEffects = new ArrayList<>(ListEffects);
    }

    public String getDescription() {
        return description;
    }
//
//    public void setDaysLost(int daysLost) {
//        this.daysLost = daysLost;
//    }
//
//    public void setFlagDecision(int flagDecision) {
//        this.flagDecision = flagDecision;
//    }

    public abstract int checkRequirements(Player player);

    public void drawCard() {
        System.out.println("Hai pescato la carta: " + getClass().getSimpleName());
    }



    public void addEffect(CardEffect effect) {
        ListEffects.add(effect);
    }

    public void rollDices(){
        throw new IllegalMethodOnThisCardException("Non puoi chiamare rollDices su questa carta");
    }

//    public abstract void askForDecision(Player player);

    public abstract void applyEffect(Player player);

    public int getLevel() {
        return level;
    }

    public List<Token> getLostTokens(){
        throw new IllegalMethodOnThisCardException("Non puoi chiamare getLostTokens su questa carta");
    }

    public List<Token> getPrize(){
        throw new IllegalMethodOnThisCardException("Non puoi chiamare getPrize su questa carta");
    }

}
