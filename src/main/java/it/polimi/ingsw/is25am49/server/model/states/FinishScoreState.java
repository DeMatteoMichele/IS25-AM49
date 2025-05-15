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

public class FinishScoreState extends State {
    public FinishScoreState(GameSession gameSession){
        super(gameSession);
    }
    public void adminSelectGameMode(int playerId,GameMode gamemode) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userJoinLobby(Color playerColor, String username, int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void adminCreateGame(int playerId, GameMode gamemode, int maxPlayers) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
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

    }

    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) {

    }


    public void userLandOnStationOrShip(int playerId, boolean isLanded) {

    }

    public void userQuitGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }

    public void userStopFlight(int playerId) {

    }

    public void adminCalculateScore(int playerId) {
        Player player = getPlayerById(playerId);
        gameSession.calculateScore(playerId);
    }
    public void userEndGame(int playerId) {
        Player player = getPlayerById(playerId);
        gameSession.userEndGame(playerId);
    }
}
