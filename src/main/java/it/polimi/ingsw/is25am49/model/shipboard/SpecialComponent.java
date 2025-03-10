package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;

import java.util.ArrayList;

public class SpecialComponent extends Component {
    private Boolean isActive;
    public SpecialComponent(ComponentType type, int level) {
        super(type, level);
        this.isActive = false;
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
    public Boolean getIsActive() {
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
