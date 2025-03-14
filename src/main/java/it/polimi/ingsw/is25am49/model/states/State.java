package it.polimi.ingsw.is25am49.model.states;

import it.polimi.ingsw.is25am49.model.*;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;

import java.awt.*;


public abstract class State {
    public State(Game game){
        this.game = game;
    }
    protected final Game game;
    public void transition(State nextState) {
        game.setState(nextState);
    }

    public abstract void adminSelectGameMode(GameMode gamemode) throws IllegalMethodOnThisStateException;
    public abstract void userSelectColor(Player player, Color playerColor) throws IllegalMethodOnThisStateException;
    public abstract void userInsertName(Player player,String playerName) throws IllegalMethodOnThisStateException;

    // public abstract void adminCreateLobby(Player) {}
    // public abstract void userJoinLobby() {}
    // public abstract void adminStartGame() {}

    public abstract void userPlaceTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException;
    public abstract void userRotateTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException;
    public abstract void userPickTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException;
    public abstract void userDiscardTile(Player player) throws IllegalMethodOnThisStateException;
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
}
