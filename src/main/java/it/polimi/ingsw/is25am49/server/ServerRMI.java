package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.client.RemoteServer;
import it.polimi.ingsw.is25am49.server.model.GameSession;
import it.polimi.ingsw.is25am49.server.model.adventurecards.Planet;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.enums.Orientation;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import javafx.util.Pair;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ServerRMI extends UnicastRemoteObject implements RemoteServer {
    ServerController serverController;
    HashMap<Integer, RemoteClient> clients;

    public ServerRMI() throws RemoteException {
        super();
        serverController=new ServerController(new GameSession());
        clients=new HashMap<>();
    }
//    public setHandTile(){
//        controller.setHandTile();
//    }

    private void inputThread() {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Server: Comando > ");
                String input = scanner.nextLine();

                switch (input.toLowerCase()){
                    case "quit":
                        System.out.println("Server: Sto chiudendo...");
                        //todo non so come si spenga il server
                        System.exit(0);
                        break;
                    case "users":
                        System.out.println("utenti registrati - "+clients.size()+ " :");
                        for (Integer id : clients.keySet()) {
                            System.out.println("ID: "+id+" : "+clients.get(id));
                        }
                        break;
                        default:
                            // Puoi aggiungere qui altri comandi
                            System.out.println("Comando '" + input+ "' sconosciuto");
                            break;
                };


            }
        }).start();
    }

    @Override
    public void serverTestConnection(int playerId) throws RemoteException {
        if(clients.containsKey(playerId)){
            serverController.serverTestConnection(playerId);
            clients.get(playerId).notifyClient("connection test client "+playerId+" succeeded");
            System.out.println("ID:"+playerId+" - callback succeeded");
        } else System.out.println("errore callback, client "+playerId+" non registrato");
    }


    @Override
    public void registerClientRMI(RemoteClient client) throws RemoteException {
        //TODO da sistemare e mettere dentro all'if(forse), attualmente l'istanza del
        // client cambia ogni riavvio del main, quindi va aggiornata la mappa
        int playerId=clients.size()+1;
        clients.put(playerId, client);
        System.out.println("ID:"+playerId+" - client logged");
        //return playerId;
    }

    @Override
    public void registerClientSocket(int playerId) throws RemoteException {

    }

    @Override
    public void adminSetup(int playerId, int numberOfPlayers, GameMode mode) throws RemoteException {
    }

    @Override
    public void playerJoin(int playerId, String username, Color color) throws RemoteException {

    }


    @Override
    public void userDrawTile(int playerId, int tileId) throws RemoteException {

    }

    @Override
    public void userPlaceTile(int playerId, Point coordinates, Orientation orientation) throws RemoteException {
        //serverController.placeTile()
    }

    @Override
    public void userPickTile(int playerId, Point coordinates) throws RemoteException {

    }

    @Override
    public void userDiscardTile(int playerId) throws RemoteException {

    }

    @Override
    public void userFinishShipboard(int playerId) throws RemoteException {

    }

    @Override
    public void userLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) throws RemoteException {

    }

    @Override
    public void userStopLookSmallAdventureCardDeck(int playerId, int smallDeckIndex) throws RemoteException {

    }

    @Override
    public void userPlaceTimer(int playerId) throws RemoteException {

    }

    @Override
    public void userSelectTypeOfToken(int playerId, Token token, Point coordinates) throws RemoteException {

    }

    @Override
    public void userThrowDices(int playerId) throws RemoteException {

    }

    @Override
    public void userDrawAdventureCard(int playerId) throws RemoteException {

    }

    @Override
    public void userFinishDecisionActivating(int playerId, HashSet<Point> coordinates) throws RemoteException {

    }

    @Override
    public void userFinishDecisionToken(int playerId, ArrayList<Pair<Point, Token>> listOfTokens) throws RemoteException {

    }

    @Override
    public void userAcceptPrizes(int playerId, boolean wantsRewards) throws RemoteException {

    }

    @Override
    public void userLandOnPlanet(int playerId, boolean isLanded, Planet planet) throws RemoteException {

    }

    @Override
    public void userLandOnStationOrShip(int playerId, boolean isLanded) throws RemoteException {

    }

    @Override
    public void userStopFlight(int playerId) throws RemoteException {

    }

    @Override
    public void adminCalculateScore(int playerId) throws RemoteException {

    }

    @Override
    public void userEndGame(int playerId) throws RemoteException {

    }

}
