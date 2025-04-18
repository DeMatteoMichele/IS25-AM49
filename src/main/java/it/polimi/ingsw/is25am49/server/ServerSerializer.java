package it.polimi.ingsw.is25am49.server;

import java.io.File;
import java.util.List;

public class ServerSerializer implements RemoteClientMethods {
    private final ServerController serverController;
    private ClientHandler clientHandler;
    public ServerSerializer(ServerController serverController) {
        this.serverController = serverController;
    }
    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }
    public void deSerializeStringFromClient(File fileJson) {
        //Qua chiamerò i corretti metodi con i corretti parametri su ControllerServer
        //Definire il Protocollo
    }
    public File SerializeObjectFromServer(String method, List<Object> parameters) {
        //Definire il Protocollo
        return null;
    }

    @Override
    public void clientTestConnection() {
        System.out.println("c");
    }
}
