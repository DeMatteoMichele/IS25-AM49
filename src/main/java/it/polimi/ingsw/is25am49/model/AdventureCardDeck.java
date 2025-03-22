package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.model.exceptions.FinishDeckExceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AdventureCardDeck {
    private final List<AdventureCard> deck = new ArrayList<>();
    private boolean isBeingLooked;

    public AdventureCard draw() {
        if(deck.isEmpty()){
            throw new FinishDeckExceptions("Il deck è vuoto");
        }
        deck.getFirst().drawCard();
        return deck.removeFirst();
    }
    public void generateDeck(){
        //crea il deck di carte dove ogni carta avrà anche il costruttore di ogni effect
    }
    public void add(AdventureCard card){
        deck.add(card);
    }
    public void shuffle(){
        Collections.shuffle(deck);
    }

    public void showCards(Player player){

    }


    public int size() {
        return deck.size();
    }
}
