package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.LeaderBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.shipboard.Component;
import it.polimi.ingsw.is25am49.server.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
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
        ship1 = new ShipBoard(1,2);
        ship2 = new ShipBoard(1,1);
        ship3 = new ShipBoard(1,3);
        player1.setShipBoard(ship1);
        player2.setShipBoard(ship2);
        player3.setShipBoard(ship3);
        component1 = new ContainerComponent(ComponentType.CABINE,4);
        component2 = new ContainerComponent(ComponentType.CABINE,5);
        component3 = new ContainerComponent(ComponentType.CABINE,4);
        component4 = new ContainerComponent(ComponentType.BROWN_ALIEN_MODULE,5);
        component5 = new ContainerComponent(ComponentType.CABINE,4);

        tile1 = new Tile(2,component1);
        tile2 = new Tile(1,component2);
        tile3 = new Tile(2,component3);
        tile4 = new Tile(3,component4);
        tile5 = new Tile(4,component5);

        player1.getShipBoard().insertTile(coordinate1,tile1);
        player1.getShipBoard().insertTile(coordinate2,tile2);
        player2.getShipBoard().insertTile(coordinate3,tile3);
        player2.getShipBoard().insertTile(coordinate4,tile4);
        player3.getShipBoard().insertTile(coordinate2,tile5);
        player1.getShipBoard().addTokenToTile(coordinate1, Token.ASTRONAUT);
        player1.getShipBoard().addTokenToTile(coordinate1, Token.ASTRONAUT);
        player1.getShipBoard().addTokenToTile(coordinate2, Token.ASTRONAUT);
        player2.getShipBoard().addTokenToTile(coordinate3, Token.ASTRONAUT);
        player2.getShipBoard().addTokenToTile(coordinate3, Token.ASTRONAUT);
        player2.getShipBoard().addTokenToTile(coordinate4, Token.BROWN_ALIENS);
        player2.getShipBoard().addTokenToTile(new Point(7,7), Token.ASTRONAUT);
        gameBoard.setLeaderBoard(GameMode.LEVEL_1);
        gameBoard.addPlayerToLeaderboard(player2);
        gameBoard.addPlayerToLeaderboard(player1);
        gameBoard.addPlayerToLeaderboard(player3);
        deck = new AdventureCardDeck();
        deck.add(new EpidemyCard(gameBoard,"Epidemy Card", 1));
        gameBoard.setMainAdventureDeck(deck);

    }
    @Test
    void drawCard() {
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        assertEquals(1, player1.getShipBoard().getTotalNumberOfaToken(Token.ASTRONAUT));
        assertEquals(0, player1.getShipBoard().getTotalNumberOfaToken(Token.BROWN_ALIENS));
    }

    @Test
    void checkInterconnectedCabin() {
    }

    @Test
    void checkRequirements() {
    }

    @Test
    void applyEffect() {
    }
}