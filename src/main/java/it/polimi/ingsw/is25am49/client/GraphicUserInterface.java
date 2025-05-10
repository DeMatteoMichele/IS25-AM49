package it.polimi.ingsw.is25am49.client;

import it.polimi.ingsw.is25am49.client.model.enums.GameMode;

public class GraphicUserInterface implements UserInterface{
    private ClientController controller;

    public GraphicUserInterface() {

    }

    public static void main(String args[]){
        System.out.println("Hello GUI");
    }

    @Override
    public void updateGameSessionNewPlayer(int playerId) {

    }

    @Override
    public void updateGameSessionSettings(GameMode gameMode, int maxPlayers) {

    }
}
