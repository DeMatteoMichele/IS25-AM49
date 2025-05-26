package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.adventurecards.Planet;
import it.polimi.ingsw.is25am49.server.model.adventurecards.PlanetCard;
import it.polimi.ingsw.is25am49.shared.enums.*;
import it.polimi.ingsw.is25am49.server.model.shipboard.ContainerComponent;
import it.polimi.ingsw.is25am49.server.model.shipboard.Component;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
import it.polimi.ingsw.is25am49.shared.enums.Color;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class PlanetCardTest {

    Player player1, player2,player3;
    Planet planet1, planet2,planet3;
    ShipBoard ship1, ship2,ship3;
    GameBoard gameBoard;
    AdventureCardDeck deck;
    List<Planet> planets;
    List<Planet> planets2;
    List<Pair<Point, Token>> coordinates,coordinates2;
    Point coordinate1 = new Point(7,8);
    Point coordinate2 = new Point(8,7);
    Component component1,component2,component3;
    Tile tile1,tile2,tile3;


    @BeforeEach
    public void init() {
        gameBoard = new GameBoard();
        gameBoard.setLeaderBoard(GameMode.LEVEL_1);
        player1 = new Player("pippo", Color.RED,2);
        player2 = new Player("marco", Color.BLUE,1);
        ship1 = new ShipBoard(1,2,Color.RED);
        ship2 = new ShipBoard(1,1,Color.BLUE);
        player1.setShipBoard(ship1);
        player2.setShipBoard(ship2);
        gameBoard.setLeaderBoard(GameMode.LEVEL_1);
        gameBoard.addPlayerToLeaderboard(player1);
        gameBoard.addPlayerToLeaderboard(player2);
        gameBoard.getLeaderBoard().modifyFlightDays(player1, -2);
        gameBoard.getLeaderBoard().modifyFlightDays(player2, +3);
        planet1 = new Planet(1,0,0,0,1);
        planet2 = new Planet(0,2,0,0,2);
        planet3 = new Planet(0,0,1,0,3);
        component1 = new ContainerComponent(ComponentType.SPECIAL_STORAGE,3);
        tile1 = new Tile(2, component1);
        component2 = new ContainerComponent(ComponentType.STORAGE,2);
        tile2 = new Tile(1, component2);
        player1.getShipBoard().insertTile(coordinate2, tile2);
        player1.getShipBoard().addTokenToTile(coordinate2, Token.GREEN_GOODS);
        player1.getShipBoard().addTokenToTile(coordinate2, Token.YELLOW_GOODS);
        player2.getShipBoard().insertTile(coordinate1, tile1);
        player2.getShipBoard().addTokenToTile(coordinate1,Token.BLUE_GOODS);
        player2.getShipBoard().addTokenToTile(coordinate1,Token.GREEN_GOODS);
        player2.getShipBoard().addTokenToTile(coordinate1,Token.RED_GOODS);
        deck = new AdventureCardDeck();
        planets = new ArrayList<>();
        planets2 = new ArrayList<>();
        planets2.add(planet1);
        planets2.add(planet2);
        planets2.add(planet3);
        planets.add(planet1);
        coordinates = new ArrayList<>();
        coordinates.add(new Pair<>(coordinate1, Token.RED_GOODS));
        coordinates2 = new ArrayList<>();
        coordinates2.add(new Pair<>(coordinate2, Token.YELLOW_GOODS));
        coordinates2.add(new Pair<>(coordinate2, Token.YELLOW_GOODS));

        deck.add(new PlanetCard(gameBoard,"Planet Card",3,"planet",1,planets));
        deck.add(new PlanetCard(gameBoard,"Planet Card",9,"planet",1,planets2));
        gameBoard.setMainAdventureDeck(deck);


    }

    @Test
    void drawCard() {
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        assertEquals(AdventureStatesTypes.DECISION_LAND_PLANET,gameBoard.getCurrentCard().getCurrentState());
        assertEquals(1,deck.size());
        assertEquals("Planet Card",gameBoard.getCurrentCard().getDescription());

    }

    @Test
    void checkRequirements() {
    }

    @Test
    void landingOnPlanet() {

        gameBoard.drawAdventureCard(gameBoard.getLeader());
        gameBoard.getCurrentCard().landingOnPlanet(true, 1);
        assertEquals(AdventureStatesTypes.DECISION_GOODS,gameBoard.getCurrentCard().getCurrentState());
        assertEquals(player2,gameBoard.getCurrentPlayer());
        assertEquals(player2.getPlayerNumber(),planet1.getPlayernumber());

    }

    @Test
    void finishDecision() {
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        gameBoard.getCurrentCard().landingOnPlanet(true, 1);
        gameBoard.getCurrentCard().finishDecision(coordinates);
        assertEquals(AdventureStatesTypes.UPDATE,gameBoard.getCurrentCard().getCurrentState());
        assertEquals(0,gameBoard.getCurrentPlayer().getDaysThisTurn());

        //gameBoard.getCurrentCard().landingOnPlanet(true, planet1);
    }

    @Test
    void finishDecision2() {
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        gameBoard.getCurrentCard().setFinished(true);
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        gameBoard.getCurrentCard().landingOnPlanet(true, 1);
        gameBoard.getCurrentCard().finishDecision(coordinates);
        gameBoard.getCurrentCard().landingOnPlanet(true, 2);
        gameBoard.getCurrentCard().finishDecision(coordinates2);
        assertEquals(player1,gameBoard.getCurrentPlayer());
        assertEquals(AdventureStatesTypes.UPDATE,gameBoard.getCurrentCard().getCurrentState());
        assertEquals(0,gameBoard.getCurrentPlayer().getDaysThisTurn());
        assertEquals(0,deck.size());
        assertEquals("Planet Card",gameBoard.getCurrentCard().getDescription());

    }
    @Test
    void applyEffect() {
    }

    @Test
    void testToString() {
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        gameBoard.getCurrentCard().setFinished(true);
        gameBoard.drawAdventureCard(gameBoard.getLeader());
        System.out.println(gameBoard.getCurrentCard().toString());
    }
}