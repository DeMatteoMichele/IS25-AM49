package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectModifyCrew;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectModifyEnergy;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectWinGoods;
import it.polimi.ingsw.is25am49.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.shipboard.Component;
import it.polimi.ingsw.is25am49.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.model.shipboard.Tile;
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
    ShipBoard shipBoard;
    Tile tile;
    Component component;
    Point coordinate = new Point(7, 8);


    @BeforeEach
    void init(){

        coordinates = new ArrayList<>();
        coordinates.add(new Pair<>(coordinate,Token.ASTRONAUT));
        coordinates.add(new Pair<>(coordinate,Token.ASTRONAUT));
        coordinates.add(new Pair<>(coordinate,Token.ASTRONAUT));
        gameBoard = new GameBoard();
        player1 = new Player("TestPlayer", Color.RED, 1);
        player2 = new Player("TestPlayer2", Color.BLUE, 2);
        shipBoard = new ShipBoard(1,2);
        player1.setShipBoard(shipBoard);
        component = new ContainerComponent(ComponentType.CABINE, 5);
        tile = new Tile(3,component);
        player1.getShipBoard().insertTile(coordinate, tile);
        player1.getShipBoard().addTokenToTile(coordinate, Token.ASTRONAUT);
        player1.getShipBoard().addTokenToTile(coordinate,Token.ASTRONAUT);
        player1.getShipBoard().addTokenToTile(coordinate, Token.ASTRONAUT);
        player1.getShipBoard().addTokenToTile(coordinate,Token.ASTRONAUT);

        gameBoard.addPlayerToLeaderboard(0, player1);
        gameBoard.addPlayerToLeaderboard(3, player2);
        gameBoard.sortLeaderBoard();
        adventureDeck = new AdventureCardDeck();
        adventureDeck.add(new AbandonedStationCard(gameBoard, "Abandoned Station", 2, 1, 100, 3,2,3,1,0));
        adventureDeck.add(new AbandonedShipCard(gameBoard, "Abandoned Ship", 2, 1, 100, 3));
        gameBoard.setMainAdventureDeck(adventureDeck);


    }

    @Test
    void drawCard() {
        gameBoard.drawAdventureCard();
        assertEquals(1, adventureDeck.size());
        assertEquals(player1,gameBoard.getCurrentPlayer());
        assertEquals(player2,gameBoard.getLeader());
        assertEquals(AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP, gameBoard.getCurrentCard().getCurrentState());

    }

    @Test
    void checkRequirements() {

    }

    @Test
    void landingOnStationOrShip() {
        gameBoard.drawAdventureCard();
        gameBoard.getCurrentCard().landingOnStationOrShip(true);
        assertEquals(AdventureStatesTypes.DECISION_GOODS,gameBoard.getCurrentCard().getCurrentState());

    }
    @Test
    void landingOnStationOrShip2() {
        gameBoard.drawAdventureCard();
        gameBoard.getCurrentCard().landingOnStationOrShip(false);
        assertEquals(AdventureStatesTypes.UPDATE,gameBoard.getCurrentCard().getCurrentState());




    }

    @Test
    void finishDecision() {
        gameBoard.drawAdventureCard();
        gameBoard.getCurrentCard().landingOnStationOrShip(true);
        gameBoard.getCurrentCard().finishDecision(coordinates);

        assertEquals(AdventureStatesTypes.UPDATE,gameBoard.getCurrentCard().getCurrentState());
        assertEquals(1,adventureDeck.size());
        assertEquals("Abandoned Station", gameBoard.getCurrentCard().getDescription());
        assertEquals(player1, gameBoard.getCurrentPlayer());

    }

    @Test
    void applyEffect() {
        gameBoard.drawAdventureCard();
        gameBoard.getCurrentCard().landingOnStationOrShip(true);
        gameBoard.getCurrentCard().finishDecision(coordinates);

        assertEquals(AdventureStatesTypes.UPDATE,gameBoard.getCurrentCard().getCurrentState());
        assertEquals(-2,gameBoard.getCurrentPlayer().getDaysThisTurn());

    }
}