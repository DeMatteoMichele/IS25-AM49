package it.polimi.ingsw.is25am49.client.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class HomeController extends GenericSceneController{
    @FXML private ImageView backgroundImage;
    @FXML
    private Button startGameButton;

    @FXML
    private StackPane AnchorPane;


    public void initialize() {
        Image image = new Image(getClass().getResource("/sfondo2.png").toExternalForm());
        backgroundImage.setImage(image);

        backgroundImage.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                backgroundImage.fitWidthProperty().bind(newScene.widthProperty());
                backgroundImage.fitHeightProperty().bind(newScene.heightProperty());
            }
        });
//        startGameButton.prefWidthProperty().bind(AnchorPane.widthProperty().multiply(0.3)); // 30% della larghezza
//
//        // Lega l'altezza del pulsante all'altezza dell'AnchorPane
//        startGameButton.prefHeightProperty().bind(AnchorPane.heightProperty().multiply(0.1)); // 10% dell'altezza
//
//        // Centra il pulsante orizzontalmente e verticalmente
//        startGameButton.layoutXProperty().bind(AnchorPane.widthProperty().subtract(startGameButton.prefWidthProperty()).divide(2));
//        startGameButton.layoutYProperty().bind(AnchorPane.heightProperty().subtract(startGameButton.prefHeightProperty()).divide(2));
    }
    @FXML
    private void handleStartGame(ActionEvent event) {
       guiApplication.showScene(ScenesName.SETTINGS);

    }
}
