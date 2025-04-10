package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.server.model.adventurecards.StarDustCard;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.shipboard.Component;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StarDustCardTest {

    GameBoard gameBoard;
    AdventureCardDeck adventureDeck;
    Player player1;
    Player player2;
    Player player3;
    ShipBoard ship1;
    ShipBoard shipMock1 = Mockito.mock(ShipBoard.class);
    ShipBoard shipMock2 = Mockito.mock(ShipBoard.class);
    ShipBoard shipMock3 = Mockito.mock(ShipBoard.class);

    List<CardEffect> listEffects = new ArrayList<>();
    AdventureCard card1;


    @BeforeEach
    void init() {
        // Initialize the game board and player1
        gameBoard = new GameBoard();
        player1 = new Player("TestPlayer", Color.RED, 1);
        player2 = new Player("TestPlayer2", Color.BLUE, 2);
        player3 = new Player("TestPlayer3", Color.GREEN, 3);
        player1.setShipBoard(shipMock1);
        player2.setShipBoard(shipMock2);
        player3.setShipBoard(shipMock3);
        gameBoard.setLeaderBoard(GameMode.LEVEL_1);
        gameBoard.addPlayerToLeaderboard( player1);
        gameBoard.addPlayerToLeaderboard( player2);
        gameBoard.addPlayerToLeaderboard( player3);
        gameBoard.getLeaderBoard().modifyFlightDays(player2, 3);
        gameBoard.getLeaderBoard().modifyFlightDays(player1, 0);
        gameBoard.getLeaderBoard().modifyFlightDays(player3, 2);
        adventureDeck = new AdventureCardDeck();

        card1 = new StarDustCard(gameBoard, "StardDust", 1);
        adventureDeck.add(card1);
        gameBoard.setMainAdventureDeck(adventureDeck);

    }

    @Test
    void drawCard() {
        assertEquals(player2, gameBoard.getLeader());
        when(shipMock1.getNumberOfExposedConnectors()).thenReturn(1);
        when(shipMock2.getNumberOfExposedConnectors()).thenReturn(2);
        when(shipMock3.getNumberOfExposedConnectors()).thenReturn(3);
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        assertEquals(AdventureStatesTypes.UPDATE, gameBoard.getCurrentCard().getCurrentState());
        assertEquals(-1,player1.getDaysThisTurn());
        assertEquals(-2,player2.getDaysThisTurn());
        assertEquals(-3,player3.getDaysThisTurn());
    }

    @Test
    void applyEffect() {

    }
}