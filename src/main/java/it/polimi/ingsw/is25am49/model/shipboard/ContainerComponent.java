package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;

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
    public void addToken(Token token) {tokens.add(token);}
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
    public boolean getIsActive() {
        // lancio eccezione??
        return false;
    }
    @Override
    public void turnOn() {
        // lancio eccezione??
    }
    @Override
    public void turnOff() {
        // lancio eccezione??
    }
}
