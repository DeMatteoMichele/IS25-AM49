package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisComponent;

import java.util.ArrayList;
import java.util.Collections;

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
    public void addToken(Token token) {
        try{
            tokens.add(token);
        }catch (IndexOutOfBoundsException e){System.out.println("component pieno\tERROR: " + e.getMessage());}
    }
    @Override
    public void removeToken(Token token) {
        tokens.remove(token);
    }
    @Override
    public void removeToken() {tokens.removeLast();}
    @Override
    public void fillComponent(Token token) {
        Collections.fill(tokens, token);
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
