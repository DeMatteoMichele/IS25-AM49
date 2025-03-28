package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.shipboard.*;
import it.polimi.ingsw.is25am49.model.shipboard.Component;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SpazioApertoTest {

    Player player1;
    Player player2;
    GameBoard gameBoard;
    AdventureCardDeck deck;
    ShipBoard ship1, ship2;
    Tile tile1, tile2,tile3,tile4,tile5,tile6,tile7;
    Point coordinate1 = new Point(7,6);
    Point coordinate2 = new Point(7,8);
    Point coordinate3 = new Point(6,7);
    Point coordinate4 = new Point(8,7);
    Point coordinate5 = new Point(8,6);
    Point coordinate6 = new Point(8,9);
    Point coordinate7 = new Point(6,6);
    Component component1, component2, component3, component4,component5,component6,component7;
    List <Pair<Point,Token>> batteries,batteries2;
    Set<Point> cannons,cannons2;

    @BeforeEach
    void init(){
        cannons = new HashSet<Point>();
        cannons2 = new HashSet<Point>();
        cannons.add(coordinate4);
        cannons.add(coordinate2);
        cannons.add(coordinate3);
        cannons2.add(coordinate7);
        batteries = new ArrayList<>();
        batteries2 = new ArrayList<>();
        batteries.add(new Pair<>(coordinate1, Token.BATTERY_CELLS));
        batteries.add(new Pair<>(coordinate1, Token.BATTERY_CELLS));
        batteries.add(new Pair<>(coordinate1, Token.BATTERY_CELLS));
        batteries2.add(new Pair<>(coordinate6, Token.BATTERY_CELLS));

        component2 = new SpecialComponent(ComponentType.DOUBLE_ENGINE,2);
        component3 = new SpecialComponent(ComponentType.DOUBLE_ENGINE,2);
        component4 = new SpecialComponent(ComponentType.DOUBLE_ENGINE,2);
        component5 = new BasicComponent(ComponentType.ENGINE,1);
        component7 = new SpecialComponent(ComponentType.DOUBLE_ENGINE,2);
        tile2 = new Tile(1,component2);
        tile3 = new Tile(4,component3);
        tile4 = new Tile(5,component4);
        tile5 = new Tile(6,component5);
        tile7 = new Tile(10,component7);

        player1 = new Player("TestPlayer1", Color.RED, 1);
        player2 = new Player("TestPlayer2", Color.GREEN, 2);
        gameBoard = new GameBoard();
        deck = new AdventureCardDeck();
        deck.add(new SpazioAperto(gameBoard,"spazio aperto",0,1,0));
        ship1 = new ShipBoard(1, 1);
        ship2 = new ShipBoard(1, 2);
        player1.setShipBoard(ship1);
        player2.setShipBoard(ship2);
        component1 = new ContainerComponent(ComponentType.BATTERY,3);
        component6 = new ContainerComponent(ComponentType.BATTERY,3);
        tile6 = new Tile(3,component6);
        tile1 = new Tile(2,component1);
        player1.getShipBoard().insertTile(coordinate1, tile1);
        player2.getShipBoard().insertTile(coordinate6, tile6);
        player2.getShipBoard().insertTile(coordinate2, tile2);
        player1.getShipBoard().addTokenToTile(coordinate1, Token.BATTERY_CELLS);
        player1.getShipBoard().addTokenToTile(coordinate1, Token.BATTERY_CELLS);
        player1.getShipBoard().addTokenToTile(coordinate1, Token.BATTERY_CELLS);
        player2.getShipBoard().addTokenToTile(coordinate6, Token.BATTERY_CELLS);
        player1.getShipBoard().insertTile(coordinate2, tile2);
        player1.getShipBoard().insertTile(coordinate3, tile3);
        player1.getShipBoard().insertTile(coordinate4, tile4);
        player1.getShipBoard().insertTile(coordinate5, tile5);
        player2.getShipBoard().insertTile(coordinate5, tile5);
        player2.getShipBoard().insertTile(coordinate7, tile7);

        gameBoard.addPlayerToLeaderboard(2,player1);
        gameBoard.addPlayerToLeaderboard(8,player2);
        gameBoard.setMainAdventureDeck(deck);
        gameBoard.sortLeaderBoard();
    }

    @Test
    void drawCard() {
        gameBoard.drawAdventureCard();
        assertEquals(0, deck.size());
        assertEquals(player2, gameBoard.getCurrentPlayer());
        assertEquals(AdventureStatesTypes.DECISION_BATTERIES,gameBoard.getCurrentCard().getCurrentState());
    }

    @Test
    void checkRequirements() {
    }

    @Test
    void finishDecision() {
        gameBoard.drawAdventureCard();
        gameBoard.getCurrentCard().finishDecision(batteries2);

        assertEquals(player2, gameBoard.getCurrentPlayer());
        assertEquals(AdventureStatesTypes.DECISION_CANNON,gameBoard.getCurrentCard().getCurrentState());
    }

    @Test
    void testFinishDecision() {
        gameBoard.drawAdventureCard();
        gameBoard.getCurrentCard().finishDecision(batteries2);
        gameBoard.getCurrentCard().finishDecision(cannons2);
        gameBoard.getCurrentCard().finishDecision(batteries);
        gameBoard.getCurrentCard().finishDecision(cannons);
        assertEquals(AdventureStatesTypes.DRAW,gameBoard.getCurrentCard().getCurrentState());
        assertEquals(player1, gameBoard.getCurrentPlayer());
    }

    @Test
    void applyEffect() {
    }
}