package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.enums.Orientation;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientSerializerTest {
    ArrayList<File> filesJson;
    ArrayList<Command> serObjectArray;
    ClientController clientController;
    GameSession gameSession;
    ClientSerializer clientSerializer;
    ServerHandler serverHandler;
    TextualUserInterface tui;

    @BeforeEach
    void init(){
        tui = new TextualUserInterface();
        filesJson = new ArrayList<>();
        serObjectArray = new ArrayList<>();
        gameSession = new GameSession(tui);
        clientController = new ClientController(gameSession);
        clientSerializer = new ClientSerializer(clientController);
        ServerHandler serverHandler= new ServerHandler("localhost", 8080);
        serverHandler.setClientSerializer(clientSerializer);
        clientSerializer.setServerHandler(serverHandler);
        clientController.setServerMethods(clientSerializer);

        Command serObject1;
        serObject1 = new Command("userPlaceTileCallback", clientController);
        serObject1.addParameter(0);
        Point point = new Point(6, 6);
        Orientation orientation = Orientation.NORTH;
        serObject1.addParameter(point);
        serObject1.addParameter(orientation);
        serObjectArray.add(serObject1);

        Command serObject2;
        serObject2 = new Command("userFinishDecisionTokenCallback", clientController);
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

        Command serObject3;
        serObject3 = new Command("registerClientCallback", clientController);
        serObject3.addParameter(-1);
        serObjectArray.add(serObject3);

        File testFile1 = new File("src/main/resources/it/polimi/ingsw/is25am49/files_JSON_test/test4.json");
        File testFile2 = new File("src/main/resources/it/polimi/ingsw/is25am49/files_JSON_test/test5.json");
        File testFile3 = new File("src/main/resources/it/polimi/ingsw/is25am49/files_JSON_test/test6.json");
        filesJson.add(testFile1);
        filesJson.add(testFile2);
        filesJson.add(testFile3);
    }
    @Test
    void deSerializeStringFromServer() {
        int i = 0;
        Command serProva1;
        for(File file: filesJson){
            serProva1 = clientSerializer.deSerializeStringFromServer(clientSerializer.serializeObjectFromClient(serObjectArray.get(i)));
            assertEquals(serProva1.toString(), clientSerializer.deSerializeStringFromServer(file).toString());
            i++;
        }
    }

    @Test
    void serializeObjectFromClient() {
        int i = 0;
        String testString  = "", actualString = "";
        for(Command serObject: serObjectArray){
            try {
                testString = Files.readString(filesJson.get(i).toPath());
                actualString = Files.readString(clientSerializer.serializeObjectFromClient(serObject).toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            assertEquals(Command.compactJsonString(testString), Command.compactJsonString(actualString));
            i++;
        }
    }
    @Test
    void testCompatibility(){
        Command serProva1;
        Command serProva2;
        for(Command serOb: serObjectArray){
            serProva1 = clientSerializer.deSerializeStringFromServer(clientSerializer.serializeObjectFromClient(serOb));
            System.out.println(serProva1);
            System.out.println(serOb);
            //System.out.println(serProva1.getParameters().get(2).getClass().getName());
            serProva2 = clientSerializer.deSerializeStringFromServer(clientSerializer.serializeObjectFromClient(serProva1));
            System.out.println(serProva2);
            System.out.println(serProva1);
            assertEquals(serProva1.toString(), serProva2.toString());
        }

    }
}
