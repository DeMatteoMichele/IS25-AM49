package it.polimi.ingsw.is25am49.client.model.states;

import it.polimi.ingsw.is25am49.client.UserInterface;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.enums.Color;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisStateException;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

public class InitState extends State {
    public InitState(GameSession gameSession, UserInterface userInterface) {
        super(gameSession, userInterface);
        stateIndex = 0;
    }

//-----------------------------------------------------------------------------------------------------------
//    CHECK initState ↓
//-----------------------------------------------------------------------------------------------------------
    @Override
    public void checkUserJoinLobby(int playerId,String username) throws GameSettingsException {
        gameSession.checkJoinLobby(playerId, username);
    }

    @Override
    public void checkAdminCreateGame(int playerId, int mode, int numberOfPlayers) {
        gameSession.checkSetupGame(playerId,mode ,numberOfPlayers);
    }

    @Override
    public void checkUserPlaceTile(int playerId, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    @Override
    public void checkUserPickTile(int playerId, Point coordinates) throws GameSettingsException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    @Override
    public void checkUserDiscardTile(int playerId) throws IllegalStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    @Override
    public void checkUserDrawTile(int playerId, int idTIle) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    @Override
    public void checkUserFinishShipboard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    @Override
    public void checkUserLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void checkUserAcceptPrizes(int playerId, boolean acceptPrizes) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    @Override
    public void checkUserStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    @Override
    public void checkUserPlaceTimer(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    @Override
    public void checkTimerIsDone() {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    @Override
    public void checkUserSelectTypeOfToken(int playerId, Token token, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    @Override
    public void checkUserThrowDices(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    @Override
    public void checkUserDrawAdventureCard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    @Override
    public void checkUserFinishDecisionActivating(int playerId, Set<Point> coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    @Override
    public void checkUserFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    @Override
    public void checkUserLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    @Override
    public void checkUserLandOnStationOrShip(int playerId, boolean isLanded) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }



    @Override
    public void checkAdminCalculateScore(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    @Override
    public void checkAdminEndGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

//-----------------------------------------------------------------------------------------------------------
//    UPDATE initState ↓
//-----------------------------------------------------------------------------------------------------------

    public void userJoinLobby(Color playerColor, String username, int playerId) {
        gameSession.JoinLobby(playerColor, username, playerId);
        userInterface.updateGameSessionNewPlayer(playerId);
        userInterface.updateGameNewPlayer(playerId);

        //TODO per Maicals ho spostato qua l'eventuale transition al buildstate
        if (gameSession.getGame().map(g->g.numOfPlayers() == g.getMaxPlayers()).orElse(false)) {
            gameSession.configurateFlight();
        }
    }

    public void adminCreateGame(int playerId, GameMode gamemode, int maxPlayers) throws GameSettingsException {
        gameSession.checkIsAdmin(playerId);
        gameSession.createNewGame(gamemode, maxPlayers);
        userInterface.updateGameSessionCreateGame(playerId, gamemode, maxPlayers);
    }


    //Illegal Methods
    public void userRotateTileClockWise(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userPlaceTile(int playerId, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    public void userPickTile(int playerId, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userDiscardTile(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userDrawTile(int playerId, int idTIle) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userFinishShipboard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userPlaceTimer(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void timerIsDone(){
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userThrowDices(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    public void userDrawAdventureCard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userFinishDecisionActivating(int playerId, Set<Point> coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    public void userLandOnPlanet(int playerId,boolean isLanded, int planetId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userLandOnStationOrShip(int playerId, boolean isLanded) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");

    }
    public void userQuitGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    public void adminCalculateScore(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    public void adminEndGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
}
