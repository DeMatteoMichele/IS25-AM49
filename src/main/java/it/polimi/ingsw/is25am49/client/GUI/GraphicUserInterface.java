package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.*;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;
import it.polimi.ingsw.is25am49.client.model.enums.Token;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GraphicUserInterface extends Application implements UserInterface {
    private ClientController controller;
    private final GameSession gameSession;

    private  String username;
    private Map<Scene, ScenesName> scenesList;
    private Map<GenericSceneController, ScenesName> controllerList;

    ObservableList<String> players = FXCollections.observableArrayList();
    ListView<String> listView;
    @FXML private TextField game;
    private GameMode mode;
    private int maxPlayers;
    Label setupLabel = new Label();
    Button setupButton1 = new Button();
    Button setupButton2 = new Button();
    Button setupButton3 = new Button();
    Label setupLabel2 = new Label();
    TextField setupTextField2 = new TextField();
    Button maxPlayersButton = new Button();
    VBox vbox2 = new VBox();
    VBox vbox = new VBox();
    private Stage stage;
    @FXML private Button startButton;
    private int myId;
    boolean isConnected = false;


        public GraphicUserInterface(){
            gameSession = new GameSession( this);
            listView = new ListView<>(players);
        }
        public GraphicUserInterface getGui() {
            return this;
        }
        public GameSession getGameSession() {
            return gameSession;
        }

    private void loadStage(Stage primaryStage)  {

        // Load all scenes defined in ScenesName enum using FXMLLoader
        try{
            stage = primaryStage;

            FXMLLoader loader = new FXMLLoader();
            Parent root;
            scenesList = new HashMap<>();
            controllerList = new HashMap<>();
            for (ScenesName scenesName : ScenesName.values()) {
                loader = new FXMLLoader(getClass().getResource(scenesName.getPath()));
                root = loader.load();
                GenericSceneController controller = loader.getController();
                controller.setGui(this);
                controllerList.put(loader.getController(), scenesName);
                scenesList.put(new Scene(root, 1280, 720), scenesName);
            } }catch (Exception e) {
                throw new RuntimeException(e);
            }
    }
    public Scene getSceneByEnum(ScenesName scenesName) {
        for (Map.Entry<Scene, ScenesName> entry : scenesList.entrySet()) {
            if (entry.getValue() == scenesName) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void showScene(ScenesName scenesName) {
        Scene scene = getSceneByEnum(scenesName);

        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.setFullScreen(true);
        stage.setResizable(true);
        stage.setTitle(scenesName.getName());

        if (scenesName == ScenesName.HOME)
            stage.centerOnScreen(); // Center the stage on screen for START scene
        stage.show();

    }

    public Stage getPrimaryStage() {
        return stage;
    }
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

      public void start(Stage primaryStage) {
            loadStage(primaryStage);



           showScene(ScenesName.HOME);






          controller = new ClientController(gameSession, this);
//          Button button = new Button("Conferma");
//          Label label1 = new Label("scegli il metodo di connessione: ");
//          Button button1 = new Button("SOCKET");
//             Label label = new Label("Welcome to the game!");
//             Label label2 = new Label("Please enter your username:");
//             Label label3 = new Label("Do you want to reconnect to a previous game?");
//             Button button2 = new Button("YES");
//             Button button3 = new Button("NO");
//             Button button4 = new Button("RMI");
//             button4.setDisable(true);
//
//
//          TextField textField = new TextField();
//          textField.setDisable(true);
//            button.setDisable(true);
//             button2.setOnAction(event -> {
//                 System.out.println("Reconnecting to previous game...");
//                 isConnected = true;
//                 label2.setText("Please enter your EXACT previous username:");
//                 button3.setDisable(true);
//                 button2.setDisable(true);
//
//                 // Add logic to reconnect to a previous game
//             });
//                button3.setOnAction(event -> {
//                    System.out.println("Starting a new game...");
//                    button3.setDisable(true);
//                    button2.setDisable(true);
//                    textField.setDisable(false);
//                    button.setDisable(false);
//                });
//
//
//
//
//          button1.setDisable(true);
//          final Label label5 = new Label("Welcome to the game " + username + "!");
//            button.setOnAction(event -> {
//                username = textField.getText().trim();
//                if (!username.isEmpty()) {
//                    System.out.println("Username: " + username);
//                    label5.setText("Welcome to the game " + username + "!");
//                    textField.setDisable(true);
//                    button.setDisable(true);
//                    button1.setDisable(false);
//                    button4.setDisable(false);
//                    controller.setUsername(username);
//                    if(!isConnected) {
//                        System.out.println("entrato");
//                        controller.createMyIdJson();
//                    }
//                } else {
//                    System.out.println("Please enter a username.");
//                }
//            });
//
//
//// Creo la ListView per mostrare i giocatori nel lobby
//
//            listView = new ListView<>(players);
//          listView.setPrefHeight(150);
//          listView.setPrefWidth(200);
//          Label lobbyLabel = new Label("Giocatori nel Lobby:");
//          lobbyLabel.setStyle("-fx-font-weight: bold;");
//
//          VBox lobbyBox = new VBox(5, lobbyLabel, listView);
//          lobbyBox.setStyle("-fx-padding: 10px;");
//
//          vbox.getChildren().addAll(label,label3, button2, button3, label2, textField, button, label1, button1,button4);
////          Label label5 = new Label("Welcome to the game " + username + "!");
//          label5.setStyle("-fx-font-size: 18px; -fx-padding: 15px;");
//
//          BorderPane borderPane = new BorderPane(label5);
//          borderPane.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: #f5f5f5;");
//          Label label6 = new Label("GAMELOBBY" );
//          label6.setStyle("-fx-font-size: 18px; -fx-padding: 15px;");
//          BorderPane borderPane1 = new BorderPane(label6);
//          borderPane1.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: #f5f5f5;");
//
//          vbox2.getChildren().add(new Region() {{ setMinHeight(4); }});
//          vbox2.getChildren().add(borderPane);
//          vbox2.getChildren().add(borderPane1);
//          vbox2.getChildren().add(new Region() {{ setMinHeight(20); }});
//          vbox2.setSpacing(10);
//          vbox2.setAlignment(Pos.CENTER_LEFT);
//          Label errorLabel = new Label();
//          Label errorLabel2 = new Label();
//          errorLabel2.setStyle("-fx-text-fill: red;");
//          vbox2.getChildren().add(errorLabel2);
//          errorLabel.setStyle("-fx-text-fill: red;");
//          vbox2.setPadding(new Insets(10, 0, 0, 20));
//          Button joinlobby = new Button("Join Lobby");
//          Button setupGame = new Button("Setup Game");
//
//          vbox2.getChildren().add(setupGame);
//            vbox2.getChildren().add(lobbyBox);
//
//            vbox2.getChildren().add(joinlobby);
//
//          joinlobby.setOnAction(event -> {
//              try {
//                  controller.playerJoin(username);
//                  joinlobby.setDisable(true);
//
//              }catch (Exception e){
//
//                  errorLabel2.setText(e.getMessage());
//              }
//
//          });
//
//          AtomicBoolean setupUICreated = new AtomicBoolean(false);
//
//          vbox2.getChildren().addAll(setupLabel, setupButton1, setupButton2,setupButton3,setupLabel2,setupTextField2,maxPlayersButton);
//
//         setupGame.setOnAction(event -> {
//    try {
//        // Prepara l'interfaccia utente per il setup del gioco
//        setupLabel.setText("Select the game mode:");
//        setupLabel.setDisable(false);
//        setupButton1.setText("Flight mode");
//        setupButton1.setDisable(false);
//        setupButton2.setText("LEVEL1");
//        setupButton2.setDisable(false);
//        setupButton3.setText("LEVEL2");
//        setupButton3.setDisable(false);
//
//        // Resetta lo stato dei controlli per il numero di giocatori
//        setupLabel2.setText("Enter the max number of players (2-4):");
//        setupLabel2.setDisable(true);
//        setupTextField2.setDisable(true);
//        setupTextField2.clear();
//        maxPlayersButton.setText("ClickHere");
//        maxPlayersButton.setDisable(true);
//        errorLabel2.setText("");
//
//        setupButton1.setOnAction(event1 -> {
//            mode = GameMode.FLIGHT_MODE;
//           // System.out.println("Flight mode selected");
//            disableModeSelection();
//            enablePlayerSelection();
//        });
//
//        setupButton2.setOnAction(event1 -> {
//            mode = GameMode.LEVEL_1;
//            //System.out.println("Level 1 mode selected");
//            disableModeSelection();
//            enablePlayerSelection();
//        });
//
//        setupButton3.setOnAction(event1 -> {
//            mode = GameMode.LEVEL_2;
//            //System.out.println("Level 2 mode selected");
//            disableModeSelection();
//            enablePlayerSelection();
//        });
//
//        maxPlayersButton.setOnAction(event1 -> {
//            try {
//                maxPlayers = Integer.parseInt(setupTextField2.getText());
//                if (maxPlayers < 2 || maxPlayers > 4) {
//                    errorLabel2.setText("Max players must be between 2 and 4");
//                    disableModeSelection();
//                    disablePlayerSelection();
//                } else {
//
//                    try {
//                        setupLabel2.setDisable(true);
//                        setupTextField2.setDisable(true);
//                        maxPlayersButton.setDisable(true);
//                        controller.adminSetup(maxPlayers, mode.ordinal());
//                        System.out.println("Game setup with mode: " + mode + " and max players: " + maxPlayers);
//                        setupGame.setDisable(true);
//                        disableModeSelection();
//                        disablePlayerSelection();
//                    } catch (Exception e) {
//                        errorLabel2.setText(e.getMessage());
//                        disableModeSelection();
//                        disablePlayerSelection();
//                    }
//                }
//            } catch (NumberFormatException e) {
//                errorLabel2.setText("Please enter a valid number");
//                disableModeSelection();
//                disablePlayerSelection();
//            }
//        });
//    } catch (Exception e) {
//        errorLabel2.setText(e.getMessage());
//        disableModeSelection();
//        disablePlayerSelection();
//    }
//});
//
//            button1.setOnAction(event -> {
//                System.out.println("Socket connection selected");
//                button1.setDisable(true);
//                button4.setDisable(true);
//
//                vbox.getChildren().clear();
//                vbox.getChildren().add(vbox2);
//
//
//                try{
//                    ClientSerializer clientSerializer = new ClientSerializer(controller);
//                    ServerHandler serverHandler = new ServerHandler(serverIP, serverPortSocket);
//                    serverHandler.setClientSerializer(clientSerializer);
//                    clientSerializer.setServerHandler(serverHandler);
//                    controller.setServerMethods(clientSerializer);
//                    controller.setServerHandler(serverHandler);
//                    controller.registerClientSocket();
//                    System.out.println("Client registered with id "+ myId);
//
//
//                }
//                catch (Exception e){
//
//                    errorLabel2.setText(e.getMessage());
//                    resetSetupControls();
//
//
//                    Label retryLabel = new Label("Do you want to retry?");
//                    Button yesButton = new Button("Yes");
//                    Button noButton = new Button("No");
//
//                    vbox.getChildren().addAll(retryLabel, yesButton, noButton);
//
//                    yesButton.setOnAction(retryEvent -> {
//
//                        vbox.getChildren().removeAll(retryLabel, yesButton, noButton, errorLabel);
//                        button1.setDisable(false);
//                        button4.setDisable(false);
//                    });
//
//                    noButton.setOnAction(retryEvent -> {
//
//                        vbox.getChildren().removeAll(retryLabel, yesButton, noButton);
//                        primaryStage.close();
//                    });
//                }
//            });
//
//            button4.setOnAction(event-> {
//                        System.out.println("RMI connection selected");
//                        button4.setDisable(true);
//                        button1.setDisable(true);
//                    });
//
//        vbox.setSpacing(10);
//        Scene scene = new Scene(vbox, 300, 200);
//
//        primaryStage.setTitle("Home");
//        primaryStage.setScene(scene);
//        primaryStage.setFullScreen(true);
//        primaryStage.show();
//        }


//          FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/it/polimi/ingsw/is25am49/FXML_files/home.fxml"));
//          fxmlLoader.setController(gui);
//          try {
//                Parent root = fxmlLoader.load();
//                Scene scene = new Scene(root);
//                primaryStage.setTitle("Home");
//                primaryStage.setScene(scene);
//                primaryStage.setFullScreen(true);
//                primaryStage.show();
//                startButton.setOnAction(event -> {
//                    String username = game.getText().trim();
//                    if (!username.isEmpty()) {
//                        System.out.println("Username: " + username);
//                        game.setDisable(true);
//                        gui.controller.setUsername(username);
//
//
//                    } else {
//                        System.out.println("Please enter a username.");
//                    }
//                });
//          }catch (IOException e){
//                throw new RuntimeException(e);
     }

      public static void main(String args[]) {
            launch(args);}
    // Il metodo disableModeSelection disabilita i controlli della modalità
    private void disableModeSelection() {
        setupLabel.setDisable(true);
        setupButton1.setDisable(true);
        setupButton2.setDisable(true);
        setupButton3.setDisable(true);
    }

    private void disablePlayerSelection() {
        setupLabel2.setDisable(true);
        setupTextField2.setDisable(true);
        maxPlayersButton.setDisable(true);
    }

    // Il metodo enablePlayerSelection abilita i controlli per il numero di giocatori
    private void enablePlayerSelection() {
        setupLabel2.setDisable(false);
        setupTextField2.setDisable(false);
        maxPlayersButton.setDisable(false);
    }

    // Il metodo resetSetupControls riporta tutti i controlli allo stato iniziale
    private void resetSetupControls() {
        setupLabel.setDisable(false);
        setupButton1.setDisable(false);
        setupButton2.setDisable(false);
        setupButton3.setDisable(false);
        setupLabel2.setDisable(true);
        setupTextField2.setDisable(true);
        maxPlayersButton.setDisable(true);
    }

    public ClientController getController() {
        return controller;
    }


    @Override
    public void updateStateTransition(int stateIndex) {
        switch (stateIndex) {
            case 1 -> {//BuildState
                System.out.println("BUILD STATE");
                Platform.runLater(() -> {
                    vbox.getChildren().clear();
                    Label buildLabel = new Label("Build Phase");
                    buildLabel.setStyle("-fx-font-size: 18px; -fx-padding: 15px;");
                    BorderPane buildPane = new BorderPane(buildLabel);
                    buildPane.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-background-color: #f5f5f5;");
                    VBox vbox3 = new VBox(buildPane);
                    vbox.getChildren().add(vbox3);
                });


            }
            default -> {
                //;
            }
        }
    }

    @Override
    public void printUpdateMessage(String msg) {

    }

    @Override
    public void setClientId(int myId) {
        this.myId = myId;
    }


    @Override
    public void updateGameSessionNewPlayer(int playerId) {
        System.out.println("SONO ENTRATO NELLA UPDATE");
        Platform.runLater(() -> {
            String playerName = gameSession.getPlayer(playerId).get().getUsername();
            if(playerName.equals(gameSession.getAdmin().get().getUsername())){
                playerName = playerName + " (Admin)";
            }
            players.add(playerName + " (ID: " + playerId + ")");
            System.out.println("Player added to lobby: " + playerName);

            // Stampa alla console per debug
            System.out.println("Lista giocatori aggiornata con successo");
            System.out.println("Dimensione attuale della lista: " + players.size());
        });

    }

    @Override
    public void updateGameSessionCreateGame(int playerId, GameMode gameMode, int maxPlayers) {
        updateGameNewPlayer(playerId);
    }

    @Override
    public void updateGameNewPlayer(int playerId) {

    }


    @Override
    public void updatePlaceTile(int playerId, Point coordinates) {

    }

    @Override
    public void updatePickTile(int playerId, Point coordinates) {

    }

    @Override
    public void updateRotateTileClockWise(int playerId) {

    }

    @Override
    public void updateDiscardTile(int playerId) {

    }

    @Override
    public void updateDrawTile(int playerId, int idTIle) {

    }

    @Override
    public void updateFinishShipboard(int playerId) {

    }

    @Override
    public void updateLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {

    }

    @Override
    public void updateStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) {

    }

    @Override
    public void updatePlaceTimer(int playerId) {

    }

    @Override
    public void updateTimerIsDone() {

    }

    @Override
    public void updateSelectTypeOfToken(int playerId, Token token, Point coordinates) {

    }

    @Override
    public void lookAtShipboard(int playerId) {

    }

    @Override
    public void setGameSession(GameSession gameSession) {

    }
}
