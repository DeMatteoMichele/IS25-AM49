package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.model.enums.Token;
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

    public void drawAdventureCard() {
        //return mainAdvenureDeck.draw();
        currentCard= mainAdvenureDeck.draw();
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

    public void calculate_final_score(Player player) {
        int toAdd = 0;
        toAdd = 5 - leaderBoard.indexOf(player);
        toAdd = toAdd - player.getLostComponents();

        int redToken= player.getShipBoard().getTotalNumberOfaToken(Token.RED_GOODS);
        int blueToken = player.getShipBoard().getTotalNumberOfaToken(Token.BLUE_GOODS);
        int yellowToken = player.getShipBoard().getTotalNumberOfaToken(Token.YELLOW_GOODS);
        int greenToken = player.getShipBoard().getTotalNumberOfaToken(Token.GREEN_GOODS);
        toAdd = toAdd + redToken*4 + blueToken + yellowToken*3 + greenToken*2;
        player.getShipBoard().removeAllTokens();


    }

//    public Player minimumExposedComponents(){
//        for(Pair<Integer, Player> pair : leaderBoard){
//            Player p = pair.getValue();
//            int temp=p.getShipBoard().getNumberOfExposedConnectors();
////            Math.min(temp,)
//        }
//    }


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
