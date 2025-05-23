package it.polimi.ingsw.is25am49.server;

import it.polimi.ingsw.is25am49.shared.RemoteClient;
import it.polimi.ingsw.is25am49.shared.RemoteServer;
import it.polimi.ingsw.is25am49.shared.Command;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ServerRMI extends UnicastRemoteObject implements RemoteClient, RemoteServer {
    private boolean gameStarted;
    private ServerController serverController;
    private HashMap<Integer, RemoteClient> clients;
    private HashMap<Integer, RemoteClient> matchPlayers;
    private int idCounter;

    public ServerRMI(ServerController serverController) throws RemoteException {
        super();
        this.serverController = serverController;
        this.clients = new HashMap<>();
        this.matchPlayers = new HashMap<>();
        this.idCounter = 0;
    }

    @Override
    public void command_FromServerToClient(Command command) throws RemoteException {
        if (gameStarted) {
            matchPlayers.forEach((id, client) -> {
                try {
                    client.command_FromServerToClient(command);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
        }
        else {
            clients.forEach((id, client) -> {
                try {
                    client.command_FromServerToClient(command);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void command_FromServerToSpecificClient(Command command, int clientId) throws RemoteException {
        RemoteClient specificClient = clients.get(clientId);
        if (specificClient != null) {
            command.setServerController(serverController);
            specificClient.command_FromServerToClient(command);
        } else {
            System.out.println("Client with ID " + clientId + " not found.");
        }
    }

    public void signalThatGameStarted() {
        gameStarted = true;
    }

    /**THIS METHOD IS CALLED BY THE CLIENT
     *REMEMBER: RMI WORKS BY CALLING METHODS ON A REMOTE OBJECT
     * SO: CLIENT CAN CALL HIS INTERFACE METHOD(command_FromClientToServer) ONLY IF
     * SERVERRMI(the remote object) HAS THE METHOD IMPLEMENTED.
     * REMEMBER, WE CAN CALL A METHOD ON AN OBJECT ONLY IF THE OBJECT HAS THAT METHOD. OTHERWISE, WE ARE NOT CALLING ANYTHING.
     * EXAMPLE: TO CALL PLACETILE ON THE SHIPBOARD, THE SHIPBOARD MUST HAVE PLACETILE ON ITS CODE.
     */

    @Override
    public void command_FromClientToServer(Command command) throws RemoteException {
        if(command.getTypeOfMessage().equals("HSK")) {
            command.setServerController(serverController);
            serverController.addPlayerToLobbyRMI(idCounter);
            clients.put(idCounter, (RemoteClient) command.getParameters().getFirst());
            Command response = new Command("registerClientCallback", idCounter);
            response.addParameter(idCounter);
            command_FromServerToSpecificClient(response, idCounter);
            idCounter++;
        }
        else if(command.getTypeOfMessage().equals("MSG")) {
            command.setServerController(serverController);
            command.callMethod();
        }
    }
    public void sayToServerRMIWhoHasJoined(int playerId){
        matchPlayers.put(playerId, clients.get(playerId));
    }
}
