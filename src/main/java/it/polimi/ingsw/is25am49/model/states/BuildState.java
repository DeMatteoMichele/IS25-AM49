package it.polimi.ingsw.is25am49.model.states;

import it.polimi.ingsw.is25am49.model.GameSession;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.FlightMode;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.exceptions.GameDoesNotExistException;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.model.exceptions.NormalUserCannotModifyGameSettingsException;

import java.awt.*;

public class BuildState extends State {
    public BuildState(GameSession gameSession){
        super(gameSession);
    }
    public void adminSelectGameMode(Player admin, GameMode gamemode) throws IllegalMethodOnThisStateException, NormalUserCannotModifyGameSettingsException, GameDoesNotExistException {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userSelectColorAndUserName(Color playerColor, String username) throws IllegalMethodOnThisStateException, GameDoesNotExistException {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void adminCreateGame(Player player) throws IllegalMethodOnThisStateException, NormalUserCannotModifyGameSettingsException {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void adminStartGame(Player player) throws IllegalMethodOnThisStateException, GameDoesNotExistException, NormalUserCannotModifyGameSettingsException {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void adminSelectTypeFlight(Player player, FlightMode flightMode) throws IllegalMethodOnThisStateException, NormalUserCannotModifyGameSettingsException, GameDoesNotExistException {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userPlaceTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException {}
    public void userRotateTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException {}
    public void userPickTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException {}
    public void userDiscardTile(Player player) throws IllegalMethodOnThisStateException {}
    public void userDrawTile(Player player, int idTIle) throws IllegalMethodOnThisStateException {}
    public void userFinishShipboard(Player player) throws IllegalMethodOnThisStateException {}
    public void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex) throws IllegalMethodOnThisStateException {}
    public void userStopLookSmallAdventureCardDeck(Player player,int smallDeckIndex) throws IllegalMethodOnThisStateException {}
    public void userPlaceTimer(Player player) throws IllegalMethodOnThisStateException {}

    public void userThrowDices(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userDrawAdventureCard(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userPlayAdventureCard(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userEffectDecision(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userActivateComponent(Player player, Point coordinates) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userActivateDaysUpdate() throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
    public void userQuitGame(Player player) throws IllegalMethodOnThisStateException {
        throw new IllegalMethodOnThisStateException("Illegal method while in BuildState");
    }
}
