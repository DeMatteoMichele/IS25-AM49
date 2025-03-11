package it.polimi.ingsw.is25am49.model;


import it.polimi.ingsw.is25am49.model.shipboard.Tile;

import java.util.HashMap;
import java.util.Map;

public class TileCardDeck {
    private final Map<Integer, Tile> tileDeck = new HashMap<>();

    public void generateDeck(){

    }

    public void shuffle(){

    }

    public void addDiscardTile(Tile tile){

        tileDeck.put(tile.getIdTile(),tile);
    }

    public Tile draw(Integer index){
        return tileDeck.get(index);
    }

}
