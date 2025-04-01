package it.polimi.ingsw.is25am49.model;


import it.polimi.ingsw.is25am49.model.shipboard.Tile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
        tileDeck.put(tile.getIdTile(),tile);
    }

    public Tile draw(int index){
        Tile tmp =  tileDeck.get(index);
        tileDeck.remove(index);
        tmp.uncoverTile();
        return tmp;
    }

}
