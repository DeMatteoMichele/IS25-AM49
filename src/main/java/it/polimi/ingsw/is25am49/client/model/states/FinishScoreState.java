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

public class FinishScoreState extends State {
    public FinishScoreState(GameSession gameSession, UserInterface ui) {
        super(gameSession,ui);
    }

    public void userJoinLobby(Color playerColor, String username, int playerID) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void adminCreateGame(Player player, GameMode gamemode, int maxPlayers) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void adminStartGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userPlaceTile(Player player, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userRotateTileClockwise(Player player, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userRotateTileCounterClockwise(Player player, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userPickTile(Player player, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userDiscardTile(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userDrawTile(Player player, int idTIle) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userFinishShipboard(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userStopLookSmallAdventureCardDeck(Player player, int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userPlaceTimer(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void timerIsDone(){
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userSelectTypeOfToken(Player player, Token token, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userThrowDices(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userDrawAdventureCard(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userPlayAdventureCard(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userFinishDecisionActivating(Player player, Set<Point> coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }

    public void userFinishDecisionToken(Player player, ArrayList<Pair<Point, Token>> listOfTokens) {

    }

    public void userLandOnPlanet(Player player) {

    }


    public void userLandOnStationOrShip(Player player) {

    }

    public void userQuitGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }

    public void userStopFlight(Player player) {

    }

    public void adminCalculateScore(Player player) {
        gameSession.calculateScore(player);
    }
    public void userEndGame(Player player) {
        gameSession.userEndGame(player);
    }
}
