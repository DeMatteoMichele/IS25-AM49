package it.polimi.ingsw.is25am49.server.model.shipboard;

import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.server.model.exceptions.IllegalMethodOnThisComponentException;

import java.util.ArrayList;

public final class SpecialComponent extends Component {
    private boolean isActive;
    public SpecialComponent(ComponentType type, int level) {
        super(type, level);
        this.isActive = false;
    }

    @Override
    public ArrayList<Token> getTokenList() {
        throw new IllegalMethodOnThisComponentException("getTokenList is not supported on SpecialComponent");
    }
    @Override
    public int getNumberOfTokens() {
        throw new IllegalMethodOnThisComponentException("getNumberOfToken is not supported on SpecialComponent");
    }
    @Override
    public int getNumberOfaToken(Token token) {
        throw new IllegalMethodOnThisComponentException("checkIfContainsToken is not supported on SpecialComponent");
    }
    @Override
    public boolean checkIfTokenIsValid(Token token) {
        throw new IllegalMethodOnThisComponentException("checkIfTokenIsValid is not supported on SpecialComponent");
    }
    @Override
    public void addToken(Token token) {
        throw new IllegalMethodOnThisComponentException("addToken is not supported on SpecialComponent");
    }
    @Override
    public void removeToken(Token token) {
        throw new IllegalMethodOnThisComponentException("removeToken is not supported on SpecialComponent");
    }
    @Override
    public void removeToken() {
        throw new IllegalMethodOnThisComponentException("removeToken is not supported on SpecialComponent");
    }
    @Override
    public void fillComponent(Token token) {
        throw new IllegalMethodOnThisComponentException("fillComponent is not supported on SpecialComponent");
    }
    @Override
    public void clearComponent() {
        throw new IllegalMethodOnThisComponentException("clearComponent is not supported on SpecialComponent");
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

    @Override
    public int getFirePower() {return isActive? super.getFirePower():0;}
    @Override
    public int getEnginePower() {return isActive? super.getEnginePower():0;}
}
