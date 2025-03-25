package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.model.enums.FlightMode;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.model.shipboard.Tile;
import javafx.util.Pair;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameBoard {

    //Da cambiare (è una merda)
    private final ArrayList<Pair<Integer, Player>> leaderBoard;

    private AdventureCardDeck mainAdventureDeck;
    private final TileCardDeck tilesDeck;
    private final ConcurrentHashMap<Integer, AdventureCardDeck> mapSmallAdventureDecks;
    private AdventureCard currentCard;
    private Player currentPlayer;
    private FlightMode flightMode;

    public GameBoard() {
        leaderBoard = new ArrayList<>();
        mapSmallAdventureDecks = new ConcurrentHashMap<>();
        tilesDeck = new TileCardDeck();
        mainAdventureDeck = new AdventureCardDeck();
        flightMode = FlightMode.DEFAULT;
        currentPlayer = null; //TODO (????) va capito come inizializzarlo
        currentCard = null;
    }
    public boolean isTestFlight(){return flightMode == FlightMode.TEST;}
    //Bisogna modificare le possibili carte del mazzo e la lunghezza del percorso, in base a flightMode e gameMode
    //Andrà anche a inizializzare la Shipboard della dimensione giusta in base alla GameMode, ( serve la lista di player)
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
    public void mergeDecks(){
        AdventureCardDeck bufferDeck = new AdventureCardDeck();
        mapSmallAdventureDecks.forEach((index,smallDeck) -> smallDeck.stream().forEach(bufferDeck::add));
        mapSmallAdventureDecks.clear();
        setMainAdventureDeck(bufferDeck);
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
        currentCard= mainAdventureDeck.draw();
    }
    public void addPlayerToLeaderboard(Integer days , Player player) {
        leaderBoard.add(new Pair<>(days, player));
    }
    public void addDiscardedTile(Player player) {
        Optional<Tile> newTile = player.discardTile();
        newTile.ifPresent(tilesDeck::addDiscardTile);
    }
    public Tile drawTile(int index) {
        return tilesDeck.draw(index);
    }
    public Player getLeader() {
        return leaderBoard.getFirst().getValue();
    }
    public void calculateFinalCredits(Player player) {
        int toAdd = 0;
        toAdd = 5 - leaderBoard.indexOf(player); //crediti per la posizione
        toAdd = toAdd - player.getLostComponents(); //crediti persi per i lost components
        toAdd = toAdd + player.scoreOfGoods(); //crediti per le merci
        if(minimumExposedComponents().contains(player)) {
            toAdd += 2; //crediti per i componentiesposti
        }
        player.addCredits(toAdd);
    }
    public void calculateFinalScore(Player player) {
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
    private Player getNextPlayer(Player player) {
        int currentIndex = -1;
        //cerco l'indice che combacia con il pair<Integer, player>
        for (int i = 0; i < leaderBoard.size(); i++) {
            if (leaderBoard.get(i).getValue().equals(player)) {
                currentIndex = i;
                break;
            }
        }
        if (currentIndex == -1) {
            throw new IllegalSetOfNextPlayerException("Player not found in leaderBoard");
        }
        //se l'indice è minore della lunghezza della leaderboard estraggo il player successivo
        if (currentIndex + 1 < leaderBoard.size()) {
            return leaderBoard.get(currentIndex + 1).getValue();
        }
        //il player di cui voglio il next è l'ultimo e perciò lancio l'eccezione
        throw new IllegalSetOfNextPlayerException("It's the last player");
    }
    public void sortLeaderBoard() {
        leaderBoard.sort((p1, p2) -> p2.getKey().compareTo(p1.getKey()));
    }
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    public void nextPlayer() {
        //setto il currentplayer al next player dato il current player
        setCurrentPlayer(getNextPlayer(currentPlayer));
    }
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    public AdventureCard getNextCard() {
        return mainAdventureDeck.draw();
    }
    public void updateLeaderboard() {
        //con i daysthisturn devo fare update facendo perde i giorni
    }
    public void setMainAdventureDeck(AdventureCardDeck mainAdventureDeck) {
        this.mainAdventureDeck = mainAdventureDeck;
    }
    public int userFinishShipboard(Player player){
        player.completeShipboard();
        player.getShipBoard().afterBuildCheckShipboard();
        addPlayerToLeaderboard(0, player);
        return leaderBoard.size();
    }
    public void userLookSmallAdventureCardDeck(Player player, int index){
        if(!mapSmallAdventureDecks.get(index).isWatched() && !player.isWatching()){
            mapSmallAdventureDecks.get(index).watch();
            player.lockLastTile();
            player.setSmallDeck(mapSmallAdventureDecks.get(index));
        }
        else
            throw new GameSettingsException("You cannot watch this deck");
    }
    public void userStopLookSmallAdventureCardDeck(Player player, int index){
        if(player.isWatching() && mapSmallAdventureDecks.get(index).equals(player.getSmallAdventureDeck())){
            player.clearSmallDeck();
            mapSmallAdventureDecks.get(index).stopWatch();
        }
        else
            throw new GameSettingsException("You are not watching this deck");
    }
}
