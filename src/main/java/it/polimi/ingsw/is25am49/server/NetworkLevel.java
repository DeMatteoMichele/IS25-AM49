package it.polimi.ingsw.is25am49.server;

import java.io.File;

public class NetworkLevel {
    private final Serializer serializer;
    //Boh
    public NetworkLevel(Serializer serializer) {
        this.serializer = serializer;
    }
    public void passInformationToServerController(File fileJson) {
        serializer.deSerializeStringFromClient(fileJson);
    }
}
