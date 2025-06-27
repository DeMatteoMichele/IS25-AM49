package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.*;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.LeaderBoard;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.client.model.shipboard.Tile;
import it.polimi.ingsw.is25am49.shared.RemoteServer;
import it.polimi.ingsw.is25am49.shared.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Orientation;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import org.jline.terminal.TerminalBuilder;

import java.awt.*;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GraphicUserInterface extends Application implements UserInterface {
    /**
     * Represents the client-side controller managing logic and communication
     * between the graphical user interface and the backend system.
     */
    private ClientController controller;
    /**
     * Represents the current game session associated with the user interface.
     * Manages game-specific data, players, and interactions during gameplay.
     */
    private GameSession gameSession;
    /**
     * Stores the server IP address for network communication.
     * Defaults to "localhost" if not explicitly set.
     */
    public static String serverIP = "localhost"; // Default server IP
    /**
     * Port number used for establishing RMI (Remote Method Invocation) connections.
     */
    private final int serverPortRMI = 1099;
    /**
     * Stores the username of the user interacting with the GUI.
     */
    private String username;
    /**
     * A mapping between scene names and their corresponding scene objects.
     * Used to manage and switch between different graphical user interface scenes.
     */
    private Map<ScenesName, Scene> scenesList;
    /**
     * Stores the mapping between scene names and their corresponding controllers.
     * Used for managing and accessing scene controllers dynamically.
     */
    private Map<ScenesName, GenericSceneController> controllerList;
    /**
     * Represents the unique identifier of the client.
     * Defaults to -1 if not assigned.
     */
    private int myId = -1;
    /**
     * Manages a fixed thread pool of 10 threads for executing concurrent tasks within the application.
     */
    private final ExecutorService executor = Executors.newFixedThreadPool(10);


    /**
     * Represents the primary stage of the JavaFX application.
     * Used to manage and display the user interface.
     */
    private Stage stage;


    /**
     * Initializes the GraphicUserInterface and its associated game session.
     */
    public GraphicUserInterface() {
        gameSession = new GameSession(this);
    }

    /**
     * Retrieves the ID of the current user.
     *
     * @return the ID of the user.
     */
    public int getMyId() {
        return myId;
    }

    /**
     * Returns the current instance of the GraphicUserInterface.
     *
     * @return the current GraphicUserInterface instance.
     */
    public GraphicUserInterface getGui() {
        return this;
    }

    /**
     * Retrieves the current game session.
     *
     * @return the current GameSession instance
     */
    public GameSession getGameSession() {
        return gameSession;
    }

    /**
     * Retrieves the username associated with this instance.
     *
     * @return the current username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username for the user.
     *
     * @param username the username to be set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Loads all scenes defined in the ScenesName enum using FXMLLoader and initializes controllers and scenes.
     *
     * @param primaryStage the primary stage of the application where the scenes will be set.
     */
    private void loadStage(Stage primaryStage) {

        // Load all scenes defined in ScenesName enum using FXMLLoader
        try {
            stage = primaryStage;

            FXMLLoader loader;
            Parent root;
            scenesList = new HashMap<>();
            controllerList = new HashMap<>();
            for (ScenesName scenesName : ScenesName.values()) {
                loader = new FXMLLoader(getClass().getResource(scenesName.getPath()));
                root = loader.load();
                GenericSceneController controller = loader.getController();
                controller.setGui(this, gameSession);
                controllerList.put(scenesName, loader.getController());
                scenesList.put(scenesName, new Scene(root, 1280, 720));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Displays the specified scene on the primary stage with appropriate settings
     * such as fullscreen mode and title. Centers the stage on the screen if
     * the HOME scene is displayed.
     *
     * @param scenesName the name of the scene to be shown
     */
    public void showScene(ScenesName scenesName) {
        Scene scene = scenesList.get(scenesName);

        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.setFullScreen(true);
        stage.setResizable(true);
        stage.setTitle(scenesName.getName());

        if (scenesName == ScenesName.HOME)
            stage.centerOnScreen(); // Center the stage on screen for START scene
        stage.show();

    }

    /**
     * Retrieves the primary stage of the application.
     *
     * @return the primary Stage object.
     */
    public Stage getPrimaryStage() {
        return stage;
    }

    /**
     * Displays a popup error message centered on the specified stage.
     * @param owner The stage where the popup will be displayed.
     * @param message The error message to be shown in the popup.
     */
    public void showError(Stage owner, String message) {
        // Crea il contenuto del popup
        Label label = new Label(message);
        label.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 5; -fx-font-size: 20px;");

        Popup popup = new Popup();
        popup.getContent().add(label);
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);
        popup.show(owner);
        // Posiziona il popup (centrato rispetto alla finestra)
        double centerX = owner.getX() + (owner.getWidth() - popup.getWidth()) / 2;
        double centerY = owner.getY() + (owner.getHeight() - popup.getHeight()) / 2;
        popup.setX(centerX);
        popup.setY(centerY);


        // Dopo 3 secondi, chiudi il popup
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> popup.hide());
        delay.play();
    }

    /**
     * Initializes and starts the user interface by setting up the primary stage
     * and navigating to the default scene.
     *
     * @param primaryStage the main stage for this JavaFX application
     */
    public void start(Stage primaryStage) {
        loadStage(primaryStage);
        showScene(ScenesName.HOME);
        controller = new ClientController(gameSession, this);

    }

    /**
     * The main method serves as the entry point for the application.
     *
     * @param args the command-line arguments, where the first argument can specify the server IP.
     */
    public static void main(String args[]) {
        if (args.length > 0) {serverIP = args[0];}
        launch(args);
    }


    /**
     * Retrieves the controller associated with the GraphicUserInterface.
     *
     * @return the ClientController instance representing the controller.
     */
    public ClientController getController() {
        return controller;
    }


    /**
     * Updates the current state of the GUI based on the provided state index
     * and transitions to the corresponding scene.
     *
     * @param stateIndex the index representing the new state to transition to
     */
    @Override
    public void updateStateTransition(int stateIndex) {
        GenericSceneController sceneController = controllerList.get(ScenesName.getSceneNameByIndex(stateIndex));
        switch (stateIndex) {
            case 1 -> {//BuildState
                BuildController buildcontr = (BuildController) sceneController;
                System.out.println("BUILD STATE");
                buildcontr.init();
                Platform.runLater(() -> {
                    showScene(ScenesName.BUILD);
                });


            }
            case 2 -> {//Adventure state
                adventureController2 adventureContr = (adventureController2) sceneController;
                System.out.println("ADVENTURE STATE");
                adventureContr.init();
                Platform.runLater(() -> {
                    showScene(ScenesName.ADVENTURE2);
                });
            }
            case 3 -> {
                System.out.println("FINISH STATE");
                Platform.runLater(() -> {
                    showScene(ScenesName.FINISH);
                });

            }
            default -> {
                //;
            }
        }
    }


    /**
     * Displays an update message as either an error popup or an informative popup.
     *
     * @param msg the message to be displayed; if it contains "⚠", it is treated as an error.
     */
    @Override
    public void printUpdateMessage(String msg) {
        Platform.runLater(() -> {
            if (msg.contains("⚠")) {
                showError(stage, msg);
            } else {
                Label label = new Label(msg);
                label.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 5; -fx-font-size: 20px;");

                Popup popup = new Popup();
                popup.getContent().add(label);
                popup.setAutoFix(true);
                popup.setAutoHide(true);
                popup.setHideOnEscape(true);
                popup.show(stage);
                // Posiziona il popup (centrato rispetto alla finestra)
                double centerX = stage.getX() + (stage.getWidth() - popup.getWidth()) / 2;
                double centerY = stage.getY() + (stage.getHeight() - popup.getHeight()) / 2;
                popup.setX(centerX);
                popup.setY(centerY);


                // Dopo 3 secondi, chiudi il popup
                PauseTransition delay = new PauseTransition(Duration.seconds(3));
                delay.setOnFinished(event -> popup.hide());
                delay.play();
            }
        });

    }

    /**
     * Sets the client ID and updates the graphical user interface to the initial scene.
     *
     * @param myId the client ID to be set
     */
    @Override
    public void setClientId(int myId) {
        this.myId = myId;
//        executor.submit(()-> Platform.runLater(()->{
//            showScene(ScenesName.INIT);
//        }));
        Platform.runLater(() -> showScene(ScenesName.INIT));


    }

    /**
     * Updates the game session when a new player joins. Displays a welcome message
     * to the joining player if it matches the client ID.
     *
     * @param playerId the ID of the player who has joined the game session
     */
    //chiamato quando utente fa join, stampa welcome con il suo nome
    @Override
    public void updateGameSessionNewPlayer(int playerId) {
        InitController contr = (InitController) controllerList.get(ScenesName.INIT);
        Platform.runLater(() -> {
//            System.out.println("PlayerID: " +playerId);
//            System.out.println("myId: " + myId);
            if (playerId == myId) {
                contr.setWelcome(username);

            }

        });
    }

    /**
     * Updates the game session when a new game is created by setting game information
     * and identifying the admin player.
     *
     * @param playerId   the ID of the player creating the game
     * @param gameMode   the selected mode of the game
     * @param maxPlayers the maximum number of players allowed in the game
     */
    //chiamato quando viene creato il game, setta le informazioni sul game e inserisce l'admin
    @Override
    public void updateGameSessionCreateGame(int playerId, GameMode gameMode, int maxPlayers) {
        InitController contr = (InitController) controllerList.get(ScenesName.INIT);
        contr.setupLobbyListView();
        contr.setupTestListView();
        Platform.runLater(() -> {

//            contr.getLobby().getItems().add(gameSession.getPlayer(playerId).get().getUsername() + " (ADMIN)    " + gameSession.getPlayer(playerId).get().getColor());
            contr.getInfo().getItems().clear();
            contr.getInfo().getItems().add("Players: 1/" + maxPlayers);
            contr.getInfo().getItems().add("Game mode--> " + gameMode);
            contr.getInfo().getItems().add("Max players--> " + maxPlayers);

        });
        updateGameNewPlayer(playerId);
    }

    /**
     * Updates the game state when a new player joins. Initializes the default settings if the
     * joining player is an admin and updates the game details for other players.
     *
     * @param playerId the ID of the player who joined the game
     */
    //chiamata quando utente fa join, se è l'admin setta a default le impostazioni, se è un altro player le aggiorna
    @Override
    public void updateGameNewPlayer(int playerId) {
        InitController contr = (InitController) controllerList.get(ScenesName.INIT);
        contr.setupTestListView();
        Platform.runLater(() -> {
            if (gameSession.getAdmin().map(player -> player.getIdPlayer() == playerId).orElse(false) && gameSession.getGame().isEmpty()) {

                contr.getInfo().getItems().add("Players: 1/4");
                contr.getInfo().getItems().add("Game mode--> DEFAULT");
                contr.getInfo().getItems().add("Max players--> 4 (DEFAULT)");
                if (myId == playerId) {
                    contr.showAdmin();
                }
            } else if (gameSession.getGame().isPresent()) {

                contr.setupLobbyListView();
                // cotrollo se è già presnete nella lista non lo inserisco
                boolean playerAlreadyInList = contr.getLobby().getItems().stream()
                        .anyMatch(item -> item.contains(gameSession.getPlayer(playerId).get().getUsername()));

                if (!playerAlreadyInList) {
                    if (gameSession.getAdmin().map(player -> player.getIdPlayer() == playerId).orElse(false)) {
                        contr.getLobby().getItems().add(gameSession.getPlayer(playerId).get().getUsername() + " (ADMIN)    " + gameSession.getPlayer(playerId).get().getColor());
                    } else {
                        contr.getLobby().getItems().add(gameSession.getPlayer(playerId).get().getUsername() + "   " + gameSession.getPlayer(playerId).get().getColor());

                    }
                }
                if (contr.getInfo().getItems().isEmpty()) {
                    contr.getInfo().getItems().add("Players: " + gameSession.getGame().get().numOfPlayers() + "/" + gameSession.getGame().get().getMaxPlayers());
                } else {
                    contr.getInfo().getItems().set(0, "Players: " + gameSession.getGame().get().numOfPlayers() + "/" + gameSession.getGame().get().getMaxPlayers());
                }
//                contr.getInfo().getItems().add("Players: " + gameSession.getGame().get().numOfPlayers() + "/" + gameSession.getGame().get().getMaxPlayers());


            }
        });
    }


    /**
     * Updates the game state to reflect a tile placement by a specific player on the board.
     *
     * @param playerId the ID of the player attempting to place a tile
     * @param coordinates the coordinates where the tile is to be placed
     */
    @Override
    public void updatePlaceTile(int playerId, Point coordinates) {
        if (playerId == myId) {
            BuildController buildController = (BuildController) controllerList.get(ScenesName.BUILD);
            Platform.runLater(() -> {

                buildController.addToShipBoard(coordinates);

            });

        }
    }

    /**
     * Updates the game state when a tile is picked by a player.
     *
     * @param playerId     the ID of the player performing the action
     * @param coordinates  the location of the tile on the game board
     * @param idTile       the ID of the tile being picked
     */
    @Override
    public void updatePickTile(int playerId, Point coordinates, int idTile) {
        Platform.runLater(() -> {
            BuildController buildController = (BuildController) controllerList.get(ScenesName.BUILD);
            if (playerId == myId) {
                System.out.println("PICKTILE");
                buildController.removeFromShipBoard(coordinates, idTile);
                buildController.setHandTile(idTile);
//
//                BuildController.setHandTileFromPickTile();
            }
        });
    }

    /**
     * Updates the graphical interface by rotating a tile clockwise for a specific player during their turn.
     * @param playerId the ID of the player performing the tile rotation
     */
    @Override
    public void updateRotateTileClockWise(int playerId) {
        BuildController buildController = (BuildController) controllerList.get(ScenesName.BUILD);
        Platform.runLater(() -> {
            if (playerId == myId) {
                buildController.rotateHandTile();
            }
        });
    }

    /**
     * Updates the game state when a tile is discarded by a player.
     *
     * @param playerId the ID of the player who discarded the tile
     * @param idTile the ID of the discarded tile
     */
    @Override
    public void updateDiscardTile(int playerId, int idTile) {
        BuildController buildController = (BuildController) controllerList.get(ScenesName.BUILD);
        Platform.runLater(() -> {
//            System.out.println("discard tile in handtile with id: " + idTile);
            if (playerId == myId) {
                buildController.clearHandTiles();
            }
//            BuildController.printTileCardDeck();
            buildController.enableTile(idTile);
        });
    }

    /**
     * Updates the game state when a tile is drawn by a player.
     *
     * @param playerId the ID of the player who draws the tile
     * @param idTIle the ID of the tile being drawn
     */
    @Override
    public void updateDrawTile(int playerId, int idTIle) {
        BuildController buildController = (BuildController) controllerList.get(ScenesName.BUILD);

        Platform.runLater(() -> {

            buildController.disableTile(idTIle);
//            BuildController.printTileCardDeck();

            if (playerId == myId) {
//            BuildController.clearHandTiles();
                buildController.setHandTile(idTIle);
                buildController.dragHandTile();
            }
        });
    }

    /**
     * Updates the graphical shipboard elements for a player when the game signals the
     * completion of the shipboard setup.
     *
     * @param playerId the identifier of the player whose shipboard needs to be updated
     */
    @Override
    public void updateFinishShipboard(int playerId) {

        BuildController buildController = (BuildController) controllerList.get(ScenesName.BUILD);
        Platform.runLater(() -> {
            if (playerId == myId) {
                buildController.clearShipBoard();
                buildController.printShipboard();
                buildController.clearListView();
                buildController.updateListView();
                buildController.selectTypeOfToken();
            }
        });

    }

    /**
     * Updates the visual representation of a player's small adventure card deck.
     *
     * @param playerId        The ID of the player requesting the update.
     * @param smallDeckIndex  The index of the small adventure card deck to display.
     */
    @Override
    public void updateLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        BuildController buildController = (BuildController) controllerList.get(ScenesName.BUILD);
        Platform.runLater(() -> {
            if (playerId == myId) {
                buildController.showAdventureCardDeck(smallDeckIndex);
            }

        });
    }

    /**
     * Updates the state of the small adventure card deck and performs a UI action based on the player's ID.
     *
     * @param playerId        the ID of the player initiating the update
     * @param smallDeckIndex  the index of the small adventure card deck to update
     */
    @Override
    public void updateStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {
        BuildController buildController = (BuildController) controllerList.get(ScenesName.BUILD);
        Platform.runLater(() -> {
            if (playerId == myId) {
                buildController.clearSmallAdventureDeck();
            }

        });

    }

    /**
     * Updates the state of the game by placing a timer for the specified player and triggers
     * relevant visual and messaging updates via the BuildController.
     *
     * @param playerId the ID of the player who placed the timer
     */
    @Override
    public void updatePlaceTimer(int playerId) {
        BuildController buildController = (BuildController) controllerList.get(ScenesName.BUILD);
        Platform.runLater(() -> {

            buildController.showCountDown();
            printUpdateMessage("Timer placed by player " + playerId);


        });
    }

    /**
     * Handles the completion of a timer by notifying the user through an update message
     * and triggering the end-of-timer animation on the BuildController UI.
     */
    @Override
    public void updateTimerIsDone() {
        BuildController buildController = (BuildController) controllerList.get(ScenesName.BUILD);
        Platform.runLater(() -> {
            printUpdateMessage("Timer is finished");
            buildController.playTimerEndAnimation();
        });
    }


    /**
     * Updates the selected type of token for a player at the specified coordinates.
     * @param playerId the identifier of the player making the update
     * @param token the token object being updated
     * @param coordinates the location on the board where the update is applied
     */
    @Override
    public void updateSelectTypeOfToken(int playerId, Token token, Point coordinates) {
        BuildController buildController = (BuildController) controllerList.get(ScenesName.BUILD);
        Platform.runLater(() -> {
            if (playerId == myId) {
                buildController.disableHighlight();
                updateFinishShipboard(playerId);
            }
        });
    }

    /**
     * Displays the shipboard information for the specified player.
     *
     * @param playerId the ID of the player whose shipboard is to be displayed
     */
    @Override
    public void lookAtShipboard(int playerId) {
        Platform.runLater(() -> {
            BuildController buildController = (BuildController) controllerList.get(ScenesName.BUILD);
            buildController.clearShipBoard();
            buildController.printPlayerIdShipboard(playerId);
        });

    }

    public void lookAtShipboard2(int playerId) {
        Platform.runLater(() -> {
            adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
            adventureController.clearShipBoard();
            adventureController.printPlayerByIdShipboard(playerId);
        });

    }

    /**
     * Updates the state of a player's card and manages turn-related visual updates
     * within the Adventure2 scene in the user interface.
     *
     * @param playerId   the ID of the player whose card state is being updated
     * @param cardState  the new state of the card, represented by an AdventureStatesTypes enum value
     */
    @Override
    public void updateCardState(int playerId, AdventureStatesTypes cardState) {
        Platform.runLater(() -> {
            adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
            if (myId == playerId) {
                adventureController.updateCardState(cardState);
                adventureController.updateChangeTurn(playerId);
            }
            else{
                adventureController.updateCardMessageBox("Waiting for your turn");
                adventureController.updateCardState(cardState);
                adventureController.updateChangeTurn(playerId);
                adventureController.addMessage( "Player " + gameSession.getGame().get().getGameboard().getCurrentPlayer().getUsername() + " is playing, wait for your turn");
            }

        });
    }

    /**
     * Updates the turn for a player and handles enabling/disabling actions based on the turn ownership.
     * @param playerId the unique identifier of the player whose turn is updated
     */
    @Override
    public void updateChangeTurn(int playerId) {
        adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
        Platform.runLater(() -> {
            if (myId == playerId) {
                adventureController.updateChangeTurn(playerId);
                adventureController.enableAll();
            }
            else{
                adventureController.updateCardMessageBox("Waiting for your turn");
                adventureController.disableall();
                adventureController.updateChangeTurn(playerId);
            }
        });
    }

    /**
     * Updates the generic card message for a specific player or all players.
     *
     * @param playerId the ID of the player for whom the message is displayed, or -99 for all players
     * @param message the content of the message to be displayed
     */
    @Override
    public void updateGenericCardMessage(int playerId, String message) {
        Platform.runLater(() -> {
            if (myId == playerId || playerId==-99) {
                adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
                adventureController.updateCardMessageBox(message);
                adventureController.addMessage(message);
            }
            else{
                adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
//                adventureController.updateCardMessageBox(gameSession.getPlayer(playerId).get().getUsername() + " is playing, wait for your turn");
//                adventureController.addMessage(gameSession.getGame().get().getGameboard().getCurrentPlayer() + " is playing, wait for your turn");
            }
        });
    }

    /**
     * Updates the dice throw results for a specified player.
     *
     * @param playerId   the unique identifier of the player whose dice results are being updated
     * @param firstDice  the value of the first dice rolled
     * @param secondDice the value of the second dice rolled
     */
    @Override
    public void updateThrowDices(int playerId, int firstDice, int secondDice) {
        Platform.runLater(() -> {

                adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
                adventureController.configureEffectPaneDices(firstDice, secondDice);

        });
    }

    /**
     * Updates the current adventure card for a specific player in the draw phase.
     *
     * @param playerId The ID of the player whose adventure card is being updated.
     * @param adventureCard The new adventure card to be updated for the player.
     */
    @Override
    public void updateDrawAdventureCard(int playerId, AdventureCard adventureCard) {
        Platform.runLater(() -> {
            adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
            adventureController.updateCurrentCard(adventureCard);
        });
    }


    /**
     * Updates and processes the finishing decision by activating specific coordinates for a given player.
     *
     * @param playerId The ID of the player performing the action.
     * @param coordinates A set of coordinates that are being activated.
     */
    @Override
    public void updateFinishDecisionActivating(int playerId, Set<Point> coordinates) {
        adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
        Platform.runLater(() -> {
                    Player p = gameSession.getPlayer(playerId).get();
                    StringBuilder messageBuilder = new StringBuilder("Player " + p.getUsername() + " has activated: ");
                    for (Point pt : coordinates) {
                        try{
                        Tile t = p.getShipBoard().getTile(pt);
                        messageBuilder.append("[").append(pt.x).append(",").append(pt.y).append("] - ").append(t.getComponentType()).append("\n");
                    }catch(Exception _){
                    }}
            adventureController.addMessage(messageBuilder.toString());
        if (myId== playerId) {

            adventureController.updatePlayerStats(p.getShipBoard().getTotalFirePower(),p.getShipBoard().getTotalEnginePower(),p.getShipBoard().getTotalCrewNumber(),p.getCredits(),p.getShipBoard().getDestroyedTiles());
            adventureController.printShipboard();
        }});
    }

    /**
     * Updates the finish decision token for a player, handling token insertion/removal
     * and updating relevant game state and UI accordingly.
     *
     * @param playerId The ID of the player making the decision.
     * @param listOfTokens The list of token positions and types being updated.
     * @param numTokens Total number of tokens affected by the decision.
     * @param removing A boolean indicating if the tokens are being removed (true) or inserted (false).
     */
    @Override
    public void updateFinishDecisionToken(int playerId, List<Pair<Point, Token>> listOfTokens, int numTokens, boolean removing) {
        adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
        Platform.runLater(() -> {
            System.out.println("entrato in updateFinishDecisionToken");
            Player p = gameSession.getPlayer(playerId).get();
            StringBuilder messageBuilder = new StringBuilder("Player " + p.getUsername() + " has decided to " + (removing ? "remove" : "inserted") + " the following tokens: ");
            for (Pair<Point, Token> pair : listOfTokens) {
                messageBuilder.append("\n- ").append(pair.getValue().toString()).append(" at ").append(pair.getKey());
            }
            adventureController.addMessage(messageBuilder.toString());
            if (myId == playerId) {

//                adventureController.clearEffectPane();
                adventureController.printShipboard();
                adventureController.updatePlayerStats(p.getShipBoard().getTotalFirePower(),p.getShipBoard().getTotalEnginePower(),p.getShipBoard().getTotalCrewNumber(),p.getCredits(),p.getShipBoard().getDestroyedTiles());
            }
        });
    }

    /**
     * Updates the deactivation process for the specified player and coordinates.
     *
     * @param playerId the ID of the player to update
     * @param coordinates the list of points to deactivate
     */
    @Override
    public void updateDeactivating(int playerId, List<Point> coordinates) {
//        Platform.runLater(() -> {
//            if (myId == playerId) {
//                adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
//                adventureController.deActivatePoints(coordinates);
//            }
//        });
    }

    /**
     * Updates the interface to reflect whether a player has landed on a planet or chosen not to.
     *
     * @param playerId   the ID of the player attempting to land
     * @param isLanded   whether the player successfully landed on the planet
     * @param planetId   the ID of the planet involved in the landing
     */
    @Override
    public void updateLandOnPlanet(int playerId, boolean isLanded, int planetId) {
    adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
        Platform.runLater(() -> {
            Player player = gameSession.getPlayer(playerId).get();
            if(isLanded) adventureController.addMessage("Player " + player.getUsername() + " has landed on planet " + planetId);
            else adventureController.addMessage("Player " + player.getUsername() + " decided not to land");
        });
    }

    /**
     * Updates the game state when a player decides to land or not on a station or ship.
     *
     * @param playerId the ID of the player making the decision
     * @param isLanded indicates if the player decided to land
     * @param cardId the ID of the card associated with the action
     */
    @Override
    public void updateLandOnStationOrShip(int playerId, boolean isLanded, String cardId) {
    adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
        Platform.runLater(() -> {
            Player p = gameSession.getPlayer(playerId).get();
            if(isLanded) adventureController.addMessage("Player " + p.getUsername() + " decided to land on the station or ship with cardID: " + cardId);
            else adventureController.addMessage("Player " + p.getUsername() + " decided not to land");
        });
    }

    /**
     * Updates the acceptance status of prizes for a specific player and updates the graphical interface accordingly.
     *
     * @param playerId the ID of the player whose prize acceptance status is being updated
     * @param isAccepted boolean value indicating whether the player accepted (true) or rejected (false) the prizes
     */
    @Override
    public void updateAcceptPrizes(int playerId, boolean isAccepted) {
        Platform.runLater(() -> {
            if (myId == playerId) {
                adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
                Player p = gameSession.getPlayer(playerId).get();
                adventureController.updatePlayerStats(p.getShipBoard().getTotalFirePower(),p.getShipBoard().getTotalEnginePower(),p.getShipBoard().getTotalCrewNumber(),p.getCredits(),p.getShipBoard().getDestroyedTiles());
                adventureController.clearEffectPane();
            }
            adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
            adventureController.addMessage("Player " + gameSession.getPlayer(playerId).get().getUsername() + " has " + (isAccepted ? "accepted" : "rejected") + " the prizes");
        });
    }

    /**
     * Updates the game state to handle the scenario where a player stops or lands,
     * removing them from the game and notifying the relevant UI components.
     *
     * @param playerId the unique identifier of the player who has landed or whose flight has stopped
     */
    @Override
    public void updateStopFlight(int playerId) {
        Platform.runLater(() -> {
            Player p= gameSession.getPlayer(playerId).get();
            printUpdateMessage("!!!Player " + p.getUsername() + " is landed!!!");
            adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
            adventureController.addMessage("!!!Player " + p.getUsername() + " has landed!!!\t-out of the game");
        });
    }

    /**
     * Updates the game state when an enemy is defeated, providing feedback to the players
     * and updating relevant player statistics.
     *
     * @param playerIdWhoWon the ID of the player who defeated the enemy
     * @param enemyName the name of the defeated enemy
     * @param cardId the ID of the card associated with the defeated enemy
     */
    @Override
    public void updateEnemyDefeated(int playerIdWhoWon, String enemyName, String cardId) {
        Platform.runLater(() -> {
            if (myId == playerIdWhoWon) {
                adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
                adventureController.updateCardMessageBox("You have defeted the enemy " + enemyName);
                adventureController.addMessage("You have defeted the enemy " + enemyName);
                Player p = gameSession.getPlayer(playerIdWhoWon).get();
                adventureController.updatePlayerStats(p.getShipBoard().getTotalFirePower(),p.getShipBoard().getTotalEnginePower(),p.getShipBoard().getTotalCrewNumber(),p.getCredits(),p.getShipBoard().getDestroyedTiles());
            }
            else{
                adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
                adventureController.addMessage("Player " + gameSession.getPlayer(playerIdWhoWon).get().getUsername() + " has defeted the enemy " + enemyName );
            }
        });
    }

    /**
     * Updates the game state when a crew member is lost at a specific coordinate due to an effect.
     *
     * @param playerId    the ID of the player affected by the effect
     * @param coordinate  the coordinates where the effect occurred
     * @param token       the type of token representing the crew member or related entity lost
     */
    @Override
    public void updateEffectModifyCrew(int playerId, Point coordinate, Token token) {
        Platform.runLater(() -> {
            System.out.println("updateEffectModifyCrew");
            adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);

            if (myId == playerId) {
//                adventureController.removeTokenFromTile(coordinate, token);
                Player p = gameSession.getPlayer(playerId).get();
                adventureController.updatePlayerStats(p.getShipBoard().getTotalFirePower(),p.getShipBoard().getTotalEnginePower(),p.getShipBoard().getTotalCrewNumber(),p.getCredits(),p.getShipBoard().getDestroyedTiles());
                adventureController.printShipboard();
                adventureController.addMessage("you have lost a crew member on coordinates: <" +coordinate.x +","+coordinate.y +"> - token: " + token);
            }
        });
    }

    /**
     * Updates the effect of a cannonade on the player's shipboard.
     *
     * @param playerId the ID of the player undergoing the cannonade effect
     * @param coordinate the coordinates of the tile affected by the cannonade
     * @param or the orientation of the cannonade
     */
    @Override
    public void updateEffectCannonade(int playerId, Point coordinate, Orientation or) {
        Platform.runLater(() -> {
            adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
            adventureController.addMessage("the cannonade successfully hit the shipboard\n -->tile in coordinates: <" +coordinate.x +","+coordinate.y +"> has been destroyed");
            if (myId == playerId) {
                adventureController.printShipboard();
                Player p = gameSession.getPlayer(playerId).get();
                adventureController.updatePlayerStats(p.getShipBoard().getTotalFirePower(),p.getShipBoard().getTotalEnginePower(),p.getShipBoard().getTotalCrewNumber(),p.getCredits(),p.getShipBoard().getDestroyedTiles());
            }
        });
    }

    /**
     * Updates the UI and player stats when a player loses a good at a specific coordinate.
     * @param playerId the unique ID of the player losing the good
     * @param coordinate the coordinates where the good was lost
     * @param token the type of good or token lost
     */
    @Override
    public void updateEffectLoseGoods(int playerId, Point coordinate, Token token) {
        Platform.runLater(() -> {
            adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
            if (myId == playerId) {
//                adventureController.removeTokenFromTile(coordinate, token);
                adventureController.printShipboard();
                Player p = gameSession.getPlayer(playerId).get();
                adventureController.updatePlayerStats(p.getShipBoard().getTotalFirePower(),p.getShipBoard().getTotalEnginePower(),p.getShipBoard().getTotalCrewNumber(),p.getCredits(),p.getShipBoard().getDestroyedTiles());
                adventureController.addMessage("you have lost a good on coordinates: <" +coordinate.x +","+coordinate.y +"> - token: " + token);
            }
        });
    }

    /**
     * Updates the game state and GUI when a player wins a token at a specified coordinate.
     *
     * @param playerId The ID of the player who won the token.
     * @param coordinate The point on the board where the token was won.
     * @param token The type of token that was won.
     */
    @Override
    public void updateEffectWinGoods(int playerId, Point coordinate, Token token) {
        Platform.runLater(() -> {
            adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
            if (myId == playerId) {
                adventureController.printShipboard();
                Player p = gameSession.getPlayer(playerId).get();
                adventureController.updatePlayerStats(p.getShipBoard().getTotalFirePower(),p.getShipBoard().getTotalEnginePower(),p.getShipBoard().getTotalCrewNumber(),p.getCredits(),p.getShipBoard().getDestroyedTiles());
                adventureController.addMessage("you have won a good on coordinates: <" +coordinate.x +","+coordinate.y +"> - token: " + token);
            }

        });
    }


    /**
     * Updates the number of goods won by a player in different categories.
     *
     * @param playerId     The ID of the player whose goods are being updated.
     * @param redgoods     The number of red goods won by the player.
     * @param yellowgoods  The number of yellow goods won by the player.
     * @param greengoods   The number of green goods won by the player.
     * @param bluegoods    The number of blue goods won by the player.
     */
    @Override
    public void updateNumberOfWinGoods(int playerId, int redgoods, int yellowgoods, int greengoods, int bluegoods) {
//        Platform.runLater(() -> {
//            if (myId == playerId) {
//                adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
//                adventureController.configureEffectPaneGoods(redgoods, greengoods, bluegoods, yellowgoods);
//            }
//        });
    }

    /**
     * Updates the game effects related to energy modification based on the given player's action.
     *
     * @param playerId    The unique identifier of the player making the update.
     * @param coordinate  The grid coordinate where the energy modification occurred.
     * @param token       The token associated with the energy modification effect.
     */
    @Override
    public void updateEffectModifyEnergy(int playerId, Point coordinate, Token token) {
        Platform.runLater(() -> {
            adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
            if (myId == playerId) {
//                adventureController.removeTokenFromTile(coordinate, token);
                adventureController.printShipboard();
                Player p = gameSession.getPlayer(playerId).get();
                adventureController.updatePlayerStats(p.getShipBoard().getTotalFirePower(),p.getShipBoard().getTotalEnginePower(),p.getShipBoard().getTotalCrewNumber(),p.getCredits(),p.getShipBoard().getDestroyedTiles());
                adventureController.addMessage("battery from coordinates: <" +coordinate.x +","+coordinate.y +"> has been removed");
            }


        });
    }

    /**
     * Updates the game UI to reflect the meteorite effect on a player's shipboard.
     *
     * @param playerId the ID of the player affected by the meteorite
     * @param coordinate the coordinates of the impacted tile
     * @param or the orientation of the meteorite impact
     */
    @Override
    public void updateEffectMeteorite(int playerId, Point coordinate, Orientation or) {
        Platform.runLater(() -> {
            adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
            if (myId == playerId) {
//               adventureController.updateAttack(coordinate, or, true);
                adventureController.printShipboard();
                Player p = gameSession.getPlayer(playerId).get();
                adventureController.updatePlayerStats(p.getShipBoard().getTotalFirePower(),p.getShipBoard().getTotalEnginePower(),p.getShipBoard().getTotalCrewNumber(),p.getCredits(),p.getShipBoard().getDestroyedTiles());
            }
            adventureController.addMessage("the meteorite successfully hit the shipboard\n -->tile in coordinates: <" +coordinate.x +","+coordinate.y +"> has been destroyed");
        });
    }

    /**
     * Updates the earned credits for a specified player and updates the UI.
     *
     * @param playerId the unique identifier of the player whose credits are being updated
     * @param credits the amount of credits to add to the player's total
     */
    @Override
    public void updateEarnedCredits(int playerId, int credits) {
        Platform.runLater(() -> {
            updateGenericCardMessage(playerId, "you have earned " + credits + " credits");
            if (myId == playerId) {
                adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
                Player p = gameSession.getPlayer(playerId).get();
                adventureController.updatePlayerStats(p.getShipBoard().getTotalFirePower(),p.getShipBoard().getTotalEnginePower(),p.getShipBoard().getTotalCrewNumber(),p.getCredits(),p.getShipBoard().getDestroyedTiles());
//                adventureController.updatePlayerStats(0,0,);
            }
        });
    }

    /**
     * Updates the game state to reflect the tiles destroyed on a player's ship.
     *
     * @param playerId           The unique identifier of the player whose tiles were destroyed.
     * @param destroyedTiles     The set of points representing the destroyed tiles on the player's ship.
     */
    @Override
    public void updateDestroyedTiles(int playerId, Set<Point> destroyedTiles) {
        Platform.runLater(() -> {
            adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
            if (myId == playerId) {
                adventureController.printShipboard();
                StringBuilder str = new StringBuilder();
                Player p = gameSession.getPlayer(playerId).get();
                str.append("Player ").append(p.getUsername()).append(" has lost the following tiles: ");
                for (Point pt : destroyedTiles) {
                    try{
                        Tile t=p.getShipBoard().getTile(pt);
                        str.append("[").append(pt.x).append(",").append(pt.y).append("] - ").append(t.getComponentType()).append("\n");
                    }catch(Exception _){}
                }
                adventureController.addMessage(str.toString());
                adventureController.updatePlayerStats(p.getShipBoard().getTotalFirePower(),p.getShipBoard().getTotalEnginePower(),p.getShipBoard().getTotalCrewNumber(),p.getCredits(),p.getShipBoard().getDestroyedTiles());
            }
        });
    }

    /**
     * Updates the leaderboard by delegating to the appropriate controller to redraw it.
     * @param leaderBoard the LeaderBoard object containing updated ranking information.
     */
    //Complesso, si potrebbe rivedere la modalità di gestione della leaderboard
    @Override
    public void updateLeaderBoard(LeaderBoard leaderBoard) {
        Platform.runLater(() -> {
            adventureController2 adventureController = (adventureController2) controllerList.get(ScenesName.ADVENTURE2);
            adventureController.reDrawLeaderBoard(leaderBoard);
        });
    }

    /**
     * Updates the end game state and triggers the end game display.
     *
     * @param playerId the ID of the player finishing the game
     */
    @Override
    public void updateEndGame(int playerId) {
        finishController finishController = (finishController) controllerList.get(ScenesName.FINISH);
        Platform.runLater(finishController::printEndGame);
    }

    /**
     * Updates and calculates the score for the specified player and invokes related UI changes.
     *
     * @param playerId the unique identifier of the player whose score needs to be updated
     */
    @Override
    public void updateCalculateScore(int playerId) {
        finishController finishController = (finishController) controllerList.get(ScenesName.FINISH);
        Platform.runLater(()->{
            finishController.printPodium();
            finishController.disableCalculateScore();
        });
    }

    /**
     * Establishes or re-establishes an RMI (Remote Method Invocation) connection with the server.
     *
     * @param isReconnecting indicates if this is a reconnection attempt or an initial connection.
     * @param clientRMI the RMI client instance used to communicate with the server.
     * @throws NotBoundException if the requested name cannot be found in the registry.
     * @throws IOException if an I/O error occurs during the connection process.
     */
    @Override
    public void connectionRMI(boolean isReconnecting, ClientRMI clientRMI) throws NotBoundException, IOException {
        RemoteServer serverStub;
        Registry reg = LocateRegistry.getRegistry(serverIP, serverPortRMI);
        serverStub = (RemoteServer) reg.lookup("Server");
        settingsController settingsController = (settingsController) controllerList.get(ScenesName.SETTINGS);
        if (!isReconnecting) {
            clientRMI = new ClientRMI(controller, serverStub, this, serverIP);
            System.out.println("Client successfully connected to [" + serverIP + " , 1099]");
            controller.setServerMethods(serverStub);
            try {
                System.out.println("trying to log in to the server ...");
                controller.registerClientRMI(clientRMI);
                System.out.println("client logged");
//                guiApplication.showScene(ScenesName.INIT);
            } catch (RemoteException e) {
                printUpdateMessage(settingsController.error + e.getMessage());
                settingsController.disableConnectionButtons();
                settingsController.enableRetryVisibility();
            }
        } else {
            clientRMI.replaceServerStub(serverStub);
            System.out.println("Reconnected successfully to [" + serverIP + " , 1099]");
            controller.setServerMethods(serverStub);
            try {
                System.out.println("trying to log in to the server ...");
                controller.registerClientRMI(clientRMI);
                System.out.println("client logged");
            } catch (RemoteException e) {
                System.out.println("client failed logging on server RMI --> " + e.getMessage());
                printUpdateMessage(settingsController.error + " client failed logging on server RMI --> " + e.getMessage());
            }
        }
    }

    /**
     * Sets the current game session with the provided GameSession instance.
     *
     * @param gameSession the GameSession object to be assigned
     */
    @Override
    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    /**
     * Resets the state of the application, including reinitializing
     * necessary components and reloading the initial scene.
     */
    public void reset() {
//        myId = -1;
//        username = null;
//        // Ricarica tutte le scene
//        try {
//            loadStage(stage);
//            showScene(ScenesName.HOME);
//            controller = new ClientController(gameSession, this);
//        } catch (Exception e) {
//            System.out.println("Errore durante il reset della GUI: " + e.getMessage());
//            e.printStackTrace();

        controller.clientReset();
//        Platform.exit();
        start(stage);
//        main(new String[]{serverIP});
    }
//    }


}
