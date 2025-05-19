package it.polimi.ingsw.is25am49.client.model.states;

import it.polimi.ingsw.is25am49.client.UserInterface;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.enums.Color;
import it.polimi.ingsw.is25am49.client.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.InvalidCoordinatesException;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Set;


public abstract class State {
    protected int stateIndex ;
    protected UserInterface userInterface;
    protected final GameSession gameSession;

    public State(GameSession gameSession, UserInterface userInterface) {
        this.gameSession = gameSession;
        this.userInterface = userInterface;
    }

    public void transition(State nextState) {
        gameSession.setState(nextState);
        userInterface.updateStateTransition(stateIndex+1);
    }

    public int getStateIndex() {
        return stateIndex;
    }

    public abstract void checkUserJoinLobby(int playerID, String username);
    public abstract void checkAdminCreateGame(int playerId, int mode, int numberOfPlayers);

    public abstract void checkUserPlaceTile(int playerId, Point coordinates) throws InvalidCoordinatesException;
    public abstract void checkUserPickTile(int playerId, Point coordinates) throws GameSettingsException;
    public abstract void checkUserDiscardTile(int playerId) throws IllegalStateException;
    public abstract void checkUserDrawTile(int playerId, int idTIle);
    public abstract void checkUserFinishShipboard(int playerId);
    public abstract void checkUserLookSmallAdventureCardDeck(int playerId, int smallDeckIndex);
    public abstract void checkUserStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex);
    public abstract void checkUserPlaceTimer(int playerId);
    public abstract void checkTimerIsDone();
    public abstract void checkUserSelectTypeOfToken(int playerId, Token token, Point coordinates);

    public abstract void checkUserThrowDices(int playerId);
    public abstract void checkUserDrawAdventureCard(int playerId);
    public abstract void checkUserFinishDecisionActivating(int playerId, Set<Point> coordinates);
    public abstract void checkUserFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens);
    public abstract void checkUserLandOnPlanet(int playerId, boolean isLanded, int planetId);
    public abstract void checkUserLandOnStationOrShip(int playerId, boolean isLanded);
    public abstract void checkUserAcceptPrizes(int playerId, boolean wantsRewards) throws RemoteException;

    public abstract void checkAdminCalculateScore(int playerId);
    public abstract void checkAdminEndGame(int playerId);



    public abstract void userJoinLobby(Color playerColor, String username, int playerID);
    public abstract void adminCreateGame(int playerId, GameMode gamemode, int maxPlayers);

    public abstract void userPlaceTile(int playerId, Point coordinates);
    public abstract void userPickTile(int playerId, Point coordinates);
    public abstract void userRotateTileClockWise(int playerId);
    public abstract void userDiscardTile(int playerId);
    public abstract void userDrawTile(int playerId, int idTIle);
    public abstract void userFinishShipboard(int playerId);
    public abstract void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex);
    public abstract void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex);
    public abstract void userPlaceTimer(int playerId);
    public abstract void timerIsDone();
    public abstract void userSelectTypeOfToken(int playerId, Token token, Point coordinates);

    public abstract void userThrowDices(int playerId);
    public abstract void userDrawAdventureCard(int playerId);
    public abstract void userFinishDecisionActivating(int playerId, Set<Point> coordinates);
    public abstract void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens);
    public abstract void userLandOnPlanet(int playerId, boolean isLanded, int planetId);
    public abstract void userLandOnStationOrShip(int playerId, boolean isLanded);

    public void userQuitGame(int playerId) {
        Player player = getPlayerById(playerId);
        gameSession.userQuitGame(player);
    }


    public abstract void adminCalculateScore(int playerId);

    public abstract void adminEndGame(int playerId);

    public Player getPlayerById(int playerId) {
        return gameSession.getGame()
                .map(game -> game.getPlayer(playerId))
                .orElseThrow(() -> new IllegalStateException("Game or Player not found"));
    }

}

