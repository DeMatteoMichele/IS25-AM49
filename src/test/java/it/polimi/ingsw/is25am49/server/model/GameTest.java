package it.polimi.ingsw.is25am49.server.model;

import it.polimi.ingsw.is25am49.server.ServerController;
import it.polimi.ingsw.is25am49.server.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Comparator;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameTest {
    ServerController controller;
    GameSession session;
    Player player;
    Player player2;
    Game game;

    @BeforeEach
    void setUp() {
        player = new Player("admin", Color.BLUE, 1);
        player2 = new Player("pollo", Color.RED, 2);
        session = new GameSession();
        controller = new ServerController(session);
        session.setServerController(controller);
        session.userJoinLobby(player.getUsername(), player.getIdPlayer());
        session.createNewGame(GameMode.LEVEL_1, 2);
        session.userJoinLobby(player2.getUsername(), player2.getIdPlayer());
        game=session.getGame().get();

    }
    @Test
    void getMode() {
        // Test that getMode returns the correct GameMode
        GameSession session = mock(GameSession.class);

        Game gameLevel1 = new Game(new Player("admin", Color.BLUE, 1), session, GameMode.LEVEL_1, 4, 1234);
        assertEquals(GameMode.LEVEL_1, gameLevel1.getMode());

        Game gameFlightMode = new Game(new Player("admin", Color.BLUE, 1), session, GameMode.FLIGHT_MODE, 4, 1234);
        assertEquals(GameMode.FLIGHT_MODE, gameFlightMode.getMode());

        Game gameDefaultMode = new Game(new Player("admin", Color.BLUE, 1), session, GameMode.DEFAULT, 4, 1234);
        assertEquals(GameMode.DEFAULT, gameDefaultMode.getMode());
    }

    @Test
    void getPlayer() {
        assertEquals(player2.getUsername(), game.getPlayer(2).getUsername());
    }

    @Test
    void disconnectPlayer() {

        session.getGame().get().disconnectPlayer(1);
        assertFalse(game.getPlayer(1).isConnected());
    }

    @Test
    void connectPlayer() {

        game.connectPlayer(1);
        assertTrue(player.isConnected());

    }

    @Test
    void getGameboard() {
        Player player = new Player("admin", Color.BLUE, 1);
        GameSession session = new GameSession();
        Game game = new Game(player, session, GameMode.DEFAULT, 4, 1234);

        assertNotNull(game.getGameboard());
    }

    @Test
    void getMaxPlayers() {
        Player player = new Player("admin", Color.BLUE, 1);
        GameSession session = new GameSession();
        Game game = new Game(player, session, GameMode.DEFAULT, 4, 1234);

        assertEquals(4, game.getMaxPlayers());
    }

    @Test
    void numOfPlayers() {
        Player player = new Player("admin", Color.BLUE, 1);
        GameSession session = new GameSession();
        Game game = new Game(player, session, GameMode.DEFAULT, 4, 1234);

        assertEquals(1, game.numOfPlayers());
    }

    @Test
    void setGameMode() {
        // Test that setGameMode sets the game mode and initializes the timer.
        Player player = new Player("admin", Color.BLUE, 1);
        GameSession session = mock(GameSession.class);
        Game game = new Game(player, session, GameMode.DEFAULT, 4, 1234);

        game.setGameMode(GameMode.FLIGHT_MODE);
        assertEquals(GameMode.FLIGHT_MODE, game.getMode());
    }

    @Test
    void setTimer() {
        Player player = new Player("admin", Color.BLUE, 1);
        GameSession session = new GameSession();
        Game game = new Game(player, session, GameMode.DEFAULT, 4, 1234);
        Timer timer = new Timer(GameMode.DEFAULT, session);

        game.setTimer(timer);

        assertEquals(timer, game.getTimer());
    }

    @Test
    void checkPlayers() {
        // Test that checkPlayers returns true when max players haven't been reached.
        Player player = new Player("admin", Color.BLUE, 1);
        GameSession session = mock(GameSession.class);
        Game game = new Game(player, session, GameMode.DEFAULT, 4, 1234);

        assertTrue(game.checkPlayers()); // maxPlayers is 4, numPlayers is 1
    }

    @Test
    void addPlayer() {
        // Test that a player is successfully added to the game.
        Player player = new Player("newPlayer", Color.RED, 2);
        GameSession session = mock(GameSession.class);
        Game game = new Game(new Player("admin", Color.BLUE, 1), session, GameMode.LEVEL_1, 4, 1234);

        assertEquals(1, game.numOfPlayers()); // verify initial players
        game.addPlayer(player);
        assertEquals(2, game.numOfPlayers());
        assertTrue(game.getListPlayers().contains(player));
    }

    @Test
    void testAddPlayerTriggersGameStartWhenMaxPlayersReached() {
        // Test that game start is triggered when max players are reached.
        GameSession session = mock(GameSession.class);
        Game game = new Game(new Player("admin", Color.BLUE, 1), session, GameMode.LEVEL_1, 2, 1234);

        Player player2 = new Player("player2", Color.RED, 2);

        game.addPlayer(player2); // Adding second player should start the game.

        verify(session, times(1)).configurateFlight(); // Verify that configurateFlight was called
        verify(session, times(1)).signalToControllerThatGameStarted(); // Verify signal to controller is triggered
    }

    @Test
    void getListPlayers() {
        Player player = new Player("admin", Color.BLUE, 1);
        GameSession session = new GameSession();
        Game game = new Game(player, session, GameMode.DEFAULT, 4, 1234);

        assertTrue(game.getListPlayers().contains(player));
    }

    @Test
    void calculateScore() {
        game.getPlayer(1).addCredits(10);
        game.getPlayer(2).addCredits(20);

        game.calculateScore(game.getPlayer(1));

        assertEquals(game.getPlayer(2), game.getListPlayers().stream().max(Comparator.comparingInt(Player::getCredits)).get());
    }

    @Test
    void configurateFlight() {
    }

    @Test
    void gameIsFinished() {
        Player player = new Player("admin", Color.BLUE, 1);
        GameSession session = new GameSession();
        Game game = new Game(player, session, GameMode.DEFAULT, 4, 1234);

        assertFalse(game.gameIsFinished());

        game.finishGame();

        assertTrue(game.gameIsFinished());
    }

    @Test
    void finishGame() {
        // Test that finishGame marks the game as finished and calls session.finishGame().
        Player player = new Player("admin", Color.BLUE, 1);
        GameSession session = mock(GameSession.class);
        Game game = new Game(player, session, GameMode.DEFAULT, 4, 1234);

        game.finishGame();
        assertTrue(game.gameIsFinished());
        verify(session, times(1)).finishGame();
    }

    @Test
    void finishGameDueToDisconnection() {
        // Test that finishGameDueToDisconnection marks the game as finished and calls session.finishGameDueToDisconnection().
        Player player = new Player("admin", Color.BLUE, 1);
        GameSession session = mock(GameSession.class);
        Game game = new Game(player, session, GameMode.DEFAULT, 4, 1234);

        game.finishGameDueToDisconnection();
        assertTrue(game.gameIsFinished());
        verify(session, times(1)).finishGameDueToDisconnection();
    }

    @Test
    void isTestFlight() {
    }

    @Test
    void userPlaceTimer() {
    }

    @Test
    void discardTile() {
        Tile tile = new Tile(3,new ContainerComponent(ComponentType.CABINE,3));
        game.getPlayer(1).setHandTile(tile);
        assertThrows(NullPointerException.class,()->game.discardTile(game.getPlayer(1)));
        assertEquals(Optional.empty(), game.getPlayer(1).getHandTile());
    }

    @Test
    void userDrawTile() {
        Player player = new Player("admin", Color.BLUE, 1);
        GameSession session = new GameSession();
        Game game = new Game(player, session, GameMode.DEFAULT, 4, 1234);

        assertThrows(NullPointerException.class,()->game.userDrawTile(player, 0));

        assertNotNull(player.getHandTile());
    }

    @Test
    void userFinishShipboard() {
        game.getPlayer(1).setShipBoard(new ShipBoard(1,1,Color.BLUE));
        boolean result = game.userFinishShipboard(game.getPlayer(1));

        assertFalse(result);
    }

    @Test
    void userLookSmallAdventureCardDeck() {
    }

    @Test
    void userStopLookSmallAdventureCardDeck() {
    }

    @Test
    void addPlayersToLeaderBoard() {

        game.addPlayersToLeaderBoard();
        assertTrue(game.getGameboard().getLeader().equals(game.getPlayer(1)));
    }

    @Test
    void userLandOnPlanet() {
    }

    @Test
    void userFinishDecisionToken() {
    }

    @Test
    void userLandOnStationOrShip() {
    }

    @Test
    void userAcceptPrizes() {
    }

    @Test
    void userFinishDecisionActivating() {
    }

    @Test
    void userStopFlight() {
    }

    @Test
    void mergeSmallDecks() {

        game.mergeSmallDecks();

        assertTrue( game.getGameboard().getMainAdventureDeck().size()>0);
    }

    @Test
    void userDrawAdventureCard() {
    }

    @Test
    void userThrowDices() {
    }

    @Test
    void userSelectTypeOfToken() {
        Point coordinates = new Point(7, 8);
        game.getPlayer(1).setShipBoard(new ShipBoard(1,1,Color.BLUE));
        game.getPlayer(1).getShipBoard().insertTile(coordinates, new Tile(3,new ContainerComponent(ComponentType.CABINE,3)));

        boolean result = game.userSelectTypeOfToken(game.getPlayer(1), Token.ASTRONAUT, coordinates);

        assertTrue(result);
        assertEquals(3, game.getPlayer(1).getShipBoard().getTile(coordinates).getComponent().getNumberOfaToken(Token.ASTRONAUT));
    }

    @Test
    void checkIfShipboardsAreFullOfToken() {

        boolean result = game.checkIfShipboardsAreFullOfToken();

        assertTrue(result);
    }
}