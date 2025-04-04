package it.polimi.ingsw.is25am49.server.model;

import it.polimi.ingsw.is25am49.server.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.server.model.enums.FlightMode;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalSetOfNextPlayerException;
import it.polimi.ingsw.is25am49.server.model.exceptions.OutOfDeckExceptions;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;

import java.awt.*;
import java.util.*;
import java.util.List;
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

    public GameBoard() {
        mapSmallAdventureDecks = new ConcurrentHashMap<>();
        tilesDeck = new TileCardDeck();
        mainAdventureDeck = new AdventureCardDeck();
        flightMode = FlightMode.DEFAULT;
        isFirstCard = true;
    }
    private void isYourTurn(Player player) {
        if (!player.equals(currentPlayer))
            throw new IllegalMethodOnThisStateException("It's not your turn");
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

    public void drawAdventureCard(Player player) {
        if(player.equals(leaderBoard.getLeader())) {
            if (isFirstCard || currentCard.getIsFinished()) {
                currentCard = mainAdventureDeck.getFirstCard();
                mainAdventureDeck.draw();
                isFirstCard = false;
            } else {
                throw new IllegalMethodOnThisStateException("You can't draw adventure card until it is finished.");
            }
        }
        else
            throw new GameSettingsException("You are not the leader of this turn");
    }
    public void userThrowDices(Player player){
        isYourTurn(player);
        currentCard.rollDices();
    }
    public void userPlayAdventureCard(Player player){}
    public void userFinishDecision(Player player){}
    public void userActivateComponent(Player player, Point coordinates){}
    public void userActivateDaysUpdate(){}
    public void userLandOnPlanet(Player player) {
    }
    public void userLandOnShip(Player player) {
    }
    public void userQuitGame(Player player){}
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
        toAdd = 5 - leaderBoard.getPos(player);//crediti per la posizione
        toAdd = toAdd - player.getLostComponents(); //crediti persi per i lost components
        toAdd = toAdd + player.scoreOfGoods(); //crediti per le merci
        if (minimumExposedComponents().contains(player)) {
            toAdd += 2; //crediti per i componenti esposti
        }
        player.addCredits(toAdd);
    }
//    public void calculateFinalScore(Player player) {
//            int toAdd = 0;
//            toAdd = 5 - leaderBoard.getPos(player);
//            toAdd = toAdd - player.getLostComponents();
//
//        int redToken = player.getShipBoard().getTotalNumberOfaToken(Token.RED_GOODS);
//        int blueToken = player.getShipBoard().getTotalNumberOfaToken(Token.BLUE_GOODS);
//        int yellowToken = player.getShipBoard().getTotalNumberOfaToken(Token.YELLOW_GOODS);
//        int greenToken = player.getShipBoard().getTotalNumberOfaToken(Token.GREEN_GOODS);
//        toAdd = toAdd + redToken * 4 + blueToken + yellowToken * 3 + greenToken * 2;
//        player.getShipBoard().removeAllTokens();
//    }
    public void setFlightModeToDefault() {
        flightMode = FlightMode.DEFAULT;
    }
    //TODO VA TESTATO
    private List<Player> minimumExposedComponents() {
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
        //faccio modify flight days con i daysthisturn del player partendo AL CONTRARIO
        leaderBoard.stream()
                .map(Map.Entry::getKey)
                .sorted(Comparator.comparing(player -> -leaderBoard.getPos(player))) // Ordine decrescente per posizione
                .forEach(player -> {
                    leaderBoard.modifyFlightDays(player, player.getDaysThisTurn());
                });
        //alla fine setto tutti i days this turn a 0
        leaderBoard.stream()
                .map(Map.Entry::getKey)
                .forEach(player -> {
                    player.setDaysThisTurn(0);
                });
    }
    private void checkIfIsLapped(){
        //TODO devo controllare se qualche player è stato doppiato
        //usare getdays
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
        if(mapSmallAdventureDecks.containsKey(index)){
            if (!mapSmallAdventureDecks.get(index).isWatched() && !player.isWatching()) {
                mapSmallAdventureDecks.get(index).watch();
                player.lockLastTile();
                player.setSmallDeck(mapSmallAdventureDecks.get(index));
            }
            else
                throw new GameSettingsException("You cannot watch this deck");
        }
        else
            throw new OutOfDeckExceptions("This deck does not exist");
    }

    public void userStopLookSmallAdventureCardDeck(Player player, int index) {
        if(mapSmallAdventureDecks.containsKey(index)){
            if (player.isWatching() && mapSmallAdventureDecks.get(index).equals(player.getSmallAdventureDeck())) {
                player.clearSmallDeck();
                mapSmallAdventureDecks.get(index).stopWatch();
            }
            else
                throw new GameSettingsException("You are not watching this deck");
        }
        else
            throw new OutOfDeckExceptions("This deck does not exist");

    }
}
