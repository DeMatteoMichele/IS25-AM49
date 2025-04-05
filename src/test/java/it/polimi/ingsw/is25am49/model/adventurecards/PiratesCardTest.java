package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.adventurecards.PiratesCard;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectCannonata;
import it.polimi.ingsw.is25am49.server.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.enums.*;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.shipboard.Component;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class PiratesCardTest {

    GameBoard gameBoard;
    AdventureCardDeck adventureDeck;
    Player player1;
    Player player2;
    Player player3;
    Player player4;
    ShipBoard shipMock2 = Mockito.mock(ShipBoard.class);
    ShipBoard shipMock1 = Mockito.mock(ShipBoard.class);
    ShipBoard shipMock4 = Mockito.mock(ShipBoard.class);
    ShipBoard shipMock3 = Mockito.mock(ShipBoard.class);
    List<CardEffect> listEffects = new ArrayList<>();
    PiratesCard card1;


    @BeforeEach
    void init() {
        gameBoard = new GameBoard();
        player1 = new Player("TestPlayer", Color.RED, 1);
        player2 = new Player("TestPlayer2", Color.BLUE, 2);
        player3 = new Player("TestPlayer3", Color.GREEN, 3);
        player4 = new Player("TestPlayer4", Color.YELLOW, 4);
        player2.setShipBoard(shipMock2);
        player1.setShipBoard(shipMock1);
        player3.setShipBoard(shipMock3);
        player4.setShipBoard(shipMock4);
        gameBoard.setLeaderBoard(GameMode.LEVEL_1);
        gameBoard.addPlayerToLeaderboard(player2);
        gameBoard.addPlayerToLeaderboard(player4);
        gameBoard.addPlayerToLeaderboard(player3);
        gameBoard.addPlayerToLeaderboard(player1);
//        gameBoard.getLeaderBoard().modifyFlightDays(player2,3);
//        gameBoard.getLeaderBoard().modifyFlightDays(player1,0);
//        gameBoard.getLeaderBoard().modifyFlightDays(player3,1);
//        gameBoard.getLeaderBoard().modifyFlightDays(player4,2);
        adventureDeck = new AdventureCardDeck();
        listEffects.add(new EffectCannonata("Cannonata1", 1, Orientation.NORTH));
        listEffects.add(new EffectCannonata("Cannonata2", 2, Orientation.SOUTH));
        listEffects.add(new EffectCannonata("Cannonata3", 1, Orientation.EAST));
        card1 = new PiratesCard(gameBoard, "PiratesCardTest", 2, 1, 3, 100, listEffects);
        adventureDeck.add(card1);
        gameBoard.setMainAdventureDeck(adventureDeck);
    }

    @Test
    void drawCard() {
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        assertEquals(0, adventureDeck.size());
        assertEquals(gameBoard.getCurrentCard().getCurrentState(), AdventureStatesTypes.ROLL_DICES);
    }

    @Test
    void checkThatAllLoseInDifferentSituationsExceptLast() {
        when(shipMock1.getTotalFirePower()).thenReturn(1.0);
        when(shipMock1.getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(3);
        when(shipMock2.getTotalFirePower()).thenReturn(1.0);
        when(shipMock2.getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(3);


        ArrayList<Point> activatablePoints = new ArrayList<>();
        activatablePoints.add(new Point(1, 1));
        activatablePoints.add(new Point(2, 2));
        activatablePoints.add(new Point(3, 3));

        when(shipMock1.getActivatableComponents(ComponentType.DOUBLE_CANNON)).thenReturn(activatablePoints);
        when(shipMock2.getActivatableComponents(ComponentType.DOUBLE_CANNON)).thenReturn(activatablePoints);


        Tile tileMock1 = Mockito.mock(Tile.class);
        Tile tileMock2 = Mockito.mock(Tile.class);
        Tile tileMock3 = Mockito.mock(Tile.class);

        when(shipMock1.getTile(new Point(1, 1))).thenReturn(tileMock1);
        when(shipMock1.getTile(new Point(2, 2))).thenReturn(tileMock2);
        when(shipMock1.getTile(new Point(3, 3))).thenReturn(tileMock3);
        when(shipMock2.getTile(new Point(1, 1))).thenReturn(tileMock1);
        when(shipMock2.getTile(new Point(2, 2))).thenReturn(tileMock2);
        when(shipMock2.getTile(new Point(3, 3))).thenReturn(tileMock3);

        when(tileMock1.getFirePower()).thenReturn(1.0);
        when(tileMock2.getFirePower()).thenReturn(1.0);
        when(tileMock3.getFirePower()).thenReturn(1.0);

        player2.setShipBoard(shipMock2);
        player1.setShipBoard(shipMock1);
        gameBoard.drawAdventureCard(gameBoard.getLeader());
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
        when(shipMock2.getTotalFirePower()).thenReturn(2.0);
        gameBoard.getCurrentCard().finishDecision(cannonCoordinates);

        List<Pair<Point, Token>> batteryCoordinates2 = new ArrayList<>();
        batteryCoordinates2.add(new Pair<>(new Point(1, 1), Token.BATTERY_CELLS));
        batteryCoordinates2.add(new Pair<>(new Point(2, 2), Token.BATTERY_CELLS));
        batteryCoordinates2.add(new Pair<>(new Point(3, 3), Token.BATTERY_CELLS));
        gameBoard.getCurrentCard().finishDecision(batteryCoordinates2);
        assertEquals(AdventureStatesTypes.DECISION_CANNON, gameBoard.getCurrentCard().getCurrentState());
        Set<Point> cannonCoordinates2 = new HashSet<>();
        cannonCoordinates2.add(new Point(1, 1));
        cannonCoordinates2.add(new Point(2, 2));
        cannonCoordinates2.add(new Point(3, 3));
        when(shipMock1.getTotalFirePower()).thenReturn(4.0);
        gameBoard.getCurrentCard().finishDecision(cannonCoordinates2);
        assertEquals(AdventureStatesTypes.ROLL_DICES, gameBoard.getCurrentCard().getCurrentState());


        assertEquals(gameBoard.getCurrentCard().getDefeatedPlayers().getFirst(), player2);
        assertEquals(gameBoard.getCurrentCard().getDefeatedPlayers().get(1), player4);
        assertEquals(gameBoard.getCurrentCard().getDefeatedPlayers().get(2), player3);
        assertEquals(gameBoard.getCurrentCard().getDefeatedPlayers().size(), 3);
        assertEquals(-1, player2.getDaysThisTurn());
        assertEquals(100, player1.getCredits());
        assertEquals(-1, player4.getDaysThisTurn());
        assertEquals(-1, player4.getDaysThisTurn());
        assertEquals(-1, player1.getDaysThisTurn());
    }

    @Test
    void checkForDefeatedPlayers() {
        when(shipMock2.getTotalFirePower()).thenReturn(1.0);
        when(shipMock2.getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(1);
        when(shipMock4.getTotalFirePower()).thenReturn(3.0);
        when(shipMock4.getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(0);
        when(shipMock3.getTotalFirePower()).thenReturn(4.0);
        when(shipMock3.getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(1);
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        assertEquals(gameBoard.getCurrentCard().getDefeatedPlayers().getFirst(), player2);
        assertEquals(1, gameBoard.getCurrentCard().getDefeatedPlayers().size());
    }

    @Test
    void CannonadePhase() {
        checkThatAllLoseInDifferentSituationsExceptLast();
        ArrayList<Point> activatablePoints = new ArrayList<>();
        activatablePoints.add(new Point(1, 1));
        activatablePoints.add(new Point(2, 2));
        activatablePoints.add(new Point(3, 3));
        when(shipMock2.getActivatableComponents(ComponentType.SHIELD)).thenReturn(activatablePoints);
        assertEquals(gameBoard.getCurrentPlayer(), player2);
        gameBoard.getCurrentCard().rollDices();
        assertEquals(AdventureStatesTypes.DECISION_BATTERIES, gameBoard.getCurrentCard().getCurrentState());
        List<Pair<Point, Token>> batteryCoordinates = new ArrayList<>();
        batteryCoordinates.add(new Pair<>(new Point(1, 1), Token.BATTERY_CELLS));
        batteryCoordinates.add(new Pair<>(new Point(2, 2), Token.BATTERY_CELLS));
        batteryCoordinates.add(new Pair<>(new Point(3, 3), Token.BATTERY_CELLS));
        gameBoard.getCurrentCard().finishDecision(batteryCoordinates);
        assertEquals(AdventureStatesTypes.DECISION_SHIELDS, gameBoard.getCurrentCard().getCurrentState());
        Set<Point> shieldCoordinates = new HashSet<>();
        shieldCoordinates.add(new Point(1, 1));
        shieldCoordinates.add(new Point(2, 2));
        shieldCoordinates.add(new Point(3, 3));
        gameBoard.getCurrentCard().finishDecision(shieldCoordinates);
        when(shipMock3.getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(4);
        player3.setShipBoard(shipMock3);
        gameBoard.getCurrentCard().rollDices();//second effect, big cannonade cant do anything
        gameBoard.getCurrentCard().rollDices();//third effect
        assertEquals(AdventureStatesTypes.DECISION_BATTERIES, gameBoard.getCurrentCard().getCurrentState());
        assertEquals(gameBoard.getCurrentPlayer(), player2);
        when(shipMock3.getActivatableComponents(ComponentType.SHIELD)).thenReturn(activatablePoints);
        gameBoard.getCurrentCard().finishDecision(new ArrayList<>());
        assertEquals(gameBoard.getCurrentPlayer(), player3);
        assertEquals(AdventureStatesTypes.DECISION_BATTERIES, gameBoard.getCurrentCard().getCurrentState());
        gameBoard.getCurrentCard().finishDecision(batteryCoordinates);
        gameBoard.getCurrentCard().finishDecision(shieldCoordinates);
        assertEquals(AdventureStatesTypes.UPDATE, gameBoard.getCurrentCard().getCurrentState());
        assertEquals(100, player1.getCredits());
        assertEquals(-1, player2.getDaysThisTurn());
        assertEquals(-1, player3.getDaysThisTurn());
        assertEquals(-1, player4.getDaysThisTurn());
        assertEquals(-1, player1.getDaysThisTurn());
    }


}