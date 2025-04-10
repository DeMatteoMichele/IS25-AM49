package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;

import java.util.ArrayList;
import java.util.List;

public class EpidemyCard extends AdventureCard {

    public EpidemyCard(GameBoard gameBoard, String description,int level) {
        super(gameBoard,description, level);
    }

    @Override
    public void checkRequirements() {

    }}



    //@Override
//    public void applyEffect() {
//        // Ottiene la plancia della nave dal game board
//        var shipBoard = gameBoard.getShipBoard();
//
//        // Trova tutte le cabine occupate
//        var occupiedCabins = shipBoard.grid.entrySet().stream()
//                .filter(entry -> entry.getValue().isPresent())
//                .filter(entry -> entry.getValue().get().getComponentType().equals(ComponentType.CABINE))
//                .filter(entry -> entry.getValue().get().hasCrewMembers())
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toList());
//
//        // Trova cabine occupate che sono interconnesse ad altre cabine occupate
//        for (Point cabin : occupiedCabins) {
//            if (hasConnectedOccupiedCabin(shipBoard, cabin, occupiedCabins)) {
//                // Rimuove un membro dell'equipaggio (umano o alieno) dalla cabina
//                Tile tile = shipBoard.getTile(cabin);
//                tile.removeOneCrewMember();
//            }
//        }
//
//        // Notifica ai giocatori l'effetto dell'epidemia
//        gameBoard.notifyPlayers(AdventureStatesTypes.EPIDEMIC_EFFECT);
//    }
//
//    /**
//     * Verifica se una cabina è interconnessa ad un'altra cabina occupata
//     * @param shipBoard la plancia della nave
//     * @param cabin la posizione della cabina da verificare
//     * @param occupiedCabins lista di tutte le cabine occupate
//     * @return true se la cabina è interconnessa ad un'altra cabina occupata
//     */
//    private boolean hasConnectedOccupiedCabin(ShipBoard shipBoard, Point cabin, List<Point> occupiedCabins) {
//        Tile cabinTile = shipBoard.getTile(cabin);
//
//        // Controlla ogni direzione per vedere se c'è una cabina occupata collegata
//        for (Orientation orientation : Orientation.values()) {
//            if (cabinTile.isConnectedIn(orientation)) {
//                Point adjacentPoint = getAdjacentPoint(cabin, orientation);
//                if (occupiedCabins.contains(adjacentPoint) && adjacentPoint != cabin) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    /**
//     * Calcola il punto adiacente in una data direzione
//     * @param point il punto di partenza
//     * @param orientation la direzione
//     * @return il punto adiacente
//     */
//    private Point getAdjacentPoint(Point point, Orientation orientation) {
//        return switch (orientation) {
//            case NORTH -> new Point(point.x, point.y - 1);
//            case SOUTH -> new Point(point.x, point.y + 1);
//            case EAST -> new Point(point.x + 1, point.y);
//            case WEST -> new Point(point.x - 1, point.y);
//        };
//    }
//}
