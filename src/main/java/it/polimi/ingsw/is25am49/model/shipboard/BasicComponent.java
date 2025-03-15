package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisComponentException;

import java.util.ArrayList;

public class BasicComponent extends Component {

    public BasicComponent(ComponentType type, int level) {
        super(type, level);
    }

    @Override
    public ArrayList<Token> getTokenList() {
        throw new IllegalMethodOnThisComponentException("getTokenList non supported su BasicComponent");
    }
    @Override
    public int getNumberOfTokens()  {
        throw new IllegalMethodOnThisComponentException("getNumberOfToken non supported su BasicComponent");
    }
    @Override
    public int getNumberOfaToken(Token token) {
        throw new IllegalMethodOnThisComponentException("checkIfContainsToken non supported su BasicComponent");
    }
    @Override
    public boolean checkIfTokenIsValid(Token token) {
        throw new IllegalMethodOnThisComponentException("checkIfTokenIsValid non supported su BasicComponent");
    }
    @Override
    public void addToken(Token token) {
        throw new IllegalMethodOnThisComponentException("addToken non supported su BasicComponent");
    }
    @Override
    public void removeToken(Token token) {
        throw new IllegalMethodOnThisComponentException("removeToken non supported su BasicComponent");
    }
    @Override
    public void removeToken() {
        throw new IllegalMethodOnThisComponentException("removeToken non supported su BasicComponent");
    }
    @Override
    public void fillComponent(Token token) {
        throw new IllegalMethodOnThisComponentException("fillComponent non supported su BasicComponent");
    }
    @Override
    public void clearComponent() {
        throw new IllegalMethodOnThisComponentException("clearComponent non supported su BasicComponent");
    }

    @Override
    public boolean getIsActive() {
        throw new IllegalMethodOnThisComponentException("getIsActive non supported su BasicComponent");
    }
    @Override
    public void turnOn() {
        throw new IllegalMethodOnThisComponentException("turnOn non supported su BasicComponent");
    }
    @Override
    public void turnOff() {
        throw new IllegalMethodOnThisComponentException("turnOff non supported su BasicComponent");
    }
}
