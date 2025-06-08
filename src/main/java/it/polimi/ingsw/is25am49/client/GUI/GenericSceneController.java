package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.PrimitiveIterator;

public abstract class GenericSceneController {
    protected GameSession gameSession;
    protected GraphicUserInterface guiApplication;
    protected String error = "⚠";
    protected final Map<Integer, ImageView> tilesMap = new HashMap<>();

    /**
     * Sets the GUI application instance associated with this controller.
     *
     * @param guiApplication The GUI application instance.
     */
    public void setGui(GraphicUserInterface guiApplication, GameSession gameSession) {
        this.guiApplication = guiApplication;
        this.gameSession = gameSession;
    }

    public void loadTilesMap() {
        // Carica le immagini delle tile e le associa agli ID
        for (int j = 1; j < 157; j++) {

            String path = "/it/polimi/ingsw/is25am49/tiles/GT-new_tiles_16_for web" + j + ".jpg";
            ImageView imageView = new ImageView(new Image(getClass().getResource(path).toExternalForm()));
            imageView.setFitWidth(40);
            imageView.setFitHeight(60);
            tilesMap.put(j, imageView);
        }
    }

    public void printShipboard(GridPane shipBoard) {

        gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getGrid().forEach((coordinates, tile) -> {
            if (tile.isPresent()) {
                int idTile = tile.get().getIdTile();


                ImageView imageView = tilesMap.get(idTile);
                if (imageView != null) {
                    imageView.setFitWidth(60);
                    imageView.setFitHeight(60);
                    imageView.setUserData(idTile);
                    imageView.setOnMouseClicked(event -> {
                        try {
                            guiApplication.getController().userPickTile(guiApplication.getMyId(), coordinates);
                        } catch (Exception e) {
                            guiApplication.printUpdateMessage(error + e.getMessage());
                        }
                    });
                    // Imposta la rotazione dell'immagine in base all'orientamento della tile
                    switch (tile.get().getTileOrientation()) {
                        case NORTH -> imageView.setRotate(0);
                        case EAST -> imageView.setRotate(90);
                        case SOUTH -> imageView.setRotate(180);
                        case WEST -> imageView.setRotate(270);
                    }


                    shipBoard.add(imageView, coordinates.x - 4, coordinates.y - 5);
                }
            }
        });}

        public void loadGrid(ImageView image, GridPane shipBoard) {
            if(gameSession.getGame().get().getMode()== GameMode.LEVEL_1 || gameSession.getGame().get().getMode()== GameMode.FLIGHT_MODE){
                Image im = new Image(getClass().getResource("/it/polimi/ingsw/is25am49/cardboard/cardboard-1.jpg").toExternalForm());
                image.setImage(im);
                int rows = 5;
                int columns = 7;
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < columns; col++) {
                        StackPane cellContainer = new StackPane();

                        cellContainer.getProperties().put("coordinates", new Point(col+4, row+5));
                        cellContainer.setStyle("-fx-background-color: transparent;"); // Contenitore trasparente
                        cellContainer.setOnDragOver(event -> {
                            if (event.getGestureSource() != cellContainer && event.getDragboard().hasImage()) {

                                event.acceptTransferModes(TransferMode.MOVE);
                            }
                            event.consume();
                        });

                        cellContainer.setOnDragDropped(event->{
                            try{
                                guiApplication.getController().userPlaceTile(guiApplication.getMyId(), (Point) cellContainer.getProperties().get("coordinates"),gameSession.getPlayer(guiApplication.getMyId()).get().getHandTileOrientation());

                            }catch (Exception e){
                                guiApplication.printUpdateMessage(error + e.getMessage());
                            }

                        });

                        shipBoard.add(cellContainer, col, row);
                    }
                }}
            else if(gameSession.getGame().get().getMode()== GameMode.LEVEL_2){
                Image im = new Image(getClass().getResource("/it/polimi/ingsw/is25am49/cardboard/cardboard-1b.jpg").toExternalForm());
                image.setImage(im);
                int rows = 5;
                int columns = 7;
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < columns; col++) {
                        StackPane cellContainer = new StackPane();

                        cellContainer.getProperties().put("coordinates", new Point(col+4, row+5));
                        cellContainer.setStyle("-fx-background-color: transparent;"); // Contenitore trasparente
                        cellContainer.setOnDragOver(event -> {
                            if (event.getGestureSource() != cellContainer && event.getDragboard().hasImage()) {

                                event.acceptTransferModes(TransferMode.MOVE);
                            }
                            event.consume();
                        });

                        cellContainer.setOnDragDropped(event->{
                            try{
                                guiApplication.getController().userPlaceTile(guiApplication.getMyId(), (Point) cellContainer.getProperties().get("coordinates"),gameSession.getPlayer(guiApplication.getMyId()).get().getHandTileOrientation());

                            }catch (Exception e){
                                guiApplication.printUpdateMessage(error + e.getMessage());
                            }

                        });

                        shipBoard.add(cellContainer, col, row);
                    }
                }}

        }

    }
