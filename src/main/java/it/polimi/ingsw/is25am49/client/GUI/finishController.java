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

    public void initialize() {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/polimi/ingsw/is25am49/podio.png")));
        podio.setImage(image);
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
            calculateScore.setVisible(true);
        }catch (Exception e) {
            guiApplication.printUpdateMessage(error + e.getMessage());
        }
    }
    public void printPodium(){
        List<Player> finalRanking = gameSession.getLeaderBoard().stream().map(Map.Entry::getKey).sorted(Comparator.comparingInt(Player::getCredits).reversed()).toList();
        firstPosition.setText("1st: " + finalRanking.get(0).getUsername() + " - " + finalRanking.get(0).getCredits() + " credits");
        ImageView playerIcon;
        Color playerColor = gameSession.getPlayer(finalRanking.get(0).getIdPlayer()).get().getColor();

        switch (playerColor) {
            case RED:
                playerIcon = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/red_astronaut.png").toExternalForm()));
                break;
            case BLUE:
                playerIcon = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/blue_astronaut.png").toExternalForm()));
                break;
            case GREEN:
                playerIcon = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/green_astronaut.png").toExternalForm()));
                break;
            case YELLOW:
                playerIcon = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/yellow_astronaut.png").toExternalForm()));
                break;
            default:
                playerIcon = null;
        }
        playerIcon.setFitWidth(34);
        playerIcon.setFitHeight(55);
        firstPosition.setGraphic(playerIcon);
        secondPosition.setText("2nd: " + finalRanking.get(1).getUsername() + " - " + finalRanking.get(1).getCredits() + " credits");
        ImageView playerIcon2;
        Color playerColor2 = gameSession.getPlayer(finalRanking.get(1).getIdPlayer()).get().getColor();

        switch (playerColor2) {
            case RED:
                playerIcon2 = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/red_astronaut.png").toExternalForm()));
                break;
            case BLUE:
                playerIcon2 = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/blue_astronaut.png").toExternalForm()));
                break;
            case GREEN:
                playerIcon2 = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/green_astronaut.png").toExternalForm()));
                break;
            case YELLOW:
                playerIcon2 = new ImageView(new Image(getClass().getResource("/it/polimi/ingsw/is25am49/tokens/yellow_astronaut.png").toExternalForm()));
                break;
            default:
                playerIcon2 = null;
        }
        playerIcon2.setFitWidth(34);
        playerIcon2.setFitHeight(55);
        secondPosition.setGraphic(playerIcon2);
    }
}
