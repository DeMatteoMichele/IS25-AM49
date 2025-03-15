package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisComponent;

import java.util.ArrayList;

public class BasicComponent extends Component {

    public BasicComponent(ComponentType type, int level) {
        super(type, level);
    }

    @Override
    public ArrayList<Token> getTokenList() {
        throw new IllegalMethodOnThisComponent("getTokenList non supported su BasicComponent");
    }
    @Override
    public int getNumberOfToken()  {
        throw new IllegalMethodOnThisComponent("getNumberOfToken non supported su BasicComponent");
    }
    @Override
    public boolean checkIfTokenIsValid(Token token) {
        throw new IllegalMethodOnThisComponent("checkIfTokenIsValid non supported su BasicComponent");
    }
    @Override
    public void addToken(Token token) {
        throw new IllegalMethodOnThisComponent("addToken non supported su BasicComponent");
    }
    @Override
    public void removeToken(Token token) {
        throw new IllegalMethodOnThisComponent("removeToken non supported su BasicComponent");
    }
    @Override
    public void removeToken() {
        throw new IllegalMethodOnThisComponent("removeToken non supported su BasicComponent");
    }
    @Override
    public void fillComponent(Token token) {
        throw new IllegalMethodOnThisComponent("fillComponent non supported su BasicComponent");
    }
    @Override
    public void clearComponent() {
        throw new IllegalMethodOnThisComponent("clearComponent non supported su BasicComponent");
    }

    @Override
    public boolean checkIfContainsToken(Token token) {
        throw new IllegalMethodOnThisComponent("checkIfContainsToken non supported su BasicComponent");
    }

    @Override
    public boolean getIsActive() {
        throw new IllegalMethodOnThisComponent("getIsActive non supported su BasicComponent");
    }
    @Override
    public void turnOn() {
        throw new IllegalMethodOnThisComponent("turnOn non supported su BasicComponent");
    }
    @Override
    public void turnOff() {
        throw new IllegalMethodOnThisComponent("turnOff non supported su BasicComponent");
    }
}
