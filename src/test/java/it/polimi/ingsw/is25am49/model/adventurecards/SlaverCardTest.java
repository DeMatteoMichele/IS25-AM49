package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.shipboard.Component;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.model.shipboard.Tile;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SlaverCardTest {

    GameBoard gameBoard;
    AdventureCardDeck adventureDeck;
    Player player1;
    Player player2;
    ShipBoard ship1;
    ShipBoard shipMock = Mockito.mock(ShipBoard.class);
    List<CardEffect> listEffects = new ArrayList<>();
    AdventureCard card1;
    Component shipBoardComponent;
    Tile tile;


    @BeforeEach
    void init() {
        // Initialize the game board and player1
        gameBoard = new GameBoard();
        player1 = new Player("TestPlayer", Color.RED, 1);
        player2 = new Player("TestPlayer2", Color.BLUE, 2);
//        player1.setShipBoard(shipMock1);
        gameBoard.addPlayerToLeaderboard(player1);
        gameBoard.addPlayerToLeaderboard(player2);
        gameBoard.getLeaderBoard().modifyFlightDays(player2,3);
        gameBoard.getLeaderBoard().modifyFlightDays(player1,0);
        adventureDeck = new AdventureCardDeck();
        card1 = new SlaverCard(gameBoard, "SlaverCardTest", 2, 1, 100, 3, 3);
        adventureDeck.add(card1);
        gameBoard.setMainAdventureDeck(adventureDeck);

    }

    @Test
    void drawCard() {
        gameBoard.drawAdventureCard();
        assertEquals(0, adventureDeck.size());
        assertEquals(gameBoard.getLeaderBoard().getLeader(), player2);
        assertEquals(gameBoard.getLeader(), gameBoard.getCurrentPlayer());
    }

    @Test
    void CheckEnoughFire() {
        when(shipMock.getTotalFirePower()).thenReturn(10.0);
        player2.setShipBoard(shipMock);
        gameBoard.drawAdventureCard();
        assertEquals(0, adventureDeck.size());
        assertEquals(gameBoard.getLeaderBoard().getLeader(), player2);
        assertEquals(gameBoard.getLeader(), gameBoard.getCurrentPlayer());
        assertEquals(AdventureStatesTypes.UPDATE, gameBoard.getCurrentCard().getCurrentState());
        assertEquals(100, player2.getCredits());
        assertEquals(-2, player2.getDaysThisTurn());
    }

    @Test
    void ActivationOfCannons() {
        when(shipMock.getTotalFirePower()).thenReturn(2.0);
        when(shipMock.getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(3);
        ArrayList<Point> activatablePoints = new ArrayList<>();
        activatablePoints.add(new Point(1, 1));
        activatablePoints.add(new Point(2, 2));
        activatablePoints.add(new Point(3, 3));

        when(shipMock.getActivatableComponents(ComponentType.DOUBLE_CANNON)).thenReturn(activatablePoints);

        Tile tileMock1 = Mockito.mock(Tile.class);
        Tile tileMock2 = Mockito.mock(Tile.class);
        Tile tileMock3 = Mockito.mock(Tile.class);

        when(shipMock.getTile(new Point(1, 1))).thenReturn(tileMock1);
        when(shipMock.getTile(new Point(2, 2))).thenReturn(tileMock2);
        when(shipMock.getTile(new Point(3, 3))).thenReturn(tileMock3);

        when(tileMock1.getFirePower()).thenReturn(1.0);
        when(tileMock2.getFirePower()).thenReturn(1.0);
        when(tileMock3.getFirePower()).thenReturn(1.0);

        player2.setShipBoard(shipMock);
        gameBoard.drawAdventureCard();
        assertEquals(gameBoard.getCurrentCard().getCurrentState(), AdventureStatesTypes.DECISION_BATTERIES);
        List<Pair<Point, Token>> batteryCoordinates = new ArrayList<>();
        batteryCoordinates.add(new Pair<>(new Point(1, 1), Token.BATTERY_CELLS));
        batteryCoordinates.add(new Pair<>(new Point(2, 2), Token.BATTERY_CELLS));
        batteryCoordinates.add(new Pair<>(new Point(3, 3), Token.BATTERY_CELLS));
        gameBoard.getCurrentCard().finishDecision(batteryCoordinates);
        assertEquals(AdventureStatesTypes.DECISION_CANNON, gameBoard.getCurrentCard().getCurrentState());
        Set<Point> cannonCoordinates = new HashSet<>();
        cannonCoordinates.add(new Point(1, 1));
        cannonCoordinates.add(new Point(2, 2));
        cannonCoordinates.add(new Point(3, 3));
        when(shipMock.getTotalFirePower()).thenReturn(3.0);
        gameBoard.getCurrentCard().finishDecision(cannonCoordinates);
        assertEquals(0, player2.getDaysThisTurn());
        assertEquals(AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS, gameBoard.getCurrentCard().getCurrentState());
        List<Pair<Point, Token>> astronautsCoordinates = new ArrayList<>();
        astronautsCoordinates.add(new Pair<>(new Point(1, 1), Token.ASTRONAUT));
        astronautsCoordinates.add(new Pair<>(new Point(2, 2), Token.ASTRONAUT));
        astronautsCoordinates.add(new Pair<>(new Point(3, 3), Token.ASTRONAUT));
        gameBoard.getCurrentCard().finishDecision(astronautsCoordinates);
        assertEquals(AdventureStatesTypes.UPDATE, gameBoard.getCurrentCard().getCurrentState());
    }


}