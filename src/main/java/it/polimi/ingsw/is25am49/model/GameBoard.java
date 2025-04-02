package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.model.enums.FlightMode;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.model.shipboard.Tile;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameBoard {
    private LeaderBoard leaderBoard;

    private AdventureCardDeck mainAdventureDeck;
    private final TileCardDeck tilesDeck;
    private final ConcurrentHashMap<Integer, AdventureCardDeck> mapSmallAdventureDecks;
    private AdventureCard currentCard;
    private Player currentPlayer;
    private FlightMode flightMode;
    private boolean isFirstCard;
//TODO da mettere a optional
    public GameBoard() {
        mapSmallAdventureDecks = new ConcurrentHashMap<>();
        tilesDeck = new TileCardDeck();
        mainAdventureDeck = new AdventureCardDeck();
        flightMode = FlightMode.DEFAULT;
        isFirstCard = true;
    }

    public boolean isTestFlight() {
        return flightMode == FlightMode.TEST;
    }

    //TODO da fare
    //Bisogna modificare le possibili carte del mazzo e la lunghezza del percorso, in base a flightMode e gameMode
    //Andrà anche a inizializzare la Shipboard della dimensione giusta in base alla GameMode, ( serve la lista di player)
    public void configurateFlight(GameMode gameMode, ArrayList<Player> players) {
        int level;
        if(gameMode == GameMode.LEVEL_1)
            level = 1;
        else if(gameMode == GameMode.LEVEL_2)
            level = 2;
        else
            throw new GameSettingsException("Illegal gameMode");
        players.stream().forEach(player -> {player.setShipBoard(new ShipBoard(level, player.getPlayerNumber()));});
        setLeaderBoard(gameMode);
    }
    public void initialize(GameMode gameMode) {
        tilesDeck.generateDeck();
        tilesDeck.shuffle();
        mainAdventureDeck.generateDeck(gameMode);
        mainAdventureDeck.shuffle();
        generateSmallDecks(gameMode);

    }
    public void generateSmallDecks(GameMode gameMode){

    }
    public void setLeaderBoard(GameMode gameMode) {
        leaderBoard = new LeaderBoard(gameMode);
    }

    public boolean checkStatus() {
        return !flightMode.equals(FlightMode.DEFAULT);
    }

    public void setFlightMode(FlightMode flightMode) {
        this.flightMode = flightMode;
    }

    public AdventureCardDeck getSmallAdventureDeck(Integer index) {
        return mapSmallAdventureDecks.get(index);
    }

    public void mergeDecks() {
        AdventureCardDeck bufferDeck = new AdventureCardDeck();
        mapSmallAdventureDecks.forEach((index, smallDeck) -> smallDeck.stream().forEach(bufferDeck::add));
        mapSmallAdventureDecks.clear();
        setMainAdventureDeck(bufferDeck);
    }

    public LeaderBoard getLeaderBoard() {
        return leaderBoard;
    }

    public void setCurrentCard(AdventureCard card) {
        currentCard = card;
    }

    public AdventureCard getCurrentCard() {
        return currentCard;
    }
    //TODO
    public void drawAdventureCard() {

        if(isFirstCard || currentCard.getIsFinished()) {
            currentCard = mainAdventureDeck.getFirstCard();
            mainAdventureDeck.draw();
            isFirstCard = false;
        }
        else{
            throw new IllegalMethodOnThisStateException("You can't draw adventure card until it is finished.");
        }


    }

    public void addPlayerToLeaderboard(Player player) {
        if(leaderBoard.isEmpty()){
            leaderBoard.add(player);
            currentPlayer = player;
        }
        else
            leaderBoard.add(player);
    }



    public void addDiscardedTile(Player player) {
        Optional<Tile> newTile = player.discardTile();
        newTile.ifPresent(tilesDeck::addDiscardTile);
    }

    public Tile drawTile(int index) {
        return tilesDeck.draw(index);
    }

    public Player getLeader() {
        return leaderBoard.getLeader();
    }

    public void calculateFinalCredits(Player player) {
        int toAdd = 0;
        toAdd = 5 - leaderBoard.getPos(player); //crediti per la posizione
        toAdd = toAdd - player.getLostComponents(); //crediti persi per i lost components
        toAdd = toAdd + player.scoreOfGoods(); //crediti per le merci
        if (minimumExposedComponents().contains(player)) {
            toAdd += 2; //crediti per i componentiesposti
        }
        player.addCredits(toAdd);
    }

    public void calculateFinalScore(Player player) {
        int toAdd = 0;
        toAdd = 5 - leaderBoard.getPos(player);
        toAdd = toAdd - player.getLostComponents();

        int redToken = player.getShipBoard().getTotalNumberOfaToken(Token.RED_GOODS);
        int blueToken = player.getShipBoard().getTotalNumberOfaToken(Token.BLUE_GOODS);
        int yellowToken = player.getShipBoard().getTotalNumberOfaToken(Token.YELLOW_GOODS);
        int greenToken = player.getShipBoard().getTotalNumberOfaToken(Token.GREEN_GOODS);
        toAdd = toAdd + redToken * 4 + blueToken + yellowToken * 3 + greenToken * 2;
        player.getShipBoard().removeAllTokens();
    }

    public List<Player> minimumExposedComponents() {
        int minValue = leaderBoard.
                stream()
                .map(Map.Entry::getKey)//ritorno il player
                //ritorno solo il numero di connettori esposti dato il player
                .mapToInt(p -> p.getShipBoard().getNumberOfExposedConnectors())
                .min()
                .orElse(0);

        return leaderBoard
                .stream()
                .map(Map.Entry::getKey) //ritorna il player
                //cerco se il player ha il minimo valore di conncetors, se si lo filtro
                .filter(p -> p.getShipBoard().getNumberOfExposedConnectors() == minValue)
                .collect(Collectors.toList());

    }

    private Player getNextPlayer() {
        int currentPos = leaderBoard.getPos(currentPlayer);
        if (currentPos == leaderBoard.size())
            throw new IllegalSetOfNextPlayerException("This is the last player");
        else
            return leaderBoard.getPlayerInPosition(currentPos + 1);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void nextPlayer() {
        //setto il currentplayer al next player dato il current player
        setCurrentPlayer(getNextPlayer());
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }



    public void updateLeaderboard() {
        //con i daysthisturn devo fare update facendo perde i giorni
    }

    public void setMainAdventureDeck(AdventureCardDeck mainAdventureDeck) {
        this.mainAdventureDeck = mainAdventureDeck;
    }

    public int userFinishShipboard(Player player) {
        player.completeShipboard();
        addPlayerToLeaderboard(player);
        return leaderBoard.size();
    }

    public void userLookSmallAdventureCardDeck(Player player, int index) {
        if (!mapSmallAdventureDecks.get(index).isWatched() && !player.isWatching()) {
            mapSmallAdventureDecks.get(index).watch();
            player.lockLastTile();
            player.setSmallDeck(mapSmallAdventureDecks.get(index));
        } else
            throw new GameSettingsException("You cannot watch this deck");
    }

    public void userStopLookSmallAdventureCardDeck(Player player, int index) {
        if (player.isWatching() && mapSmallAdventureDecks.get(index).equals(player.getSmallAdventureDeck())) {
            player.clearSmallDeck();
            mapSmallAdventureDecks.get(index).stopWatch();
        } else
            throw new GameSettingsException("You are not watching this deck");
    }
}
