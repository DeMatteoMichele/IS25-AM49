package it.polimi.ingsw.is25am49.model.cardeffects;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.Token;

public class EffectLoseGoods extends CardEffect {
    int numberOfTokensToLose;
    public EffectLoseGoods(String name_effect, int numberOfTokensToLose) {

        super(name_effect);
        this.numberOfTokensToLose = numberOfTokensToLose;
    }
    public void action(Player player) {
        if(numberOfTokensToLose!=0 && player.getShipBoard().getTotalNumberOfaToken(Token.RED_GOODS)!=0){
            while(player.getShipBoard().getTotalNumberOfaToken(Token.RED_GOODS)!=0 || numberOfTokensToLose!=0 ){
                player.getShipBoard().removeTokenFromTile(coordinate, Token.RED_GOODS);
                numberOfTokensToLose--;
            }
        }

    }
    public void setPosition(int position) {}

}
