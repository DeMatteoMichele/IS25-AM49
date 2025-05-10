package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.client.model.enums.GameMode;

public interface UserInterface {
    public void updateGameSessionNewPlayer(int playerId);
    public void updateGameSessionSettings(GameMode gameMode, int maxPlayers);
}
