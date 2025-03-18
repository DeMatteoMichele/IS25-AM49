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
    private Optional<Game> game;
    private Optional<Player> admin;
    private State currentState;
    GameSession() {
        game = Optional.empty();
        admin = Optional.empty();
        currentState = new InitState(this);
    }
    public Optional<Game> getGame() { return game; }
    public void setState(State nextState){
        currentState = nextState;
    }
    public void createNewGame(){
        game = Optional.of(new Game(admin));
    }
    public Optional<Player> getAdmin(){
        return admin;
    }
    public void setGameMode(GameMode mode) {
        game.ifPresent(g->g.setGameMode(mode));
    }
    public void userSelectColorAndUserName(Color playerColor, String username) {
        Player tempPlayer = new Player(username, playerColor, game.map(g -> g.getListPlayers().size()).orElse(0));
        if(game.map(Game::getListPlayers).isEmpty()){
            admin = Optional.of(tempPlayer);
        }
        game.ifPresent(g -> g.addPlayer(tempPlayer));
    }
    public boolean checkPlayers(){ return game.map(Game::checkPlayers).orElse(false); }
    public boolean checkStatus(){
        if(game.isPresent())
            return game.map(Game::checkStatus).orElse(false);
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
        if(game.isPresent())
            game.get().getGameboard().setFlightMode(flightMode);
        else
            throw new GameSettingsException("The game has not been created");
    }
    public void calculateScore(Player player) { game.ifPresent(g -> g.calculateScore(player));}
    public void configurateFlight(){
        game.ifPresent(Game::configurateFlight);
    }
    public void finishBuild(Player player){
        player.completeShipboard();
        player.getShipBoard().afterBuildCheckShipboard();
    }
    public void userEndGame() throws GameSettingsException{
        if(game.map(Game::gameIsFinished).orElse(false)){
            game = Optional.empty();
            setState(new InitState(this));
        }
        else
            throw new GameSettingsException("The game is not finished yet");
    }
    public Optional<Player> userShowWinner() { return game.flatMap(Game::userShowWinner); }
}
