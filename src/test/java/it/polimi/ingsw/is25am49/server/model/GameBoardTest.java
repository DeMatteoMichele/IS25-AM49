package it.polimi.ingsw.is25am49.server.model;

import it.polimi.ingsw.is25am49.server.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.shared.enums.*;
import it.polimi.ingsw.is25am49.server.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.server.model.shipboard.Component;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {
    GameBoard gameBoard;
    Player player1;
    Player player2;
    Player player3;
    Player player4;
    Player player5;
    Player player6;
    Player player7;
    ShipBoard ship1, ship2, ship3, ship4;
    Tile tile1, tile2, tile3, tile4;
    Component component1, component2, component3, component4;
    Point coordinate1 = new Point(7, 8);
    Point coordinate2 = new Point(8, 7);
    Point coordinate3 = new Point(7, 6);
    Point coordinate4 = new Point(6, 7);
    @BeforeEach
    void init(){
        gameBoard = new GameBoard();
        gameBoard.setLeaderBoard(GameMode.LEVEL_1);
        player1 = new Player("pippo", Color.RED,3);
        player2 = new Player("pluto", Color.BLUE,2);
        player3 = new Player("marco", Color.GREEN,1);
        player4 = new Player("ci", Color.BLUE,12);
        player5 = new Player("rojo", Color.RED,8);
        player6 = new Player("verde", Color.BLUE,6);
        player7 = new Player("azul", Color.BLUE,9);
        ship1 = new ShipBoard(1,3, Color.RED);
        ship2 = new ShipBoard(1,2, Color.BLUE);
        ship3 = new ShipBoard(1,1, Color.GREEN);
        ship4 = new ShipBoard(1,12 , Color.YELLOW);
        player1.setShipBoard(ship1);
        player2.setShipBoard(ship2);
        player3.setShipBoard(ship3);
        player4.setShipBoard(ship4);
        component1 = new ContainerComponent(ComponentType.CABINE, 5);
        tile1 = new Tile(1,component1);
        tile1.setConnectors(
                ConnectorType.UNIVERSAL,
                ConnectorType.FLAT,
                ConnectorType.FLAT,
                ConnectorType.FLAT
        );
        player1.getShipBoard().insertTile(coordinate1,tile1);
        player1.getShipBoard().addTokenToTile(coordinate1, Token.ASTRONAUT);

        component2 = new ContainerComponent(ComponentType.CABINE, 5);
        tile2 = new Tile(2,component2);
        tile2.setConnectors(
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL
        );
        player2.getShipBoard().insertTile(coordinate2,tile2);
        component3 = new ContainerComponent(ComponentType.CABINE, 5);
        tile3 = new Tile(1,component3);
        tile3.setConnectors(
                ConnectorType.FLAT,
                ConnectorType.FLAT,
                ConnectorType.UNIVERSAL,
                ConnectorType.FLAT
        );
        player3.getShipBoard().insertTile(coordinate3,tile3);
        component4 = new ContainerComponent(ComponentType.CABINE, 5);
        tile4 = new Tile(1,component4);
        tile4.setConnectors(
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL,
                ConnectorType.FLAT
        );
        player4.getShipBoard().insertTile(coordinate4,tile4);
        player1.addCredits(23);
        player2.addCredits(34);
        player3.addCredits(12);
        player4.addCredits(27);
        gameBoard.addPlayerToLeaderboard(player1);
        gameBoard.addPlayerToLeaderboard(player2);
        gameBoard.addPlayerToLeaderboard(player3);
        gameBoard.addPlayerToLeaderboard(player4);
        gameBoard.setCurrentPlayer(gameBoard.getLeader());


    }



    @Test
    void configurateFlight() {
    }

    @Test
    void checkStatus() {
    }

    @Test
    void setFlightMode() {
    }

    @Test
    void getSmallAdventureDeck() {
    }

    @Test
    void mergeDecks() {
    }

    @Test
    void getLeaderBoard() {
    }

    @Test
    void setCurrentCard() {
    }

    @Test
    void getCurrentCard() {
    }

    @Test
    void drawAdventureCard() {
    }

    @Test
    void addPlayerToLeaderboard() {
    }

    @Test
    void addDiscardedTile() {
    }

    @Test
    void drawTile() {
    }

    @Test
    void getLeader() {
    }
    @Test
    void TetstoString() {
        System.out.println(gameBoard.toString());
    }

    @Test
    void calculateFinalCredits() {
        gameBoard.calculateFinalCredits();
        assertEquals(29,player1.getCredits());
        assertEquals(37,player2.getCredits());
        assertEquals(28,player4.getCredits());
    }

    @Test
    void calculate_final_score() {
    }

    @Test
    void minimumExposedComponents() {

        assertEquals(2,gameBoard.minimumExposedComponents().size());
    }

    @Test
    void getCurrentPlayer() {
        assertEquals(player1.getColor(),gameBoard.getCurrentPlayer().getColor());
    }

    @Test
    void nextPlayer() {
        gameBoard.nextPlayer();
        assertEquals(player2.getUsername(),gameBoard.getCurrentPlayer().getUsername());
        gameBoard.nextPlayer();
        assertEquals(player3.getColor(), gameBoard.getCurrentPlayer().getColor());
    }

    @Test
    void setCurrentPlayer() {
        gameBoard.setCurrentPlayer(gameBoard.getLeader());
        assertEquals(player1.getUsername(),gameBoard.getCurrentPlayer().getUsername());
    }

    @Test
    void getNextCard() {

    }
    @Test
    void initialize(){
//        gameBoard.generateDeck(GameMode.FLIGHT_MODE);
        gameBoard.initialize(GameMode.FLIGHT_MODE);
//        assertEquals(3,gameBoard.getMainAdventureDeck().size());
        gameBoard.drawAdventureCard(gameBoard.getLeader());


    }
    @Test
    void initialize2(){
        gameBoard.initialize(GameMode.LEVEL_2);
        assertEquals(0,gameBoard.getMainAdventureDeck().size());
        gameBoard.mergeDecks();
        assertEquals(12,gameBoard.getMainAdventureDeck().size());
        for(int i = 0; i < gameBoard.getMainAdventureDeck().size(); i++) {
            AdventureCard card = gameBoard.getMainAdventureDeck().getCard(i);
            System.out.println(card.toString());
            System.out.println("\n");
        }
        gameBoard.drawAdventureCard(gameBoard.getLeader());
    }
    @Test
    void generateDeck() {
        gameBoard.generateSmallDecks(GameMode.FLIGHT_MODE);
//        assertEquals(8, gameBoard.getMainAdventureDeck().size());

        for(int i = 0; i < gameBoard.getMainAdventureDeck().size(); i++) {
            AdventureCard card = gameBoard.getMainAdventureDeck().getCard(i);
            System.out.println(card.toString());
            System.out.println("\n");
        }
    }
    @Test
    void generateDeck2(){
        gameBoard.generateSmallDecks(GameMode.LEVEL_1);
        assertEquals(2, gameBoard.getSmallAdventureDeck(0).size());
        assertEquals(2, gameBoard.getSmallAdventureDeck(1).size());
        assertEquals(2, gameBoard.getSmallAdventureDeck(2).size());
        assertEquals(2, gameBoard.getSmallAdventureDeck(3).size());
        for(int i=0; i<4;i++){
            System.out.println("Small Deck "+i);
            for(int j=0;j<gameBoard.getSmallAdventureDeck(i).size();j++){
                AdventureCard card = gameBoard.getSmallAdventureDeck(i).getCard(j);
                System.out.println(card.toString());
                System.out.println("\n");
            }
        }

    }

    @Test
    void generateDeck3(){
        gameBoard.generateSmallDecks(GameMode.LEVEL_2);
        assertEquals(3, gameBoard.getSmallAdventureDeck(0).size());
        assertEquals(3, gameBoard.getSmallAdventureDeck(1).size());
        assertEquals(3, gameBoard.getSmallAdventureDeck(2).size());
        assertEquals(3, gameBoard.getSmallAdventureDeck(3).size());
        for(int i=0; i<4;i++){
            System.out.println("Small Deck "+i);
            for(int j=0;j<gameBoard.getSmallAdventureDeck(i).size();j++){
                AdventureCard card = gameBoard.getSmallAdventureDeck(i).getCard(j);
                System.out.println(card.toString());
                System.out.println("\n");
            }
        }
    }

    @Test
    void testString(){
        gameBoard.generateSmallDecks(GameMode.LEVEL_2);
        AdventureCardDeck deck2 = gameBoard.getSmallAdventureDeck(2);
        System.out.println(deck2.toString());

    }
    @Test
    void updateLeaderboard() {
        assertEquals(4,gameBoard.getLeaderBoard().getDays(player1));
        assertEquals(2,gameBoard.getLeaderBoard().getDays(player2));
        assertEquals(1,gameBoard.getLeaderBoard().getDays(player3));
        assertEquals(0,gameBoard.getLeaderBoard().getDays(player4));
        player1.setDaysThisTurn(-1);
        player2.setDaysThisTurn(-2);
        player3.setDaysThisTurn(+1);
        player4.setDaysThisTurn(0);
        gameBoard.updateLeaderboard();
        assertEquals(player3.getUsername(), gameBoard.getLeaderBoard().getPlayerInPosition(1).getUsername());
        assertEquals(player1.getUsername(),gameBoard.getLeaderBoard().getPlayerInPosition(2).getUsername());
        assertEquals(player4.getUsername(),gameBoard.getLeaderBoard().getPlayerInPosition(3).getUsername());
        assertEquals(0,gameBoard.getLeaderBoard().getDays(player4));
        assertEquals(3,gameBoard.getLeaderBoard().getDays(player3));
        assertEquals(-1,gameBoard.getLeaderBoard().getDays(player2));
        assertEquals(2,gameBoard.getLeaderBoard().getDays(player1));
        assertEquals(0,player1.getDaysThisTurn());
        assertEquals(0,player2.getDaysThisTurn());
        assertEquals(0,player3.getDaysThisTurn());
        assertEquals(0,player4.getDaysThisTurn());
    }

    @Test
    void checkIfIsLapped(){
        player1.setDaysThisTurn(15);
        player4.setDaysThisTurn(1);
        player2.setDaysThisTurn(5);
        player3.setDaysThisTurn(5);
        gameBoard.updateLeaderboard();
        gameBoard.checkIfIsLapped();
        assertEquals(player1.getUsername(),gameBoard.getLeaderBoard().getPlayerInPosition(1).getUsername());
        assertEquals(player4,gameBoard.getLandedPlayers().getFirst());
        assertEquals(3,gameBoard.getLeaderBoard().size());
        //assertEquals(player4.getUsername(), gameBoard.getLandedPlayers().getFirst().getUsername());

    }

    @Test
    void finishUpdateLeaderboard(){
        player1.setDaysThisTurn(16);
        player4.setDaysThisTurn(1);
        player2.setDaysThisTurn(1);
        player3.setDaysThisTurn(1);
        gameBoard.updateLeaderboard();
        gameBoard.finishUpdateLeaderboard();
        assertTrue(gameBoard.getLandedPlayers().contains(player4));
        assertTrue(gameBoard.getLandedPlayers().contains(player2));
        assertTrue(gameBoard.getLandedPlayers().contains(player3));
        assertEquals(1,gameBoard.getLeaderBoard().size());
        assertFalse(gameBoard.getLandedPlayers().isEmpty());
    }
}