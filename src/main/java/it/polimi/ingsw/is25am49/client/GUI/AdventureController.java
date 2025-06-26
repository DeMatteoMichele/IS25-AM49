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
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.Pair;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

public class AdventureController extends GenericSceneController {
    @FXML
    public Button stopButton;
    @FXML
    public StackPane tokens;
    @FXML
    private StackPane cardMessages;
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
    private AnchorPane adventureContainer;
    @FXML
    private VBox messageBox;
    @FXML
    private ScrollPane scrollPane;

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
    private final int[] gdn;
    private final Map<Integer, Image> goodsMap;
    private final ArrayList<Token> tokensArrayForGoodPosition;
    private Point lastSelectedCoordinates;
    private final String styleForHighlight;
    private final String styleForDamage;
    private final String styleForTransparent;
    private final Map<Point, StackPane> highlightsMap;

    public AdventureController() {
        coordinatesSet = new HashSet<>();
        tokenPointsListDefenitive = new ArrayList<>();
        playerShipboard = null;
        currentAdventureCard = null;
        counterStampe = 0;
        currentAdventureState = AdventureStatesTypes.DRAW;
        playerStatistics = new double[5];
        goodsNumbers = new int[5];
        gdn = new int[5];
        goodsMap = new HashMap<>();
        lastSelectedCoordinates = new Point(-1, -1);
        tokensArrayForGoodPosition = new ArrayList<>();
        styleForHighlight = "-fx-border-color: yellow; -fx-border-width: 5; -fx-border-style: solid; -fx-background-color: rgba(255,255,0,0.3);";
        styleForDamage = "-fx-border-color: red; -fx-border-width: 5; -fx-border-style: solid; -fx-background-color: rgba(255,0,0,0.3);";
        styleForTransparent = "-fx-border-color: transparent; -fx-border-width: 0;";
        highlightsMap = new HashMap<>();
    }


    public void initialize() {
        tokens.setDisable(true);
        tokens.setVisible(false);
        Image image = new Image(getClass().getResource("/sfondo3.jpg").toExternalForm());
        backgroundImage.setImage(image);
        backgroundImage.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                backgroundImage.fitWidthProperty().bind(newScene.widthProperty());
                backgroundImage.fitHeightProperty().bind(newScene.heightProperty());
            }
        });
        String path = "/it/polimi/ingsw/is25am49/tokens/";
        goodsMap.put(0, new Image(Objects.requireNonNull(getClass().getResource(path + "redgood.png")).toExternalForm()));
        goodsMap.put(1, new Image(Objects.requireNonNull(getClass().getResource(path + "greengood.png")).toExternalForm()));
        goodsMap.put(2, new Image(Objects.requireNonNull(getClass().getResource(path + "bluegood.png")).toExternalForm()));
        goodsMap.put(3, new Image(Objects.requireNonNull(getClass().getResource(path + "yellowgood.png")).toExternalForm()));
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
        loadAdventureCardsAndButton();
        loadLeaderBoard();
        loadTokens();
        loadTilesMap();
        loadGrid();
        printShipboard();
        updatePlayerStats(0, 0, 0, 0, 0);
        cardEffect.setVisible(true);
        cardMessages.setVisible(true);
        damagePane.setStyle(styleForDamage);
        highlightPane.setStyle(styleForHighlight);
    }

    private void loadLeaderBoard() {
        if (getGameMode() == GameMode.FLIGHT_MODE || getGameMode() == GameMode.LEVEL_1) {
            String path = "/it/polimi/ingsw/is25am49/cardboard/cardboard-3.png";
            Image image = new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
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
            Canvas canvas = new Canvas();
            drawLeaderBoardState(canvas, getleaderBoard(), leaderboardImage);
            leaderboard.getChildren().clear();
            leaderboard.getChildren().add(leaderboardImage);
            leaderboard.getProperties().put("leaderboard", leaderboardImage);
            leaderboard.getProperties().put("canvas", canvas);
            leaderboard.getChildren().add(canvas);
        } else if (getGameMode() == GameMode.LEVEL_2) {
            String path = "/it/polimi/ingsw/is25am49/cardboard/cardboard-5.png";
            Image image = new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
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
            Canvas canvas = new Canvas();
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
                    new BackgroundSize(100, 100, true, true, true, false)
            );
            drawButton.setBackground(new Background(backgroundImageButton));
        } else if (getGameMode() == GameMode.LEVEL_2) {
            String pathBack = "/it/polimi/ingsw/is25am49/graphics/GT-cards_II121.jpg";
            image = new Image(Objects.requireNonNull(getClass().getResource(pathBack)).toExternalForm());
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
        List<Node> childrenCopy = new ArrayList<>(pane.getChildren());
        for (Node child : childrenCopy) {
            if (child instanceof ImageView) {
                pane.getChildren().remove(child);
            } else if (child instanceof Pane) {
                removeImagesRecursively((Pane) child);
            }
        }
        if (pane instanceof HBox) {
            pane.setBackground(null);
        }
    }

    public void printShipboard() {
        //shipboardGrid.getChildren().clear();
        playerShipboard = Objects.requireNonNull(getPlayer()).getShipBoard();
        removeImagesFromShipboardGrid();
        playerShipboard.getGrid().forEach((coordinates, tile) -> {
            getNodeFromGridPane(coordinates.x -4, coordinates.y -5).getChildren().remove(highlightsMap.get(coordinates));
            if (tile.isPresent()) {
                int idTile = tile.get().getIdTile();
                ImageView imageView = tilesMap.get(idTile);
                if (imageView != null) {
                    imageView.setFitWidth(60);
                    imageView.setFitHeight(60);
                    imageView.setUserData(idTile);
                    imageView.setMouseTransparent(true);
                    switch (tile.get().getTileOrientation()) {
                        case NORTH -> imageView.setRotate(0);
                        case EAST -> imageView.setRotate(90);
                        case SOUTH -> imageView.setRotate(180);
                        case WEST -> imageView.setRotate(270);
                    }
                    Image i = new Image(Objects.requireNonNull(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/graysquare.png")).toExternalForm());
                    StackPane sp = getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5);
                    sp.getChildren().add(imageView);
                    sp.getProperties().put("image", imageView);
                    shipboard.getProperties().put(coordinates, imageView);
                    if (tile.get().getComponent() instanceof ContainerComponent) {

                        if (isTheComponentOfThisType(ComponentType.BATTERY, coordinates)) {
                            switch (tile.get().getTileOrientation()) {
                                case NORTH, SOUTH -> {
                                    HBox tokensBox = new HBox(1);
                                    tokensBox.setAlignment(Pos.CENTER);
                                    if (isTheComponentOfThisType(ComponentType.BATTERY, coordinates)) {
                                        BackgroundImage backgroundImageGray = new BackgroundImage(
                                                i,
                                                BackgroundRepeat.NO_REPEAT,
                                                BackgroundRepeat.NO_REPEAT,
                                                BackgroundPosition.CENTER,
                                                new BackgroundSize(100, 100, true, true, true, false)
                                        );
                                        tokensBox.setBackground(new Background(backgroundImageGray));
                                    }
                                    final int[] ind = {0};
                                    tile.ifPresent(t -> {
                                        t.getComponent().getTokenList().forEach(token -> {
                                            final int tokenIndex = ind[0];
                                            ImageView imgV = new ImageView(tokensMap.get(token).getImage());
                                            if (token.equals(Token.BATTERY_CELLS)) {
                                                switch (tile.get().getTileOrientation()) {
                                                    case NORTH -> imgV.setRotate(0);
                                                    case SOUTH -> imgV.setRotate(180);
                                                }
                                                imgV.setFitWidth(10);
                                                imgV.setFitHeight(30);
                                            } else if (token.equals(Token.ASTRONAUT) || token.equals(Token.PURPLE_ALIENS) || token.equals(Token.BROWN_ALIENS)) {
                                                imgV.setFitWidth(20);
                                                imgV.setFitHeight(30);
                                            } else {
                                                imgV.setFitWidth(30);
                                                imgV.setFitHeight(30);
                                            }
                                            imgV.setUserData(token);
                                            tokensBox.getChildren().add(imgV);
                                            tokensBox.getProperties().put(tokenIndex, imgV);
                                            ind[0]++;
                                        });
                                        tokensBox.setMouseTransparent(true);
                                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getChildren().add(tokensBox);
                                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getProperties().put("tokens", tokensBox);

                                    });
                                }
                                case EAST, WEST -> {
                                    VBox tokensBox = new VBox(1);
                                    tokensBox.setAlignment(Pos.CENTER);
                                    if (tile.get().getComponent().getType().equals(ComponentType.BATTERY)) {
                                        BackgroundImage backgroundImageGray = new BackgroundImage(
                                                i,
                                                BackgroundRepeat.NO_REPEAT,
                                                BackgroundRepeat.NO_REPEAT,
                                                BackgroundPosition.CENTER,
                                                new BackgroundSize(100, 100, true, true, true, false)
                                        );
                                        tokensBox.setBackground(new Background(backgroundImageGray));
                                    }
                                    final int[] ind = {0};
                                    tile.ifPresent(t -> {
                                        t.getComponent().getTokenList().forEach(token -> {
                                            final int tokenIndex = ind[0];
                                            ImageView imgV = new ImageView(tokensMap.get(token).getImage());
                                            if (token.equals(Token.BATTERY_CELLS)) {
                                                imgV.setFitWidth(10);
                                                imgV.setFitHeight(30);
                                                imgV.setPreserveRatio(false);
                                                switch (tile.get().getTileOrientation()) {
                                                    case EAST -> imgV.setRotate(90);
                                                    case WEST -> imgV.setRotate(270);
                                                }
                                                imgV.setUserData(token);
                                                Group rotatedGroup = new Group(imgV);
                                                tokensBox.getChildren().add(rotatedGroup);
                                                tokensBox.getProperties().put(tokenIndex, imgV);
                                            } else if (token.equals(Token.ASTRONAUT) || token.equals(Token.PURPLE_ALIENS) || token.equals(Token.BROWN_ALIENS)) {
                                                imgV.setFitWidth(20);
                                                imgV.setFitHeight(30);
                                                imgV.setUserData(token);
                                                tokensBox.getChildren().add(imgV);
                                                tokensBox.getProperties().put(tokenIndex, imgV);
                                            } else {
                                                imgV.setFitWidth(30);
                                                imgV.setFitHeight(30);
                                                imgV.setUserData(token);
                                                tokensBox.getChildren().add(imgV);
                                                tokensBox.getProperties().put(tokenIndex, imgV);
                                            }
                                            ind[0]++;
                                        });
                                        tokensBox.setMouseTransparent(true);
                                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getChildren().add(tokensBox);
                                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getProperties().put("tokens", tokensBox);
                                    });
                                }
                                default -> {
                                    guiApplication.printUpdateMessage(error + "Unknown tile orientation: " + tile.get().getTileOrientation());
                                }
                            }
                        } else {
                            HBox tokensBox = new HBox(1);
                            tokensBox.setAlignment(Pos.CENTER);
                            final int[] ind = {0};
                            tile.ifPresent(t -> {
                                t.getComponent().getTokenList().forEach(token -> {
                                    final int tokenIndex = ind[0];
                                    ImageView imgV = new ImageView(tokensMap.get(token).getImage());
                                    if (token.equals(Token.BATTERY_CELLS)) {
                                        switch (tile.get().getTileOrientation()) {
                                            case NORTH -> imgV.setRotate(0);
                                            case SOUTH -> imgV.setRotate(180);
                                        }
                                        imgV.setFitWidth(10);
                                        imgV.setFitHeight(30);
                                    } else if (token.equals(Token.ASTRONAUT) || token.equals(Token.PURPLE_ALIENS) || token.equals(Token.BROWN_ALIENS)) {
                                        imgV.setFitWidth(20);
                                        imgV.setFitHeight(30);
                                    } else if (token.equals(Token.RED_GOODS) || token.equals(Token.GREEN_GOODS) || token.equals(Token.BLUE_GOODS) || token.equals(Token.YELLOW_GOODS)) {
                                        imgV.setFitWidth(20);
                                        imgV.setFitHeight(20);
                                    } else {
                                        imgV.setFitWidth(30);
                                        imgV.setFitHeight(30);
                                    }
                                    imgV.setUserData(token);
                                    tokensBox.getChildren().add(imgV);
                                    tokensBox.getProperties().put(tokenIndex, imgV);
                                    ind[0]++;
                                });
                                tokensBox.setMouseTransparent(true);
                                getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getChildren().add(tokensBox);
                                getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getProperties().put("tokens", tokensBox);

                            });
                        }


                    }
                }
            }
        });
        coordinatesSet.forEach(coordinate -> {
            getNodeFromGridPane(coordinate.x - 4, coordinate.y-5).getChildren().add(highlightsMap.get(coordinate));
        });
    }

    public void loadGrid() {
        if (getGameMode() == GameMode.LEVEL_1 || getGameMode() == GameMode.FLIGHT_MODE) {
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
                    StackPane highlight = new StackPane();
                    highlight.setStyle(styleForHighlight);
                    highlightsMap.put(new Point(col + 4, row + 5), highlight);
                    cellContainer.getProperties().put("coordinates", new Point(col + 4, row + 5));
                    cellContainer.setStyle("-fx-background-color: transparent;");
                    cellContainer.setOnMouseClicked(event -> {
                        clickTile((Point) cellContainer.getProperties().get("coordinates"));
                    });
                    shipboardGrid.add(cellContainer, col, row);
                }
            }
        } else if (getGameMode() == GameMode.LEVEL_2) {
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
                    StackPane highlight = new StackPane();
                    highlight.setStyle(styleForHighlight);
                    highlightsMap.put(new Point(col + 4, row + 5), highlight);
                    cellContainer.getProperties().put("coordinates", new Point(col + 4, row + 5));
                    cellContainer.setStyle("-fx-background-color: transparent;");
                    cellContainer.setOnMouseClicked(event -> {
                        clickTile((Point) cellContainer.getProperties().get("coordinates"));
                    });
                    shipboardGrid.add(cellContainer, col, row);
                }
            }
        }
        playerShipboard = Objects.requireNonNull(getPlayer()).getShipBoard();
        playerStatistics[0] = playerShipboard.getTotalFirePower();
        playerStatistics[1] = playerShipboard.getTotalEnginePower();
        playerStatistics[2] = playerShipboard.getTotalCrewNumber();
        playerStatistics[3] = 0;
        playerStatistics[4] = playerShipboard.getDestroyedTiles();
        shipboardGrid.setDisable(false);
        tokensArrayForGoodPosition.add(Token.RED_GOODS);
        tokensArrayForGoodPosition.add(Token.GREEN_GOODS);
        tokensArrayForGoodPosition.add(Token.BLUE_GOODS);
        tokensArrayForGoodPosition.add(Token.YELLOW_GOODS);
    }

    public void configureEffectPaneYesNo(InputActions inputCommand) {
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0);";
        Label labelString = new Label("Do you want to " + inputCommand.toString() + "?");
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
                        configureEffectPaneYesNo(inputCommand);
                    }
                });
                noButton.setOnAction(event -> {
                    try {
                        guiApplication.getController().userAcceptPrizes(guiApplication.getMyId(), false);
                    } catch (Exception e) {
                        guiApplication.printUpdateMessage(error + e.getMessage());
                        configureEffectPaneYesNo(inputCommand);
                    }
                });
            }
            case InputActions.REPAIR_SHIP, InputActions.LAND_ON_STATION -> {
                yesButton.setOnAction(event -> {
                    try {
                        guiApplication.getController().userLandOnStationOrShip(guiApplication.getMyId(), true);
                    } catch (Exception e) {
                        guiApplication.printUpdateMessage(error + e.getMessage());
                        configureEffectPaneYesNo(inputCommand);
                    }
                });
                noButton.setOnAction(event -> {
                    try {
                        guiApplication.getController().userLandOnStationOrShip(guiApplication.getMyId(), false);
                    } catch (Exception e) {
                        guiApplication.printUpdateMessage(error + e.getMessage());
                        configureEffectPaneYesNo(inputCommand);
                    }
                });
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
        addMessage(Objects.requireNonNull(getPlayer()).getUsername() + " will land at the end of the turn.");
        drawButton.setDisable(true);
        shipboardGrid.setDisable(true);
    }

    public void configureEffectPaneDices(int firstDice, int secondDice) {
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0);";
        Label labelString = new Label("The result of the dices is:");
        labelString.setStyle(style);
        String path = "/it/polimi/ingsw/is25am49/animations/dado_" + firstDice + ".png";
        Image firstDiceImage = new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
        path = "/it/polimi/ingsw/is25am49/animations/dado_" + secondDice + ".png";
        Image secondDiceImage = new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
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

        cardMessages.getChildren().clear();
        cardMessages.getChildren().add(container);
    }

    public void configureEffectPanePlanets() {
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0);";
        Label labelString = new Label("Where do you want to land?");
        labelString.setStyle(style);
        int numPlanets = 0;
        if (currentAdventureCard instanceof PlanetCard planetCard) {
            numPlanets = planetCard.getNumOfPlanets();
            ArrayList<Button> buttons = new ArrayList<>();
            HBox buttonsBox = new HBox(10);
            buttonsBox.setAlignment(Pos.CENTER);
            for (int i = 1; i <= numPlanets; i++) {
                Button planetButton = new Button("Planet " + planetCard.getPlanets().get(i-1).getPlanetId());
                planetButton.setStyle(style);
                int planetIndex = planetCard.getPlanets().get(i-1).getPlanetId();
                planetButton.setOnAction(event -> {
                    try {
                        guiApplication.getController().userLandOnPlanet(guiApplication.getMyId(), true, planetIndex);
                    } catch (Exception e) {
                        guiApplication.printUpdateMessage(error + e.getMessage());
                        configureEffectPanePlanets();
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
                    configureEffectPanePlanets();
                }
            });
            buttons.add(planetButton);
            buttonsBox.getChildren().add(planetButton);


            VBox container = new VBox(20, labelString, buttonsBox);
            container.setAlignment(Pos.CENTER);

            cardEffect.getChildren().clear();
            cardEffect.getChildren().add(container);
        }
    }

    public void configureEffectPaneFinishDecision(InputActions inputCommand) {
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0);";
        Label labelString = new Label("Click here to finish: ");
        labelString.setStyle(style);
        Button finishButton = new Button("Finish");
        finishButton.setStyle(style);
        switch (inputCommand) {
            case InputActions.ACTIVATE_COMPONENTS -> {
                finishButton.setOnAction(event -> {
                    try {
                        System.out.println("Coordinates set: " + coordinatesSet);
                        System.out.println("Token points list definitive: " + tokenPointsListDefenitive);
                        guiApplication.getController().userFinishDecisionActivating(guiApplication.getMyId(), coordinatesSet);
                        coordinatesSet.clear();
                        printShipboard();
                        configureEffectPaneFinishDecision(inputCommand);
                    } catch (Exception e) {
                        guiApplication.printUpdateMessage(error + e.getMessage());
                    }
                });
            }
            case InputActions.SELECT_TOKENS -> {
                finishButton.setOnAction(event -> {
                    try {
                        System.out.println("Coordinates set: " + coordinatesSet);
                        System.out.println("Token points list definitive: " + tokenPointsListDefenitive);
                        guiApplication.getController().userFinishDecisionToken(guiApplication.getMyId(), tokenPointsListDefenitive);
                        tokenPointsListDefenitive.clear();
                        printShipboard();
                        configureEffectPaneFinishDecision(inputCommand);
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
        Label labelString = new Label("Click here to throw the dices");
        labelString.setStyle(style);
        Button throwButton = new Button("Throw");
        throwButton.setStyle(style);
        throwButton.setOnAction(event -> {
            try {
                guiApplication.getController().userThrowDices(guiApplication.getMyId());
            } catch (Exception e) {
                guiApplication.printUpdateMessage(error + e.getMessage());
                configureEffectPaneThrowDices();
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

    public void configureEffectPaneGoods(int red, int green, int blue, int yellow) {
        goodsNumbers[0] = red;
        gdn[0] = red;
        goodsNumbers[1] = green;
        gdn[1] = green;
        goodsNumbers[2] = blue;
        gdn[2] = blue;
        goodsNumbers[3] = yellow;
        gdn[3] = yellow;
        configureEffectPaneShowNextGood();

    }

    public void configureEffectPaneShowNextGood() {
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0);";
        Label labelString = new Label("You are placing the good above");
        labelString.setStyle(style);
        Button placeButton = new Button("Place");
        placeButton.setStyle(style);
        Button noPlaceButton = new Button("Dont'");
        noPlaceButton.setStyle(style);

        int i;
        for (i = 0; i < gdn.length; i++) {
            if (gdn[i] > 0) {
                tokens.setDisable(false);
                tokens.setVisible(true);
                tokens.getChildren().clear();
                ImageView imView = new ImageView(goodsMap.get(i));
                tokens.getChildren().add(imView);
                gdn[i]--;
                int finalI = i;
                placeButton.setOnAction(event -> {
                    if(lastSelectedCoordinates!=null) {
                        tokenPointsListDefenitive.add(new Pair<>(lastSelectedCoordinates, tokensArrayForGoodPosition.get(finalI)));
                        configureEffectPaneShowNextGood();
                        List<Point> listCoord = new ArrayList<>();
                        listCoord.add(lastSelectedCoordinates);
                    }
                    else{
                        guiApplication.printUpdateMessage(error + "You must select a tile to place the good.");
                    }
                });
                noPlaceButton.setOnAction(event -> {
                    configureEffectPaneShowNextGood();
                });
                HBox buttonsBox = new HBox(10);
                buttonsBox.setAlignment(Pos.CENTER);
                buttonsBox.getChildren().add(noPlaceButton);
                buttonsBox.getChildren().add(placeButton);
                VBox container = new VBox(20, labelString, buttonsBox);
                container.setAlignment(Pos.CENTER);
                cardEffect.getChildren().clear();
                cardEffect.getChildren().add(container);
                return;
            }
        }
        try {
            guiApplication.getController().userFinishDecisionToken(guiApplication.getMyId(), tokenPointsListDefenitive);
            for (i = 0; i < gdn.length; i++) {
                gdn[i] = goodsNumbers[i];
            }
            configureEffectPaneShowNextGood();
        } catch (Exception e) {
            guiApplication.printUpdateMessage(error + "Hai sbagliato a posizionare i beni, riprova.");
        }

    }

    public void updateCardMessageBox(String messageText) {
        cardMessages.getChildren().clear();
        Label message = new Label();
        message.setText(messageText);
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0); -fx-padding: 10;";
        message.setStyle(style);
        message.setWrapText(true);
        message.setMaxWidth(400);
        message.setAlignment(Pos.TOP_LEFT);
        message.setTextAlignment(TextAlignment.LEFT);
        cardMessages.getChildren().add(message);
    }

    public void updatePlayerStats(int firePower, int motorPower, int crew, int credits, int destroyedTiles) {
        statsContainer.getChildren().clear();
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0); -fx-padding: 10;";
        Player player = getPlayer();
        Label playerStats = new Label();
        playerStats.setStyle(style);
        if (player != null) {
            StringBuilder stats = new StringBuilder("Player: " + player.getUsername() + ", Color: " + player.getColor() + "\n");
            stats.append("FirePower: ").append(Objects.requireNonNull(getPlayer()).getShipBoard().getTotalFirePower()).append("\n");
            stats.append("MotorPower: ").append(Objects.requireNonNull(getPlayer()).getShipBoard().getTotalEnginePower()).append("\n");
            stats.append("Crew: ").append(Objects.requireNonNull(getPlayer()).getShipBoard().getTotalCrewNumber()).append("\n");
            stats.append("Credits: ").append(playerStatistics[3] + credits).append("\n");
            stats.append("Destroyed tiles: ").append(playerStatistics[4] + destroyedTiles).append("\n");
            playerStats.setText(stats.toString());
        } else {
            playerStats.setText("No player data available.");
        }
        statsContainer.getChildren().add(playerStats);
    }
    public void updateCardState(AdventureStatesTypes state) {
        currentAdventureState = state;
        switch (currentAdventureState) {
            case DRAW, UPDATE -> {
                drawButton.setDisable(false);
                disableAllCellsShipboard();
                clearEffectPane();
            }
            case DECISION_BATTERIES, DECISION_ASTRONAUTS_OR_ALIENS -> {
                drawButton.setDisable(true);
                enableAllCellsShipboard();
                configureEffectPaneFinishDecision(InputActions.SELECT_TOKENS);
            }
            case DECISION_CANNON, DECISION_SHIELDS, DECISION_MOTOR -> {
                drawButton.setDisable(true);
                enableAllCellsShipboard();
                configureEffectPaneFinishDecision(InputActions.ACTIVATE_COMPONENTS);
            }
            //TODO
            case DECISION_GOODS -> {
                drawButton.setDisable(true);
                enableAllCellsShipboard();

            }
            case DECISION_LAND_PLANET -> {
                drawButton.setDisable(true);
                disableAllCellsShipboard();
                configureEffectPanePlanets();
            }
            case DECISION_REWARDS -> {
                drawButton.setDisable(true);
                disableAllCellsShipboard();
                configureEffectPaneYesNo(InputActions.ACCEPT_PRIZES);
            }
            case DECISION_LAND_STATION_OR_SHIP -> {
                drawButton.setDisable(true);
                disableAllCellsShipboard();
                if (currentAdventureCard instanceof AbandonedStationCard)
                    configureEffectPaneYesNo(InputActions.LAND_ON_STATION);
                if (currentAdventureCard instanceof AbandonedShipCard)
                    configureEffectPaneYesNo(InputActions.REPAIR_SHIP);
            }
            case ROLL_DICES -> {
                drawButton.setDisable(true);
                disableAllCellsShipboard();
                configureEffectPaneThrowDices();
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
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getChildren().remove(highlightsMap.get(coordinates));
                        lastSelectedCoordinates = null;
                    } else {
                        lastSelectedCoordinates = coordinates;
                        coordinatesSet.add(coordinates);
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getChildren().add(highlightsMap.get(coordinates));
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
                if (isTheComponentOfThisType(ComponentType.SHIELD, coordinates)) {
                    if (coordinatesSet.contains(coordinates)) {
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getChildren().remove(highlightsMap.get(coordinates));
                        coordinatesSet.remove(coordinates);
                        lastSelectedCoordinates = null;
                    } else {
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getChildren().add(highlightsMap.get(coordinates));
                        lastSelectedCoordinates = coordinates;
                        coordinatesSet.add(coordinates);
                    }
                } else {
                    guiApplication.printUpdateMessage(error + "You can only select Shields.");
                }

            }
            case AdventureStatesTypes.DECISION_GOODS -> {

                if (isTheComponentOfThisType(ComponentType.SPECIAL_STORAGE, coordinates) || isTheComponentOfThisType(ComponentType.STORAGE, coordinates)) {
                    if (lastSelectedCoordinates == coordinates) {
                        lastSelectedCoordinates = null;
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getChildren().remove(highlightPane);
                    } else {
                        lastSelectedCoordinates = coordinates;
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
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getChildren().remove(highlightsMap.get(coordinates));
                        coordinatesSet.remove(coordinates);
                    } else {
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getChildren().add(highlightsMap.get(coordinates));
                        lastSelectedCoordinates = coordinates;
                        coordinatesSet.add(coordinates);
                       
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

    public void drawLeaderBoardState(Canvas canvas, LeaderBoard leaderBoard, ImageView leaderboardImage) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Image img = leaderboardImage.getImage();
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
        tokens.getChildren().clear();
        tokens.setDisable(true);
        tokens.setVisible(false);
    }

    public void updateChangeTurn(boolean isMyTurn) {
        if (isMyTurn) {
            enableAllCellsShipboard();
            clearEffectPane();
        } else {
            disableAllCellsShipboard();

        }
    }

    public void reDrawLeaderBoard(LeaderBoard leaderBoard) {
        ImageView leaderboardImage = (ImageView) leaderboard.getProperties().get("leaderboard");
        Canvas leaderboardCanvas = (Canvas) leaderboard.getProperties().get("canvas");
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

    public void updateAttack(Point coord, Orientation or, boolean isMeteor) {
        getNodeFromGridPane(coord.x - 4, coord.y - 5).getChildren().add(damagePane);
        timerForAnimation(coord);
    }

    private void timerForAnimation(Point coord) {
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> animationFinishAttack(coord));
        pause.play();
    }

    private void animationFinishAttack(Point coord) {
        getNodeFromGridPane(coord.x - 4, coord.y - 5).getChildren().remove(damagePane);
        printShipboard();
    }

    private void enableAllCellsShipboard() {
        for (Node node : shipboardGrid.getChildren()) {
            if (node instanceof StackPane) {
                node.setDisable(false);
            }
        }
    }

    private void disableAllCellsShipboard() {
        for (Node node : shipboardGrid.getChildren()) {
            if (node instanceof StackPane) {
                node.setDisable(true);
            }
        }
    }

    public void addMessage(String text) {
        Label messageLabel = new Label(text);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-text-fill: white;");
        messageBox.getChildren().add(messageLabel);
        javafx.scene.control.Separator separator = new javafx.scene.control.Separator();
        separator.setStyle("-fx-background-color: #dddddd;");
        messageBox.getChildren().add(separator);
        scrollPane.layout();
        scrollPane.setVvalue(1.0);
    }

    public void enableAll() {
        shipboardGrid.setDisable(false);
        drawButton.setDisable(false);
        cardEffect.getChildren().clear();
        cardMessages.getChildren().clear();
        tokens.setDisable(false);
        tokens.setVisible(false);
        statsContainer.getChildren().clear();
    }

    public void disableAll() {
        shipboardGrid.setDisable(true);
        drawButton.setDisable(true);
        cardEffect.getChildren().clear();
        cardMessages.getChildren().clear();
        tokens.setDisable(true);
        tokens.setVisible(false);
        statsContainer.getChildren().clear();
    }
}
