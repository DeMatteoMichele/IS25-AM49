package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.AdventureCardDeck;
import it.polimi.ingsw.is25am49.model.Game;
import it.polimi.ingsw.is25am49.model.GameBoard;
import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectModifyCrew;
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.GameMode;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.shipboard.*;
import it.polimi.ingsw.is25am49.model.shipboard.Component;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WarZoneCardTest {

    Player player1,player2;
    GameBoard gameBoard;
    AdventureCardDeck deck;
    ShipBoard ship1,ship2;
    Tile tile1,tile2,tile3,tile4,tile5,tile6,tile7;
    Component component1,component2,component3,component4,component5,component6,component7;
    List<Pair<Point, Token>> batteries,batteries2,astronauts;
    Set<Point> motors,motors2;
    Point coordinate1 = new Point(7,6);
    Point coordinate2 = new Point(7,8);
    Point coordinate3 = new Point(7,5);
    Point coordinate4 = new Point(6,7);
    Point coordinate5 = new Point(8,6);
    List<CardEffect> effects;


    @BeforeEach
    void init(){
        effects = new ArrayList<>();
        effects.add(new EffectModifyCrew("EffectModifyCrew",2));
        motors = new HashSet<Point>();
        motors.add(coordinate1);
        motors.add(coordinate2);
        batteries = new ArrayList<>();
        batteries.add(new Pair<>(coordinate3, Token.BATTERY_CELLS));
        batteries.add(new Pair<>(coordinate3, Token.BATTERY_CELLS));
        astronauts = new ArrayList<>();
        astronauts.add(new Pair<>(coordinate5, Token.ASTRONAUT));
        astronauts.add(new Pair<>(coordinate5, Token.ASTRONAUT));


        component1 = new SpecialComponent(ComponentType.DOUBLE_ENGINE,2);
        component2 = new SpecialComponent(ComponentType.DOUBLE_ENGINE,2);
        component3 = new ContainerComponent(ComponentType.BATTERY,2);
        component4 = new ContainerComponent(ComponentType.CABINE,3);
        component5 = new ContainerComponent(ComponentType.CABINE,5);
        tile1= new Tile(1,component1);
        tile2= new Tile(2,component2);
        tile3= new Tile(3,component3);
        tile4= new Tile(4,component4);
        tile5 = new Tile(5,component5);
        player1 = new Player("ciro", Color.RED,2);
        player2 = new Player("dan", Color.GREEN,1);
        gameBoard = new GameBoard();
        gameBoard.setLeaderBoard(GameMode.LEVEL_1);
        deck = new AdventureCardDeck();
        deck.add(new WarZoneCard(gameBoard,"War Zone",1,effects));
        ship1 = new ShipBoard(1,2);
        ship2 = new ShipBoard(1,1);
        player1.setShipBoard(ship1);
        player2.setShipBoard(ship2);
        player1.getShipBoard().insertTile(coordinate1,tile1);
        player1.getShipBoard().insertTile(coordinate2,tile2);
        player1.getShipBoard().insertTile(coordinate3,tile3);
        player1.getShipBoard().insertTile(coordinate4,tile4);
        player2.getShipBoard().insertTile(coordinate5,tile5);
        player1.getShipBoard().addTokenToTile(coordinate3,Token.BATTERY_CELLS);
        player1.getShipBoard().addTokenToTile(coordinate3,Token.BATTERY_CELLS);
        player1.getShipBoard().addTokenToTile(coordinate4,Token.ASTRONAUT);
        player1.getShipBoard().addTokenToTile(coordinate4,Token.ASTRONAUT);
        player1.getShipBoard().addTokenToTile(coordinate4,Token.ASTRONAUT);
        player2.getShipBoard().addTokenToTile(coordinate5,Token.ASTRONAUT);
        player2.getShipBoard().addTokenToTile(coordinate5,Token.ASTRONAUT);
        player2.getShipBoard().addTokenToTile(coordinate5,Token.ASTRONAUT);

        gameBoard.addPlayerToLeaderboard(player1);
        gameBoard.addPlayerToLeaderboard(player2);
        gameBoard.setMainAdventureDeck(deck);


    }
    @Test
    void drawCard() {
        gameBoard.drawAdventureCard();
        gameBoard.getCurrentCard().finishDecision(batteries);
        gameBoard.getCurrentCard().finishDecision(motors);
        gameBoard.getCurrentCard().finishDecision(astronauts);
    }

    @Test
    void checkRequirements() {
    }

    @Test
    void checkRequirements2() {
    }

    @Test
    void finishDecision() {
    }

    @Test
    void testFinishDecision() {
    }

    @Test
    void applyEffect() {
    }
}