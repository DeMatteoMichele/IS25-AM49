package it.polimi.ingsw.is25am49.model.states;

import it.polimi.ingsw.is25am49.model.GameSession;
import it.polimi.ingsw.is25am49.model.Player;

import java.awt.*;

public class DrawState extends AdventureState {
    public DrawState(GameSession gameSession){
        super(gameSession);
    }
    public void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex) {}
    public void userStopLookSmallAdventureCardDeck(Player player,int smallDeckIndex) {}
    public void userPlaceTimer() {}
    public void userThrowDices(Player player) {}


    public void userDrawAdventureCard(Player player) {}
    public void userPlayAdventureCard(Player player) {}
    public void userFinishDecision(Player player) {}
    public void userActivateComponent(Player player, Point coordinates) {}


    public void userLandOnPlanet(Player player) {

    }


    public void userLandOnShip(Player player) {

    }

    public void userActivateDaysUpdate() {}
    public void userQuitGame(Player player) {}

}
