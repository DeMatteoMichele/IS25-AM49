package it.polimi.ingsw.is25am49.model.states;

import it.polimi.ingsw.is25am49.model.*;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.FlightMode;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;

import java.awt.*;

public class InitState extends State {
    public InitState(GameSession gameSession){
        super(gameSession);
    }

    //Implemented
    public void adminSelectGameMode(Player player, GameMode gamemode) {
        if(player.equals(gameSession.getAdmin())){
            if(gameSession.getGame()!=null)
                gameSession.setGameMode(gamemode);
            else
                throw new GameSettingsException("The game has not been created");
        }
        else
            throw new GameSettingsException("You are not the admin of this game");
    }
    public void userSelectColorAndUserName(Color playerColor, String username) {
        if(gameSession.getGame() == null)
            throw new GameSettingsException("The game has not been created");
        else{
            if(gameSession.checkPlayers())
                gameSession.userSelectColorAndUserName(playerColor, username);
            else
                throw new GameSettingsException("You cannot join the game");
        }
    }
    public void adminCreateGame(Player player) {
        if(player.equals(gameSession.getAdmin()))
            gameSession.createNewGame();
        else
            throw new GameSettingsException("You are not the admin of this game");
    }
    public void adminStartGame(Player player) {
        if(player.equals(gameSession.getAdmin())){
            if(!gameSession.checkStatus())
                throw new GameSettingsException("You cannot start the game yet, settings are not active");
            else{
                if(gameSession.getGame().getListPlayers().size()<2){
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
        if(player.equals(gameSession.getAdmin())){
            if(gameSession.getGame()!=null)
                gameSession.adminSelectTypeFlight(flightMode);
            else
                throw new GameSettingsException("The game has not been created");
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

    }
    public void userPickTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userDiscardTile(Player player) throws IllegalMethodOnThisStateException {
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
    public void userPlaceTimer() {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userThrowDices(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }


    public void userDrawAdventureCard(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userPlayAdventureCard(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userFinishDecision(Player player) {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userActivateComponent(Player player, Point coordinates) {
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
    public Player userShowWinner() {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userEndGame() {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
}
