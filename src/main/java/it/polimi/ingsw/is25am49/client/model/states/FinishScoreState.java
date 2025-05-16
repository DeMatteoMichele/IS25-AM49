package it.polimi.ingsw.is25am49.client.model.states;

import it.polimi.ingsw.is25am49.client.UserInterface;
import it.polimi.ingsw.is25am49.client.model.GameSession;
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

public class FinishScoreState extends State {
    public FinishScoreState(GameSession gameSession, UserInterface ui) {
        super(gameSession,ui);
    }

    @Override
    public void checkUserJoinLobby(int playerId) throws GameSettingsException {

    }

    @Override
    public void checkAdminCreateGame(int playerId) {

    }

    public void userJoinLobby(Color playerColor, String username, int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }

    public void adminCreateGame(int playerId, GameMode gamemode, int maxPlayers) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }

    @Override
    public void checkAdminStartGame(int playerId) throws GameSettingsException {

    }

    @Override
    public void checkUserPlaceTile(int playerId, Point coordinates) throws InvalidCoordinatesException {

    }

    @Override
    public void checkUserPickTile(int playerId, Point coordinates) throws GameSettingsException {

    }

    @Override
    public void checkUserDiscardTile(int playerId) throws IllegalStateException {

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

    }

    @Override
    public void checkUserDrawAdventureCard(int playerId) {

    }

    @Override
    public void checkUserFinishDecisionActivating(int playerId, Set<Point> coordinates) {

    }

    @Override
    public void checkUserFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) {

    }

    @Override
    public void checkUserLandOnPlanet(int playerId, boolean isLanded, int planetId) {

    }

    @Override
    public void checkUserLandOnStationOrShip(int playerId, boolean isLanded) {

    }

    @Override
    public void checkUserStopFlight(int playerId) {

    }

    @Override
    public void checkAdminCalculateScore(int playerId) {

    }

    @Override
    public void checkUserEndGame(int playerId) {

    }

    public void adminStartGame(int playerId) {
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
    public void userPlayAdventureCard(int playerId) {
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


    public void userStopFlight(int playerId) {

    }

    public void adminCalculateScore(int playerId) {
        gameSession.calculateScore(playerId);
    }
    public void userEndGame(int playerId) {
        gameSession.userEndGame(playerId);
    }
}
