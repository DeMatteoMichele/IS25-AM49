package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;

import java.util.List;

public class WarZoneCard extends AdventureCard {


    public WarZoneCard(GameBoard gameBoard,String description, int daysLost, int level, int creditsEarned, List<CardEffect> listEffects) {
        super(gameBoard,description, daysLost, level, creditsEarned);
        listEffects.addAll(listEffects);
    }

    @Override
    public void checkRequirements() {
    }



    @Override
    public void applyEffect() {


    }


}
