package it.polimi.ingsw.is25am49.server.model.states;

    import it.polimi.ingsw.is25am49.server.model.GameSession;
    import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisStateException;
    import it.polimi.ingsw.is25am49.shared.enums.GameMode;
    import it.polimi.ingsw.is25am49.shared.enums.Token;
    import org.junit.jupiter.api.Test;

    import java.awt.*;

    import static org.junit.jupiter.api.Assertions.assertThrows;
    import static org.junit.jupiter.api.Assertions.assertNull;
    import static org.mockito.Mockito.*;

    class AdventureStateTest {


        @Test
        void adminSelectGameMode_ThrowsException() {
            AdventureState state = new AdventureState(mock(GameSession.class));
            assertThrows(IllegalMethodOnThisStateException.class, () -> state.adminSelectGameMode(1, GameMode.DEFAULT));
        }

        @Test
        void userJoinLobby_ThrowsException() {
            AdventureState state = new AdventureState(mock(GameSession.class));
            assertThrows(IllegalMethodOnThisStateException.class, () -> state.userJoinLobby("pollo", 3));
        }


        @Test
        void adminCreateGame_ThrowsException() {
            AdventureState state = new AdventureState(mock(GameSession.class));
            assertThrows(IllegalMethodOnThisStateException.class, () -> state.adminCreateGame(1, GameMode.DEFAULT, 4));
        }

        @Test
        void adminStartGame_ThrowsException() {
            AdventureState state = new AdventureState(mock(GameSession.class));
            assertThrows(IllegalMethodOnThisStateException.class, () -> state.adminStartGame(1));
        }

        @Test
        void userPlaceTile_ThrowsException() {
            AdventureState state = new AdventureState(mock(GameSession.class));
            assertThrows(IllegalMethodOnThisStateException.class, () -> state.userPlaceTile(1, new Point(0, 0)));
        }

        @Test
        void userPickTile_ThrowsException() {
            AdventureState state = new AdventureState(mock(GameSession.class));
            assertThrows(IllegalMethodOnThisStateException.class, () -> state.userPickTile(1, new Point(0, 0)));
        }

        @Test
        void userDiscardTile_ThrowsException() {
            AdventureState state = new AdventureState(mock(GameSession.class));
            assertThrows(IllegalMethodOnThisStateException.class, () -> state.userDiscardTile(1));
        }

        @Test
        void userDrawTile_ThrowsException() {
            AdventureState state = new AdventureState(mock(GameSession.class));
            assertThrows(IllegalMethodOnThisStateException.class, () -> state.userDrawTile(1, 0));
        }

        @Test
        void userFinishShipboard_ThrowsException() {
            AdventureState state = new AdventureState(mock(GameSession.class));
            assertThrows(IllegalMethodOnThisStateException.class, () -> state.userFinishShipboard(1));
        }

        @Test
        void userLookSmallAdventureCardDeck_ThrowsException() {
            AdventureState state = new AdventureState(mock(GameSession.class));
            assertThrows(IllegalMethodOnThisStateException.class, () -> state.userLookSmallAdventureCardDeck(1, 0));
        }

        @Test
        void userStopLookSmallAdventureCardDeck_ThrowsException() {
            AdventureState state = new AdventureState(mock(GameSession.class));
            assertThrows(IllegalMethodOnThisStateException.class, () -> state.userStopLookSmallAdventureCardDeck(1, 0));
        }

        @Test
        void userPlaceTimer_ThrowsException() {
            AdventureState state = new AdventureState(mock(GameSession.class));
            assertThrows(IllegalMethodOnThisStateException.class, () -> state.userPlaceTimer(1));
        }

        @Test
        void timerIsDone_ThrowsException() {
            AdventureState state = new AdventureState(mock(GameSession.class));
            assertThrows(IllegalMethodOnThisStateException.class, state::timerIsDone);
        }

        @Test
        void userSelectTypeOfToken_ThrowsException() {
            AdventureState state = new AdventureState(mock(GameSession.class));
            assertThrows(IllegalMethodOnThisStateException.class, () -> state.userSelectTypeOfToken(1, Token.PURPLE_ALIENS, new Point(0, 0)));
        }

        @Test
        void adminCalculateScore_ThrowsException() {
            AdventureState state = new AdventureState(mock(GameSession.class));
            assertThrows(IllegalMethodOnThisStateException.class, () -> state.adminCalculateScore(1));
        }

        @Test
        void getCurrentPlayer_ReturnsNullIfNoGame() {
            GameSession mockSession = mock(GameSession.class);
            when(mockSession.getGame()).thenReturn(java.util.Optional.empty());
            AdventureState state = new AdventureState(mockSession);
            assertNull(state.getCurrentPlayer());
        }
    }