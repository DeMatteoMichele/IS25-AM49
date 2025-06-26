package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.PrimitiveIterator;

public abstract class GenericSceneController {
    protected GameSession gameSession;
    protected GraphicUserInterface guiApplication;
    protected String error = "⚠";
    //associo a ogni id della tile l'immagine scoperta
    protected final Map<Integer, ImageView> tilesMap = new HashMap<>();
    //associa a ogni id della carta la sua immagine
    protected final Map<String, ImageView> adventureCardMap = new HashMap<>();
    protected final Map<Token, ImageView> tokensMap = new HashMap<>();


    protected StackPane damagePane = new StackPane();

    /**
     * Sets the GUI application instance associated with this controller.
     *
     * @param guiApplication The GUI application instance.
     */
    public void setGui(GraphicUserInterface guiApplication, GameSession gameSession) {
        this.guiApplication = guiApplication;
        this.gameSession = gameSession;
    }

    public void loadTilesMap() {
        // Carica le immagini delle tile e le associa agli ID
        for (int j = 1; j < 157; j++) {

            String path = "/it/polimi/ingsw/is25am49/tiles/GT-new_tiles_16_for web" + j + ".jpg";
            ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm()));
            imageView.setFitWidth(40);
            imageView.setFitHeight(60);
            tilesMap.put(j, imageView);
        }
    }

    public void loadTokens() {
        String basePath = "/it/polimi/ingsw/is25am49/tokens/";
        tokensMap.put(Token.ASTRONAUT, createTokenImage(basePath + "astronaut.png"));
        tokensMap.put(Token.BROWN_ALIENS, createTokenImage(basePath + "brownalien.png"));
        tokensMap.put(Token.PURPLE_ALIENS, createTokenImage(basePath + "purplealien.png"));
        tokensMap.put(Token.YELLOW_GOODS, createTokenImage(basePath + "yellowgood.png"));
        tokensMap.put(Token.RED_GOODS, createTokenImage(basePath + "redgood.png"));
        tokensMap.put(Token.BLUE_GOODS, createTokenImage(basePath + "bluegood.png"));
        tokensMap.put(Token.GREEN_GOODS, createTokenImage(basePath + "greengood.png"));
        tokensMap.put(Token.BATTERY_CELLS, createTokenImage(basePath + "battery.png"));
    }

    private ImageView createTokenImage(String imagePath) {
        Image image = new Image(Objects.requireNonNull(getClass().getResource(imagePath)).toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);
        return imageView;
    }

}
