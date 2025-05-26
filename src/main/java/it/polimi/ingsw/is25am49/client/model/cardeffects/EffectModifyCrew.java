package it.polimi.ingsw.is25am49.client.model.cardeffects;

import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisCardException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalSizeToRemove;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EffectModifyCrew extends CardEffect {
    private final int lostCrew;
    private List<Pair<Point, Token>> coordinates = new ArrayList<>();
    public EffectModifyCrew(String nameEffect, int lostCrew) {
        super(nameEffect);
        this.lostCrew = lostCrew;
    }

    public void action(Player player) {
        if(coordinates.size()!=lostCrew) {
            throw new IllegalSizeToRemove("Please select the right amount of crew to remove "+ coordinates.size());
        }
        //per tutte le coordinate nella lista rimuovo il rispettivo Token
        for(Pair<Point, Token> coordinate : coordinates) {
            player.getShipBoard().removeTokenFromTile(coordinate.getKey(), coordinate.getValue());
            userInterface.updateGenericCardMessage(player.getIdPlayer(),"rimozione di "+ coordinate.getValue() + " dalla posizione "+ coordinate.getKey());
            userInterface.updateEffectModifyCrew(player.getIdPlayer(), coordinate.getKey(), coordinate.getValue());
        }
    }

    public void setPosition(int position) {
        throw new IllegalMethodOnThisCardException("Illegal Method on Effect ModifyCrew");
    }

    public void setCoordinate(Point coordinate) {
        throw new IllegalMethodOnThisCardException("Illegal Method on Effect ModifyCrew");
    }

    @Override
    public void setListOfCoordinates(List<Pair<Point, Token>> coordinates) {
        this.coordinates = coordinates;
    }
//    @Override
//    public List<Pair<Point,Token>> getCoordinates() {
//        return coordinates;
//    }
    @Override
    public int getLostCrew() {
        return lostCrew;
    }
}
