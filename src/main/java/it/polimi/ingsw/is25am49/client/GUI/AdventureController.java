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
    private ImageView shipBoardImage;
    @FXML
    private StackPane shipboard;
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
    @FXML
    private ImageView backgroundImage;
    @FXML
    private HBox cardDeckContainer;
    @FXML
    private ImageView cardDeckImage;
    @FXML
    private ImageView leaderboardImage;

    ShipBoard playerShipboard;

    public AdventureController() {

    }
    public void handleDrawButton() {

    }
    public void init(){
        loadTilesMap();
        printShipboard(shipboardGrid);
        loadGrid(shipBoardImage, shipboardGrid);

    }


}
