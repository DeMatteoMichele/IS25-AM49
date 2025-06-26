package it.polimi.ingsw.is25am49.server.model;

import it.polimi.ingsw.is25am49.server.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.server.model.exceptions.FinishDeckExceptions;

import java.util.*;
import java.util.stream.Stream;


/**
 * Represents a deck of AdventureCard objects. Provides functionality to add, shuffle,
 * draw, and manage cards within the deck, as well as observing the deck's state.
 */
public class AdventureCardDeck {
    /**
     * Represents a collection of {@link AdventureCard} instances.
     * The deck is immutable and initialized at creation time.
     */
    private final List<AdventureCard> deck;
    /**
     * Indicates whether the deck is currently being watched.
     */
    private boolean isBeingWatched;
    /**
     * Constructs a new AdventureCardDeck with an empty card deck.
     * The deck is not initially being watched.
     */
    public AdventureCardDeck() {
        deck = new ArrayList<>();
        isBeingWatched = false;
    }
    /**
     * Checks if the deck is currently being watched.
     *
     * @return true if the deck is being watched, false otherwise
     */
    public boolean isWatched() { return isBeingWatched; }
    /**
     * Sets the deck's watch status to active.
     */
    public void watch(){ isBeingWatched = true; }
    /**
     * Stops watching the AdventureCardDeck.
     * Sets the watching state to false.
     */
    public void stopWatch(){ isBeingWatched = false; }
    /**
     * Draws the first card from the AdventureCardDeck.
     * Removes the card from the deck after drawing it.
     *
     * @throws FinishDeckExceptions if the deck is empty.
     */
    public void draw() {
        if(deck.isEmpty()){
            throw new FinishDeckExceptions("Il deck è vuoto");
        }
        deck.getFirst().drawCard();
        deck.removeFirst();
        //return deck.removeFirst();
    }
    /**
     * Adds an AdventureCard to the deck.
     *
     * @param card the AdventureCard to be added to the deck
     */
    public void add(AdventureCard card){
        deck.add(card);
    }
    /**
     * Randomly shuffles the cards in the deck using the specified random generator.
     *
     * @param random the random generator to use for shuffling the deck
     */
    public void shuffle(Random random) {
        Collections.shuffle(deck,random);
    }
    /**
     * Returns the number of AdventureCard objects currently in the deck.
     *
     * @return the number of cards in the deck
     */
    public int size() {
        return deck.size();
    }
    /**
     * Provides a sequential {@link Stream} for accessing the AdventureCard objects in the deck.
     *
     * @return a sequential {@link Stream} of AdventureCard objects contained in the deck
     */
    public Stream<AdventureCard> stream() {
        return deck.stream();
    }

    /**
     * Retrieves the first AdventureCard from the deck without removing it.
     *
     * @return the first AdventureCard in the deck
     * @throws NoSuchElementException if the deck is empty
     */
    public AdventureCard getFirstCard() {
        return deck.getFirst();
    }


    /**
     * Retrieves an AdventureCard at the specified index from the deck.
     *
     * @param i the index of the card to be retrieved, must be within the range of 0 (inclusive)
     *          and the size of the deck (exclusive)
     * @return the AdventureCard located at the specified index
     */
    public AdventureCard getCard(int i) {
        if (i < 0 || i >= deck.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + i);
        }
        return deck.get(i);
    }

    /**
     * Returns a string representation of the AdventureCardDeck, with each card in the deck
     * represented by its string representation on a new line.
     *
     * @return a string representation of the deck, where each AdventureCard is
     *         described on a separate line.
     */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(AdventureCard card : deck) {
            sb.append(card.toString()).append("\n");
        }
        return sb.toString();
    }
}
