package it.polimi.ingsw.is25am49.server.model.states;

import it.polimi.ingsw.is25am49.server.model.Game;
import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.exceptions.OutOfDeckExceptions;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Orientation;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

public class AdventureState extends State {
    public AdventureState(GameSession gameSession){
        super(gameSession);
    }
    public void adminSelectGameMode(Player player, GameMode gamemode) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userJoinLobby(String username, int playerId) {
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

    public void adminSelectGameMode(int playerId, GameMode gamemode) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void adminCreateGame(int playerId, GameMode gamemode, int maxPlayers) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
   public void adminStartGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userRotateTile(int playerId, Orientation orientation) throws GameSettingsException {
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
    public void userDrawAdventureCard(int playerId) throws GameSettingsException {
        Player player = getPlayerById(playerId);
        try {
            gameSession.userDrawAdventureCard(player);
        }catch (OutOfDeckExceptions _){
            gameSession.getGame().ifPresent(Game::finishGame);
            transition(new FinishScoreState(gameSession));
        }

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
    public void userAcceptPrizes(int playerId, boolean isAccepted){
        Player player = getPlayerById(playerId);
        Player currentPlayer = getCurrentPlayer();
        if(player.equals(currentPlayer)){
            gameSession.userAcceptPrizes(isAccepted);
        }
        else{
            throw new IllegalMethodOnThisStateException("You are not the current player");
        }
    }

    public void userStopFlight(int playerId) {
        Player player = getPlayerById(playerId);
        try {
            gameSession.userStopFlight(player);
        }catch (GameSettingsException _){//lanciata in remove player della leaderboard (se ho un giocatore solo finish)
            gameSession.getGame().ifPresent(Game::finishGame);
            transition(new FinishScoreState(gameSession));
        }
    }

    public void adminCalculateScore(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userEndGame(int playerId) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public Player getCurrentPlayer(){
        return gameSession.getGame()
                .map(g->g.getGameboard().getCurrentPlayer())
                .orElse(null);
    }
}
