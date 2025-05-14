package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.server.CommandServer;
import it.polimi.ingsw.is25am49.server.RemoteClient;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.server.model.adventurecards.Planet;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Set;

public class ClientRMI implements RemoteServer {
    public GameSession gameSession;
    private Integer playerID;

    private boolean isConnected;

    public ClientRMI() {}
    public ClientRMI(GameSession session) {
        gameSession = session;
    }

    RemoteServer connect(String serverIP, int serverPort) throws RemoteException {
        RemoteServer serverStub = null;
        try {
            Registry reg = LocateRegistry.getRegistry(serverIP, 1099);//eventualmente serverPort
            serverStub = (RemoteServer) reg.lookup("ServerController");//nome serverController da cambiare
            System.out.println("Client successfully connected to ["+serverIP+" , 1099]");

        }catch(RemoteException | NotBoundException e){
            System.out.println("errore creazione stub --> "+e.getMessage());
        }
        return serverStub;
    }

    @Override
    public void sendCommand(CommandClient commandClient) throws RemoteException {

    }
}

