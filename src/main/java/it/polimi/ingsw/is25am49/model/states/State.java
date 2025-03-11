package it.polimi.ingsw.is25am49.model.states;

import it.polimi.ingsw.is25am49.model.*;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.GameMode;

import java.awt.*;


public abstract class State {
    public State(Game game){
        this.game = game;
    }
    protected final Game game;
    public void transition(State nextState) {
        game.set_state(nextState);
    }

    public abstract void adminSelectGameMode(GameMode gamemode);
    public abstract void userSelectColor(Player player, Color playerColor);
    public abstract void userInsertName(Player player,String playerName);

    // public abstract void adminCreateLobby(Player) {}
    // public abstract void userJoinLobby() {}
    // public abstract void adminStartGame() {}

    public abstract void userPlaceTile(Player player, Point coordinates);
    public abstract void userRotateTile(Player player, Point coordinates);
    public abstract void userPickTile(Player player, Point coordinates);
    public abstract void userDiscardTile(Player player);
    public abstract void userDrawTile(Player player, int idTIle);
    public abstract void userFinishShipboard(Player player);
    public abstract void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex);
    public abstract void userStopLookSmallAdventureCardDeck(Player player,int smallDeckIndex);
    public abstract void userPlaceTimer(Player player);
    public abstract void userThrowDices(Player player);


    public abstract void userDrawAdventureCard(Player player);
    public abstract void userPlayAdventureCard(Player player);
    public abstract void userEffectDecision(Player player);
    public abstract void userActivateComponent(Player player, Point coordinates);
    public abstract void userActivateDaysUpdate();
    public abstract void userQuitGame(Player player);
}
