package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.model.exceptions.FinishDeckExceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AdventureCardDeck {
    private final List<AdventureCard> deck = new ArrayList<>();
    private boolean isBeingLooked;


    public AdventureCard draw() throws FinishDeckExceptions{

        if(deck.isEmpty()){
            throw new FinishDeckExceptions("Il deck è vuoto");
        }
        return deck.remove(0);
    }
    public void generateDeck(){

    }
    public void shuffle(){
        Collections.shuffle(deck);
    }

    public void showCards(Player player){

    }


}
