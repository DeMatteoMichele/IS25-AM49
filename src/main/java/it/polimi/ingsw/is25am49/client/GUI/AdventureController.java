package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.client.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;

import java.awt.*;
import java.util.Objects;

public class AdventureController extends GenericSceneController {
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
    private int counterStampe = 0;

    public void initialize(){
        Image image = new Image(getClass().getResource("/sfondo3.jpg").toExternalForm());
        backgroundImage.setImage(image);

        backgroundImage.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                backgroundImage.fitWidthProperty().bind(newScene.widthProperty());
                backgroundImage.fitHeightProperty().bind(newScene.heightProperty());
            }
        });
    }

    public void handleDrawButton() {
        try {
            guiApplication.getController().userDrawAdventureCard(guiApplication.getMyId());
//            System.out.println("Bottone schiacciato");
        } catch (Exception e) {
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }

    public void init() {
        loadTilesMap();
        loadGrid();
        printShipboard();
        loadAdventureCardsAndButton();
        loadLeaderBoard();
    }
    private void loadLeaderBoard() {
        if (getGameMode() == GameMode.FLIGHT_MODE || getGameMode() == GameMode.LEVEL_1){
            String path = "/it/polimi/ingsw/is25am49/cardboard/cardboard-3.png";
            Image image = new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
            leaderboardImage.setImage(image);
        }
        else if (getGameMode() == GameMode.LEVEL_2){
            String path = "/it/polimi/ingsw/is25am49/cardboard/cardboard-5.png";
            Image image = new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
            leaderboardImage.setImage(image);
        }
    }

    private void loadAdventureCardsAndButton() {
        AdventureCardDeck deck = gameSession.getGame().get().getGameboard().getMainAdventureDeck();
        deck.stream().forEach(adventureCard -> {
            String idCard = adventureCard.getCardId();
            String path = "/it/polimi/ingsw/is25am49/graphics/GT-cards_" + idCard + ".jpg";
            ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm()));
            adventureCardMap.put(idCard, imageView);
        });
        Image image;
        if (getGameMode() == GameMode.FLIGHT_MODE || getGameMode() == GameMode.LEVEL_1) {
            String pathBack = "/it/polimi/ingsw/is25am49/graphics/GT-cards_I121.jpg";
            image = new Image(Objects.requireNonNull(getClass().getResource(pathBack)).toExternalForm());
            BackgroundImage backgroundImageButton = new BackgroundImage(
                    image,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, true, true)
            );
            drawButton.setBackground(new Background(backgroundImageButton));
        } else if( getGameMode() == GameMode.LEVEL_2) {
            String pathBack = "/it/polimi/ingsw/is25am49/graphics/GT-cards_II121.jpg";
            image = new Image(Objects.requireNonNull(getClass().getResource(pathBack)).toExternalForm());
            BackgroundImage backgroundImageButton = new BackgroundImage(
                    image,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, true, true)
            );
            drawButton.setBackground(new Background(backgroundImageButton));
        }
    }

    public void updateCurrentCard(String cardId) {
        if (cardId == null || cardId.isEmpty()) {
            guiApplication.printUpdateMessage(error + "Error with cardId " + cardId);
        } else {
            ImageView imageView = adventureCardMap.get(cardId);
            if (imageView != null) {
                currentCard.setImage(imageView.getImage());
            } else {
                guiApplication.printUpdateMessage(error + "Adventure Card not found: " + cardId);
            }
        }
    }

    private GameMode getGameMode(){
        if (gameSession.getGame().isPresent()) {
            return gameSession.getGame().get().getMode();
        }
        else {
            guiApplication.printUpdateMessage(error + "No game session available.");
            return null;
        }
    }
    private AdventureCard getCurrentCard() {
        if (gameSession.getGame().isPresent()) {
            return gameSession.getGame().get().getGameboard().getCurrentCard();
        } else {
            guiApplication.printUpdateMessage(error + "No game session available.");
            return null;
        }
    }
    private void dbg(){
        System.out.println("Debug" + counterStampe++);
    }
    public void printShipboard() {

        gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getGrid().forEach((coordinates, tile) -> {
            if (tile.isPresent()) {
                int idTile = tile.get().getIdTile();


                ImageView imageView = tilesMap.get(idTile);
                if (imageView != null) {
                    imageView.setFitWidth(60);
                    imageView.setFitHeight(60);
                    imageView.setUserData(idTile);
                    imageView.setOnMouseClicked(event -> {
                        clickTile();
                    });
                    // Imposta la rotazione dell'immagine in base all'orientamento della tile
                    switch (tile.get().getTileOrientation()) {
                        case NORTH -> imageView.setRotate(0);
                        case EAST -> imageView.setRotate(90);
                        case SOUTH -> imageView.setRotate(180);
                        case WEST -> imageView.setRotate(270);
                    }


                    shipboardGrid.add(imageView, coordinates.x - 4, coordinates.y - 5);
                }
            }
        });}

    public void loadGrid() {
        if(getGameMode() == GameMode.LEVEL_1 || getGameMode()== GameMode.FLIGHT_MODE){
            Image im = new Image(Objects.requireNonNull(getClass().getResource("/it/polimi/ingsw/is25am49/cardboard/cardboard-1.jpg")).toExternalForm());
            BackgroundImage backgroundImageGrid = new BackgroundImage(
                    im,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, true, true)
            );
            shipboardGrid.setBackground(new Background(backgroundImageGrid));
            int rows = 5;
            int columns = 7;
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    StackPane cellContainer = new StackPane();
                    cellContainer.getProperties().put("coordinates", new Point(col+4, row+5));
                    cellContainer.setStyle("-fx-background-color: transparent;"); // Contenitore trasparente
                    shipboardGrid.add(cellContainer, col, row);
                }
            }}
        else if(getGameMode() == GameMode.LEVEL_2){
            Image im = new Image(Objects.requireNonNull(getClass().getResource("/it/polimi/ingsw/is25am49/cardboard/cardboard-1b.jpg")).toExternalForm());
            BackgroundImage backgroundImageGrid = new BackgroundImage(
                    im,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, true, true)
            );
            shipboardGrid.setBackground(new Background(backgroundImageGrid));
            int rows = 5;
            int columns = 7;
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    StackPane cellContainer = new StackPane();
                    cellContainer.getProperties().put("coordinates", new Point(col+4, row+5));
                    cellContainer.setStyle("-fx-background-color: transparent;"); // Contenitore trasparente
                    shipboardGrid.add(cellContainer, col, row);
                }
            }}
    }

    private void clickTile() {

    }
}
