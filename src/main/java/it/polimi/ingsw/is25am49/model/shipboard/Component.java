package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;

import java.util.ArrayList;

public abstract class  Component {
    final protected ComponentType type;
    final protected int level;

    public Component(ComponentType type, int level) {
        this.type = type;
        this.level = level;
    }

    public int getLevel(){
        return level;
    }
    public ComponentType getType(){return type;}

    public abstract ArrayList<Token> getTokenList();
    public abstract int getNumberOfTokens();
    public abstract int getNumberOfaToken(Token token);
    public abstract boolean checkIfTokenIsValid(Token token);
    public abstract void addToken(Token token);
    public abstract void removeToken(Token token);
    public abstract void removeToken();
    public abstract void fillComponent(Token token);
    public abstract void clearComponent();


    public abstract boolean getIsActive();
    public abstract void turnOn();
    public abstract void turnOff();
}
