package it.polimi.ingsw.is25am49.model;
import it.polimi.ingsw.is25am49.model.states.*;

public class Game {
    private State currentState;
    Game() {
        currentState = new InitState(this);
    }
    public void set_state(State currentState){
        this.currentState = currentState;
    }

}
