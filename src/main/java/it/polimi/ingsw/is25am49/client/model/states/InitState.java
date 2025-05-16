package it.polimi.ingsw.is25am49.client.model.states;

import it.polimi.ingsw.is25am49.client.UserInterface;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.enums.Color;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.client.model.Player;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

public class InitState extends State {
    public InitState(GameSession gameSession, UserInterface userInterface) {
        super(gameSession, userInterface);
    }

    public void userJoinLobby(Color playerColor, String username, int playerId) {
        gameSession.userJoinLobby(playerColor, username, playerId);
        userInterface.updateGameSessionNewPlayer(playerId);
        userInterface.updateGameNewPlayer(playerId);
    }
    public void adminSelectGameMode(int playerId, GameMode gamemode) {
        Player player = getPlayerById(playerId);
        gameSession.checkIsAdmin(playerId);
        gameSession.setGameMode(gamemode);
    }
    public void adminCreateGame(int playerId, GameMode gamemode, int maxPlayers) throws GameSettingsException {
        gameSession.checkIsAdmin(playerId);
        gameSession.createNewGame(gamemode, maxPlayers);
        userInterface.updateGameSessionCreateGame(playerId, gamemode, maxPlayers);
    }
    public void adminStartGame(int playerId) {
        gameSession.checkIsAdmin(playerId);
        if(!gameSession.checkGameMode())
            throw new GameSettingsException("You cannot start the game yet, set gameMode");
        else{
            if(gameSession.getGame().map(g -> g.numOfPlayers() < 2).orElse(false)){
                throw new GameSettingsException("You cannot start the game yet, not enough players");
            }
            else{
                gameSession.configurateFlight();
                transition(new BuildState(gameSession, userInterface));
            }
        }
    }


    //Illegal Methods
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

    public void userStopFlight(int playerId) {

    }

    public void adminCalculateScore(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userEndGame(int playerId)  {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void checkAdminSelectGameMode(int playerId, GameMode gamemode) throws GameSettingsException {
        gameSession.checkIsAdmin(playerId);
        gameSession.setGameMode(gamemode);
    }
    public void checkUserJoinLobby(Color playerColor, String username, int playerId) throws GameSettingsException {
        gameSession.userJoinLobby(playerColor, username, playerId);
    }
    public void checkAdminCreateGame(int playerId, GameMode gamemode, int maxPlayers) throws GameSettingsException{
        gameSession.checkIsAdmin(playerId);
        gameSession.createNewGame(gamemode, maxPlayers);
    }
    public void checkAdminStartGame(int playerId) throws GameSettingsException{
        gameSession.checkIsAdmin(playerId);
        if(!gameSession.checkGameMode())
            throw new GameSettingsException("You cannot start the game yet, set gameMode");
        else{
            if(gameSession.getGame().map(g -> g.numOfPlayers() < 2).orElse(false)){
                throw new GameSettingsException("You cannot start the game yet, not enough players");
            }
            else{
                gameSession.configurateFlight();
                transition(new BuildState(gameSession, userInterface));
            }
        }
    }
    //Illegal
    public void checkUserPlaceTile(int playerId, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    public void checkUserPickTile(int playerId, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void checkUserDiscardTile(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void checkUserDrawTile(int playerId, int idTIle) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void checkUserFinishShipboard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void checkUserLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void checkUserStopLookSmallAdventureCardDeck(int playerId,int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void checkUserPlaceTimer(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void checkTimerIsDone(){
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void checkUserSelectTypeOfToken(int playerId, Token token, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void checkUserThrowDices(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }


    public void checkUserDrawAdventureCard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void checkUserFinishDecisionActivating(int playerId, Set<Point> coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    public void checkUserFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    public void checkUserLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void checkUserLandOnStationOrShip(int playerId, boolean isLanded) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void checkUserAcceptPrizes(int playerId, boolean isAccepted) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }


    public void checkUserStopFlight(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    public void checkAdminCalculateScore(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void checkUserEndGame(int playerId)  {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
}
