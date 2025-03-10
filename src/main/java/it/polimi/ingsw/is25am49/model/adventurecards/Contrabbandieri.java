package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.Token;

import java.util.ArrayList;
import java.util.List;

public class Contrabbandieri extends AdventureCard {
    int minimumFirePower;
    private final List<Token> LostTokens=new ArrayList<>();
    private final List<Token> Prize=new ArrayList<>();

    public Contrabbandieri(String description, int daysLost, int level, int creditsEarned, List<CardEffect> ListEffects) {
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

    public void setMinimumFirePower(int minimumFirePower) {
        this.minimumFirePower = minimumFirePower;
    }

    public void addAllPrizes(List<Token> Prize) {
        this.Prize.addAll(Prize);
    }

    public void add_all_lost_tokens(List<Token> LostTokens) {
        this.LostTokens.addAll(LostTokens);
    }


}
