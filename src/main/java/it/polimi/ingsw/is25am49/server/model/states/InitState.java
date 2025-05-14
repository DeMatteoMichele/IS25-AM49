package it.polimi.ingsw.is25am49.server.model.states;

import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
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

public class InitState extends State {

    public InitState(GameSession gameSession){
        super(gameSession);
    }

    //To change
    public void adminSelectGameMode(int playerId, GameMode gamemode) {
        Player player = gameSession.getPlayers().get(playerId);
        gameSession.checkIsAdmin(player);
        gameSession.setGameMode(gamemode);
    }
    public void userJoinLobby(Color playerColor, String username, int playerId) {
        gameSession.userJoinLobby(playerColor, username, playerId);
    }
    public void adminCreateGame(int playerId, GameMode gamemode, int maxPlayers) {
        Player player = gameSession.getPlayers().get(playerId);
        gameSession.checkIsAdmin(player);
        gameSession.createNewGame(gamemode, maxPlayers);
    }
    public void adminStartGame(int playerId) {
        Player player = getPlayerById(playerId);
        gameSession.checkIsAdmin(player);
        if(!gameSession.checkGameMode())
            throw new GameSettingsException("You cannot start the game yet, set gameMode");
        else{
            if(gameSession.getGame().map(g -> g.numOfPlayers() < 2).orElse(false)){
                throw new GameSettingsException("You cannot start the game yet, not enough players");
            }
            else{
                gameSession.configurateFlight();
                transition(new BuildState(gameSession));
            }
        }
    }
    //Illegal
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
    public void userStopLookSmallAdventureCardDeck(int playerId,int smallDeckIndex) {
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

    }

    public void userLandOnPlanet(int playerId, boolean isLanded, int planetId) {

    }
    public void userLandOnStationOrShip(int playerId, boolean isLanded) {

    }
    public void userQuitGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    public void userStopFlight(int playerId) {

    }

    public void adminCalculateScore(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userEndGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
}
