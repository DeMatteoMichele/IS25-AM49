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
        currentState = new InitState(this);
        this.mode = mode;
    }

    public GameBoard getGameboard() {
        return board;
    }

    public State getState() {
        return currentState;
    }

    public void adminSelectGamemode(GameMode mode){
        currentState.admin_select_gamemode(mode);
    }

    public void userSelectColor(Player player, Color color){
        currentState.user_select_color(player,color);
    }

    public void userInsertName(Player player, String playerName) {
        currentState.user_insert_name(player,playerName);
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
        currentState.user_place_tile(player,coordinates);
    }

    public void userRotateTile(Player player, Point coordinates) {
        currentState.user_rotate_tile(player,coordinates);
    }

    public void userPickTile(Player player, Point coordinates) {
        currentState.user_pick_tile(player,coordinates);
    }

    public void userDiscardTile(Player player){
        currentState.user_discard_tile(player);
    }

    public void userDrawTile(Player player, int idTile ){
        currentState.user_draw_tile(player,idTile);
    }

    public void userFinishShipboard(Player player){
        currentState.user_finish_shipboard(player);
    }

    public void userLookSmallAdventureCardDeck(Player player, int index){
        currentState.user_look_small_adventure_card_deck(player,index);
    }

    public void userStopLookSmallAdventureCardDeck(Player player, int index){
        currentState.user_stop_look_small_adventure_card_deck(player, index);
    }

    public void userPlaceTimer(Player player){
        currentState.user_place_timer(player);
    }

    public void userThrowDices(Player player){
        currentState.user_throw_dices(player);
    }

    public void userDrawAdventureCard(Player player){
        currentState.user_draw_adventure_card(player);
    }

    public void userPlayAdventureCard(Player player){
        currentState.user_play_adventure_card(player);
    }

    public void userEffectDecision(Player player){
        currentState.user_effect_decision(player);
    }

    public void userActivateComponent(Player player, Point coordinates) {
        currentState.user_activate_component(player,coordinates);
    }

    public void userActivateDaysUpdate(){
        currentState.user_activate_days_update();
    }

    public void userQuitGame(Player player){
        currentState.user_quit_game(player);
    }
    public void setState(State currentState){
        this.currentState = currentState;
    }

}
