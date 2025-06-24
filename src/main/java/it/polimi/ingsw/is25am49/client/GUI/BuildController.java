package it.polimi.ingsw.is25am49.client.GUI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am49.client.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.client.model.Game;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.TileCardDeck;
import it.polimi.ingsw.is25am49.client.model.shipboard.Tile;
import it.polimi.ingsw.is25am49.client.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Orientation;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    @FXML
    private Label timerLabel;
    @FXML
    private HBox smallAdventureDeckContainer;
    @FXML
    private HBox smallAdventureDeck;
    @FXML
    private Button stopLooking;
    @FXML
    private Button discardTile;
    @FXML
    private Button rotateTile;
    @FXML
    private Button timer;
    @FXML
    private Button finish;
    @FXML
    private Button lookAtShipboard;
    @FXML
    private Button backToShipboard;
    @FXML
    private HBox astronautsOrAliens;
    @FXML
    private Label chooseAliens;
    @FXML
    private Label waitingLabel;
    @FXML
    private Button build1;
    @FXML
    private Button build2;
    @FXML
    private Button build3;
    @FXML
    private Button build4;
    StackPane highlightPane = new StackPane();
    int count = 0;
    //salvo la posizione in cui salvo la tessera e il nodo corrispondente
    private final Map<Integer, Node> container = new HashMap<>();
    private final Map<Integer, Integer> tileMap = new HashMap<>();

    ImageView handTile = new ImageView();
    int deckIndex;



    public void initialize(){
        Image image = new Image(getClass().getResource("/sfondo3.jpg").toExternalForm());
        backgroundImage.setImage(image);
        astronautsOrAliens.setDisable(true);
        astronautsOrAliens.setVisible(false);
        chooseAliens.setVisible(false);
        chooseAliens.setDisable(true);
        waitingLabel.setVisible(false);
        waitingLabel.setDisable(true);
        loadTokens();

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
        if(gameSession.getGame().get().getMode()==GameMode.FLIGHT_MODE){
            timer.setDisable(true);
            timer.setVisible(false);
            timerLabel.setVisible(false);
            timerLabel.setDisable(true);
            stopLooking.setVisible(false);
            stopLooking.setDisable(true);
            smallAdventureDeckContainer.setVisible(false);
            smallAdventureDeckContainer.setDisable(true);
        }
    }
    public void loadGrid() {
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
                    if(tile.get().getComponent().getType()== ComponentType.CABINE){
                     //creo hbox per affiancarli
                        HBox tokenContainer = new HBox();
                        tokenContainer.setSpacing(5); // Spazio tra i token
                        tokenContainer.setAlignment(javafx.geometry.Pos.CENTER);

                        //aggiungo token
                       new ArrayList<>(tile.get().getComponent().getTokenList()).forEach(token -> {

                                ImageView tokenImageView;
                                Color playerColor = gameSession.getPlayer(guiApplication.getMyId()).get().getColor();
                                if(token==Token.ASTRONAUT) {
                                    //se il token è un astronauta, prendo il colore del giocatore
                                    playerColor = gameSession.getPlayer(guiApplication.getMyId()).get().getColor();

                                    switch (playerColor) {
                                        case RED:
                                            tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Red_astronaut.png").toExternalForm()));
                                            break;
                                        case BLUE:
                                            tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Blue_astronaut.png").toExternalForm()));
                                            break;
                                        case GREEN:
                                            tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Green_astronaut.png").toExternalForm()));
                                            break;
                                        case YELLOW:
                                            tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Yellow_astronaut.png").toExternalForm()));
                                            break;
                                        default:
                                            // Immagine predefinita in caso di colore non gestito
                                            tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Red_astronaut.png").toExternalForm()));
                                    }
                                }
                                else{
                                    tokenImageView = tokensMap.get(token);
                                }
                                tokenImageView.setFitWidth(23);
                                tokenImageView.setFitHeight(32);
                                tokenContainer.getChildren().add(tokenImageView);

                        });

                        // aggiungo box al grid
                        shipBoard.add(tokenContainer, coordinates.x-4, coordinates.y-5);
                        GridPane.setHalignment(tokenContainer, javafx.geometry.HPos.CENTER);
                    }
                }
                    }

        });}

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


    public void clearListView(){
        listViewPlayers.getItems().clear();
    }
    public void updateListView(){
        ArrayList<Player> listPlayers = gameSession.getGame().get().getListPlayers();
        for(Player p : listPlayers){
            listViewPlayers.getItems().add(p.getUsername() + "  "+ p.getColor() + " (ID: " + p.getIdPlayer() + ")");
        }
        ImageView playerIcon;
        Color playerColor = gameSession.getPlayer(guiApplication.getMyId()).get().getColor();

        switch (playerColor) {
            case RED:
                playerIcon = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Red_astronaut.png").toExternalForm()));
                break;
            case BLUE:
                playerIcon = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Blue_astronaut.png").toExternalForm()));
                break;
            case GREEN:
                playerIcon = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Green_astronaut.png").toExternalForm()));
                break;
            case YELLOW:
                playerIcon = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Yellow_astronaut.png").toExternalForm()));
                break;
            default:
                playerIcon = null;
        }
        playerIcon.setFitWidth(34);
        playerIcon.setFitHeight(55);


        playerInformation.setText("You are " + gameSession.getPlayer(guiApplication.getMyId()).get().getUsername() + " (ID: " + guiApplication.getMyId() + ")\n"+ "Lost Components: "+ gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getDestroyedTiles());
        playerInformation.setGraphic(playerIcon);
        playerInformation.setContentDisplay(ContentDisplay.LEFT);
        playerInformation.setGraphicTextGap(10);


        listViewPlayers.setOnMouseClicked(event -> {
            int selectedIndex = listViewPlayers.getSelectionModel().getSelectedIndex();
            System.out.println("Selected index: " + selectedIndex);
            if(selectedIndex==guiApplication.getMyId()){
                clearShipBoard();
                setupDragAndDropCells(); // Riconfigura le celle per il drag and drop
                printShipboard();
            }
            else{
                guiApplication.lookAtShipboard(selectedIndex);
            }

        });
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
            System.out.println("HO CHIAMATO FINISH SHIPBOARD SULLO USER CON ID : " + guiApplication.getMyId());

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
//    public void enablePickTile(){
//        try{
//            gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getTile(new Point(9,5));
//            System.out.println("Enabling pick tile for coordinates (9,5)");
//            System.out.println(gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getTile(new Point(9,5)).getIsLocked());
//            ImageView imageView = tilesMap.get(gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getTile(new Point(9,5)).getIdTile());
//            imageView.setOnMouseClicked(event -> {
//                try {
//                    guiApplication.getController().userPickTile(guiApplication.getMyId(), new Point(9,5));
//                } catch (Exception e) {
//                    guiApplication.printUpdateMessage(error + e.getMessage());
//                }
//            });
//        } catch (Exception e) {
//
//        }
//        try{
//            gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getTile(new Point(10,5));
//
//            ImageView imageView = tilesMap.get(gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getTile(new Point(10,5)).getIdTile());
//            imageView.setOnMouseClicked(event -> {
//                try {
//                    guiApplication.getController().userPickTile(guiApplication.getMyId(), new Point(10,5));
//                } catch (Exception e) {
//                    guiApplication.printUpdateMessage(error + e.getMessage());
//                }
//            });
//        } catch (Exception e) {
//
//        }
//    }

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
        buildContainer.getChildren().clear();
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
//                System.out.println("Immagine non trovata per la carta ID: " + idCard);
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
                   switch (tile.get().getTileOrientation()) {
                       case NORTH -> imageView.setRotate(0);
                       case EAST -> imageView.setRotate(90);
                       case SOUTH -> imageView.setRotate(180);
                       case WEST -> imageView.setRotate(270);
                   }

                   // Aggiungi la nuova ImageView alla griglia
                   shipBoard.add(imageView, coordinates.x - 4, coordinates.y - 5);
                   if (tile.get().getComponent().getType() == ComponentType.CABINE) {
                       //creo hbox per affiancarli
                       HBox tokenContainer = new HBox();
                       tokenContainer.setSpacing(5); // Spazio tra i token
                       tokenContainer.setAlignment(javafx.geometry.Pos.CENTER);

                       //aggiungo token
                       tile.get().getComponent().getTokenList().forEach(token -> {

                           ImageView tokenImageView;
                           Color playerColor = gameSession.getPlayer(guiApplication.getMyId()).get().getColor();
                           if (token == Token.ASTRONAUT) {
                               //se il token è un astronauta, prendo il colore del giocatore
                               playerColor = gameSession.getPlayer(playerId).get().getColor();

                               switch (playerColor) {
                                   case RED:
                                       tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Red_astronaut.png").toExternalForm()));
                                       break;
                                   case BLUE:
                                       tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Blue_astronaut.png").toExternalForm()));
                                       break;
                                   case GREEN:
                                       tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Green_astronaut.png").toExternalForm()));
                                       break;
                                   case YELLOW:
                                       tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Yellow_astronaut.png").toExternalForm()));
                                       break;
                                   default:
                                       // Immagine predefinita in caso di colore non gestito
                                       tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Red_astronaut.png").toExternalForm()));
                               }
                           } else {
                               tokenImageView = tokensMap.get(token);
                           }
                           tokenImageView.setFitWidth(23);
                           tokenImageView.setFitHeight(32);
                           tokenContainer.getChildren().add(tokenImageView);

                       });
                       shipBoard.add(tokenContainer, coordinates.x - 4, coordinates.y - 5);
                       GridPane.setHalignment(tokenContainer, javafx.geometry.HPos.CENTER);
                   } else {
//                       System.out.println("Immagine non trovata per la tile ID: " + idTile);
                   }
               }
           }
       });
   }
   public void disableHighlight(){
        highlightPane.getChildren().clear();
   }
   public void selectTypeOfToken(){
       disableBuild4();
       disableBuild3();
       disableBuild2();
       disableBuild1();
        astronautsOrAliens.getChildren().clear();
        if(gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getAlienPoint().isPresent()){
            disableAll();
            loadTokens();
            if(gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getAlienPoint().isPresent()) {
                Point alienpoint = gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getAlienPoint().get();

                // evidenzio casella

                highlightPane.setStyle("-fx-border-color: yellow; -fx-border-width: 4; -fx-border-style: solid; -fx-background-color: rgba(255,255,0,0.3);");
                shipBoard.add(highlightPane, alienpoint.x - 4, alienpoint.y - 5);


//                System.out.println("Alien Point: " + alienpoint);
                astronautsOrAliens.setDisable(false);
                astronautsOrAliens.setVisible(true);
                chooseAliens.setDisable(false);
                chooseAliens.setVisible(true);
                gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().checkIfAlienArePossible(alienpoint).stream().forEach((token) -> {
                    if (token == Token.BROWN_ALIENS) {
                        ImageView brownAlienImage = tokensMap.get(Token.BROWN_ALIENS);
                        brownAlienImage.setFitWidth(90);
                        brownAlienImage.setFitHeight(135);
                        brownAlienImage.setOnMouseClicked(event -> {
                            try {
                                guiApplication.getController().userSelectTypeOfToken(guiApplication.getMyId(), token, alienpoint);
                            } catch (Exception e) {
                                guiApplication.printUpdateMessage(error + e.getMessage());
                            }
                        });
                        astronautsOrAliens.setSpacing(30);
                        astronautsOrAliens.getChildren().add(brownAlienImage);


                    } else if (token == Token.ASTRONAUT) {
                        ImageView astronautImage = tokensMap.get(Token.ASTRONAUT);
                        astronautImage.setFitWidth(106);
                        astronautImage.setFitHeight(135);
                        astronautImage.setOnMouseClicked(event -> {
                            try {
                                guiApplication.getController().userSelectTypeOfToken(guiApplication.getMyId(), token, alienpoint);
                            } catch (Exception e) {
                                guiApplication.printUpdateMessage(error + e.getMessage());
                            }
                        });
                        astronautsOrAliens.setSpacing(30);
                        astronautsOrAliens.getChildren().add(astronautImage);
                    } else if (token == Token.PURPLE_ALIENS) {
                        ImageView purpleAlienImage = tokensMap.get(Token.PURPLE_ALIENS);
                        purpleAlienImage.setFitWidth(106);
                        purpleAlienImage.setFitHeight(135);
                        purpleAlienImage.setOnMouseClicked(event -> {
                            try {

                                guiApplication.getController().userSelectTypeOfToken(guiApplication.getMyId(), token, alienpoint);
                            } catch (Exception e) {
                                guiApplication.printUpdateMessage(error + e.getMessage());
                            }
                        });
                        astronautsOrAliens.setSpacing(30);
                        astronautsOrAliens.getChildren().add(purpleAlienImage);
                    }
                });

            }

   }
        else{
            astronautsOrAliens.getChildren().clear();
            disableAll();
            waitingLabel.setDisable(false);
            waitingLabel.setVisible(true);
            astronautsOrAliens.setDisable(true);
            astronautsOrAliens.setVisible(false);
            chooseAliens.setDisable(true);
            chooseAliens.setVisible(false);
            disableBuild4();
            disableBuild3();
            disableBuild2();
            disableBuild1();
        }
    }
    private void disableAll(){
        cardContainer.setVisible(false);
        cardContainer.setDisable(true);
        buildContainer.setVisible(false);
        buildContainer.setDisable(true);
        smallAdventureDeckContainer.setVisible(false);
        smallAdventureDeckContainer.setDisable(true);
        smallAdventureDeck.setVisible(false);
        smallAdventureDeck.setDisable(true);
        stopLooking.setVisible(false);
        stopLooking.setDisable(true);
        discardTile.setVisible(false);
        discardTile.setDisable(true);
        rotateTile.setVisible(false);
        rotateTile.setDisable(true);
        finish.setVisible(false);
        finish.setDisable(true);
        timer.setLayoutX(155);
        timer.setLayoutY(191);
        timerLabel.setLayoutX(155);
        timerLabel.setLayoutY(250);

    }
    public void loadShip1(ActionEvent event) {
        try{
            readShipboardFromFile("ship_layout_1.json");
            disableBuild1();
        } catch (Exception e) {
            guiApplication.printUpdateMessage(error + e.getMessage());
        }

    }
    public void loadShip2(ActionEvent event) {
        try{
            readShipboardFromFile("ship_layout_2.json");
            disableBuild2();
        } catch (Exception e) {
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }
    public void loadShip3(ActionEvent event) {
        try{
            readShipboardFromFile("ship_layout_3.json");
            disableBuild3();
        } catch (Exception e) {
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }
    public void loadShip4(ActionEvent event) {
        try{
            readShipboardFromFile("ship_layout_4.json");
            disableBuild4();
        } catch (Exception e) {
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }
    public void disableBuild1(){
        build1.setVisible(false);
        build1.setDisable(true);
    }
    public void disableBuild2(){
        build2.setVisible(false);
        build2.setDisable(true);
    }
    public void disableBuild3(){
        build3.setVisible(false);
        build3.setDisable(true);
    }
    public void disableBuild4(){
        build4.setVisible(false);
        build4.setDisable(true);
    }
    public void readShipboardFromFile(String filename) {
        filename = "src/main/resources/it/polimi/ingsw/is25am49/" + filename;

        try {
            File file = new File(filename);
            if (!file.exists()) {
                throw new FileNotFoundException("File (" + filename + ") non trovato: " + file.getAbsolutePath());
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootArray = objectMapper.readTree(file);

            // Elabora i nodi in sequenza per vedere gli aggiornamenti
            processNodesSequentially(rootArray, 0);

        } catch (FileNotFoundException e) {
            guiApplication.printUpdateMessage(error + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void processNodesSequentially(JsonNode rootArray, int index) {
        if (index >= rootArray.size()) {
            return;
        }

        JsonNode node = rootArray.get(index);
        Timeline timeline = new Timeline();

        KeyFrame kf1 = new KeyFrame(Duration.ZERO, e -> {
            try {
                guiApplication.getController().userDrawTile(guiApplication.getMyId(), node.get("id").asInt());
            } catch (Exception ex) {
                guiApplication.printUpdateMessage(error + ex.getMessage());
            }
        });

        KeyFrame kf2 = new KeyFrame(Duration.millis(150), e -> {
            try {
                Orientation o = Orientation.valueOf(node.get("orientation").asText());
                gameSession.getState().userRotateHandTile(guiApplication.getMyId(), o);
            } catch (Exception ex) {
                guiApplication.printUpdateMessage(error + ex.getMessage());

            }
        });

        KeyFrame kf3 = new KeyFrame(Duration.millis(300), e -> {
            try {
                Orientation o = Orientation.valueOf(node.get("orientation").asText());
                Point p = new Point(node.get("x").asInt(), node.get("y").asInt());
                guiApplication.getController().userPlaceTile(guiApplication.getMyId(), p, o);
            } catch (Exception ex) {
                guiApplication.printUpdateMessage(error + ex.getMessage());

            } finally {
                KeyFrame nextNode = new KeyFrame(Duration.millis(200),
                        event -> processNodesSequentially(rootArray, index + 1));
                Timeline nextTimeline = new Timeline(nextNode);
                nextTimeline.play();
            }
        });

        timeline.getKeyFrames().addAll(kf1, kf2, kf3);
        timeline.play();
    }

}
