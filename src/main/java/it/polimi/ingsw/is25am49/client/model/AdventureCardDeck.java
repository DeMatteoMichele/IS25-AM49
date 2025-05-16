package it.polimi.ingsw.is25am49.client.model;

import it.polimi.ingsw.is25am49.client.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.client.model.exceptions.FinishDeckExceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;


public class AdventureCardDeck {
    private final List<AdventureCard> deck;
    private boolean isBeingWatched;
    public AdventureCardDeck() {
        deck = new ArrayList<>();
        isBeingWatched = false;
    }
    public List<AdventureCard> getDeck() {
        return deck;
    }
    public boolean isWatched() { return isBeingWatched; }
    public void watch(){ isBeingWatched = true; }
    public void stopWatch(){ isBeingWatched = false; }
    public void draw() {
        if(deck.isEmpty()){
            throw new FinishDeckExceptions("Il deck è vuoto");
        }
        deck.getFirst().drawCard();
        deck.removeFirst();
        //return deck.removeFirst();
    }
    public void add(AdventureCard card){
        deck.add(card);
    }
    public void shuffle(Random random) {
        Collections.shuffle(deck,random);
    }
    public int size() {
        return deck.size();
    }
    public Stream<AdventureCard> stream() {
        return deck.stream();
    }

    public AdventureCard getFirstCard() {
        return deck.getFirst();
    }

    //TODO usato per testare poi si può rimuovere
    public AdventureCard getCard(int i) {
        if (i < 0 || i >= deck.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + i);
        }
        return deck.get(i);
    }
}
