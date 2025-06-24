package it.polimi.ingsw.is25am49.server.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.adventurecards.MeteorRainCard;
import it.polimi.ingsw.is25am49.server.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.server.model.cardeffects.EffectMeteoriti;
import it.polimi.ingsw.is25am49.shared.enums.*;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.shared.enums.Color;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MeteorRainCardTest {

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
    MeteorRainCard card1;
    MeteorRainCard spyCard;


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
        adventureDeck = new AdventureCardDeck();
        listEffects.add(new EffectMeteoriti("Meteorite1", 1, Orientation.NORTH));
        listEffects.add(new EffectMeteoriti("Meteorite2", 2, Orientation.SOUTH));
        listEffects.add(new EffectMeteoriti("Meteorite3", 1, Orientation.EAST));
        card1 = new MeteorRainCard(gameBoard, "Meteorrain", 2,"meteor", listEffects);
        spyCard = Mockito.spy(card1);

        gameBoard.setMainAdventureDeck(adventureDeck);
    }

    @Test
    void testDrawCard() {
        adventureDeck.add(card1);
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        assertEquals(AdventureStatesTypes.ROLL_DICES, gameBoard.getCurrentCard().getCurrentState());
    }

    @Test
    void testFlow() {
        adventureDeck.add(spyCard);
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        assertEquals(AdventureStatesTypes.ROLL_DICES, gameBoard.getCurrentCard().getCurrentState());
        gameBoard.getCurrentCard().rollDices();
        gameBoard.getCurrentCard().rollDices();
        gameBoard.getCurrentCard().rollDices();
        verify(spyCard, Mockito.times(12)).applyEffect();
    }

    @Test
    void testExecution() {
        adventureDeck.add(spyCard);
        gameBoard.drawAdventureCard(gameBoard.getLeader());

        // Setup dei giocatori senza batterie (non possono difendersi)
        when(shipMock1.getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(0);
        when(shipMock4.getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(0);

        // Setup per player2 (ha batterie e componenti)
        when(shipMock2.getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(2);
        // Per il primo effetto (livello 1) - ha scudi disponibili
        ArrayList<Point> ListOfShields = new ArrayList<>();
        ListOfShields.add(new Point(1, 1));
        ListOfShields.add(new Point(2, 2));
        ListOfShields.add(new Point(3, 3));

        ArrayList<Point> ListOfCannons = new ArrayList<>();
        ListOfCannons.add(new Point(1, 1));
        ListOfCannons.add(new Point(2, 2));
        ListOfCannons.add(new Point(3, 3));

        when(shipMock2.getActivatableComponents(ComponentType.SHIELD)).thenReturn(ListOfShields);
        // Per il secondo effetto (livello 2) - ha cannoni disponibili
        when(shipMock2.getActivatableComponents(ComponentType.DOUBLE_CANNON)).thenReturn(ListOfCannons);

        // Setup per player3 (ha batterie ma non ha componenti per difendersi)
        when(shipMock3.getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(1);
        when(shipMock3.getActivatableComponents(ComponentType.SHIELD)).thenReturn(new ArrayList<>());
        when(shipMock3.getActivatableComponents(ComponentType.DOUBLE_CANNON)).thenReturn(new ArrayList<>());


        // Test primo effetto (Meteorite piccolo - livello 1)
        gameBoard.getCurrentCard().rollDices();
        assertEquals(player2, gameBoard.getCurrentPlayer());

        // Simula player2 che decide di non usare batterie, poi per gli altri giocatori si applicano l'effetto a catena
        List<Pair<Point, Token>> emptyBatteries = new ArrayList<>();
        gameBoard.getCurrentCard().finishDecision(emptyBatteries);


        // Test secondo effetto (Meteorite grande - livello 2)
        gameBoard.getCurrentCard().rollDices();
        assertEquals(player2, gameBoard.getCurrentPlayer());

        // Simula player2 che usa batterie per attivare un cannone
        List<Pair<Point, Token>> batteries = new ArrayList<>();
        batteries.add(new Pair<>(new Point(1, 1), Token.BATTERY_CELLS));
        gameBoard.getCurrentCard().finishDecision(batteries);

        // Simula la scelta del cannone di Player2
        Set<Point> cannonCoordinates = new HashSet<>();
        cannonCoordinates.add(new Point(2, 1));
        gameBoard.getCurrentCard().finishDecision(cannonCoordinates);

        // Test terzo effetto (Meteorite piccolo - livello 1). Simulo che nessuno abbia più batterie
        when(shipMock2.getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(0);
        when(shipMock3.getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(0);
        gameBoard.getCurrentCard().rollDices();

        // Verifica che la carta sia terminata dopo tutti gli effetti
        verify(spyCard, Mockito.times(12)).applyEffect(); // 4 giocatori x 3 effetti
        assertTrue(gameBoard.getCurrentCard().getIsFinished());
        assertEquals(AdventureStatesTypes.UPDATE, gameBoard.getCurrentCard().getCurrentState());
    }

    @Test
    void testToString() {
        adventureDeck.add(card1);
        gameBoard.drawAdventureCard(gameBoard.getLeader());

        System.out.println(gameBoard.getCurrentCard().toString());
    }

}