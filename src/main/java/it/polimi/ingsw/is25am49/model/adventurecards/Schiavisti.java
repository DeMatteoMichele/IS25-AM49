package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;

import java.util.ArrayList;
import java.util.List;

public class Schiavisti extends AdventureCard {

    int lostAstronauts;
    int minimumFirePower;

    public Schiavisti(String description, int daysLost, int level, int creditsEarned, List<CardEffect> ListEffects) {
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

    public void setLostAstronauts(int lostAstronauts) {
        this.lostAstronauts = lostAstronauts;
    }

}
