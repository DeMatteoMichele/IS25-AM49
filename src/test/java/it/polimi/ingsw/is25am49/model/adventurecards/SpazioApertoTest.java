package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.model.shipboard.Component;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.model.shipboard.Tile;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class SpazioApertoTest {

    Player player1;
    Player player2;
    GameBoard gameBoard;
    AdventureCardDeck deck;
    ShipBoard ship1, ship2;
    Tile tile1, tile2;
    Point coordinate1 = new Point(7,6);
    Point coordinate2 = new Point(7,8);
    Component component1, component2;

    @BeforeEach
    void init(){
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
        tile1 = new Tile(2,component1);
        player1.getShipBoard().insertTile(coordinate1, tile1);
        player1.getShipBoard().addTokenToTile(coordinate1, Token.BATTERY_CELLS);
        player1.getShipBoard().addTokenToTile(coordinate1, Token.BATTERY_CELLS);
        player1.getShipBoard().addTokenToTile(coordinate1, Token.BATTERY_CELLS);
        gameBoard.addPlayerToLeaderboard(2,player1);
        gameBoard.addPlayerToLeaderboard(8,player2);
        gameBoard.setMainAdventureDeck(deck);
        gameBoard.sortLeaderBoard();
    }

    @Test
    void drawCard() {
        gameBoard.drawAdventureCard();
        assertEquals(0, deck.size());
        assertEquals(player1, gameBoard.getCurrentPlayer());
        assertEquals(AdventureStatesTypes.DECISION_BATTERIES,gameBoard.getCurrentCard().getCurrentState());
    }

    @Test
    void checkRequirements() {
    }

    @Test
    void finishDecision() {
    }

    @Test
    void testFinishDecision() {
    }

    @Test
    void applyEffect() {
    }
}