package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.FlightMode;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.model.states.InitState;
import it.polimi.ingsw.is25am49.model.states.State;

import java.awt.*;
import java.util.Optional;

public class GameSession {
    private Game game;
    private Player admin;
    private State currentState;
    GameSession() {
        game = null;
        admin = null;
        currentState = new InitState(this);
    }
    public Game getGame() { return game; }
    public void setState(State nextState){
        currentState = nextState;
    }
    public void createNewGame(){
        game = new Game(admin);
    }
    public Player getAdmin(){
        return admin;
    }
    public void setGameMode(GameMode mode) {
        game.setGameMode(mode);
    }
    public void userSelectColorAndUserName(Color playerColor, String username) {
        Player tempPlayer = new Player(username, playerColor, game.getListPlayers().size());
        if(game.getListPlayers().size() == 0){
            admin = tempPlayer;
        }
        game.addPlayer(tempPlayer);
    }
    public boolean checkPlayers(){ return game.checkPlayers(); }
    public boolean checkStatus(){
        if(game!= null)
            return game.checkStatus();
        else
            return false;
    }
    public State getState() {
        return currentState;
    }

    public void userPlaceTile(Player player, Point coordinates) {
    }

    public void userRotateTile(Player player, Point coordinates) {

    }
    public void userDiscardTile(Player player){
    }

    public void userDrawTile(Player player, int idTile ){
        player.setHandTile(game.getGameboard().drawTile(idTile));
    }

    public void userFinishShipboard(Player player){

    }

    public void userLookSmallAdventureCardDeck(Player player, int index){

    }

    public void userStopLookSmallAdventureCardDeck(Player player, int index){
    }

    public void userPlaceTimer(){

    }

    public void userThrowDices(Player player){
    }

    public void userDrawAdventureCard(Player player){
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
    public void adminSelectTypeFlight(FlightMode flightMode){
        game.getGameboard().setFlightMode(flightMode);
    }
    public void calculateScore(Player player) { game.calculateScore(player);}
    public void configurateFlight(){
        game.configurateFlight();
    }
    public void finishBuild(Player player){
        player.completeShipboard();
        player.getShipBoard().afterBuildCheckShipboard();
    }
    public void userEndGame() throws GameSettingsException{
        if(game.gameIsFinished()){
            game = null;
            setState(new InitState(this));
        }
        else
            throw new GameSettingsException("The game is not finished yet");
    }
    public Optional<Player> userShowWinner() { return game.userShowWinner(); }
}
