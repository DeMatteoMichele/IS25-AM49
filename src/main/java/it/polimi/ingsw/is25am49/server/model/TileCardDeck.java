package it.polimi.ingsw.is25am49.server.model;


import it.polimi.ingsw.is25am49.server.model.exceptions.OutOfDeckExceptions;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class TileCardDeck {
    private ConcurrentHashMap<Integer, Tile> tileDeck;
    private ConcurrentHashMap<Integer, Tile> temporaryTileDeck;

    public TileCardDeck() {
        tileDeck = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, Tile> temporaryTileDeck = new ConcurrentHashMap<>(Objects.requireNonNull(Tile.generateTiles()));
        Random rand = new Random();
        temporaryTileDeck.forEach((key, value) -> tileDeck.put(rand.nextInt(200), value));
    }
    public void addDiscardTile(Tile tile){
        tile.uncoverTile();
        tileDeck.put(tile.getIdTile(),tile);
    }

    public Tile draw(int index){
        if(tileDeck.containsKey(index)) {
            Tile tmp = tileDeck.get(index);
            tileDeck.remove(index);
            tmp.uncoverTile();
            return tmp;
        }
        else
            throw new OutOfDeckExceptions("Tile not found in the deck");
    }
    public Stream<Map.Entry<Integer, Tile>> stream(){
        return tileDeck.entrySet().stream();
    }
}
