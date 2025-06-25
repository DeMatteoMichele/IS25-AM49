package it.polimi.ingsw.is25am49.client.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class HomeController extends GenericSceneController{
    @FXML private ImageView backgroundImage;
    @FXML
    private Button startGameButton;

    @FXML
    private StackPane AnchorPane;
    @FXML
    private VBox messageBox;

    @FXML
    private ScrollPane scrollPane;


    public void initialize() {
        Image image = new Image(getClass().getResource("/sfondo2.png").toExternalForm());
        backgroundImage.setImage(image);

        backgroundImage.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                backgroundImage.fitWidthProperty().bind(newScene.widthProperty());
                backgroundImage.fitHeightProperty().bind(newScene.heightProperty());
            }
        });
//        for(int i=0;i<100;i++){
//            addMessage("CIAO");
//        }
//        addMessage("CiAo");
//        addMessage("CiAo");
//        addMessage("Benvenuto nel gioco IS25AM49!");
//        addMessage("Per iniziare, premi il pulsante 'Inizia partita' per configurare le impostazioni di gioco.");
//        addMessage("Per iniziare");
    }
//    public void addMessage(String text) {
//        Label messageLabel = new Label(text);
//        messageLabel.setWrapText(true); // Va a capo se troppo lungo
//        messageLabel.setStyle("-fx-text-fill: black;"); // Imposta il colore del testo a nero
//        messageBox.getChildren().add(messageLabel);
//
//        // Aggiungo un separatore dopo ogni messaggio
//        javafx.scene.control.Separator separator = new javafx.scene.control.Separator();
//        separator.setStyle("-fx-background-color: #dddddd;"); // Colore grigio chiaro
//        messageBox.getChildren().add(separator);
//
//        // Scrolla automaticamente in basso
//        scrollPane.layout(); // Importante per aggiornare layout
//        scrollPane.setVvalue(1.0);
//    }
    @FXML
    private void handleStartGame(ActionEvent event) {
       guiApplication.showScene(ScenesName.SETTINGS);

    }
}
