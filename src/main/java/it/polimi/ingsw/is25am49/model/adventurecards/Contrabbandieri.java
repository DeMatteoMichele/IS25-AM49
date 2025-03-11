package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import javafx.util.Pair;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Contrabbandieri extends AdventureCard {
    private int minimumFirePower;
    private boolean canBeDefeated;
    private final List<Token> LostTokens=new ArrayList<>();
    private final List<Token> Prize=new ArrayList<>();

    public Contrabbandieri(String description, int daysLost, int level, int creditsEarned, List<CardEffect> ListEffects) {
        super(description, daysLost, level, creditsEarned, ListEffects);
        canBeDefeated=false;
    }

    @Override
    public void checkRequirements(Player player) {
        int firePower=player.get_shipBoard().getTotalFirePower();
        if (firePower>=minimumFirePower)
            canBeDefeated=true;
    }

    @Override
    public void askForDecision(Player player) {

    }

    public void cycleEffects(Player player) {
        for(CardEffect e:ListEffects){
            e.action(player);
        }
    }

    @Override
    public void applyEffect(ArrayList<Pair<Integer,Player>> Leaderboard) {
        System.out.println("I Contrabbandieri ti attaccano!");
        for (Pair<Integer,Player> pair : Leaderboard) {
            Player p= pair.getValue();
            checkRequirements(p);
            ShipBoard shipboard=p.get_shipBoard();
            if(canBeDefeated){
            System.out.println("Hai" + shipboard.getTotalFirePower()+ " potenza di fuoco, puoi sconfiggere i contrabbandieri!");
            System.out.println("Hai sconfitto i contrabbandieri!");
            break;
            }
            else{
                System.out.println("Non hai abbastanza potenza di fuoco!");
                if(shipboard.getTotalNumberOfaToken(Token.BATTERY_CELLS)>0){
                    List<Point> ActivatableCannons=getActivatable(ComponentType.CANNON);


                }
                else {
                    System.out.println("Non puoi attivare altri cannoni! Verrai attaccato dai Contrabbandieri!");
                    cycleEffects(p);
                }
            }
        }
    }

    public void setMinimumFirePower(int minimumFirePower) {
        this.minimumFirePower = minimumFirePower;
    }

    public void addAllPrizes(List<Token> Prize) {
        this.Prize.addAll(Prize);
    }

    public void add_all_lost_tokens(List<Token> LostTokens) {
        this.LostTokens.addAll(LostTokens);
    }


}
