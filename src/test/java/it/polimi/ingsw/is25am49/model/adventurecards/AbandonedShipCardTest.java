package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
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
        gameBoard.addPlayerToLeaderboard(0, player1);
        gameBoard.addPlayerToLeaderboard(3, player2);
        gameBoard.sortLeaderBoard();
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
        assertEquals(gameBoard.getLeaderBoard().getFirst().getValue(),player2);
        assertEquals(gameBoard.getLeader(), gameBoard.getCurrentPlayer());
        assertEquals(AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP, gameBoard.getCurrentCard().getCurrentState());
    }

    @Test
    void checkRequirements() {
    }

    @Test
    void landingOnStationOrShip() {
        gameBoard.drawAdventureCard();
        gameBoard.getCurrentCard().landingOnStationOrShip();
        assertEquals(AdventureStatesTypes.DECISION_ASTRONAUTS_OR_ALIENS, gameBoard.getCurrentCard().getCurrentState());

    }

    @Test
    void finishDecision() {
        gameBoard.drawAdventureCard();
        gameBoard.getCurrentCard().finishDecision();
        assertEquals(AdventureStatesTypes.DECISION_LAND_STATION_OR_SHIP, gameBoard.getCurrentCard().getCurrentState());
        assertEquals(0, adventureDeck.size());
        assertEquals(gameBoard.getCurrentCard(),card1);
        assertEquals(gameBoard.getCurrentPlayer(),player1);
    }

    @Test
    void applyEffect() {
        gameBoard.drawAdventureCard();
//        ArrayList<Pair<Point, Token>> Coordinates = new ArrayList<>();
//        Coordinates.add(new Pair<>(new Point(2, 2), Token.ASTRONAUT));
        gameBoard.getCurrentCard().landingOnStationOrShip();
        gameBoard.getCurrentCard().getCurrentEffect().setListOfCoordinates(new ArrayList<Pair<Point, Token>>());
        gameBoard.getCurrentCard().finishDecision();
        assertEquals(AdventureStatesTypes.UPDATE, gameBoard.getCurrentCard().getCurrentState());
        assertEquals(100, gameBoard.getCurrentPlayer().getCredits());
        assertEquals(2,gameBoard.getCurrentPlayer().getDaysThisTurn());
    }

}