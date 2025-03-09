package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;

import java.util.ArrayList;
import java.util.Set;

public abstract class  Component {
    final protected ComponentType type;
    final protected int level;
    final protected ArrayList<Token> tokens;
    protected Boolean isActive;

    public Component(ComponentType type, int level) {
        this.type = type;
        this.level = level;
        this.tokens = new ArrayList<>();
        this.isActive = false;
    }

    public int get_level(){
        return level;
    }

    public ArrayList<Token> get_token_list(){
        return tokens;
    }
    public int get_number_of_token(){
        return tokens.size();
    }

    public Boolean is_active() {
        return isActive;
    }
    public void turn_on(){
        isActive = true;
    }
    public void turn_off(){
        isActive = false;
    }

}
