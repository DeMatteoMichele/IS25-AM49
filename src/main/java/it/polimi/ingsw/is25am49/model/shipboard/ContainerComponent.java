package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisComponentException;
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
    public int getNumberOfTokens() {return tokens.size();}
    @Override
    public int getNumberOfaToken(Token token) {
            return tokens.stream().filter(t -> t.equals(token)).mapToInt(t -> 1).sum();
    }

    @Override
    public boolean checkIfTokenIsValid(Token token) {
        switch (token){
            case BATTERY_CELLS : return type.equals(ComponentType.BATTERY);
            case ASTRONAUT: return type.equals(ComponentType.CABINE);
            case PURPLE_ALIENS: return type.equals(ComponentType.PURPLE_ALIEN_MODULE);
            case BROWN_ALIENS: return type.equals(ComponentType.BROWN_ALIEN_MODULE);
            case RED_GOODS: return type.equals(ComponentType.SPECIAL_STORAGE);
            default: return (type.equals(ComponentType.STORAGE) || (type.equals(ComponentType.SPECIAL_STORAGE)));
        }
    }

    @Override
    public void addToken(Token token) {
        if(!checkIfTokenIsValid(token)) throw new IllegalTokenInComponentEcxeption("Cannot add " + token + " to a " + type);
        if (Objects.equals(level, tokens.size())) throw new ArrayIndexOutOfBoundsException("Cannot add tokens to "+type+", container is full! level: " +level + " elements: "+tokens.size());
        else tokens.add(token);
    }
    @Override
    public void removeToken(Token token){
        if(!checkIfTokenIsValid(token))throw new IllegalTokenInComponentEcxeption("Cannot have a " + token + " in a " + type);
        if(tokens.isEmpty()) throw new ArrayIndexOutOfBoundsException("ContainerComponent is empty!");
        if (tokens.contains(token)) tokens.remove(token);
        else throw new IllegalTokenInComponentEcxeption("Token: " + token + " is not in the container!");
    }
    @Override
    public void removeToken() {
        if(tokens.isEmpty()) throw new ArrayIndexOutOfBoundsException("ContainerComponent is empty!");
        else tokens.removeLast();
    }
    @Override
    public void fillComponent(Token token) throws IllegalTokenInComponentEcxeption {
        if(!checkIfTokenIsValid(token)) throw new IllegalTokenInComponentEcxeption("Cannot add " + token + " to a " + type);
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
        throw new IllegalMethodOnThisComponentException("getIsActive is not supported on ContainerComponent");
    }
    @Override
    public void turnOn() {
        throw new IllegalMethodOnThisComponentException("turnOn is not supported on ContainerComponent");
    }
    @Override
    public void turnOff() {
        throw new IllegalMethodOnThisComponentException("turnOff is not supported on ContainerComponent");
    }
}
