package it.polimi.ingsw.is25am49.server;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NetworkController {
    private final Set<Integer> playersRMI;
    private final Set<Integer> playersSocket;
    public final ConcurrentHashMap<Integer, Boolean> connections;
    private int idCounter;
    private final ConcurrentMap<String, Integer> usernamesToIds;


    public NetworkController() {
        this.playersRMI = new HashSet<>();
        this.playersSocket = new HashSet<>();
        this.connections = new ConcurrentHashMap<>();
        this.idCounter = -1;
        this.usernamesToIds = new ConcurrentHashMap<>();
    }

    public int getIdCounter() {
        return idCounter;
    }
    public void updateIdCounter() {
        this.idCounter++;
    }

    public void addPlayerToLobbyRMI(int playerId) {
        playersRMI.add(playerId);
    }

    public void addPlayerToLobbySocket(int playerId) {
        playersSocket.add(playerId);
    }

    public Set<Integer> getPlayersRMI() {
        return playersRMI;
    }

    public Set<Integer> getPlayersSocket() {
        return playersSocket;
    }

    public boolean isInPlayersRMI(int playerId) {
        return playersRMI.contains(playerId);
    }

    public boolean isInPlayersSocket(int playerId) {
        return playersSocket.contains(playerId);
    }

    public boolean hasThisUsername(String username) {
        return usernamesToIds.containsKey(username);
    }

    public void putUsername(String username) {
        usernamesToIds.put(username, idCounter);
    }
}
