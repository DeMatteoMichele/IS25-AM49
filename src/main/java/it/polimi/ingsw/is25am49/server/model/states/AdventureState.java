package it.polimi.ingsw.is25am49.server.model.states;

import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.FlightMode;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;

import java.awt.*;

public class AdventureState extends State {
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
    public void userPickTile(Player player, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userDiscardTile(Player player){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userDrawTile(Player player, int idTIle) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userFinishShipboard(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }

    public void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userStopLookSmallAdventureCardDeck(Player player,int smallDeckIndex){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userPlaceTimer(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void timerIsDone(){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userThrowDices(Player player){
        gameSession.userThrowDices(player);
    }
    public void userDrawAdventureCard(Player player){
        gameSession.userDrawAdventureCard(player);
    }
    public void userPlayAdventureCard(Player player){}
    public void userFinishDecision(Player player){}
    public void userActivateComponent(Player player, Point coordinates){}
    public void userActivateDaysUpdate(){}
    public void userLandOnPlanet(Player player) {
    }
    public void userLandOnShip(Player player) {
    }
    public void userQuitGame(Player player){}

    public void adminCalculateScore(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public Player userShowWinner(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userEndGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
}
