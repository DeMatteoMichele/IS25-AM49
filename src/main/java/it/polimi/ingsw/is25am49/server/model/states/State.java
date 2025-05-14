package it.polimi.ingsw.is25am49.server.model.states;

import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;


public abstract class State {
    public State(GameSession gameSession){
        this.gameSession = gameSession;
    }
    protected final GameSession gameSession;
    public void transition(State nextState) {
        gameSession.setState(nextState);
    }

    public abstract void adminSelectGameMode(int playerId, GameMode gamemode);
    public abstract void userJoinLobby(Color playerColor, String username, int playerId);
    public abstract void adminCreateGame(int playerId, GameMode gamemode, int maxPlayers);
    public abstract void adminStartGame(int playerId);

    public abstract void userPlaceTile(int playerId, Point coordinates);
    public abstract void userPickTile(int playerId, Point coordinates);
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
    public abstract void userQuitGame(int playerId);
    public abstract void userStopFlight(int playerId);

    public abstract void adminCalculateScore(int playerId);
    public abstract void userEndGame(int playerId);

    public Player getPlayerById(int playerId) {
        return gameSession.getGame()
                .map(game -> game.getPlayer(playerId))
                .orElseThrow(() -> new IllegalStateException("Game or Player not found"));
    }
}

