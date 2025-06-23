package it.polimi.ingsw.is25am49.client.GUI;

import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.shared.enums.Color;
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

public class finishController extends GenericSceneController{
    @FXML
    private Button calculateScore;
    @FXML
    private ImageView podio;
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
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/is25am49/podio.png")));
        podio.setImage(image);
        Image image2 = new Image(getClass().getResource("/sfondo3.jpg").toExternalForm());
        backgroundImage.setImage(image2);
        backgroundImage.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                backgroundImage.fitWidthProperty().bind(newScene.widthProperty());
                backgroundImage.fitHeightProperty().bind(newScene.heightProperty());
            }
        });
    }
    public void calculateScore(ActionEvent event) {
        try{
            guiApplication.getController().adminCalculateScore(guiApplication.getMyId());
        }catch(Exception e){
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }
    public void endGame(ActionEvent event) {
        try {
            guiApplication.getController().adminEndGame(guiApplication.getMyId());
        }catch (Exception e) {
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }
    public void printPodium(){
        List<Player> finalRanking = gameSession.getLeaderBoard().stream().map(Map.Entry::getKey).sorted(Comparator.comparingInt(Player::getCredits).reversed()).toList();
        firstPosition.setText("1st: " + finalRanking.get(0).getUsername() + " - " + finalRanking.get(0).getCredits() + " credits");
        ImageView playerIcon= getPlayerIcon(finalRanking.get(0).getIdPlayer());
        firstPosition.setGraphic(playerIcon);
        secondPosition.setText("2nd: " + finalRanking.get(1).getUsername() + " - " + finalRanking.get(1).getCredits() + " credits");
        ImageView playerIcon2 = getPlayerIcon(finalRanking.get(1).getIdPlayer());
        secondPosition.setGraphic(playerIcon2);
        if(finalRanking.size()>2){
            thirdPosition.setText("3rd: " + finalRanking.get(2).getUsername() + " - " + finalRanking.get(2).getCredits() + " credits");
            ImageView playerIcon3 = getPlayerIcon(finalRanking.get(2).getIdPlayer());
            thirdPosition.setGraphic(playerIcon3);
        }
        if(finalRanking.size()>3){
            fourthPosition.setText("4th: " + finalRanking.get(3).getUsername() + " - " + finalRanking.get(3).getCredits() + " credits");
            ImageView playerIcon4 = getPlayerIcon(finalRanking.get(3).getIdPlayer());
            fourthPosition.setGraphic(playerIcon4);
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
