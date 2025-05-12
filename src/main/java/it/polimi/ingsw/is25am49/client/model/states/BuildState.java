package it.polimi.ingsw.is25am49.client.model.states;

import it.polimi.ingsw.is25am49.client.UserInterface;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.enums.Color;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisStateException;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

public class BuildState extends State {
    public BuildState(GameSession gameSession, UserInterface ui){
        super(gameSession, ui);
    }

    public void userJoinLobby(Color playerColor, String username, int playerID) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void adminCreateGame(Player player, GameMode gamemode, int maxPlayers) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void adminStartGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    //ok
    public void userPlaceTile(Player player, Point coordinates) {
        player.placeTile(coordinates);
    }
    //da discutere
//    public void userRotateTileClockwise(Player player, Point coordinates) {
//        player.rotateTile(true);
//    }
//    public void userRotateTileCounterClockwise(Player player, Point coordinates) {
//        player.rotateTile(false);
//    }
    //ok
    public void userPickTile(Player player, Point coordinates){
        player.pickTile(coordinates);
    }
    public void userDiscardTile(Player player){
        player.shipBoardIsNotCompletedYet();
        gameSession.userDiscardTile(player);
    }
    public void userDrawTile(Player player, int idTIle) {
        player.shipBoardIsNotCompletedYet();
        gameSession.userDrawTile(player, idTIle);
    }
    public void userFinishShipboard(Player player) {
        player.shipBoardIsNotCompletedYet();
        if(gameSession.userFinishShipboard(player)) {
            gameSession.mergeSmallDecks();
        }
    }
    public void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex) {
        player.shipBoardIsNotCompletedYet();
        gameSession.userLookSmallAdventureCardDeck(player, smallDeckIndex);
    }
    public void userStopLookSmallAdventureCardDeck(Player player, int smallDeckIndex) {
        player.shipBoardIsNotCompletedYet();
        gameSession.userStopLookSmallAdventureCardDeck(player, smallDeckIndex);
    }
    public void userPlaceTimer(Player player) {
        gameSession.userPlaceTimer(player);
    }
    public void timerIsDone(){
        gameSession.addPlayersToLeaderBoard();
        gameSession.mergeSmallDecks();
    }
    //riempi tutti i component, anche le batterie, ecc.
    public void userSelectTypeOfToken(Player player, Token token, Point coordinates){

    }
    //illegal
    public void userThrowDices(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userDrawAdventureCard(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userPlayAdventureCard(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userFinishDecisionActivating(Player player, Set<Point> coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    @Override
    public void userFinishDecisionToken(Player player, ArrayList<Pair<Point, Token>> listOfTokens) {

    }

    public void userActivateComponent(Player player, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userLandOnPlanet(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userLandOnStationOrShip(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userActivateDaysUpdate() {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userQuitGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    public void userStopFlight(Player player) {

    }

    public void adminCalculateScore(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userEndGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
}
