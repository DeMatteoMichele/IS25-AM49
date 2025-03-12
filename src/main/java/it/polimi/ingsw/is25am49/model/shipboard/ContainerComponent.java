package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisComponent;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalTokenInComponentEcxeption;

import java.util.ArrayList;
import java.util.Objects;

public class ContainerComponent extends Component {
    private final ArrayList<Token> tokens;
    public ContainerComponent(ComponentType type, int level) {
        super(type, level);
        this.tokens = new ArrayList<>(level);
    }

    @Override
    public ArrayList<Token> getTokenList() {
        return tokens;
    }
    @Override
    public int getNumberOfToken() {return tokens.size();}

    @Override
    public boolean checkIfTokenIsValid(Token token) {
        switch (token){
            case BATTERY_CELLS : return type.equals(ComponentType.BATTERY);
            case ASTRONAUT: return type.equals(ComponentType.CABINE);
            case PURPLE_ALIENS: return type.equals(ComponentType.PURPLE_ALIEN_MODULE);
            case BROWN_ALIENS: return type.equals(ComponentType.BROWN_ALIEN_MODULE);
            case RED_GOODS: return type.equals(ComponentType.SPECIAL_STORAGE);
            default: return type.equals(ComponentType.STORAGE);
        }
    }

    @Override
    public void addToken(Token token) {
        if(!checkIfTokenIsValid(token)) throw new IllegalTokenInComponentEcxeption("Cannot add " + token + " to a" + type);
        else if (Objects.equals(level, tokens.size())) throw new IllegalStateException("Cannot add tokens to container, is full!");
        else tokens.add(token);
    }
    @Override
    public void removeToken(Token token) {
        tokens.remove(token);
    }
    @Override
    public void removeToken() {tokens.removeLast();}
    @Override
    public void fillComponent(Token token) {
        if(!checkIfTokenIsValid(token)) throw new IllegalTokenInComponentEcxeption("Cannot add " + token + " to a" + type);
        for (int i = 0; i < level; i++) {
            tokens.add(token);
        }
    }
    @Override
    public void clearComponent() {
        tokens.clear();
    }

    @Override
    public boolean getIsActive() {
        throw new IllegalMethodOnThisComponent("getIsActive non supportato su ContainerComponent");
    }
    @Override
    public void turnOn() {
        throw new IllegalMethodOnThisComponent("turnOn non supportato su ContainerComponent");
    }
    @Override
    public void turnOff() {
        throw new IllegalMethodOnThisComponent("turnOff non supportato su ContainerComponent");
    }
}
