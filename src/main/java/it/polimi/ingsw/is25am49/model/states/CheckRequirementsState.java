package it.polimi.ingsw.is25am49.model.states;

import it.polimi.ingsw.is25am49.model.Game;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;

import java.awt.*;

public class CheckRequirementsState extends AdventureState{
    public CheckRequirementsState(Game game){
        super(game);
    }
    public void userLookSmallAdventureCardDeck(Player player, int smallDeckIndex) throws IllegalMethodOnThisStateException {}
    public void userStopLookSmallAdventureCardDeck(Player player,int smallDeckIndex) throws IllegalMethodOnThisStateException {}
    public void userPlaceTimer(Player player) throws IllegalMethodOnThisStateException {}
    public void userThrowDices(Player player) throws IllegalMethodOnThisStateException {}
    public void userDrawAdventureCard(Player player) throws IllegalMethodOnThisStateException {}
    public void userPlayAdventureCard(Player player) throws IllegalMethodOnThisStateException {}
    public void userEffectDecision(Player player) throws IllegalMethodOnThisStateException {}
    public void userActivateComponent(Player player, Point coordinates) throws IllegalMethodOnThisStateException {}
    public void userActivateDaysUpdate() throws IllegalMethodOnThisStateException {}
    public void userQuitGame(Player player) throws IllegalMethodOnThisStateException {}
}
