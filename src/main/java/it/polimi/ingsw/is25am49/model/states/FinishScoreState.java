package it.polimi.ingsw.is25am49.model.states;

import it.polimi.ingsw.is25am49.model.GameSession;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.FlightMode;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;

import java.awt.*;

public class FinishScoreState extends State {
    public FinishScoreState(GameSession gameSession){
        super(gameSession);
    }
    public void adminSelectGameMode(Player player,GameMode gamemode) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userSelectColorAndUserName(Color playerColor, String username) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void adminCreateGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void adminStartGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userPlaceTile(Player player, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userRotateTileClockwise(Player player, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userRotateTileCounterClockwise(Player player, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userPickTile(Player player, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userDiscardTile(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userDrawTile(Player player, int idTIle) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userFinishShipboard(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void adminSelectTypeFlight(Player player, FlightMode flightMode) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userStopLookSmallAdventureCardDeck(Player player,int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userPlaceTimer(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void timerIsDone(){
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userThrowDices(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userDrawAdventureCard(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userPlayAdventureCard(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userFinishDecision(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userActivateComponent(Player player, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }


    public void userLandOnPlanet(Player player) {

    }


    public void userLandOnShip(Player player) {

    }

    public void userActivateDaysUpdate() {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userQuitGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void adminCalculateScore(Player player) {
        gameSession.calculateScore(player);
    }
    public Player userShowWinner() {
        if(gameSession.userShowWinner().isPresent())
            return gameSession.userShowWinner().get();
        else
            throw new GameSettingsException("The game is not finished yet");
    }
    public void userEndGame() {
        gameSession.userEndGame();
    }
}
