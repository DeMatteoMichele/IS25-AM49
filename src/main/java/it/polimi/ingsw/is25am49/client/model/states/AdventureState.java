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

public class AdventureState extends State {
    public AdventureState(GameSession gameSession, UserInterface ui){
        super(gameSession,ui);
    }

    @Override
    public void checkUserJoinLobby(int playerID) {

    }

    @Override
    public void checkAdminCreateGame(int playerId) {

    }

    @Override
    public void checkAdminStartGame(int playerId) {

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

    public void userJoinLobby(Color playerColor, String username, int playerID) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }

    public void adminCreateGame(int playerId, GameMode gamemode, int maxPlayers) {
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
        Player currentPlayer = getCurrentPlayer();
        if(player.equals(currentPlayer)){
            gameSession.userThrowDices(player);
        }
        else{
            throw new IllegalMethodOnThisStateException("You are not the current player");
        }
    }
    public void userDrawAdventureCard(int playerId) {
        Player player = getPlayerById(playerId);
        gameSession.userDrawAdventureCard(player);
    }

    public void userFinishDecisionActivating(int playerId, Set<Point> coordinates){
        Player player = getPlayerById(playerId);
        Player currentplayer = getCurrentPlayer();
        if(player.equals(currentplayer)){
            gameSession.userFinishDecisionActivating(coordinates);
        }

    }
    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens){
        Player player = getPlayerById(playerId);
        Player currentPlayer = getCurrentPlayer();
        if(player.equals(currentPlayer)){
            gameSession.userFinishDecisionToken(listOfTokens);
        }
        else {
            throw new IllegalMethodOnThisStateException("You are not the current player");
        }
    }

    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) {
        Player player = getPlayerById(playerId);
        //controllo se il player che chiama il metodo è il current player
        //se lo è smetto di passare il player perché gli effetti della carta sono sul current player
        Player currentPlayer = getCurrentPlayer();
        if(player.equals(currentPlayer)){
            gameSession.userLandOnPlanet(isLanded, planetId);
        }
        else{
            throw new IllegalMethodOnThisStateException("You are not the current player");
        }

    }
    public void userLandOnStationOrShip(int playerId, boolean isLanded) {
        Player player = getPlayerById(playerId);
        Player currentPlayer = getCurrentPlayer();
        if(player.equals(currentPlayer)){
            gameSession.userLandOnStationOrShip(isLanded);
        }
        else{
            throw new IllegalMethodOnThisStateException("You are not the current player");
        }

    }

    public void userStopFlight(int playerId) {

    }

    public void adminCalculateScore(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userEndGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
}
