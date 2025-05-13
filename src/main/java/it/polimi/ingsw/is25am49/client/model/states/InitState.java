package it.polimi.ingsw.is25am49.client.model.states;

import it.polimi.ingsw.is25am49.client.UserInterface;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.Player;
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
    }

    public void userJoinLobby(Color playerColor, String username, int playerId) {
        gameSession.userJoinLobby(playerColor, username, playerId);
    }
    public void adminCreateGame(Player player, GameMode gamemode, int maxPlayers) {
        gameSession.checkIsAdmin(player);
        gameSession.createNewGame(gamemode, maxPlayers);
        userInterface.updateGameSessionCreateGame(player.getIdPlayer(), gamemode, maxPlayers);
    }
    public void adminStartGame(Player player) {
        gameSession.checkIsAdmin(player);
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
    public void userPlaceTile(Player player, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    public void userPickTile(Player player, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userDiscardTile(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userDrawTile(Player player, int idTIle) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userFinishShipboard(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userStopLookSmallAdventureCardDeck(Player player, int smallDeckIndex) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userPlaceTimer(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void timerIsDone(){
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userSelectTypeOfToken(Player player, Token token, Point coordinates){
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userThrowDices(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    public void userDrawAdventureCard(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userFinishDecisionActivating(Player player, Set<Point> coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    public void userFinishDecisionToken(Player player, ArrayList<Pair<Point, Token>> listOfTokens) {

    }

    public void userLandOnPlanet(Player player) {

    }
    public void userLandOnStationOrShip(Player player) {

    }
    public void userQuitGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    public void userStopFlight(Player player) {

    }

    public void adminCalculateScore(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userEndGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
}
