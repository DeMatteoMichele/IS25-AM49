package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbandonedShipCardTest {
    GameBoard gameBoard;
    AdventureCardDeck adventureDeck;
    Player player1;
    Player player2;
    ShipBoard shipBoard1;
    ShipBoard shipBoard2;

    @BeforeEach
    void init(){
        // Initialize the game board and player1
        gameBoard = new GameBoard();
        player1 = new Player("TestPlayer", Color.RED, 1);
        player2 = new Player("TestPlayer2", Color.BLUE, 2);
        gameBoard.addPlayerToLeaderboard(0, player1);
        gameBoard.addPlayerToLeaderboard(3, player2);
        gameBoard.sortLeaderBoard();
        adventureDeck = new AdventureCardDeck();
        adventureDeck.add(new AbandonedShipCard(gameBoard, "Abandoned Ship", 2, 1, 100, 3));
        gameBoard.setMainAdventureDeck(adventureDeck);
        // Create an instance of AbandonedShipCard

        // Set up the initial state of the game board and player1

    }


    @Test
    void drawCard() {
        gameBoard.drawAdventureCard();
        assertEquals(0, adventureDeck.size());
        assertEquals(gameBoard.getLeaderBoard().getFirst().getValue(),player2);
        assertEquals(gameBoard.getLeader(), gameBoard.getCurrentPlayer());
        assertEquals(AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP, gameBoard.getCurrentCard().getCurrentState());
    }

    @Test
    void checkRequirements() {
    }

    @Test
    void landingOnStationOrShip() {
        gameBoard.drawAdventureCard();
        gameBoard.getCurrentCard().landingOnStationOrShip();
        assertEquals(AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS, gameBoard.getCurrentCard().getCurrentState());

    }

    @Test
    void finishDecision() {
        gameBoard.drawAdventureCard();
        gameBoard.getCurrentCard().finishDecision();
        assertEquals(AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP, gameBoard.getCurrentCard().getCurrentState());
        assertEquals(0, adventureDeck.size());
        assertEquals(gameBoard.getLeaderBoard().getFirst().getValue(),player1);
    }

    @Test
    void applyEffect() {
    }
}