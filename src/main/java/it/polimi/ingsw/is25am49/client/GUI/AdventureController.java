package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.client.model.Game;
import it.polimi.ingsw.is25am49.client.model.LeaderBoard;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.client.model.adventurecards.PlanetCard;
import it.polimi.ingsw.is25am49.client.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.shared.enums.*;
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
import javafx.util.Pair;

import java.awt.Point;
import java.util.*;

public class AdventureController extends GenericSceneController {
    @FXML
    public Button stopButton;
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

    private final HashSet<Point> coordinatesSet;
    private final ArrayList<Pair<Point, Token>> tokensPointsList;
    private ShipBoard playerShipboard;
    private AdventureCard currentAdventureCard;
    private int counterStampe;
    private AdventureStatesTypes currentAdventureState;
    private int cosmicCredits;
    private Point2D[] coordsLeaderboard;

    public AdventureController() {
        coordinatesSet = new HashSet<>();
        tokensPointsList = new ArrayList<>();
        playerShipboard = null;
        currentAdventureCard = null;
        counterStampe = 0;
        currentAdventureState = AdventureStatesTypes.DRAW;
        cosmicCredits = 0;
    }


    public void initialize() {
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
        } catch (Exception e) {
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }

    public void handleStopButton() {
        try {
            //guiApplication.getController().userQuitGame(guiApplication.getMyId());
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
        updateCardMessageBox("TestoProva");
        configureEffectPaneDices(2, 4);
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
        if (isThisPlayerLeader())
            updateViewForSpecificCard();
    }

    public void printShipboard() {
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
                    if (tile.get().getComponent().getType().equals(ComponentType.BATTERY)) {

                    }
                    if (tile.get().getComponent() instanceof ContainerComponent) {
                        switch (tile.get().getTileOrientation()) {
                            case NORTH, SOUTH -> {
                                HBox tokensBox = new HBox(1);
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
                                tile.ifPresent(t -> {
                                    t.getComponent().getTokenList().forEach(token -> {
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
                                        tokensBox.getChildren().add(imgV);
                                    });

                                    shipboardGrid.add(tokensBox, coordinates.x - 4, coordinates.y - 5);

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
                                tile.ifPresent(t -> {
                                    t.getComponent().getTokenList().forEach(token -> {
                                        ImageView imgV = new ImageView(tokensMap.get(token).getImage());
                                        if (token.equals(Token.BATTERY_CELLS)) {
                                            imgV.setFitWidth(10);
                                            imgV.setFitHeight(30);
                                            imgV.setPreserveRatio(false);
                                            switch (tile.get().getTileOrientation()) {
                                                case EAST -> imgV.setRotate(90);
                                                case WEST -> imgV.setRotate(270);
                                            }
                                            Group rotatedGroup = new Group(imgV);
                                            tokensBox.getChildren().add(rotatedGroup);
                                        } else if (token.equals(Token.ASTRONAUT) || token.equals(Token.PURPLE_ALIENS) || token.equals(Token.BROWN_ALIENS)) {
                                            imgV.setFitWidth(20);
                                            imgV.setFitHeight(30);
                                            tokensBox.getChildren().add(imgV);
                                        } else {
                                            imgV.setFitWidth(30);
                                            imgV.setFitHeight(30);
                                            tokensBox.getChildren().add(imgV);
                                        }
                                    });

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
    }

    public void configureEffectPaneYesNo(InputActions inputCommand) {
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0);";
        Label labelString = new Label("Do you want to " + inputCommand + "?");
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

    public void startTurn() {

    }

    public void stopTurn() {
        cardEffect.getChildren().clear();
        updateCardMessageBox("Your turn has ended.");
        coordinatesSet.clear();
        tokensPointsList.clear();
        drawButton.setDisable(true);
        shipboardGrid.setDisable(true);
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
                finishButton.setOnAction(event -> {
                    guiApplication.getController().userFinishDecisionToken(guiApplication.getMyId(), tokensPointsList);
                });
            }
        }
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);

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
            guiApplication.getController().userThrowDices(guiApplication.getMyId());
        });

        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox container = new VBox(20, labelString, buttonsBox);
        container.setAlignment(Pos.CENTER);

        cardEffect.getChildren().clear();
        cardEffect.getChildren().add(container);
    }

    public void configureEffectPaneShowNextGood() {
        String style = "-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0);";
        Label labelString = new Label("Click here to throw the dices");
        labelString.setStyle(style);

        ImageView currentGood = new ImageView();

        VBox container = new VBox(20, labelString, currentGood);
        container.setAlignment(Pos.CENTER);

        cardEffect.getChildren().clear();
        cardEffect.getChildren().add(container);
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
            stats.append("FirePower: ").append(playerShipboard.getTotalFirePower() + firePower).append("\n");
            stats.append("MotorPower: ").append(playerShipboard.getTotalEnginePower() + motorPower).append("\n");
            stats.append("Crew: ").append(playerShipboard.getTotalCrewNumber() + crew).append("\n");
            stats.append("Credits: ").append(cosmicCredits + credits).append("\n");
            stats.append("Destroyed tiles: ").append(playerShipboard.getDestroyedTiles() + destroyedTiles).append("\n");
            playerStats.setText(stats.toString());
        } else {
            playerStats.setText("No player data available.");
        }
        statsContainer.getChildren().add(playerStats);
    }

    public void updateShipboardDestroyedTiles(Set<Point> destroyedTiles) {
        destroyedTiles.forEach(coordinates -> playerShipboard.destroyTile(coordinates));
        printShipboard();
    }

    private void updateViewForSpecificCard() {
        switch (currentAdventureCard.getCardId()) {
            case "SpazioAperto" -> {

            }
            case "PlanetCard" -> {

            }
            case "SmugglerCard" -> {

            }
            case "MeteorRainCard" -> {

            }
            case "AbandonedShipCard" -> {

            }
            case "StarDustCard" -> {

            }
            case "AbandonedStationCard" -> {

            }
            case "WarZoneCard" -> {

            }
            case "PiratesCard" -> {

            }
            case "SlaverCard" -> {

            }
            case "EpidemicCard" -> {

            }
        }
    }

    private void clickTile(Point coordinates) {
        switch (currentAdventureState) {
            case AdventureStatesTypes.DECISION_BATTERIES -> {
                if (isTheComponentOfThisType(ComponentType.BATTERY, coordinates)) {

                } else {
                    guiApplication.printUpdateMessage(error + "You can only select batteries.");
                }
            }
            case AdventureStatesTypes.DECISION_CANNON -> {
                if (isTheComponentOfThisType(ComponentType.DOUBLE_CANNON, coordinates)) {
                    if (coordinatesSet.contains(coordinates)) {
                        coordinatesSet.remove(coordinates);
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
                    } else {
                        coordinatesSet.add(coordinates);
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).setStyle("-fx-border-color: white; -fx-border-width: 3; -fx-border-radius: 5;");
                    }
                } else {
                    guiApplication.printUpdateMessage(error + "You can only select batteries.");
                }
            }
            case AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS -> {

            }
            case AdventureStatesTypes.DECISION_SHIELDS -> {
                if (isTheComponentOfThisType(ComponentType.SHIELD, coordinates)) {
                    if (coordinatesSet.contains(coordinates)) {
                        coordinatesSet.remove(coordinates);
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
                    } else {
                        coordinatesSet.add(coordinates);
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).setStyle("-fx-border-color: white; -fx-border-width: 3; -fx-border-radius: 5;");
                    }
                } else {
                    guiApplication.printUpdateMessage(error + "You can only select batteries.");
                }
            }
            case AdventureStatesTypes.DECISION_GOODS -> {

            }
            case AdventureStatesTypes.DECISION_MOTOR -> {
                if (isTheComponentOfThisType(ComponentType.DOUBLE_ENGINE, coordinates)) {
                    if (coordinatesSet.contains(coordinates)) {
                        coordinatesSet.remove(coordinates);
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
                    } else {
                        coordinatesSet.add(coordinates);
                        getNodeFromGridPane(coordinates.x - 4, coordinates.y - 5).setStyle("-fx-border-color: white; -fx-border-width: 3; -fx-border-radius: 5;");
                    }
                } else {
                    guiApplication.printUpdateMessage(error + "You can only select batteries.");
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
        System.out.println("Debug" + counterStampe++);
    }

    private boolean isTheComponentOfThisType(ComponentType type, Point coordinates) {
        return playerShipboard.getTile(coordinates).getComponent().getType().equals(type);
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
}
