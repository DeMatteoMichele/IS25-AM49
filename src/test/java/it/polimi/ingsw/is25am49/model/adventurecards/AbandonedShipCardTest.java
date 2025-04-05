package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.server.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.server.model.GameBoard;
import it.polimi.ingsw.is25am49.server.model.Player;
import it.polimi.ingsw.is25am49.server.model.adventurecards.AbandonedShipCard;
import it.polimi.ingsw.is25am49.server.model.adventurecards.AdventureCard;
import it.polimi.ingsw.is25am49.server.model.enums.AdventureStatesTypes;
import it.polimi.ingsw.is25am49.server.model.enums.Color;
import it.polimi.ingsw.is25am49.server.model.enums.Token;
import it.polimi.ingsw.is25am49.server.model.shipboard.Component;
import it.polimi.ingsw.is25am49.server.model.shipboard.ShipBoard;
import it.polimi.ingsw.is25am49.server.model.shipboard.Tile;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbandonedShipCardTest {
    GameBoard gameBoard;
    AdventureCardDeck adventureDeck;
    Player player1;
    Player player2;
    ShipBoard shipBoard1;
    AdventureCard card1;
    Component shipBoardComponent;
    Tile tile;


    @BeforeEach
    void init(){
        // Initialize the game board and player1
        gameBoard = new GameBoard();
        player1 = new Player("TestPlayer", Color.RED, 1);
        player2 = new Player("TestPlayer2", Color.BLUE, 2);
        gameBoard.addPlayerToLeaderboard(player1);
        gameBoard.addPlayerToLeaderboard(player2);
        gameBoard.getLeaderBoard().modifyFlightDays(player2,3);
        gameBoard.getLeaderBoard().modifyFlightDays(player1,0);
        adventureDeck = new AdventureCardDeck();
        card1=new AbandonedShipCard(gameBoard, "Abandoned Ship", 2, 1, 100, 3);
        adventureDeck.add(card1);
        gameBoard.setMainAdventureDeck(adventureDeck);
//        shipBoard1 = new ShipBoard(1,2);
//        shipBoardComponent = new ContainerComponent(ComponentType.CABINE, 3);
//        tile = new Tile(3, shipBoardComponent);
//        player2.setShipBoard(shipBoard1);
//        player2.getShipBoard().insertTile(new Point(2,2 ), tile);
//        player2.getShipBoard().addTokenToTile(new Point(2,2), Token.ASTRONAUT);
//        player2.getShipBoard().addTokenToTile(new Point(2,2), Token.ASTRONAUT);
//        player2.getShipBoard().addTokenToTile(new Point(2,2), Token.ASTRONAUT);


        // Create an instance of AbandonedShipCard

        // Set up the initial state of the game board and player1

    }


    @Test
    void drawCard() {
        gameBoard.drawAdventureCard();
        assertEquals(0, adventureDeck.size());
        assertEquals(gameBoard.getLeaderBoard().getLeader(),player2);
        assertEquals(gameBoard.getLeader(), gameBoard.getCurrentPlayer());
        assertEquals(AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP, gameBoard.getCurrentCard().getCurrentState());
    }

    @Test
    void checkRequirements() {
    }

    @Test
    void landingOnStationOrShip() {
        gameBoard.drawAdventureCard();
        gameBoard.getCurrentCard().landingOnStationOrShip(true);
        assertEquals(AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS, gameBoard.getCurrentCard().getCurrentState());

    }

    @Test
    void finishDecision() {
        gameBoard.drawAdventureCard();
        gameBoard.getCurrentCard().landingOnStationOrShip(false);
        gameBoard.getCurrentCard().landingOnStationOrShip(true);
        gameBoard.getCurrentCard().finishDecision(new ArrayList<Pair<Point, Token>>());
        assertEquals(AdventureStatesTypes.UPDATE, gameBoard.getCurrentCard().getCurrentState());
        assertEquals(0, adventureDeck.size());
        assertEquals(gameBoard.getCurrentCard(),card1);
        assertEquals(gameBoard.getCurrentPlayer(),player1);
    }

    @Test
    void applyEffect() {
        gameBoard.drawAdventureCard();
//        ArrayList<Pair<Point, Token>> Coordinates = new ArrayList<>();
//        Coordinates.add(new Pair<>(new Point(2, 2), Token.ASTRONAUT));
        gameBoard.getCurrentCard().landingOnStationOrShip(true);
        gameBoard.getCurrentCard().finishDecision(new ArrayList<Pair<Point, Token>>());
        assertEquals(AdventureStatesTypes.UPDATE, gameBoard.getCurrentCard().getCurrentState());
        assertEquals(100, gameBoard.getCurrentPlayer().getCredits());
        assertEquals(2,gameBoard.getCurrentPlayer().getDaysThisTurn());
    }

}