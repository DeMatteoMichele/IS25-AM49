package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.ClientController;
import it.polimi.ingsw.is25am49.client.ClientRMI;
import it.polimi.ingsw.is25am49.client.ClientSerializer;
import it.polimi.ingsw.is25am49.client.ServerHandler;
import it.polimi.ingsw.is25am49.client.ClientRMI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class settingsController extends GenericSceneController {
    private final String serverIP = "localhost";
    private final int serverPortSocket = 8081;
    private final int serverPortRMI = 1099;
    private String username;
    boolean isAlreadyConnected = false;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private Button yesReconnection;
    @FXML
    private Button noReconnection;
    @FXML
    private Button rmiButton;
    @FXML
    private Button socketButton;
    @FXML
    private Button usernameButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label retryLabel;
    @FXML
    private Button noRetry;
    @FXML
    private Button yesRetry;


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

    private void disableAll() {
        yesReconnection.setDisable(false);
        noReconnection.setDisable(false);
        rmiButton.setDisable(true);
        socketButton.setDisable(true);
        usernameButton.setDisable(true);
        usernameTextField.setDisable(true);
        disableRetryVisibility();

    }

    @FXML
    private void disableRetryVisibility() {
        retryLabel.setVisible(false);
        yesRetry.setVisible(false);
        noRetry.setVisible(false);
    }

    @FXML
    private void enableRetryVisibility() {
        retryLabel.setVisible(true);
        yesRetry.setVisible(true);
        noRetry.setVisible(true);
    }

    @FXML
    private void disableReconnectionButtons() {
        yesReconnection.setDisable(true);
        noReconnection.setDisable(true);
        usernameTextField.setDisable(false);
        usernameButton.setDisable(false);
    }

    @FXML
    private void disableConnectionButtons() {
        rmiButton.setDisable(true);
        socketButton.setDisable(true);
    }
    @FXML
    private void enableConnectionButtons() {
        rmiButton.setDisable(false);
        socketButton.setDisable(false);
    }

    @FXML
    private void handleReconnection(ActionEvent event) {
        usernameLabel.setText("Please select your EXACT! previous username: ");
        isAlreadyConnected = true;
        disableReconnectionButtons();
    }

    @FXML
    private void handleUsername(ActionEvent event) {
        username = usernameTextField.getText().trim();
        if (username.isEmpty()) {
            getGui().showError(getStage(), "Please enter a username!");
        } else {
            System.out.println("Username set to: " + username);
            getGui().getController().setUsername(username);
            if (!isAlreadyConnected) {
                System.out.println("entrato");
                getGui().getController().createMyIdJson();
            }
            usernameTextField.setDisable(true);
            usernameButton.setDisable(true);
            rmiButton.setDisable(false);
            socketButton.setDisable(false);
        }
    }

    @FXML
    private void handleRMI(ActionEvent event) {
        try {
            ClientRMI clientRMI = new ClientRMI(getGui().getGameSession());
            ClientController controller = getGui().getController();
            try {
                controller.setServerMethods(clientRMI.connect(serverIP, serverPortRMI));
                try {
                    System.out.println("trying to log in to the server ...");
                    UnicastRemoteObject.exportObject(clientRMI, 0);
                    controller.registerClientRMI(clientRMI);
                    System.out.println("client logged");
                    disableConnectionButtons();
                } catch (RemoteException e) {
                    getGui().showError(getStage(), e.getMessage());
                }
            } catch (RemoteException e) {
                getGui().showError(getStage(), e.getMessage());
            }
        } catch (Exception e) {
            getGui().showError(getStage(), e.getMessage());
        }
    }

    @FXML
    private void handleSocket(ActionEvent event) {


        try {

            ClientController controller = getGui().getController();
            ClientSerializer clientSerializer = new ClientSerializer(controller);
            ServerHandler serverHandler = new ServerHandler(serverIP, serverPortSocket);
            serverHandler.setClientSerializer(clientSerializer);
            clientSerializer.setServerHandler(serverHandler);
            controller.setServerMethods(clientSerializer);
            controller.setServerHandler(serverHandler);
            controller.registerClientSocket();
            disableConnectionButtons();
            System.out.println("Socket connection selected");
            getGui().showScene(ScenesName.INIT);


        } catch (Exception e) {
            System.out.println("errore");
            getGui().showError(getStage(), e.getMessage());
            disableConnectionButtons();
            enableRetryVisibility();
        }


    }

    @FXML
    private void noRetry(ActionEvent event) {
        disableAll();
        usernameTextField.setText("");

        getGui().showScene(ScenesName.HOME);

    }

    @FXML
    private void yesRetry(ActionEvent event) {
        disableRetryVisibility();
        enableConnectionButtons();
    }
}
