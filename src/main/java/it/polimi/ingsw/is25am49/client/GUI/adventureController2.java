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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
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
    @FXML
    public StackPane tokens;
//    @FXML
//    private StackPane cardMessages;
    @FXML
    private HBox statsContainer;
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
    private ImageView cardDeckImage;
    @FXML
    private ImageView image;
    @FXML
    private GridPane shipBoard;
    @FXML
    private AnchorPane adventureContainer;
    @FXML
    private VBox messageBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private HBox containerToken;
    @FXML
    private Label selectedTokens;
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


    public void initialize() {
        tokens.setDisable(true);
        tokens.setVisible(false);
        javafx.scene.image.Image image = new javafx.scene.image.Image(getClass().getResource("/sfondo3.jpg").toExternalForm());
        backgroundImage.setImage(image);
        backgroundImage.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                backgroundImage.fitWidthProperty().bind(newScene.widthProperty());
                backgroundImage.fitHeightProperty().bind(newScene.heightProperty());
            }
        });
        String path = "/it/polimi/ingsw/is25am49/tokens/";
        goodsMap.put(0, new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResource(path + "Redgood.png")).toExternalForm()));
        goodsMap.put(1, new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResource(path + "Greengood.png")).toExternalForm()));
        goodsMap.put(2, new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResource(path + "Bluegood.png")).toExternalForm()));
        goodsMap.put(3, new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResource(path + "Yellowgood.png")).toExternalForm()));
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
            guiApplication.getController().userStopFlight(guiApplication.getMyId());
        } catch (Exception e) {
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }

    public void init() {
        stateCard.setText("");
        stateCard.setWrapText(true);
        stateCard.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 5px; -fx-border-color: #000000; -fx-border-width: 1px;");
        turn.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: bold; -fx-padding: 5px; -fx-border-color: #000000; -fx-border-width: 1px;");
        turn.setText(gameSession.getGame().get().getGameboard().getCurrentPlayer().getUsername()+ " IS PLAYING");
        turn.setWrapText(true);
        loadAdventureCardsAndButton();
        loadLeaderBoard();
        loadTokens();
        loadTilesMap();
        loadGrid();
        printShipboard();
        updatePlayerStats(gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getTotalFirePower(), gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getTotalEnginePower(), gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getTotalCrewNumber(), gameSession.getPlayer(guiApplication.getMyId()).get().getCredits(), gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getDestroyedTiles());
        cardEffect.setVisible(true);
//        cardMessages.setVisible(true);
        highlightPane.setStyle("-fx-border-color: yellow; -fx-border-width: 4; -fx-border-style: solid; -fx-background-color: rgba(255,255,0,0.3);");
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
    public void removeImagesFromShipboardGrid() {
        // Scorre ogni cella della GridPane
        for (Node node : shipboardGrid.getChildren()) {
            if (node instanceof StackPane) {
                removeImagesRecursively((Pane) node);
            }
        }
    }

    private void removeImagesRecursively(Pane pane) {
        // Crea una copia della lista per evitare errori di concurrent modification
        java.util.List<Node> childrenCopy = new ArrayList<>(pane.getChildren());
        for (Node child : childrenCopy) {
            if (child instanceof ImageView) {
                pane.getChildren().remove(child);
            } else if (child instanceof Pane) {
                // Richiama il metodo ricorsivamente sul contenitore
                removeImagesRecursively((Pane) child);
            }
        }
    }

//    public void printShipboard() {
//        //shipboardGrid.getChildren().clear();
//        playerShipboard = Objects.requireNonNull(getPlayer()).getShipBoard();
//        removeImagesFromShipboardGrid();
//        playerShipboard.getGrid().forEach((coordinates, tile) -> {
//            if (tile.isPresent()) {
//                int idTile = tile.get().getIdTile();
//                ImageView imageView = tilesMap.get(idTile);
//                if (imageView != null) {
//                    imageView.setFitWidth(60);
//                    imageView.setFitHeight(60);
//                    imageView.setUserData(idTile);
//                    imageView.setMouseTransparent(true);
//                    switch (tile.get().getTileOrientation()) {
//                        case NORTH -> imageView.setRotate(0);
//                        case EAST -> imageView.setRotate(90);
//                        case SOUTH -> imageView.setRotate(180);
//                        case WEST -> imageView.setRotate(270);
//                    }
//                    javafx.scene.image.Image i = new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Graysquare.png")).toExternalForm());
//                    getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getChildren().add(imageView);
//                    shipboard.getProperties().put(coordinates, imageView);
//                    if (tile.get().getComponent() instanceof ContainerComponent) {
//
//                        if (isTheComponentOfThisType(ComponentType.BATTERY, coordinates)) {
//                            switch (tile.get().getTileOrientation()) {
//                                case NORTH, SOUTH -> {
//                                    HBox tokensBox = new HBox(1);
//                                    tokensBox.setAlignment(Pos.CENTER);
//                                    if (isTheComponentOfThisType(ComponentType.BATTERY, coordinates)) {
//                                        BackgroundImage backgroundImageGray = new BackgroundImage(
//                                                i,
//                                                BackgroundRepeat.NO_REPEAT,
//                                                BackgroundRepeat.NO_REPEAT,
//                                                BackgroundPosition.CENTER,
//                                                new BackgroundSize(100, 100, true, true, true, false)
//                                        );
//                                        tokensBox.setBackground(new Background(backgroundImageGray));
//                                    }
//                                    final int[] ind = {0};
//                                    tile.ifPresent(t -> {
//                                        t.getComponent().getTokenList().forEach(token -> {
//                                            final int tokenIndex = ind[0];
//                                            ImageView imgV = new ImageView(tokensMap.get(token).getImage());
//                                            if (token.equals(Token.BATTERY_CELLS)) {
//                                                switch (tile.get().getTileOrientation()) {
//                                                    case NORTH -> imgV.setRotate(0);
//                                                    case SOUTH -> imgV.setRotate(180);
//                                                }
//                                                imgV.setFitWidth(10);
//                                                imgV.setFitHeight(30);
//                                            } else if (token.equals(Token.ASTRONAUT) || token.equals(Token.PURPLE_ALIENS) || token.equals(Token.BROWN_ALIENS)) {
//                                                imgV.setFitWidth(20);
//                                                imgV.setFitHeight(30);
//                                            } else {
//                                                imgV.setFitWidth(30);
//                                                imgV.setFitHeight(30);
//                                            }
//                                            imgV.setUserData(token);
//                                            tokensBox.getChildren().add(imgV);
//                                            tokensBox.getProperties().put(tokenIndex, imgV);
//                                            ind[0]++;
//                                        });
//                                        tokensBox.setMouseTransparent(true);
//                                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getChildren().add(tokensBox);
//                                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getProperties().put("tokens", tokensBox);
//
//                                    });
//                                }
//                                case EAST, WEST -> {
//                                    VBox tokensBox = new VBox(1);
//                                    tokensBox.setAlignment(Pos.CENTER);
//                                    if (tile.get().getComponent().getType().equals(ComponentType.BATTERY)) {
//                                        BackgroundImage backgroundImageGray = new BackgroundImage(
//                                                i,
//                                                BackgroundRepeat.NO_REPEAT,
//                                                BackgroundRepeat.NO_REPEAT,
//                                                BackgroundPosition.CENTER,
//                                                new BackgroundSize(100, 100, true, true, true, false)
//                                        );
//                                        tokensBox.setBackground(new Background(backgroundImageGray));
//                                    }
//                                    final int[] ind = {0};
//                                    tile.ifPresent(t -> {
//                                        t.getComponent().getTokenList().forEach(token -> {
//                                            final int tokenIndex = ind[0];
//                                            ImageView imgV = new ImageView(tokensMap.get(token).getImage());
//                                            if (token.equals(Token.BATTERY_CELLS)) {
//                                                imgV.setFitWidth(10);
//                                                imgV.setFitHeight(30);
//                                                imgV.setPreserveRatio(false);
//                                                switch (tile.get().getTileOrientation()) {
//                                                    case EAST -> imgV.setRotate(90);
//                                                    case WEST -> imgV.setRotate(270);
//                                                }
//                                                imgV.setUserData(token);
//                                                Group rotatedGroup = new Group(imgV);
//                                                tokensBox.getChildren().add(rotatedGroup);
//                                                tokensBox.getProperties().put(tokenIndex, imgV);
//                                            } else if (token.equals(Token.ASTRONAUT) || token.equals(Token.PURPLE_ALIENS) || token.equals(Token.BROWN_ALIENS)) {
//                                                imgV.setFitWidth(20);
//                                                imgV.setFitHeight(30);
//                                                imgV.setUserData(token);
//                                                tokensBox.getChildren().add(imgV);
//                                                tokensBox.getProperties().put(tokenIndex, imgV);
//                                            } else {
//                                                imgV.setFitWidth(30);
//                                                imgV.setFitHeight(30);
//                                                imgV.setUserData(token);
//                                                tokensBox.getChildren().add(imgV);
//                                                tokensBox.getProperties().put(tokenIndex, imgV);
//                                            }
//                                            ind[0]++;
//                                        });
//                                        tokensBox.setMouseTransparent(true);
//                                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getChildren().add(tokensBox);
//                                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getProperties().put("tokens", tokensBox);
//                                    });
//                                }
//                                default -> {
//                                    guiApplication.printUpdateMessage(error + "Unknown tile orientation: " + tile.get().getTileOrientation());
//                                }
//                            }
//                        }
//                        else{
//                            HBox tokensBox = new HBox(1);
//                            tokensBox.setAlignment(Pos.CENTER);
//                            final int[] ind = {0};
//                            tile.ifPresent(t -> {
//                                t.getComponent().getTokenList().forEach(token -> {
//                                    final int tokenIndex = ind[0];
//                                    ImageView imgV = new ImageView(tokensMap.get(token).getImage());
//                                    if (token.equals(Token.BATTERY_CELLS)) {
//                                        switch (tile.get().getTileOrientation()) {
//                                            case NORTH -> imgV.setRotate(0);
//                                            case SOUTH -> imgV.setRotate(180);
//                                        }
//                                        imgV.setFitWidth(10);
//                                        imgV.setFitHeight(30);
//                                    } else if (token.equals(Token.ASTRONAUT) || token.equals(Token.PURPLE_ALIENS) || token.equals(Token.BROWN_ALIENS)) {
//                                        imgV.setFitWidth(20);
//                                        imgV.setFitHeight(30);
//                                    } else if(token.equals(Token.RED_GOODS) || token.equals(Token.GREEN_GOODS) || token.equals(Token.BLUE_GOODS) || token.equals(Token.YELLOW_GOODS)) {
//                                        imgV.setFitWidth(20);
//                                        imgV.setFitHeight(20);
//                                    }
//                                    else{
//                                        imgV.setFitWidth(30);
//                                        imgV.setFitHeight(30);
//                                    }
//                                    imgV.setUserData(token);
//                                    tokensBox.getChildren().add(imgV);
//                                    tokensBox.getProperties().put(tokenIndex, imgV);
//                                    ind[0]++;
//                                });
//                                tokensBox.setMouseTransparent(true);
//                                getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getChildren().add(tokensBox);
//                                getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getProperties().put("tokens", tokensBox);
//
//                            });
//                        }
//
//
//                    }
//                }
//            }
//        });
//    }
public void printShipboard() {
    shipBoard.getChildren().clear();
    gameSession.getPlayer(guiApplication.getMyId()).get().getShipBoard().getGrid().forEach((coordinates, tile) -> {
        selectedTokens.setStyle("-fx-text-fill: white; -fx-background-color: rgba(0,30,60,0.8); -fx-padding: 5; -fx-border-color: white; -fx-border-width: 1;");
        if (tile.isPresent()) {
            int idTile = tile.get().getIdTile();


            ImageView imageView = tilesMap.get(idTile);
            if (imageView != null) {
                imageView.setFitWidth(60);
                imageView.setFitHeight(60);
                imageView.setUserData(idTile);
                imageView.setOnMouseClicked(event -> {
                    System.out.println("cliccato"+ coordinates);
                    containerToken.getChildren().clear();
                    if(tile.get().getComponent().getType()==ComponentType.CABINE){
                        containerToken.setSpacing(10);
                        new ArrayList<>(tile.get().getComponent().getTokenList()).forEach(token -> {

                            ImageView tokenImageView;
                            if(token==Token.ASTRONAUT) {
                                //se il token è un astronauta, prendo il colore del giocatore
                                Color playerColor = gameSession.getPlayer(guiApplication.getMyId()).get().getColor();

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
                                        tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Red_astronaut.png").toExternalForm()));
                                }
                            }
                            else{
                                tokenImageView = tokensMap.get(token);
                            }
                            tokenImageView.setFitWidth(50);
                            tokenImageView.setFitHeight(90);
                           tokenImageView.setOnMouseClicked(tokenEvent->{
                               System.out.println("cliccato token "+token);

                             selectedTokens.setText("Tokens selezionati: " + i++);

                           });
                            containerToken.getChildren().add(tokenImageView);

                        });
                    }
                    else{
                        containerToken.setSpacing(10);
                        new ArrayList<>(tile.get().getComponent().getTokenList()).forEach(token -> {


                            Image tokenImage = tokensMap.get(token).getImage();
                            ImageView tokenImageView = new ImageView(tokenImage);
                            tokenImageView.setFitWidth(50);
                            tokenImageView.setFitHeight(90);
                            containerToken.getChildren().add(tokenImageView);

                            tokenImageView.setOnMouseClicked(tokenEvent-> {


                                // Verifica se il token è già selezionato controllando se ha un effetto applicato
                                if (tokenImageView.getEffect() == null) {
                                    System.out.println("Seleziono token " + token + "in poszione nell'hobx " + containerToken.getChildren().indexOf(tokenImageView));
                                    javafx.scene.effect.ColorAdjust colorAdjust = new javafx.scene.effect.ColorAdjust();
                                    colorAdjust.setHue(0.1); // Valore per tendere al giallo
                                    colorAdjust.setSaturation(0.8);
                                    colorAdjust.setBrightness(0.1);
                                    tokenImageView.setEffect(colorAdjust);

                                    // Aggiorna il contatore dei token selezionati
                                    i++;
                                    selectedTokens.setText("Tokens selezionati: " + i);
                                } else {
                                    System.out.println("Deselezionato token " + token + " in posizione nell'hobx " + containerToken.getChildren().indexOf(tokenImageView));
                                    tokenImageView.setEffect(null);

                                    // Decrementa il contatore dei token selezionati
                                    i--;
                                    selectedTokens.setText("Tokens selezionati: " + i);
                                }

                            });


                    });
                    }

                });

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
                                    tokenImageView = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Red_astronaut.png").toExternalForm()));
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

    });}
//    public void loadGrid() {
//        if (getGameMode() == GameMode.LEVEL_1 || getGameMode() == GameMode.FLIGHT_MODE) {
//            javafx.scene.image.Image im = new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResource("/it/polimi/ingsw/is25am49/cardboard/cardboard-1.jpg")).toExternalForm());
//            BackgroundImage backgroundImageGrid = new BackgroundImage(
//                    im,
//                    BackgroundRepeat.NO_REPEAT,
//                    BackgroundRepeat.NO_REPEAT,
//                    BackgroundPosition.CENTER,
//                    new BackgroundSize(100, 100, true, true, true, true)
//            );
//            shipboardGrid.setBackground(new Background(backgroundImageGrid));
//            int rows = 5;
//            int columns = 7;
//            for (int row = 0; row < rows; row++) {
//                for (int col = 0; col < columns; col++) {
//                    StackPane cellContainer = new StackPane();
//                    cellContainer.getProperties().put("coordinates", new Point(col + 4, row + 5));
//                    cellContainer.setStyle("-fx-background-color: transparent;");
//                    cellContainer.setOnMouseClicked(event -> {
//                        clickTile((Point) cellContainer.getProperties().get("coordinates"));
//                    });
//                    shipboardGrid.add(cellContainer, col, row);
//                }
//            }
//        } else if (getGameMode() == GameMode.LEVEL_2) {
//            javafx.scene.image.Image im = new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResource("/it/polimi/ingsw/is25am49/cardboard/cardboard-1b.jpg")).toExternalForm());
//            BackgroundImage backgroundImageGrid = new BackgroundImage(
//                    im,
//                    BackgroundRepeat.NO_REPEAT,
//                    BackgroundRepeat.NO_REPEAT,
//                    BackgroundPosition.CENTER,
//                    new BackgroundSize(100, 100, true, true, true, true)
//            );
//            shipboardGrid.setBackground(new Background(backgroundImageGrid));
//            int rows = 5;
//            int columns = 7;
//            for (int row = 0; row < rows; row++) {
//                for (int col = 0; col < columns; col++) {
//                    StackPane cellContainer = new StackPane();
//                    cellContainer.getProperties().put("coordinates", new Point(col + 4, row + 5));
//                    cellContainer.setStyle("-fx-background-color: transparent;");
//                    cellContainer.setOnMouseClicked(event -> {
//                        clickTile((Point) cellContainer.getProperties().get("coordinates"));
//                    });
//                    shipboardGrid.add(cellContainer, col, row);
//                }
//            }
//        }
//        playerShipboard = Objects.requireNonNull(getPlayer()).getShipBoard();
//        playerStatistics[0] = playerShipboard.getTotalFirePower();
//        playerStatistics[1] = playerShipboard.getTotalEnginePower();
//        playerStatistics[2] = playerShipboard.getTotalCrewNumber();
//        playerStatistics[3] = 0;
//        playerStatistics[4] = playerShipboard.getDestroyedTiles();
//        shipboardGrid.setDisable(false);
//        tokensArrayForGoodPosition.add(Token.RED_GOODS);
//        tokensArrayForGoodPosition.add(Token.GREEN_GOODS);
//        tokensArrayForGoodPosition.add(Token.BLUE_GOODS);
//        tokensArrayForGoodPosition.add(Token.YELLOW_GOODS);
//    }
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

    public void configureEffectPaneYesNo(InputActions inputCommand) {
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0);";
        javafx.scene.control.Label labelString = new javafx.scene.control.Label("Do you want to " + inputCommand.toString() + "?");
        labelString.setStyle(style);
        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");
        yesButton.setStyle(style);
        noButton.setStyle(style);
        switch (inputCommand) {
            case InputActions.ACCEPT_PRIZES -> {
                yesButton.setOnAction(event -> {
                    try {
                        guiApplication.getController().userAcceptPrizes(guiApplication.getMyId(), true);
                    } catch (Exception e) {
                        guiApplication.printUpdateMessage(error + e.getMessage());
                    }
                });
                noButton.setOnAction(event -> {
                    try {
                        guiApplication.getController().userAcceptPrizes(guiApplication.getMyId(), false);
                    } catch (Exception e) {
                        guiApplication.printUpdateMessage(error + e.getMessage());
                    }
                });
            }
            case InputActions.REPAIR_SHIP, InputActions.LAND_ON_STATION -> {
                yesButton.setOnAction(event -> {
                    try {
                        guiApplication.getController().userLandOnStationOrShip(guiApplication.getMyId(), true);
                    } catch (Exception e) {
                        guiApplication.printUpdateMessage(error + e.getMessage());
                    }
                });
                noButton.setOnAction(event -> {
                    try {
                        guiApplication.getController().userLandOnStationOrShip(guiApplication.getMyId(), false);
                    } catch (Exception e) {
                        guiApplication.printUpdateMessage(error + e.getMessage());
                    }                });
            }
        }

        HBox buttonsBox = new HBox(10, yesButton, noButton);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox container = new VBox(20, labelString, buttonsBox);
        container.setAlignment(Pos.CENTER);

        cardEffect.getChildren().clear();
        cardEffect.getChildren().add(container);
    }

    public void stopFlight() {
        cardEffect.getChildren().clear();
        updateCardMessageBox("Your flight has ended.");
        drawButton.setDisable(true);
        shipboardGrid.setDisable(true);
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

    public void configureEffectPanePlanets() {
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0);";
        javafx.scene.control.Label labelString = new javafx.scene.control.Label("Where do you want to land?");
        labelString.setStyle(style);
        int numPlanets = 0;
        if (currentAdventureCard instanceof PlanetCard planetCard) {
            numPlanets = planetCard.getNumOfPlanets();
        }
        ArrayList<Button> buttons = new ArrayList<>();
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        for (int i = 1; i <= numPlanets; i++) {
            Button planetButton = new Button("Planet " + i);
            planetButton.setStyle(style);
            int planetIndex = i;
            planetButton.setOnAction(event -> {
                try {
                    guiApplication.getController().userLandOnPlanet(guiApplication.getMyId(), true, planetIndex);
                } catch (Exception e) {
                    guiApplication.printUpdateMessage(error + e.getMessage());
                }
            });
            buttons.add(planetButton);
            buttonsBox.getChildren().add(planetButton);
        }
        Button planetButton = new Button("Don't land");
        planetButton.setStyle(style);
        planetButton.setOnAction(event -> {
            try {
                guiApplication.getController().userLandOnPlanet(guiApplication.getMyId(), false, -1);
            } catch (Exception e) {
                guiApplication.printUpdateMessage(error + e.getMessage());
            }
        });
        buttons.add(planetButton);
        buttonsBox.getChildren().add(planetButton);


        VBox container = new VBox(20, labelString, buttonsBox);
        container.setAlignment(Pos.CENTER);

        cardEffect.getChildren().clear();
        cardEffect.getChildren().add(container);
    }

    public void configureEffectPaneFinishDecision(InputActions inputCommand) {
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0);";
        javafx.scene.control.Label labelString = new javafx.scene.control.Label("Click here to finish: ");
        labelString.setStyle(style);
        Button finishButton = new Button("Finish");
        finishButton.setStyle(style);
        switch (inputCommand) {
            case InputActions.ACTIVATE_COMPONENTS -> {
                finishButton.setOnAction(event -> {
                    try {
                        System.out.println("The Token points list is" + tokenPointsListDefenitive);
                        System.out.println("The coordinates set is" + coordinatesSet);
                        guiApplication.getController().userFinishDecisionActivating(guiApplication.getMyId(), coordinatesSet);
                    } catch (Exception e) {
                        guiApplication.printUpdateMessage(error + e.getMessage());
                    }
                });
            }
            case InputActions.SELECT_TOKENS -> {
                finishButton.setOnAction(event -> {
                    try {
                        System.out.println("The Token points list is" + tokenPointsListDefenitive);
                        System.out.println("The coordinates set is" + coordinatesSet);
                        guiApplication.getController().userFinishDecisionToken(guiApplication.getMyId(), tokenPointsListDefenitive);
                    } catch (Exception e) {
                        guiApplication.printUpdateMessage(error + e.getMessage());
                    }
                });
            }
        }
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getChildren().add(finishButton);

        VBox container = new VBox(20, labelString, buttonsBox);
        container.setAlignment(Pos.CENTER);

        cardEffect.getChildren().clear();
        cardEffect.getChildren().add(container);
    }

    public void configureEffectPaneThrowDices() {
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0);";
        javafx.scene.control.Label labelString = new javafx.scene.control.Label("Click here to throw the dices");
        labelString.setStyle(style);
        Button throwButton = new Button("Throw");
        throwButton.setStyle(style);
        throwButton.setOnAction(event -> {
            try {
                guiApplication.getController().userThrowDices(guiApplication.getMyId());
            } catch (Exception e) {
                guiApplication.printUpdateMessage(error + e.getMessage());
            }
        });

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getChildren().add(throwButton);

        VBox container = new VBox(20, labelString, buttonsBox);
        container.setAlignment(Pos.CENTER);

        cardEffect.getChildren().clear();
        cardEffect.getChildren().add(container);
    }

    public void configureEffectPaneSimpleString(String string) {
        javafx.scene.control.Label message = new javafx.scene.control.Label();
        message.setText(string);
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0); -fx-padding: 10;";
        message.setStyle(style);
        message.setWrapText(true);
        message.setMaxWidth(400);
        message.setAlignment(Pos.TOP_LEFT);
        message.setTextAlignment(TextAlignment.LEFT);

        cardEffect.getChildren().clear();
        cardEffect.getChildren().add(message);
    }

    public void configureEffectPaneGoods(int red, int green, int blue, int yellow) {
        goodsNumbers[0] = red;
        goodsNumbers[1] = green;
        goodsNumbers[2] = blue;
        goodsNumbers[3] = yellow;
        configureEffectPaneShowNextGood();

    }

    public void configureEffectPaneShowNextGood() {
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0);";
        javafx.scene.control.Label labelString = new javafx.scene.control.Label("You are placing the good above");
        labelString.setStyle(style);
        Button placeButton = new Button("Place");
        placeButton.setStyle(style);


        int i;
        for(i=0; i<goodsNumbers.length; i++){
            if(goodsNumbers[i] > 0){
                tokens.setDisable(false);
                tokens.setVisible(true);
                tokens.getChildren().clear();
                ImageView imView = new ImageView(goodsMap.get(i));
                tokens.getChildren().add(imView);
                goodsNumbers[i]--;
                int finalI = i;
                placeButton.setOnAction(event -> {
                    tokenPointsListDefenitive.add(new Pair<>(lastSelectedCoordinates, tokensArrayForGoodPosition.get(finalI)));
                    configureEffectPaneShowNextGood();
                });
                VBox container = new VBox(20, labelString, placeButton);
                container.setAlignment(Pos.CENTER);
                cardEffect.getChildren().clear();
                cardEffect.getChildren().add(container);
                return;
            }
        }
        guiApplication.getController().userFinishDecisionToken(guiApplication.getMyId(), tokenPointsListDefenitive);
        tokens.getChildren().clear();
        tokens.setDisable(true);
        tokens.setVisible(false);
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
            StringBuilder stats = new StringBuilder("Player: " + player.getUsername() + "\n");
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

    public void updateShipboardDestroyedTiles(Set<Point> destroyedTiles) {
        printShipboard();
    }

    public void updateCardState(AdventureStatesTypes state) {
        currentAdventureState = state;
        switch (currentAdventureState) {
            case DRAW -> {
                drawButton.setDisable(false);
//                disableAllCellsShipboard();
                stateCard.setText("Updating... Now the leader must draw again");
                confirm.setVisible(false);
                send.setVisible(false);
                clearEffectPane();
            }
            case  DECISION_ASTRONAUTS_OR_ALIENS -> {
                stateCard.setText("Please select the Astronauts or Aliens you want to remove.");
                drawButton.setDisable(true);
                confirm.setText("Finish");
                send.setVisible(true);
                send.setOnAction(event -> {
                    String value = input.getText();
                    Token tok;
                    if(value!=null && !value.isEmpty()) {
                        try{
                            String[] parts = value.trim().split("\\s+");

                            int x = Integer.parseInt(parts[0]);
                            int y = Integer.parseInt(parts[1]);
                            char tokenType = parts[2].charAt(0); // prende il primo carattere del terzo input
                            Point coordinates = new Point(x, y);
//                            Point point = new Point(x, y);
//                            char xChar = value.charAt(0);
//                            char yChar = value.charAt(2);
//                            char tokenType = value.charAt(4);
//                            int x = Character.getNumericValue(xChar);
//                            int y = Character.getNumericValue(yChar);


                            switch (tokenType){
                                case 'a':
                                    tok = Token.ASTRONAUT;
                                    break;
                                case 'p':
                                    tok = Token.PURPLE_ALIENS;
                                    break;
                                case 'b':
                                    tok = Token.BROWN_ALIENS;
                                    break;
                                default:
                                    guiApplication.printUpdateMessage(error + "invalid choice, select astronauts or aliens");
                                    input.clear();
                                    return;
                            }
                            tokenPointsListDefenitive.add(new Pair<>(coordinates,tok));
                            input.clear();
                            addMessage("Batteria aggiunta alle coordinate: " + coordinates);
                        } catch (Exception e) {
                            addMessage("Invalid input format. Use the format 'x y' z'");
                        }
                    }
                });
                confirm.setOnAction(event -> {
                    try {
                        guiApplication.getController().userFinishDecisionToken(guiApplication.getMyId(), tokenPointsListDefenitive);
                        input.clear();
                        tokenPointsListDefenitive.clear();
                    } catch (Exception e) {
                        guiApplication.printUpdateMessage(error + e.getMessage());
                        tokenPointsListDefenitive.clear();
                    }
                });


            }
            case DECISION_BATTERIES -> {
                stateCard.setText("Please select the Batteries you want to activate.");
                confirm.setText("Finish");
                send.setVisible(true);
                send.setOnAction(event -> {
                    String value = input.getText().trim();
                    if (value != null && !value.isEmpty()) {
                        try {
//                            // Dividi l'input usando lo spazio come separatore
//                            String[] coordinates = value.split("\\s+");
//                            if (coordinates.length != 2) {
//                                addMessage("Formato non valido. Inserire coordinate nel formato 'x y'");
//                                return;
//                            }
//
//                            int x = Integer.parseInt(coordinates[0]);
//                            int y = Integer.parseInt(coordinates[1]);
//                            Point coordinatess = new Point(x, y);
                            String[] parts = value.trim().split("\\s+");

                            int x = Integer.parseInt(parts[0]);
                            int y = Integer.parseInt(parts[1]);
                            Point coordinatess = new Point(x, y);

                            tokenPointsListDefenitive.add(new Pair<>(coordinatess, Token.BATTERY_CELLS));
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
                        System.out.println("chiamato");
                        guiApplication.getController().userFinishDecisionToken(guiApplication.getMyId(), tokenPointsListDefenitive);
                        input.clear();
                        tokenPointsListDefenitive.clear();;
                    } catch (Exception e) {
                        guiApplication.printUpdateMessage(error + e.getMessage());
                        tokenPointsListDefenitive.clear();
                    }
                });
            }

            case DECISION_CANNON,DECISION_SHIELDS, DECISION_MOTOR -> {
                drawButton.setDisable(false);
                switch (currentAdventureState) {
                    case DECISION_SHIELDS -> stateCard.setText("Please select the Shields you want to activate.");
                    case DECISION_MOTOR -> stateCard.setText("Please select the Motors you want to activate.");
                    case DECISION_CANNON -> stateCard.setText("Please select the Cannons you want to activate.");
                }
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
                            coordinatesSet.add(coordinatess);
                            input.clear();
                            addMessage("Cannone selezionato alle coordinate: " + coordinatess);
                        }catch (NumberFormatException e) {
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
                        guiApplication.getController().userFinishDecisionActivating(guiApplication.getMyId(), coordinatesSet);
                        input.clear();
                        coordinatesSet.clear();
                    }catch (Exception e) {
                        guiApplication.printUpdateMessage(error + e.getMessage());
                        printShipboard();
                        coordinatesSet.clear();
                    }
                });
            }

            case DECISION_GOODS -> {
                drawButton.setDisable(true);
                send.setVisible(true);
                confirm.setText("Finish");
                stateCard.setText("Please select the storage in which you wanna add the goods.");
                send.setOnAction(event -> {
                    String value = input.getText();
                    Token tok;
                    if(value!=null && !value.isEmpty()) {
                        try{
//                            char xChar = value.charAt(0);
//                            char yChar = value.charAt(2);
//                            char tokenType = value.charAt(4);
//                            int x = Character.getNumericValue(xChar);
//                            int y = Character.getNumericValue(yChar);
//                            Point coordinates = new Point(x, y);
                            String[] parts = value.trim().split("\\s+");

                            int x = Integer.parseInt(parts[0]);
                            int y = Integer.parseInt(parts[1]);
                            char tokenType = parts[2].charAt(0); // prende il primo carattere del terzo input
                            Point coordinates = new Point(x, y);

                            switch (tokenType){
                                case 'r':
                                    tok = Token.RED_GOODS;
                                    break;
                                case 'y':
                                    tok = Token.YELLOW_GOODS;
                                    break;
                                case 'b':
                                    tok = Token.BLUE_GOODS;
                                    break;
                                case 'g':
                                    tok = Token.GREEN_GOODS;
                                    break;
                                default:
                                    guiApplication.printUpdateMessage(error + "invalid choice, select astronauts or aliens");
                                    input.clear();
                                    return;
                            }
                            tokenPointsListDefenitive.add(new Pair<>(coordinates,tok));
                        } catch (Exception e) {
                            addMessage("Invalid input format. Use the format 'x y' z'");
                        }
                    }});
                confirm.setOnAction(event -> {
                    try {
                        guiApplication.getController().userFinishDecisionToken(guiApplication.getMyId(), tokenPointsListDefenitive);
                        input.clear();
                        tokenPointsListDefenitive.clear();
                    } catch (Exception e) {
                        guiApplication.printUpdateMessage(error + e.getMessage());
                        tokenPointsListDefenitive.clear();
                    }
                });

                }

            case DECISION_LAND_PLANET -> {
                drawButton.setDisable(true);
                stateCard.setText("Please select the planet you want to land on.");
                confirm.setText("Land");
                send.setVisible(false);
                confirm.setOnAction(event -> {
                    String value = input.getText();
                    if(value != null && value.length() == 1) {
                        guiApplication.getController().userLandOnPlanet(guiApplication.getMyId(), true, Integer.parseInt(value));
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
                drawButton.setDisable(true);
                stateCard.setText("Do you want to accept the prizes?");
                confirm.setText("Accept");
                send.setVisible(false);
                confirm.setOnAction(event -> {
                    String value = input.getText();
                    if(value.equals("yes") || value.equals("y") || value.equals("Y") || value.equals("Yes")){
                        guiApplication.getController().userAcceptPrizes(guiApplication.getMyId(), true);
                    }
                    else {
                        guiApplication.getController().userAcceptPrizes(guiApplication.getMyId(), false);
                    }
                });

            }
            case DECISION_LAND_STATION_OR_SHIP -> {
                drawButton.setDisable(true);
                stateCard.setText("Select if you wanna land on the station or on your ship.");
                confirm.setText("Land");
                send.setVisible(false);
                confirm.setOnAction(event -> {
                    String value = input.getText();
                    if(value.equals("yes") || value.equals("y") || value.equals("Y") || value.equals("Yes")){
                        guiApplication.getController().userLandOnStationOrShip(guiApplication.getMyId(), true);
                    }
                    else {
                       guiApplication.getController().userLandOnStationOrShip(guiApplication.getMyId(), false);
                    }
                });
            }
            case ROLL_DICES -> {
                drawButton.setDisable(true);
            }
            case UPDATE -> {
                drawButton.setDisable(false);
                stateCard.setText("Updating... Now the leader must draw again");
                confirm.setVisible(false);
                send.setVisible(false);
                updateChangeTurn(gameSession.getGame().get().getGameboard().getCurrentPlayer().getIdPlayer());
//                disableAllCellsShipboard();
                clearEffectPane();
            }
            default -> {
            }

        }
    }



    private void clickTile(Point coordinates) {
        switch (currentAdventureState) {
            case AdventureStatesTypes.DECISION_BATTERIES -> {
                if (isTheComponentOfThisType(ComponentType.BATTERY, coordinates)) {
                    Pane pane = (Pane) getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getProperties().get("tokens");
                    pane.getChildren().removeLast();
                    tokenPointsListDefenitive.add(new Pair<>(coordinates, Token.BATTERY_CELLS));
                    lastSelectedCoordinates = coordinates;
                } else {
                    guiApplication.printUpdateMessage(error + "You can only select Batteries.");
                }
            }
            case AdventureStatesTypes.DECISION_CANNON -> {
                if (isTheComponentOfThisType(ComponentType.DOUBLE_CANNON, coordinates)) {
                    if (coordinatesSet.contains(coordinates)) {
                        coordinatesSet.remove(coordinates);
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
                    } else {
                        lastSelectedCoordinates = coordinates;
                        coordinatesSet.add(coordinates);
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).setStyle(styleForHighlight);
                    }
                } else {
                    guiApplication.printUpdateMessage(error + "You can only select Cannons.");
                }
            }
            case AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS -> {
                if (isTheComponentOfThisType(ComponentType.CABINE, coordinates)) {
                    Pane pane = (Pane) getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getProperties().get("tokens");
                    Token tok = (Token) pane.getChildren().removeLast().getUserData();
                    tokenPointsListDefenitive.add(new Pair<>(coordinates, tok));
                    lastSelectedCoordinates = coordinates;
                } else {
                    guiApplication.printUpdateMessage(error + "You can only select Cabines.");
                }
            }
            case AdventureStatesTypes.DECISION_SHIELDS -> {
                if (coordinatesSet.contains(coordinates)) {
                    coordinatesSet.remove(coordinates);
                    getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
                } else {
                    lastSelectedCoordinates = coordinates;
                    coordinatesSet.add(coordinates);
                    getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).setStyle(styleForHighlight);
                }
            }
            case AdventureStatesTypes.DECISION_GOODS -> {

                if (isTheComponentOfThisType(ComponentType.SPECIAL_STORAGE, coordinates) || isTheComponentOfThisType(ComponentType.STORAGE, coordinates)) {
                    if (coordinatesSet.contains(coordinates)) {
                        coordinatesSet.remove(coordinates);
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getChildren().remove(highlightPane);
                    } else {
                        lastSelectedCoordinates = coordinates;
                        coordinatesSet.add(coordinates);
                        highlightPane.getChildren().clear();
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getChildren().add(highlightPane);
                    }
                } else {
                    guiApplication.printUpdateMessage(error + "You can only select Storage.");
                }
            }
            case AdventureStatesTypes.DECISION_MOTOR -> {

                if (isTheComponentOfThisType(ComponentType.DOUBLE_ENGINE, coordinates)) {
                    if (coordinatesSet.contains(coordinates)) {
                        coordinatesSet.remove(coordinates);
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
                    } else {
                        lastSelectedCoordinates = coordinates;
                        coordinatesSet.add(coordinates);
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).setStyle(styleForHighlight);
                    }
                } else {
                    guiApplication.printUpdateMessage(error + "You can only select Engines.");
                }
            }
        }
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

    private boolean isMyTurn() {
        Player player = getPlayer();
        Player currPlayer;

        currPlayer = Objects.requireNonNull(getGame()).getGameboard().getCurrentPlayer();
        return currPlayer.equals(player);
    }

    private boolean isThisPlayerLeader() {
        Player player = getPlayer();
        Player leader;
        leader = Objects.requireNonNull(getGame()).getGameboard().getLeader();
        return leader.equals(player);
    }

    private LeaderBoard getleaderBoard() {
        return Objects.requireNonNull(getGame()).getGameboard().getLeaderBoard();
    }

    private void dbg() {
        System.out.println("Debug" + counterStampe++ + " " + currentAdventureState.toString());
    }

    private boolean isTheComponentOfThisType(ComponentType type, Point coordinates) {
        return playerShipboard.getTile(coordinates).getComponentType().equals(type);
    }

    public StackPane getNodeFromGridPane(int col, int row) {
        for (Node node : shipboardGrid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);
            int currentCol = (colIndex == null ? 0 : colIndex);
            int currentRow = (rowIndex == null ? 0 : rowIndex);
            if (currentCol == col && currentRow == row) {
                return (StackPane) node;
            }
        }
        return null;
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

    private boolean isPresentInTokenList(Point coordinates, Token token) {
        return tokenPointsListDefenitive.stream().anyMatch(pair -> pair.getKey().equals(coordinates) && pair.getValue().equals(token));
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

    public void deActivatePoints(List<Point> coordinates) {
        coordinates.forEach(coordinate -> {
            StackPane node = getNodeFromGridPane(coordinate.x - 4, coordinate.y - 5);
            if (node != null) {
                node.setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
            }
        });
    }

    public void removeTokenFromTile(Point coordinate, Token tok) {
        HBox tokensBox = (HBox) getNodeFromGridPane(coordinate.x - 4, coordinate.y - 5).getProperties().get("tokens");
        if (tokensBox != null) {
            boolean[] firstRemove = {true};
            tokensBox.getChildren().removeIf(node -> {
                if (node instanceof ImageView imgV && firstRemove[0]) {
                    firstRemove[0] = false;
                    return imgV.getUserData().equals(tok);
                }
                return false;
            });
        } else {
            guiApplication.printUpdateMessage(error + "No tokens found on the tile at " + coordinate);
        }
    }

    public void addTokenToTile(Point coordinate, Token token) {
        HBox tokensBox = (HBox) getNodeFromGridPane(coordinate.x - 4, coordinate.y - 5).getProperties().get("tokens");
        if (tokensBox == null) {
            tokensBox = new HBox(1);
            tokensBox.setAlignment(Pos.CENTER);
            getNodeFromGridPane(coordinate.x - 4, coordinate.y - 5).getProperties().put("tokens", tokensBox);
        }
        Image im = tokensMap.get(token).getImage();
        ImageView newImageView = new ImageView(im);
        tokensBox.getChildren().add(newImageView);

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
    public void updateAttack(Point coord, Orientation or, boolean isMeteor){
        getNodeFromGridPane(coord.x - 4, coord.y - 5).getChildren().add(highlightPane);
        timerForAnimation(coord);
    }
    private void timerForAnimation(Point coord) {
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> animationFinishAttack(coord));
        pause.play();
    }
    private void animationFinishAttack(Point coord) {
        getNodeFromGridPane(coord.x - 4, coord.y - 5).getChildren().remove(highlightPane);
        printShipboard();
    }
//    private void enableAllCellsShipboard() {
//        for (Node node : shipboardGrid.getChildren()) {
//            if (node instanceof StackPane) {
//                node.setDisable(false);
//            }
//        }
//    }
//    private void disableAllCellsShipboard() {
//        for (Node node : shipboardGrid.getChildren()) {
//            if (node instanceof StackPane) {
//                node.setDisable(true);
//            }
//        }
//    }
    //    private void removeCellFromShipboard(Point coordinates) {
//        Node node = getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5);
//        if (node != null) {
//            shipboardGrid.getChildren().remove(node);
//        } else {
//            guiApplication.printUpdateMessage(error + "No tile found at " + coordinates);
//        }
//    }
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
}
