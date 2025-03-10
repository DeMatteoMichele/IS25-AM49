package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;

import java.util.ArrayList;

public class BasicComponent extends Component {

    public BasicComponent(ComponentType type, int level) {
        super(type, level);
    }

    @Override
    public ArrayList<Token> getTokenList() {
        //LANCIO ECCEZIONE??
        return null;
    }
    @Override
    public int getNumberOfToken() {
        //LANCIO ECCEZIONE??
        return 0;
    }
    @Override
    public void addToken(Token token) {
        //LANCIO ECCEZIONE??
    }
    @Override
    public void removeToken(Token token) {
        //LANCIO ECCEZIONE??
    }
    @Override
    public void removeToken() {
        //LANCIO ECCEZIONE??
    }
    @Override
    public void fillComponent(Token token) {
        //LANCIO ECCEZIONE??
    }

    @Override
    public boolean getIsActive() {
        //LANCIO ECCEZIONE??
        return false;
    }
    @Override
    public void turnOn() {
        //LANCIO ECCEZIONE??
    }
    @Override
    public void turnOff() {
        //LANCIO ECCEZIONE??
    }
}
