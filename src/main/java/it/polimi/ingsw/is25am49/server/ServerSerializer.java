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
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSerializer implements RemoteClient {
    private final ServerController serverController;
    private ClientHandler clientHandler;
    private final BlockingQueue<File> filesFromSocketQueue;
    private final ExecutorService executorService;
    private int idCounter;


    public ServerSerializer(ServerController serverController) {
        this.serverController = serverController;
        this.idCounter = -1;
        filesFromSocketQueue = new ArrayBlockingQueue<File>(4000, true);
        executorService= Executors.newFixedThreadPool(10);
        executorService.submit(this::processFilesFromSocketQueue);
    }

    /**
     * This method is used to process the files from the socket queue.
     * It takes the file from the queue and deserializes it.
     * Method take() is blocking, so it will wait until a file is available in the queue.
     */
    private void processFilesFromSocketQueue() {
        while(true){
            try{
                File json = filesFromSocketQueue.take();
                deSerializeStringFromClient(json);
            }
            catch (InterruptedException e){
                System.err.println("Thread that process the FilesFromSocketQueue has been interrrupted: " + e.getMessage());
            }
        }
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
                                case "it.polimi.ingsw.is25am49.client.model.enums.Token" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Token.class);
                                }
                                case "it.polimi.ingsw.is25am49.client.model.enums.Color" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Color.class);
                                }
                                case "java.lang.Integer" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield contentObject.asInt();
                                }
                                case "it.polimi.ingsw.is25am49.client.model.enums.GameMode" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), GameMode.class);
                                }
                                case "java.lang.String" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield contentObject.asText();
                                }
                                case "java.awt.Point" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Point.class);
                                }
                                case "it.polimi.ingsw.is25am49.client.model.enums.Orientation" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Orientation.class);
                                }
                                case "java.lang.Boolean" -> {
                                    JsonNode contentObject = contentNode.get("content");
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
                                    yield mapper.readValue(contentObject.traverse(), Token.class);
                                }
                                case "it.polimi.ingsw.is25am49.client.model.enums.Color" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Color.class);
                                }
                                case "java.lang.Integer" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield contentObject.asInt();
                                }
                                case "it.polimi.ingsw.is25am49.client.model.enums.GameMode" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), GameMode.class);
                                }
                                case "java.lang.String" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield contentObject.asText();
                                }
                                case "java.awt.Point" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Point.class);
                                }
                                case "it.polimi.ingsw.is25am49.client.model.enums.Orientation" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Orientation.class);
                                }
                                case "java.lang.Boolean" -> {
                                    JsonNode contentObject = contentNode.get("content");
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
                                yield mapper.readValue(contentObject.traverse(), Token.class);
                            }
                            case "it.polimi.ingsw.is25am49.client.model.enums.Color" -> {
                                JsonNode contentObject = content.get("content");
                                yield mapper.readValue(contentObject.traverse(), Color.class);
                            }
                            case "java.lang.Integer" -> {
                                JsonNode contentObject = content.get("content");
                                yield contentObject.asInt();
                            }
                            case "it.polimi.ingsw.is25am49.client.model.enums.GameMode" -> {
                                JsonNode contentObject = content.get("content");
                                yield mapper.readValue(contentObject.traverse(), GameMode.class);
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
                                yield mapper.readValue(contentObject.traverse(), Orientation.class);
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
                serObject.callServerMethod();
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
            return serObject;
        } catch (IOException e) {
            System.err.println("Errore di I/O nella lettura del file: " + fileJson.getAbsolutePath());
            System.err.println("Messaggio di errore: " + e.getMessage());
            System.err.println("Dimensione file: " + fileJson.length() + " bytes");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public File serializeObjectFromServer(SerializationObjectServer serObject) {
        File jsonFile=null;
        try {
             jsonFile = File.createTempFile("message_", ".json");
        } catch (IOException e) {
            System.err.println("Creazione file temporaneo in serializeObjectFromServer fallita: " + e.getMessage());
        }
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
        //QUESTO METODO NON VIENE USATO, DA CAPIRE
        //NON SI PASSA DAL MODEL IN QUESTO CASO
    }

    @Override
    public void playerJoinCallback(int playerId, int seedForGeneration) throws RemoteException {
        SerializationObjectServer serObjectTemp= new SerializationObjectServer("playerJoinCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(seedForGeneration);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void playerErrorJoinCallback(int playerId, String typeOfError) throws RemoteException {
        SerializationObjectServer serObjectTemp= new SerializationObjectServer("playerErrorJoinCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(typeOfError);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void gameStartedCallback() throws RemoteException {
        //HARDCODATO IL PLAYERID SICCOME NON È UN INFORMAZIONE RILEVANTE
        SerializationObjectServer serObjectTemp= new SerializationObjectServer("gameStartedCallback", serverController, -99);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userDrawTileCallback(int playerId, int tileId) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userDrawTileCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(tileId);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userErrorDrawTileCallback(int playerId, int tileId) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userErrorDrawTileCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(tileId);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userPlaceTileCallback(int playerId, Point coordinates, Orientation orientation) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userPlaceTileCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(coordinates);
        serObjectTemp.addParameter(orientation);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userPickTileCallback(int playerId, Point coordinates) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userPickTileCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(coordinates);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userDiscardTileCallback(int playerId) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userDiscardTileCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userFinishShipboardCallback(int playerId) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userFinishShipboardCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userLookSmallAdventureCardDeckCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(smallDeckIndex);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userErrorLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userErrorLookSmallAdventureCardDeckCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(smallDeckIndex);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userStopLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userStopLookSmallAdventureCardDeckCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(smallDeckIndex);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userPlaceTimerCallback(int playerId) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userPlaceTimerCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userErrorPlaceTimerCallback(int playerId) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userErrorPlaceTimerCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userSelectTypeOfTokenCallback(int playerId, Token token, Point coordinates) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userSelectTypeOfTokenCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(token);
        serObjectTemp.addParameter(coordinates);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userThrowDicesCallback(int playerId, int result) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userThrowDicesCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(result);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userDrawAdventureCardCallback(int playerId) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userDrawAdventureCardCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userFinishDecisionActivatingCallback(int playerId, Set<Point> coordinates) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userFinishDecisionActivatingCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(coordinates);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userFinishDecisionTokenCallback(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userFinishDecisionTokenCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(listOfTokens);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userAcceptPrizesCallback(int playerId) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userAcceptPrizesCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userLandOnPlanetCallback(int playerId, boolean isLanded, Planet planet) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userLandOnPlanetCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(isLanded);
        serObjectTemp.addParameter(planet);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userLandOnStationOrShipCallback(int playerId, boolean isLanded) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userLandOnStationOrShipCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(isLanded);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userStopFlightCallback(int playerId) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userStopFlightCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void adminCalculateScoreCallback(int playerId) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("adminCalculateScoreCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void userEndGameCallback(int playerId) throws RemoteException {
        SerializationObjectServer serObjectTemp = new SerializationObjectServer("userEndGameCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serializeObjectFromServer(serObjectTemp);
    }

    @Override
    public void changeState(String state) throws RemoteException {
        //HARDCODATO IL PLAYERID SICCOME NON È UN INFORMAZIONE RILEVANTE
        SerializationObjectServer serObjectTemp= new SerializationObjectServer("changeState", serverController, -99);
        serializeObjectFromServer(serObjectTemp);
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