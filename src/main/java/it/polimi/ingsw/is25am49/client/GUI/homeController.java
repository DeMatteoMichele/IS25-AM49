package it.polimi.ingsw.is25am49.client.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class homeController extends GenericSceneController{
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
        // Binding delle proprietà per centrare il bottone dinamicamente
//        AnchorPane.sceneProperty().addListener((observable, oldScene, newScene) -> {
//            if (newScene != null) {
//                // Utilizzo degli ancoraggi di AnchorPane per centrare il bottone
//                newScene.widthProperty().addListener((obs, oldWidth, newWidth) -> {
//                    AnchorPane.setLeftAnchor(startGameButton, (newWidth.doubleValue() - startGameButton.getWidth()) / 2);
//                    startGameButton.setPrefWidth(newScene.getWidth()/10);
//                });
//
//
//                newScene.heightProperty().addListener((obs, oldHeight, newHeight) -> {
//                    AnchorPane.setTopAnchor(startGameButton, (newHeight.doubleValue() - startGameButton.getHeight()) / 2);
//                    startGameButton.setPrefWidth(newScene.getWidth()/20);
//                });
//            }
//        });


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
