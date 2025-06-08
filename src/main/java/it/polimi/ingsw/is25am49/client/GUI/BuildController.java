package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.TileCardDeck;
import it.polimi.ingsw.is25am49.client.model.shipboard.Tile;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Orientation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//todo devo fare un'altra grid con più righe e colonne e metterla nel build fxml, in base alla gamemode
//todo ne metto una disable a true e non la uso
public class BuildController extends GenericSceneController {
    @FXML
    private GridPane cardContainer;
    @FXML
    private FlowPane buildContainer;
    @FXML
    private GridPane shipBoard;
    @FXML
    private TextField textFieldLookAtShipboard;
    @FXML
    private ListView<String> listViewPlayers;
    @FXML
    private Label playerInformation;
    @FXML
    private ImageView image;
    @FXML
    private ImageView backgroundImage;
    int count = 0;
    //associo a ogni id della tile l'immagine scoperta
    //salvo la posizione in cui salvo la tessera e il nodo corrispondente
    private final Map<Integer, Node> container = new HashMap<>();
    private final Map<Integer, Integer> tileMap = new HashMap<>();
    ImageView handTile = new ImageView();

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
    public void init(){
        loadTilesMap();
        updateListView();
        loadGrid(image, shipBoard);
        printTileCardDeck();
        printShipboard(shipBoard);

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


    private void updateListView(){
        ArrayList<Player> listPlayers = gameSession.getGame().get().getListPlayers();
        for(Player p : listPlayers){
            listViewPlayers.getItems().add(p.getUsername() + "  "+ p.getColor() + " (ID: " + p.getIdPlayer() + ")");
        }
        playerInformation.setText("You are " + gameSession.getPlayer(guiApplication.getMyId()).get().getUsername() + " (ID: " + guiApplication.getMyId() + ")");

    }
    public void printShipboard() {
        printShipboard(shipBoard);
    }

    private void printTileCardDeck(){
       Image image = new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tiles/GT-new_tiles_16_for web157.jpg").toExternalForm());
       TileCardDeck deck = gameSession.getGame().get().getGameboard().getTilesDeck();
       System.out.println("Numero totale di tile nel deck: " + deck.stream().toList().size());

       int columns = 13;
       int rows = 12;
       int maxTiles = columns * rows;


       cardContainer.getChildren().clear();
        // Aggiungi direttamente le tile nella griglia
        count = 0;
        for (Map.Entry<Integer, Tile> entry : deck.stream().toList()) {
            if (count >= maxTiles) break;  // Limita al massimo di celle disponibili


            int index = entry.getKey();
            Tile tile = entry.getValue();
            int tileId = tile.getIdTile();

            int col = count % columns;
            int row = count / columns;
            ImageView imageView;
            if(tile.getIsCovered()){
                // Calcola posizione nella griglia 12x13

                imageView = new ImageView(image);
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

            }
            else{
                // Calcola posizione nella griglia 12x13

                imageView = tilesMap.get(tileId);
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

            }
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
        try{
            Tile tile =gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getTile(coordinates);
            int idTile = tile.getIdTile();
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
                switch (tile.getTileOrientation()) {
                    case NORTH -> imageView.setRotate(0);
                    case EAST -> imageView.setRotate(90);
                    case SOUTH -> imageView.setRotate(180);
                    case WEST -> imageView.setRotate(270);
                }

                shipBoard.add(imageView, coordinates.x - 4, coordinates.y - 5);
            }
        }catch (Exception e){}

    }

    public void removeFromShipBoard(Point coordinates, int idTile) {
        try{
            ImageView imageView = tilesMap.get(idTile);
            if (imageView != null) {
                shipBoard.getChildren().remove(imageView);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
//    public void setHandTileFromPickTile(){
//        if(gameSession.getPlayer(guiApplication.getMyId()).get().getHandTile().isPresent()){
//            int idTile = gameSession.getPlayer(guiApplication.getMyId()).get().getHandTile().get().getIdTile();
//            ImageView imageView = tilesMap.get(idTile);
//            imageView.setFitHeight(181);
//            imageView.setFitWidth(181);
//            Orientation orientation = gameSession.getGame().get().getPlayer(guiApplication.getMyId()).getHandTile().get().getTileOrientation();
//            switch (orientation){
//                case NORTH -> imageView.setRotate(0);
//                case EAST -> imageView.setRotate(90);
//                case SOUTH -> imageView.setRotate(180);
//                case WEST -> imageView.setRotate(270);
//            }
//            buildContainer.getChildren().add(imageView);
//            handTile = imageView;
//    }}

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
            ImageView tileView = tilesMap.get(idTile);

            imageView.setImage(tileView.getImage());
            handTile = null;
//            System.out.println("Enabling tile with ID: " + idTile);
        }
       }


    public void handleRotation(ActionEvent event){
       try{
           gameSession.getState().userRotateTileClockWise(guiApplication.getMyId());
//           System.out.println("Bottone schiacciato");
       }catch (Exception e){
           guiApplication.printUpdateMessage(error+e.getMessage());
       }
    }
    public void rotateHandTile(){
        Tile handTile = gameSession.getPlayer(guiApplication.getMyId()).get().getHandTile().get();
        if(gameSession.getPlayer(guiApplication.getMyId()).get().getHandTile().isPresent()){
            int idTile = handTile.getIdTile();
            ImageView imageView = tilesMap.get(idTile);
            Orientation orientation = handTile.getTileOrientation();
            switch (orientation){
                case NORTH -> imageView.setRotate(0);
                case EAST -> imageView.setRotate(90);
                case SOUTH -> imageView.setRotate(180);
                case WEST -> imageView.setRotate(270);
            }
        }

    }
    public void setHandTile(int tileId){
        ImageView imageView = tilesMap.get(tileId);
        imageView.setFitHeight(181);
        imageView.setFitWidth(181);
        if (gameSession.getPlayer(guiApplication.getMyId()).get().getHandTile().isPresent()){
            Orientation orientation = gameSession.getGame().get().getPlayer(guiApplication.getMyId()).getHandTile().get().getTileOrientation();
            switch (orientation){
                case NORTH -> imageView.setRotate(0);
                case EAST -> imageView.setRotate(90);
                case SOUTH -> imageView.setRotate(180);
                case WEST -> imageView.setRotate(270);
            }
            buildContainer.getChildren().add(imageView);
            handTile = imageView;

    }}
    public void dragHandTile() {
        if(handTile!=null) {
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

    }


    public void handleDiscard(ActionEvent event) {
        try {
            guiApplication.getController().userDiscardTile(guiApplication.getMyId());
//            System.out.println("Bottone schiacciato");
        } catch (Exception e) {
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }

    public void handleLookAtShipboard(ActionEvent event) {
        try {
            int idPlayer = Integer.parseInt(textFieldLookAtShipboard.getText());
            if(idPlayer<0 || idPlayer > 3 || idPlayer == guiApplication.getMyId() || gameSession.getPlayer(idPlayer).isEmpty()) {
                guiApplication.printUpdateMessage(error + "Giocatore con ID " + idPlayer + " non trovato");

            }
            else{
                System.out.println("Visualizzazione shipboard del giocatore con ID: " + idPlayer);
                guiApplication.lookAtShipboard(idPlayer);
            }

        } catch (NumberFormatException e) {
            guiApplication.printUpdateMessage(error + "Inserisci un ID giocatore valido");
        }
    }

    public void returnBackToShipboard(ActionEvent event) {
        clearShipBoard();
        setupDragAndDropCells(); // Riconfigura le celle per il drag and drop
        printShipboard(shipBoard);
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
                    // Accetta il drag solo se la sorgente è handTile (non una tessera già posizionata)
                    if (event.getGestureSource() == handTile && event.getDragboard().hasImage()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                    event.consume();
                });

                cellContainer.setOnDragDropped(event -> {
                    // Verifica che la sorgente sia handTile prima di procedere
                    if (event.getGestureSource() == handTile) {
                        try {
                            guiApplication.getController().userPlaceTile(guiApplication.getMyId(),
                                (Point) cellContainer.getProperties().get("coordinates"),
                                gameSession.getPlayer(guiApplication.getMyId()).get().getHandTileOrientation());
                        } catch (Exception e) {
                            guiApplication.printUpdateMessage(error + e.getMessage());
                        }
                    }
                    event.consume();
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
