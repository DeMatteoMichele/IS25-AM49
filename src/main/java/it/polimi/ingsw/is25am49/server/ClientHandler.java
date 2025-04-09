package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.server.model.GameSession;

import java.io.File;

public class ClientHandler {
    private final ServerSerializer serverSerializer;
    public ClientHandler(ServerSerializer serverSerializer) {
        this.serverSerializer = serverSerializer;
    }

    public void passInformationToServerSerializer(File fileJson) {
        serverSerializer.deSerializeStringFromClient(fileJson);
    }
    public void passInformationToServerHandler(File fileJson) {

    }
    public void main(String[] args) {
        GameSession gameSession = new GameSession();
        ServerController serverController = new ServerController(gameSession);
        ServerSerializer serverSerializer = new ServerSerializer(serverController);
        ClientHandler clientHandler = new ClientHandler(serverSerializer);
        serverSerializer.setClientHandler(clientHandler);
        serverController.setSerializer(serverSerializer);
        gameSession.setView(serverSerializer);
    }
}
