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
import net.bytebuddy.description.type.RecordComponentDescription;
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
    Tile tile,tile2;
    Component component,component2;
    Point coordinate = new Point(7, 8);
    LeaderBoard leaderBoard;


    @BeforeEach
    void init(){


        coordinates = new ArrayList<>();
        coordinates.add(new Pair<>(new Point(6,7),Token.RED_GOODS));
        coordinates.add(new Pair<>(new Point(6,7),Token.YELLOW_GOODS));
        coordinates.add(new Pair<>(new Point(6,7),Token.GREEN_GOODS));
        gameBoard = new GameBoard();
        player1 = new Player("pippo", Color.RED, 1);
        player2 = new Player("pluto", Color.BLUE, 2);
        shipBoard = new ShipBoard(1,2,Color.RED);
        player1.setShipBoard(shipBoard);
        component = new ContainerComponent(ComponentType.CABINE, 5);
        component2 = new ContainerComponent(ComponentType.SPECIAL_STORAGE, 4);
        tile2 = new Tile(2,component2);
        tile = new Tile(3,component);
        player1.getShipBoard().insertTile(coordinate, tile);
        player1.getShipBoard().insertTile(new Point(6,7), tile2);
        player1.getShipBoard().addTokenToTile(coordinate, Token.ASTRONAUT);
        player1.getShipBoard().addTokenToTile(coordinate,Token.ASTRONAUT);
        player1.getShipBoard().addTokenToTile(coordinate, Token.ASTRONAUT);
        player1.getShipBoard().addTokenToTile(coordinate,Token.ASTRONAUT);
        player1.getShipBoard().addTokenToTile(new Point(6,7), Token.BLUE_GOODS);
        player1.getShipBoard().addTokenToTile(new Point(6,7), Token.YELLOW_GOODS);
        gameBoard.setLeaderBoard(GameMode.LEVEL_1);
        gameBoard.addPlayerToLeaderboard(player1);
        gameBoard.addPlayerToLeaderboard(player2);
        gameBoard.getLeaderBoard().modifyFlightDays(player1, -2);
        gameBoard.getLeaderBoard().modifyFlightDays(player2, 8);

        adventureDeck = new AdventureCardDeck();
        adventureDeck.add(new AbandonedStationCard(gameBoard, "Abandoned Station", 2,"abandoned station", 1, 2, 3,2,3,1));
        adventureDeck.add(new AbandonedShipCard(gameBoard, "Abandoned Ship", 2, "abandoned station",1, 100, 3));
        gameBoard.setMainAdventureDeck(adventureDeck);


    }

    @Test
    void drawCard() {
        assertEquals(player2.getUsername(),gameBoard.getLeader().getUsername());
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        assertEquals(1, adventureDeck.size());
        assertEquals(player1,gameBoard.getCurrentPlayer());

        assertEquals(AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP, gameBoard.getCurrentCard().getCurrentState());

    }

    @Test
    void checkRequirements() {

    }

    @Test
    void landingOnStationOrShip() {
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        gameBoard.getCurrentCard().landingOnStationOrShip(true);
        assertEquals(AdventureStatesTypes.DECISION_GOODS,gameBoard.getCurrentCard().getCurrentState());

    }
    @Test
    void landingOnStationOrShip2() {
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        gameBoard.getCurrentCard().landingOnStationOrShip(false);
        assertEquals(AdventureStatesTypes.UPDATE,gameBoard.getCurrentCard().getCurrentState());




    }

    @Test
    void finishDecision() {
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        gameBoard.getCurrentCard().landingOnStationOrShip(true);
        gameBoard.getCurrentCard().finishDecision(coordinates);

        assertEquals(AdventureStatesTypes.UPDATE,gameBoard.getCurrentCard().getCurrentState());
        assertEquals(1,adventureDeck.size());
        assertEquals("Abandoned Station", gameBoard.getCurrentCard().getDescription());
        assertEquals(player1, gameBoard.getCurrentPlayer());

    }

    @Test
    void applyEffect() {
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        gameBoard.getCurrentCard().landingOnStationOrShip(true);
        gameBoard.getCurrentCard().finishDecision(coordinates);

        assertEquals(AdventureStatesTypes.UPDATE,gameBoard.getCurrentCard().getCurrentState());


    }
    @Test
    void testToString() {
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        System.out.println(gameBoard.getCurrentCard().toString());
    }}