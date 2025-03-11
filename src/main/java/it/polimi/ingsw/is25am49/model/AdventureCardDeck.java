package it.polimi.ingsw.is25am49.model;

import it.polimi.ingsw.is25am49.model.adventurecards.AdventureCard;

import java.util.Stack;

public class AdventureCardDeck {
    private final Stack<AdventureCard> deck = new Stack<>();
    private boolean isBeingLooked;


    public AdventureCard draw(){
        return deck.peek();
    }
    public void generateDeck(){

    }
    public void shuffle(){

    }

    public void showCards(Player player){

    }


}
