package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.adventurecards.SabotageCard;
import it.polimi.ingsw.is25am49.server.model.enums.*;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.shipboard.Component;
import it.polimi.ingsw.is25am49.server.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class SabotageCardTest {

    Player player1, player2, player3;
    GameBoard gameBoard;
    ShipBoard ship1,ship2,ship3;
    AdventureCardDeck deck;
    Component component1,component3;
    Tile tile1,tile3;
    Point coordinate1 = new Point(7,8);
    Point coordinate3 = new Point(7,6);


    @BeforeEach
    public void init(){
        gameBoard = new GameBoard();
        gameBoard.setLeaderBoard(GameMode.LEVEL_1);

        player1 = new Player("pippo", Color.RED,2);
        player2 = new Player("marco", Color.BLUE,3);
        player3 = new Player("luca", Color.GREEN,4);
        ship1 = new ShipBoard(1,2);
        ship2 = new ShipBoard(1,3);
        ship3 = new ShipBoard(1,4);
        player1.setShipBoard(ship1);
        player2.setShipBoard(ship2);
        player3.setShipBoard(ship3);
        component1 = new ContainerComponent(ComponentType.CABINE,6);
        component3 = new ContainerComponent(ComponentType.CABINE,7);
        tile1 = new Tile(2,component1);
        tile3 = new Tile(3,component3);
        tile1.setConnectors(ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL);
        tile3.setConnectors(ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL);
        player1.getShipBoard().insertTile(coordinate1,tile1);
        player3.getShipBoard().insertTile(coordinate3,tile3);
        player3.getShipBoard().addTokenToTile(coordinate3, Token.ASTRONAUT);
        player1.getShipBoard().addTokenToTile(coordinate1, Token.ASTRONAUT);
        gameBoard.addPlayerToLeaderboard(player1);
        gameBoard.addPlayerToLeaderboard(player2);
        gameBoard.addPlayerToLeaderboard(player3);
        gameBoard.getLeaderBoard().modifyFlightDays(player1, -1);
        gameBoard.getLeaderBoard().modifyFlightDays(player2, +3);
        gameBoard.getLeaderBoard().modifyFlightDays(player3, +7);
        deck = new AdventureCardDeck();
        deck.add(new SabotageCard(gameBoard,"Sabotage Card",1));
        gameBoard.setMainAdventureDeck(deck);
        ship1.completeShipBoard();
        ship2.completeShipBoard();
        ship3.completeShipBoard();


    }


    @Test
    void drawCard() {
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        gameBoard.getCurrentCard().rollDices();
        gameBoard.getCurrentCard().rollDices();
        gameBoard.getCurrentCard().rollDices();
        gameBoard.getCurrentCard().rollDices();

        assertEquals("Sabotage Card",gameBoard.getCurrentCard().getDescription());
        assertEquals(0,deck.size());
        assertEquals(AdventureStatesTypes.DRAW,gameBoard.getCurrentCard().getCurrentState());
        assertEquals(player2,gameBoard.getCurrentPlayer());
    }

    @Test
    void checkRequirements() {
    }

    @Test
    void rollDices() {
    }

    @Test
    void applyEffect() {
    }
}