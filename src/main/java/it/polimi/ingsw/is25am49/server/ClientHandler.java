package it.polimi.ingsw.is25am49.server;

import java.io.File;

public class ClientHandler {
    private final ServerSerializer serverSerializer;
    public ClientHandler(ServerSerializer serverSerializer) {
        this.serverSerializer = serverSerializer;
    }
    public void passInformationToServerController(File fileJson) {
        serverSerializer.deSerializeStringFromClient(fileJson);
    }
}
