package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.client.model.enums.GameMode;

public interface UserInterface {
    public void updateStateTransition(int stateIndex);
    public void printUpdateMessage(String msg);

    public void setClientId(int myId);
    public void updateGameSessionNewPlayer(int playerId);
    public void updateGameSessionCreateGame(int playerId,GameMode gameMode, int maxPlayers);
    public void updateGameNewPlayer(int playerId);


}
