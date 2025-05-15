package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.client.model.Game;
import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.Player;
import it.polimi.ingsw.is25am49.client.model.exceptions.GameSettingsException;
import it.polimi.ingsw.is25am49.client.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.client.model.states.InitState;
import it.polimi.ingsw.is25am49.client.model.states.State;

import java.util.Optional;

public class ClientChecks {
    private Optional<GameSession> gameSession;
    private Optional<Player> admin;
    private Optional<Player> Player;
    private Optional<Game> game;
    private Optional<ShipBoard> shipboard;
    private Optional<State> currentState;

    public ClientChecks() {
        this.gameSession = Optional.empty();

        this.game = Optional.empty();
        this.shipboard = Optional.empty();
        this.currentState = Optional.empty();
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = Optional.of(gameSession);
    }
    public void setAdmin(Player player) {
        this.admin = Optional.of(player);
    }
    public void setPlayer(Player player) {
        this.Player = Optional.of(player);
    }
    public void setGame(Game game) {
        this.game = Optional.of(game);
    }
    public void setShipboard(ShipBoard shipboard) {
        this.shipboard = Optional.of(shipboard);
    }
    public void setCurrentState(State currentState) {
        this.currentState = Optional.of(currentState);
    }

    public void checkIsAdmin(int playerId) {
        if(admin.isEmpty()) throw new GameSettingsException("There is no admin");
        if(admin.get().getIdPlayer()!=playerId) throw new GameSettingsException("You are not the admin of this game");
    }
    public void checkJoinLobby(){
        if(admin.isEmpty())throw new GameSettingsException("JoinLobby: admin is null");
        else if(game.isEmpty())throw new GameSettingsException("JoinLobby: Game is not ready yet");
        else if(game.get().getListPlayers().size()>=game.get().getMaxPlayers())throw new GameSettingsException("JoinLobby: too many players");
    }


}
