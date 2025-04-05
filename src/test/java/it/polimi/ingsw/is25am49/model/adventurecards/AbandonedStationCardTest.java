package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.LeaderBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.adventurecards.AbandonedShipCard;
import it.polimi.ingsw.is25am49.server.model.adventurecards.AbandonedStationCard;
import it.polimi.ingsw.is25am49.server.model.enums.*;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.shipboard.Component;
import it.polimi.ingsw.is25am49.server.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
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
    List<Pair<Point, Token>> coordinates;
    ShipBoard shipBoard;
    Tile tile;
    Component component;
    Point coordinate = new Point(7, 8);
    LeaderBoard leaderBoard;


    @BeforeEach
    void init(){


        coordinates = new ArrayList<>();
        coordinates.add(new Pair<>(coordinate,Token.ASTRONAUT));
        coordinates.add(new Pair<>(coordinate,Token.ASTRONAUT));
        coordinates.add(new Pair<>(coordinate,Token.ASTRONAUT));
        gameBoard = new GameBoard();
        player1 = new Player("pippo", Color.RED, 1);
        player2 = new Player("pluto", Color.BLUE, 2);
        shipBoard = new ShipBoard(1,2);
        player1.setShipBoard(shipBoard);
        component = new ContainerComponent(ComponentType.CABINE, 5);
        tile = new Tile(3,component);
        player1.getShipBoard().insertTile(coordinate, tile);
        player1.getShipBoard().addTokenToTile(coordinate, Token.ASTRONAUT);
        player1.getShipBoard().addTokenToTile(coordinate,Token.ASTRONAUT);
        player1.getShipBoard().addTokenToTile(coordinate, Token.ASTRONAUT);
        player1.getShipBoard().addTokenToTile(coordinate,Token.ASTRONAUT);
        gameBoard.setLeaderBoard(GameMode.LEVEL_1);
        gameBoard.addPlayerToLeaderboard(player1);
        gameBoard.addPlayerToLeaderboard(player2);
        gameBoard.getLeaderBoard().modifyFlightDays(player1, -2);
        gameBoard.getLeaderBoard().modifyFlightDays(player2, 8);

        adventureDeck = new AdventureCardDeck();
        adventureDeck.add(new AbandonedStationCard(gameBoard, "Abandoned Station", 2, 1, 2, 3,2,3,1));
        adventureDeck.add(new AbandonedShipCard(gameBoard, "Abandoned Ship", 2, 1, 100, 3));
        gameBoard.setMainAdventureDeck(adventureDeck);


    }

    @Test
    void drawCard() {
        assertEquals(player2.getUsername(),gameBoard.getLeader().getUsername());
        gameBoard.drawAdventureCard(gameBoard.getLeaderBoard().getLeader());
        assertEquals(1, adventureDeck.size());
        assertEquals(player1,gameBoard.getCurrentPlayer());

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