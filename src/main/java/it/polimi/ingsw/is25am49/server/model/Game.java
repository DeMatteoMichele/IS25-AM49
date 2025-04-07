package it.polimi.ingsw.is25am49.server.model;
import it.polimi.ingsw.is25am49.server.model.enums.FlightMode;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;

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

    public Game(Optional<Player> admin, GameSession session) {
        this.session = session;
        board = new GameBoard();
        players = new ArrayList<>();
        mode = GameMode.DEFAULT;
        players.add(admin.orElse(null));
        winner = Optional.empty();
        timer = null;
        maxPlayers = 4;
        isFinished = false;
        numPlayers = 0;
    }
    public boolean checkStatus(){
        if(!mode.equals(GameMode.DEFAULT))
            return board.checkStatus();
        else
            return false;
    }
    public GameBoard getGameboard() {
        return board;
    }
    public void setGameMode(GameMode mode) {
        this.mode = mode;
        timer = new Timer(mode, session);
    }
    public boolean checkPlayers(){ return players.size() < maxPlayers; }
    public void addPlayer(Player player) { players.add(player); }
    public ArrayList<Player> getListPlayers() { return players; }
    //TODO: da fare
    public void calculateScore(Player player) {
        if(winner.isEmpty()){
                //board.calculateFinalScore();
                winner = players.stream().max((p1, p2) -> Integer.compare(p1.getScore(), p2.getScore()));
        }
        else
            throw new GameSettingsException("Final score already calculated");
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
    public void userPlaceTimer(Player player){
        if(!board.isTestFlight())
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
        player.setHandTile(board.drawTile(idTile));
    }
    public boolean userFinishShipboard(Player player){ return board.userFinishShipboard(player) == numPlayers;}
    public void userLookSmallAdventureCardDeck(Player player, int index){ board.userLookSmallAdventureCardDeck(player, index);}
    public void adminSelectTypeFlight(FlightMode flightMode){
        board.setFlightMode(flightMode);
    }
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
