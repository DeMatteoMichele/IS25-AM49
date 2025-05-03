package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.server.model.adventurecards.Planet;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ServerSerializer implements RemoteClient {
    private final ServerController serverController;
    private ClientHandler clientHandler;
    private final BlockingQueue<File> filesFromSocketQueue;
    private int i = -1;


    public ServerSerializer(ServerController serverController) {
        this.serverController = serverController;
        filesFromSocketQueue = new ArrayBlockingQueue<File>(4000, true);

    }

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void deSerializeStringFromClient(File fileJson) {
        //Qua chiamerò i corretti metodi con i corretti parametri su ControllerServer
        //Definire il Protocollo
    }
    public File serializeObjectFromServer(String method, List<Object> parameters) {
        //Definire il Protocollo
        return null;
    }

    @Override
    public void notifyClient(String msg) throws RemoteException {

    }

    @Override
    public void registerClientCallback(RemoteClient client, int playerId) throws RemoteException {

    }

    @Override
    public void playerJoinCallback(int playerId) throws RemoteException {

    }

    @Override
    public void gameStartedCallback() throws RemoteException {

    }

    @Override
    public void userDrawTileCallback(int playerId, int tileId) throws RemoteException {

    }

    @Override
    public void userErrorDrawTileCallback(int playerId, int tileId) throws RemoteException {

    }

    @Override
    public void userPlaceTileCallback(int playerId, Point coordinates, Orientation orientation) throws RemoteException {

    }

    @Override
    public void userPickTileCallback(int playerId, Point coordinates) throws RemoteException {

    }

    @Override
    public void userDiscardTileCallback(int playerId) throws RemoteException {

    }

    @Override
    public void userFinishShipboardCallback(int playerId) throws RemoteException {

    }

    @Override
    public void userLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) throws RemoteException {

    }

    @Override
    public void userErrorLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) throws RemoteException {

    }

    @Override
    public void userStopLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) throws RemoteException {

    }

    @Override
    public void userPlaceTimerCallback(int playerId) throws RemoteException {

    }

    @Override
    public void userErrorPlaceTimerCallback(int playerId) throws RemoteException {

    }

    @Override
    public void userSelectTypeOfTokenCallback(int playerId, Token token, Point coordinates) throws RemoteException {

    }

    @Override
    public void userThrowDicesCallback(int playerId, int result) throws RemoteException {

    }

    @Override
    public void userDrawAdventureCardCallback(int playerId) throws RemoteException {

    }

    @Override
    public void userFinishDecisionActivatingCallback(int playerId, Set<Point> coordinates) throws RemoteException {

    }

    @Override
    public void userFinishDecisionTokenCallback(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) throws RemoteException {

    }

    @Override
    public void userAcceptPrizesCallback(int playerId) throws RemoteException {

    }

    @Override
    public void userLandOnPlanetCallback(int playerId, boolean isLanded, Planet planet) throws RemoteException {

    }

    @Override
    public void userLandOnStationOrShipCallback(int playerId, boolean isLanded) throws RemoteException {

    }

    @Override
    public void userStopFlightCallback(int playerId) throws RemoteException {

    }

    @Override
    public void adminCalculateScoreCallback(int playerId) throws RemoteException {

    }

    @Override
    public void userEndGameCallback(int playerId) throws RemoteException {

    }

    @Override
    public void changeState(String state) throws RemoteException {

    }

    public void putFileFromClientSocketInQueue(File fileJson) {
        try {
            filesFromSocketQueue.put(fileJson);
        } catch (InterruptedException e) {
            System.err.println("Error putting file from socket in queue in serializer cause thread was interrupted: " + e.getMessage());
        }
    }

    public int deserializeHandshake(File fileJson) {
        // ritorna id del client e mette il pacchetto nella coda outgoing unicast
        try {
            String stringa = java.nio.file.Files.readString(fileJson.toPath()).trim();
            int clientId = Integer.parseInt(stringa);
            if (clientId == -1) {
                i++;
                clientId = i;
            }
            String stringResponse = String.valueOf(clientId);
            File fileJsonResponse = new File("handshake_response.json");
            Files.writeString(fileJsonResponse.toPath(), stringResponse);
            clientHandler.putFileInTheOutGoingFileQueue(fileJsonResponse);
            return clientId;
        } catch (Exception e) {
            System.err.println("Errore nella deserializzazione dell'handshake: " + e.getMessage());
            return -1;
        }
    }
}
