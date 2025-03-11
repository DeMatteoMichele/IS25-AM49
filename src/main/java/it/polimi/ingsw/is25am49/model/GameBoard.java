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

    public AdventureCardDeck getSmallAdventureDeck(Integer index) {
        return mapSmallAdventureDecks.get(index);
    }

    public List<Pair<Integer, Player>> getLeaderBoard(){
        return leaderBoard;
    }

    public void setCurrentCard(AdventureCard card) {
        currentCard = card;
    }

    public AdventureCard getCurrentCard(){
        return currentCard;
    }

    public AdventureCard drawAdventureCard() {
        return mainAdvenureDeck.draw();
    }

    public void addPlayerToLeaderboard(Integer days , Player player) {
        leaderBoard.add(new Pair<>(days, player));
    }

    public void addDiscardedTile(Player player) {
        Tile newTile = player.discardTile();
        tilesDeck.addDiscardTile(newTile);
        
    }

    public Tile drawTile(Integer index) {
        return tilesDeck.draw(index);
    }

    public Player getLeader() {
        return leaderBoard.get(0).getValue();
    }

    public Player getNextPlayer(Player player) {
        int currentIndex= leaderBoard.indexOf(player);

        if(currentIndex == -1){
            return null;
        }
        if(currentIndex +1 < leaderBoard.size()){
            return leaderBoard.get(currentIndex+1).getValue();
        }
        return null;

    }

    public AdventureCard getNextCard() {
        return mainAdvenureDeck.draw();
    }

    public void updateLeaderboard() {

    }
}
