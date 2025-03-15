package it.polimi.ingsw.is25am49.model;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.states.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private GameMode mode;
    private final GameBoard board;
    private final ArrayList<Player> players;
    private int gameId;
    private Player winner;

    public Game(Player admin) {
        board = new GameBoard();
        players = new ArrayList<>();
        mode = GameMode.LEVEL_1;
        gameId = 0;
        players.add(admin);
        winner = null;
    }
    public GameBoard getGameboard() {
        return board;
    }
    public void setGameMode(GameMode mode) { this.mode = mode; }
    public void addPlayer(Player player) { players.add(player); }
    public ArrayList<Player> getListPlayers() { return players; }
    public void calculateScore() {
        players.forEach(Player::calculateFinalScore);
    }
    public void configurateFlight(){
        board.configurateFlight(mode);
    }
    private void setWinner(Player winner) {}
}
