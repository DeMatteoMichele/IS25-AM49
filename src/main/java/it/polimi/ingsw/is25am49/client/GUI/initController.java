package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;


public class initController extends GenericSceneController{
    @FXML
    private Label labelMaxPlayers;
    @FXML
    private TextField textMaxPlayers;
    @FXML
    private Label textGamemode;
    @FXML
    private Button buttonFlightMode;
    @FXML
    private Button buttonLv1;
    @FXML
    private Button buttonLv2;
    @FXML
    private Button buttonMaxPlayers;
    @FXML
    private Button setupGame;
    @FXML
    private Button joinLobby;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private ListView<String> lobby;
    @FXML
    private Label welcome;
    @FXML
    private ListView<String> info;
    @FXML
    private Label labelAdmin;
    private GameMode mode;


    public void initialize() {
        Image image = new Image(getClass().getResource("/sfondo3.jpg").toExternalForm());
        backgroundImage.setImage(image);
        disableAll();

        backgroundImage.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                backgroundImage.fitWidthProperty().bind(newScene.widthProperty());
                backgroundImage.fitHeightProperty().bind(newScene.heightProperty());
            }
        });
    }

    public void setWelcome(String username) {
        welcome.setText("WELCOME TO THE GAME:  " + username.toUpperCase() + "!");
        welcome.setStyle("-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.4), 5, 0.3, 0, 0); -fx-padding: 10;");
    }
//    public void setupLobbyListView() {
//        lobby.setCellFactory(listView -> new ListCell<>() {
//            @Override
//            protected void updateItem(String item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty || item == null) {
//                    setText(null);
//                    setStyle("");
//                } else {
//                    setText(item);
//                    setStyle("-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.4), 5, 0.3, 0, 0); -fx-padding: 10;");
//                }
//            }
//        });
//    }
public void setupLobbyListView() {
    lobby.setCellFactory(listView -> new ListCell<>() {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setStyle("");
                setGraphic(null);
            } else {
                String username = item;
                String colorHex = "#FFFFFF"; // default bianco
                boolean isDuplicate = false;
                boolean hasDuplicateColor = false;

                //tengo traccia dei coloir già visti
                java.util.Set<String> seenColors = new java.util.HashSet<>();
                java.util.Set<String> seenUsernames = new java.util.HashSet<>();

                var players = getGui().getGameSession().getGame().get().getListPlayers();

                // Verifica duplicati di username
                for (var player : players) {
                    String playerUsername = player.getUsername();
                    String playerColor = String.valueOf(player.getColor());
                    //guardo se ci sono doppioni
                    if (!seenColors.add(playerColor)) {
                        System.out.println("ATTENZIONE: Colore duplicato rilevato: " + playerColor);
                        hasDuplicateColor = true;
                    }

                    //colore per lo username
                    if (username.contains(playerUsername)) {
                        colorHex = playerColor;
                    }
                }

                // creo quadratino del colore
                javafx.scene.shape.Rectangle colorSquare = new javafx.scene.shape.Rectangle(20, 20);
                colorSquare.setFill(javafx.scene.paint.Color.web(colorHex));
                colorSquare.setStroke(javafx.scene.paint.Color.BLACK);

                // creo un box con testo e colore
                javafx.scene.layout.HBox container = new javafx.scene.layout.HBox(10);
                Label usernameLabel = new Label(username);
                container.getChildren().addAll(colorSquare, usernameLabel);

                setGraphic(container);
                setStyle("-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.4), 5, 0.3, 0, 0); -fx-padding: 10;");
            }
        }
    });
}
    public void setupTestListView() {
        info.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-border-color: #00ccff; -fx-border-radius: 15; -fx-background-radius: 15; -fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #00ccff; -fx-background-color: rgba(0,30,60,0.8); -fx-effect: dropshadow(gaussian, rgba(0,204,255,0.4), 5, 0.3, 0, 0); -fx-padding: 10;");
                }
            }
        });
    }
    public ListView<String> getLobby() {
        return lobby;
    }
    public ListView<String> getInfo() {
        return info;
    }
    public void showAdmin(){
        labelAdmin.setVisible(true);
    }
    private void disableAll() {
        labelMaxPlayers.setVisible(false);
        textMaxPlayers.setVisible(false);
        textGamemode.setVisible(false);
        buttonFlightMode.setVisible(false);
        buttonLv1.setVisible(false);
        buttonLv2.setVisible(false);
        buttonFlightMode.setVisible(false);
        buttonMaxPlayers.setVisible(false);
        labelAdmin.setVisible(false);
    }
    private void enableAll() {
        labelMaxPlayers.setVisible(true);
        textMaxPlayers.setVisible(true);
        textGamemode.setVisible(true);
        buttonFlightMode.setVisible(true);
        buttonFlightMode.setDisable(false);
        buttonLv1.setDisable(false);
        buttonLv2.setDisable(false);
        buttonLv1.setVisible(true);
        buttonLv2.setVisible(true);
        buttonFlightMode.setVisible(true);
        buttonMaxPlayers.setVisible(true);
        textMaxPlayers.setDisable(true);
        buttonMaxPlayers.setDisable(true);

    }
    private void disableModeButtons() {
        buttonFlightMode.setDisable(true);
        buttonLv1.setDisable(true);
        buttonLv2.setDisable(true);
        textMaxPlayers.setDisable(false);
        buttonMaxPlayers.setDisable(false);
    }

    @FXML
    private void handleJoin(ActionEvent event) {
        try{
            getGui().getController().playerJoin(getGui().getUsername());
            System.out.println("Player joined the lobby: " + getGui().getUsername());
            joinLobby.setDisable(true);
        }catch (Exception e) {
            getGui().printUpdateMessage( getError()+ e.getMessage());
        }
    }

    @FXML
    private void handleSetupGame(ActionEvent event) {
        setupGame.setDisable(true);
        joinLobby.setDisable(true);
        enableAll();
    }
    @FXML
    private void handleFlightMode(ActionEvent event) {
        mode = GameMode.FLIGHT_MODE;
        disableModeButtons();
        System.out.println("Flight mode selected");
    }
    @FXML
    private void handleLv1(ActionEvent event) {
        mode = GameMode.LEVEL_1;
        disableModeButtons();
        System.out.println("Level 1 mode selected");
    }
    @FXML
    private void handleLv2(ActionEvent event) {
        mode = GameMode.LEVEL_2;
        disableModeButtons();
        System.out.println("Level 2 mode selected");
    }

    @FXML
    private void handleMaxPlayers(ActionEvent event) {
        try {

            int maxPlayers = Integer.parseInt(textMaxPlayers.getText());
            if (maxPlayers < 2 || maxPlayers > 4) {
                getGui().printUpdateMessage(getError()+ " Max players must be between 2 and 4");
                textMaxPlayers.setText("");
            }
            else{
                try{
                    getGui().getController().adminSetup(maxPlayers, mode.ordinal());
                    System.out.println("Max players set to: " + maxPlayers + " with mode: " + mode);
                    textMaxPlayers.setDisable(true);
                    buttonMaxPlayers.setDisable(true);
                    joinLobby.setDisable(false);
                    disableAll();
                }catch (Exception e) {
                    getGui().printUpdateMessage(getError()+  e.getMessage());
                    disableAll();
                    textMaxPlayers.setText("");
                    setupGame.setDisable(false);
                    joinLobby.setDisable(false);
                }
            }

        } catch (NumberFormatException e) {
            getGui().printUpdateMessage(getError()+" Invalid number format for max players");
            textMaxPlayers.setText("");
        }
    }


}
