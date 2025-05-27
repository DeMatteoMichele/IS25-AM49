package it.polimi.ingsw.is25am49.client.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class homeController extends GenericSceneController{
    @FXML private ImageView backgroundImage;
    public void initialize() {
        Image image = new Image(getClass().getResource("/sfondo2.png").toExternalForm());
        backgroundImage.setImage(image);

        backgroundImage.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                backgroundImage.fitWidthProperty().bind(newScene.widthProperty());
                backgroundImage.fitHeightProperty().bind(newScene.heightProperty());
            }
        });
    }
    @FXML
    private void handleStartGame(ActionEvent event) {
       getGui().showScene(ScenesName.SETTINGS);

    }
}
