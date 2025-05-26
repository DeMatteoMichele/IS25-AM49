package it.polimi.ingsw.is25am49.server.model.shipboard;

import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisComponentException;

import java.util.ArrayList;

public final class BasicComponent extends Component {

    public BasicComponent(ComponentType type, int level) {
        super(type, level);
    }

    @Override
    public ArrayList<Token> getTokenList() {
        throw new IllegalMethodOnThisComponentException("getTokenList is not supported on BasicComponent");
    }
    @Override
    public int getNumberOfTokens()  {
        throw new IllegalMethodOnThisComponentException("getNumberOfToken is not supported on BasicComponent");
    }
    @Override
    public int getNumberOfaToken(Token token) {
        throw new IllegalMethodOnThisComponentException("checkIfContainsToken is not supported on BasicComponent");
    }
    @Override
    public boolean checkIfTokenIsValid(Token token) {
        throw new IllegalMethodOnThisComponentException("checkIfTokenIsValid is not supported on BasicComponent");
    }
    @Override
    public void addToken(Token token) {
        throw new IllegalMethodOnThisComponentException("addToken is not supported on BasicComponent");
    }
    @Override
    public void removeToken(Token token) {
        throw new IllegalMethodOnThisComponentException("removeToken is not supported on BasicComponent");
    }
    @Override
    public void removeToken() {
        throw new IllegalMethodOnThisComponentException("removeToken is not supported on BasicComponent");
    }
    @Override
    public void fillComponent(Token token) {
        throw new IllegalMethodOnThisComponentException("fillComponent is not supported on BasicComponent");
    }
    @Override
    public void clearComponent() {
        throw new IllegalMethodOnThisComponentException("clearComponent is not supported on BasicComponent");
    }

    @Override
    public boolean getIsActive() {
        throw new IllegalMethodOnThisComponentException("getIsActive is not supported on BasicComponent");
    }
    @Override
    public void turnOn() {
        throw new IllegalMethodOnThisComponentException("turnOn is not supported on BasicComponent");
    }
    @Override
    public void turnOff() {
        throw new IllegalMethodOnThisComponentException("turnOff is not supported on BasicComponent");
    }
}
