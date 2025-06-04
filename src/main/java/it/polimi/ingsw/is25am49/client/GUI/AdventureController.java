package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class AdventureController extends GenericSceneController {
    @FXML
    private HBox shipboard;
    @FXML
    private GridPane shipboardGrid;
    @FXML
    private StackPane leaderboard;
    @FXML
    private StackPane cardEffect;
    @FXML
    private Button drawButton;
    @FXML
    private ImageView currentCard;

    ShipBoard playerShipboard;

    public AdventureController() {

    }

    public void loadShipboard() {
        switch(gameSession.getGame().orElseThrow().getMode()){
            case LEVEL_1 -> {
                String path = "it/polimi/ingsw/is25am49/cardboard/cardboard-1.jpg";
                ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm()));
                imageView.setFitHeight(506);

            }
        }
        playerShipboard = gameSession.getGame().orElseThrow().getPlayer(guiApplication.getMyId()).getShipBoard();
        shipboardGrid.getChildren().clear();

        
    }


}
