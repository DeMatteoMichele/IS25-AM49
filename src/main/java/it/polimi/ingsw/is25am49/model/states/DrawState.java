package it.polimi.ingsw.is25am49.model.states;

import it.polimi.ingsw.is25am49.model.Game;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import java.awt.*;

public class DrawState extends AdventureState {
    public DrawState(Game game){
        super(game);
    }
    public void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex) {}
    public void userStopLookSmallAdventureCardDeck(Player player,int smallDeckIndex) {}
    public void userPlaceTimer(Player player) {}
    public void userThrowDices(Player player) {}


    public void userDrawAdventureCard(Player player) {}
    public void userPlayAdventureCard(Player player) {}
    public void userEffectDecision(Player player) {}
    public void userActivateComponent(Player player, Point coordinates) {}
    public void userActivateDaysUpdate() {}
    public void userQuitGame(Player player) {}

}
