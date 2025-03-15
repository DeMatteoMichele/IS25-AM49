package it.polimi.ingsw.is25am49.model.states.decisionstates;

import it.polimi.ingsw.is25am49.model.GameSession;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisStateException;
import it.polimi.ingsw.is25am49.model.states.AdventureState;

import java.awt.*;

public abstract class DecisionState extends AdventureState {
    public DecisionState(GameSession gameSession){
        super(gameSession);
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
