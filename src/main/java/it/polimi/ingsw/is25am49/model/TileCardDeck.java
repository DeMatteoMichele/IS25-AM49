package it.polimi.ingsw.is25am49.model;


import it.polimi.ingsw.is25am49.model.shipboard.Tile;

import java.util.HashMap;
import java.util.Map;

public class TileCardDeck {
    private final Map<Integer, Tile> tileDeck = new HashMap<>();

    public void generate_deck(){

    }

    public void shuffle(){

    }

    public void add_discard_tile(Tile tile){
        tileDeck.put(tile.getIdTile(),tile);
    }

    public Tile draw(Integer index){
        return tileDeck.get(index);
    }

}
