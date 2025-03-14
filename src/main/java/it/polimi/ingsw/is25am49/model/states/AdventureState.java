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

public abstract class AdventureState extends State {
    public AdventureState(GameSession gameSession){
        super(gameSession);
    }
    public void adminSelectGameMode(Player player, GameMode gamemode) throws IllegalMethodOnThisStateException, NormalUserCannotModifyGameSettingsException, GameDoesNotExistException {}
    public void userSelectColorAndUserName(Color playerColor, String username) throws IllegalMethodOnThisStateException, GameDoesNotExistException {}
    public void adminCreateGame(Player player) throws IllegalMethodOnThisStateException, NormalUserCannotModifyGameSettingsException {}
    public void adminStartGame(Player player) throws IllegalMethodOnThisStateException, GameDoesNotExistException, NormalUserCannotModifyGameSettingsException {}
    public void adminSelectTypeFlight(Player player, FlightMode flightMode) throws IllegalMethodOnThisStateException, NormalUserCannotModifyGameSettingsException, GameDoesNotExistException {
        throw new IllegalMethodOnThisStateException("Illegal method while in AdventureState");
    }
    public void userPlaceTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException {}
    public void userRotateTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException {}
    public void userPickTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException {}
    public void userDiscardTile(Player player) throws IllegalMethodOnThisStateException {}
    public void userDrawTile(Player player, int idTIle) throws IllegalMethodOnThisStateException {}
    public void userFinishShipboard(Player player) throws IllegalMethodOnThisStateException {}

    public abstract void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex) throws IllegalMethodOnThisStateException;
    public abstract void userStopLookSmallAdventureCardDeck(Player player,int smallDeckIndex) throws IllegalMethodOnThisStateException;
    public abstract void userPlaceTimer(Player player) throws IllegalMethodOnThisStateException;
    public abstract void userThrowDices(Player player) throws IllegalMethodOnThisStateException;
    public abstract void userDrawAdventureCard(Player player) throws IllegalMethodOnThisStateException;
    public abstract void userPlayAdventureCard(Player player) throws IllegalMethodOnThisStateException;
    public abstract void userEffectDecision(Player player) throws IllegalMethodOnThisStateException;
    public abstract void userActivateComponent(Player player, Point coordinates) throws IllegalMethodOnThisStateException;
    public abstract void userActivateDaysUpdate() throws IllegalMethodOnThisStateException;
    public abstract void userQuitGame(Player player) throws IllegalMethodOnThisStateException;
}
