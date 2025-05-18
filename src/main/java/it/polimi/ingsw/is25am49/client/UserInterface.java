package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;

public interface UserInterface {
    void setClientId(int myId);
    void updateGameSessionNewPlayer(int playerId);
    void updateGameSessionCreateGame(int playerId,GameMode gameMode, int maxPlayers);
    void updateGameNewPlayer(int playerId);
    void printUpdateMessage(String msg);
    void setGamesession(GameSession gameSession);
}
