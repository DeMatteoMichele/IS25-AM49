package it.polimi.ingsw.is25am49.client.model;

import it.polimi.ingsw.is25am49.client.UserInterface;
import it.polimi.ingsw.is25am49.client.model.enums.Color;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.client.model.states.InitState;
import it.polimi.ingsw.is25am49.client.model.states.State;
import it.polimi.ingsw.is25am49.server.RemoteClient;

import java.awt.*;
import java.util.Optional;

public class GameSession {
    private final UserInterface ui;
    private Game game;
    private Optional<Player> admin;
    private State currentState;
    public GameSession(UserInterface ui) {
        this.ui=ui;
        admin = Optional.empty();
        currentState = new InitState(this,ui);
    }

    public Game getGame() { return game; }
    private void checkIsAdmin(Player player){
        if(admin.isEmpty())
            throw new GameSettingsException("There is no admin");
        if(!admin.get().equals(player))
            throw new GameSettingsException("You are not the admin of this game");
    }
    public void setState(State nextState){
        currentState = nextState;
    }
    public void createNewGame(Player player){
        checkIsAdmin(player);
        game = new Game(admin.get(), this);
    }
    public Optional<Player> getAdmin(){
        return admin;
    }
    public void setGameMode(Player player, GameMode mode) {
        checkIsAdmin(player);
        game.setGameMode(mode);
    }
    public void userSelectColorAndUserName(Color playerColor, String username) {
        if(admin.isEmpty()){
            Player tempPlayer = new Player(username, playerColor, 1);
            admin = Optional.of(tempPlayer);
            game=new Game(admin.get(), this);
        }else {
            Player tempPlayer = new Player(username, playerColor, game.getListPlayers().size()+1);
            game.addPlayer(tempPlayer);
        }
    }
    public boolean checkPlayers(){ return game.checkPlayers(); }
    public boolean checkStatus(){return game.checkStatus();}
    public State getState() {
        return currentState;
    }
    public void userDiscardTile(Player player){game.discardTile(player);}
    public void userDrawTile(Player player, int idTile ){game.userDrawTile(player, idTile);}

    //se è l'ultimo player si passa ad AdventureState
    public boolean userFinishShipboard(Player player){return game.userFinishShipboard(player);}
    public void userLookSmallAdventureCardDeck(Player player, int index){
        game.userLookSmallAdventureCardDeck(player, index);
    }
    public void userStopLookSmallAdventureCardDeck(Player player, int index){
        game.userStopLookSmallAdventureCardDeck(player, index);
    }

    public void userPlaceTimer(Player player){
        game.userPlaceTimer(player);
    }

    public void userThrowDices(Player player){
        game.userThrowDices(player);
    }

    public void userDrawAdventureCard(Player player){
        game.userDrawAdventureCard(player);
    }

    public void userPlayAdventureCard(Player player){
    }

    public void userEffectDecision(Player player){
    }

    public void userActivateComponent(Player player, Point coordinates) {
    }

    public void userActivateDaysUpdate(){
    }

    public void userQuitGame(Player player){
    }
    public void calculateScore(Player player) {
        checkIsAdmin(player);
        game.calculateScore(player);
    }
    public void configurateFlight(){
        game.configurateFlight();
    }

    public void userEndGame(Player player){
        if(game.gameIsFinished()){
            checkIsAdmin(player);
            setState(new InitState(this));
        }
        else
            throw new GameSettingsException("The game is not finished yet");
    }
    public Optional<Player> userShowWinner(Player player) {
        checkIsAdmin(player);
        return game.userShowWinner();
    }
    public void addPlayersToLeaderBoard(){ game.addPlayersToLeaderBoard() ;}
    public void mergeSmallDecks(){
        game.mergeSmallDecks();
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append("+----------------------------------------------\n");
        str.append("| LOBBY : Players - ").append(game.getListPlayers().size());
        str.append("/4 \tMode: ").append(game.getMode()).append("\n");
        for(Player player : game.getListPlayers()){
            str.append("| ").append(player.getIdPlayer()).append(")\t").append(player.getUsername()).append("\n");
        }
        str.append("+----------------------------------------------");
        return str.toString();
    }
}