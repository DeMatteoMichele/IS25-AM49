package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

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

    //ADVENTURE STATE:
    public void updateGenericCardMessage(int playerId, String message);
    public void updateThrowDices(int playerId, int firstDice, int secondDice);
    public void updateDrawAdventureCard(int playerId, String cardId);
    public void updateFinishDecisionActivating(int playerId, Set<Point> coordinates);
    public void updateFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens);
    public void updateLandOnPlanet(int playerId, boolean isLanded, int planetId);
    public void updateLandOnStationOrShip(int playerId, boolean isLanded) ;
    public void updateAcceptPrizes(int playerId, boolean isAccepted);
    public void updateStopFlight(int playerId);

    public void setGameSession(GameSession gameSession);//TODO sus non penso serva


}
