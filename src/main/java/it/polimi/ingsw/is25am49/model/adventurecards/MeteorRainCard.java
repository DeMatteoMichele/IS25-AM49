package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;

import java.util.ArrayList;
import java.util.List;

public class MeteorRainCard extends AdventureCard {

    List<CardEffect> listEffects = new ArrayList<>();

    public MeteorRainCard(GameBoard gameBoard, String description, int level, List<CardEffect> ListEffects) {
        super(gameBoard, description, level);
        listEffects.addAll(ListEffects);
    }

    @Override
    public void drawCard() {
        super.drawCard();
        System.out.println("You have found a meteor rain! The leader must roll the dices");
        currentEffect = listEffects.removeFirst();
        currentState = AdventureStatesTypes.ROLL_DICES;
    }


    @Override
    public void checkRequirements() {

    }


    @Override
    public void applyEffect() {

    }


}
