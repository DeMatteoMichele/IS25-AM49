package it.polimi.ingsw.is25am49.server.model.states;

import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

public class BuildState extends State {
    public BuildState(GameSession gameSession){
        super(gameSession);
    }
    //illegal
    public void adminSelectGameMode(int playerId, GameMode gamemode) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userJoinLobby(Color playerColor, String username, int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void adminCreateGame(int playerId, GameMode gamemode, int maxPlayers) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void adminStartGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    //ok
    public void userPlaceTile(int playerId, Point coordinates) {
        Player player = getPlayerById(playerId);
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
    public void userPickTile(int playerId, Point coordinates){
        Player player = getPlayerById(playerId);
        player.pickTile(coordinates);
    }
    public void userDiscardTile(int playerId){
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        gameSession.userDiscardTile(player);
    }
    public void userDrawTile(int playerId, int idTIle) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        gameSession.userDrawTile(player, idTIle);
    }
    public void userFinishShipboard(int playerId) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        if(gameSession.userFinishShipboard(player)) {
            gameSession.mergeSmallDecks();
        }
    }
    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        gameSession.userLookSmallAdventureCardDeck(player, smallDeckIndex);
    }
    public void userStopLookSmallAdventureCardDeck(int playerId,int smallDeckIndex) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        gameSession.userStopLookSmallAdventureCardDeck(player, smallDeckIndex);
    }
    public void userPlaceTimer(int playerId) {
        Player player = getPlayerById(playerId);
        gameSession.userPlaceTimer(player);
    }
    public void timerIsDone(){
        gameSession.addPlayersToLeaderBoard();
        gameSession.mergeSmallDecks();
    }
    //riempi tutti i component, anche le batterie, ecc.
    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates){

    }
    //illegal
    public void userThrowDices(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userDrawAdventureCard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userPlayAdventureCard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userFinishDecisionActivating(int playerId, Set<Point> coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    @Override
    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
        throw  new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    public void userActivateComponent(int playerId, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userLandOnStationOrShip(int playerId,boolean isLanded) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userActivateDaysUpdate() {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userAcceptPrizes(int playerId, boolean isAccepted){
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userStopFlight(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    public void adminCalculateScore(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userEndGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
}
