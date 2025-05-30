package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.*;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.LeaderBoard;
import it.polimi.ingsw.is25am49.client.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import it.polimi.ingsw.is25am49.shared.enums.Token;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.rmi.NotBoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.application.Application;
import org.jline.terminal.TerminalBuilder;

public class GraphicUserInterface extends Application implements UserInterface {
    private ClientController controller;
    private final GameSession gameSession;

    private  String username;
    private Map<Scene, ScenesName> scenesList;
    private Map<GenericSceneController, ScenesName> controllerList;
    private int myId;
    ObservableList<String> players = FXCollections.observableArrayList();
    private Stage stage;



    public GraphicUserInterface(){
            gameSession = new GameSession( this);
    }
    public int getMyId(){
        return myId;
    }
    public GraphicUserInterface getGui() {
            return this;
        }
        public GameSession getGameSession() {
            return gameSession;
        }
        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }

    private void loadStage(Stage primaryStage)  {

        // Load all scenes defined in ScenesName enum using FXMLLoader
        try{
            stage = primaryStage;

            FXMLLoader loader;
            Parent root;
            scenesList = new HashMap<>();
            controllerList = new HashMap<>();
            for (ScenesName scenesName : ScenesName.values()) {
                loader = new FXMLLoader(getClass().getResource(scenesName.getPath()));
                root = loader.load();
                GenericSceneController controller = loader.getController();
                controller.setGui(this,gameSession);
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

     }

      public static void main(String args[]) {
            launch(args);
        }


    public ClientController getController() {
        return controller;
    }


    @Override
    public void updateStateTransition(int stateIndex) {
        buildController buildcontr = (buildController) getControllerBySceneName(ScenesName.BUILD);
        switch (stateIndex) {
            case 1 -> {//BuildState
                System.out.println("BUILD STATE");

                buildcontr.loadTilesMap();
                buildcontr.loadCards();
                Platform.runLater(()-> {
                   // showScene(ScenesName.BUILD);
                });


            }
            case 2->{//Adventure state
                System.out.println("ADVENTURE STATE");

            }
            default -> {
                //;
            }
        }
    }
    public GenericSceneController getControllerBySceneName(ScenesName scenesName) {
        for (Map.Entry<GenericSceneController, ScenesName> entry : controllerList.entrySet()) {
            if (entry.getValue() == scenesName) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public void printUpdateMessage(String msg) {
        if(msg.contains("⚠")){
            showError(stage, msg);
        }
        else{

        }
    }

    @Override
    public void setClientId(int myId) {
        this.myId = myId;
    }

//chiamato quando utente fa join, stampa welcome con il suo nome
    @Override
    public void updateGameSessionNewPlayer(int playerId) {
        initController contr = (initController) getControllerBySceneName(ScenesName.INIT);
        Platform.runLater(() -> {
//            System.out.println("PlayerID: " +playerId);
//            System.out.println("myId: " + myId);
            if (playerId==myId) {
                contr.setWelcome(username);

            }

        });
    }
    //chiamato quando viene creato il game, setta le informazioni sul game e inserisce l'admin
    @Override
    public void updateGameSessionCreateGame(int playerId, GameMode gameMode, int maxPlayers) {
        initController contr = (initController) getControllerBySceneName(ScenesName.INIT);
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
//chiamata quando utente fa join, se è l'admin setta a default le impostazioni, se è un altro player le aggiorna
    @Override
    public void updateGameNewPlayer(int playerId) {
        initController contr = (initController) getControllerBySceneName(ScenesName.INIT);
        contr.setupTestListView();
        Platform.runLater(() -> {
            if (gameSession.getAdmin().map(player -> player.getIdPlayer() == playerId).orElse(false) && gameSession.getGame().isEmpty()) {

                contr.getInfo().getItems().add("Players: 1/4");
                contr.getInfo().getItems().add("Game mode--> DEFAULT");
                contr.getInfo().getItems().add("Max players--> 4 (DEFAULT)");
                if(myId==playerId){
                    contr.showAdmin();
                }
            } else if (gameSession.getGame().isPresent()) {

                contr.setupLobbyListView();
                // cotrollo se è già presnete nella lista non lo inserisco
                boolean playerAlreadyInList = contr.getLobby().getItems().stream()
                        .anyMatch(item -> item.contains(gameSession.getPlayer(playerId).get().getUsername()));

                if (!playerAlreadyInList) {
                    if(gameSession.getAdmin().map(player -> player.getIdPlayer() == playerId).orElse(false)) {
                        contr.getLobby().getItems().add(gameSession.getPlayer(playerId).get().getUsername() + " (ADMIN)    " + gameSession.getPlayer(playerId).get().getColor());
                    }
                    else {
                        contr.getLobby().getItems().add(gameSession.getPlayer(playerId).get().getUsername() + "   " + gameSession.getPlayer(playerId).get().getColor());

                    }}
                if (contr.getInfo().getItems().isEmpty()) {
                   contr.getInfo().getItems().add("Players: " + gameSession.getGame().get().numOfPlayers() + "/" + gameSession.getGame().get().getMaxPlayers());
               } else {
                   contr.getInfo().getItems().set(0, "Players: " + gameSession.getGame().get().numOfPlayers() + "/" + gameSession.getGame().get().getMaxPlayers());
               }
//                contr.getInfo().getItems().add("Players: " + gameSession.getGame().get().numOfPlayers() + "/" + gameSession.getGame().get().getMaxPlayers());



            }
        });
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
        buildController buildController = (buildController) getControllerBySceneName(ScenesName.BUILD);
        FlowPane cardContainer = buildController.getCardContainer();
        for (javafx.scene.Node node : cardContainer.getChildren()) {
            if (node instanceof ImageView) {
                ImageView imageView = (ImageView) node;
                if (imageView.getUserData() != null && (int) imageView.getUserData() == idTIle) {
                    // Rendi la tessera opaca e disabilitata
                    imageView.setDisable(true);
                    imageView.setOpacity(0.5);
                    break;
                }
            }
        }
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
    public void updateGenericCardMessage(int playerId, String message) {

    }

    @Override
    public void updateThrowDices(int playerId, int firstDice, int secondDice) {

    }

    @Override
    public void updateDrawAdventureCard(int playerId, AdventureCard adventureCard) {

    }


    @Override
    public void updateFinishDecisionActivating(int playerId, Set<Point> coordinates) {

    }

    @Override
    public void updateFinishDecisionToken(int playerId, List<Pair<Point, Token>> listOfTokens, int numTokens, boolean removing) {

    }

    @Override
    public void updateDeactivating(int playerId, List<Point> coordinates) {

    }

    @Override
    public void updateLandOnPlanet(int playerId, boolean isLanded, int planetId) {

    }

    @Override
    public void updateLandOnStationOrShip(int playerId, boolean isLanded, String cardId) {

    }

    @Override
    public void updateAcceptPrizes(int playerId, boolean isAccepted) {

    }

    @Override
    public void updateStopFlight(int playerId) {

    }

    @Override
    public void updateEffectModifyCrew(int playerId, Point coordinate, Token token) {

    }

    @Override
    public void updateEffectCannonade(int playerId, Point coordinate) {

    }

    @Override
    public void updateEffectLoseGoods(int playerId, Point coordinate, Token token) {

    }

    @Override
    public void updateEffectWinGoods(int playerId, Point coordinate, Token token) {

    }

    @Override
    public void updateEffectModifyEnergy(int playerId, Point coordinate, Token token) {

    }

    @Override
    public void updateEffectMeteorite(int playerId, Point coordinate, Token token) {

    }

    @Override
    public void updateEarnedCredits(int playerId, int credits) {

    }

    @Override
    public void updateDestroyedTiles(int idPlayer, Set<Point> destroyedTiles) {

    }

    @Override
    public void updateLeaderBoard(LeaderBoard leaderBoard) {

    }

    @Override
    public void connectionRMI(boolean isReconnecting, ClientRMI clientRMI) throws NotBoundException, IOException {

    }

    @Override
    public void setGameSession(GameSession gameSession) {

    }
}
