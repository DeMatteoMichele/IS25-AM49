package it.polimi.ingsw.is25am49.client.model.states;

import it.polimi.ingsw.is25am49.client.UserInterface;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisStateException;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

public class AdventureState extends State {
    public AdventureState(GameSession gameSession, UserInterface ui){
        super(gameSession,ui);
        stateIndex = 2;
    }

    public void checkUserJoinLobby(int playerID,String username) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }

    public void checkAdminCreateGame(int playerId, int mode, int numberOfPlayers) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }


    public void checkAdminEndGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }

    public void userJoinLobby(Color playerColor, String username, int playerID) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }

    public void adminCreateGame(int playerId, GameMode gamemode, int maxPlayers) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }

    public void userRotateTileClockWise(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }

    public void adminStartGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userPlaceTile(int playerId, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userPickTile(int playerId, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userDiscardTile(int playerId){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userDrawTile(int playerId, int idTIle) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userFinishShipboard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }

    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userPlaceTimer(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void timerIsDone(){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userThrowDices(int playerId){
        Player player = getPlayerById(playerId);
        gameSession.userThrowDices(player);
    }
    public void userDrawAdventureCard(int playerId){
        Player player = getPlayerById(playerId);
        gameSession.userDrawAdventureCard(player);
    }

    public void userFinishDecisionActivating(int playerId, Set<Point> coordinates){
        gameSession.userFinishDecisionActivating(coordinates);
    }
    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens){
        gameSession.userFinishDecisionToken(listOfTokens);
    }

    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        gameSession.userLandOnPlanet(isLanded, planetId);
    }
    public void userLandOnStationOrShip(int playerId, boolean isLanded) {
        gameSession.userLandOnStationOrShip(isLanded);

    }
    public void userAcceptPrizes(int playerId, boolean isAccepted){
        gameSession.userAcceptPrizes(isAccepted);
    }

    public void userStopFlight(int playerId) {
        Player player = getPlayerById(playerId);
        gameSession.userStopFlight(player);
    }



    public void adminCalculateScore(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void adminEndGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void checkAdminSelectGameMode(Player player, GameMode gamemode) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void checkUserJoinLobby(Color playerColor, String username, int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void checkAdminSelectGameMode(int playerId, GameMode gamemode) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void checkAdminCreateGame(int playerId, GameMode gamemode, int maxPlayers) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }

    public void checkUserPlaceTile(int playerId, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void checkUserPickTile(int playerId, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void checkUserDiscardTile(int playerId){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void checkUserDrawTile(int playerId, int idTIle) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void checkUserFinishShipboard(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }

    public void checkUserLookSmallAdventureCardDeck(int playerId, int smallDeckIndex){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void checkUserStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void checkUserPlaceTimer(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void checkTimerIsDone(){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void checkUserSelectTypeOfToken(int playerId, Token token, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void checkUserThrowDices(int playerId){
        Player player = getPlayerById(playerId);
        Player currentPlayer = getCurrentPlayer();
        gameSession.checkThrowDicesState();
        if(!player.equals(currentPlayer)){
            throw new IllegalMethodOnThisStateException("You are not the current player");
        }
    }

    public void checkUserDrawAdventureCard(int playerId)  {
        gameSession.checkUserDrawAdventureCard(playerId);
    }
    public void checkUserFinishDecisionActivating(int playerId, Set<Point> coordinates){
        gameSession.checkFinishDecisionActivatingStates();
        Player player = getPlayerById(playerId);
        Player currentPlayer = getCurrentPlayer();
        if(!player.equals(currentPlayer)){
            throw new IllegalMethodOnThisStateException("You are not the current player");
        }
        gameSession.checkFinishDecisionActivatingCoordinates(coordinates);

    }
    public void checkUserFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens){
        gameSession.checkFinishDecisionTokenStates();
        Player player = getPlayerById(playerId);
        Player currentPlayer = getCurrentPlayer();
        if(!player.equals(currentPlayer)){
            throw new IllegalMethodOnThisStateException("You are not the current player");
        }
        gameSession.checkFinishDecisionTokenCoordinates(listOfTokens);
    }

    public void checkUserLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        gameSession.checkLandingOnPlanet();
        Player player = getPlayerById(playerId);
        //controllo se il player che chiama il metodo è il current player
        //se lo è smetto di passare il player perché gli effetti della carta sono sul current player
        Player currentPlayer = getCurrentPlayer();
        if(!player.equals(currentPlayer)){
            throw new IllegalMethodOnThisStateException("You are not the current player");
        }

    }
    public void checkUserLandOnStationOrShip(int playerId, boolean isLanded) {
        gameSession.checkLandOnStationOrShipState();
        Player player = getPlayerById(playerId);
        Player currentPlayer = getCurrentPlayer();
        if(!player.equals(currentPlayer)){
            throw new IllegalMethodOnThisStateException("You are not the current player");
        }

    }
    public void checkUserAcceptPrizes(int playerId, boolean isAccepted){
        gameSession.checkAcceptPrizesState();
        Player player = getPlayerById(playerId);
        Player currentPlayer = getCurrentPlayer();
        if(!player.equals(currentPlayer)){
            throw new IllegalMethodOnThisStateException("You are not the current player");
        }
    }



    public void checkAdminCalculateScore(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void checkUserEndGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }

    public Player getCurrentPlayer() {
        return gameSession.getGame()
                .map(g -> g.getGameboard().getCurrentPlayer())
                .orElse(null);
    }
}
