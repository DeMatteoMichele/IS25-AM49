package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.enums.Token;

import java.util.ArrayList;
import java.util.List;

public class Pianeta {
    private boolean isOccupied;
    private int playernumber;
    private final List<Token> Prize;

    public Pianeta(List<Token> Prize) {
        this.isOccupied = false;
        this.Prize =new ArrayList<>(Prize);
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public int getPlayernumber() {
        return playernumber;
    }
    public void setPlayernumber(int playernumber) {
        this.playernumber = playernumber;
    }

    public List<Token> getPrize() {
        return Prize;
    }



}
