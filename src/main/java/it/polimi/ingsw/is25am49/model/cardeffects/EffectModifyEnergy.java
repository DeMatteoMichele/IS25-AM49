package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalComponentTypeException;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisCardException;

import java.awt.*;

public class EffectModifyEnergy extends CardEffect {
    private int energy;
    private Point coordinate;
    public EffectModifyEnergy(String name_effect, int energy) {

        super(name_effect);
        this.energy = energy;
    }
    public void action(Player player) {
        while(player.getShipBoard().getTotalNumberOfaToken(Token.BATTERY_CELLS)!=0 && energy!=0){
            if(player.getShipBoard().getTile(coordinate).getComponent().getType()== ComponentType.BATTERY) {
                player.getShipBoard().removeTokenFromTile(coordinate, Token.BATTERY_CELLS);
                energy--;
            }
            else{
                throw new IllegalComponentTypeException("This Component doesn't have battery cells");
            }
        }
    }
    public void setPosition(int position) {
        throw new IllegalMethodOnThisCardException("Illegal Method On This Card");
    }
}
