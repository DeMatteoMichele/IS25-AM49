package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisCardException;

import java.awt.*;

public class EffectModifyCrew extends CardEffect {
    private int lostAstronauts;
    private Point coordinate;
    public EffectModifyCrew(String nameEffect, int lostAstronauts) {

        super(nameEffect);
        this.lostAstronauts = lostAstronauts;
    }
    public void action(Player player) {
        while(player.getShipBoard().getTotalNumberOfaToken(Token.ASTRONAUT)!=0 && lostAstronauts!=0){
            player.getShipBoard().removeTokenFromTile(coordinate, Token.ASTRONAUT);
            lostAstronauts--;
        }
    }
    public void setPosition(int position) {
        throw new IllegalMethodOnThisCardException("Illegal Method on Effect ModifyCrew");
    }
}
