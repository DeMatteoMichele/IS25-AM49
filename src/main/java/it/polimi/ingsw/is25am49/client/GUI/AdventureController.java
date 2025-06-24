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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.Pair;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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


    private volatile boolean timeIsDone;
    private final HashSet<Point> coordinatesSet;
    private final ArrayList<Pair<Point, String>> tokensPointsList;
    private final ArrayList<Pair<Point, Token>> tokenPointsListDefenitive;
    private ShipBoard playerShipboard;
    private AdventureCard currentAdventureCard;
    private int counterStampe;
    private AdventureStatesTypes currentAdventureState;
    private Point2D[] coordsLeaderboard;
    private ScheduledExecutorService scheduler;
    private final double[] playerStatistics;
    private final int[] goodsNumbers;
    private final Map<Integer, Image> goodsMap;

    public AdventureController() {
        coordinatesSet = new HashSet<>();
        tokensPointsList = new ArrayList<>();
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
        goodsMap.put(0, new Image(Objects.requireNonNull(getClass().getResource(path + "Redgood.png")).toExternalForm()));
        goodsMap.put(1, new Image(Objects.requireNonNull(getClass().getResource(path + "Greengood.png")).toExternalForm()));
        goodsMap.put(2, new Image(Objects.requireNonNull(getClass().getResource(path + "Bluegood.png")).toExternalForm()));
        goodsMap.put(3, new Image(Objects.requireNonNull(getClass().getResource(path + "Yellowgood.png")).toExternalForm()));
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
        highlightPane.setStyle("-fx-border-color: yellow; -fx-border-width: 4; -fx-border-style: solid; -fx-background-color: rgba(255,255,0,0.3);");
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

    public void printShipboard() {
        playerShipboard = Objects.requireNonNull(getPlayer()).getShipBoard();
        playerShipboard.getGrid().forEach((coordinates, tile) -> {
            if (tile.isPresent()) {
                int idTile = tile.get().getIdTile();
                ImageView imageView = tilesMap.get(idTile);
                if (imageView != null) {
                    imageView.setFitWidth(60);
                    imageView.setFitHeight(60);
                    imageView.setUserData(idTile);
                    imageView.setOnMouseClicked(event -> {
                        clickTile(coordinates);
                    });
                    switch (tile.get().getTileOrientation()) {
                        case NORTH -> imageView.setRotate(0);
                        case EAST -> imageView.setRotate(90);
                        case SOUTH -> imageView.setRotate(180);
                        case WEST -> imageView.setRotate(270);
                    }
                    Image i = new Image(Objects.requireNonNull(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Graysquare.png")).toExternalForm());
                    shipboardGrid.add(imageView, coordinates.x - 4, coordinates.y - 5);
                    shipboard.getProperties().put(coordinates, imageView);
                    if (tile.get().getComponent() instanceof ContainerComponent) {
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
                                    tokensBox.setOnMouseClicked(event -> {clickTile(coordinates);});
                                    shipboardGrid.add(tokensBox, coordinates.x - 4, coordinates.y - 5);
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
                                    tokensBox.setOnMouseClicked(event -> {clickTile(coordinates);});
                                    shipboardGrid.add(tokensBox, coordinates.x - 4, coordinates.y - 5);

                                });
                            }
                            default -> {
                                guiApplication.printUpdateMessage(error + "Unknown tile orientation: " + tile.get().getTileOrientation());
                            }
                        }


                    }
                }
            }
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
                    cellContainer.getProperties().put("coordinates", new Point(col + 4, row + 5));
                    cellContainer.setStyle("-fx-background-color: transparent;");
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
                    cellContainer.getProperties().put("coordinates", new Point(col + 4, row + 5));
                    cellContainer.setStyle("-fx-background-color: transparent;");
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
                    guiApplication.getController().userAcceptPrizes(guiApplication.getMyId(), true);
                });
                noButton.setOnAction(event -> {
                    guiApplication.getController().userAcceptPrizes(guiApplication.getMyId(), false);
                });
            }
            case InputActions.REPAIR_SHIP, InputActions.LAND_ON_STATION -> {
                yesButton.setOnAction(event -> {
                    guiApplication.getController().userLandOnStationOrShip(guiApplication.getMyId(), true);
                });
                noButton.setOnAction(event -> {
                    guiApplication.getController().userLandOnStationOrShip(guiApplication.getMyId(), false);
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
        updateCardMessageBox("Your flight has ended.");
        coordinatesSet.clear();
        tokensPointsList.clear();
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

        cardEffect.getChildren().clear();
        cardEffect.getChildren().add(container);
    }

    public void configureEffectPanePlanets() {
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0);";
        Label labelString = new Label("Where do you want to land?");
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
                guiApplication.getController().userLandOnPlanet(guiApplication.getMyId(), true, planetIndex);
            });
            buttons.add(planetButton);
            buttonsBox.getChildren().add(planetButton);
        }
        Button planetButton = new Button("Don't land");
        planetButton.setStyle(style);
        planetButton.setOnAction(event -> {
            guiApplication.getController().userLandOnPlanet(guiApplication.getMyId(), false, -1);
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
        Label labelString = new Label("Click here to finish: ");
        labelString.setStyle(style);
        Button finishButton = new Button("Finish");
        finishButton.setStyle(style);
        switch (inputCommand) {
            case InputActions.ACTIVATE_COMPONENTS -> {
                finishButton.setOnAction(event -> {
                    guiApplication.getController().userFinishDecisionActivating(guiApplication.getMyId(), coordinatesSet);
                });
            }
            case InputActions.SELECT_TOKENS -> {
                tokenPointsListDefenitive.clear();
                convertTokenList(tokensPointsList);
                finishButton.setOnAction(event -> {
                    guiApplication.getController().userFinishDecisionToken(guiApplication.getMyId(), tokenPointsListDefenitive);
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

    private void convertTokenList(ArrayList<Pair<Point, String>> tokensPointsList) {
        tokensPointsList.forEach(pair -> {
            Point coordinates = pair.getKey();
            String tokenType = pair.getValue();
            Token token = convertName(tokenType);
            if (tokensMap.containsKey(token)) {
                Pair<Point, Token> pairDefinitive = new Pair<>(coordinates, token);
                tokenPointsListDefenitive.add(pairDefinitive);
            } else {
                guiApplication.printUpdateMessage(error + "Token not found: " + tokenType);
            }
        });
    }

    public void configureEffectPaneThrowDices() {
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0);";
        Label labelString = new Label("Click here to throw the dices");
        labelString.setStyle(style);
        Button throwButton = new Button("Throw");
        throwButton.setStyle(style);
        throwButton.setOnAction(event -> {
            guiApplication.getController().userThrowDices(guiApplication.getMyId());
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
        Label message = new Label();
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
        Label labelString = new Label("You are placing the good above");
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
//                placeButton.setOnAction(event -> {
//                    if (isPresentInTokenList(coordinates, Token.ASTRONAUT) || isPresentInTokenList(coordinates, Token.PURPLE_ALIENS) || isPresentInTokenList(coordinates, Token.BROWN_ALIENS)) {
//                        tokensPointsList.stream().filter((pair) -> pair.getKey().equals(coordinates) && pair.getValue().equals(imgV.getUserData().toString() + key)).findFirst().ifPresent(tokensPointsList::remove);
//                        imageView.setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
//                    } else {
//                        tokensPointsList.add(new Pair<>(coordinates, imgV.getUserData().toString() + key));
//                        imageView.setStyle("-fx-border-color: yellow; -fx-border-width: 3; -fx-border-radius: 5;");
//                    }
//                });
                VBox container = new VBox(20, labelString, placeButton);
                container.setAlignment(Pos.CENTER);
                cardEffect.getChildren().clear();
                cardEffect.getChildren().add(container);
                return;
            }
        }
        guiApplication.getController().userFinishDecisionToken(guiApplication.getMyId(), tokenPointsListDefenitive);
        tokens.setDisable(true);
        tokens.setVisible(false);
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
                disableAllCellsShipboard();
                tokens.setDisable(true);
                tokens.setVisible(false);
                clearEffectPane();
            }
            case DECISION_BATTERIES, DECISION_ASTRONAUTS_OR_ALIENS -> {
                drawButton.setDisable(true);
                enableAllCellsShipboard();
                tokens.setDisable(false);
                tokens.setVisible(true);
                configureEffectPaneFinishDecision(InputActions.SELECT_TOKENS);
            }
            case DECISION_CANNON, DECISION_SHIELDS, DECISION_MOTOR -> {
                drawButton.setDisable(true);
                enableAllCellsShipboard();
                tokens.setDisable(false);
                tokens.setVisible(true);
                configureEffectPaneFinishDecision(InputActions.ACTIVATE_COMPONENTS);
            }
            //TODO
            case DECISION_GOODS -> {
                drawButton.setDisable(true);
                enableAllCellsShipboard();
                tokens.setDisable(false);
                tokens.setVisible(true);
                tokens.getChildren().clear();

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
            case UPDATE -> {
                drawButton.setDisable(false);
                disableAllCellsShipboard();
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
                    tokens.setDisable(false);
                    tokens.setVisible(true);
                    tokens.getChildren().clear();
                    tokens.getChildren().add(setUpTokensBox(coordinates, Token.BATTERY_CELLS));
                } else {
                    guiApplication.printUpdateMessage(error + "You can only select Batteries.");
                }
            }
            case AdventureStatesTypes.DECISION_CANNON -> {
                if (isTheComponentOfThisType(ComponentType.DOUBLE_CANNON, coordinates)) {
                    if (coordinatesSet.contains(coordinates)) {
                        coordinatesSet.remove(coordinates);
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5);
                    } else {
                        coordinatesSet.add(coordinates);
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).setStyle("-fx-border-color: yellow; -fx-border-width: 4; -fx-border-style: solid; -fx-background-color: rgba(255,255,0,0.3);");
                    }
                } else {
                    guiApplication.printUpdateMessage(error + "You can only select Cannons.");
                }
            }
            case AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS -> {
                if (isTheComponentOfThisType(ComponentType.CABINE, coordinates)) {

                    tokens.setDisable(false);
                    tokens.setVisible(true);
                    tokens.getChildren().clear();
                    tokens.getChildren().add(setUpTokensBox(coordinates, Token.ASTRONAUT));
                } else {
                    guiApplication.printUpdateMessage(error + "You can only select Cabines.");
                }
            }
            case AdventureStatesTypes.DECISION_SHIELDS -> {

                if (isTheComponentOfThisType(ComponentType.SHIELD, coordinates)) {
                    if (coordinatesSet.contains(coordinates)) {

                        coordinatesSet.remove(coordinates);
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
                    } else {
                        coordinatesSet.add(coordinates);
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).setStyle("-fx-border-color: yellow; -fx-border-width: 4; -fx-border-style: solid; -fx-background-color: rgba(255,255,0,0.3);");
                    }
                } else {
                    guiApplication.printUpdateMessage(error + "You can only select Shields.");
                }
            }
            case AdventureStatesTypes.DECISION_GOODS -> {

                if (isTheComponentOfThisType(ComponentType.SPECIAL_STORAGE, coordinates) || isTheComponentOfThisType(ComponentType.STORAGE, coordinates)) {
                    if (coordinatesSet.contains(coordinates)) {

                        coordinatesSet.remove(coordinates);
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
                    } else {
                        coordinatesSet.add(coordinates);
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).setStyle("-fx-border-color: yellow; -fx-border-width: 4; -fx-border-style: solid; -fx-background-color: rgba(255,255,0,0.3);");
                    }
                } else {
                    guiApplication.printUpdateMessage(error + "You can only select Storage.");
                }
            }
            case AdventureStatesTypes.DECISION_MOTOR -> {

                if (isTheComponentOfThisType(ComponentType.DOUBLE_ENGINE, coordinates)) {

                    if (coordinatesSet.contains(coordinates)) {
                        coordinatesSet.remove(coordinates);
                        highlightPane.getChildren().clear();
                    } else {
                        coordinatesSet.add(coordinates);
                        shipboardGrid.add(highlightPane, coordinates.x - 4, coordinates.y - 5);
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

    public Node getNodeFromGridPane(int col, int row) {
        for (Node node : shipboardGrid.getChildren()) {
            Integer column = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            if ((column == null ? 0 : column) == col &&
                    (rowIndex == null ? 0 : rowIndex) == row) {
                return node;
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

        double originalWidth = 0;
        double originalHeight = 0;
        if (getGameMode().equals(GameMode.LEVEL_1) || getGameMode().equals(GameMode.FLIGHT_MODE)) {
            originalWidth = 985;
            originalHeight = 546;
        } else if (getGameMode().equals(GameMode.LEVEL_2)) {
            originalWidth = 1055;
            originalHeight = 639;
        }
        double scaleX = imgWidth / originalWidth;
        double scaleY = imgHeight / originalHeight;

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

    private boolean isPresentInTokenList(Point coordinates, Token token, int key) {
        return tokensPointsList.stream().anyMatch(pair -> pair.getKey().equals(coordinates) && pair.getValue().equals(token.toString() + key));
    }

//    private HBox setUpTokensBox(Point coordinates, Token tok) {
//        HBox temporaryToken = (HBox) getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getProperties().get("tokens");
//        HBox newHBox = new HBox(1);
//        newHBox.setAlignment(Pos.CENTER);
//
//        temporaryToken.getProperties().forEach((key, value) -> {
//            if (value instanceof ImageView imgV) {
//                Image im = imgV.getImage();
//                ImageView imageView = new ImageView(im);
//
//                switch (tok) {
//                    case ASTRONAUT -> imageView.setOnMouseClicked(event -> {
//                        if (isPresentInTokenList(coordinates, Token.ASTRONAUT, (int)key) || isPresentInTokenList(coordinates, Token.PURPLE_ALIENS, (int) key) || isPresentInTokenList(coordinates, Token.BROWN_ALIENS, (int) key)) {
//                            tokensPointsList.stream().filter((pair) -> pair.getKey().equals(coordinates) && pair.getValue().equals(imgV.getUserData().toString() + key)).findFirst().ifPresent(tokensPointsList::remove);
//                            System.out.println("Removing token: " + imgV.getUserData().toString() + key);
//                            imageView.setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
//                        } else {
//                            tokensPointsList.add(new Pair<>(coordinates, imgV.getUserData().toString() + key));
//                            System.out.println("Adding token: " + imgV.getUserData().toString() + key);
//                            imageView.setStyle("-fx-border-color: yellow; -fx-border-width: 10; -fx-border-radius: 10;");
//                        }
//                    });
//                    case BATTERY_CELLS -> imageView.setOnMouseClicked(event -> {
//                        if (isPresentInTokenList(coordinates, Token.BATTERY_CELLS, (int) key)) {
//                            tokensPointsList.stream().filter((pair) -> pair.getKey().equals(coordinates) && pair.getValue().equals(imgV.getUserData().toString() + key)).findFirst().ifPresent(tokensPointsList::remove);
//                            imageView.setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
//                        } else {
//                            tokensPointsList.add(new Pair<>(coordinates, imgV.getUserData().toString() + key));
//                            imageView.setStyle("-fx-border-color: yellow; -fx-border-width: 3; -fx-border-radius: 5;");
//                        }
//                    });
//                }
//                newHBox.getChildren().add(imageView);
//            }
//        });
//        return newHBox;
//    }
    private Pane setUpTokensBox(Point coordinates, Token tok) {
        Node containerNode = (Node) getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).getProperties().get("tokens");
        Pane temporaryToken;
        if (containerNode instanceof HBox) {
            temporaryToken = (HBox) containerNode;
        } else if (containerNode instanceof VBox) {
            temporaryToken = (VBox) containerNode;
        } else {
            temporaryToken = new HBox(1);
            ((HBox) temporaryToken).setAlignment(Pos.CENTER);
        }
        // Crea un nuovo contenitore dello stesso tipo
        Pane newContainer;
        if (temporaryToken instanceof HBox) {
            newContainer = new HBox(1);
            ((HBox) temporaryToken).setAlignment(Pos.CENTER);
        } else {
            newContainer = new VBox(1);
            ((VBox) temporaryToken).setAlignment(Pos.CENTER);
        }
        // Itera sulle proprietà del contenitore per ricostruire le ImageView con eventi di selezione multipla
        temporaryToken.getProperties().forEach((key, value) -> {
            if (value instanceof ImageView imgV) {
                Image im = imgV.getImage();
                ImageView imageView = new ImageView(im);
                imageView.setUserData(imgV.getUserData());
                // Gestione del click per il toggle della selezione sul token
                imageView.setOnMouseClicked(event -> {
                    String tokenKey = imageView.getUserData().toString() + key;
                    if (isPresentInTokenList(coordinates, tok, (int) key)) {
                        tokensPointsList.removeIf(pair ->
                                pair.getKey().equals(coordinates) && pair.getValue().equals(tokenKey)
                        );
                        imageView.setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
                    } else {
                        tokensPointsList.add(new Pair<>(coordinates, tokenKey));
                        imageView.setStyle("-fx-border-color: yellow; -fx-border-width: 3; -fx-border-radius: 5;");
                    }
                });
                newContainer.getChildren().add(imageView);
            }
        });
        return newContainer;
    }

    private Token convertName(String name) {
        if (name.contains("BROWN_ALIENS")) return Token.BROWN_ALIENS;
        else if (name.contains("PURPLE_ALIEN")) return Token.PURPLE_ALIENS;
        else if (name.contains("YELLOW_GOODS")) return Token.YELLOW_GOODS;
        else if (name.contains("RED_GOODS")) return Token.RED_GOODS;
        else if (name.contains("BLUE_GOODS")) return Token.BLUE_GOODS;
        else if (name.contains("GREEN_GOODS")) return Token.GREEN_GOODS;
        else if (name.contains("BATTERY_CELLS")) return Token.BATTERY_CELLS;
        else if (name.contains("ASTRONAUT")) return Token.ASTRONAUT;
        else return null;
    }

    public void clearEffectPane() {
        cardEffect.getChildren().clear();
    }

    public void updateChangeTurn() {
        drawButton.setDisable(true);
        enableAllCellsShipboard();
        clearEffectPane();
    }

    public void deActivatePoints(List<Point> coordinates) {
        coordinates.forEach(coordinate -> {
            Node node = getNodeFromGridPane(coordinate.x - 4, coordinate.y - 5);
            if (node != null) {
                node.setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
            }
        });
        coordinates.forEach(coordinatesSet::remove);
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
    public void updateAttack(Point coord, Orientation or, boolean isMeteor){
        System.out.println("Updating attack animation at " + coord + " with orientation " + or + " and isMeteor: " + isMeteor);
        ImageView imView;
        String path = "/it/polimi/ingsw/is25am49/animations/";
        if(isMeteor) {
            imView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource(path +"cannonade_BIG.jpg")).toExternalForm()));
        }
        else{
            imView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource(path +"meteor_BIG.jpg")).toExternalForm()));
        }
        switch(or) {
            case NORTH -> {
                imView.setRotate(0);
                imView.setFitHeight(70);
                imView.setFitWidth(50);
                imView.setLayoutX(getNodeFromGridPane(coord.x  - 4, coord.y - 5).getLayoutX());
                imView.setLayoutY(shipboard.getLayoutY() + shipboard.getHeight() + 10);
            }
            case SOUTH -> {
                imView.setRotate(180);
                imView.setFitHeight(70);
                imView.setFitWidth(50);
                imView.setLayoutX(getNodeFromGridPane(coord.x  - 4, coord.y - 5).getLayoutX());
                imView.setLayoutY(shipboard.getLayoutY() - 10);
            }
            case EAST -> {
                imView.setRotate(90);
                imView.setFitHeight(50);
                imView.setFitWidth(70);
                imView.setLayoutY(getNodeFromGridPane(coord.x - 4, coord.y - 5).getLayoutY());
                imView.setLayoutX(shipboard.getLayoutX() + shipboard.getWidth() + 10);
            }
            case WEST -> {
                imView.setRotate(270);
                imView.setFitHeight(50);
                imView.setFitWidth(70);
                imView.setLayoutY(getNodeFromGridPane(coord.x  - 4, coord.y - 5).getLayoutY());
                imView.setLayoutX(shipboard.getLayoutX() - 10);
            }
        }
        System.out.println("ImageView position: " + imView.getLayoutX() + ", " + imView.getLayoutY());
        imView.setVisible(true);
        adventureContainer.getChildren().add(imView);
        shipboardGrid.getChildren().add(highlightPane);
        timerForAnimation(imView, coord);
    }
    private void timerForAnimation(ImageView imv, Point coord) {
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> animationFinishAttack(imv, coord));
        pause.play();
    }
    private void animationFinishAttack(ImageView imv, Point coord) {
        System.out.println("Finishing attack animation at " + coord);
        highlightPane.getChildren().clear();
        imv.setVisible(false);
        imv.setManaged(false);
        adventureContainer.getChildren().remove(imv);
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
//    private void removeCellFromShipboard(Point coordinates) {
//        Node node = getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5);
//        if (node != null) {
//            shipboardGrid.getChildren().remove(node);
//        } else {
//            guiApplication.printUpdateMessage(error + "No tile found at " + coordinates);
//        }
//    }
}
