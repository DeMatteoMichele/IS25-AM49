package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;

import java.util.ArrayList;
import java.util.List;

public class PlanetCard extends AdventureCard {

    protected final List<Planet> planets=new ArrayList<Planet>();

    public PlanetCard(GameBoard gameBoard, String description, int daysLost, int level, int creditsEarned, List<CardEffect> ListEffects){
        super(gameBoard,description, daysLost, level, creditsEarned);
        listEffects.addAll(ListEffects);
    }
    public void addPlanet(Planet planet){
        planets.add(planet);
    }

    @Override
    public void checkRequirements() {

    }



    @Override
    public void applyEffect() {


    }







    
}
