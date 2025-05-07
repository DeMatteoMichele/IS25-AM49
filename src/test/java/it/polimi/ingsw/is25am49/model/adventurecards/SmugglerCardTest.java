package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.server.model.adventurecards.SmugglerCard;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectLoseGoods;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectWinGoods;
import it.polimi.ingsw.is25am49.server.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import javafx.util.Pair;
import it.polimi.ingsw.is25am49.server.model.enums.GameMode;
import it.polimi.ingsw.is25am49.server.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
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
    ShipBoard shipMock= Mockito.mock(ShipBoard.class);
    List<CardEffect> listEffects = new ArrayList<>();
    AdventureCard card1;

    @BeforeEach
    void init(){
        // Initialize the game board and player1
        gameBoard = new GameBoard();
        player1 = new Player("TestPlayer", Color.RED, 1);
        player2 = new Player("TestPlayer2", Color.BLUE, 2);
//        player1.setShipBoard(shipMock1);
        gameBoard.setLeaderBoard(GameMode.LEVEL_1);
        gameBoard.addPlayerToLeaderboard(player2);
        gameBoard.addPlayerToLeaderboard(player1);
//        gameBoard.getLeaderBoard().modifyFlightDays(player2,3);
//        gameBoard.getLeaderBoard().modifyFlightDays(player1,0);
        adventureDeck = new AdventureCardDeck();
        listEffects.add(new EffectWinGoods("WinGoods", 3,2,1,1));
        listEffects.add(new EffectLoseGoods("LoseGoods", 3));
        card1=new SmugglerCard(gameBoard, "SmugglerCardTest", 1, "smuggler",2,  3, 3,2,1,1,3 );
        adventureDeck.add(card1);
        gameBoard.setMainAdventureDeck(adventureDeck);

    }

    @Test
    void drawCard() {
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        assertEquals(0, adventureDeck.size());

    }


    @Test
    void applyEffect() {
    }

    @Test
    void CheckNotEnoughFireNorBatteries() {
        when(shipMock.getTotalFirePower()).thenReturn(1.0);
        when(shipMock.getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(0);
        player2.setShipBoard(shipMock);
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        assertEquals(0, adventureDeck.size());
        assertEquals(gameBoard.getLeaderBoard().getLeader(),player2);
        assertEquals(AdventureStatesTypes.UPDATE, gameBoard.getCurrentCard().getCurrentState());
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
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        assertEquals(0, adventureDeck.size());
        assertEquals(gameBoard.getLeaderBoard().getLeader(),player2);
        assertEquals(gameBoard.getLeader(), gameBoard.getCurrentPlayer());
        assertEquals(AdventureStatesTypes.DECISION_BATTERIES, gameBoard.getCurrentCard().getCurrentState());
    }

    @Test
    void CheckEnoughFirePowerToDrawButNotEnoughBatteriesToWin(){
        when(shipMock.getTotalFirePower()).thenReturn(3.0);
        when(shipMock.getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(0);

        player2.setShipBoard(shipMock);
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        assertEquals(gameBoard.getLeaderBoard().getLeader(),player2);
        assertEquals(player1, gameBoard.getCurrentPlayer());
        assertEquals(AdventureStatesTypes.UPDATE, gameBoard.getCurrentCard().getCurrentState());
    }

    @Test
    void CheckEnoughFire() {
        when(shipMock.getTotalFirePower()).thenReturn(10.0);
        player2.setShipBoard(shipMock);
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        assertEquals(0, adventureDeck.size());
        assertEquals(gameBoard.getLeaderBoard().getLeader(),player2);
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
        gameBoard.drawAdventureCard(gameBoard.getLeader());
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
                assertEquals(AdventureStatesTypes.UPDATE, gameBoard.getCurrentCard().getCurrentState());
                assertEquals(EffectLoseGoods.class, gameBoard.getCurrentCard().getCurrentEffect().getClass());

    }


    @Test
    void testApplyEffect() {
        when(shipMock.getTotalFirePower()).thenReturn(10.0);
        player2.setShipBoard(shipMock);

        gameBoard.drawAdventureCard(gameBoard.getLeader());

        assertEquals(AdventureStatesTypes.DECISION_GOODS, gameBoard.getCurrentCard().getCurrentState());
        assertEquals(EffectWinGoods.class, gameBoard.getCurrentCard().getCurrentEffect().getClass());


        List<Pair<Point, Token>> goodsCoordinates = new ArrayList<>();
        goodsCoordinates.add(new Pair<>(new Point(1, 1), Token.RED_GOODS));
        goodsCoordinates.add(new Pair<>(new Point(2, 2), Token.BLUE_GOODS));

        gameBoard.getCurrentCard().finishDecision(goodsCoordinates);

        assertEquals(-2, gameBoard.getCurrentPlayer().getDaysThisTurn()); // verifica che i giorni persi siano impostati
        assertEquals(AdventureStatesTypes.UPDATE, gameBoard.getCurrentCard().getCurrentState()); // verifica lo stato finale
    }


    @Test
    void testToString() {
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        System.out.println(gameBoard.getCurrentCard().toString());
    }

}