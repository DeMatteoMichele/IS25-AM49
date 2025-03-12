package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;

import java.util.ArrayList;
import java.util.List;

public class PlanetCard extends AdventureCard {

    protected final List<Planet> planets=new ArrayList<Planet>();

    public PlanetCard(String description, int daysLost, int level, int creditsEarned, List<CardEffect> ListEffects){
        super(description, daysLost, level, creditsEarned, ListEffects);
    }
    public void addPlanet(Planet planet){
        planets.add(planet);
    }

    @Override
    public int checkRequirements(Player player) {
        return 0;
    }



//    @Override
//    public void askForDecision(Player player) {
//
//    }

    @Override
    public void applyEffect(Player player) {

    }

    public List<Planet> getPlanets() {
        return planets;
    }







    
}
