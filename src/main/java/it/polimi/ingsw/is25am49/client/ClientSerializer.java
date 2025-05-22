package it.polimi.ingsw.is25am49.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.shared.Command;
import it.polimi.ingsw.is25am49.shared.RemoteServer;
import javafx.util.Pair;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private final File directory;

    public ClientSerializer(GameSession session, ClientController clientController) {
        this.filesFromSocketQueue = new ArrayBlockingQueue<>(1000, true);
        executors = Executors.newFixedThreadPool(20);
        executors.submit(this::processFilesFromSocketQueue);
        gameSession = session;
        this.clientController = clientController;
        directory = new File("$FilesFromClientSerializer");
        if (!directory.exists()) {
            System.out.println("Directory doesn't exist, creating it");
            directory.mkdirs();
        }
    }

    public ClientSerializer(ClientController clientController) {
        this.filesFromSocketQueue = new ArrayBlockingQueue<>(1000, true);
        executors = Executors.newFixedThreadPool(20);
        executors.submit(this::processFilesFromSocketQueue);
        this.clientController = clientController;
        directory = new File("$FilesFromClientSerializer");
        if (!directory.exists()) {
            System.out.println("Directory doesn't exist, creating it");
            directory.mkdirs();
        }
    }

    private void processFilesFromSocketQueue() {
        while (true) {
            try {
                File jsonFile = filesFromSocketQueue.take();
                System.out.println("Processing file: ");
                deSerializeStringFromServer(jsonFile);
            } catch (InterruptedException e) {
                System.out.println("Error while taking the message from the queue, thread has been interrupted");
            }
        }
    }

    public void setGamesession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    //IL SERIALIZER SE DEVE PASSARE UN FILE AL SERVERHANDLER GLIELO DEVE METTERE NELLA SUA OUTGOINGQUEUE TRAMITE IL METODO APPOSIT DEL SERVERHANDLER.
    //IL SERIALIZER SE DEVE PASSARE UN FILE AL SERVERHANDLER GLIELO DEVE METTERE NELLA UA OUTGOINGQUEUE TRAMITE IL METODO APPOSIT DEL SERVERHANDLER.

    /**
     * This method is used to put files in the queue.
     * It is called by ServerHandler after receiving a message from server
     */
    public void putFileFromSocketInQueue(File fileJson) {
        try {
            filesFromSocketQueue.put(fileJson);
        } catch (InterruptedException e) {
            System.out.println("Error while putting the file in the filesFromSocketQueue, thread has been interrupted");
        }
    }

    public Command deSerializeStringFromServer(File fileJson) {
        try {
            String jsonContent = Files.readString(fileJson.toPath(), StandardCharsets.UTF_8).trim();
            // Regex Definitiva Potenziata: Rimuove tutti i caratteri Unicode della categoria "Other" (C)
            // e rimuove anche il carattere di sostituzione Unicode (U+FFFD).
//            jsonContent = jsonContent.replaceAll("[\\p{C}]|\\uFFFD", "");
            jsonContent = jsonContent.replaceAll("[\\p{C}&&[^\\t\\n\\r]]|\\uFFFD", "");
            // Tenta di bilanciare le parentesi come workaround rischioso
            ObjectMapper mapper = new ObjectMapper();
            mapper.addMixIn(Pair.class, it.polimi.ingsw.is25am49.client.PairMixIn.class);
            JsonNode rootNode = mapper.readTree(jsonContent);
            String typeOfMessage = rootNode.get("typeOfMessage").asText();
            //System.out.println("Type of message: " + typeOfMessage);
            String methodName = rootNode.get("methodName").asText();
            //System.out.println("Method name: " + methodName);
            Command command = new Command(methodName, clientController);
            ArrayNode params = (ArrayNode) rootNode.get("params");
            //System.out.println("Params: " + params);
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
                                    yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.Token.class);

                                }
                                case "it.polimi.ingsw.is25am49.server.model.enums.Color" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.Color.class);
                                }
                                case "java.lang.Integer" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield contentObject.asInt();
                                }
                                case "it.polimi.ingsw.is25am49.server.model.enums.GameMode" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.GameMode.class);
                                }
                                case "java.lang.String" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield contentObject.asText();
                                }
                                case "java.awt.Point" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Point.class);
                                }
                                case "it.polimi.ingsw.is25am49.server.model.enums.Orientation" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.Orientation.class);
                                }
                                case "java.lang.Boolean" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield contentObject.asBoolean();
                                }
                                default -> new Object();
                            };
                            collectionList.add(value);
                        }
                        command.addParameter(collectionList);
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
                                    yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.Token.class);

                                }
                                case "it.polimi.ingsw.is25am49.server.model.enums.Color" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.Color.class);
                                }
                                case "java.lang.Integer" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield contentObject.asInt();
                                }
                                case "it.polimi.ingsw.is25am49.server.model.enums.GameMode" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.GameMode.class);
                                }
                                case "java.lang.String" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield contentObject.asText();
                                }
                                case "java.awt.Point" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), Point.class);
                                }
                                case "it.polimi.ingsw.is25am49.server.model.enums.Orientation" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.Orientation.class);
                                }
                                case "java.lang.Boolean" -> {
                                    JsonNode contentObject = contentNode.get("content");
                                    yield contentObject.asBoolean();
                                }
                                default -> new Object();
                            };
                            collectionSet.add(value);
                        }
                        command.addParameter(collectionSet);
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
                                yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.Token.class);

                            }
                            case "it.polimi.ingsw.is25am49.server.model.enums.Color" -> {
                                JsonNode contentObject = content.get("content");
                                yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.Color.class);
                            }
                            case "java.lang.Integer" -> {
                                JsonNode contentObject = content.get("content");
                                yield contentObject.asInt();
                            }
                            case "it.polimi.ingsw.is25am49.server.model.enums.GameMode" -> {
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
                            case "it.polimi.ingsw.is25am49.server.model.enums.Orientation" -> {
                                JsonNode contentObject = content.get("content");
                                yield mapper.readValue(contentObject.traverse(), it.polimi.ingsw.is25am49.client.model.enums.Orientation.class);
                            }
                            case "java.lang.Boolean" -> {
                                JsonNode contentObject = content.get("content");
                                yield contentObject.asBoolean();
                            }
                            default -> new Object();
                        };
                        command.addParameter(object);
                    }
                }
            } else if (typeOfMessage.equals("HSK")) {
                System.out.println("SONO IN HSK");
                for (JsonNode paramNode : params) {
                    String typeOfCollection = paramNode.get("typeOfCollection").asText();
                    int idPlayer = paramNode.get("content").get("content").asInt();
                    command.addParameter(idPlayer);
                }
            }
            System.out.println("EXECUTING COMMAND");
            command.callMethod();
            System.out.println("COMMAND EXECUTED:" + command.toString());
            return command;
        } catch (IOException e) {
            System.err.println("porcatr " + e.getMessage());
            e.printStackTrace();
            return new Command("EXCEPTION", clientController);
        }
    }

    public File serializeObjectFromClient(Command command) {
        File jsonFile = new File(directory, "$commandToServer" + java.util.UUID.randomUUID() + ".json");
        jsonFile.deleteOnExit();
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(Pair.class, PairMixIn.class);
        ObjectNode rootNode = mapper.createObjectNode();
        ArrayNode params = mapper.createArrayNode();
        rootNode.put("typeOfMessage", command.getTypeOfMessage());
        rootNode.put("methodName", command.getMethodName());
        rootNode.set("params", params);
        for (Object param : command.getParameters()) {
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


    public void setServerHandler(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    @Override
    public void command_FromClientToServer(Command command) throws RemoteException {
        serializeObjectFromClient(command);
    }

    public void askControllerToStartRegistration() throws IOException {
        try {
            clientController.registerClientSocket();
        } catch (RemoteException e) {
            System.err.println("Error while trying to register the client socket due to remoteexception");
        }
    }

    public void askControllerToResetModel() {
        clientController.clientReset();
    }}

