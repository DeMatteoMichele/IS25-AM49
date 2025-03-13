package it.polimi.ingsw.is25am49.model;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.states.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private GameMode mode;
    private GameBoard board;
    private final List<Player> players = new ArrayList<Player>();
    private int gameId;
    private State currentState;

    public Game(GameMode mode) {
        board = new GameBoard();
        currentState = new InitState(this);
        mode = GameMode.LEVEL_1;
    }



    public GameBoard getGameboard() {
        return board;
    }

    public State getState() {
        return currentState;
    }

    public void adminSelectGamemode(GameMode mode){
        currentState.adminSelectGameMode(mode);
    }

    public void userSelectColor(Player player, Color color){
        currentState.userSelectColor(player,color);
    }

    public void userInsertName(Player player, String playerName) {
        currentState.userInsertName(player,playerName);
    }

    public void addPlayer(Player player){
        players.add(player);
    }
    /*public void admin_create_lobby(Player player) {
        currentState.admin_create_lobby(player);
    }
    public void user_join_lobby(){
        currentState.user_join_lobby();
        }
    public void admin_start_game(GameMode mode) {
        currentState.admin_start_game
    }*/
    public void userPlaceTile(Player player, Point coordinates) {
        currentState.userPlaceTile(player,coordinates);
    }

    public void userRotateTile(Player player, Point coordinates) {
        currentState.userRotateTile(player,coordinates);
    }

    public void userPickTile(Player player, Point coordinates) {
        currentState.userPickTile(player,coordinates);
    }

    public void userDiscardTile(Player player){
        currentState.userDiscardTile(player);
    }

    public void userDrawTile(Player player, int idTile ){
        currentState.userDrawTile(player,idTile);
    }

    public void userFinishShipboard(Player player){
        currentState.userFinishShipboard(player);
    }

    public void userLookSmallAdventureCardDeck(Player player, int index){
        currentState.userLookSmallAdventureCardDeck(player,index);
    }

    public void userStopLookSmallAdventureCardDeck(Player player, int index){
        currentState.userStopLookSmallAdventureCardDeck(player, index);
    }

    public void userPlaceTimer(Player player){
        currentState.userPlaceTimer(player);
    }

    public void userThrowDices(Player player){
        currentState.userThrowDices(player);
    }

    public void userDrawAdventureCard(Player player){
        currentState.userDrawAdventureCard(player);
    }

    public void userPlayAdventureCard(Player player){
        currentState.userPlayAdventureCard(player);
    }

    public void userEffectDecision(Player player){
        currentState.userEffectDecision(player);
    }

    public void userActivateComponent(Player player, Point coordinates) {
        currentState.userActivateComponent(player,coordinates);
    }

    public void userActivateDaysUpdate(){
        currentState.userActivateDaysUpdate();
    }

    public void userQuitGame(Player player){
        currentState.userQuitGame(player);
    }
    public void setState(State currentState){
        this.currentState = currentState;
    }

}
