package it.polimi.ingsw.is25am49.model.states;

import it.polimi.ingsw.is25am49.model.*;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.FlightMode;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.exceptions.GameDoesNotExistException;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.model.exceptions.NormalUserCannotModifyGameSettingsException;

import java.awt.*;

public class InitState extends State {
    public InitState(GameSession gameSession){
        super(gameSession);
    }

    //Implemented
    public void adminSelectGameMode(Player player, GameMode gamemode) throws IllegalMethodOnThisStateException, GameDoesNotExistException, NormalUserCannotModifyGameSettingsException {
        if(gameSession.getGame() == null)
            throw new GameDoesNotExistException("The game has not been created");
        if(player.equals(gameSession.getAdmin()))
            gameSession.setGameMode(gamemode);
        else
            throw new NormalUserCannotModifyGameSettingsException("You are not the admin of this game");
    }
    public void userSelectColorAndUserName(Color playerColor, String username) throws IllegalMethodOnThisStateException, GameDoesNotExistException, NormalUserCannotModifyGameSettingsException {
        if(gameSession.getGame() == null)
            throw new GameDoesNotExistException("The game has not been created");
        gameSession.userSelectColorAndUserName(playerColor, username);
    }
    public void adminCreateGame(Player player) throws IllegalMethodOnThisStateException, NormalUserCannotModifyGameSettingsException {
        if(player.equals(gameSession.getAdmin()))
            gameSession.createNewGame();
        else
            throw new NormalUserCannotModifyGameSettingsException("You are not the admin of this game");
    }
    public void adminStartGame(Player player) throws IllegalMethodOnThisStateException, GameDoesNotExistException, NormalUserCannotModifyGameSettingsException {
        if(gameSession.getGame() == null)
            throw new GameDoesNotExistException("The game has not been created");
        if(player.equals(gameSession.getAdmin())){
            gameSession.configurateFlight();
            transition(new BuildState(gameSession));
        }
        else
            throw new NormalUserCannotModifyGameSettingsException("You are not the admin of this game");

    }
    public void adminSelectTypeFlight(Player player, FlightMode flightMode) throws IllegalMethodOnThisStateException, NormalUserCannotModifyGameSettingsException, GameDoesNotExistException {
        if(player.equals(gameSession.getAdmin()))
            gameSession.adminSelectTypeFlight(flightMode);
        else
            throw new NormalUserCannotModifyGameSettingsException("You are not the admin of this game");
    }

    //Illegal
    public void userPlaceTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userRotateTileClockwise(Player player, Point coordinates) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userRotateTileCounterClockwise(Player player, Point coordinates) throws IllegalMethodOnThisStateException{

    }
    public void userPickTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userDiscardTile(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userDrawTile(Player player, int idTIle) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userFinishShipboard(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userStopLookSmallAdventureCardDeck(Player player,int smallDeckIndex) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userPlaceTimer() throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userThrowDices(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }


    public void userDrawAdventureCard(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userPlayAdventureCard(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userEffectDecision(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userActivateComponent(Player player, Point coordinates) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userActivateDaysUpdate() throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userQuitGame(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void calculateScore() throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in InitState");
    }
    public void userShowWinner() throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userEndGame() throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
}
