package it.polimi.ingsw.is25am49.server.model.cardeffects;

import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.ConnectorType;
import it.polimi.ingsw.is25am49.shared.enums.Orientation;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisCardException;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.util.Optional;

/**
 * Represents the effect of a meteorite in the game.
 * A meteorite may have different levels and orientations, impacting the player's ship accordingly.
 * Level 0 meteors can be blocked by shields, while larger meteors require cannons to destroy.
 */
public class EffectMeteoriti extends CardEffect {
    private final int level;
    private final Orientation direction;
    private int position;


    /**
     * Creates an instance of the EffectMeteoriti class, representing an effect associated
     * with meteorit impacts, setting its level and orientation.
     *
     * @param name_effect the name of the effect
     * @param level the intensity or level of the effect
     * @param direction the orientation associated with the effect
     */
    public EffectMeteoriti(String name_effect, int level, Orientation direction) {
        super(name_effect);
        this.level = level;
        this.direction = direction;
    }

    /**
     * Applies the meteorite effect to the specified player's ship board.
     * The action varies depending on the meteorite's level. At level 0,
     * it checks for shields or exposed connectors to determine damage. For higher levels,
     * the player may attempt to destroy the meteorite with a cannon; otherwise,
     * components are damaged on impact.
     *
     * @param player the player whose ship board is affected by the meteorite.
     */
    public void action(Player player) {
        //meteorite piccolo
        // posso attivare gli scudi, se non li attivo subisco effetto
        //componente distrutto solo se colspice il connettore esposto
        if (level == 0) {
            if (player.getShipBoard().getProtectedSides().contains(direction)) {
                System.out.println("No damage, shield activated");
            } else {
                Optional<Point> target = player.getShipBoard().getExposedPoint(position, direction);
                if (target.isPresent()) {
                    if (player.getShipBoard().getTile(target.get()).getConnector(direction) == ConnectorType.FLAT) {
                        System.out.println("No damage, the meteor bounces ");
                    } else {
                        player.getShipBoard().destroyTile(target.get());
                        System.out.println("distruggo il componente in posizione " + target.get());
                        player.getShipBoard().destroyDisconnectedTiles();

                    }
                }
            }
        } else {
            //meteorite grande
            //unica possibilità è sparargli contro con un cannone
            if (defendAgainstMeteor(player)) {
                System.out.println("Distruggo il meteorite");
            } else {
                Optional<Point> target = player.getShipBoard().getExposedPoint(position, direction);
                target.ifPresent(point -> {
                    player.getShipBoard().destroyTile(point);
                    System.out.println("distruggo il componente in posizione " +point.getX() + "," + point.getY());
                });
                player.getShipBoard().destroyDisconnectedTiles();
            }
        }
    }

    /**
     * Retrieves a list of usable cannon positions for a player. Filters cannons
     * based on their type and ensures they are oriented and positioned correctly
     * to be used against a meteor.
     *
     * @param player the player for whom the cannon positions are being retrieved
     * @return a list of points representing the usable cannon positions
     */
    private List<Point> getAvailableCannons(Player player) {
        //ottengo tutti i cannoni singoli e doppi
        List<Point> cannons = new ArrayList<>(player.getShipBoard().getComponentsPositions(ComponentType.CANNON));
        cannons.addAll(player.getShipBoard().getComponentsPositions(ComponentType.DOUBLE_CANNON));

        //filtro i cannoni, voglio solo quelli nella giusta direzione e orientazione
        return cannons.stream()
                .filter(point -> isCannonUsableAgainstMeteor(player, point))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Checks if a cannon is usable against a meteor based on its direction and position.
     *
     * @param player the player whose cannon is being checked
     * @param point the coordinates of the cannon to check
     * @return true if the cannon is in the correct direction and position to be usable, false otherwise
     */
    //verifica se il cannone è nella giusta direzione e posizione
    private boolean isCannonUsableAgainstMeteor(Player player, Point point) {
        // è nella giusta direzione?
        if (player.getShipBoard().getTile(point).getTileOrientation() != direction) {
            return false;
        }

        //è nella giusta riga o colonna
        if ((direction == Orientation.NORTH || direction == Orientation.SOUTH) && point.getX() != position) {
            return false;
        } else if ((direction == Orientation.EAST || direction == Orientation.WEST) && point.getY() != position) {
            return false;
        }

        return true;
    }

    /**
     * Determines whether the player can defend against a meteor using available cannons on their ship.
     *
     * @param player the player attempting to defend against the meteor event
     * @return true if the meteor is successfully defended against; false otherwise
     */
    private boolean defendAgainstMeteor(Player player) {
        //ottengo tutti i cannoni utilizzabili contro il meteorite
        List<Point> cannons = getAvailableCannons(player);

        //se non ci sono cannoni subisco l'effetto
        if (cannons.isEmpty()) {
//            System.out.println("Non hai cannoni da attivare. Subisci l'effetto del meteorite.");
            return false;
        }

        //c'è almeno un cannone utilizzabile?
        for (Point point : cannons) {
            //se il cannone è singolo posso difendermi
            if (player.getShipBoard().getTile(point).getComponent().getType() == ComponentType.CANNON) {
//                System.out.println("distruggo meteorite con cannone singolo");
                return true;
            } else {
                //se il cannone è doppio devo controllare che sia stato attivati
                if (player.getShipBoard().getTile(point).getComponent().getIsActive()) {
//                    System.out.println("Distruggo meteorite con cannone doppio");
                    return true;
                }
            }
        }

//        System.out.println("Cannoni doppi non attivi, non puoi difenderti");
        return false;
    }

    /**
     * Sets the position associated with the meteor effect.
     * Updates the row or column affected by the meteor based on the given position.
     *
     * @param position the position of the effect, indicating the specific row or column hit
     */
    public void setPosition(int position) {
        this.position = position;
        //serve per i meteoriti per selezionare la riga in cui viene colpito, rollando i dadi aggiorni set position
    }

    /**
     * Sets the list of coordinates and associated tokens where the effect is applied.
     * This method is not applicable for this effect and will throw an exception if called.
     *
     * @param listOfCoordinates the list of pairs containing Points and Tokens to set
     * @throws IllegalMethodOnThisCardException if the method is called
     */
    public void setListOfCoordinates(List<Pair<Point, Token>> listOfCoordinates) {
        throw new IllegalMethodOnThisCardException("This method is not applicable for this effect");
    }

    /**
     * Sets the coordinate for the effect. This method is not applicable for this effect
     * and will always throw an exception.
     *
     * @param coordinate the coordinate to be set
     * @throws IllegalMethodOnThisCardException if the method is invoked
     */
    public void setCoordinate(Point coordinate) {
        throw new IllegalMethodOnThisCardException("This method is not applicable for this effect");
    }

    /**
     * Retrieves the level of the effect.
     * @return the level of the effect.
     */
    @Override
    public int getLevel() {
        return level;
    }

    /**
     * Retrieves the number of crew members lost.
     * This method is not applicable for this effect and will always throw an exception.
     *
     * @return This method does not return a value as it always throws an exception.
     * @throws IllegalMethodOnThisCardException always thrown as this method is not applicable.
     */
    @Override
    public int getLostCrew() {
        throw new IllegalMethodOnThisCardException("This method is not applicable for this effect");
    }

    /**
     * Retrieves the list of goods associated with the card effect.
     * Throws an exception if the method is not applicable to the current card.
     *
     * @return a list of integers representing the quantities of different types of goods.
     * @throws IllegalMethodOnThisCardException if the method is invoked on an unsupported card.
     */
    @Override
    public List<Integer> getGoods() {
        throw new IllegalMethodOnThisCardException("Illegal Method On This Card");
    }

    /**
     * Retrieves the orientation of the effect.
     *
     * @return the current orientation (NORTH, EAST, SOUTH, or WEST)
     */
    public Orientation getOrientation() {
        return direction;
    }

}
