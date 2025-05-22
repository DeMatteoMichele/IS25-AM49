package it.polimi.ingsw.is25am49.client.model.shipboard;

import it.polimi.ingsw.is25am49.shared.enums.ComponentType;
import it.polimi.ingsw.is25am49.shared.enums.Token;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalMethodOnThisComponentException;
import it.polimi.ingsw.is25am49.client.model.exceptions.IllegalTokenInComponentEcxeption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public final class ContainerComponent extends Component {
    private final ArrayList<Token> tokens;

    public ContainerComponent(ComponentType type, int level) {
        super(type, level);
        this.tokens = new ArrayList<>(level);
    }

    @Override
    public void checkAddToken(Token token) {
        if(checkIfTokenIsValid(token)){
            if (Objects.equals(level, tokens.size())) {
                if (!type.equals(ComponentType.STORAGE) && !type.equals(ComponentType.SPECIAL_STORAGE))
                    throw new ArrayIndexOutOfBoundsException("Cannot add tokens to " + type + ", container is full! level: " + level + " elements: " + tokens.size());
            }
        }else throw new IllegalTokenInComponentEcxeption("Cannot add " + token + " to a " + type);
    }
    @Override
    public void checkRemoveToken(Token token) {
        if(checkIfTokenIsValid(token)){
            if (tokens.isEmpty()) throw new ArrayIndexOutOfBoundsException("container is empty!");
            if (!tokens.contains(token)) throw new IllegalTokenInComponentEcxeption("Token: " + token + " is not in the container!");
        }else throw new IllegalTokenInComponentEcxeption("Cannot have a " + token + " in a " + type);
    }
    @Override
    public boolean checkIfTokenIsValid(Token token) {
        return switch (token) {
            case BATTERY_CELLS -> type.equals(ComponentType.BATTERY);
            case ASTRONAUT, PURPLE_ALIENS, BROWN_ALIENS -> type.equals(ComponentType.CABINE);
            case RED_GOODS -> type.equals(ComponentType.SPECIAL_STORAGE);
            default -> (type.equals(ComponentType.STORAGE) || (type.equals(ComponentType.SPECIAL_STORAGE)));
        };
    }

    @Override
    public ArrayList<Token> getTokenList() {
        return tokens;
    }
    @Override
    public int getNumberOfTokens() {
        return tokens.size();
    }
    @Override
    public int getNumberOfaToken(Token token) {
        return tokens.stream().filter(t -> t.equals(token)).mapToInt(_ -> 1).sum();
    }
    @Override
    public void addToken(Token token) {
        if (!checkIfTokenIsValid(token))
            throw new IllegalTokenInComponentEcxeption("Cannot add " + token + " to a " + type);
        else if (Objects.equals(level, tokens.size())) {
            if (type.equals(ComponentType.STORAGE) || type.equals(ComponentType.SPECIAL_STORAGE)) {
                tokens.remove(Collections.max(tokens));
                tokens.add(token);
            } else throw new ArrayIndexOutOfBoundsException("Cannot add tokens to " + type + ", container is full! level: " + level + " elements: " + tokens.size());
        } else tokens.add(token);
    }

    @Override
    public void removeToken(Token token) {
        if (!checkIfTokenIsValid(token))
            throw new IllegalTokenInComponentEcxeption("Cannot have a " + token + " in a " + type);
        if (tokens.isEmpty()) throw new ArrayIndexOutOfBoundsException("container is empty!");
        if (tokens.contains(token)) tokens.remove(token);
        else throw new IllegalTokenInComponentEcxeption("Token: " + token + " is not in the container!");
    }
    @Override
    public void removeToken() {
        if (tokens.isEmpty()) throw new ArrayIndexOutOfBoundsException("container is empty!");
        else tokens.removeLast();
    }
    @Override
    public void fillComponent(Token token) throws IllegalTokenInComponentEcxeption {
        if (!checkIfTokenIsValid(token))
            throw new IllegalTokenInComponentEcxeption("Cannot add " + token + " to a " + type);
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
