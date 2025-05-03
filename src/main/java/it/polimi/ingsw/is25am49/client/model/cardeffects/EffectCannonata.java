package it.polimi.ingsw.is25am49.client.model.cardeffects;

import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.enums.Orientation;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
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
    public void action(Player player) {
        // Implementazione per cannonata di livello 1
        if (level == 1) {
            Optional<Point> target = player.getShipBoard().getExposedPoint(position,direction);
            if (target.isPresent()) {
                player.getShipBoard().destroyTile(target.get());
                System.out.println("distruggo il componente in posizione " + target.get());
                player.getShipBoard().destroyDisconnectedTiles();
            }
        } else if(level==0) {
            //cannonata di livello zero, si può prevenire con attivazione di scudi
            //controllo se ci sono scudi che proteggono dalle direioni da cui colpisce la cannoanata
            if(player.getShipBoard().getProtectedSides().contains(direction)) {
                System.out.println("No damage, shield activated");
            }
            else{
                Optional<Point> target = player.getShipBoard().getExposedPoint(position,direction);
                if (target.isPresent()) {
                    player.getShipBoard().destroyTile(target.get());
                    System.out.println("distruggo il componente in posizione " + target.get());
                    player.getShipBoard().destroyDisconnectedTiles();
                }
            }
    }}

    public void setPosition(int position) {
        this.position = position;
        //indica dove colpisce il cannone
    }

    public void setListOfCoordinates(List<Pair<Point, Token>> listOfCoordinates) {
        throw  new IllegalMethodOnThisCardException("Illegal Method On This Card");
    }
    public void setCoordinate(Point coordinate) {
        throw  new IllegalMethodOnThisCardException("Illegal Method On This Card");
    }

    @Override
    public int getLevel() {
        return level;
    }
}
