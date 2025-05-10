package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ServerSerializerTest {
    ArrayList<File> filesJson;
    ArrayList<SerializationObjectServer> serObjectArray;
    ServerController serverController;
    GameSession gameSession;
    ServerSerializer serverSerializer;
    ClientHandler clientHandler;

    @BeforeEach
    void init(){
        filesJson = new ArrayList<>();
        serObjectArray = new ArrayList<>();
        gameSession = new GameSession();
        serverController = new ServerController(gameSession);
        serverSerializer = new ServerSerializer(serverController);
        clientHandler = new ClientHandler(serverSerializer, 8080);
        serverSerializer.setClientHandler(clientHandler);
        serverController.setSerializer(serverSerializer);

        SerializationObjectServer serObject1;
        serObject1 = new SerializationObjectServer("userPlaceTile", serverController, -1);
        serObject1.addParameter(0);
        Point point = new Point(6, 6);
        Orientation orientation = Orientation.NORTH;
        serObject1.addParameter(point);
        serObject1.addParameter(orientation);
        serObjectArray.add(serObject1);

        SerializationObjectServer serObject2;
        serObject2 = new SerializationObjectServer("userFinishDecisionToken", serverController, -1);
        serObject2.addParameter(0);
        ArrayList<Pair<Point, Token>> listOfTokens = new ArrayList<>();
        Pair<Point, Token> pair;
        for(int i = 0; i<10; i++){
            point = new Point(i, i);
            pair = new Pair<Point, Token>(point, Token.values()[i % Token.values().length]);
            listOfTokens.add(pair);
        }
        serObject2.addParameter(listOfTokens);
        serObjectArray.add(serObject2);

        SerializationObjectServer serObject3;
        serObject3 = new SerializationObjectServer("registerClientSocket", serverController, -1);
        serObject3.addParameter(-1);
        serObjectArray.add(serObject3);

        File testFile1 = new File("src/main/resources/it/polimi/ingsw/is25am49/files_JSON_test/test1.json");
        File testFile2 = new File("src/main/resources/it/polimi/ingsw/is25am49/files_JSON_test/test2.json");
        File testFile3 = new File("src/main/resources/it/polimi/ingsw/is25am49/files_JSON_test/test3.json");
        filesJson.add(testFile1);
        filesJson.add(testFile2);
        filesJson.add(testFile3);
    }
    @Test
    void deSerializeStringFromClient() {
        int i = 0;
        SerializationObjectServer serProva1;
        for(File file: filesJson){
            serProva1 = serverSerializer.deSerializeStringFromClient(serverSerializer.serializeObjectFromServer(serObjectArray.get(i)));
            assertEquals(serProva1.toString(), serverSerializer.deSerializeStringFromClient(file).toString());
            i++;
        }
    }

    @Test
    void serializeObjectFromServer() {
        int i = 0;
        String testString  = "", actualString = "";
        for(SerializationObjectServer serObject: serObjectArray){
            try {
                testString = Files.readString(filesJson.get(i).toPath());
                actualString = Files.readString(serverSerializer.serializeObjectFromServer(serObject).toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            assertEquals(SerializationObjectServer.compactJsonString(testString), SerializationObjectServer.compactJsonString(actualString));
            i++;
        }
    }
    @Test
    void testCompatibility(){
        SerializationObjectServer serProva1;
        SerializationObjectServer serProva2;
        for(SerializationObjectServer serOb: serObjectArray){
            serProva1 = serverSerializer.deSerializeStringFromClient(serverSerializer.serializeObjectFromServer(serOb));
            System.out.println(serProva1);
            System.out.println(serOb);
            serProva2 = serverSerializer.deSerializeStringFromClient(serverSerializer.serializeObjectFromServer(serProva1));
            System.out.println(serProva2);
            System.out.println(serProva1);
            assertEquals(serProva1.toString(), serProva2.toString());
        }

    }
}