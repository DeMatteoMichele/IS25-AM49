package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import org.junit.jupiter.api.Test;

class ClientControllerTest {
    ClientController clientController;
    UserInterface userInterface;
    ClientSerializer clientSerializer;
    ServerHandler serverHandler;
    GameSession gameSession;

    void setUpSocket() {
        userInterface = new TextualUserInterface();
        gameSession = new GameSession(userInterface);
        clientController = new ClientController(gameSession, userInterface);
        clientSerializer = new ClientSerializer(clientController);
        serverHandler = new ServerHandler("localhost", 8080);
        serverHandler.setClientSerializer(clientSerializer);
        clientSerializer.setServerHandler(serverHandler);
        clientController.setServerMethods(clientSerializer);
        clientController.setServerHandler(serverHandler);
    }
    @Test
    void clientReset() {
        setUpSocket();
        // Controllato a mano, comunque da completare
        clientController.clientReset();

    }
}