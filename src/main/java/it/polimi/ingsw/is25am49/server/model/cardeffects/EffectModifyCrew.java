package it.polimi.ingsw.is25am49.server.model.cardeffects;

import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisCardException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalSizeToRemove;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * EffectModifyCrew represents a card effect that removes crew members and modifies the ship board's tiles.
 * It removes specified tokens from the player's ship board.
 */
public class EffectModifyCrew extends CardEffect {
    /**
     * Represents the number of crew members lost due to the effect.
     * Immutable and defined during object construction.
     */
    private final int lostCrew;
    /**
     * Stores a list of pairs, each containing a point on the ship board and a token to be removed.
     * Represents the coordinates and tokens affected by the effect.
     */
    private List<Pair<Point, Token>> coordinates = new ArrayList<>();

    /**
     * Constructs an EffectModifyCrew object, representing an effect that reduces
     * the crew on a player's ship.
     *
     * @param nameEffect the name of the effect
     * @param lostCrew the number of crew members to be lost
     */
    public EffectModifyCrew(String nameEffect, int lostCrew) {
        super(nameEffect);
        this.lostCrew = lostCrew;
    }

    /**
     * Executes the effect of removing specified tokens from the given player's ship board.
     * Each token is removed from its associated tile at the given coordinates.
     *
     * @param player the target player whose ship board will be modified
     */
    public void action(Player player) {
            for (Pair<Point, Token> coordinate : coordinates) {
                player.getShipBoard().removeTokenFromTile(coordinate.getKey(), coordinate.getValue());
                System.out.println("rimozione di " + coordinate.getValue() + " dalla posizione " + coordinate.getKey());
            }
    }

    /**
     * Overrides the abstract method to handle position setting for the card effect.
     * This implementation is not applicable for EffectModifyCrew and will throw an exception.
     *
     * @param position the position where the effect would be applied
     * @throws IllegalMethodOnThisCardException if this method is called on an instance of EffectModifyCrew
     */
    public void setPosition(int position) {
        throw new IllegalMethodOnThisCardException("Illegal Method on Effect ModifyCrew");
    }

    /**
     * Sets the coordinate where the effect is applied.
     * This method is not applicable for EffectModifyCrew and will throw an exception.
     *
     * @param coordinate the coordinate to be set
     * @throws IllegalMethodOnThisCardException when invoked on this card effect
     */
    public void setCoordinate(Point coordinate) {
        throw new IllegalMethodOnThisCardException("Illegal Method on Effect ModifyCrew");
    }

    /**
     * Sets the list of coordinates and associated tokens where the effect is applied.
     *
     * @param coordinates the list of pairs containing points (coordinates) and tokens to be modified or affected
     */
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

    @Override
    public List<Integer> getGoods() {
        throw new IllegalMethodOnThisCardException("Illegal Method On This Card");
    }
}
