package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AbandonedStationCardTest {
    GameBoard gameBoard;
    Player player1;
    Player player2;
    AdventureCardDeck adventureDeck;
    List<Pair<Point,Token>> coordinates;

    @BeforeEach
    void init(){
        gameBoard = new GameBoard();
        player1 = new Player("TestPlayer", Color.RED, 1);
        player2 = new Player("TestPlayer2", Color.BLUE, 2);
        coordinates = new ArrayList<>();
        gameBoard.addPlayerToLeaderboard(0, player1);
        gameBoard.addPlayerToLeaderboard(3, player2);
        gameBoard.sortLeaderBoard();
        adventureDeck = new AdventureCardDeck();
        adventureDeck.add(new AbandonedStationCard(gameBoard, "Abandoned Station", 2, 1, 100, 3));
        adventureDeck.add(new AbandonedShipCard(gameBoard, "Abandoned Ship", 2, 1, 100, 3));
        gameBoard.setMainAdventureDeck(adventureDeck);
    }

    @Test
    void drawCard() {
        gameBoard.drawAdventureCard();
        assertEquals(1, adventureDeck.size());
        assertEquals(player1,gameBoard.getCurrentPlayer());
        assertEquals(player2,gameBoard.getLeader());

    }

    @Test
    void checkRequirements() {
    }

    @Test
    void landingOnStationOrShip() {

    }

    @Test
    void finishDecision() {
    }

    @Test
    void applyEffect() {
    }
}