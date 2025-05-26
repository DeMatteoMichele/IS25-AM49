package it.polimi.ingsw.is25am49.client.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class initController extends GenericSceneController{

    @FXML
    private Label welcome;

    public void initialize() {
        welcome.setText("Welcome to the Game!");
        welcome.setStyle("-fx-font-size: 24px; -fx-text-fill: #FFFFFF; -fx-font-weight: bold;");
        welcome.setLayoutX(100);
        welcome.setLayoutY(100);
    }
}
