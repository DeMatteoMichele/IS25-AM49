package it.polimi.ingsw.is25am49.server.model;

import it.polimi.ingsw.is25am49.server.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.server.model.exceptions.FinishDeckExceptions;

import java.util.*;
import java.util.stream.Stream;


public class AdventureCardDeck {
    private final List<AdventureCard> deck;
    private boolean isBeingWatched;
    public AdventureCardDeck() {
        deck = new ArrayList<>();
        isBeingWatched = false;
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

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(AdventureCard card : deck) {
            sb.append(card.toString()).append("\n");
        }
        return sb.toString();
    }
}
