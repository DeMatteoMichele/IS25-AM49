package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.LeaderBoard;
import it.polimi.ingsw.is25am49.client.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import javafx.util.Pair;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.Set;
import java.util.List;
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
    public void updatePickTile(int playerId, Point coordinates, int idTile);
    public void updateRotateTileClockWise(int playerId);
    public void updateDiscardTile(int playerId, int idTile);
    public void updateDrawTile(int playerId, int idTIle);
    public void updateFinishShipboard(int playerId);
    public void updateLookSmallAdventureCardDeck(int playerId, int smallDeckIndex);
    public void updateStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex);
    public void updatePlaceTimer(int playerId);
    public void updateTimerIsDone();
    public void updateSelectTypeOfToken(int playerId, Token token, Point coordinates);
    public void lookAtShipboard(int playerId);

    //ADVENTURE STATE:
    public void updateCardState(int playerId, AdventureStatesTypes cardState);
    public void updateChangeTurn(int playerId);
    public void updateGenericCardMessage(int playerId, String message);
    public void updateThrowDices(int playerId, int firstDice, int secondDice);
    public void updateDrawAdventureCard(int playerId, AdventureCard adventureCard);
    public void updateFinishDecisionActivating(int playerId, Set<Point> coordinates);
    public void updateFinishDecisionToken(int playerId, List<Pair<Point, Token>> listOfTokens, int numTokens,boolean removing);
    public void updateDeactivating(int playerId, List<Point> coordinates);

    public void updateLandOnPlanet(int playerId, boolean isLanded, int planetId);
    public void updateLandOnStationOrShip(int playerId, boolean isLanded,String cardId);
    public void updateAcceptPrizes(int playerId, boolean isAccepted);
    public void updateStopFlight(int playerId);

    public void updateEnemyDefeated(int playerIdWhoWon, String enemyName, String cardId);
    public void updateEffectModifyCrew(int playerId, Point coordinate, Token token);
    public void updateEffectCannonade(int playerId, Point coordinate);
    public void updateEffectLoseGoods(int playerId, Point coordinate, Token token);
    public void updateEffectWinGoods(int playerId, Point coordinate, Token token);
    public void updateEffectModifyEnergy(int playerId,Point coordinate, Token token);
    public void updateEffectMeteorite(int playerId, Point coordinate);
    public void updateEarnedCredits(int playerId, int credits);
    public void updateDestroyedTiles(int idPlayer, Set<Point> destroyedTiles);
    public void updateLeaderBoard(LeaderBoard leaderBoard);

    public void updateEndGame(int playerId);
    public void updateCalculateScore(int playerId);
    //public void updateShowWinner(int playerId);


    public void connectionRMI(boolean isReconnecting, ClientRMI clientRMI) throws NotBoundException, IOException;
    public void setGameSession(GameSession gameSession);//TODO sus non penso serva
}
