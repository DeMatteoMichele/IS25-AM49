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
    public void setupLobbyListView() {
        lobby.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-background-color: #2b2b2b; -fx-text-fill: #ffcc00; -fx-font-size: 16; -fx-font-weight: bold; -fx-padding: 10; -fx-border-color: #ffcc00; -fx-border-radius: 10; -fx-background-radius: 10;");
                }
            }
        });
    }
    public ListView<String> getLobby() {
        return lobby;
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
            getGui().showError(getStage(), e.getMessage());
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
                getGui().showError(getStage(), "Max players must be between 2 and 4");
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
                    getGui().showError(getStage(), e.getMessage());
                    disableAll();
                    textMaxPlayers.setText("");
                    setupGame.setDisable(false);
                    joinLobby.setDisable(false);
                }
            }

        } catch (NumberFormatException e) {
            getGui().showError(getStage(), "Invalid number format for max players");
            textMaxPlayers.setText("");
        }
    }


}
