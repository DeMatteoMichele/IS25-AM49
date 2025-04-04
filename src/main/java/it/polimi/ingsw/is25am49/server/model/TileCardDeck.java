package it.polimi.ingsw.is25am49.server.model;


import it.polimi.ingsw.is25am49.server.model.exceptions.OutOfDeckExceptions;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;

import java.util.concurrent.ConcurrentHashMap;

public class TileCardDeck {
    private final ConcurrentHashMap<Integer, Tile> tileDeck;

    public TileCardDeck() {
        tileDeck = new ConcurrentHashMap<>();
    }
    public void generateDeck(){

    }

    public void shuffle(){

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
}
