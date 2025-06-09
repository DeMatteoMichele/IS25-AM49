package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.ClientController;
import it.polimi.ingsw.is25am49.client.ClientRMI;
import it.polimi.ingsw.is25am49.client.ClientSerializer;
import it.polimi.ingsw.is25am49.client.ServerHandler;
import it.polimi.ingsw.is25am49.client.ClientRMI;
import it.polimi.ingsw.is25am49.shared.RemoteClient;
import it.polimi.ingsw.is25am49.shared.RemoteServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class settingsController extends GenericSceneController {
    private final String serverIP = "localhost";
    private final int serverPortSocket = 8081;

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
    public void enableRetryVisibility() {
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
    public void disableConnectionButtons() {
        rmiButton.setDisable(true);
        socketButton.setDisable(true);
    }
    @FXML
    private void enableConnectionButtons() {
        rmiButton.setDisable(false);
        socketButton.setDisable(false);
    }

    @FXML
    public void handleReconnection(ActionEvent event) {
        usernameLabel.setText("Please select your EXACT! previous username: ");
        isAlreadyConnected = true;
        disableReconnectionButtons();
    }

    @FXML
    public void handleUsername(ActionEvent event) {
        username = usernameTextField.getText().trim();

        if (username.isEmpty()) {
            guiApplication.printUpdateMessage(error+ " Please enter a username!");
        } else if (username.length()>20) {
            guiApplication.printUpdateMessage(error + " Username too long! Maximum length is 20 characters.");
            usernameTextField.setText("");
        } else if (username.contains(" ")) {
            guiApplication.printUpdateMessage( error+ " Username contains spaces!");
        } else {
            System.out.println("Username set to: " + username);
            guiApplication.setUsername(username);
            guiApplication.getController().setUsername(username);
            if (!isAlreadyConnected) {
                System.out.println("entrato");
                guiApplication.getController().createMyIdJson();
            }
            usernameTextField.setDisable(true);
            usernameButton.setDisable(true);
            rmiButton.setDisable(false);
            socketButton.setDisable(false);
        }
    }

    @FXML
    public void handleRMI(ActionEvent event) {
        try {
            try {
                ClientRMI clientRMI = null;
                guiApplication.connectionRMI(false, clientRMI);

            }catch (RemoteException e) {

                    System.out.println("errore RMI");
                    guiApplication.printUpdateMessage(error + "Error connecting to the server");
                    disableConnectionButtons();
                    enableRetryVisibility();
                }

        } catch (Exception e) {
            System.out.println("errore RMI");
            guiApplication.printUpdateMessage( error + " Error connecting to the server");
            disableConnectionButtons();
            enableRetryVisibility();
        }
    }


    @FXML
    public void handleSocket(ActionEvent event) {


        try {
            ClientController controller = guiApplication.getController();
            ClientSerializer clientSerializer = new ClientSerializer(controller);
            ServerHandler serverHandler = new ServerHandler(serverIP, serverPortSocket);
            serverHandler.setClientSerializer(clientSerializer);
            clientSerializer.setServerHandler(serverHandler);
            controller.setServerMethods(clientSerializer);
            controller.setServerHandler(serverHandler);
            controller.registerClientSocket();
            disableConnectionButtons();
            System.out.println("Socket connection selected");
//            guiApplication.showScene(ScenesName.INIT);


        } catch (Exception e) {
            System.out.println("errore");
            guiApplication.printUpdateMessage(error + " Error connecting to the server");
            disableConnectionButtons();
            enableRetryVisibility();
        }


    }

    @FXML
    private void noRetry(ActionEvent event) {
        disableAll();
        usernameTextField.setText("");

        guiApplication.showScene(ScenesName.HOME);

    }

    @FXML
    private void yesRetry(ActionEvent event) {
        disableRetryVisibility();
        enableConnectionButtons();
    }
}
