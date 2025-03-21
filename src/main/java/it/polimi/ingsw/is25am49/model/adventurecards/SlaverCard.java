package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;

import java.util.List;

public class SlaverCard extends AdventureCard {

    public SlaverCard(GameBoard gameBoard, String description, int daysLost, int level, int creditsEarned, int minimumFirePower, int lostAstronauts, List<CardEffect> listEffects) {
        super(gameBoard,description, daysLost, level, creditsEarned);
        this.minimumFirePower = minimumFirePower;
        this.lostAstronauts = lostAstronauts;
        this.listEffects.addAll(listEffects);
    }

    @Override
    public void checkRequirements() {

    }

    @Override
    public void applyEffect() {

    }

}
