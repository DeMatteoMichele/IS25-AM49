package it.polimi.ingsw.is25am49.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import it.polimi.ingsw.is25am49.server.model.adventurecards.Planet;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ServerSerializer implements RemoteClient {
    private final ServerController serverController;
    private ClientHandler clientHandler;
    private final BlockingQueue<File> filesFromSocketQueue;
    private int idCounter = -1;


    public ServerSerializer(ServerController serverController) {
        this.serverController = serverController;
        filesFromSocketQueue = new ArrayBlockingQueue<File>(4000, true);

    }

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    //se cambiano i parametri dei metodi bisogna modificare questo codice
    public SerializationObjectServer deSerializeStringFromClient(File fileJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.addMixIn(Pair.class, PairMixIn.class);
            JsonNode rootNode = mapper.readTree(fileJson);
            String typeOfMessage = rootNode.get("typeOfMessage").asText();
            String methodName = rootNode.get("methodName").asText();
            SerializationObjectServer serObject = new SerializationObjectServer(methodName, serverController, -1);
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
                                case "it.polimi.ingsw.is25am49.server.model.enums.Token" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Token.class);

                                }
                                case "it.polimi.ingsw.is25am49.server.model.enums.color" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Color.class);
                                }
                                case "java.lang.Integer" -> {
                                    String contentObject = contentNode.get("content").asText();
                                    yield mapper.readValue(contentObject, int.class);
                                }
                                case "it.polimi.ingsw.is25am49.server.model.enums.GameMode" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), GameMode.class);
                                }
                                case "java.lang.String" -> {
                                    String contentObject = contentNode.get("content").asText();
                                    yield mapper.readValue(contentObject, String.class);
                                }
                                case "java.awt.Point" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Point.class);
                                }
                                case "it.polimi.ingsw.is25am49.server.model.enums.Orientation" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Orientation.class);
                                }
                                case "java.lang.Boolean" -> {
                                    String contentObject = contentNode.get("content").asText();
                                    yield mapper.readValue(contentObject, boolean.class);
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
                                case "it.polimi.ingsw.is25am49.server.model.enums.Token" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Token.class);

                                }
                                case "it.polimi.ingsw.is25am49.server.model.enums.color" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Color.class);
                                }
                                case "java.lang.Integer" -> {
                                    String contentObject = contentNode.get("content").asText();
                                    yield mapper.readValue(contentObject, int.class);
                                }
                                case "it.polimi.ingsw.is25am49.server.model.enums.GameMode" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), GameMode.class);
                                }
                                case "java.lang.String" -> {
                                    String contentObject = contentNode.get("content").asText();
                                    yield mapper.readValue(contentObject, String.class);
                                }
                                case "java.awt.Point" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Point.class);
                                }
                                case "it.polimi.ingsw.is25am49.server.model.enums.Orientation" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Orientation.class);
                                }
                                case "java.lang.Boolean" -> {
                                    String contentObject = contentNode.get("content").asText();
                                    yield mapper.readValue(contentObject, boolean.class);
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
                            case "it.polimi.ingsw.is25am49.server.model.enums.Token" -> {
                                JsonNode contentObject = content.get("content");
                                yield mapper.readValue(contentObject.traverse(), Token.class);

                            }
                            case "it.polimi.ingsw.is25am49.server.model.enums.color" -> {
                                JsonNode contentObject = content.get("content");
                                yield mapper.readValue(contentObject.traverse(), Color.class);
                            }
                            case "java.lang.Integer" -> {
                                String contentObject = content.get("content").asText();
                                yield mapper.readValue(contentObject, int.class);
                            }
                            case "it.polimi.ingsw.is25am49.server.model.enums.GameMode" -> {
                                JsonNode contentObject = content.get("content");
                                yield mapper.readValue(contentObject.traverse(), GameMode.class);
                            }
                            case "java.lang.String" -> {
                                String contentObject = content.get("content").asText();
                                yield mapper.readValue(contentObject, String.class);
                            }
                            case "java.awt.Point" -> {
                                JsonNode contentObject = content.get("content");
                                yield mapper.readValue(contentObject.traverse(), Point.class);
                            }
                            case "it.polimi.ingsw.is25am49.server.model.enums.Orientation" -> {
                                JsonNode contentObject = content.get("content");
                                yield mapper.readValue(contentObject.traverse(), Orientation.class);
                            }
                            case "java.lang.Boolean" -> {
                                String contentObject = content.get("content").asText();
                                yield mapper.readValue(contentObject, boolean.class);
                            }
                            default -> new Object();
                        };
                        serObject.addParameter(object);
                    }
                }
            }
            else if (typeOfMessage.equals("HSK")) {
                for(JsonNode paramNode : params){
                    String typeOfCollection = paramNode.get("typeOfCollection").asText();
                    int contentObject = paramNode.get("content").get("content").asInt();
                    if(contentObject==-1) {
                        idCounter++;
                    }
                    serObject.addParameter(idCounter);
                    serObject.setPlayerId(idCounter);
                }
            }
            //TODO serObject.callServerMethod();
            return serObject;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public File serializeObjectFromServer(SerializationObjectServer serObject) {
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
                        if (className.contains("it.polimi.ingsw.is25am49.server.model.enums")) {
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
                        if (className.contains("it.polimi.ingsw.is25am49.server.model.enums")) {
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
                    if (className.contains("it.polimi.ingsw.is25am49.server.model.enums")) {
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
        if (serObject.getTypeOfMessage().equals("MSG")) {
            clientHandler.putFileInTheBroadcastQueue(jsonFile);
        }
        else if(serObject.getTypeOfMessage().equals("HSK")){
            clientHandler.putFileInTheOutGoingFileQueue(jsonFile, serObject.getPlayerId());
        }
        return jsonFile;
    }

    @Override
    public void notifyClient(String msg) throws RemoteException {

    }

    @Override
    public void registerClientCallback(int playerId) throws RemoteException {

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
        SerializationObjectServer serObjectTemp;
        serObjectTemp = deSerializeStringFromClient(fileJson);
        serObjectTemp.setMethodName("registerClientCallback");
        serializeObjectFromServer(serObjectTemp);
        return serObjectTemp.getPlayerId();
    }
}