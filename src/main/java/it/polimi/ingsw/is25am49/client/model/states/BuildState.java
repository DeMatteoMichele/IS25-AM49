package it.polimi.ingsw.is25am49.client.model.states;

import it.polimi.ingsw.is25am49.client.UserInterface;
import it.polimi.ingsw.is25am49.client.model.Game;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Token;
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
        stateIndex = 1;
    }

//-----------------------------------------------------------------------------------------------------------
//    CHECK buildState ↓
//-----------------------------------------------------------------------------------------------------------
    @Override
    public void checkUserJoinLobby(int playerID,String username){
        throw new IllegalMethodOnThisStateException("Illegal Method on this state");
    }
    @Override
    public void checkAdminCreateGame(int playerId, int mode, int numberOfPlayers) {
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
        if(idTIle<1 || idTIle>157)throw new RuntimeException("invalid idTIle");
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
    }
    @Override
    public void checkUserFinishShipboard(int playerId) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
    }
    @Override
    public void checkUserLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        if(smallDeckIndex <0 || smallDeckIndex >3) throw new RuntimeException("selected smallDeck doesn't exist!");
    }
    @Override
    public void checkUserStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        if(smallDeckIndex <0 || smallDeckIndex >3) throw new RuntimeException("selected smallDeck doesn't exist!");
    }
    @Override
    public void checkUserPlaceTimer(int playerId) {
        if (gameSession.getGame().map(Game::isTestFlight).orElse(true)) throw new GameSettingsException("This is a Test Flight");
    }
    @Override
    public void checkTimerIsDone() {
        //non penso servano controlli
    }
    @Override
    public void checkUserSelectTypeOfToken(int playerId, Token token, Point coordinates) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        player.getShipBoard().checkCoordinates(coordinates);
        player.getShipBoard().checkAddTokenToTile(coordinates, token);
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
        userInterface.updatePlaceTile(playerId,coordinates);
    }

    public void userRotateTileClockWise(int playerId) {
        Player player = getPlayerById(playerId);
        if(player.getHandTile().isEmpty())throw new RuntimeException("there is no tile in hand to rotate");
        player.rotateTile(true);
        userInterface.updateRotateTileClockWise(playerId);
    }
    //ok
    public void userPickTile(int playerId, Point coordinates) {
        Player player = getPlayerById(playerId);
        player.pickTile(coordinates);
        userInterface.updatePickTile(playerId,coordinates);
    }

    public void userDiscardTile(int playerId) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        gameSession.userDiscardTile(player);
        userInterface.updateDiscardTile(playerId);
    }

    public void userDrawTile(int playerId, int idTIle) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        gameSession.userDrawTile(player, idTIle);
        userInterface.updateDrawTile(playerId,idTIle);
    }

    public void userFinishShipboard(int playerId) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        if (gameSession.userFinishShipboard(player)) {
            gameSession.mergeSmallDecks();
        }
        userInterface.updateFinishShipboard(playerId);
    }

    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        gameSession.userLookSmallAdventureCardDeck(player, smallDeckIndex);
        userInterface.updateLookSmallAdventureCardDeck(playerId,smallDeckIndex);
    }

    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        gameSession.userStopLookSmallAdventureCardDeck(player, smallDeckIndex);
        userInterface.updateStopLookSmallAdventureCardDeck(playerId,smallDeckIndex);
    }

    public void userPlaceTimer(int playerId) {
        Player player = getPlayerById(playerId);
        gameSession.userPlaceTimer(player);
        userInterface.updatePlaceTimer(playerId);
    }

    public void timerIsDone() {
        gameSession.addPlayersToLeaderBoard();
        gameSession.mergeSmallDecks();
        userInterface.updateTimerIsDone();
    }
    //todo NON è stata ricopiata esattamente, Bisogna decidere bene cosa fare con le transizioni di stato
    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) {
        Player player = getPlayerById(playerId);
        gameSession.userSelectTypeOfToken(player, token, coordinates);
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

    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }

    public void userLandOnStationOrShip(int playerId, boolean isLanded) {
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

    public void adminEndGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
}
