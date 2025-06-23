package it.polimi.ingsw.is25am49.client.model.states;

import it.polimi.ingsw.is25am49.client.UserInterface;
import it.polimi.ingsw.is25am49.client.model.Game;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.shared.enums.Orientation;

import it.polimi.ingsw.is25am49.client.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.client.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.is25am49.client.model.states.AdventureState;
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
        if(player.isWatching())throw new RuntimeException("you are still watching a small deck, stop watching it first");
        player.thereIsHandTile();
        player.shipBoardIsNotCompletedYet();
        player.getShipBoard().checkInsertTile(coordinates);
    }
    @Override
    public void checkUserPickTile(int playerId, Point coordinates) throws GameSettingsException {
        Player player = getPlayerById(playerId);
        if(player.isWatching())throw new RuntimeException("you are still watching a small deck, stop watching it first");
        player.checkPickTile(coordinates);
    }
    @Override
    public void checkUserDiscardTile(int playerId) throws IllegalStateException {
        Player player = getPlayerById(playerId);
        if(player.isWatching())throw new RuntimeException("you are still watching a small deck, stop watching it first");
        player.shipBoardIsNotCompletedYet();
        player.thereIsHandTile();
    }
    @Override
    public void checkUserDrawTile(int playerId, int idTIle) {
        if(idTIle<1 || idTIle>157)throw new RuntimeException("invalid idTIle");
        Player player = getPlayerById(playerId);
        if(player.isWatching())throw new RuntimeException("you are still watching a small deck, stop watching it first");
        player.shipBoardIsNotCompletedYet();
    }
    @Override
    public void checkUserFinishShipboard(int playerId) {
        Player player = getPlayerById(playerId);
        if(player.isWatching())throw new RuntimeException("you are still watching a small deck, stop watching it first");
        player.shipBoardIsNotCompletedYet();
    }
    @Override
    public void checkUserLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        if(smallDeckIndex <0 || smallDeckIndex >2) throw new RuntimeException("selected smallDeck doesn't exist!");
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
        if(gameSession.getGame().isPresent()){
            if (gameSession.getGame().get().isTestFlight())throw new GameSettingsException("This is a Test Flight you can not add an alien");
        }
        if(player.isWatching())throw new RuntimeException("you are still watching a small deck, stop watching it first");
        player.getShipBoard().checkCoordinates(coordinates);
        if(!player.getShipBoard().checkIfAlienArePossible(coordinates).contains(token))throw new RuntimeException("you cannot add a "+token+" token in this position, correct alien module is missing");
        player.getShipBoard().checkAddTokenToTile(coordinates, token);
        if(token.equals(Token.BROWN_ALIENS)||token.equals(Token.PURPLE_ALIENS)){
            if(player.getShipBoard().getTotalNumberOfaToken(token)>0)throw new RuntimeException("you can have only one "+token+" token in your shipboard");
        }
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
    public void userRotateHandTile(int playerId, Orientation orientation) {
        Player player = getPlayerById(playerId);
        if(player.getHandTile().isPresent()){
            player.getHandTile().get().setTileOrientation(orientation);
        }else throw new RuntimeException("there is no tile in hand to rotate");
    }
    //ok
    public void userPickTile(int playerId, Point coordinates) {
        Player player = getPlayerById(playerId);
        int idTile = player.getShipBoard().getTile(coordinates).getIdTile();
        player.pickTile(coordinates);
        userInterface.updatePickTile(playerId,coordinates,idTile);
    }

    public void userDiscardTile(int playerId) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        gameSession.userDiscardTile(player);
//        userInterface.updateDiscardTile(playerId);
    }

    public void userDrawTile(int playerId, int idTIle) {
        Player player = getPlayerById(playerId);
        player.shipBoardIsNotCompletedYet();
        gameSession.userDrawTile(player, idTIle);
        userInterface.updateDrawTile(playerId,idTIle);
    }

    public void userFinishShipboard(int playerId) {
        Player player = getPlayerById(playerId);
        //controlla che tutti abbiano finito la shipboard
        if (gameSession.userFinishShipboard(player)) {
            gameSession.mergeSmallDecks();
            userInterface.updateFinishShipboard(playerId);
            if(gameSession.getGame().isPresent()) {
                Game game = gameSession.getGame().get();
                for (Player p : game.getListPlayers()) {
                    if(p.getShipBoard().getAlienPoint().isPresent()) {
                        return;
                    }
                }
                userInterface.updateFinishShipboard(playerId);
                transition(new AdventureState(gameSession, userInterface));
                return;
            }
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

    public void userPlaceTimerNoTime(int playerId) {
        Player player = getPlayerById(playerId);
        gameSession.userPlaceTimerNoTime(player);
    }

    public void timerIsDone() {
        gameSession.mergeSmallDecks();
        userInterface.updateTimerIsDone();
        if (gameSession.getGame().isPresent()) {
            for (Player p: gameSession.getGame().get().getListPlayers()) {
                try {
                    gameSession.getState().userFinishShipboard(p.getIdPlayer());
                }catch (Exception _){}
            }
        }else throw new RuntimeException("timer is done GAME NON SETTATO non so perch'");
    }
    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) {
        Player player = getPlayerById(playerId);
        if(gameSession.userSelectTypeOfToken(player, token, coordinates)) {
            userInterface.updateSelectTypeOfToken(playerId,token,coordinates);
            userFinishShipboard(playerId);
            return;
        }
        userInterface.updateSelectTypeOfToken(playerId,token,coordinates);
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

    @Override
    public void connectPlayer(int playerId) {
        gameSession.connectPlayer(playerId);
    }

    @Override
    public void disconnectPlayer(int playerId) {
        gameSession.disconnectPlayer(playerId);
    }
}
