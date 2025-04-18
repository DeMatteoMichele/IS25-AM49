package it.polimi.ingsw.is25am49.client;

import java.io.File;
import java.rmi.RemoteException;
import java.util.List;

public class ClientSerializer implements RemoteServerMethods {
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
    public void serverTestConnection() throws RemoteException {
        System.out.println("no");
    }
}
