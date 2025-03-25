package it.polimi.ingsw.is25am49.controller;

import it.polimi.ingsw.is25am49.model.GameSession;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.model.states.State;

import java.util.ArrayList;

public class Controller {
    private final GameSession gameSession;
    //Ci serve averle nel controller per filtrare le possibili selezioni di Token/Tessere dell'utente
    private final ArrayList<ShipBoard> shipBoards;
    private AdventureCard currentAdventureCard;
    private Player currentPlayer;
    //Bisogna decidere se implementare la funzionalità per i game multipli, per ora faccio come se non ci sia
    public Controller() {
        gameSession = new GameSession();
        shipBoards = new ArrayList<>();
        currentAdventureCard = null;
    }
    public void addFinalShipBoardToArray(Player player){
        if(player.getShipBoard().isCompleted())
            shipBoards.add(player.getShipBoard());
    }



}
