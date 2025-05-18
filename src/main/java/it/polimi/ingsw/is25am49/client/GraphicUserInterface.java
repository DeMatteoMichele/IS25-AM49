package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.client.model.GameSession;
import it.polimi.ingsw.is25am49.client.model.enums.GameMode;

public class GraphicUserInterface implements UserInterface{
    private ClientController controller;

    public GraphicUserInterface() {

    }

    public static void main(String args[]){
        System.out.println("Hello GUI");
    }

    @Override
    public void setClientId(int myId) {

    }

    @Override
    public void updateGameSessionNewPlayer(int playerId) {

    }

    @Override
    public void updateGameSessionCreateGame(int playerId, GameMode gameMode, int maxPlayers) {

    }

    @Override
    public void updateGameNewPlayer(int playerId) {

    }


    @Override
    public void updateStateTransition(int stateIndex) {

    }

    @Override
    public void printUpdateMessage(String msg) {

    }

    @Override
    public void setGamesession(GameSession gameSession) {

    }
}
