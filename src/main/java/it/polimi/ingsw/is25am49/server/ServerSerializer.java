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
        executorService = Executors.newFixedThreadPool(10);
        executorService.submit(this::processFilesFromSocketQueue);
    }

    /**
     * This method is used to process the files from the socket queue.
     * It takes the file from the queue and deserializes it.
     * Method take() is blocking, so it will wait until a file is available in the queue.
     */
    private void processFilesFromSocketQueue() {
        while (true) {
            try {
                File json = filesFromSocketQueue.take();
                deSerializeStringFromClient(json);
            } catch (InterruptedException e) {
                System.err.println("Thread that process the FilesFromSocketQueue has been interrrupted: " + e.getMessage());
            }
        }
    }

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    //se cambiano i parametri dei metodi bisogna modificare questo codice
    public CommandServer deSerializeStringFromClient(File fileJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.addMixIn(Pair.class, PairMixIn.class);
            JsonNode rootNode = mapper.readTree(fileJson);
            String typeOfMessage = rootNode.get("typeOfMessage").asText();
            String methodName = rootNode.get("methodName").asText();
            CommandServer serObject = new CommandServer(methodName, serverController, -1);
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
            } else if (typeOfMessage.equals("HSK")) {
                for (JsonNode paramNode : params) {
                    String typeOfCollection = paramNode.get("typeOfCollection").asText();
                    int contentObject = paramNode.get("content").get("content").asInt();
                    if (contentObject == -1) {
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

    public File serializeObjectFromServer(CommandServer serObject) {
        File jsonFile = null;
        try {
            jsonFile = File.createTempFile("server_response", ".json");
        } catch (IOException e) {
            System.out.println("Error creating temp file: " + e.getMessage());
        }
        //jsonFile.deleteOnExit();
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
        } else if (serObject.getTypeOfMessage().equals("HSK")) {
            clientHandler.putFileInTheOutGoingFileQueue(jsonFile, serObject.getPlayerId());
        }
        return jsonFile;
    }

    public void notifyClient(String msg) throws RemoteException {

    }

    public void registerClientCallback(int playerId) throws RemoteException {
        //QUESTO METODO NON VIENE USATO, DA CAPIRE
        //NON SI PASSA DAL MODEL IN QUESTO CASO
    }

    public void playerJoinCallback(int playerId, String username, int seedForGeneration) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("playerJoinCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(username);
        serObjectTemp.addParameter(seedForGeneration);
        sendCommand(serObjectTemp);
    }

    public void playerErrorJoinCallback(int playerId, String typeOfError) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("playerErrorJoinCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(typeOfError);
        sendCommand(serObjectTemp);
    }

    public void adminSetupCallback(int playerId, int maxPlayers, GameMode mode) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("adminSetupCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(maxPlayers);
        serObjectTemp.addParameter(mode);
        sendCommand(serObjectTemp);
    }

    public void gameStartedCallback() throws RemoteException {
        //HARDCODATO IL PLAYERID SICCOME NON È UN INFORMAZIONE RILEVANTE
        CommandServer serObjectTemp = new CommandServer("gameStartedCallback", serverController, -99);
        sendCommand(serObjectTemp);
    }

    public void userDrawTileCallback(int playerId, int tileId) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userDrawTileCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(tileId);
        sendCommand(serObjectTemp);
    }

    public void userErrorDrawTileCallback(int playerId, int tileId) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userErrorDrawTileCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(tileId);
        sendCommand(serObjectTemp);
    }

    public void userPlaceTileCallback(int playerId, Point coordinates, Orientation orientation) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userPlaceTileCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(coordinates);
        serObjectTemp.addParameter(orientation);
        sendCommand(serObjectTemp);
    }

    public void userPickTileCallback(int playerId, Point coordinates) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userPickTileCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(coordinates);
        sendCommand(serObjectTemp);
    }

    public void userDiscardTileCallback(int playerId) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userDiscardTileCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        sendCommand(serObjectTemp);
    }

    public void userFinishShipboardCallback(int playerId) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userFinishShipboardCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        sendCommand(serObjectTemp);
    }

    public void userLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userLookSmallAdventureCardDeckCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(smallDeckIndex);
        sendCommand(serObjectTemp);
    }

    public void userErrorLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userErrorLookSmallAdventureCardDeckCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(smallDeckIndex);
        sendCommand(serObjectTemp);
    }

    public void userStopLookSmallAdventureCardDeckCallback(int playerId, int smallDeckIndex) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userStopLookSmallAdventureCardDeckCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(smallDeckIndex);
        sendCommand(serObjectTemp);
    }

    public void userPlaceTimerCallback(int playerId) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userPlaceTimerCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        sendCommand(serObjectTemp);
    }

    public void userErrorPlaceTimerCallback(int playerId) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userErrorPlaceTimerCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        sendCommand(serObjectTemp);
    }

    public void userSelectTypeOfTokenCallback(int playerId, Token token, Point coordinates) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userSelectTypeOfTokenCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(token);
        serObjectTemp.addParameter(coordinates);
        sendCommand(serObjectTemp);
    }

    public void userThrowDicesCallback(int playerId, int result) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userThrowDicesCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(result);
        sendCommand(serObjectTemp);
    }

    public void userDrawAdventureCardCallback(int playerId) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userDrawAdventureCardCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        sendCommand(serObjectTemp);
    }

    public void userFinishDecisionActivatingCallback(int playerId, Set<Point> coordinates) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userFinishDecisionActivatingCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(coordinates);
        sendCommand(serObjectTemp);
    }

    public void userFinishDecisionTokenCallback(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userFinishDecisionTokenCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(listOfTokens);
        sendCommand(serObjectTemp);
    }

    public void userAcceptPrizesCallback(int playerId) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userAcceptPrizesCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        sendCommand(serObjectTemp);
    }

    public void userLandOnPlanetCallback(int playerId, boolean isLanded, Planet planet) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userLandOnPlanetCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(isLanded);
        serObjectTemp.addParameter(planet);
        sendCommand(serObjectTemp);
    }

    public void userLandOnStationOrShipCallback(int playerId, boolean isLanded) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userLandOnStationOrShipCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        serObjectTemp.addParameter(isLanded);
        sendCommand(serObjectTemp);
    }

    public void userStopFlightCallback(int playerId) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userStopFlightCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        sendCommand(serObjectTemp);
    }

    public void adminCalculateScoreCallback(int playerId) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("adminCalculateScoreCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        sendCommand(serObjectTemp);
    }

    public void userEndGameCallback(int playerId) throws RemoteException {
        CommandServer serObjectTemp = new CommandServer("userEndGameCallback", serverController, playerId);
        serObjectTemp.addParameter(playerId);
        sendCommand(serObjectTemp);
    }

    public void changeState(String state) throws RemoteException {
        //HARDCODATO IL PLAYERID SICCOME NON È UN INFORMAZIONE RILEVANTE
        CommandServer serObjectTemp = new CommandServer("changeState", serverController, -99);
        sendCommand(serObjectTemp);
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
        CommandServer serObjectTemp;
        serObjectTemp = deSerializeStringFromClient(fileJson);
        serObjectTemp.setMethodName("registerClientCallback");
        serializeObjectFromServer(serObjectTemp);
        return serObjectTemp.getPlayerId();
    }

    @Override
    public void sendCommand(CommandServer commandServer) throws RemoteException {
        serializeObjectFromServer(commandServer);
    }
}