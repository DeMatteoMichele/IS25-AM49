package it.polimi.ingsw.is25am49.server;

import java.io.File;
import java.util.List;

public class Serializer {
    private final ServerController serverController;
    private NetworkLevel networkLevel;
    public Serializer(ServerController serverController) {
        this.serverController = serverController;
    }
    public void setNetworkLevel(NetworkLevel networkLevel) {
        this.networkLevel = networkLevel;
    }
    public void deSerializeStringFromClient(File fileJson) {
        //Qua chiamerò i corretti metodi con i corretti parametri su ControllerServer
        //Definire il Protocollo
    }
    public File SerializeObjectFromServer(String method, List<Object> parameters) {
        //Definire il Protocollo
        return null;
    }

}
