package it.polimi.ingsw.is25am49.server.model.states;

import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Orientation;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.is25am49.server.model.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class InitStateTest {

    InitState state;
    GameSession mockSession;

    @BeforeEach
    void init() {
        mockSession = mock(GameSession.class);
        state = new InitState(mockSession);
    }

    @Test
    void adminSelectGameMode() {
        assertDoesNotThrow(() -> state.adminSelectGameMode(1, GameMode.DEFAULT));
    }


    @Test
    void testAdminSelectGameMode() {
        assertDoesNotThrow( () -> state.adminSelectGameMode(1, GameMode.DEFAULT));
    }

    @Test
    void userJoinLobby() {
        assertDoesNotThrow( () -> state.userJoinLobby("user", 1));
    }

    @Test
    void connectPlayer() {
        state.connectPlayer(1);
        verify(mockSession).connectPlayer(1);
    }

    @Test
    void disconnectPlayer() {
        state.disconnectPlayer(1);
        verify(mockSession).disconnectPlayer(1);
    }

    @Test
    void adminCreateGame() {
        assertDoesNotThrow( () -> state.adminCreateGame(1, GameMode.DEFAULT, 4));
    }

    @Test
    void userRotateTile() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userRotateTile(3, Orientation.NORTH));
    }

    @Test
    void userPlaceTile() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userPlaceTile(1, new Point(0, 0)));
    }

    @Test
    void userPickTile() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userPickTile(1, new Point(0, 0)));
    }

    @Test
    void userDiscardTile() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userDiscardTile(1));
    }

    @Test
    void userDrawTile() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userDrawTile(1, 0));
    }

    @Test
    void userFinishShipboard() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userFinishShipboard(1));
    }

    @Test
    void userLookSmallAdventureCardDeck() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userLookSmallAdventureCardDeck(1, 0));
    }

    @Test
    void userStopLookSmallAdventureCardDeck() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userStopLookSmallAdventureCardDeck(1, 0));
    }

    @Test
    void userPlaceTimer() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userPlaceTimer(1));
    }

    @Test
    void timerIsDone() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.timerIsDone());
    }

    @Test
    void userSelectTypeOfToken() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userSelectTypeOfToken(1, Token.ASTRONAUT, new Point(0, 0)));
    }

    @Test
    void userThrowDices() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userThrowDices(1));
    }

    @Test
    void userDrawAdventureCard() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userDrawAdventureCard(1));
    }

    @Test
    void userFinishDecisionActivating() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userFinishDecisionActivating(1, new HashSet<>()));
    }

    @Test
    void userFinishDecisionToken() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userFinishDecisionToken(1, new ArrayList<>()));
    }

    @Test
    void userLandOnPlanet() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userLandOnPlanet(1, true, 0));
    }

    @Test
    void userLandOnStationOrShip() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userLandOnStationOrShip(1, true));
    }

    @Test
    void userAcceptPrizes() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userAcceptPrizes(1, true));
    }

    @Test
    void userStopFlight() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userStopFlight(1));
    }

    @Test
    void adminCalculateScore() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.adminCalculateScore(1));
    }

    @Test
    void userEndGame() {
        assertThrows(IllegalMethodOnThisStateException.class, () -> state.userEndGame(1));
    }
}