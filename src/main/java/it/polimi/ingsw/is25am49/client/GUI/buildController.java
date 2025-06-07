package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.model.TileCardDeck;
import it.polimi.ingsw.is25am49.client.model.shipboard.Tile;
import it.polimi.ingsw.is25am49.shared.enums.Orientation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextField;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class buildController extends GenericSceneController {
    @FXML
    private GridPane cardContainer;
    @FXML
    private FlowPane buildContainer;
    @FXML
    private GridPane shipBoard;
    @FXML
    private TextField textFieldLookAtShipboard;
    //salvo la posizione in cui salvo la tessera e il nodo corrispondente
    private final Map<Integer, Node> container = new HashMap<>();
    private final Map<Integer,Integer> tileMap = new HashMap<>();
    ImageView handTile = new ImageView();

    public ImageView getTileImageView(int tileId) {
        Tile tile = getTileById(tileId);
        if(tile.getIsCovered()){
            Image image = new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tiles/GT-new_tiles_16_for web157.jpg").toExternalForm());
            ImageView imageView = new ImageView(image);
            return imageView;
        }
        else if(!tile.getIsCovered()){
            return tilesMap.get(tileId);
        }
       return null;
    }

    public Tile getTileById(int tileId) {
        return gameSession.getGame().get().getGameboard().getTilesDeck().stream()
                .filter(entry -> entry.getValue().getIdTile() == tileId)
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }





   public void loadCards() {
       Image image = new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tiles/GT-new_tiles_16_for web157.jpg").toExternalForm());
       TileCardDeck deck = gameSession.getGame().get().getGameboard().getTilesDeck();
       System.out.println("Numero totale di tile nel deck: " + deck.stream().toList().size());

       int columns = 13;
       int rows = 12;
       int maxTiles = columns * rows;

// Pulisci eventuali elementi esistenti nella griglia
        cardContainer.getChildren().clear();
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
                       guiApplication.getController().userPlaceTile(guiApplication.getMyId(), (Point) cellContainer.getProperties().get("coordinates"),Orientation.NORTH);

//                       cellContainer.getChildren().add(handTile);
//                       handTile.fitWidthProperty().bind(cellContainer.widthProperty());
//                       handTile.fitHeightProperty().bind(cellContainer.heightProperty());
//                       clearHandTiles();
                   }catch (Exception e){
                       guiApplication.printUpdateMessage(error + e.getMessage());
                   }

               });

               shipBoard.add(cellContainer, col, row);
           }
       }
        // Aggiungi direttamente le tile nella griglia
        int count = 0;
        for (Map.Entry<Integer, Tile> entry : deck.stream().toList()) {
            if (count >= maxTiles) break;  // Limita al massimo di celle disponibili

            int index = entry.getKey();
            Tile tile = entry.getValue();
            int tileId = tile.getIdTile();

            // Calcola posizione nella griglia 12x13
            int col = count % columns;
            int row = count / columns;

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(40);
            imageView.setFitHeight(40);
            imageView.setUserData(tileId);
            imageView.setOnMouseClicked(event -> {
                int clickedTileId = (int) imageView.getUserData();
                try {
                    guiApplication.getController().userDrawTile(guiApplication.getMyId(), clickedTileId);
                } catch (Exception e) {
                    guiApplication.printUpdateMessage(error+ e.getMessage());
                }
            });

            // Aggiungi direttamente l'immagine nella posizione corretta
            cardContainer.add(imageView, col, row);

            container.put(index, imageView);
            tileMap.put(tileId, index);
            count++;
        }
    }
    public void handleFinishShipBoard(ActionEvent event){
        try {
            guiApplication.getController().userFinishShipboard(guiApplication.getMyId());
        } catch (Exception e) {
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }
    public void clearShipBoard() {
        shipBoard.getChildren().clear();
    }
//mi serve anche quando faccio finishShipboard
    public void printShipboard(){
       gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getGrid().forEach((coordinates, tile) -> {
           if (tile.isPresent()) {
               int idTile = tile.get().getIdTile();


               ImageView imageView = tilesMap.get(idTile);
               if (imageView != null) {
                imageView.setFitWidth(60);
                imageView.setFitHeight(60);
//                     imageView.setUserData(idTile);
//                     imageView.setOnMouseClicked(event -> {
//                          try {
//                            guiApplication.getController().userPickTile(guiApplication.getMyId(), coordinates);
//                          } catch (Exception e) {
//                            guiApplication.printUpdateMessage(error + e.getMessage());
//                          }
//                     });
                     // Imposta la rotazione dell'immagine in base all'orientamento della tile
                   switch (tile.get().getTileOrientation()){
                       case NORTH -> imageView.setRotate(0);
                       case EAST -> imageView.setRotate(90);
                       case SOUTH -> imageView.setRotate(180);
                       case WEST -> imageView.setRotate(270);
                   }

                   shipBoard.getChildren().remove(imageView); // Rimuove il nodo se già presente
                   shipBoard.add(imageView, coordinates.x - 4, coordinates.y - 5);
               }
           }
       });
        System.out.println(gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().toString());
           }

    public void clearHandTiles() {
        buildContainer.getChildren().clear();
    }
    public void handleTimer(ActionEvent event) {
        try {
            guiApplication.getController().userPlaceTimer(guiApplication.getMyId());
        }catch (Exception e){
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }


    public void addToShipBoard(Point coordinates) {
        Tile tile =gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getTile(coordinates);
        int idTile = tile.getIdTile();
        ImageView imageView = tilesMap.get(idTile);
        if (imageView != null) {
            imageView.setFitWidth(60);
            imageView.setFitHeight(60);
//            imageView.setUserData(idTile);
//            imageView.setOnMouseClicked(event -> {
//                try {
//                    guiApplication.getController().userPickTile(guiApplication.getMyId(), coordinates);
//                } catch (Exception e) {
//                    guiApplication.printUpdateMessage(error + e.getMessage());
//                }
//            });

            // Imposta la rotazione dell'immagine in base all'orientamento della tile
            switch (tile.getTileOrientation()) {
                case NORTH -> imageView.setRotate(0);
                case EAST -> imageView.setRotate(90);
                case SOUTH -> imageView.setRotate(180);
                case WEST -> imageView.setRotate(270);
            }

            shipBoard.add(imageView, coordinates.x - 4, coordinates.y - 5);
        }
    }

    public void disableTile(int idTile) {
        int index = tileMap.get(idTile);
        Node node = container.get(index);
        if(node instanceof ImageView){
            ImageView imageView = (ImageView) node;
            imageView.setOpacity(0.5);
            imageView.setDisable(true);


//            System.out.println("disable tile with ID: " + idTile);
        }
    }
    public void enableTile(int idTile){
        int index = tileMap.get(idTile);
        Node node = container.get(index);
        if(node instanceof ImageView){
            ImageView imageView = (ImageView) node;
            imageView.setOpacity(1.0);
            imageView.setDisable(false);
            ImageView tileView = getTileImageView(idTile);

            imageView.setImage(tileView.getImage());
            handTile = null;
//            System.out.println("Enabling tile with ID: " + idTile);
        }
       }
//    public void drawTile(int idTile) {
//        if(gameSession.getPlayer(guiApplication.getMyId()).get().getHandTile().isPresent()){
//        //            buildContainer.getChildren().clear();
//            buildContainer.getChildren().add(getTileImageView(idTile));
//        }
//        int index = tileMap.get(idTile);
//        ImageView imageView = (ImageView) container.get(index);
//        imageView.setDisable(true);
//        imageView.setOpacity(0.5);
//        }


    public void handleRotation(ActionEvent event){
       try{
           gameSession.getState().userRotateTileClockWise(guiApplication.getMyId());
//           System.out.println("Bottone schiacciato");
       }catch (Exception e){
           guiApplication.printUpdateMessage(error+e.getMessage());
       }
    }
    public void rotateHandTile(){
//        System.out.println("Rotating hand tile");
//        Node handTileNode = buildContainer.getChildren().getFirst();
//        handTileNode.setRotate(handTileNode.getRotate() + 90);
        System.out.println(gameSession.getGame().get().getPlayer(guiApplication.getMyId()).getHandTile().get().getTileOrientation().toString());
        int idTile = gameSession.getPlayer(guiApplication.getMyId()).get().getHandTile().get().getIdTile();
        ImageView imageView = tilesMap.get(idTile);
        Orientation orientation = gameSession.getGame().get().getPlayer(guiApplication.getMyId()).getHandTile().get().getTileOrientation();
        switch (orientation){
            case NORTH -> imageView.setRotate(0);
            case EAST -> imageView.setRotate(90);
            case SOUTH -> imageView.setRotate(180);
            case WEST -> imageView.setRotate(270);
        }


    }
    public void setHandTile(int tileId){
        ImageView imageView = tilesMap.get(tileId);
        imageView.setFitHeight(181);
        imageView.setFitWidth(181);
        Orientation orientation = gameSession.getGame().get().getPlayer(guiApplication.getMyId()).getHandTile().get().getTileOrientation();
        switch (orientation){
            case NORTH -> imageView.setRotate(0);
            case EAST -> imageView.setRotate(90);
            case SOUTH -> imageView.setRotate(180);
            case WEST -> imageView.setRotate(270);
        }
        buildContainer.getChildren().add(imageView);
        handTile = imageView;

    }
    public void dragHandTile() {
        handTile.setOnDragDetected(event -> {
            Dragboard db = handTile.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            // Crea un'immagine temporanea che riflette la rotazione corrente
            Image rotatedImage = handTile.snapshot(null, null);
            content.putImage(rotatedImage);

            db.setContent(content);
            event.consume();
        });

        handTile.setOnDragOver(event -> {
            if (event.getGestureSource() != handTile && event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        handTile.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasImage()) {
                handTile.setImage(db.getImage());
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });

    }


    public void handleDiscard(ActionEvent event) {
        try {
            guiApplication.getController().userDiscardTile(guiApplication.getMyId());
//            System.out.println("Bottone schiacciato");
        } catch (Exception e) {
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }
    //todo devo cotrollare che il player esista
    public void handleLookAtShipboard(ActionEvent event) {
        try {
            int idPlayer = Integer.parseInt(textFieldLookAtShipboard.getText());
            System.out.println("Visualizzazione shipboard del giocatore con ID: " + idPlayer);
            guiApplication.lookAtShipboard(idPlayer);
        } catch (NumberFormatException e) {
            guiApplication.printUpdateMessage(error + "Inserisci un ID giocatore valido");
        }
    }

    public void returnBackToShipboard(ActionEvent event) {
        clearShipBoard();
        setupDragAndDropCells(); // Riconfigura le celle per il drag and drop
        printShipboard();
    }

    private void setupDragAndDropCells() {
        int columns = 13;
        int rows = 12;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                StackPane cellContainer = new StackPane();

                cellContainer.getProperties().put("coordinates", new Point(col + 4, row + 5));
                cellContainer.setStyle("-fx-background-color: transparent;");
                cellContainer.setOnDragOver(event -> {
                    if (event.getGestureSource() != cellContainer && event.getDragboard().hasImage()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                    event.consume();
                });

                cellContainer.setOnDragDropped(event -> {
                    try {
                        gameSession.getState().userPlaceTile(guiApplication.getMyId(),
                                (Point) cellContainer.getProperties().get("coordinates"));
                    } catch (Exception e) {
                        guiApplication.printUpdateMessage(error + e.getMessage());
                    }
                });

                shipBoard.add(cellContainer, col, row);
            }
        }

    }
   public void printPlayerIdShipboard(int playerId) {
       System.out.println("Visualizzazione shipboard del giocatore: " + playerId);
       // Prima pulisci la shipboard
       shipBoard.getChildren().clear();

       // Poi aggiungi ogni tile
       var player = gameSession.getPlayer(playerId);
       if (player.isEmpty()) {
           guiApplication.printUpdateMessage(error + "Giocatore con ID " + playerId + " non trovato");
           return;
       }

       var grid = player.get().getShipBoard().getGrid();
       System.out.println(player.get().getShipBoard().toString());
       System.out.println("Numero di tile nella shipboard: " + grid.size());

       grid.forEach((coordinates, tile) -> {
           if (tile.isPresent()) {
               int idTile = tile.get().getIdTile();
               System.out.println("Trovata tile con ID: " + idTile + " alle coordinate: " + coordinates.x + "," + coordinates.y);

               // Ottieni l'immagine originale
               ImageView originalImageView = tilesMap.get(idTile);
               if (originalImageView != null) {
                   // Crea una nuova ImageView con la stessa immagine
                   ImageView imageView = new ImageView(originalImageView.getImage());
                   imageView.setFitWidth(60);
                   imageView.setFitHeight(60);

                   // Imposta la rotazione dell'immagine in base all'orientamento della tile
                   switch (tile.get().getTileOrientation()){
                       case NORTH -> imageView.setRotate(0);
                       case EAST -> imageView.setRotate(90);
                       case SOUTH -> imageView.setRotate(180);
                       case WEST -> imageView.setRotate(270);
                   }

                   // Aggiungi la nuova ImageView alla griglia
                   shipBoard.add(imageView, coordinates.x - 4, coordinates.y - 5);
               } else {
                   System.out.println("Immagine non trovata per la tile ID: " + idTile);
               }
           }
       });
   }
}
