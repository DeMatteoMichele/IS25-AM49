package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisComponent;

import java.util.ArrayList;

public class SpecialComponent extends Component {
    private boolean isActive;
    public SpecialComponent(ComponentType type, int level) {
        super(type, level);
        this.isActive = false;
    }

    @Override
    public ArrayList<Token> getTokenList() {
        throw new IllegalMethodOnThisComponent("getTokenList non supported su SpecialComponent");
    }
    @Override
    public int getNumberOfToken() {
        throw new IllegalMethodOnThisComponent("getNumberOfToken non supported su SpecialComponent");
    }
    @Override
    public boolean checkIfTokenIsValid(Token token) {
        throw new IllegalMethodOnThisComponent("checkIfTokenIsValid non supported su SpecialComponent");
    }
    @Override
    public void addToken(Token token) {
        throw new IllegalMethodOnThisComponent("addToken non supported su SpecialComponent");
    }
    @Override
    public void removeToken(Token token) {
        throw new IllegalMethodOnThisComponent("removeToken non supported su SpecialComponent");
    }
    @Override
    public void removeToken() {
        throw new IllegalMethodOnThisComponent("removeToken non supported su SpecialComponent");
    }
    @Override
    public void fillComponent(Token token) {
        throw new IllegalMethodOnThisComponent("fillComponent non supported su SpecialComponent");
    }
    @Override
    public void clearComponent() {
        throw new IllegalMethodOnThisComponent("clearComponent non supported su SpecialComponent");
    }

    @Override
    public boolean checkIfContainsToken(Token token) {
        throw new IllegalMethodOnThisComponent("checkIfContainsToken non supported su SpecialComponent");
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
