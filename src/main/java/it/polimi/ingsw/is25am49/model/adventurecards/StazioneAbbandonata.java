package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.Token;

import java.util.ArrayList;
import java.util.List;

public class StazioneAbbandonata extends AdventureCard {
    protected final ArrayList<Token> Prize = new ArrayList<>();

    public StazioneAbbandonata(String description, int daysLost, int level, int creditsEarned, List<CardEffect> ListEffects) {
        super(description, daysLost, level, creditsEarned, ListEffects);
    }

    @Override
    public void checkRequirements(Player player) {

    }

    @Override
    public void askForDecision() {

    }

    @Override
    public void applyEffect(ArrayList<Player> players) {

    }

    public void addAllPrizes(ArrayList<Token> Prize) {
        this.Prize.addAll(Prize);
    }
}
