package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalMethodOnThisCardException;

import java.awt.*;

public class EffectLoseGoods extends CardEffect {
    private int numberOfTokensToLose;
    private Point coordinate;

    public EffectLoseGoods(String name_effect, int numberOfTokensToLose) {

        super(name_effect);
        this.numberOfTokensToLose = numberOfTokensToLose;
    }
    public void action(Player player) {

        while(player.getShipBoard().getTotalNumberOfaToken(Token.RED_GOODS)!=0 && numberOfTokensToLose!=0 ){
                player.getShipBoard().removeTokenFromTile(coordinate, Token.RED_GOODS);
                numberOfTokensToLose--;
        }
        while(player.getShipBoard().getTotalNumberOfaToken(Token.YELLOW_GOODS)!=0 && numberOfTokensToLose!=0 ){
                player.getShipBoard().removeTokenFromTile(coordinate, Token.YELLOW_GOODS);
                numberOfTokensToLose--;

            }

        while(player.getShipBoard().getTotalNumberOfaToken(Token.GREEN_GOODS)!=0 && numberOfTokensToLose!=0 ){
                player.getShipBoard().removeTokenFromTile(coordinate, Token.GREEN_GOODS);
                numberOfTokensToLose--;

            }
        while(player.getShipBoard().getTotalNumberOfaToken(Token.BLUE_GOODS)!=0 && numberOfTokensToLose!=0 ){
                player.getShipBoard().removeTokenFromTile(coordinate, Token.BLUE_GOODS);
                numberOfTokensToLose--;


        }
        if(numberOfTokensToLose != 0){
            while(numberOfTokensToLose!=0 && player.getShipBoard().getTotalNumberOfaToken(Token.BATTERY_CELLS)!=0){
                player.getShipBoard().removeTokenFromTile(coordinate, Token.BATTERY_CELLS);
                numberOfTokensToLose--;
            }
        }

    }
    public void setPosition(int position) {
        throw new IllegalMethodOnThisCardException("Illegal Method On Effect LoseGoods");
    }

}
