package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.TileCardDeck;
import it.polimi.ingsw.is25am49.client.model.shipboard.Tile;
import it.polimi.ingsw.is25am49.client.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Orientation;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.awt.*;
import java.util.ArrayList;
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
    @FXML
    private ListView<String> listViewPlayers;
    @FXML
    private Label playerInformation;
    @FXML
    private ImageView image;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private Label timerLabel;
    @FXML
    private HBox smallAdventureDeckContainer;
    @FXML
    private HBox smallAdventureDeck;
    @FXML
    private Button stopLooking;
    int count = 0;
    //associo a ogni id della tile l'immagine scoperta
    private final Map<Integer, ImageView> tilesMap = new HashMap<>();
    //salvo la posizione in cui salvo la tessera e il nodo corrispondente
    private final Map<Integer, Node> container = new HashMap<>();
    private final Map<Integer, Integer> tileMap = new HashMap<>();
    //associa a ogni id della carta la sua immagine
    private final Map<String, ImageView> adventureCardMap = new HashMap<>();
    ImageView handTile = new ImageView();
    int deckIndex;



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
        loadGrid();
        printTileCardDeck();
        printShipboard();
        loadAdventureCards();

    }

    private void loadAdventureCards(){
        Image image1 = new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tiles/GT-new_tiles_16_for web157.jpg").toExternalForm());
        if(gameSession.getGame().get().getMode()== GameMode.FLIGHT_MODE){
          AdventureCardDeck deck = gameSession.getGame().get().getGameboard().getMainAdventureDeck();
          deck.stream().forEach(adventureCard -> {
              String idCard = adventureCard.getCardId();
              System.out.println("Adventure Card ID: " + idCard);
              String path = "/it/polimi/ingsw/is25am49/graphics/GT-cards_" + idCard + ".jpg";
              ImageView imageView = new ImageView(new Image(getClass().getResource(path).toExternalForm()));
              System.out.println("trovata immagine per la carta: " + idCard);
              adventureCardMap.put(idCard, imageView);
              smallAdventureDeckContainer.setVisible(false);
              stopLooking.setVisible(false);
              smallAdventureDeck.setVisible(false);
        });
    }
    else if(gameSession.getGame().get().getMode()== GameMode.LEVEL_1){
        for(int i=0; i<4;i++){
            for(int j=0;j<gameSession.getGame().get().getGameboard().getSmallAdventureDeck(i).size();j++){
                AdventureCard adventureCard = gameSession.getGame().get().getGameboard().getSmallAdventureDeck(i).getCard(j);
                String idCard = adventureCard.getCardId();
                System.out.println("Adventure Card ID: " + idCard);
                String path = "/it/polimi/ingsw/is25am49/graphics/GT-cards_" + idCard + ".jpg";
                ImageView imageView = new ImageView(new Image(getClass().getResource(path).toExternalForm()));
                System.out.println("trovata immagine per la carta: " + idCard);
                adventureCardMap.put(idCard, imageView);
            }
            if(i<3){
           smallAdventureDeckContainer.setSpacing(10);
           ImageView adventureDeckImage = new ImageView(image1);
           adventureDeckImage.setFitHeight(90);
           adventureDeckImage.setFitWidth(60);
            HBox.setMargin(adventureDeckImage, new javafx.geometry.Insets(5, 10, 5, 10));
           smallAdventureDeckContainer.getChildren().add(adventureDeckImage);
           int index = i;
           adventureDeckImage.setOnMouseClicked(event->{
               try{
                   guiApplication.getController().userLookSmallAdventureCardDeck(guiApplication.getMyId(), index);
                   System.out.println("Clicked on small adventure deck: " + index);
               }catch (Exception e){
                     guiApplication.printUpdateMessage(error + e.getMessage());
               }
           });}
        }
    }
    else if(gameSession.getGame().get().getMode()== GameMode.LEVEL_2){
            for(int i=0; i<4;i++){
                for(int j=0;j<gameSession.getGame().get().getGameboard().getSmallAdventureDeck(i).size();j++){
                    AdventureCard adventureCard = gameSession.getGame().get().getGameboard().getSmallAdventureDeck(i).getCard(j);
                    String idCard = adventureCard.getCardId();
                    System.out.println("Adventure Card ID: " + idCard);
                    String path = "/it/polimi/ingsw/is25am49/graphics/GT-cards_" + idCard + ".jpg";
                    ImageView imageView = new ImageView(new Image(getClass().getResource(path).toExternalForm()));
                    System.out.println("trovata immagine per la carta: " + idCard);
                    adventureCardMap.put(idCard, imageView);

                }
                if(i<3){
                smallAdventureDeckContainer.setSpacing(10);
                ImageView adventureDeckImage = new ImageView(image1);
                adventureDeckImage.setFitHeight(90);
                adventureDeckImage.setFitWidth(60);
                HBox.setMargin(adventureDeckImage, new javafx.geometry.Insets(5, 10, 5, 10));
                smallAdventureDeckContainer.getChildren().add(adventureDeckImage);
                int index = i;
                adventureDeckImage.setOnMouseClicked(event->{
                    try{
                        guiApplication.getController().userLookSmallAdventureCardDeck(guiApplication.getMyId(), index);
                        System.out.println("Clicked on small adventure deck: " + index);
                    }catch (Exception e){
                        guiApplication.printUpdateMessage(error + e.getMessage());
                    }
                });}
        }
        }
    }
    private void loadTilesMap() {
        // Carica le immagini delle tile e le associa agli ID
        for (int j = 1; j < 157; j++) {

            String path = "/it/polimi/ingsw/is25am49/tiles/GT-new_tiles_16_for web" + j + ".jpg";
            ImageView imageView = new ImageView(new Image(getClass().getResource(path).toExternalForm()));
            imageView.setFitWidth(40);
            imageView.setFitHeight(60);
            tilesMap.put(j, imageView);
        }
    }

    public void clearListView(){
        listViewPlayers.getItems().clear();
    }
    public void updateListView(){
        ArrayList<Player> listPlayers = gameSession.getGame().get().getListPlayers();
        for(Player p : listPlayers){
            listViewPlayers.getItems().add(p.getUsername() + "  "+ p.getColor() + " (ID: " + p.getIdPlayer() + ")");
        }
        playerInformation.setText("You are " + gameSession.getPlayer(guiApplication.getMyId()).get().getUsername() + " (ID: " + guiApplication.getMyId() + ")\n" + "lostComponents: "+ gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getDestroyedTiles());

    }
   private void loadGrid() {
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
                imageView.setFitWidth(30);
                imageView.setFitHeight(30);
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

    public void printShipboard(){

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
                   switch (tile.get().getTileOrientation()){
                       case NORTH -> imageView.setRotate(0);
                       case EAST -> imageView.setRotate(90);
                       case SOUTH -> imageView.setRotate(180);
                       case WEST -> imageView.setRotate(270);
                   }


                   shipBoard.add(imageView, coordinates.x - 4, coordinates.y - 5);
               }
           }
       });


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
    public void showCountDown(){


        int timerDuration = 30;
        final int[] timeLeft = {timerDuration};


        Timeline timeline = new Timeline();
        timeline.setCycleCount(timerDuration);

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), e -> {

            timeLeft[0]--;
            timerLabel.setText("⏱00:"+ timeLeft[0]);


            if (timeLeft[0] <= 0) {
                timerLabel.setText("⏱ 00:00");

            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    public void playTimerEndAnimation() {
        timerLabel.setText("⏱ TEMPO SCADUTO!");

        // Animazione di lampeggio
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), timerLabel);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.2);
        fadeTransition.setCycleCount(6);
        fadeTransition.setAutoReverse(true);

        // Animazione di ingrandimento/rimpicciolimento
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(1200), timerLabel);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);

        // Esegui entrambe le animazioni in parallelo
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition, scaleTransition);
        parallelTransition.play();

        // Ripristina il testo originale dopo l'animazione
        parallelTransition.setOnFinished(event -> {
            timerLabel.setText("⏱ 00:00");
        });
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


//            System.out.println("Enabling tile with ID: " + idTile);
        }
       }


    public void handleRotation(ActionEvent event){
       try{
           System.out.println("ROTATE");
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
            handTile= imageView;


    }}
    public void dragHandTile() {
        if(gameSession.getPlayer(guiApplication.getMyId()).get().getHandTile().isPresent()) {
//            Tile tile = gameSession.getPlayer(guiApplication.getMyId()).get().getHandTile().get();
//            int idTile = tile.getIdTile();
//            handTile = tilesMap.get(idTile);

            if (handTile != null) {
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
    public void clearSmallAdventureDeck() {
        smallAdventureDeck.getChildren().clear();
    }
    public void showAdventureCardDeck(int smallDeckIndex) {
        deckIndex = smallDeckIndex;
        smallAdventureDeck.getChildren().clear();
        AdventureCardDeck deck = gameSession.getGame().get().getGameboard().getSmallAdventureDeck(smallDeckIndex);
        for (int i = 0; i < deck.size(); i++) {
            AdventureCard card = deck.getCard(i);
            String idCard = card.getCardId();
            ImageView imageView = adventureCardMap.get(idCard);
            if (imageView != null) {
                imageView.setFitWidth(100);
                imageView.setFitHeight(170);
                smallAdventureDeck.getChildren().add(imageView);
                smallAdventureDeck.setSpacing(20);
            } else {
                System.out.println("Immagine non trovata per la carta ID: " + idCard);
            }
        }
    }
    public void stopLooking(ActionEvent event) {
        try{
            guiApplication.getController().userStopLookSmallAdventureCardDeck(guiApplication.getMyId(), deckIndex);
        }catch (Exception e){
            guiApplication.printUpdateMessage(error + e.getMessage());
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
