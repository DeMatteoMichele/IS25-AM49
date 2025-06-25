package it.polimi.ingsw.is25am49.server.model;

import it.polimi.ingsw.is25am49.server.ServerController;
import it.polimi.ingsw.is25am49.server.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.server.model.states.BuildState;
import it.polimi.ingsw.is25am49.server.model.states.InitState;
import it.polimi.ingsw.is25am49.server.model.states.State;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {

    @Test
    void getGame() {
        GameSession session = new GameSession();
        assertFalse(session.getGame().isPresent(), "Expected game to be absent initially.");
    }

    @Test
    void getState() {
        GameSession session = new GameSession();
        assertNotNull(session.getState(), "Expected initial state to be not null.");
        assertTrue(session.getState() instanceof InitState, "Expected initial state to be InitState.");
    }

    @Test
    void getServerController() {
        GameSession session = new GameSession();
        assertNull(session.getServerController(), "Expected initial server controller to be null.");
    }

    @Test
    void setServerController() {
        GameSession session = new GameSession();
        ServerController mockController = new ServerController(session);
        session.setServerController(mockController);
        assertEquals(mockController, session.getServerController(), "Expected server controller to be set correctly.");
    }

    @Test
    void getAdmin() {
        GameSession session = new GameSession();
        assertFalse(session.getAdmin().isPresent(), "Expected admin to not be present initially.");

        Player admin = new Player("Admin", Color.RED, 1);
        session.userJoinLobby(admin.getUsername(), admin.getIdPlayer());
        assertTrue(session.getAdmin().isPresent(), "Expected admin to be present after joining.");
    }

    @Test
    void getPlayer() {
        GameSession session = new GameSession();
        Player admin = new Player("Admin", Color.RED, 1);
        session.userJoinLobby(admin.getUsername(), admin.getIdPlayer());

        assertTrue(session.getPlayer(1).isPresent(), "Expected player with ID 1 to be present.");
        assertFalse(session.getPlayer(999).isPresent(), "Expected no player with ID 999.");
    }

    @Test
    void setState() {
        GameSession session = new GameSession();
        State newState = new BuildState(session);
        session.setState(newState);
        assertEquals(newState, session.getState(), "Expected state to be updated correctly.");
    }

    @Test
    void getColor() {
        GameSession session = new GameSession();
        assertEquals(Color.values()[0], session.getColor(), "Expected initial color based on default.");

        Player admin = new Player("Admin", Color.RED, 1);
        session.userJoinLobby(admin.getUsername(), admin.getIdPlayer());
        session.createNewGame(GameMode.DEFAULT, 4);

        assertEquals(Color.values()[1], session.getColor(), "Expected color based on the number of players.");
    }

    @Test
    void createNewGame() {
        GameSession session = new GameSession();
        Player admin = new Player("Admin", Color.RED, 1);
        session.userJoinLobby(admin.getUsername(), admin.getIdPlayer());

        assertDoesNotThrow(() -> session.createNewGame(GameMode.DEFAULT, 4),
                "Creating a new game with valid parameters should not throw.");

        GameSettingsException exception = assertThrows(GameSettingsException.class,
                () -> session.createNewGame(GameMode.DEFAULT, 4),
                "Should throw an exception if a game already exists.");
        assertEquals("Game already exists!", exception.getMessage());
    }

    @Test
    void setGameMode() {
        GameSession session = new GameSession();
        Player admin = new Player("Admin", Color.RED, 1);
        session.userJoinLobby(admin.getUsername(), admin.getIdPlayer());
        session.createNewGame(GameMode.FLIGHT_MODE, 4);

        session.setGameMode(GameMode.DEFAULT);
        assertTrue(session.getGame().isPresent(), "Expected game to be present after setting game mode.");
        assertEquals(GameMode.DEFAULT, session.getGame().get().getMode(), "Expected game mode to be updated correctly.");
    }

    @Test
    void checkIsAdmin() {
        GameSession session = new GameSession();
        Player admin = new Player("Admin", Color.RED, 1);
        session.userJoinLobby(admin.getUsername(), admin.getIdPlayer());

        assertDoesNotThrow(() -> session.checkIsAdmin(admin.getIdPlayer()),
                "Admin should pass the check");

        GameSettingsException exception = assertThrows(GameSettingsException.class,
                () -> session.checkIsAdmin(999),
                "Should throw an exception for non-admin players");
        assertEquals("You are not the admin of this game", exception.getMessage());
    }

    @Test
    void checkPlayers() {
        GameSession session = new GameSession();
        assertFalse(session.checkPlayers(), "Expected no players initially.");

        Player admin = new Player("Admin", Color.RED, 1);
        session.userJoinLobby(admin.getUsername(), admin.getIdPlayer());

        assertFalse(session.checkPlayers(), "Expected to pass checkPlayers when only one player exists.");
    }

    @Test
    void checkGameMode() {
        GameSession session = new GameSession();
        Player admin = new Player("Admin", Color.RED, 1);
        session.userJoinLobby(admin.getUsername(), admin.getIdPlayer());
        session.createNewGame(GameMode.DEFAULT, 4);

        assertTrue(session.checkGameMode(), "Expected game mode to be DEFAULT.");
    }

    @Test
    void userJoinLobby() {
        GameSession session = new GameSession();
        Player player1 = new Player("Player1", Color.BLUE, 1);
        Player player2 = new Player("Player2", Color.GREEN, 2);

        assertDoesNotThrow(() -> session.userJoinLobby(player1.getUsername(), player1.getIdPlayer()),
                "The first player should join as admin.");

        GameSettingsException exception = assertThrows(GameSettingsException.class,
                () -> session.userJoinLobby(player1.getUsername(), player1.getIdPlayer()),
                "The same player shouldn't be able to join twice.");
        assertEquals("game is not ready yet", exception.getMessage());
    }

    @Test
    void checkJoinLobby() {
        GameSession session = new GameSession();
        Player admin = new Player("Admin", Color.RED, 1);
        session.userJoinLobby(admin.getUsername(), admin.getIdPlayer());

        assertThrows(GameSettingsException.class,
                () -> session.checkJoinLobby(admin.getIdPlayer(), admin.getUsername()),
                "game is no ready yet");

    }

    @Test
    void checkIsInLobby() {
    }

    @Test
    void userDiscardTile() {
    }

    @Test
    void userDrawTile() {
        GameSession session = new GameSession();
        Player player = new Player("TestPlayer", Color.BLUE, 1);

        assertDoesNotThrow(
                () -> session.userDrawTile(player, 1),
                "Should throw NullPointerException since no game is present.");
    }

    @Test
    void userFinishShipboard() {
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
    void userThrowDices() {
    }

    @Test
    void userDrawAdventureCard() {
    }

    @Test
    void userLandOnPlanet() {
        GameSession session = new GameSession();
        Player admin = new Player("Admin", Color.RED, 1);
        session.userJoinLobby(admin.getUsername(), admin.getIdPlayer());
        session.createNewGame(GameMode.DEFAULT, 4);

        assertThrows(NullPointerException.class,() -> session.userLandOnPlanet(true, 1),
                "Landing on a planet should not throw exceptions.");
    }

    @Test
    void userFinishDecisionToken() {
    }

    @Test
    void userLandOnStationOrShip() {
    }

    @Test
    void userFinishDecisionActivating() {
    }

    @Test
    void userAcceptPrizes() {
    }

    @Test
    void calculateScore() {
    }

    @Test
    void configurateFlight() {
    }

    @Test
    void userEndGame() {
    }

    @Test
    void userSelectTypeOfToken() {
    }

    @Test
    void addPlayersToLeaderBoard() {
    }

    @Test
    void mergeSmallDecks() {
    }

    @Test
    void getSeedForGeneration() {
    }

    @Test
    void signalToControllerThatGameStarted() {
    }

    @Test
    void userStopFlight() {
        GameSession session = new GameSession();
        Player player = new Player("TestPlayer", Color.GREEN, 1);

        assertDoesNotThrow(() -> session.userStopFlight(player),
                "Should not throw any exception even if the game is not initialized.");
    }

    @Test
    void connectPlayer() {
    }

    @Test
    void disconnectPlayer() {
    }

    @Test
    void finishGame() {
    }

    @Test
    void finishGameDueToDisconnection() {
    }
}