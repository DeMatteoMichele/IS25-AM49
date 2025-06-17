package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.client.model.Game;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.client.model.adventurecards.PlanetCard;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class AdventureController extends GenericSceneController {
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
    private ImageView leaderboardImage;

    private final HashSet<Point> coordinatesSet = new HashSet<>();
    private final ArrayList<Pair<Point, Token>> tokensPointsList = new ArrayList<>();
    private ShipBoard playerShipboard;
    private AdventureCard currentAdventureCard;
    private int counterStampe = 0;

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
//            System.out.println("Bottone schiacciato");
        } catch (Exception e) {
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }

    public void init() {
        loadTilesMap();
        loadGrid();
        printShipboard();
        loadAdventureCardsAndButton();
        loadLeaderBoard();
        configureEffectPaneYesNo("accept prizes");
        configureEffectPanePlanets();
        configureEffectPaneDices(2, 4);
    }

    private void loadLeaderBoard() {
        if (getGameMode() == GameMode.FLIGHT_MODE || getGameMode() == GameMode.LEVEL_1) {
            String path = "/it/polimi/ingsw/is25am49/cardboard/cardboard-3.png";
            Image image = new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
            leaderboardImage.setImage(image);
        } else if (getGameMode() == GameMode.LEVEL_2) {
            String path = "/it/polimi/ingsw/is25am49/cardboard/cardboard-5.png";
            Image image = new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
            leaderboardImage.setImage(image);
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
        updateViewForSpecificCard();
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
                        clickTile();
                    });
                    switch (tile.get().getTileOrientation()) {
                        case NORTH -> imageView.setRotate(0);
                        case EAST -> imageView.setRotate(90);
                        case SOUTH -> imageView.setRotate(180);
                        case WEST -> imageView.setRotate(270);
                    }


                    shipboardGrid.add(imageView, coordinates.x - 4, coordinates.y - 5);
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
                    cellContainer.setStyle("-fx-background-color: transparent;"); // Contenitore trasparente
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
                    cellContainer.setStyle("-fx-background-color: transparent;"); // Contenitore trasparente
                    shipboardGrid.add(cellContainer, col, row);
                }
            }
        }
    }
    public void configureEffectPaneYesNo(String inputCommand) {
        String style="-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0);";
        Label labelString = new Label("Do you want to " + inputCommand + "?");
        labelString.setStyle(style);
        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");
        yesButton.setStyle(style);
        noButton.setStyle(style);
        switch(inputCommand){
            case "accept the prizes" -> {
                yesButton.setOnAction(event -> {
                    guiApplication.getController().userAcceptPrizes(guiApplication.getMyId(), true);
                    cardEffect.getChildren().clear();
                });
                noButton.setOnAction(event -> {
                    guiApplication.getController().userAcceptPrizes(guiApplication.getMyId(), false);
                    cardEffect.getChildren().clear();
                });
            }
            case "repair the ship", "land on the station" -> {
                yesButton.setOnAction(event -> {
                    guiApplication.getController().userLandOnStationOrShip(guiApplication.getMyId(), true);
                    cardEffect.getChildren().clear();
                });
                noButton.setOnAction(event -> {
                    guiApplication.getController().userLandOnStationOrShip(guiApplication.getMyId(), false);
                    cardEffect.getChildren().clear();
                });
            }
        }

        HBox bottoniBox = new HBox(10, yesButton, noButton);
        bottoniBox.setAlignment(Pos.CENTER);

        VBox contenitore = new VBox(20, labelString, bottoniBox);
        contenitore.setAlignment(Pos.CENTER);

        cardEffect.getChildren().clear();
        cardEffect.getChildren().add(contenitore);
    }

    public void configureEffectPaneDices(int firstDice, int secondDice) {
        String style="-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0);";
        Label labelString = new Label("The result of the dices is:");
        labelString.setStyle(style);
        String path = "/it/polimi/ingsw/is25am49/animations/dado_" +  firstDice + ".png";
        Image firstDiceImage = new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
        path = "/it/polimi/ingsw/is25am49/animations/dado_" +  secondDice + ".png";
        Image secondDiceImage = new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
        ImageView firstDiceView = new ImageView(firstDiceImage);
        ImageView secondDiceView = new ImageView(secondDiceImage);
        firstDiceView.setFitHeight(75);
        firstDiceView.setFitWidth(75);
        secondDiceView.setFitHeight(75);
        secondDiceView.setFitWidth(75);
        HBox bottoniBox = new HBox(10, firstDiceView, secondDiceView);
        bottoniBox.setAlignment(Pos.CENTER);

        VBox contenitore = new VBox(20, labelString, bottoniBox);
        contenitore.setAlignment(Pos.CENTER);

        cardEffect.getChildren().clear();
        cardEffect.getChildren().add(contenitore);
    }

    public void configureEffectPanePlanets() {
        String style="-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0);";
        Label labelString = new Label("Where do you want to land?");
        labelString.setStyle(style);
        int numPlanets = 0;
        if(currentAdventureCard instanceof PlanetCard planetCard){
            numPlanets = planetCard.getNumOfPlanets();
        }
        ArrayList<Button> buttons = new ArrayList<>();
        HBox bottoniBox = new HBox(10);
        bottoniBox.setAlignment(Pos.CENTER);
        for (int i = 1; i <= numPlanets; i++) {
            Button planetButton = new Button("Planet " + i);
            planetButton.setStyle(style);
            int planetIndex = i;
            planetButton.setOnAction(event -> {
                guiApplication.getController().userLandOnPlanet(guiApplication.getMyId(), true, planetIndex);
                cardEffect.getChildren().clear();
            });
            buttons.add(planetButton);
            bottoniBox.getChildren().add(planetButton);
        }
        Button planetButton = new Button("Don't land");
        planetButton.setStyle(style);
        planetButton.setOnAction(event -> {
            guiApplication.getController().userLandOnPlanet(guiApplication.getMyId(), false, -1 );
            cardEffect.getChildren().clear();
        });
        buttons.add(planetButton);
        bottoniBox.getChildren().add(planetButton);


        VBox contenitore = new VBox(20, labelString, bottoniBox);
        contenitore.setAlignment(Pos.CENTER);

        cardEffect.getChildren().clear();
        cardEffect.getChildren().add(contenitore);
    }
    public void configureEffectPaneFinishDecision(String inputCommand) {
        String style="-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.6), 10, 0.5, 0, 0);";
        Label labelString = new Label("Click here to finish ");
        labelString.setStyle(style);
        Button finishButton = new Button("Finish");
        finishButton.setStyle(style);
        switch(inputCommand){
            case "Activation" ->{
                finishButton.setOnAction(event -> {
                    guiApplication.getController().userFinishDecisionActivating(guiApplication.getMyId(), coordinatesSet);
                    cardEffect.getChildren().clear();
                    coordinatesSet.clear();
                });
            }
            case "Selecting Tokens" -> {
                finishButton.setOnAction(event -> {
                    guiApplication.getController().userFinishDecisionToken(guiApplication.getMyId(), tokensPointsList);
                    cardEffect.getChildren().clear();
                    tokensPointsList.clear();
                });
            }
        }
        HBox bottoniBox = new HBox(10);
        bottoniBox.setAlignment(Pos.CENTER);
    }

    public void updateCardMessageBox(String messageText) {
        Label message = new Label();
        cardEffect.getChildren().clear();
        message.setText(messageText);
        message.setStyle("-fx-text-fill: #00ccff; -fx-font-size: 16; -fx-font-weight: bold;");
        cardMessages.getChildren().add(message);
    }

    public void updatePlayerStats() {
        Player player = getPlayer();
        Label playerStats = new Label();
        if (player != null) {
//            StringBuilder stats = new StringBuilder("Player: " + player.getUsername() + "\n");
//            stats.append("FirePower (without activating): ").append(player.getScore()).append("\n");
//            stats.append("MotorPower (without activating): ").append(player.getTokens()).append("\n");
//            stats.append("Crew: ").append(player.getShipBoard().getGrid().size()).append(" tiles\n");
//            stats.append("Credits: ").append(player.getShipBoard().getGrid().size()).append(" ships\n");
//            stats.append("Destroyed tiles: ").append(player.getShipBoard().getGrid().size()).append(" destroyed tiles\n");
//            playerStats.setText(stats.toString());
        } else {
            playerStats.setText("No player data available.");
        }
    }

    private void updateViewForSpecificCard() {
        switch(currentAdventureCard.getCardId()){
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

    private void clickTile() {

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
    private boolean isThisPlayerLeader(){
        Player player = getPlayer();
        Player leader;
        leader = Objects.requireNonNull(getGame()).getGameboard().getLeader();
        return leader.equals(player);
    }

    private void dbg() {
        System.out.println("Debug" + counterStampe++);
    }
}
