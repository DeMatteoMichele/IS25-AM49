package it.polimi.ingsw.is25am49.client.model.states;

import it.polimi.ingsw.is25am49.client.UserInterface;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.enums.Color;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisStateException;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

public class FinishScoreState extends State {
    public FinishScoreState(GameSession gameSession, UserInterface ui) {
        super(gameSession,ui);
    }


    @Override
    public void checkAdminEndGame(int playerId) {
        gameSession.checkAdminEndGame(playerId);
    }

    public void adminCreateGame(int playerId, GameMode gamemode, int maxPlayers) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userJoinLobby(Color playerColor, String username, int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userPlaceTile(int playerId, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userRotateTileClockwise(int playerId, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userRotateTileCounterClockwise(int playerId, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userPickTile(int playerId, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userDiscardTile(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userDrawTile(int playerId, int idTIle) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userFinishShipboard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userStopLookSmallAdventureCardDeck(int playerId,int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userPlaceTimer(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void timerIsDone(){
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userThrowDices(int playerid) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userDrawAdventureCard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userFinishDecisionActivating(int playerId, Set<Point> coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }

    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }

    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }

    public void userLandOnStationOrShip(int playerId, boolean isLanded) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }

    public void userAcceptPrizes(int playerId, boolean isAccepted){
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }



    public void adminCalculateScore(int playerId) {
        gameSession.calculateScore(playerId);
    }
    public void adminEndGame(int playerId) {
        gameSession.adminEndGame(playerId);
    }
    public void checkUserJoinLobby(int playerId,String username) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void checkAdminCreateGame(int playerId, int mode, int numberOfPlayers) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void checkAdminSelectGameMode(int playerId,GameMode gamemode) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void checkUserJoinLobby(Color playerColor, String username, int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }

    public void checkAdminStartGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void checkUserPlaceTile(int playerId, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void checkUserRotateTileClockwise(int playerId, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void checkUserRotateTileCounterClockwise(int playerId, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void checkUserPickTile(int playerId, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void checkUserDiscardTile(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void checkUserDrawTile(int playerId, int idTIle) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void checkUserFinishShipboard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void checkUserLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void checkUserStopLookSmallAdventureCardDeck(int playerId,int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void checkUserPlaceTimer(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void checkTimerIsDone(){
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void checkUserSelectTypeOfToken(int playerId, Token token, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void checkUserThrowDices(int playerid) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void checkUserDrawAdventureCard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void checkUserPlayAdventureCard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void checkUserFinishDecisionActivating(int playerId, Set<Point> coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }

    public void checkUserFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }

    public void checkUserLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }


    public void checkUserLandOnStationOrShip(int playerId, boolean isLanded) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }

    public void checkUserAcceptPrizes(int playerId, boolean isAccepted){
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }



    public void checkAdminCalculateScore(int playerId)  {
        gameSession.calculateScore(playerId);
    }
    public void checkUserEndGame(int playerId){
        gameSession.adminEndGame(playerId);
    }
}
