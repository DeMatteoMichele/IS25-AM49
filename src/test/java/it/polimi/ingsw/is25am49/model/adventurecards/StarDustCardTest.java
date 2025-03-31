package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectLoseGoods;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectWinGoods;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.shipboard.Component;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.shipboard.Tile;
import javafx.util.Pair;
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
        gameBoard.addPlayerToLeaderboard(0, player1);
        gameBoard.addPlayerToLeaderboard(3, player2);
        gameBoard.sortLeaderBoard();
        adventureDeck = new AdventureCardDeck();
        listEffects.add(new EffectWinGoods("TestEffect", 3, 2, 1, 1));
        listEffects.add(new EffectLoseGoods("TestEffect2", 3));
        card1 = new StarDustCard(gameBoard, "StardDust", 2, 1, 100);
        adventureDeck.add(card1);
        gameBoard.setMainAdventureDeck(adventureDeck);

    }

    @Test
    void drawCard() {
        assertEquals(gameBoard.getLeader(), player2);
        gameBoard.getCurrentCard().drawCard();


        assertEquals(AdventureStatesTypes.EFFECT, gameBoard.getCurrentCard().getCurrentState());

        assertEquals(AdventureStatesTypes.UPDATE, gameBoard.getCurrentCard().getCurrentState());
    }

    @Test
    void applyEffect() {

    }
}