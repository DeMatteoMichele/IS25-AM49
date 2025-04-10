package it.polimi.ingsw.is25am49.server.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.is25am49.server.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.server.model.adventurecards.SabotageCard;
import it.polimi.ingsw.is25am49.server.model.adventurecards.SpazioAperto;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.exceptions.FinishDeckExceptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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


//    public static List<AdventureCard> generateDeck(GameMode gameMode){
//        // crea il deck di carte dove ogni carta avrà anche il costruttore di ogni effect.
//        //TODO creare i file JSON  e guardare tutorial di come si si accede
//        //in base alla gamemode devo decidere che carte aggiungere e poi in base al nome della carta chiamo il costruttore equivalente
//        //per quelli che hanno la lista di effetti prima, per quelle specifiche carte, ne creo una, successivamente
//        //aggiungo i relativi effetti (facendo costruttore) e infine passo la lista come parametro al costruttore della carta
//        //altrimenti, conoscendo la carta, so direttamente gli effetti che ha e li posso settare in automatico
//
//
//
//
//
//        try{
//            File file = new File("src/main/resources/it/polimi/ingsw/is25am49/adventure_card_deck_config.json");
//            if(!file.exists()){throw new FileNotFoundException("File non trovato");
//            }
//            else{
//                List<AdventureCard> adventureCards = new ArrayList<>();
//                ObjectMapper mapper = new ObjectMapper();
//                JsonNode rootNode = mapper.readTree(file);
//
//                for(JsonNode node : rootNode){
//                    if(gameMode == GameMode.FLIGHT_MODE){
//
//                    } else if (gameMode == GameMode.LEVEL_1) {
//
//                    }
//                    else{
//
//                    }
//                    JsonNode level = node.get("level");
//                    JsonNode description = node.get("description");
//                    if (level == null || description == null) {
//                        System.err.println("Attenzione: Nodo JSON mancante di campi obbligatori: " + node.toString());
//                        continue;
//                    }
//
//                    int id = level.asInt();
//                    String name = description.asText();
//
//                    AdventureCard card = switch (name){
//                        case "SabotageCard" -> new SabotageCard(new GameBoard(), name, id);
//                        default -> new SpazioAperto(new GameBoard(),name,id);
//                    };
//                    adventureCards.add(card);
//                }
//                return adventureCards;
//            }
//        }
//        catch (FileNotFoundException e){
//            System.err.println("Errore: Il file di configurazione non è stato trovato");
//        }
//        catch (IOException e){
//            throw new RuntimeException(e);
//        }
//        return null;
//    }
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


    //TODO usato per testare poi si può rimuovere
    public AdventureCard getCard(int i) {
        if (i < 0 || i >= deck.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + i);
        }
        return deck.get(i);
    }
}
