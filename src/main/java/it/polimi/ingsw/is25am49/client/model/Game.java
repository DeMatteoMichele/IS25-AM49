package it.polimi.ingsw.is25am49.client.model;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.exceptions.GameSettingsException;

import java.util.ArrayList;
import java.util.Optional;

public class Game {
    private GameSession session;
    private GameMode mode;
    private final GameBoard board;
    private final ArrayList<Player> players;
    private Optional<Player> winner;
    private Timer timer;
    private int maxPlayers;
    private int numPlayers;
    private boolean isFinished;
    private final long seedForGeneration;

    public Game(Player admin, GameSession session, GameMode mode, int maxPlayers) {
        this.session = session;
        seedForGeneration= System.currentTimeMillis();
        board = new GameBoard(seedForGeneration);
        board.setSeedForGeneration(seedForGeneration);
        players = new ArrayList<>();
        this.mode = mode;
        winner = Optional.empty();
        timer = null;
        isFinished = false;

        players.add(admin);
        numPlayers = 1;
        this.maxPlayers = maxPlayers;
    }
    public GameMode getMode() {return mode;}
    public Player getPlayer(int id) throws IllegalArgumentException{
        for (Player p : players) {
            if(p.getIdPlayer() == id) return p;
        }
        throw new IllegalArgumentException("Player not found");
    }
    public GameBoard getGameboard() {return board;}
    public int getMaxPlayers() {return maxPlayers;}
    public int numOfPlayers() {return numPlayers;}

    public void setGameMode(GameMode mode) {
        this.mode = mode;
        timer = new Timer(mode, session);
    }
    public boolean checkPlayers(){ return players.size() < maxPlayers; }
    public void addPlayer(Player player) {
        players.add(player);
        numPlayers++;
    }
    public ArrayList<Player> getListPlayers() { return players; }
    public void calculateScore(Player player) {
        if(winner.isEmpty()){
                board.calculateFinalCredits();
                winner = players.stream().max((p1, p2) -> Integer.compare(p1.getCredits(), p2.getCredits()));
        }
        else throw new GameSettingsException("Final score already calculated");
    }
    public void configurateFlight(){
        board.configurateFlight(mode, players);
        board.initialize(mode);
    }
    public boolean gameIsFinished(){
        return isFinished;
    }
    public Optional<Player> userShowWinner() {
            isFinished = true;
            return winner;
    }
    public boolean isTestFlight() {
        return mode == GameMode.FLIGHT_MODE;
    }
    public void userPlaceTimer(Player player){
        if(!isTestFlight())
            timer.place(player);
        else
            throw new GameSettingsException("This is a Test Flight");
    }
    public void discardTile(Player player){
        board.addDiscardedTile(player);
    }
    public void userDrawTile(Player player, int idTile){
        if(player.getHandTile().isPresent()){
            board.addDiscardedTile(player);
        }
        player.lockLastTile();
        player.setHandTile(board.drawTile(idTile, player));
    }
    public boolean userFinishShipboard(Player player){ return board.userFinishShipboard(player) == numPlayers;}
    public void userLookSmallAdventureCardDeck(Player player, int index){ board.userLookSmallAdventureCardDeck(player, index);}
    public void userStopLookSmallAdventureCardDeck(Player player, Integer index){ board.userStopLookSmallAdventureCardDeck(player, index);}
    public void addPlayersToLeaderBoard(){
        players.forEach(Player::completeShipboard);
        players.forEach(board::addPlayerToLeaderboard);
    }
    public void mergeSmallDecks(){
        board.mergeDecks();
    }
    public void userDrawAdventureCard(Player player){
        board.drawAdventureCard(player);
    }
    public void userThrowDices(Player player){
        board.userThrowDices(player);
    }
}
