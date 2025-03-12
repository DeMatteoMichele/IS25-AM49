package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectCannonata;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectMeteoriti;
import it.polimi.ingsw.is25am49.model.enums.Orientation;
import it.polimi.ingsw.is25am49.model.enums.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmugglerCardTest {
    private SmugglerCard smugglerCard;
    String description ="CIAO";
    int daysLost = 10;
    int level=4;
    int minimumFirePower = 5;
    int creditsEarned = 40;
    List<CardEffect> ListEffect= new ArrayList<>();
    List<Token> LostTokens=new ArrayList<>();
    List<Token> Prize=new ArrayList<>();





    @BeforeEach
    void init(){
        ListEffect.add(new EffectCannonata("Cannonata", 1, Orientation.NORTH));
        LostTokens.add(Token.BLUE_GOODS);
        Prize.add(Token.RED_GOODS);
        smugglerCard=new SmugglerCard(description,daysLost, level, creditsEarned, minimumFirePower,ListEffect,LostTokens,Prize);
    }

    @Test
    void drawCard(){
        smugglerCard.drawCard();
    }
    @Test
    void checkRequirements() {
    }

    @Test
    void applyEffect() {
    }

    @Test
    void getLostTokens() {
    }

    @Test
    void getPrize() {
    }
}