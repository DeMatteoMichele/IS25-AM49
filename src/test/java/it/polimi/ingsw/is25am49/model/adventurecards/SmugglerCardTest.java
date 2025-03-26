package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.cardeffects.EffectLoseGoods;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectWinGoods;
import it.polimi.ingsw.is25am49.model.enums.*;
import it.polimi.ingsw.is25am49.model.enums.Color;
import javafx.util.Pair;
import org.mockito.Mock;
import it.polimi.ingsw.is25am49.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.shipboard.Component;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.model.shipboard.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SmugglerCardTest {

    GameBoard gameBoard;
    AdventureCardDeck adventureDeck;
    Player player1;
    Player player2;
    ShipBoard ship1;
    ShipBoard shipMock= Mockito.mock(ShipBoard.class);
    List<CardEffect> listEffects = new ArrayList<>();
    AdventureCard card1;
    Component shipBoardComponent;
    Tile tile;


    @BeforeEach
    void init(){
        // Initialize the game board and player1
        gameBoard = new GameBoard();
        player1 = new Player("TestPlayer", Color.RED, 1);
        player2 = new Player("TestPlayer2", Color.BLUE, 2);
//        player1.setShipBoard(shipMock);
        gameBoard.addPlayerToLeaderboard(0, player1);
        gameBoard.addPlayerToLeaderboard(3, player2);
        gameBoard.sortLeaderBoard();
        adventureDeck = new AdventureCardDeck();
        listEffects.add(new EffectWinGoods("TestEffect", 3,2,1,1));
        listEffects.add(new EffectLoseGoods("TestEffect2", 3));
        card1=new SmugglerCard(gameBoard, "SmugglerCardTest", 2, 1, 100, 3, 0, listEffects);
        adventureDeck.add(card1);
        gameBoard.setMainAdventureDeck(adventureDeck);

    }

    @Test
    void drawCard() {
        gameBoard.drawAdventureCard();
        assertEquals(0, adventureDeck.size());
        assertEquals(gameBoard.getLeaderBoard().getFirst().getValue(),player2);
        assertEquals(gameBoard.getLeader(), gameBoard.getCurrentPlayer());
    }


    @Test
    void applyEffect() {
    }

    @Test
    void CheckNotEnoughFireNorBatteries() {
        when(shipMock.getTotalFirePower()).thenReturn(1.0);
        when(shipMock.getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(0);
        player2.setShipBoard(shipMock);
        gameBoard.drawAdventureCard();
        assertEquals(0, adventureDeck.size());
        assertEquals(gameBoard.getLeaderBoard().getFirst().getValue(),player2);
        assertEquals(gameBoard.getLeader(), gameBoard.getCurrentPlayer());
        assertEquals(AdventureStatesTypes.DECISION_GOODS, gameBoard.getCurrentCard().getCurrentState());
    }

    @Test
    void CheckNotEnoughFireButEnoughBatteriesAndCannons() {
        when(shipMock.getTotalFirePower()).thenReturn(1.0);
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
        assertEquals(0, adventureDeck.size());
        assertEquals(gameBoard.getLeaderBoard().getFirst().getValue(),player2);
        assertEquals(gameBoard.getLeader(), gameBoard.getCurrentPlayer());
        assertEquals(AdventureStatesTypes.DECISION_BATTERIES, gameBoard.getCurrentCard().getCurrentState());
    }

    @Test
    void CheckEnoughFirePowerToDrawButNotEnoughBatteriesToWin(){
        when(shipMock.getTotalFirePower()).thenReturn(3.0);
        when(shipMock.getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(0);

        player2.setShipBoard(shipMock);
        gameBoard.drawAdventureCard();
        assertEquals(0, adventureDeck.size());
        assertEquals(gameBoard.getLeaderBoard().getFirst().getValue(),player2);
        assertEquals(player1, gameBoard.getCurrentPlayer());
        assertEquals(AdventureStatesTypes.DECISION_GOODS, gameBoard.getCurrentCard().getCurrentState());
    }

    @Test
    void CheckEnoughFire() {
        when(shipMock.getTotalFirePower()).thenReturn(10.0);
        player2.setShipBoard(shipMock);
        gameBoard.drawAdventureCard();
        assertEquals(0, adventureDeck.size());
        assertEquals(gameBoard.getLeaderBoard().getFirst().getValue(),player2);
        assertEquals(gameBoard.getLeader(), gameBoard.getCurrentPlayer());
        assertEquals(AdventureStatesTypes.DECISION_GOODS, gameBoard.getCurrentCard().getCurrentState());
    }

    @Test
    void ActivationOfCannons(){
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
                when(shipMock.getTotalFirePower()).thenReturn(2.0);
                gameBoard.getCurrentCard().finishDecision(cannonCoordinates);
                assertEquals(AdventureStatesTypes.DECISION_GOODS, gameBoard.getCurrentCard().getCurrentState());
                assertEquals(gameBoard.getCurrentCard().getCurrentEffect(), listEffects.get(1));

    }


    @Test
    void testApplyEffect() {
        when(shipMock.getTotalFirePower()).thenReturn(10.0);
        player2.setShipBoard(shipMock);

        gameBoard.drawAdventureCard();

        assertEquals(AdventureStatesTypes.DECISION_GOODS, gameBoard.getCurrentCard().getCurrentState());
        assertEquals(listEffects.get(0), gameBoard.getCurrentCard().getCurrentEffect()); // EffectWinGoods

        List<Pair<Point, Token>> goodsCoordinates = new ArrayList<>();
        goodsCoordinates.add(new Pair<>(new Point(1, 1), Token.RED_GOODS));
        goodsCoordinates.add(new Pair<>(new Point(2, 2), Token.BLUE_GOODS));

        gameBoard.getCurrentCard().finishDecision(goodsCoordinates);

        assertEquals(2, gameBoard.getCurrentPlayer().getDaysThisTurn()); // verifica che i giorni persi siano impostati
        assertEquals(AdventureStatesTypes.UPDATE, gameBoard.getCurrentCard().getCurrentState()); // verifica lo stato finale
    }

}