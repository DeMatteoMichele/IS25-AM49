package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.client.model.Game;
import it.polimi.ingsw.is25am49.client.model.LeaderBoard;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.adventurecards.AbandonedShipCard;
import it.polimi.ingsw.is25am49.client.model.adventurecards.AbandonedStationCard;
import it.polimi.ingsw.is25am49.client.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.client.model.adventurecards.PlanetCard;
import it.polimi.ingsw.is25am49.client.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.client.model.shipboard.Tile;
import it.polimi.ingsw.is25am49.shared.enums.*;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.Pair;

import java.awt.*;
import java.awt.color.ICC_ColorSpace;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class adventureController2 extends GenericSceneController {
    @FXML
    public Button stopButton;
//    @FXML
//    private StackPane cardMessages;
    @FXML
    private HBox statsContainer;
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
    private ImageView image;
    @FXML
    private GridPane shipBoard;

    @FXML
    private VBox messageBox;
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ListView<String> listViewPlayers;
    @FXML
    private Button confirm;
    @FXML
    private TextField input;
    @FXML
    private Label stateCard;
    @FXML
    private Button send;
    @FXML
    private Label turn;
    @FXML
    private ComboBox<String> select;


    private volatile boolean timeIsDone;
    private final HashSet<Point> coordinatesSet;
    private final ArrayList<Pair<Point, Token>> tokenPointsListDefenitive;
    private ShipBoard playerShipboard;
    private AdventureCard currentAdventureCard;
    private int counterStampe;
    private AdventureStatesTypes currentAdventureState;
    private Point2D[] coordsLeaderboard;
    private ScheduledExecutorService scheduler;
    private final double[] playerStatistics;
    private final int[] goodsNumbers;
    private final Map<Integer, javafx.scene.image.Image> goodsMap;
    private final ArrayList<Token> tokensArrayForGoodPosition;
    private Point lastSelectedCoordinates;
    private final String styleForHighlight;
    int i=1;

    public adventureController2() {
        coordinatesSet = new HashSet<>();
        tokenPointsListDefenitive = new ArrayList<>();
        playerShipboard = null;
        currentAdventureCard = null;
        counterStampe = 0;
        currentAdventureState = AdventureStatesTypes.DRAW;
        scheduler = Executors.newScheduledThreadPool(10);
        timeIsDone = false;
        playerStatistics = new double[5];
        goodsNumbers = new int[5];
        goodsMap = new HashMap<>();
        lastSelectedCoordinates = new Point(-1, -1);
        tokensArrayForGoodPosition = new ArrayList<>();
        styleForHighlight = "-fx-border-color: yellow; -fx-border-width: 4; -fx-border-style: solid; -fx-background-color: rgba(255,255,0,0.3);";
    }

    public void updateListView(){
        ArrayList<Player> listPlayers = gameSession.getGame().get().getListPlayers();
        listPlayers.sort(Comparator.comparingInt(Player::getIdPlayer));
        for(Player p : listPlayers){
            listViewPlayers.getItems().add(p.getUsername() + "  "+ p.getColor() + " (ID: " + p.getIdPlayer() + ")");
        }
        ImageView playerIcon;
        Color playerColor = gameSession.getPlayer(guiApplication.getMyId()).get().getColor();

        switch (playerColor) {
            case RED:
                playerIcon = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/red_astronaut.png").toExternalForm()));
                break;
            case BLUE:
                playerIcon = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/blue_astronaut.png").toExternalForm()));
                break;
            case GREEN:
                playerIcon = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/green_astronaut.png").toExternalForm()));
                break;
            case YELLOW:
                playerIcon = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/yellow_astronaut.png").toExternalForm()));
                break;
            default:
                playerIcon = null;
        }
        playerIcon.setFitWidth(34);
        playerIcon.setFitHeight(55);


        listViewPlayers.setOnMouseClicked(event -> {
            int selectedIndex = listViewPlayers.getSelectionModel().getSelectedIndex();
            System.out.println("Selected index: " + selectedIndex);
            if(selectedIndex==guiApplication.getMyId()){
                clearShipBoard();
                printShipboard();
            }
            else{
                guiApplication.lookAtShipboard2(selectedIndex);
            }

        });
    }
    public void clearShipBoard() {
        shipBoard.getChildren().clear();
    }

    public void initialize() {
        select.setVisible(false);
        javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResource("/sfondo3.jpg").toExternalForm());
        backgroundImage.setImage(image);
        backgroundImage.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                backgroundImage.fitWidthProperty().bind(newScene.widthProperty());
                backgroundImage.fitHeightProperty().bind(newScene.heightProperty());
            }
        });
        select.setCellFactory(list -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: white;");
                }
            }
        });

        select.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: white;");
                }
            }
        });
    }

    public void handleDrawButton() {
        try {
            guiApplication.getController().userDrawAdventureCard(guiApplication.getMyId());
        } catch (Exception e) {
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }

    public void handleStopButton() {
        try {

            guiApplication.printUpdateMessage("!!!Player " + gameSession.getPlayer(guiApplication.getMyId()).get().getUsername() + " has decided to land at the end of the turn!!!");
            guiApplication.getController().userStopFlight(guiApplication.getMyId());
            stopButton.setVisible(false);
        } catch (Exception e) {
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }

    public void init() {
        stateCard.setText("");
        stateCard.setWrapText(true);
//        stateCard.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 5px; -fx-border-color: #000000; -fx-border-width: 1px;");
//        turn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 5px; -fx-border-color: #000000; -fx-border-width: 1px;");
        turn.setText(gameSession.getGame().get().getGameboard().getCurrentPlayer().getUsername()+ " IS PLAYING");
        turn.setWrapText(true);
        loadAdventureCardsAndButton();
        loadLeaderBoard();
        loadTokens();
        loadTilesMap();
        loadGrid();
        updateListView();
        printShipboard();
        updatePlayerStats(gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getTotalFirePower(), gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getTotalEnginePower(), gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getTotalCrewNumber(), gameSession.getPlayer(guiApplication.getMyId()).get().getCredits(), gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getDestroyedTiles());
        cardEffect.setVisible(true);
//        cardMessages.setVisible(true);
//        highlightPane.setStyle("-fx-border-color: yellow; -fx-border-width: 4; -fx-border-style: solid; -fx-background-color: rgba(255,255,0,0.3);");
    }

    private void loadLeaderBoard() {
        if (getGameMode() == GameMode.FLIGHT_MODE || getGameMode() == GameMode.LEVEL_1) {
            String path = "/it/polimi/ingsw/is25am49/cardboard/cardboard-3.png";
            javafx.scene.image.Image image = new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
            ImageView leaderboardImage = new ImageView();
            leaderboardImage.setImage(image);
            leaderboardImage.setFitHeight(273);
            leaderboardImage.setFitWidth(493);
            leaderboardImage.setPreserveRatio(true);
            coordsLeaderboard = new Point2D[]{
                    new Point2D(129, 59.5),
                    new Point2D(176.5, 45),
                    new Point2D(224, 40),
                    new Point2D(269.5, 40.5),
                    new Point2D(316, 47),
                    new Point2D(361, 60),
                    new Point2D(403, 90.5),
                    new Point2D(424.5, 141.5),
                    new Point2D(398.5, 183.5),
                    new Point2D(355, 210),
                    new Point2D(312, 225),
                    new Point2D(265, 233),
                    new Point2D(215.5, 231.5),
                    new Point2D(170.5, 226),
                    new Point2D(128, 210.5),
                    new Point2D(85.5, 180),
                    new Point2D(63, 130.5),
                    new Point2D(88.5, 84.5)
            };
            javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas();
            drawLeaderBoardState(canvas, getleaderBoard(), leaderboardImage);
            leaderboard.getChildren().clear();
            leaderboard.getChildren().add(leaderboardImage);
            leaderboard.getProperties().put("leaderboard", leaderboardImage);
            leaderboard.getProperties().put("canvas", canvas);
            leaderboard.getChildren().add(canvas);
        } else if (getGameMode() == GameMode.LEVEL_2) {
            String path = "/it/polimi/ingsw/is25am49/cardboard/cardboard-5.png";
            javafx.scene.image.Image image = new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
            ImageView leaderboardImage = new ImageView();
            leaderboardImage.setImage(image);
            leaderboardImage.setPreserveRatio(true);
            coordsLeaderboard = new Point2D[]{
                    new Point2D(128, 74.5),
                    new Point2D(165, 54),
                    new Point2D(205, 49),
                    new Point2D(242, 48),
                    new Point2D(282, 48.5),
                    new Point2D(321.5, 55),
                    new Point2D(361.5, 63),
                    new Point2D(399.5, 76),
                    new Point2D(429.5, 100),
                    new Point2D(457.5, 139.5),
                    new Point2D(454.5, 184),
                    new Point2D(428.5, 219.5),
                    new Point2D(393, 239),
                    new Point2D(356.5, 254.5),
                    new Point2D(318, 265),
                    new Point2D(276.5, 270),
                    new Point2D(235.5, 267),
                    new Point2D(198.5, 263),
                    new Point2D(159, 253),
                    new Point2D(118.5, 237),
                    new Point2D(84.5, 212),
                    new Point2D(61.5, 173),
                    new Point2D(66, 130),
                    new Point2D(90.5, 95)
            };
            leaderboardImage.setFitHeight(320);
            leaderboardImage.setFitWidth(528);
            javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas();
            drawLeaderBoardState(canvas, getleaderBoard(), leaderboardImage);
            canvas.setHeight(320);
            canvas.setWidth(528);
            leaderboard.getChildren().clear();
            leaderboard.getChildren().add(leaderboardImage);
            leaderboard.getProperties().put("leaderboard", leaderboardImage);
            leaderboard.getProperties().put("canvas", canvas);
            leaderboard.getChildren().add(canvas);
        }
    }

    private void loadAdventureCardsAndButton() {
        AdventureCardDeck deck = gameSession.getGame().get().getGameboard().getMainAdventureDeck();
        deck.stream().forEach(adventureCard -> {
            String idCard = adventureCard.getCardId();
            String path = "/it/polimi/ingsw/is25am49/graphics/GT-cards_" + idCard + ".jpg";
            ImageView imageView = new ImageView(new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm()));
            adventureCardMap.put(idCard, imageView);
        });
        javafx.scene.image.Image image;
        if (getGameMode() == GameMode.FLIGHT_MODE || getGameMode() == GameMode.LEVEL_1) {
            String pathBack = "/it/polimi/ingsw/is25am49/graphics/GT-cards_I121.jpg";
            image = new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResource(pathBack)).toExternalForm());
            BackgroundImage backgroundImageButton = new BackgroundImage(
                    image,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, true, false)
            );
            drawButton.setBackground(new Background(backgroundImageButton));
        } else if (getGameMode() == GameMode.LEVEL_2) {
            String pathBack = "/it/polimi/ingsw/is25am49/graphics/GT-cards_II121.jpg";
            image = new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResource(pathBack)).toExternalForm());
            BackgroundImage backgroundImageButton = new BackgroundImage(
                    image,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(100, 100, true, true, true, false)
            );
            drawButton.setBackground(new Background(backgroundImageButton));
        }
    }

    public void updateCurrentCard(AdventureCard card) {
        String cardId = card.getCardId();
        if (cardId == null || cardId.isEmpty()) {
            guiApplication.printUpdateMessage(error + "Error with cardId " + cardId);
        } else {
            ImageView imageView = adventureCardMap.get(cardId);
            if (imageView != null) {
                currentCard.setImage(imageView.getImage());
            } else {
                guiApplication.printUpdateMessage(error + "Adventure Card not found: " + cardId);
            }
            currentAdventureCard = card;
        }
    }
public void printPlayerByIdShipboard(int playerId){
    shipBoard.getChildren().clear();
    gameSession.getPlayer(playerId).get().getShipBoard().getGrid().forEach((coordinates, tile) -> {
        if (tile.isPresent()) {
            int idTile = tile.get().getIdTile();
            ImageView imageView = tilesMap.get(idTile);
            if (imageView != null) {
                imageView.setFitWidth(60);
                imageView.setFitHeight(60);
                imageView.setUserData(idTile);
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

                        if(token==Token.ASTRONAUT) {
                            //se il token è un astronauta, prendo il colore del giocatore
                            Color playerColor = gameSession.getPlayer(playerId).get().getColor();

                            switch (playerColor) {
                                case RED:
                                    tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/red_astronaut.png").toExternalForm()));
                                    break;
                                case BLUE:
                                    tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/blue_astronaut.png").toExternalForm()));
                                    break;
                                case GREEN:
                                    tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/green_astronaut.png").toExternalForm()));
                                    break;
                                case YELLOW:
                                    tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/yellow_astronaut.png").toExternalForm()));
                                    break;
                                default:
                                    tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/red_astronaut.png").toExternalForm()));
                            }
                        }
                        else{
                            tokenImageView = tokensMap.get(token);
                        }
                        tokenImageView.setFitWidth(23);
                        tokenImageView.setFitHeight(32);
                        tokenContainer.getChildren().add(tokenImageView);
                        tokenContainer.setMouseTransparent(true);

                    });

                    // aggiungo box al grid
                    shipBoard.add(tokenContainer, coordinates.x-4, coordinates.y-5);
                    GridPane.setHalignment(tokenContainer, javafx.geometry.HPos.CENTER);
                }
                else if(tile.get().getComponent().getType()== ComponentType.BATTERY){
                    HBox tokenContainer = new HBox();
                    tokenContainer.setPrefSize(30, 30);
                    tokenContainer.setMaxSize(30, 30);
                    tokenContainer.setSpacing(5); // Spazio tra i token
                    tokenContainer.setAlignment(javafx.geometry.Pos.CENTER);
                    tokenContainer.setStyle("-fx-background-color: #c0c0c0;");
                    //aggiungo token
                    new ArrayList<>(tile.get().getComponent().getTokenList()).forEach(token -> {
                        Image tokenImage = tokensMap.get(token).getImage();
                        ImageView tokenImageView = new ImageView(tokenImage);
                        tokenImageView.setFitWidth(10);
                        tokenImageView.setFitHeight(37);
                        tokenContainer.getChildren().add(tokenImageView);
                        tokenContainer.setMouseTransparent(true);

                    });

                    // aggiungo box al grid
                    shipBoard.add(tokenContainer, coordinates.x-4, coordinates.y-5);
                    GridPane.setHalignment(tokenContainer, javafx.geometry.HPos.CENTER);
                } else if (tile.get().getComponent().getType()== ComponentType.STORAGE) {
                    HBox tokenContainer = new HBox();
                    tokenContainer.setSpacing(5);
                    tokenContainer.setAlignment(javafx.geometry.Pos.CENTER);
                    new ArrayList<>(tile.get().getComponent().getTokenList()).forEach(token -> {
                        Image tokenImage = tokensMap.get(token).getImage();
                        ImageView tokenImageView = new ImageView(tokenImage);
                        tokenImageView.setFitWidth(20);
                        tokenImageView.setFitHeight(20);
                        tokenContainer.getChildren().add(tokenImageView);
                    });
                    shipBoard.add(tokenContainer, coordinates.x-4, coordinates.y-5);
                    GridPane.setHalignment(tokenContainer, javafx.geometry.HPos.CENTER);

                }
                else if(tile.get().getComponent().getType()==ComponentType.SPECIAL_STORAGE){
                    HBox tokenContainer = new HBox();
                    tokenContainer.setSpacing(5);
                    tokenContainer.setAlignment(javafx.geometry.Pos.CENTER);
                    new ArrayList<>(tile.get().getComponent().getTokenList()).forEach(token -> {
                        Image tokenImage = tokensMap.get(token).getImage();
                        ImageView tokenImageView = new ImageView(tokenImage);
                        tokenImageView.setFitWidth(20);
                        tokenImageView.setFitHeight(20);
                        tokenContainer.getChildren().add(tokenImageView);
                    });
                    shipBoard.add(tokenContainer, coordinates.x-4, coordinates.y-5);
                    GridPane.setHalignment(tokenContainer, javafx.geometry.HPos.CENTER);
                }
            }
        }

    });

}
public void printShipboard() {
    shipBoard.getChildren().clear();
    Map<Point, Optional<Tile>> gridCopy = new HashMap<>(gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getGrid());
    gridCopy.forEach((coordinates, tile) -> {
        if (tile.isPresent()) {
            int idTile = tile.get().getIdTile();
            ImageView imageView = tilesMap.get(idTile);
            if (imageView != null) {
                imageView.setFitWidth(60);
                imageView.setFitHeight(60);
                imageView.setUserData(idTile);

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

                        if(token==Token.ASTRONAUT) {
                            //se il token è un astronauta, prendo il colore del giocatore
                            Color playerColor = gameSession.getPlayer(guiApplication.getMyId()).get().getColor();

                            switch (playerColor) {
                                case RED:
                                    tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/red_astronaut.png").toExternalForm()));
                                    break;
                                case BLUE:
                                    tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/blue_astronaut.png").toExternalForm()));
                                    break;
                                case GREEN:
                                    tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/green_astronaut.png").toExternalForm()));
                                    break;
                                case YELLOW:
                                    tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/yellow_astronaut.png").toExternalForm()));
                                    break;
                                default:
                                    tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/red_astronaut.png").toExternalForm()));
                            }
                        }
                        else{
                            tokenImageView = tokensMap.get(token);
                        }
                        tokenImageView.setFitWidth(23);
                        tokenImageView.setFitHeight(32);
                        tokenContainer.getChildren().add(tokenImageView);
                        tokenContainer.setMouseTransparent(true);

                    });

                    // aggiungo box al grid
                    shipBoard.add(tokenContainer, coordinates.x-4, coordinates.y-5);
                    GridPane.setHalignment(tokenContainer, javafx.geometry.HPos.CENTER);
                }
                else if(tile.get().getComponent().getType()== ComponentType.BATTERY){
                    HBox tokenContainer = new HBox();
                   tokenContainer.setPrefSize(30, 30);
                    tokenContainer.setMaxSize(30, 30);
                    tokenContainer.setSpacing(5); // Spazio tra i token
                    tokenContainer.setAlignment(javafx.geometry.Pos.CENTER);
                    tokenContainer.setStyle("-fx-background-color: #c0c0c0;");
                    //aggiungo token
                    new ArrayList<>(tile.get().getComponent().getTokenList()).forEach(token -> {
                        Image tokenImage = tokensMap.get(token).getImage();
                        ImageView tokenImageView = new ImageView(tokenImage);
                        tokenImageView.setFitWidth(10);
                        tokenImageView.setFitHeight(37);
                        tokenContainer.getChildren().add(tokenImageView);
                        tokenContainer.setMouseTransparent(true);

                });

                // aggiungo box al grid
                shipBoard.add(tokenContainer, coordinates.x-4, coordinates.y-5);
                GridPane.setHalignment(tokenContainer, javafx.geometry.HPos.CENTER);
                } else if (tile.get().getComponent().getType()== ComponentType.STORAGE) {
                    HBox tokenContainer = new HBox();
                    tokenContainer.setSpacing(5);
                    tokenContainer.setAlignment(javafx.geometry.Pos.CENTER);
                    new ArrayList<>(tile.get().getComponent().getTokenList()).forEach(token -> {
                        Image tokenImage = tokensMap.get(token).getImage();
                        ImageView tokenImageView = new ImageView(tokenImage);
                        tokenImageView.setFitWidth(20);
                        tokenImageView.setFitHeight(20);
                        tokenContainer.getChildren().add(tokenImageView);
                    });
                    shipBoard.add(tokenContainer, coordinates.x-4, coordinates.y-5);
                    GridPane.setHalignment(tokenContainer, javafx.geometry.HPos.CENTER);

                }
                else if(tile.get().getComponent().getType()==ComponentType.SPECIAL_STORAGE){
                    HBox tokenContainer = new HBox();
                    tokenContainer.setSpacing(5);
                    tokenContainer.setAlignment(javafx.geometry.Pos.CENTER);
                    new ArrayList<>(tile.get().getComponent().getTokenList()).forEach(token -> {
                        Image tokenImage = tokensMap.get(token).getImage();
                        ImageView tokenImageView = new ImageView(tokenImage);
                        tokenImageView.setFitWidth(20);
                        tokenImageView.setFitHeight(20);
                        tokenContainer.getChildren().add(tokenImageView);
                    });
                    shipBoard.add(tokenContainer, coordinates.x-4, coordinates.y-5);
                    GridPane.setHalignment(tokenContainer, javafx.geometry.HPos.CENTER);
                }
            }
        }

    });
    }

    public void loadGrid() {
        if(gameSession.getGame().get().getMode()== GameMode.LEVEL_1 || gameSession.getGame().get().getMode()== GameMode.FLIGHT_MODE){
            Image im = new Image(getClass().getResource("/it/polimi/ingsw/is25am49/cardboard/cardboard-1.jpg").toExternalForm());
            System.out.println("entrato in loadGrid");
            image.setImage(im);
            int rows = 5;
            int columns = 7;
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < columns; col++) {
                    StackPane cellContainer = new StackPane();

                    cellContainer.getProperties().put("coordinates", new Point(col+4, row+5));
                    cellContainer.setStyle("-fx-background-color: transparent;");
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
                    cellContainer.setStyle("-fx-background-color: transparent;");
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


    public void configureEffectPaneDices(int firstDice, int secondDice) {
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0);";
        javafx.scene.control.Label labelString = new javafx.scene.control.Label("The result of the dices is:");
        labelString.setStyle(style);
        String path = "/it/polimi/ingsw/is25am49/animations/dado_" + firstDice + ".png";
        javafx.scene.image.Image firstDiceImage = new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
        path = "/it/polimi/ingsw/is25am49/animations/dado_" + secondDice + ".png";
        javafx.scene.image.Image secondDiceImage = new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
        ImageView firstDiceView = new ImageView(firstDiceImage);
        ImageView secondDiceView = new ImageView(secondDiceImage);
        firstDiceView.setFitHeight(75);
        firstDiceView.setFitWidth(75);
        secondDiceView.setFitHeight(75);
        secondDiceView.setFitWidth(75);
        HBox buttonsBox = new HBox(10, firstDiceView, secondDiceView);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox container = new VBox(20, labelString, buttonsBox);
        container.setAlignment(Pos.CENTER);

        cardEffect.getChildren().clear();
        cardEffect.getChildren().add(container);
    }


    public void updateCardMessageBox(String messageText) {
//        cardMessages.getChildren().clear();
        javafx.scene.control.Label message = new javafx.scene.control.Label();
        message.setText(messageText);
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0); -fx-padding: 10;";
        message.setStyle(style);
        message.setWrapText(true);
        message.setMaxWidth(400);
        message.setAlignment(Pos.TOP_LEFT);
        message.setTextAlignment(TextAlignment.LEFT);
//        cardMessages.getChildren().add(message);
    }

    public void updatePlayerStats(double firePower, int motorPower, int crew, int credits, int destroyedTiles) {
        statsContainer.getChildren().clear();
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0); -fx-padding: 10;";
        Player player = getPlayer();
        javafx.scene.control.Label playerStats = new javafx.scene.control.Label();
        playerStats.setStyle(style);
        if (player != null) {
            StringBuilder stats = new StringBuilder("Player: " + player.getUsername() + "     " + player.getColor().toString() + "\n");
            stats.append("FirePower: ").append(playerStatistics[0] + firePower).append("\n");
            stats.append("MotorPower: ").append(playerStatistics[1] + motorPower).append("\n");
            stats.append("Crew: ").append(playerStatistics[2] + crew).append("\n");
            stats.append("Credits: ").append(playerStatistics[3] + credits).append("\n");
            stats.append("Destroyed tiles: ").append(playerStatistics[4] + destroyedTiles).append("\n");
            playerStats.setText(stats.toString());
        } else {
            playerStats.setText("No player data available.");
        }
        statsContainer.getChildren().add(playerStats);
    }
    public void disable(){
        send.setVisible(false);
        select.setVisible(false);
    }
    public void enable(){
        send.setVisible(true);
        select.setVisible(true);
    }
    public void updateCardState(AdventureStatesTypes state) {
        currentAdventureState = state;
        switch (currentAdventureState) {
            case DRAW -> {
                drawButton.setDisable(false);
//                disableAllCellsShipboard();
                stateCard.setText("Drawing the card...");
                confirm.setVisible(false);
                send.setVisible(false);
                clearEffectPane();
            }
            case  DECISION_ASTRONAUTS_OR_ALIENS -> {
                ArrayList<Pair<Point, Token>> tokens = new ArrayList<>();
                stateCard.setText("Please select the Astronauts or Aliens you want to remove.");
                drawButton.setDisable(true);
                select.getItems().addAll("Astronaut", "Purple Alien", "Brown Alien");
                select.setPromptText("Select crew member");
                select.setVisible(true);
                send.setText("Remove Crew");
                confirm.setText("Finish Decision");
                send.setVisible(true);
                confirm.setVisible(true);
                input.setVisible(true);
                send.setOnAction(event -> {
                    String value = input.getText();
                    Token tok;
                    if(value!=null && !value.isEmpty()) {
                        try{
                            String[] parts = value.trim().split("\\s+");

                            int x = Integer.parseInt(parts[0]);
                            int y = Integer.parseInt(parts[1]);
                            Point coordinates = new Point(x, y);
                            String selectedGood = select.getValue();
                            if (selectedGood == null) {
                                guiApplication.printUpdateMessage(error + "Please select a good type from the dropdown menu");
                                return;
                            }

                            switch (selectedGood) {
                                case "Astronaut":
                                    tok = Token.ASTRONAUT;
                                    break;
                                case "Purple Alien":
                                    tok = Token.PURPLE_ALIENS;
                                    break;
                                case "Brown Alien":
                                    tok = Token.BROWN_ALIENS;
                                    break;
                                default:
                                    guiApplication.printUpdateMessage(error + "Invalid good type selected");
                                    input.clear();
                                    return;
                            }
                            tokens.add(new Pair<>(coordinates, tok));
                            input.clear();
                            switch (selectedGood){
                                case "Astronaut":
                                    addMessage("Selected Astronaut at coordinates: [" + x + ", " + y + "]");
                                    break;
                                case "Purple Alien":
                                    addMessage("Selected Purple Alien at coordinates: [" + x + ", " + y + "]");
                                    break;
                                case "Brown Alien":
                                    addMessage("Selected Brown Alien at coordinates: [" + x + ", " + y + "]");
                                    break;
                            }
                        } catch (Exception e) {
                            guiApplication.printUpdateMessage(error + "Invalid input format. Use the format 'x y' z'");
                        }
                    }
                });
                confirm.setOnAction(event -> {
                    try {
                        ArrayList<Pair<Point, Token>> tokens2 = new ArrayList<>(tokens);
                        guiApplication.getController().userFinishDecisionToken(guiApplication.getMyId(), tokens);
                        input.clear();
                        select.getItems().clear();
                        select.setVisible(false);
                    } catch (Exception e) {
                        guiApplication.printUpdateMessage(error + e.getMessage());
                        tokens.clear();
                    }
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    tokens.clear();
                });


            }
            case DECISION_BATTERIES -> {
                System.out.println("ENTRATO IN DECISION BATTERIES");
                ArrayList<Pair<Point, Token>> tokens = new ArrayList<>();
                stateCard.setText("Please select the Batteries you want to activate.");
                send.setText("Use Batteries");
                confirm.setText("Finish Decision");
                send.setVisible(true);
                input.setVisible(true);
                confirm.setVisible(true);
                send.setOnAction(event -> {
                    String value = input.getText().trim();
                    if (value != null && !value.isEmpty()) {
                        try {
                            String[] parts = value.trim().split("\\s+");

                            int x = Integer.parseInt(parts[0]);
                            int y = Integer.parseInt(parts[1]);
                            Point coordinatess = new Point(x, y);

                            tokens.add(new Pair<>(coordinatess, Token.BATTERY_CELLS));
                            input.clear();
                            addMessage("Batteria aggiunta alle coordinate: " + coordinatess);
                        } catch (NumberFormatException e) {
                            addMessage("Formato input non valido. Inserire solo numeri (es. '5 6')");
                        } catch (Exception e) {
                            addMessage("Errore nell'elaborazione: " + e.getMessage());
                        }
                    } else {
                        addMessage("Input non valido. Inserire coordinate nel formato 'x y'");
                    }
                });
                confirm.setOnAction(event -> {
                    try{
                        ArrayList<Pair<Point, Token>> tokens2 = new ArrayList<>(tokens);
                        guiApplication.getController().userFinishDecisionToken(guiApplication.getMyId(), tokens);
                        input.clear();
                    } catch (Exception e) {
                        guiApplication.printUpdateMessage(error + e.getMessage());
                        tokens.clear();
                    }
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    tokens.clear();
                });
            }

            case DECISION_CANNON,DECISION_SHIELDS, DECISION_MOTOR -> {
                HashSet<Point> points = new HashSet<>();
                drawButton.setDisable(false);
                confirm.setVisible(true);
                select.setVisible(false);
                send.setVisible(true);
                input.setVisible(true);
                switch (currentAdventureState) {
                    case DECISION_SHIELDS -> {
                        send.setText("Activate Shield");
                        stateCard.setText("Please select the Shields you want to activate.");
                    }
                    case DECISION_MOTOR -> {
                        send.setText("Activate Motor");
                        stateCard.setText("Please select the Motors you want to activate.");
                    }
                    case DECISION_CANNON -> {
                        send.setText("Activate Cannon");
                        stateCard.setText("Please select the Cannons you want to activate.");
                    }
                }
                confirm.setText("Finish Decision");
                send.setOnAction(event ->{
                    String value = input.getText();
                    if (value != null && !value.isEmpty()) {
                        try {
                            String[] coordinates = value.split("\\s+");
                            if (coordinates.length != 2) {
                                addMessage("Formato non valido. Inserire coordinate nel formato 'x y'");
                                return;
                            }

                            int x = Integer.parseInt(coordinates[0]);
                            int y = Integer.parseInt(coordinates[1]);
                            Point coordinatess = new Point(x, y);
                            points.add(coordinatess);
                            input.clear();
                             StackPane highlightPane = new StackPane();
                            highlightPane.setStyle("-fx-border-color: yellow; -fx-border-width: 4; -fx-border-style: solid; -fx-background-color: rgba(255,255,0,0.3);");
                            shipBoard.add(highlightPane, x - 4, y - 5);
                            switch (currentAdventureState) {
                                case DECISION_SHIELDS -> {
                                    addMessage("Selected Shield at coordinates: [" + x+ ", " + y + "]");
                                    break;
                                }
                                case DECISION_MOTOR -> {
                                    addMessage("Selected Motor at coordinates: [" + x+ ", " + y + "]");
                                    break;
                                }
                                case DECISION_CANNON -> {
                                    addMessage("Selected Cannon at coordinates: [" + x+ ", " + y + "]");
                                    break;
                                }
                            }
                        }catch (NumberFormatException e) {
                            guiApplication.printUpdateMessage(error + "Formato input non valido. Inserire solo numeri (es. '5 6')");
                        } catch (Exception e) {
                            guiApplication.printUpdateMessage(error + "Errore nell'elaborazione: " + e.getMessage());
                        }
                    } else {
                        guiApplication.printUpdateMessage(error + "Input non valido. Inserire coordinate nel formato 'x y'");
                    }
                });
                confirm.setOnAction(event -> {
                    try{
                        HashSet<Point> points2 = new HashSet<>(points);
                        guiApplication.getController().userFinishDecisionActivating(guiApplication.getMyId(), points);
                        input.clear();
                        select.setVisible(false);
                        printShipboard();
                    }catch (Exception e) {
                        guiApplication.printUpdateMessage(error + e.getMessage());
                        printShipboard();
                        points.clear();
                    }
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    points.clear();
                });
            }

            case DECISION_GOODS -> {
                ArrayList<Pair<Point, Token>> tokens = new ArrayList<>();
                drawButton.setDisable(true);
                select.getItems().addAll("Red Good", "Yellow Good", "Blue Good", "Green Good");
                select.setPromptText("Select good type");
                select.setVisible(true);
                send.setVisible(true);
                input.setVisible(true);
                confirm.setVisible(true);
                send.setText("Add Good");
                confirm.setText("Finish Decision");
                stateCard.setText("Please select the storage in which you wanna add the goods.");
                send.setOnAction(event -> {
                    String value = input.getText();
                    Token tok;
                    if(value!=null && !value.isEmpty()) {
                        try{
                            String[] parts = value.trim().split("\\s+");

                            int x = Integer.parseInt(parts[0]);
                            int y = Integer.parseInt(parts[1]);

                            String selectedGood = select.getValue();
                            if (selectedGood == null) {
                                guiApplication.printUpdateMessage(error + "Please select a good type from the dropdown menu");
                                return;
                            }

                            switch (selectedGood) {
                                case "Red Good":
                                    tok = Token.RED_GOODS;
                                    break;
                                case "Yellow Good":
                                    tok = Token.YELLOW_GOODS;
                                    break;
                                case "Blue Good":
                                    tok = Token.BLUE_GOODS;
                                    break;
                                case "Green Good":
                                    tok = Token.GREEN_GOODS;
                                    break;
                                default:
                                    guiApplication.printUpdateMessage(error + "Invalid good type selected");
                                    input.clear();
                                    return;
                            }

                            Point coordinates = new Point(x, y);
                            tokens.add(new Pair<>(coordinates,tok));
                            input.clear();
                            switch (selectedGood) {
                                case "Red Good":
                                    addMessage("Selected Red Good at coordinates: [" + x + ", " + y + "]");
                                    break;
                                case "Yellow Good":
                                    addMessage("Selected Yellow Good at coordinates: [" + x + ", " + y + "]");
                                    break;
                                case "Blue Good":
                                    addMessage("Selected Blue Good at coordinates: [" + x + ", " + y + "]");
                                    break;
                                case "Green Good":
                                    addMessage("Selected Green Good at coordinates: [" + x + ", " + y + "]");
                                    break;
                            }
                        } catch (Exception e) {
                            guiApplication.printUpdateMessage(error + "Invalid input format. Use the format 'x y' z'");
                        }
                    }});
                confirm.setOnAction(event -> {
                    try {
                        ArrayList<Pair<Point, Token>> tokens2 = new ArrayList<>(tokens);
                        guiApplication.getController().userFinishDecisionToken(guiApplication.getMyId(), tokens);
                        input.clear();
                        select.getItems().clear();
                        select.setVisible(false);
                    } catch (Exception e) {
                        tokens.clear();
                        guiApplication.printUpdateMessage(error + e.getMessage());
                    }
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    tokens.clear();
                });

                }

            case DECISION_LAND_PLANET -> {
                drawButton.setDisable(true);
                stateCard.setText("Please select the planet you want to land on. (id of the planet if you want to land, nothing if you don't want to land)");
                confirm.setText("Land");
                send.setVisible(false);
                input.setVisible(true);
                select.setVisible(false);
                confirm.setVisible(true);
                confirm.setOnAction(event -> {
                    String value = input.getText();
                    if(value != null && value.length() == 1) {
                        guiApplication.getController().userLandOnPlanet(guiApplication.getMyId(), true, Integer.parseInt(value));
                        input.clear();
                    }
                    else if(value != null && value.length() > 1){
                        guiApplication.printUpdateMessage(error + "Invalid input, you have to put only the number of the planet");
                    }
                    else if(value.isEmpty()){
                        guiApplication.getController().userLandOnPlanet(guiApplication.getMyId(), false, 0);
                    }
                });
            }
            case DECISION_REWARDS -> {
                confirm.setVisible(true);
                drawButton.setDisable(true);
                input.setVisible(true);
                select.setVisible(false);
                stateCard.setText("Do you want to accept the prizes? (y if you want to land, nothing if you don't want to land)");
                confirm.setText("Accept");
                send.setVisible(false);
                confirm.setOnAction(event -> {
                    String value = input.getText();
                    if(value.equals("yes") || value.equals("y") || value.equals("Y") || value.equals("Yes")){
                        guiApplication.getController().userAcceptPrizes(guiApplication.getMyId(), true);
                        input.clear();
                    }
                    else {
                        guiApplication.getController().userAcceptPrizes(guiApplication.getMyId(), false);
                    }
                });

            }
            case DECISION_LAND_STATION_OR_SHIP -> {
                drawButton.setDisable(true);
                confirm.setVisible(true);
                input.setVisible(true);
                select.setVisible(false);
                stateCard.setText("Select if you wanna land on the station or on your ship (y if you want to land, nothing if you don't want to land)");
                confirm.setText("Land");
                send.setVisible(false);
                confirm.setOnAction(event -> {
                    String value = input.getText();
                    if(value.equals("yes") || value.equals("y") || value.equals("Y") || value.equals("Yes")){
                        guiApplication.getController().userLandOnStationOrShip(guiApplication.getMyId(), true);
                        input.clear();
                    }
                    else {
                       guiApplication.getController().userLandOnStationOrShip(guiApplication.getMyId(), false);
                    }
                });
            }
            case ROLL_DICES -> {
                stateCard.setText("Roll the dices!");
                drawButton.setDisable(true);
                confirm.setVisible(false);
                input.setVisible(false);
                select.setVisible(false);
                disable();
            }
            case UPDATE -> {
                drawButton.setDisable(false);
                confirm.setVisible(false);
                send.setVisible(false);
                input.setVisible(false);
                select.setVisible(false);
//                disableAllCellsShipboard();
                clearEffectPane();
            }
            default -> {
            }

        }
    }
    public void setDraw(){
        stateCard.setText("Updating... Now the leader must draw again");
        turn.setText(gameSession.getGame().get().getGameboard().getLeader().getUsername() + " IS PLAYING");
    }

    private GameMode getGameMode() {
        return Objects.requireNonNull(getGame()).getMode();
    }

    private Player getPlayer() {
        if (gameSession.getPlayer(guiApplication.getMyId()).isPresent())
            return gameSession.getPlayer(guiApplication.getMyId()).get();
        else {
            guiApplication.printUpdateMessage(error + "No player is present.");
            return null;
        }
    }

    private Game getGame() {
        if (gameSession.getGame().isPresent()) {
            return gameSession.getGame().get();
        } else {
            guiApplication.printUpdateMessage(error + "No game session available.");
            return null;
        }
    }

    private LeaderBoard getleaderBoard() {
        return Objects.requireNonNull(getGame()).getGameboard().getLeaderBoard();
    }



    public void drawLeaderBoardState(javafx.scene.canvas.Canvas canvas, LeaderBoard leaderBoard, ImageView leaderboardImage) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        javafx.scene.image.Image img = leaderboardImage.getImage();
        double imgWidth = img.getWidth() / 2;
        double imgHeight = img.getHeight() / 2;
        canvas.setWidth(imgWidth);
        canvas.setHeight(imgHeight);
        gc.drawImage(img, 0, 0, imgWidth, imgHeight);
        ArrayList<Optional<Player>> showableBoard = leaderBoard.getShowableLeaderBoard();
        for (int i = 0; i < showableBoard.size(); i++) {
            final int cellIndex = i;
            showableBoard.get(i).ifPresent(player -> {
                javafx.scene.paint.Color playerColor = mapPlayerColorToFxColor(player.getColor());
                gc.setFill(playerColor);

                Point2D vertix = coordsLeaderboard[cellIndex];
                int radius = 10;
                gc.fillOval(vertix.getX() - radius, vertix.getY() - radius, radius * 2, radius * 2);
            });
        }
    }

    private javafx.scene.paint.Color mapPlayerColorToFxColor(Color color) {
        return switch (color) {
            case RED -> javafx.scene.paint.Color.rgb(255, 0, 0, 0.8);
            case BLUE -> javafx.scene.paint.Color.rgb(0, 0, 255, 0.8);
            case GREEN -> javafx.scene.paint.Color.rgb(0, 255, 0, 0.8);
            case YELLOW -> javafx.scene.paint.Color.rgb(255, 255, 0, 0.8);
        };
    }

    public void clearEffectPane() {
        cardEffect.getChildren().clear();
        tokenPointsListDefenitive.clear();
        coordinatesSet.clear();
    }
    public void disableall(){

        send.setDisable(true);
        send.setVisible(false);
        confirm.setDisable(true);
        confirm.setVisible(false);
        input.setDisable(true);
        input.setVisible(false);
        stateCard.setDisable(true);
        stateCard.setVisible(false);
        select.setVisible(false);
    }
    public void enableAll(){

        send.setDisable(false);
        send.setVisible(true);
        confirm.setDisable(false);
        confirm.setVisible(true);
        input.setDisable(false);
        input.setVisible(true);
        stateCard.setDisable(false);
        stateCard.setVisible(true);
    }
    public void updateChangeTurn(int playerId) {
//        drawButton.setDisable(true);
        turn.setText(gameSession.getGame().get().getGameboard().getCurrentPlayer().getUsername() + " IS PLAYING");
    }


    public void reDrawLeaderBoard(LeaderBoard leaderBoard) {
        ImageView leaderboardImage = (ImageView) leaderboard.getProperties().get("leaderboard");
        javafx.scene.canvas.Canvas leaderboardCanvas = (Canvas) leaderboard.getProperties().get("canvas");
        if (leaderBoard != null) {
            drawLeaderBoardState(leaderboardCanvas, leaderBoard, leaderboardImage);
            leaderboard.getProperties().remove("canvas");
            leaderboard.getChildren().clear();
            leaderboard.getChildren().add(leaderboardImage);
            leaderboard.getChildren().add(leaderboardCanvas);
            leaderboard.getProperties().put("leaderboard", leaderboardImage);
            leaderboard.getProperties().put("canvas", leaderboardCanvas);
        } else {
            guiApplication.printUpdateMessage(error + "No leaderboard available.");
        }

    }

    public void addMessage(String text) {
        javafx.scene.control.Label messageLabel = new Label(text);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-text-fill: white;");
        messageBox.getChildren().add(messageLabel);
        javafx.scene.control.Separator separator = new javafx.scene.control.Separator();
        separator.setStyle("-fx-background-color: #dddddd;");
        messageBox.getChildren().add(separator);
        scrollPane.layout();
        scrollPane.setVvalue(1.0);
    }
    public void throwDices(ActionEvent event){
        try{
            guiApplication.getController().userThrowDices(guiApplication.getMyId());
        }catch (Exception e){
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }
    public void reset(ActionEvent event){
        guiApplication.reset();
    }
}
