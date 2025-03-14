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

public class FinishScoreState extends State {
    public FinishScoreState(GameSession gameSession){
        super(gameSession);
    }
    public void adminSelectGameMode(Player player,GameMode gamemode) throws IllegalMethodOnThisStateException, NormalUserCannotModifyGameSettingsException, GameDoesNotExistException {}
    public void userSelectColorAndUserName(Color playerColor, String username) throws IllegalMethodOnThisStateException, GameDoesNotExistException {}
    public void userInsertName(Player player,String playerName) throws IllegalMethodOnThisStateException {}
    public void adminCreateGame(Player player) throws IllegalMethodOnThisStateException, NormalUserCannotModifyGameSettingsException {}
    public void adminStartGame(Player player) throws IllegalMethodOnThisStateException, GameDoesNotExistException, NormalUserCannotModifyGameSettingsException {}
    public void userPlaceTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException {}
    public void userRotateTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException {}
    public void userPickTile(Player player, Point coordinates) throws IllegalMethodOnThisStateException {}
    public void userDiscardTile(Player player) throws IllegalMethodOnThisStateException {}
    public void userDrawTile(Player player, int idTIle) throws IllegalMethodOnThisStateException {}
    public void userFinishShipboard(Player player) throws IllegalMethodOnThisStateException {}
    public void adminSelectTypeFlight(Player player, FlightMode flightMode) throws IllegalMethodOnThisStateException, NormalUserCannotModifyGameSettingsException, GameDoesNotExistException {
        throw new IllegalMethodOnThisStateException("Illegal method while in FinishScoreState");
    }
    public void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex) throws IllegalMethodOnThisStateException {}
    public void userStopLookSmallAdventureCardDeck(Player player,int smallDeckIndex) throws IllegalMethodOnThisStateException {}
    public void userPlaceTimer(Player player) throws IllegalMethodOnThisStateException {}
    public void userThrowDices(Player player) throws IllegalMethodOnThisStateException {}
    public void userDrawAdventureCard(Player player) throws IllegalMethodOnThisStateException {}
    public void userPlayAdventureCard(Player player) throws IllegalMethodOnThisStateException {}
    public void userEffectDecision(Player player) throws IllegalMethodOnThisStateException {}
    public void userActivateComponent(Player player, Point coordinates) throws IllegalMethodOnThisStateException {}
    public void userActivateDaysUpdate() throws IllegalMethodOnThisStateException {}
    public void userQuitGame(Player player) throws IllegalMethodOnThisStateException {}
}
