package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisComponentException;

import java.util.ArrayList;

public class SpecialComponent extends Component {
    private boolean isActive;
    public SpecialComponent(ComponentType type, int level) {
        super(type, level);
        this.isActive = false;
    }

    @Override
    public ArrayList<Token> getTokenList() {
        throw new IllegalMethodOnThisComponentException("getTokenList non supported su SpecialComponent");
    }
    @Override
    public int getNumberOfTokens() {
        throw new IllegalMethodOnThisComponentException("getNumberOfToken non supported su SpecialComponent");
    }
    @Override
    public int getNumberOfaToken(Token token) {
        throw new IllegalMethodOnThisComponentException("checkIfContainsToken non supported su SpecialComponent");
    }
    @Override
    public boolean checkIfTokenIsValid(Token token) {
        throw new IllegalMethodOnThisComponentException("checkIfTokenIsValid non supported su SpecialComponent");
    }
    @Override
    public void addToken(Token token) {
        throw new IllegalMethodOnThisComponentException("addToken non supported su SpecialComponent");
    }
    @Override
    public void removeToken(Token token) {
        throw new IllegalMethodOnThisComponentException("removeToken non supported su SpecialComponent");
    }
    @Override
    public void removeToken() {
        throw new IllegalMethodOnThisComponentException("removeToken non supported su SpecialComponent");
    }
    @Override
    public void fillComponent(Token token) {
        throw new IllegalMethodOnThisComponentException("fillComponent non supported su SpecialComponent");
    }
    @Override
    public void clearComponent() {
        throw new IllegalMethodOnThisComponentException("clearComponent non supported su SpecialComponent");
    }


    @Override
    public boolean getIsActive() {
        return isActive;
    }
    @Override
    public void turnOn() {
        isActive = true;
    }
    @Override
    public void turnOff() {
        isActive = false;
    }
}
