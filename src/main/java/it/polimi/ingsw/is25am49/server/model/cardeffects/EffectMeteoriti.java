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

public class EffectMeteoriti extends CardEffect {
    private final int level;
    private final Orientation direction;
    private int position;



    public EffectMeteoriti(String name_effect, int level, Orientation direction) {
        super(name_effect);
        this.level = level;
        this.direction = direction;
    }
    public void action(Player player) {
        //meteorite piccolo
        // posso attivare gli scudi, se non li attivo subisco effetto
        //componente distrutto solo se colspice il connettore esposto
        if(level==0) {
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

                    }}}
        }
        else{
            //meteorite grande
            //unica possibilità è sparargli contro con un cannone
            if(defendAgainstMeteor(player)){
                System.out.println("Distruggo il meteorite");
            }
            else{
                Optional<Point> target = player.getShipBoard().getExposedPoint(position, direction);
                target.ifPresent(point -> player.getShipBoard().destroyTile(point));
                System.out.println("distruggo il componente in posizione " + target.get());
                player.getShipBoard().destroyDisconnectedTiles();
            }
        }
    }
    private List<Point> getAvailableCannons(Player player) {
        //ottengo tutti i cannoni singoli e doppi
        List<Point> cannons = new ArrayList<>(player.getShipBoard().getComponentsPositions(ComponentType.CANNON));
        cannons.addAll(player.getShipBoard().getComponentsPositions(ComponentType.DOUBLE_CANNON));

        //filtro i cannoni, voglio solo quelli nella giusta direzione e orientazione
        return cannons.stream()
                .filter(point -> isCannonUsableAgainstMeteor(player, point))
                .collect(java.util.stream.Collectors.toList());
    }

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

    public void setPosition(int position) {
        this.position = position;
        //serve per i meteoriti per selezionare la riga in cui viene colpito, rollando i dadi aggiorni set position
    }

    public void setListOfCoordinates(List<Pair<Point, Token>> listOfCoordinates) {
        throw new IllegalMethodOnThisCardException("This method is not applicable for this effect");
    }

    public void setCoordinate(Point coordinate) {
        throw new IllegalMethodOnThisCardException("This method is not applicable for this effect");
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getLostCrew() {
        throw new IllegalMethodOnThisCardException("This method is not applicable for this effect");
    }

    public Orientation getOrientation() {
        return direction;
    }

}
