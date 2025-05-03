package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.server.RemoteClient;
import it.polimi.ingsw.is25am49.server.model.adventurecards.Planet;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientSerializer implements RemoteServer {
    private ServerHandler serverHandler;
    private BlockingQueue<File> filesFromSocketQueue;
    private final ExecutorService executors;

    public ClientSerializer() {
        filesFromSocketQueue = new ArrayBlockingQueue<>(1000, true);
        executors = Executors.newFixedThreadPool(20);
        executors.submit(this::processFilesFromSocketQueue);
    }

    private void processFilesFromSocketQueue() {    // TODO: implementare il metodo
        try {
            filesFromSocketQueue.take();
            // il file va ora deserializzato
        } catch (InterruptedException e) {
            System.out.println("Error while taking the message from the queue, thread has been interrupted");
        }
    }

    //IL SERIALIZER SE DEVE PASSARE UN FILE AL SERVERHANDLER GLIELO DEVE METTERE NELLA UA OUTGOINGQUEUE TRAMITE IL METODO APPOSIT DEL SERVERHANDLER.

    /**
     * This method is used to put files in the queue.
     * It is called by ServerHandler after receiving a message from server
     */
    public void putFileFromSocketInQueue(File file) {
        try {
            filesFromSocketQueue.put(file);
        } catch (InterruptedException e) {
            System.out.println("Error while putting the file in the filesFromSocketQueue, thread has been interrupted");
        }
    }

    public void placetile() {
        //traduci in un pacchetto serializzato
    }

    public void deSerializeStringFromServer(File fileJson) {
        //deserializza il pacchetto
    }

    public File serializeObjectFromClient(String method, List<Object> parameters) {
        //serializza il pacchetto
        return null;
    }

    @Override
    public void serverTestConnection(int playerId) throws RemoteException {
        System.out.println("no");
    }

    @Override
    public void registerClientRMI(RemoteClient client) throws RemoteException {

    }

    /*************************************************************************
     * THIS METHOD IS ONLY A PROTOTYPE, DOESNT FOLLOW THE SERIALIZING PROTOCOL
     * *******************************************************************
     *
     * This method is used to register the client socket on the server.
     * It creates a temporary file with the playerId and puts it in the outgoing queue.
     * @param playerId the id of the player
     * @throws RemoteException if there is an error during the remote call
     */
    @Override
    public void registerClientSocket(int playerId) throws RemoteException {
        try {
            // Crea un file temporaneo con il contenuto effettivo
            String jsonString = String.valueOf(playerId);
            File fileJson = new File("first_client_handshake.json");
            Files.writeString(fileJson.toPath(), jsonString);
            // Mette il file nella coda
            serverHandler.connectCommand();
            serverHandler.putInTheOutGoingFileQueue(fileJson);
        } catch (IOException e) {
            System.out.println("Errore durante la creazione del file di handshake: " + e.getMessage());
        }
    }

    @Override
    public void adminSetup(int playerId, int numberOfPlayers, GameMode mode) throws RemoteException {

    }

    @Override
    public void playerJoin(int playerId, String username, Color color) throws RemoteException {

    }

    @Override
    public void userDrawTile(int playerId, int tileId) throws RemoteException {

    }

    @Override
    public void userPlaceTile(int playerId, Point coordinates, Orientation orientation) throws RemoteException {

    }

    @Override
    public void userPickTile(int playerId, Point coordinates) throws RemoteException {

    }

    @Override
    public void userDiscardTile(int playerId) throws RemoteException {

    }

    @Override
    public void userFinishShipboard(int playerId) throws RemoteException {

    }

    @Override
    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) throws RemoteException {

    }

    @Override
    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) throws RemoteException {

    }

    @Override
    public void userPlaceTimer(int playerId) throws RemoteException {

    }

    @Override
    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) throws RemoteException {

    }

    @Override
    public void userThrowDices(int playerId) throws RemoteException {

    }

    @Override
    public void userDrawAdventureCard(int playerId) throws RemoteException {

    }

    @Override
    public void userFinishDecisionActivating(int playerId, Set<Point> coordinates) throws RemoteException {

    }

    @Override
    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) throws RemoteException {

    }

    @Override
    public void userAcceptPrizes(int playerId, boolean wantsRewards) throws RemoteException {

    }

    @Override
    public void userLandOnPlanet(int playerId, boolean isLanded, Planet planet) throws RemoteException {

    }

    @Override
    public void userLandOnStationOrShip(int playerId, boolean isLanded) throws RemoteException {

    }

    @Override
    public void userStopFlight(int playerId) throws RemoteException {

    }

    @Override
    public void adminCalculateScore(int playerId) throws RemoteException {

    }

    @Override
    public void userEndGame(int playerId) throws RemoteException {

    }

    public void setServerHandler(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }
}
