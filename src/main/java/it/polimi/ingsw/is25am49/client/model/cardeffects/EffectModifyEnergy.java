package it.polimi.ingsw.is25am49.client.model.cardeffects;

import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisCardException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalSizeToRemove;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EffectModifyEnergy extends CardEffect {
    private int energy;
    private Point coordinate;
    List<Pair<Point, Token>> coordinates = new ArrayList<>();
    public EffectModifyEnergy(String name_effect, int energy) {

        super(name_effect);
        this.energy = energy;
    }
    public void action(Player player) {

        if(coordinates.size()!=energy) {
            throw new IllegalSizeToRemove("Please select the right amount of energy to remove");
        }
        //per tutte le coordinate nella lista rimuovo il rispettivo Token
        //in qusto caso il token sarà sempre BATTERYCELLS
        for (Pair<Point, Token> coordinate : coordinates) {
            player.getShipBoard().removeTokenFromTile(coordinate.getKey(), coordinate.getValue());

        }
    }
    public void setPosition(int position) {
        throw new IllegalMethodOnThisCardException("Illegal Method On This Card");
    }

    public void setCoordinate(Point coordinate) {
        throw new IllegalMethodOnThisCardException("Illegal Method On This Card");
    }

    @Override
    public int getLostCrew() {
        throw new IllegalMethodOnThisCardException("This method is not applicable for this effect");
    }

    public void setListOfCoordinates(List<Pair<Point, Token>> coordinates) {
        this.coordinates = coordinates;
    }
}
