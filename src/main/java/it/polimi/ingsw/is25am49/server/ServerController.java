package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;

import java.util.ArrayList;
import java.util.List;

public class ServerController {
    private final GameSession gameSession;
    private ServerSerializer serverSerializer;
    //Ci serve averle nel controller per filtrare le possibili selezioni di Token/Tessere dell'utente
    private final ArrayList<ShipBoard> shipBoards;
    private AdventureCard currentAdventureCard;
    private Player currentPlayer;
    //Bisogna decidere se implementare la funzionalità per i game multipli, per ora faccio come se non ci sia
    public ServerController(GameSession gameSession) {
        this.gameSession = gameSession;
        shipBoards = new ArrayList<>();
        currentAdventureCard = null;
    }
    public void setSerializer(ServerSerializer serverSerializer) {
        this.serverSerializer = serverSerializer;
    }
    public void addFinalShipBoardToArray(Player player){
        if(player.getShipBoard().isCompleted())
            shipBoards.add(player.getShipBoard());
    }
    public void passInformationToClientHandler(String method, List<Object> parameters) {
        serverSerializer.SerializeObjectFromServer(method, parameters);
    }


}
