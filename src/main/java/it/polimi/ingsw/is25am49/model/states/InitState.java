package it.polimi.ingsw.is25am49.model.states;

import it.polimi.ingsw.is25am49.model.*;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;

import java.awt.*;

public class InitState extends State {
    public InitState(Game game){
        super(game);
    }
    public void adminSelectGameMode(GameMode gamemode) throws IllegalMethodOnThisStateException {
    }
    public void userSelectColor(Player player, Color playerColor) throws IllegalMethodOnThisStateException {
    }
    public void userInsertName(Player player,String playerName) throws IllegalMethodOnThisStateException {

    }

    // public void adminCreateLobby(Player) {}
    // public void userJoinLobby() {}
    // public void adminStartGame() {}

    public void userPlaceTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userRotateTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userPickTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userDiscardTile(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userDrawTile(Player player, int idTIle) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userFinishShipboard(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userStopLookSmallAdventureCardDeck(Player player,int smallDeckIndex) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userPlaceTimer(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userThrowDices(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }


    public void userDrawAdventureCard(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userPlayAdventureCard(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userEffectDecision(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userActivateComponent(Player player, Point coordinates) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userActivateDaysUpdate() throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userQuitGame(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

}
