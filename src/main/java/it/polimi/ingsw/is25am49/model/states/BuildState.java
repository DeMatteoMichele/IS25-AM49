package it.polimi.ingsw.is25am49.model.states;

import it.polimi.ingsw.is25am49.model.Game;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import java.awt.*;

public class BuildState extends State {
    BuildState(Game game){
        super(game);
    }
    public void admin_select_gamemode(GameMode gamemode) {}
    public void user_select_color(Player player, Color playerColor) {}
    public void user_insert_name(Player player,String playerName) {}

    // public void admin_create_lobby(Player) {}
    // public void user_join_lobby() {}
    // public void admin_start_game() {}

    public void user_place_tile(Player player, Point coordinates) {}
    public void user_rotate_tile(Player player, Point coordinates) {}
    public void user_pick_tile(Player player, Point coordinates) {}
    public void user_discard_tile(Player player) {}
    public void user_draw_tile(Player player, int idTIle) {}
    public void user_finish_shipboard(Player player) {}
    public void user_look_small_adventure_card_deck(Player player, int smallDeckIndex) {}
    public void user_stop_look_small_adventure_card_deck(Player player,int smallDeckIndex) {}
    public void user_place_timer(Player player) {}
    public void user_throw_dices(Player player) {}


    public void user_draw_adventure_card(Player player) {}
    public void user_play_adventure_card(Player player) {}
    public void user_effect_decision(Player player) {}
    public void user_activate_component(Player player, Point coordinates) {}
    public void user_activate_days_update() {}
    public void user_quit_game(Player player) {}
}
