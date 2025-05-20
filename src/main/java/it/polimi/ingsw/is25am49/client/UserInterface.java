package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.enums.Token;

import java.awt.*;

public interface UserInterface {
    public void updateStateTransition(int stateIndex);
    public void printUpdateMessage(String msg);

    //INIT STATE:
    public void setClientId(int myId);
    public void updateGameSessionNewPlayer(int playerId);
    public void updateGameSessionCreateGame(int playerId,GameMode gameMode, int maxPlayers);
    public void updateGameNewPlayer(int playerId);

    //BUILD STATE:
    public void updatePlaceTile(int playerId, Point coordinates);
    public void updatePickTile(int playerId, Point coordinates);
    public void updateRotateTileClockWise(int playerId);
    public void updateDiscardTile(int playerId);
    public void updateDrawTile(int playerId, int idTIle);
    public void updateFinishShipboard(int playerId);
    public void updateLookSmallAdventureCardDeck(int playerId, int smallDeckIndex);
    public void updateStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex);
    public void updatePlaceTimer(int playerId);
    public void updateTimerIsDone();
    public void updateSelectTypeOfToken(int playerId, Token token, Point coordinates);
    public void lookAtShipboard(int playerId);


    public void setGameSession(GameSession gameSession);//TODO sus non penso serva


}
