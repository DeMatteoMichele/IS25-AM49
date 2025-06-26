package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServerControllerTest {
    ServerController controller;
    GameSession session;

    @BeforeEach
    void setUp() {
        session = new GameSession();

        controller = new ServerController(session);
        ServerSerializer serializer = new ServerSerializer(controller);
        controller.setSerializer(serializer);
        ClientHandler clientHandler = new ClientHandler(serializer,8081);
    }

    @Test
    void adminSetup() {
//
    }

    @Test
    void playerJoin() {
//
    }

    @Test
    void playerConnected() {

    }

    @Test
    void playerDisconnected() {

    }

    @Test
    void userDrawTile() {
    }

    @Test
    void userPlaceTile() {
    }

    @Test
    void userPickTile() {
    }

    @Test
    void userDiscardTile() {
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
    void userSelectTypeOfToken() {
    }

    @Test
    void userThrowDices() {
    }

    @Test
    void userDrawAdventureCard() {
    }

    @Test
    void userFinishDecisionActivating() {
    }

    @Test
    void userFinishDecisionToken() {
    }

    @Test
    void userAcceptPrizes() {
    }

    @Test
    void userLandOnPlanet() {
    }

    @Test
    void userLandOnStationOrShip() {
    }

    @Test
    void userStopFlight() {
    }

    @Test
    void changeState() {
    }

    @Test
    void adminCalculateScore() {
    }

    @Test
    void adminEndGame() {
    }

    @Test
    void setServerRMI() {
    }

    @Test
    void signalThatGameStarted() {
    }
}