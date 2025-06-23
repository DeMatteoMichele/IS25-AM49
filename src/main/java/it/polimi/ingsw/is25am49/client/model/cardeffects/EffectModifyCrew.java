package it.polimi.ingsw.is25am49.client.model.cardeffects;

import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;
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
        for (Pair<Point, Token> coordinate : coordinates) {
            player.getShipBoard().removeTokenFromTile(coordinate.getKey(), coordinate.getValue());
            System.out.println("rimozione di " + coordinate.getValue() + " dalla posizione " + coordinate.getKey());
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

    /**
     * @return 
     */
    @Override
    public int getRedGoods() {
        return 0;
    }

    /**
     * @return 
     */
    @Override
    public int getYellowGoods() {
        return 0;
    }

    /**
     * @return 
     */
    @Override
    public int getBlueGoods() {
        return 0;
    }

    /**
     * @return 
     */
    @Override
    public int getGreenGoods() {
        return 0;
    }
}
