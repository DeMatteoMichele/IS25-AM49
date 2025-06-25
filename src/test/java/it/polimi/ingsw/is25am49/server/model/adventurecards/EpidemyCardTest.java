package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.LeaderBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.adventurecards.EpidemyCard;
import it.polimi.ingsw.is25am49.shared.enums.*;
import it.polimi.ingsw.is25am49.server.model.shipboard.Component;
import it.polimi.ingsw.is25am49.server.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class EpidemyCardTest {
    GameBoard gameBoard;
    Player player1,player2,player3;
    AdventureCardDeck deck;
    ShipBoard ship1, ship2,ship3;
    Tile tile1, tile2, tile3, tile4, tile5;
    Component component1, component2, component3, component4, component5;
    LeaderBoard leaderboard;
    Point coordinate1 = new Point(5,7);
    Point coordinate2 = new Point(5,8);
    Point coordinate3 = new Point(6,6);
    Point coordinate4 = new Point(6,7);



    @BeforeEach
    void init(){
        gameBoard = new GameBoard();
        player1 = new Player("Michele", Color.RED, 2);
        player2 = new Player("Daniele", Color.BLUE, 1);
        player3 = new Player("Sean", Color.GREEN, 3);
        ship1 = new ShipBoard(1,2,Color.RED);
        ship2 = new ShipBoard(1,1,Color.RED);
        ship3 = new ShipBoard(1,3,Color.RED);
        player1.setShipBoard(ship1);
        player2.setShipBoard(ship2);
        player3.setShipBoard(ship3);
        component1 = new ContainerComponent(ComponentType.CABINE,2);
        component2 = new ContainerComponent(ComponentType.CABINE,2);
        component3 = new ContainerComponent(ComponentType.CABINE,2);
        component4 = new ContainerComponent(ComponentType.CABINE,2);
        component5 = new ContainerComponent(ComponentType.CABINE,2);

        tile1 = new Tile(2,component1);
        tile1.setConnectors(
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL
        );

        tile2 = new Tile(1,component2);
        tile2.setConnectors(
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL
        );
        tile3 = new Tile(2,component3);
        tile4 = new Tile(3,component4);
        tile4.setConnectors(
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL
        );
        tile5 = new Tile(4,component5);

        player1.getShipBoard().insertTile(coordinate1,tile1);
        player1.getShipBoard().insertTile(coordinate2,tile2);
        player1.getShipBoard().insertTile(coordinate4,tile4);
        player2.getShipBoard().insertTile(coordinate3,tile3);
        player2.getShipBoard().insertTile(coordinate4,tile4);
        player3.getShipBoard().insertTile(coordinate2,tile5);
        player1.getShipBoard().addTokenToTile(coordinate1, Token.ASTRONAUT);
        player1.getShipBoard().addTokenToTile(coordinate1, Token.ASTRONAUT);
        player1.getShipBoard().addTokenToTile(coordinate2, Token.ASTRONAUT);
        player2.getShipBoard().addTokenToTile(coordinate3, Token.ASTRONAUT);
        player2.getShipBoard().addTokenToTile(coordinate3, Token.ASTRONAUT);
        player2.getShipBoard().addTokenToTile(coordinate4, Token.ASTRONAUT);
        gameBoard.setLeaderBoard(GameMode.LEVEL_1);
        gameBoard.addPlayerToLeaderboard(player2);
        gameBoard.addPlayerToLeaderboard(player1);
        gameBoard.addPlayerToLeaderboard(player3);
        deck = new AdventureCardDeck();
        deck.add(new EpidemyCard(gameBoard,"Epidemy Card", 1,"epidemy"));
        gameBoard.setMainAdventureDeck(deck);
        ship1.completeShipBoard();
        System.out.println(ship1.toString());

    }
    @Test
    void drawCard() {
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        System.out.println("NAVE 1");
        System.out.println(ship1.toString());
        assertEquals(3, player1.getShipBoard().getTotalNumberOfaToken(Token.ASTRONAUT));
        assertEquals(0, player1.getShipBoard().getTotalNumberOfaToken(Token.BROWN_ALIENS));
    }

    @Test
    void checkInterconnectedCabin() {
    }

    @Test
    void checkRequirements() {
    }
    @Test
    void testToString(){
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        System.out.println(gameBoard.getCurrentCard().toString());
    }
    @Test
    void applyEffect() {
    }
}