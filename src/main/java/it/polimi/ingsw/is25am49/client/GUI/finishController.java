package it.polimi.ingsw.is25am49.client.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class finishController extends GenericSceneController{
    @FXML
    private Button calculateScore;

    public void initialize() {
        calculateScore.setVisible(false);
    }
    public void calculateScore(ActionEvent event) {
        try{
            guiApplication.getController().adminCalculateScore(guiApplication.getMyId());
        }catch(Exception e){
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }
    public void endGame(ActionEvent event) {
        try {
            guiApplication.getController().adminEndGame(guiApplication.getMyId());
            calculateScore.setVisible(true);
        }catch (Exception e) {
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }
}
