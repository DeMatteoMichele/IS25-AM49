package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.exceptions.FinishDeckExceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    public void generateDeck(GameMode gameMode){
        // crea il deck di carte dove ogni carta avrà anche il costruttore di ogni effect.
        //TODO creare i file JSON  e guardare tutorial di come si si accede
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
    public Stream<AdventureCard> stream() {
        return deck.stream();
    }

    public AdventureCard getFirstCard() {
        return deck.getFirst();
    }
}
