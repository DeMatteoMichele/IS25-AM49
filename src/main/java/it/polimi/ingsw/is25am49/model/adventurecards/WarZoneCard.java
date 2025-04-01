package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;

import java.util.List;

public class WarZoneCard extends AdventureCard {


    public WarZoneCard(GameBoard gameBoard,String description, int level) {
        super(gameBoard,description, level);
        listEffects.addAll(listEffects);
    }

    @Override
    public void checkRequirements() {
    }



    @Override
    public void applyEffect() {


    }


}
