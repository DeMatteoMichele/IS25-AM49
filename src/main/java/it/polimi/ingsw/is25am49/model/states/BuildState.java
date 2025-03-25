package it.polimi.ingsw.is25am49.model.states;

import it.polimi.ingsw.is25am49.model.GameSession;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.FlightMode;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;

import java.awt.*;

public class BuildState extends State {
    public BuildState(GameSession gameSession){
        super(gameSession);

    }

    public void adminSelectGameMode(Player admin, GameMode gamemode) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userSelectColorAndUserName(Color playerColor, String username) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void adminCreateGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void adminStartGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void adminSelectTypeFlight(Player player, FlightMode flightMode) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    public void userPlaceTile(Player player, Point coordinates) {
        player.placeTile(coordinates);
    }
    public void userRotateTileClockwise(Player player, Point coordinates) {
        player.rotateTile(true);
    }
    public void userRotateTileCounterClockwise(Player player, Point coordinates) {
        player.rotateTile(false);
    }
    public void userPickTile(Player player, Point coordinates){
        player.pickTile(coordinates);
    }
    public void userDiscardTile(Player player){
        gameSession.userDiscardTile(player);
    }
    public void userDrawTile(Player player, int idTIle) {
        gameSession.userDrawTile(player, idTIle);
    }
    public void userFinishShipboard(Player player) {
        if(gameSession.userFinishShipboard(player)) {
            gameSession.mergeSmallDecks();
            transition(new AdventureState(gameSession));
        }
    }
    public void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex) {
        gameSession.userLookSmallAdventureCardDeck(player, smallDeckIndex);
    }
    public void userStopLookSmallAdventureCardDeck(Player player,int smallDeckIndex) {
        gameSession.userStopLookSmallAdventureCardDeck(player, smallDeckIndex);
    }
    public void userPlaceTimer(Player player) {
        gameSession.userPlaceTimer(player);
    }
    public void timerIsDone(){
        gameSession.addPlayersToLeaderBoard();
        gameSession.mergeSmallDecks();
        transition(new AdventureState(gameSession));
    }

    public void userThrowDices(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userDrawAdventureCard(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userPlayAdventureCard(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userFinishDecision(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userActivateComponent(Player player, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userLandOnPlanet(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userLandOnShip(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    public void userActivateDaysUpdate() {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userQuitGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void adminCalculateScore(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public Player userShowWinner() {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userEndGame() {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
}
