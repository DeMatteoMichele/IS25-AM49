package it.polimi.ingsw.is25am49.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.polimi.ingsw.is25am49.server.RemoteClient;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.SerializationObject;
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
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientSerializer implements RemoteServer {
    private ClientController clientController;
    private ServerHandler serverHandler;
    private BlockingQueue<File> filesFromSocketQueue;
    private final ExecutorService executors;
    private GameSession gameSession;

    public ClientSerializer(GameSession session) {
        this.filesFromSocketQueue = new ArrayBlockingQueue<>(1000,true);
        executors = Executors.newFixedThreadPool(20);
        executors.submit(this::processFilesFromSocketQueue);
        gameSession = session;
        this.clientController = null;
    }

    public ClientSerializer(ClientController clientController) {
        this.filesFromSocketQueue = new ArrayBlockingQueue<>(1000,true);
        executors = Executors.newFixedThreadPool(20);
        executors.submit(this::processFilesFromSocketQueue);
        gameSession =  null;
        this.clientController = clientController;
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
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(fileJson);
            String typeOfMessage = rootNode.get("typeOfMessage").asText();
            if (typeOfMessage.equals("MSG")) {
                String methodName = rootNode.get("methodName").asText();
                it.polimi.ingsw.is25am49.server.SerializationObject serObject = new it.polimi.ingsw.is25am49.server.SerializationObject(methodName, clientController);
                JsonNode params = rootNode.get("params");
                for (JsonNode paramNode : params) {
                    ArrayList<Object> collectionList = null;
                    HashSet<Object> collectionSet = null;
                    Object object = null;
                    String typeOfCollection = paramNode.get("typeOfCollection").asText();
                    Object collection;
                    collection = switch (typeOfCollection) {
                        case "Set" -> new HashSet<>();
                        case "List" -> new ArrayList<>();
                        default -> new Object();
                    };
                    if (collection instanceof ArrayList) {
                        collectionList = (ArrayList<Object>) collection;
                        JsonNode content = paramNode.get("content");
                        for (JsonNode contentNode : content) {
                            String className = contentNode.get("className").asText();
                            String contentObject = contentNode.get("contentObject").asText();
                            Object value = switch (className) {
                                case "Pair" -> mapper.readValue(contentObject, Pair.class);
                                case "Token" -> mapper.readValue(contentObject, Token.class);
                                case "Color" -> mapper.readValue(contentObject, Color.class);
                                case "int" -> mapper.readValue(contentObject, int.class);
                                case "GameMode" -> mapper.readValue(contentObject, GameMode.class);
                                case "String" -> mapper.readValue(contentObject, String.class);
                                case "Point" -> mapper.readValue(contentObject, Point.class);
                                case "Orientation" -> mapper.readValue(contentObject, Orientation.class);
                                case "boolean" -> mapper.readValue(contentObject, boolean.class);
                                default -> new Object();
                            };
                            collectionList.add(value);
                        }
                        serObject.addParameter(collectionList);
                    } else if (collection instanceof HashSet) {
                        collectionSet = (HashSet<Object>) collection;
                        JsonNode content = paramNode.get("content");
                        for (JsonNode contentNode : content) {
                            String className = contentNode.get("className").asText();
                            String contentObject = contentNode.get("contentObject").asText();
                            Object value = switch (className) {
                                case "Pair" -> mapper.readValue(contentObject, Pair.class);
                                case "Token" -> mapper.readValue(contentObject, Token.class);
                                case "Color" -> mapper.readValue(contentObject, Color.class);
                                case "int" -> mapper.readValue(contentObject, int.class);
                                case "GameMode" -> mapper.readValue(contentObject, GameMode.class);
                                case "String" -> mapper.readValue(contentObject, String.class);
                                case "Point" -> mapper.readValue(contentObject, Point.class);
                                case "Orientation" -> mapper.readValue(contentObject, Orientation.class);
                                case "boolean" -> mapper.readValue(contentObject, boolean.class);
                                default -> new Object();
                            };
                            collectionSet.add(value);
                        }
                        serObject.addParameter(collectionSet);
                    } else {
                        object = collection;
                        JsonNode content = paramNode.get("content");
                        for (JsonNode contentNode : content) {
                            String className = contentNode.get("className").asText();
                            String contentObject = contentNode.get("contentObject").asText();
                            object = switch (className) {
                                case "Pair" -> mapper.readValue(contentObject, Pair.class);
                                case "Token" -> mapper.readValue(contentObject, Token.class);
                                case "Color" -> mapper.readValue(contentObject, Color.class);
                                case "int" -> mapper.readValue(contentObject, int.class);
                                case "GameMode" -> mapper.readValue(contentObject, GameMode.class);
                                case "String" -> mapper.readValue(contentObject, String.class);
                                case "Point" -> mapper.readValue(contentObject, Point.class);
                                case "Orientation" -> mapper.readValue(contentObject, Orientation.class);
                                case "boolean" -> mapper.readValue(contentObject, boolean.class);
                                default -> new Object();
                            };
                        }
                        serObject.addParameter(object);
                    }
                }
                serObject.callClientMethod();
            }
            // HandShake ?, HeartBeat ?, dipende dalla gestione nel Layer di Rete
            else {

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void serializeObjectFromClient(SerializationObject serObject) {
        File jsonFile = new File("commandToServer.json");
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        ArrayNode params = mapper.createArrayNode();
        rootNode.put("typeOfMessage", "MSG");
        rootNode.put("methodName", serObject.getMethodName());
        rootNode.set("params", params);
        for (Object param : serObject.getParameters()) {
            ObjectNode paramNode = mapper.createObjectNode();
            if (param instanceof HashSet) {
                ArrayNode contentNode = mapper.createArrayNode();
                paramNode.put("typeOfCollection", "Set");
                paramNode.set("content", contentNode);
                HashSet set = (HashSet) param;
                for (Object elem : set) {
                    ObjectNode elemNode = mapper.createObjectNode();
                    ObjectNode valueNode = mapper.valueToTree(elem);
                    elemNode.put("className", elem.getClass().getName());
                    elemNode.set("content", valueNode);
                    contentNode.add(elemNode);
                }
            } else if (param instanceof ArrayList) {
                ArrayNode contentNode = mapper.createArrayNode();
                paramNode.put("typeOfCollection", "List");
                paramNode.set("content", contentNode);
                ArrayList list = (ArrayList) param;
                for (Object elem : list) {
                    ObjectNode elemNode = mapper.createObjectNode();
                    ObjectNode valueNode = mapper.valueToTree(elem);
                    elemNode.put("className", elem.getClass().getName());
                    elemNode.set("content", valueNode);
                    contentNode.add(elemNode);
                }
            } else {
                ObjectNode contentNode = mapper.createObjectNode();
                paramNode.put("typeOfCollection", "");
                contentNode.put("className", param.getClass().getName());
                ObjectNode valueNode = mapper.valueToTree(param);
                contentNode.set("content", valueNode);
                paramNode.set("content", contentNode);
            }
            params.add(paramNode);
        }
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, rootNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        serverHandler.putInTheOutGoingFileQueue(jsonFile);
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
    public void userFinishDecisionActivating(int playerId, HashSet<Point> coordinates) throws RemoteException {

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
