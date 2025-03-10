package it.polimi.ingsw.is25am49.model;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.states.*;

import java.awt.*;
import java.util.List;

public class Game {
    private GameMode mode;
    private GameBoard board;
    private final List<Player> players;
    private int gameId;
    private State currentState;




    public Game(List<Player> players) {
        this.players = players;
        currentState = new InitState(this);
    }

    public GameBoard get_gameboard() {
        return board;
    }

    public State get_state() {
        return currentState;
    }

    public void admin_select_gamemode(GameMode mode){
        currentState.admin_select_gamemode(mode);
    }

    public void user_select_color(Player player, Color color){
        currentState.user_select_color(player,color);
    }

    public void user_insert_name(Player player, String playerName) {
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
    public void user_place_tile(Player player, Point coordinates) {
        currentState.user_place_tile(player,coordinates);
    }

    public void user_rotate_tile(Player player, Point coordinates) {
        currentState.user_rotate_tile(player,coordinates);
    }

    public void user_pick_tile(Player player, Point coordinates) {
        currentState.user_pick_tile(player,coordinates);
    }

    public void user_discard_tile(Player player){
        currentState.user_discard_tile(player);
    }

    public void user_draw_tile(Player player,int idTile ){
        currentState.user_draw_tile(player,idTile);
    }

    public void user_finish_shipboard(Player player){
        currentState.user_finish_shipboard(player);
    }

    public void user_look_small_adventure_card_deck(Player player,int index){
        currentState.user_look_small_adventure_card_deck(player,index);
    }

    public void user_stop_look_small_adventure_card_deck(Player player, int index){
        currentState.user_stop_look_small_adventure_card_deck(player, index);
    }

    public void user_place_timer(Player player){
        currentState.user_place_timer(player);
    }

    public void user_throw_dices(Player player){
        currentState.user_throw_dices(player);
    }

    public void user_draw_adventure_card(Player player){
        currentState.user_draw_adventure_card(player);
    }

    public void user_play_adventure_card(Player player){
        currentState.user_play_adventure_card(player);
    }

    public void user_effect_decision(Player player){
        currentState.user_effect_decision(player);
    }

    public void user_activate_component(Player player, Point coordinates) {
        currentState.user_activate_component(player,coordinates);
    }

    public void user_activate_days_update(){
        currentState.user_activate_days_update();
    }

    public void user_quit_game(Player player){
        currentState.user_quit_game(player);
    }
    public void set_state(State currentState){
        this.currentState = currentState;
    }

}
