package it.polimi.ingsw.is25am49.client.model.states;

import it.polimi.ingsw.is25am49.client.UserInterface;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.enums.Color;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.client.model.exceptions.InvalidCoordinatesException;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

public class BuildState extends State {
    public BuildState(GameSession gameSession, UserInterface ui) {
        super(gameSession, ui);
    }

//-----------------------------------------------------------------------------------------------------------
//    CHECK buildState ↓
//-----------------------------------------------------------------------------------------------------------
    @Override
    public void checkUserJoinLobby(int playerID) {
        throw new IllegalMethodOnThisStateException("Illegal Method on this state");
    }
    @Override
    public void checkAdminCreateGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal Method on this state");

    }
    @Override
    public void checkAdminStartGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal Method on this state");

    }
    @Override
    public void checkUserPlaceTile(int playerId, Point coordinates) throws InvalidCoordinatesException {
        Player player = getPlayerById(playerId);
        player.thereIsHandTile();
        player.shipBoardIsNotCompletedYet();
        player.getShipBoard().checkInsertTile(coordinates);
    }
    @Override
    public void checkUserPickTile(int playerId, Point coordinates) throws GameSettingsException {
        Player player = getPlayerById(playerId);
        player.checkPickTile(coordinates);
    }
    @Override
    public void checkUserDiscardTile(int playerId) throws IllegalStateException {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        player.thereIsHandTile();
    }
    @Override
    public void checkUserDrawTile(int playerId, int idTIle) {

    }
    @Override
    public void checkUserFinishShipboard(int playerId) {

    }
    @Override
    public void checkUserLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {

    }
    @Override
    public void checkUserStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {

    }
    @Override
    public void checkUserPlaceTimer(int playerId) {

    }
    @Override
    public void checkTimerIsDone() {

    }
    @Override
    public void checkUserSelectTypeOfToken(int playerId, Token token, Point coordinates) {

    }
    @Override
    public void checkUserThrowDices(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal Method on this state");

    }
    @Override
    public void checkUserDrawAdventureCard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal Method on this state");

    }
    @Override
    public void checkUserAcceptPrizes(int playerId, boolean wantsRewards) {
        throw new IllegalMethodOnThisStateException("Illegal Method on this state");
    }
    @Override
    public void checkUserFinishDecisionActivating(int playerId, Set<Point> coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal Method on this state");

    }
    @Override
    public void checkUserFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
        throw new IllegalMethodOnThisStateException("Illegal Method on this state");

    }
    @Override
    public void checkUserLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        throw new IllegalMethodOnThisStateException("Illegal Method on this state");

    }
    @Override
    public void checkUserLandOnStationOrShip(int playerId, boolean isLanded) {
        throw new IllegalMethodOnThisStateException("Illegal Method on this state");

    }
    @Override
    public void checkAdminCalculateScore(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal Method on this state");

    }
    @Override
    public void checkAdminEndGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal Method on this state");

    }

//-----------------------------------------------------------------------------------------------------------
//    UPDATE buildState ↓
//-----------------------------------------------------------------------------------------------------------
    public void userJoinLobby(Color playerColor, String username, int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    public void adminCreateGame(int playerId, GameMode gamemode, int maxPlayers) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    public void adminStartGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

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
    public void userPickTile(int playerId, Point coordinates) {
        Player player = getPlayerById(playerId);
        player.pickTile(coordinates);
    }

    public void userDiscardTile(int playerId) {
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
        if (gameSession.userFinishShipboard(player)) {
            gameSession.mergeSmallDecks();
        }
    }

    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        gameSession.userLookSmallAdventureCardDeck(player, smallDeckIndex);
    }

    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        gameSession.userStopLookSmallAdventureCardDeck(player, smallDeckIndex);
    }

    public void userPlaceTimer(int playerId) {
        Player player = getPlayerById(playerId);
        gameSession.userPlaceTimer(player);
    }

    public void timerIsDone() {
        gameSession.addPlayersToLeaderBoard();
        gameSession.mergeSmallDecks();
    }

    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) {
        //TODO
    }

    //illegal
    public void userThrowDices(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    public void userDrawAdventureCard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    public void userFinishDecisionActivating(int playerId, Set<Point> coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    @Override
    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    public void userLandOnStationOrShip(int playerId, boolean isLanded) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }



    public void adminCalculateScore(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    public void adminEndGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
}
