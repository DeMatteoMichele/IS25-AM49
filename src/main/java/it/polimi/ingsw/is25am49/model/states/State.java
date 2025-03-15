package it.polimi.ingsw.is25am49.model.states;

import it.polimi.ingsw.is25am49.model.*;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.FlightMode;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.exceptions.GameDoesNotExistException;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.model.exceptions.NormalUserCannotModifyGameSettingsException;

import java.awt.*;


public abstract class State {
    public State(GameSession gameSession){
        this.gameSession = gameSession;
    }
    protected final GameSession gameSession;
    public void transition(State nextState) {
        gameSession.setState(nextState);
    }

    public abstract void adminSelectGameMode(Player player, GameMode gamemode) throws IllegalMethodOnThisStateException, NormalUserCannotModifyGameSettingsException, GameDoesNotExistException;
    public abstract void userSelectColorAndUserName(Color playerColor, String username) throws IllegalMethodOnThisStateException, GameDoesNotExistException;
    public abstract void adminCreateGame(Player player) throws IllegalMethodOnThisStateException, NormalUserCannotModifyGameSettingsException;
    public abstract void adminStartGame(Player player) throws IllegalMethodOnThisStateException, GameDoesNotExistException, NormalUserCannotModifyGameSettingsException;
    public abstract void adminSelectTypeFlight(Player player, FlightMode flightMode) throws IllegalMethodOnThisStateException,NormalUserCannotModifyGameSettingsException, GameDoesNotExistException;

    public abstract void userPlaceTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException;
    public abstract void userRotateTileClockwise(Player player, Point coordinates) throws IllegalMethodOnThisStateException;
    public abstract void userRotateTileCounterClockwise(Player player, Point coordinates) throws IllegalMethodOnThisStateException;
    public abstract void userDrawTile(Player player, int idTIle) throws IllegalMethodOnThisStateException;
    public abstract void userFinishShipboard(Player player) throws IllegalMethodOnThisStateException;
    public abstract void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex) throws IllegalMethodOnThisStateException;
    public abstract void userStopLookSmallAdventureCardDeck(Player player,int smallDeckIndex) throws IllegalMethodOnThisStateException;
    public abstract void userPlaceTimer(Player player) throws IllegalMethodOnThisStateException;
    public abstract void userThrowDices(Player player) throws IllegalMethodOnThisStateException;


    public abstract void userDrawAdventureCard(Player player) throws IllegalMethodOnThisStateException;
    public abstract void userPlayAdventureCard(Player player) throws IllegalMethodOnThisStateException;
    public abstract void userEffectDecision(Player player) throws IllegalMethodOnThisStateException;
    public abstract void userActivateComponent(Player player, Point coordinates) throws IllegalMethodOnThisStateException;
    public abstract void userActivateDaysUpdate() throws IllegalMethodOnThisStateException;
    public abstract void userQuitGame(Player player) throws IllegalMethodOnThisStateException;
    public abstract void calculateScore() throws IllegalMethodOnThisStateException;
    public abstract void userShowWinner() throws IllegalMethodOnThisStateException;
    public abstract void userEndGame() throws IllegalMethodOnThisStateException;
}

