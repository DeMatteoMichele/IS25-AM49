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

    public TileCardDeck(int seedForGeneration) {
        lastRemovedTile = new ConcurrentHashMap<>();
        numOfTiles = 157;
        tileDeck = new ConcurrentHashMap<>();
        HashMap<Integer, Tile> temporaryTileDeck = new HashMap<>(Objects.requireNonNull(Tile.generateTiles()));
        List<Integer> trackingRandoms = new ArrayList<>(numOfTiles);
        for (int i = 0; i < numOfTiles; i++)
            trackingRandoms.add(i);
        Collections.shuffle(trackingRandoms,new Random(seedForGeneration));
        temporaryTileDeck.forEach((key, value) -> tileDeck.put(trackingRandoms.get(tileDeck.size()), value));
    }
    public void addDiscardTile(Tile tile, Player player) {
        tile.uncoverTile();
        tileDeck.put(lastRemovedTile.get(player),tile);
    }

    private Tile draw(int index, Player player) {
        synchronized (tileDeck.get(index)) {
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
    }
    public Tile drawByTileId(int tileId, Player player) {
        // cerco l'indice della carta nel mazzo che ha l'id specificato
        Optional<Map.Entry<Integer, Tile>> entry = tileDeck.entrySet().stream()
                .filter(e -> e.getValue().getIdTile() == tileId)
                .findFirst();

        if (entry.isPresent()) {
            int index = entry.get().getKey();
            return draw(index, player); // utilizza draw esistente
        } else {
            throw new OutOfDeckExceptions("Tessera con ID " + tileId + " non disponibile o già pescata");
        }
    }
    public Stream<Map.Entry<Integer, Tile>> stream(){
        return tileDeck.entrySet().stream();
    }
    public int getNumberOfTile() {
        return numOfTiles;
    }
    public Tile getTileById(int id) {
//        return tileDeck.entrySet().stream()
//                .filter(e -> e.getValue().getIdTile() == id)
//                .filter(e->!e.getValue().getIsCovered())
//                .map(Map.Entry::getValue)
//                .findFirst()
//                .orElse(null);


        return tileDeck.entrySet().stream()
                .filter(e -> e.getValue().getIdTile() == id)
                .map(Map.Entry::getValue)
                .findFirst()
                .map(tile -> {
                    if (tile.getIsCovered()) {
                        throw new OutOfDeckExceptions("La tessera con ID " + id + " è coperta e non può essere vista");
                    }
                    return tile;
                })
                .orElse(null);

    }

    public String toString() {

      StringBuilder sr = new StringBuilder();
              final int width = 136;
              final int tilesPerRow = 17;
              final int tileWidth = 8;//larghezza di ogni id
              String border = "+" + "-".repeat(width - 2) + "+\n";
              sr.append(border);

              for (int i = 0; i < numOfTiles; i++) {
                  if(i % tilesPerRow == 0) {
                      if (i != 0) {
                          sr.append("\n|\n"); //chiudo la riga con | e vado a capo di 2
                      }
                      sr.append("| "); // inizio la nuova riga
                  }

                  Tile tile = tileDeck.get(i);
                  if (tile != null) {
                      if (!tile.getIsCovered()) {
                          sr.append(String.format("%-" + tileWidth + "s", tile.getIdTile() + "\uD83D\uDD13"));
                      } else {
                          sr.append(String.format("%-" + tileWidth + "s", tile.getIdTile()));
                      }
                  } else {
                      // spazio per le tessere rimosse
                      sr.append(" ".repeat(tileWidth));
                  }
              }
              //un | alla fine dell'ultima riga
              if (numOfTiles > 0) {
                  sr.append("|\n");
              }
              sr.append(border);
              return sr.toString();
}}
