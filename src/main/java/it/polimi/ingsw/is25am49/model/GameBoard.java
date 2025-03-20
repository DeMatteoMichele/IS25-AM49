package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.model.enums.FlightMode;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.model.shipboard.Tile;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class GameBoard {
    private final List<Pair<Integer, Player>> leaderBoard;
    private AdventureCardDeck mainAdvenureDeck;
    private final HashMap<Integer, AdventureCardDeck> mapSmallAdventureDecks;
    private final TileCardDeck tilesDeck;
    private AdventureCard currentCard;
    private int currentPlayer;
    private FlightMode flightMode;
    public GameBoard() {
        leaderBoard = new ArrayList<>();
        mapSmallAdventureDecks = new HashMap<>();
        tilesDeck = new TileCardDeck();
        mainAdvenureDeck = new AdventureCardDeck();
        flightMode = FlightMode.DEFAULT;
        currentPlayer = 0;
        currentCard = null;
    }
    //Bisogna modificare le possibili carte del mazzo, la lunghezza del percorso e l'utilizzo del tempo in base a flightMode e gameMode
    //Andrà anche a inizializzare la Shipboard delle dimensione giusta in base alla GameMode, (per questo gli ho passato Game, serva la lista di player)
    public void configurateFlight(GameMode gameMode, ArrayList<Player> players) {

    }
    public boolean checkStatus(){
        return !flightMode.equals(FlightMode.DEFAULT);
    }
    public void setFlightMode(FlightMode flightMode){
        this.flightMode = flightMode;
    }
    public AdventureCardDeck getSmallAdventureDeck(Integer index) {
        return mapSmallAdventureDecks.get(index);
    }
    public void mergeDecks(){}
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

    public Tile drawTile(int index) {
        return tilesDeck.draw(index);
    }

    public Player getLeader() {
        return leaderBoard.get(0).getValue();
    }

    public void calculateFinalCredits(Player player) {
        int toAdd = 0;
        toAdd = 5 - leaderBoard.indexOf(player); //crediti per la posizione
        toAdd = toAdd - player.getLostComponents(); //crediti persi per i lost components
        toAdd = toAdd + player.scoreOfGoods(); //crediti per le merci
        if(minimumExposedComponents().contains(player)) {
            toAdd += 2; //crediti per i componentiesposti
        }
        player.addCredit(toAdd);
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

    public List<Player> minimumExposedComponents() {

        int minValue = leaderBoard.
                stream()
                .map(Pair :: getValue)//ritorno il player
                //ritorno solo il numero di connettori esposti dato il player
                .mapToInt(p-> p.getShipBoard().getNumberOfExposedConnectors())
                .min()
                .orElse(0);

        return leaderBoard
                .stream()
                .map(Pair:: getValue) //ritorna il player
                //cerco se il player ha il minimo valore di conncetors, se si lo filtro
                .filter(p-> p.getShipBoard().getNumberOfExposedConnectors()==minValue)
                .collect(Collectors.toList());

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

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public AdventureCard getNextCard() {
        return mainAdvenureDeck.draw();
    }

    public void updateLeaderboard() {
        //con i daysthisturn devo fare update facendo perde i giorni
    }
}
