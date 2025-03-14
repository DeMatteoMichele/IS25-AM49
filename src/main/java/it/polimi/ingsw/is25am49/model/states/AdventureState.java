package it.polimi.ingsw.is25am49.model.states;

import it.polimi.ingsw.is25am49.model.Game;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;

import java.awt.*;

public abstract class AdventureState extends State {
    public AdventureState(Game game){
        super(game);
    }
    public void adminSelectGameMode(GameMode gamemode) throws IllegalMethodOnThisStateException {}
    public void userSelectColor(Player player, Color playerColor) throws IllegalMethodOnThisStateException {}
    public void userInsertName(Player player,String playerName) throws IllegalMethodOnThisStateException {}

    // public void adminCreateLobby(Player) {}
    // public void userJoinLobby() {}
    // public void adminStartGame() {}

    public void userPlaceTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException {}
    public void userRotateTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException {}
    public void userPickTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException {}
    public void userDiscardTile(Player player) throws IllegalMethodOnThisStateException {}
    public void userDrawTile(Player player, int idTIle) throws IllegalMethodOnThisStateException {}
    public void userFinishShipboard(Player player) throws IllegalMethodOnThisStateException {}
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
