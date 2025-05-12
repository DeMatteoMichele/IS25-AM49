package it.polimi.ingsw.is25am49.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import it.polimi.ingsw.is25am49.client.PairMixIn;
import it.polimi.ingsw.is25am49.server.RemoteClient;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.adventurecards.Planet;
import it.polimi.ingsw.is25am49.client.model.enums.Color;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.enums.Orientation;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
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

    public ClientSerializer(GameSession session, ClientController clientController) {
        this.filesFromSocketQueue = new ArrayBlockingQueue<>(1000, true);
        executors = Executors.newFixedThreadPool(20);
        executors.submit(this::processFilesFromSocketQueue);
        gameSession = session;
        this.clientController = clientController;
    }

    public ClientSerializer(ClientController clientController) {
        this.filesFromSocketQueue = new ArrayBlockingQueue<>(1000, true);
        executors = Executors.newFixedThreadPool(20);
        executors.submit(this::processFilesFromSocketQueue);
        gameSession = null;
        this.clientController = clientController;
    }

    private void processFilesFromSocketQueue() {
        while (true) {
            try {
                File json = filesFromSocketQueue.take();
                deSerializeStringFromServer(json);
            } catch (InterruptedException e) {
                System.out.println("Error while taking the message from the queue, thread has been interrupted");
            }
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

    public SerializationObjectClient deSerializeStringFromServer(File fileJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.addMixIn(Pair.class, it.polimi.ingsw.is25am49.client.PairMixIn.class);
            JsonNode rootNode = mapper.readTree(fileJson);
            String typeOfMessage = rootNode.get("typeOfMessage").asText();
            String methodName = rootNode.get("methodName").asText();
            SerializationObjectClient serObject = new SerializationObjectClient(methodName, clientController);
            ArrayNode params = (ArrayNode) rootNode.get("params");
            if (typeOfMessage.equals("MSG")) {
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
                        ArrayNode content = (ArrayNode) paramNode.get("content");
                        for (JsonNode contentNode : content) {
                            String className = contentNode.get("className").asText();
                            Object value = switch (className) {
                                case "javafx.util.Pair" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Pair.class);
                                }
                                case "it.polimi.ingsw.is25am49.client.model.enums.Token" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.Token.class);

                                }
                                case "it.polimi.ingsw.is25am49.client.model.enums.Color" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.Color.class);
                                }
                                case "java.lang.Integer" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield contentObject.asInt();
                                }
                                case "it.polimi.ingsw.is25am49.client.model.enums.GameMode" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.GameMode.class);
                                }
                                case "java.lang.String" -> {
                                    JsonNode contentObject = content.get("content");
                                    yield contentObject.asText();
                                }
                                case "java.awt.Point" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Point.class);
                                }
                                case "it.polimi.ingsw.is25am49.client.model.enums.Orientation" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.Orientation.class);
                                }
                                case "java.lang.Boolean" -> {
                                    JsonNode contentObject = content.get("content");
                                    yield contentObject.asBoolean();
                                }
                                default -> new Object();
                            };
                            collectionList.add(value);
                        }
                        serObject.addParameter(collectionList);
                    } else if (collection instanceof HashSet) {
                        collectionSet = (HashSet<Object>) collection;
                        ArrayNode content = (ArrayNode) paramNode.get("content");
                        for (JsonNode contentNode : content) {
                            String className = contentNode.get("className").asText();
                            Object value = switch (className) {
                                case "javafx.util.Pair" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Pair.class);
                                }
                                case "it.polimi.ingsw.is25am49.client.model.enums.Token" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.Token.class);

                                }
                                case "it.polimi.ingsw.is25am49.client.model.enums.Color" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.Color.class);
                                }
                                case "java.lang.Integer" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield contentObject.asInt();
                                }
                                case "it.polimi.ingsw.is25am49.client.model.enums.GameMode" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.GameMode.class);
                                }
                                case "java.lang.String" -> {
                                    JsonNode contentObject = content.get("content");
                                    yield contentObject.asText();
                                }
                                case "java.awt.Point" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Point.class);
                                }
                                case "it.polimi.ingsw.is25am49.client.model.enums.Orientation" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.Orientation.class);
                                }
                                case "java.lang.Boolean" -> {
                                    JsonNode contentObject = content.get("content");
                                    yield contentObject.asBoolean();
                                }
                                default -> new Object();
                            };
                            collectionSet.add(value);
                        }
                        serObject.addParameter(collectionSet);
                    } else {
                        object = collection;
                        JsonNode content = paramNode.get("content");
                        String className = content.get("className").asText();
                        object = switch (className) {
                            case "javafx.util.Pair" -> {
                                JsonNode contentObject = content.get("content");
                                yield mapper.readValue(contentObject.traverse(), Pair.class);
                            }
                            case "it.polimi.ingsw.is25am49.client.model.enums.Token" -> {
                                JsonNode contentObject = content.get("content");
                                yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.Token.class);

                            }
                            case "it.polimi.ingsw.is25am49.client.model.enums.color" -> {
                                JsonNode contentObject = content.get("content");
                                yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.Color.class);
                            }
                            case "java.lang.Integer" -> {
                                JsonNode contentObject = content.get("content");
                                yield contentObject.asInt();
                            }
                            case "it.polimi.ingsw.is25am49.client.model.enums.GameMode" -> {
                                JsonNode contentObject = content.get("content");
                                yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.GameMode.class);
                            }
                            case "java.lang.String" -> {
                                JsonNode contentObject = content.get("content");
                                yield contentObject.asText();
                            }
                            case "java.awt.Point" -> {
                                JsonNode contentObject = content.get("content");
                                yield mapper.readValue(contentObject.traverse(), Point.class);
                            }
                            case "it.polimi.ingsw.is25am49.client.model.enums.Orientation" -> {
                                JsonNode contentObject = content.get("content");
                                yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.Orientation.class);
                            }
                            case "java.lang.Boolean" -> {
                                JsonNode contentObject = content.get("content");
                                yield contentObject.asBoolean();
                            }
                            default -> new Object();
                        };
                        serObject.addParameter(object);
                    }
                }
            } else if (typeOfMessage.equals("HSK")) {
                for (JsonNode paramNode : params) {
                    String typeOfCollection = paramNode.get("typeOfCollection").asText();
                    int idPlayer = paramNode.get("content").get("content").asInt();
                    serObject.addParameter(idPlayer);
                }
            }
            serObject.callClientMethod();
            return serObject;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File serializeObjectFromClient(SerializationObjectClient serObject) {
        File jsonFile = new File("commandToClient.json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(Pair.class, PairMixIn.class);
        ObjectNode rootNode = mapper.createObjectNode();
        ArrayNode params = mapper.createArrayNode();
        rootNode.put("typeOfMessage", serObject.getTypeOfMessage());
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
                    String className = elem.getClass().getName();
                    elemNode.put("className", className);
                    if (className.equals("java.lang.Integer")) {
                        int intElem = (int) elem;
                        elemNode.put("content", intElem);
                    } else if (className.equals("java.lang.Boolean")) {
                        boolean boolElem = (boolean) elem;
                        elemNode.put("content", boolElem);
                    } else if (className.equals("java.lang.String")) {
                        String stringElem = (String) elem;
                        elemNode.put("content", stringElem);
                    } else {
                        if (className.contains("it.polimi.ingsw.is25am49.client.model.enums")) {
                            TextNode valueNode = mapper.valueToTree(elem);
                            elemNode.set("content", valueNode);
                        } else {
                            ObjectNode valueNode = mapper.valueToTree(elem);
                            elemNode.set("content", valueNode);
                        }
                    }
                    contentNode.add(elemNode);
                }
            } else if (param instanceof ArrayList) {
                ArrayNode contentNode = mapper.createArrayNode();
                paramNode.put("typeOfCollection", "List");
                paramNode.set("content", contentNode);
                ArrayList list = (ArrayList) param;
                for (Object elem : list) {
                    ObjectNode elemNode = mapper.createObjectNode();
                    String className = elem.getClass().getName();
                    elemNode.put("className", className);
                    if (className.equals("java.lang.Integer")) {
                        int intElem = (int) elem;
                        elemNode.put("content", intElem);
                    } else if (className.equals("java.lang.Boolean")) {
                        boolean boolElem = (boolean) elem;
                        elemNode.put("content", boolElem);
                    } else if (className.equals("java.lang.String")) {
                        String stringElem = (String) elem;
                        elemNode.put("content", stringElem);
                    } else {
                        if (className.contains("it.polimi.ingsw.is25am49.client.model.enums")) {
                            TextNode valueNode = mapper.valueToTree(elem);
                            elemNode.set("content", valueNode);
                        } else {
                            ObjectNode valueNode = mapper.valueToTree(elem);
                            elemNode.set("content", valueNode);
                        }
                    }
                    contentNode.add(elemNode);
                }
            } else {
                ObjectNode contentNode = mapper.createObjectNode();
                paramNode.put("typeOfCollection", "");
                String className = param.getClass().getName();
                contentNode.put("className", className);
                if (className.equals("java.lang.Integer")) {
                    int intParam = (int) param;
                    contentNode.put("content", intParam);
                } else if (className.equals("java.lang.Boolean")) {
                    boolean boolParam = (boolean) param;
                    contentNode.put("content", boolParam);
                } else if (className.equals("java.lang.String")) {
                    String stringParam = (String) param;
                    contentNode.put("content", stringParam);
                } else {
                    if (className.contains("it.polimi.ingsw.is25am49.client.model.enums")) {
                        TextNode valueNode = mapper.valueToTree(param);
                        contentNode.set("content", valueNode);
                    } else {
                        ObjectNode valueNode = mapper.valueToTree(param);
                        contentNode.set("content", valueNode);
                    }

                }
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
        return jsonFile;
    }

    @Override
    public void serverTestConnection(int playerId) throws RemoteException {
        System.out.println("no");
    }

    @Override
    public void registerClientRMI(RemoteClient client) throws RemoteException {

    }

    /**
     * This method is used to register the client socket on the server.
     * It creates a temporary file with the playerId and puts it in the outgoing queue.
     *
     * @param playerId the id of the player
     *                 //     * @throws RemoteException if there is an error during the remote call
     */
    @Override
    public void registerClientSocket(int playerId) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("registerClientSocket", clientController);
        serializationObjectClient.addParameter(playerId);
        serializeObjectFromClient(serializationObjectClient);
        serverHandler.connectCommand();
    }


    @Override
    public void adminSetup(int playerId, int numberOfPlayers, GameMode mode) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("adminSetup", clientController);
        serializationObjectClient.addParameter(playerId);
        serializationObjectClient.addParameter(numberOfPlayers);
        serializationObjectClient.addParameter(mode);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void playerJoin(int playerId, String username, Color color) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("playerJoin", clientController);
        serializationObjectClient.addParameter(playerId);
        serializationObjectClient.addParameter(username);
        serializationObjectClient.addParameter(color);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void userDrawTile(int playerId, int tileId) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("userDrawTile", clientController);
        serializationObjectClient.addParameter(playerId);
        serializationObjectClient.addParameter(tileId);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void userPlaceTile(int playerId, Point coordinates, Orientation orientation) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("userPlaceTile", clientController);
        serializationObjectClient.addParameter(playerId);
        serializationObjectClient.addParameter(coordinates);
        serializationObjectClient.addParameter(orientation);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void userPickTile(int playerId, Point coordinates) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("userPickTile", clientController);
        serializationObjectClient.addParameter(playerId);
        serializationObjectClient.addParameter(coordinates);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void userDiscardTile(int playerId) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("userDiscardTile", clientController);
        serializationObjectClient.addParameter(playerId);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void userFinishShipboard(int playerId) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("userFinishShipboard", clientController);
        serializationObjectClient.addParameter(playerId);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("userLookSmallAdventureCardDeck", clientController);
        serializationObjectClient.addParameter(playerId);
        serializationObjectClient.addParameter(smallDeckIndex);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("userStopLookSmallAdventureCardDeck", clientController);
        serializationObjectClient.addParameter(playerId);
        serializationObjectClient.addParameter(smallDeckIndex);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void userPlaceTimer(int playerId) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("userPlaceTimer", clientController);
        serializationObjectClient.addParameter(playerId);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("userSelectTypeOfToken", clientController);
        serializationObjectClient.addParameter(playerId);
        serializationObjectClient.addParameter(token);
        serializationObjectClient.addParameter(coordinates);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void userThrowDices(int playerId) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("userThrowDices", clientController);
        serializationObjectClient.addParameter(playerId);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void userDrawAdventureCard(int playerId) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("userDrawAdventureCard", clientController);
        serializationObjectClient.addParameter(playerId);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void userFinishDecisionActivating(int playerId, HashSet<Point> coordinates) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("userFinishDecisionActivating", clientController);
        serializationObjectClient.addParameter(playerId);
        serializationObjectClient.addParameter(coordinates);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("userFinishDecisionToken", clientController);
        serializationObjectClient.addParameter(playerId);
        serializationObjectClient.addParameter(listOfTokens);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void userAcceptPrizes(int playerId, boolean wantsRewards) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("userAcceptPrizes", clientController);
        serializationObjectClient.addParameter(playerId);
        serializationObjectClient.addParameter(wantsRewards);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void userLandOnPlanet(int playerId, boolean isLanded, Planet planet) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("userLandOnPlanet", clientController);
        serializationObjectClient.addParameter(playerId);
        serializationObjectClient.addParameter(isLanded);
        serializationObjectClient.addParameter(planet);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void userLandOnStationOrShip(int playerId, boolean isLanded) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("userLandOnStationOrShip", clientController);
        serializationObjectClient.addParameter(playerId);
        serializationObjectClient.addParameter(isLanded);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void userStopFlight(int playerId) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("userStopFlight", clientController);
        serializationObjectClient.addParameter(playerId);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void adminCalculateScore(int playerId) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("adminCalculateScore", clientController);
        serializationObjectClient.addParameter(playerId);
        serializeObjectFromClient(serializationObjectClient);
    }

    @Override
    public void userEndGame(int playerId) throws RemoteException {
        SerializationObjectClient serializationObjectClient = new SerializationObjectClient("userEndGame", clientController);
        serializationObjectClient.addParameter(playerId);
        serializeObjectFromClient(serializationObjectClient);
    }

    public void setServerHandler(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }
}