package it.polimi.ingsw.is25am49.server.model;


import it.polimi.ingsw.is25am49.server.model.exceptions.OutOfDeckExceptions;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class TileCardDeck {
    private final ConcurrentHashMap<Integer, Tile> tileDeck;
    private final ConcurrentHashMap<Player, Integer> lastRemovedTile;
    private final int numOfTiles;

    public TileCardDeck() {
        lastRemovedTile = new ConcurrentHashMap<>();
        numOfTiles = 157;
        tileDeck = new ConcurrentHashMap<>();
        HashMap<Integer, Tile> temporaryTileDeck = new HashMap<>(Objects.requireNonNull(Tile.generateTiles()));
        List<Integer> trackingRandoms = new ArrayList<>(numOfTiles);
        for (int i = 0; i < numOfTiles; i++)
            trackingRandoms.add(i);
        Collections.shuffle(trackingRandoms);
        temporaryTileDeck.forEach((key, value) -> tileDeck.put(trackingRandoms.get(tileDeck.size()), value));
    }
    public void addDiscardTile(Tile tile, Player player) {
        tile.uncoverTile();
        tileDeck.put(lastRemovedTile.get(player),tile);
    }

    public Tile draw(int index, Player player) {
        if(tileDeck.containsKey(index)) {
            Tile tmp = tileDeck.get(index);
            tileDeck.remove(index);
            lastRemovedTile.put(player, index);
            tmp.uncoverTile();
            return tmp;
        }
        else
            throw new OutOfDeckExceptions("Tile not found in the deck");
    }
    public Stream<Map.Entry<Integer, Tile>> stream(){
        return tileDeck.entrySet().stream();
    }
    public int getNumberOfTile() {
        return numOfTiles;
    }
}
