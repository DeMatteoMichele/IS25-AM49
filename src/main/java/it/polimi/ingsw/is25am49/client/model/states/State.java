package it.polimi.ingsw.is25am49.client.model.states;

import it.polimi.ingsw.is25am49.client.UserInterface;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.enums.Color;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;


public abstract class State {
    protected UserInterface userInterface;
    protected final GameSession gameSession;

    public State(GameSession gameSession) {
        this.gameSession = gameSession;
    }
    public State(GameSession gameSession, UserInterface userInterface) {
        this.gameSession = gameSession;
        this.userInterface = userInterface;
    }

    public void transition(State nextState) {
        gameSession.setState(nextState);
    }

    public abstract void adminSelectGameMode(Player player, GameMode gamemode);
    public abstract void userJoinLobby(Color playerColor, String username, int playerID);
    public abstract void adminCreateGame(Player player);
    public abstract void adminStartGame(Player player);

    public abstract void userPlaceTile(Player player, Point coordinates);
    public abstract void userPickTile(Player player, Point coordinates);
    public abstract void userDiscardTile(Player player);
    public abstract void userDrawTile(Player player, int idTIle);
    public abstract void userFinishShipboard(Player player);
    public abstract void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex);
    public abstract void userStopLookSmallAdventureCardDeck(Player player, int smallDeckIndex);
    public abstract void userPlaceTimer(Player player);
    public abstract void timerIsDone();
    public abstract void userSelectTypeOfToken(Player player, Token token, Point coordinates);

    public abstract void userThrowDices(Player player);
    public abstract void userDrawAdventureCard(Player player);
    public abstract void userFinishDecisionActivating(Player player, Set<Point> coordinates);
    public abstract void userFinishDecisionToken(Player player, ArrayList<Pair<Point, Token>> listOfTokens);
    public abstract void userLandOnPlanet(Player player);
    public abstract void userLandOnStationOrShip(Player player);
    public abstract void userQuitGame(Player player);
    public abstract void userStopFlight(Player player);

    public abstract void adminCalculateScore(Player player);
    public abstract void userEndGame(Player player);
}

