package it.polimi.ingsw.is25am49.server.model.shipboard;

import it.polimi.ingsw.is25am49.server.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.server.model.enums.Token;

import java.util.ArrayList;

public sealed abstract class  Component permits BasicComponent, ContainerComponent, SpecialComponent {
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
    public int getFirePower(){
        return switch (type){
            case CANNON,DOUBLE_CANNON -> level;
            default -> 0;
        };
    }
    public int getEnginePower(){
        return switch (type){
            case ENGINE,DOUBLE_ENGINE -> level;
            default -> 0;
        };
    }

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
