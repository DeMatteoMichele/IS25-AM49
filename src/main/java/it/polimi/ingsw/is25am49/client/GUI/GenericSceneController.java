package it.polimi.ingsw.is25am49.client.GUI;

import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.PrimitiveIterator;

public class GenericSceneController {



    private GraphicUserInterface guiApplication;

    @FXML


    /**
     * Sets the GUI application instance associated with this controller.
     *
     * @param guiApplication The GUI application instance.
     */
    public void setGui(GraphicUserInterface guiApplication) {
        this.guiApplication = guiApplication;
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
