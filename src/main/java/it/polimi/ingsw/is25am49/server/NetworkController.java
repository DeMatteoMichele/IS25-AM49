package it.polimi.ingsw.is25am49.server;
import java.util.HashSet;
import java.util.Set;

public class NetworkController {
    private final Set<Integer> playersRMI;
    private final Set<Integer> playersSocket;
    private int idCounter;

    public NetworkController() {
        this.playersRMI = new HashSet<>();
        this.playersSocket = new HashSet<>();
        this.idCounter = -1;
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

    public boolean isInPlayers(int playerId) {
        return playersRMI.contains(playerId) || playersSocket.contains(playerId);
    }

    public boolean isInPlayersRMI(int playerId) {
        return playersRMI.contains(playerId);
    }

    public boolean isInPlayersSocket(int playerId) {
        return playersSocket.contains(playerId);
    }
}
