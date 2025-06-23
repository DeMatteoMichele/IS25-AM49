package it.polimi.ingsw.is25am49.client.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class settingsErrorController extends GenericSceneController {
    private String username;
    boolean isAlreadyConnected = false;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private Button yesReconnection;
    @FXML
    private Button noReconnection;
    @FXML
    private Button usernameButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private Label usernameLabel;



    public void initialize() {
        Image image = new Image(getClass().getResource("/sfondo3.jpg").toExternalForm());
        backgroundImage.setImage(image);
        disableAll();

        backgroundImage.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                backgroundImage.fitWidthProperty().bind(newScene.widthProperty());
                backgroundImage.fitHeightProperty().bind(newScene.heightProperty());
            }
        });
    }

    private void disableAll() {
        yesReconnection.setDisable(false);
        noReconnection.setDisable(false);
        usernameButton.setDisable(true);
        usernameTextField.setDisable(true);


    }



    @FXML
    private void disableReconnectionButtons() {
        yesReconnection.setDisable(true);
        noReconnection.setDisable(true);
        usernameTextField.setDisable(false);
        usernameButton.setDisable(false);
    }



    @FXML
    public void handleReconnection(ActionEvent event) {
        usernameLabel.setText("Please select your EXACT! previous username: ");
        isAlreadyConnected = true;
        disableReconnectionButtons();
    }

    @FXML
    public void handleUsername(ActionEvent event) {
        username = usernameTextField.getText().trim();

        if (username.isEmpty()) {
            guiApplication.printUpdateMessage(error+ " Please enter a username!");
        } else if (username.length()>20) {
            guiApplication.printUpdateMessage(error + " Username too long! Maximum length is 20 characters.");
            usernameTextField.setText("");
        } else if (username.contains(" ")) {
            guiApplication.printUpdateMessage( error+ " Username contains spaces!");
        } else {
            System.out.println("Username set to: " + username);
            guiApplication.setUsername(username);
            guiApplication.getController().setUsername(username);

            if (!isAlreadyConnected) {
                System.out.println("entrato");
                guiApplication.getController().createMyIdJson();
            }
            usernameTextField.setDisable(true);
            usernameButton.setDisable(true);
            initialize();
            guiApplication.showScene(ScenesName.INIT);

        }
    }


}
