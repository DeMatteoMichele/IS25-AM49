package it.polimi.ingsw.is25am49.client.model.cardeffects;

import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.shared.enums.Orientation;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisCardException;
import javafx.util.Pair;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class EffectCannonata extends CardEffect {
    private final int level;
    private final Orientation direction;
    private int position;


    public EffectCannonata(String name_effect, int level, Orientation direction) {
        super(name_effect);
        this.level = level;
        this.direction = direction;
    }

    /**
     * This method applies the cannon effect to the player.
     * It depends on the level of the cannonade
     * It destroys the tile in the position of the cannonade
     * @param player the player to which the effect is applied
     */
    public void action(Player player) {
        // Implementazione per cannonata di livello 1
        if (level == 1) {
            Optional<Point> target = player.getShipBoard().getExposedPoint(position, direction);
            if (target.isPresent()) {
                player.getShipBoard().destroyTile(target.get());
                userInterface.updateDestroyedTiles(player.getIdPlayer(),player.getShipBoard().destroyDisconnectedTiles());
                userInterface.updateEffectCannonade(player.getIdPlayer(),  target.get(), direction);
            }
            else {
                userInterface.updateGenericCardMessage(-99, "Big cannonade coming from " + direction+" pointing at " + position + "  missed "+ player.getUsername() + "!");
            }
        } else if(level==0) {
            //cannonata di livello zero, si può prevenire con attivazione di scudi
            //controllo se ci sono scudi che proteggono dalle direioni da cui colpisce la cannoanata
            if (player.getShipBoard().getProtectedSides().contains(direction)) {
                userInterface.updateGenericCardMessage(-99, "No damage for " +player.getUsername() +", shield activated");
            } else {
                Optional<Point> target = player.getShipBoard().getExposedPoint(position, direction);
                if (target.isPresent()) {
                    player.getShipBoard().destroyTile(target.get());
                    userInterface.updateEffectCannonade(player.getIdPlayer(), target.get(), direction);
                    userInterface.updateDestroyedTiles(player.getIdPlayer(),player.getShipBoard().destroyDisconnectedTiles());
                }
                else userInterface.updateGenericCardMessage(-99, "Small cannonade coming from " + direction+" pointing at " + position + "  missed "+ player.getUsername() + "!");
            }
        }
    }

    /**
     * This method sets the position where the cannonade hits
     * It sets the number of row or column (it depend on the orientation) where the cannonade hits
     * @param position the position of the cannonade
     */
    public void setPosition(int position) {
        this.position = position;
        //indica dove colpisce il cannone
    }

    public void setListOfCoordinates(List<Pair<Point, Token>> listOfCoordinates) {
        throw new IllegalMethodOnThisCardException("Illegal Method On This Card");
    }
    public void setCoordinate(Point coordinate) {
        throw new IllegalMethodOnThisCardException("Illegal Method On This Card");
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getLostCrew() {
        throw new IllegalMethodOnThisCardException("Illegal Method On Effect LostCrew");
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

    public Orientation getOrientation() {
        return direction;
    }
}
