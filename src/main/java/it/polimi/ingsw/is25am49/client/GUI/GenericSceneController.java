package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.PrimitiveIterator;

public class GenericSceneController {
    //associo a ogni id della tile l'immagine scoperta
    protected final Map<Integer, ImageView> tilesMap = new HashMap<>();
    protected GameSession gameSession;
    protected GraphicUserInterface guiApplication;
    protected String error = "⚠";


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
            ImageView imageView = new ImageView(new Image(getClass().getResource(path).toExternalForm()));
            imageView.setFitWidth(40);
            imageView.setFitHeight(60);
            tilesMap.put(j, imageView);
        }
    }
}
