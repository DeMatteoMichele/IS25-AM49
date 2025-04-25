package it.polimi.ingsw.is25am49.server.model.states;

import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.FlightMode;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;

import java.awt.*;
import java.util.Set;

public class InitState extends State {
    public InitState(GameSession gameSession){
        super(gameSession);
    }

    //To change
    public void adminSelectGameMode(Player player, GameMode gamemode) {
        gameSession.setGameMode(player, gamemode);
    }
    public void userSelectColorAndUserName(Color playerColor, String username) {
        if(gameSession.checkPlayers())
            gameSession.userSelectColorAndUserName(playerColor, username);
        else
            throw new GameSettingsException("You cannot join the game");
    }
    public void adminCreateGame(Player player) {
            gameSession.createNewGame(player);
    }
    public void adminStartGame(Player player) {
        if(player.equals(gameSession.getAdmin().orElse(null))){
            if(!gameSession.checkStatus())
                throw new GameSettingsException("You cannot start the game yet, settings are not active");
            else{
                if(gameSession.getGame().map(g -> g.getListPlayers().size() < 2).orElse(false)){
                    throw new GameSettingsException("You cannot start the game yet, not enough players");
                }
                else{
                    gameSession.configurateFlight();
                    transition(new BuildState(gameSession));
                }
            }
        }
        else
            throw new GameSettingsException("You are not the admin of this game");
    }
    public void adminSelectTypeFlight(Player player, FlightMode flightMode) {
        if(player.equals(gameSession.getAdmin().orElse(null))){
                gameSession.adminSelectTypeFlight(flightMode);
        }
        else
            throw new GameSettingsException("You are not the admin of this game");
    }

    //Illegal
    public void userPlaceTile(Player player, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userRotateTileClockwise(Player player, Point coordinates) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userRotateTileCounterClockwise(Player player, Point coordinates) {
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
    public void userStopLookSmallAdventureCardDeck(Player player,int smallDeckIndex) {
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
    public void userLandOnPlanet(Player player) {

    }
    public void userLandOnShip(Player player) {

    }
    public void userActivateDaysUpdate() {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }

    public void userQuitGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void adminCalculateScore(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public Player userShowWinner(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userEndGame(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
}
