package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.server.RemoteClient;

import java.io.File;
import java.rmi.RemoteException;
import java.util.List;

public class ClientSerializer implements RemoteServer {
    private ServerHandler serverHandler;
    public void placetile(){
        //traduci in un pacchetto serializzato
    }

    public void deSerializeStringFromServer(File fileJson){
        //deserializza il pacchetto
    }

    public File serializeObjectFromClient(String method, List<Object> parameters){
        //serializza il pacchetto
        return null;
    }

    @Override
    public void serverTestConnection(Integer playerId) throws RemoteException {
        System.out.println("no");
    }

    @Override
    public void registerClient(RemoteClient client, Integer playerId) throws RemoteException {
        //MICHELE E' UN GAY DEL CAZZO
    }
}
