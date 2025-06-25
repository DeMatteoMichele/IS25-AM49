package it.polimi.ingsw.is25am49.server.model.states;

import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class BuildStateTest {



    @BeforeEach
    void setUp() {
        GameSession mockSession = mock(GameSession.class);
        BuildState state = new BuildState(mockSession);
    }
    @Test
    void adminSelectGameMode() {
        BuildState state = new BuildState(mock(GameSession.class));
        // Metodo non presente in BuildState, non testabile
    }

    @Test
    void userJoinLobby() {
        BuildState state = new BuildState(mock(GameSession.class));
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userJoinLobby("user", 1));
    }

    @Test
    void adminCreateGame() {
        BuildState state = new BuildState(mock(GameSession.class));
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.adminCreateGame(1, GameMode.DEFAULT, 4));
    }

    @Test
    void userThrowDices() {
        BuildState state = new BuildState(mock(GameSession.class));
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userThrowDices(1));
    }

    @Test
    void userDrawAdventureCard() {
        BuildState state = new BuildState(mock(GameSession.class));
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userDrawAdventureCard(1));
    }

    @Test
    void userFinishDecisionActivating() {
        BuildState state = new BuildState(mock(GameSession.class));
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userFinishDecisionActivating(1, new HashSet<>()));
    }

    @Test
    void userFinishDecisionToken() {
        BuildState state = new BuildState(mock(GameSession.class));
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userFinishDecisionToken(1, new ArrayList<Pair<Point, Token>>()));
    }

    @Test
    void userLandOnPlanet() {
        BuildState state = new BuildState(mock(GameSession.class));
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userLandOnPlanet(1, true, 0));
    }

    @Test
    void userLandOnStationOrShip() {
        BuildState state = new BuildState(mock(GameSession.class));
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userLandOnStationOrShip(1, true));
    }

    @Test
    void userAcceptPrizes() {
        BuildState state = new BuildState(mock(GameSession.class));
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userAcceptPrizes(1, true));
    }

    @Test
    void userStopFlight() {
        BuildState state = new BuildState(mock(GameSession.class));
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userStopFlight(1));
    }

    @Test
    void adminCalculateScore() {
        BuildState state = new BuildState(mock(GameSession.class));
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.adminCalculateScore(1));
    }

    @Test
    void userEndGame() {
        BuildState state = new BuildState(mock(GameSession.class));
        // Metodo non presente in BuildState, non testabile
    }

    @Test
    void userPlaceTile() {

    }

    @Test
    void userRotateTileClockwise() {
    }

    @Test
    void userPickTile() {

    }

    @Test
    void userDiscardTile() {

    }

    @Test
    void userDrawTile() {

    }

    @Test
    void userFinishShipboard() {
    }

    @Test
    void connectPlayer() {
        GameSession mockSession = mock(GameSession.class);
        BuildState state = new BuildState(mockSession);
        state.connectPlayer(1);
    }

    @Test
    void disconnectPlayer() {
        GameSession mockSession = mock(GameSession.class);
        BuildState state = new BuildState(mockSession);
        state.disconnectPlayer(1);
    }

    @Test
    void userLookSmallAdventureCardDeck() {
    }

    @Test
    void userStopLookSmallAdventureCardDeck() {
    }

    @Test
    void userPlaceTimer() {
    }

    @Test
    void timerIsDone() {
    }

    @Test
    void userSelectTypeOfToken() {
    }

}