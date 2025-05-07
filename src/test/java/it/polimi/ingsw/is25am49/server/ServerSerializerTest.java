package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ServerSerializerTest {
    ArrayList<File> filesJSON;
    ArrayList<SerializationObjectServer> serObjectArray;
    SerializationObjectServer serObject;
    ServerController serverController;
    GameSession gameSession;
    ServerSerializer serverSerializer;

    @BeforeEach
    void init(){
        filesJSON = new ArrayList<>();
        serObjectArray = new ArrayList<>();
        gameSession = new GameSession();
        serverController = new ServerController(gameSession);
        serverSerializer = new ServerSerializer(serverController);
        serObject = new SerializationObjectServer("userPlaceTile", serverController, 0);
        serObject.addParameter(0);
        Point point = new Point(6, 6);
        Orientation orientation = Orientation.NORTH;
        serObject.addParameter(point);
        serObject.addParameter(orientation);
        serObjectArray.add(serObject);
        File testFile1 = new File("src/main/resources/it/polimi/ingsw/is25am49/files_JSON_test/test1.json");
        File testFile2 = new File("src/main/resources/it/polimi/ingsw/is25am49/files_JSON_test/test2.json");
        File testFile3 = new File("src/main/resources/it/polimi/ingsw/is25am49/files_JSON_test/test3.json");
        filesJSON.add(testFile1);
        filesJSON.add(testFile2);
        filesJSON.add(testFile3);
    }
    @Test
    void deSerializeStringFromClient() {

    }

    @Test
    void serializeObjectFromServer() {
        int i = 0;
        for(SerializationObjectServer serObject: serObjectArray){
            String testString  = "", actualString = "";
            try {
                testString = Files.readString(filesJSON.get(i).toPath());
                actualString = Files.readString(serverSerializer.serializeObjectFromServer(serObject).toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            assertEquals(testString, actualString);
        }
    }
}