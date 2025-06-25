package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static javafx.util.Duration.seconds;

public class finishController extends GenericSceneController{
    @FXML
    private Button calculateScore;
    @FXML
    private Button endGame;
    @FXML
    private Label gameFinished;
    @FXML
    private Label firstPosition;
    @FXML
    private Label secondPosition;
    @FXML
    private Label thirdPosition;
    @FXML
    private Label fourthPosition;
    @FXML
    private ImageView backgroundImage;

    public void initialize() {
//        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/is25am49/podio.png")));
//        podio.setImage(image);
        Image image2 = new Image(getClass().getResource("/sfondo3.jpg").toExternalForm());
        backgroundImage.setImage(image2);
        backgroundImage.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                backgroundImage.fitWidthProperty().bind(newScene.widthProperty());
                backgroundImage.fitHeightProperty().bind(newScene.heightProperty());
            }
        });
        gameFinished.setVisible(false);
    }
    public void calculateScore(ActionEvent event) {
        try{
            guiApplication.getController().adminCalculateScore(guiApplication.getMyId());
        }catch(Exception e){
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }
    public void disableCalculateScore() {
        calculateScore.setDisable(true);
        calculateScore.setVisible(false);
    }
    public void endGame(ActionEvent event) {
        try {
            guiApplication.getController().adminEndGame(guiApplication.getMyId());
        }catch (Exception e) {
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }
    public void printEndGame(){
        endGame.setDisable(true);
        endGame.setVisible(false);
        gameFinished.setVisible(true);
        PauseTransition delay = new PauseTransition(seconds(5));
        delay.setOnFinished(event -> Platform.exit());
        delay.play();
    }
    public void printPodium(){
        List<Player> finalRanking = Stream.concat(
                        gameSession.getLeaderBoard().stream().map(Map.Entry::getKey),
                        gameSession.getGame().get().getGameboard().getLandedPlayers().stream()
                )
                .sorted(Comparator.comparingInt(Player::getCredits).reversed())
                .toList();
        firstPosition.setText("1st\uD83E\uDD47:  " + finalRanking.get(0).getUsername() + System.lineSeparator() + finalRanking.get(0).getCredits() + " credits");
        firstPosition.setWrapText(true);
        ImageView playerIcon = getPlayerIcon(finalRanking.get(0).getIdPlayer());
        firstPosition.setGraphic(playerIcon);
        firstPosition.setContentDisplay(javafx.scene.control.ContentDisplay.BOTTOM);
        firstPosition.setAlignment(javafx.geometry.Pos.CENTER);
        secondPosition.setText("2nd\uD83E\uDD48: " + finalRanking.get(1).getUsername() + System.lineSeparator() + finalRanking.get(1).getCredits() + " credits");
        secondPosition.setWrapText(true);
        ImageView playerIcon2 = getPlayerIcon(finalRanking.get(1).getIdPlayer());
        secondPosition.setGraphic(playerIcon2);
        secondPosition.setContentDisplay(javafx.scene.control.ContentDisplay.BOTTOM);
        secondPosition.setAlignment(javafx.geometry.Pos.CENTER);
        if(finalRanking.size()>2){
            thirdPosition.setText("3rd\uD83E\uDD49:    " + finalRanking.get(2).getUsername() + System.lineSeparator() + finalRanking.get(2).getCredits() + " credits");
            ImageView playerIcon3 = getPlayerIcon(finalRanking.get(2).getIdPlayer());
            thirdPosition.setGraphic(playerIcon3);
            thirdPosition.setContentDisplay(javafx.scene.control.ContentDisplay.BOTTOM);
            thirdPosition.setAlignment(javafx.geometry.Pos.CENTER);
        }
        if(finalRanking.size()>3){
            fourthPosition.setText("4th: " + finalRanking.get(3).getUsername() + System.lineSeparator() + finalRanking.get(3).getCredits() + " credits");
            ImageView playerIcon4 = getPlayerIcon(finalRanking.get(3).getIdPlayer());
            fourthPosition.setGraphic(playerIcon4);
            fourthPosition.setContentDisplay(javafx.scene.control.ContentDisplay.BOTTOM);
            fourthPosition.setAlignment(javafx.geometry.Pos.CENTER);
        }
    }
    public ImageView getPlayerIcon(int playerId) {
        Color playerColor = gameSession.getPlayer(playerId).get().getColor();
        ImageView playerIcon;

        switch (playerColor) {
            case RED:
                playerIcon = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Red_astronaut.png").toExternalForm()));
                break;
            case BLUE:
                playerIcon = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Blue_astronaut.png").toExternalForm()));
                break;
            case GREEN:
                playerIcon = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Green_astronaut.png").toExternalForm()));
                break;
            case YELLOW:
                playerIcon = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/Yellow_astronaut.png").toExternalForm()));
                break;
            default:
                playerIcon = null;
        }
        playerIcon.setFitWidth(34);
        playerIcon.setFitHeight(55);
        return playerIcon;
    }
}
