package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.model.shipboard.Tile;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameBoard {
    private final List<Pair<Integer, Player>> leaderBoard = new ArrayList<>();
    private AdventureCardDeck mainAdvenureDeck;
    private final HashMap<Integer, AdventureCardDeck> mapSmallAdventureDecks = new HashMap<>();
    private TileCardDeck tilesDeck;
    private AdventureCard currentCard;
    private int currentPlayer;
    private Timer timer;

    public AdventureCardDeck get_small_adventure_deck(Integer index) {
        return mapSmallAdventureDecks.get(index);
    }

    public List<Pair<Integer, Player>> get_leaderBoard(){
        return leaderBoard;
    }

    public void set_currentCard(AdventureCard card) {
        currentCard = card;
    }

    public AdventureCard get_current_card(){
        return currentCard;
    }

    public AdventureCard draw_adventure_card() {
        return mainAdvenureDeck.draw();
    }

    public void add_player_to_leaderboard(Integer days ,Player player) {
        leaderBoard.add(new Pair<>(days, player));
    }

    public void add_discarded_tile(Tile tile) {
        tilesDeck.put(tile.getIdTile(),tile);
    }

    public Tile draw_tile(Integer index) {
        return tilesDeck.draw(index);
    }

    public Player get_leader() {
        return leaderBoard.get(0).getValue();
    }

    public Player get_next_player(Player player) {
        int currentIndex= leaderBoard.indexOf(player);

        if(currentIndex == -1){
            return null;
        }
        if(currentIndex +1 < leaderBoard.size()){
            return leaderBoard.get(currentIndex+1).getValue();
        }
        return null;

    }

    public AdventureCard get_next_card() {
        return mainAdvenureDeck.draw();
    }

    public void update_leaderboard() {

    }
}
