package it.polimi.ingsw.is25am49.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    private int i = -1;


    public ServerSerializer(ServerController serverController) {
        this.serverController = serverController;
        filesFromSocketQueue = new ArrayBlockingQueue<File>(4000, true);

    }

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    //se cambiano i parametri dei metodi bisogna modificare questo codice
    public void deSerializeStringFromClient(File fileJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(fileJson);
            String typeOfMessage = rootNode.get("typeOfMessage").asText();
            if (typeOfMessage.equals("MSG")) {
                String methodName = rootNode.get("methodName").asText();
                SerializationObjectServer serObject = new SerializationObjectServer(methodName, serverController, 0);
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
                serObject.callServerMethod();
            }
            // HandShake ?, HeartBeat ?, dipende dalla gestione nel Layer di Rete
            else {

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void serializeObjectFromServer(SerializationObjectServer serObject) {
        File jsonFile = new File("commandToClient.json");
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
        clientHandler.putFileInTheOutGoingFileQueue(jsonFile, serObject.getPlayerId());
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
            String stringa = Files.readString(fileJson.toPath()).trim();
            int clientId = Integer.parseInt(stringa);
            if (clientId == -1) {
                i++;
                clientId = i;
            }
            String stringResponse = String.valueOf(clientId);
            File fileJsonResponse = new File("handshake_response.json");
            Files.writeString(fileJsonResponse.toPath(), stringResponse);
            clientHandler.putFileInTheOutGoingFileQueue(fileJsonResponse,clientId);
            return clientId;
        } catch (Exception e) {
            System.err.println("Errore nella deserializzazione dell'handshake: " + e.getMessage());
            return -1;
        }
    }
}
