package it.polimi.ingsw.is25am49.model.states;

import it.polimi.ingsw.is25am49.model.GameSession;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.FlightMode;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;

import java.awt.*;

public abstract class AdventureState extends State {
    public AdventureState(GameSession gameSession){
        super(gameSession);
    }
    public void adminSelectGameMode(Player player, GameMode gamemode) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userSelectColorAndUserName(Color playerColor, String username) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void adminCreateGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void adminStartGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void adminSelectTypeFlight(Player player, FlightMode flightMode) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userPlaceTile(Player player, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userRotateTileClockwise(Player player, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userRotateTileCounterClockwise(Player player, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userPickTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userDiscardTile(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userDrawTile(Player player, int idTIle) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userFinishShipboard(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }

    public abstract void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex);
    public abstract void userStopLookSmallAdventureCardDeck(Player player,int smallDeckIndex);
    public abstract void userPlaceTimer();
    public abstract void userThrowDices(Player player);
    public abstract void userDrawAdventureCard(Player player);
    public abstract void userPlayAdventureCard(Player player);
    public abstract void userFinishDecision(Player player);
    public abstract void userActivateComponent(Player player, Point coordinates);
    public abstract void userActivateDaysUpdate();
    public abstract void userQuitGame(Player player);

    public void adminCalculateScore(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public Player userShowWinner() {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userEndGame() {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
}
