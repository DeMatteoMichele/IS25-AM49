package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.model.adventurecards.Contrabbandieri;
import it.polimi.ingsw.is25am49.model.adventurecards.Epidemia;
import it.polimi.ingsw.is25am49.model.adventurecards.Pianeti;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectCannonata;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectModifyGoods;
import it.polimi.ingsw.is25am49.model.exceptions.FinishDeckExceptions;
import javafx.scene.effect.Effect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdventureCardDeckTest {
    private AdventureCardDeck deck2;
    private AdventureCard card;
    private AdventureCard card2;
    private List<CardEffect> effects = new ArrayList<>();
    private EffectCannonata cannonata;
    private AdventureCard cardDrawn;

    @BeforeEach
    void init(){
        deck2 = new AdventureCardDeck();
        effects.add(cannonata);
        card = new Pianeti("pianeti",3,1,20, effects);
        card2 = new Epidemia("epidemia", 2,3,15,effects);


    }


    @Test
    void drawDeckIsEmpty() {
        assertThrows(FinishDeckExceptions.class, ()->deck2.draw()); // per testare l'eccezione, non faccio nessuna add

    }

    @Test
    void draw(){
        deck2.add(card);
        deck2.add(card2);
        //il resto per testare se rimuove la prima adventure card inserita
        cardDrawn = deck2.draw();
        assertEquals("pianeti",cardDrawn.getDescription() );
        assertEquals(card.getLevel(),cardDrawn.getLevel());
        cardDrawn = deck2.draw();
        assertEquals(card2.getDescription(),cardDrawn.getDescription());
    }



    @Test
    void generateDeck() {

    }

    @Test
    void shuffle() {
    }

    @Test
    void showCards() {
    }
}