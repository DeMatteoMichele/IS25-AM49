package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.PrimitiveIterator;

public class GenericSceneController {


    private GameSession gameSession;
    private GraphicUserInterface guiApplication;



    /**
     * Sets the GUI application instance associated with this controller.
     *
     * @param guiApplication The GUI application instance.
     */
    public void setGui(GraphicUserInterface guiApplication, GameSession gameSession) {
        this.guiApplication = guiApplication;
        this.gameSession = gameSession;
    }
    public GameSession  getGameSession() {
        return gameSession;
    }

    /**
     * Retrieves the GUI application instance associated with this controller.
     *
     * @return The GUI application instance.
     */
    public GraphicUserInterface getGui() {
        return guiApplication;
    }

    public Stage getStage() {
        return guiApplication.getPrimaryStage();




}}
