package it.polimi.ingsw.is25am49.server.model.states;

import it.polimi.ingsw.is25am49.server.model.*;
import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.FlightMode;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;

import java.awt.*;


public abstract class State {
    public State(GameSession gameSession){
        this.gameSession = gameSession;
    }
    protected final GameSession gameSession;
    public void transition(State nextState) {
        gameSession.setState(nextState);
    }

    public abstract void adminSelectGameMode(Player player, GameMode gamemode);
    public abstract void userSelectColorAndUserName(Color playerColor, String username);
    public abstract void adminCreateGame(Player player);
    public abstract void adminStartGame(Player player);
    public abstract void adminSelectTypeFlight(Player player, FlightMode flightMode);

    public abstract void userPlaceTile(Player player, Point coordinates);
    public abstract void userRotateTileClockwise(Player player, Point coordinates);
    public abstract void userRotateTileCounterClockwise(Player player, Point coordinates) ;
    public abstract void userPickTile(Player player, Point coordinates);
    public abstract void userDiscardTile(Player player);
    public abstract void userDrawTile(Player player, int idTIle);
    public abstract void userFinishShipboard(Player player);
    public abstract void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex);
    public abstract void userStopLookSmallAdventureCardDeck(Player player,int smallDeckIndex);
    public abstract void userPlaceTimer(Player player);
    public abstract void timerIsDone();

    public abstract void userThrowDices(Player player);
    public abstract void userDrawAdventureCard(Player player);
    public abstract void userPlayAdventureCard(Player player);
    public abstract void userFinishDecision(Player player);
    public abstract void userActivateComponent(Player player, Point coordinates);
    public abstract void userLandOnPlanet(Player player);
    public abstract void userLandOnShip(Player player);
    public abstract void userActivateDaysUpdate();


    public abstract void userQuitGame(Player player);
    public abstract void adminCalculateScore(Player player);
    public abstract Player userShowWinner(Player player);
    public abstract void userEndGame(Player player);
}

