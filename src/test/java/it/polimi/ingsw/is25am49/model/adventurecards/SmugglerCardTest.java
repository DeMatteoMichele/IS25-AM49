package it.polimi.ingsw.is25am49.model.adventurecards;

import it.polimi.ingsw.is25am49.model.Player;
import it.polimi.ingsw.is25am49.model.cardeffects.CardEffect;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectCannonata;
import it.polimi.ingsw.is25am49.model.cardeffects.EffectMeteoriti;
<<<<<<< HEAD
=======
import it.polimi.ingsw.is25am49.model.enums.Color;
import it.polimi.ingsw.is25am49.model.enums.ComponentType;
>>>>>>> ac1eaf3e87e9d4ebd6c09e76f498b55fe99125c8
import it.polimi.ingsw.is25am49.model.enums.Orientation;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.shipboard.ShipBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.Mockito.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmugglerCardTest {
    private SmugglerCard smugglerCard;
    private Player player;
    private ShipBoard shipBoard;
    String description ="CIAO";
    int daysLost = 10;
    int level=4;
    int minimumFirePower = 4;
    int lostAstronauts = 0;
    int creditsEarned = 0;
    List<CardEffect> ListEffect= new ArrayList<>();





    @BeforeEach
    void init(){
        shipBoard = new ShipBoard();
        Player player = new Player("Pippo", Color.BLUE);
        smugglerCard=new SmugglerCard(description,daysLost, level, creditsEarned, minimumFirePower, lostAstronauts,ListEffect);
    }

    @Test
    void drawCard(){
        smugglerCard.drawCard();
    }

    @Test
    void checkRequirements() {
        Player playermock = Mockito.mock(Player.class);
        ShipBoard shipBoardMock = Mockito.mock(ShipBoard.class);
        Mockito.when(playermock.getShipBoard()).thenReturn(shipBoardMock);
        Mockito.when(shipBoardMock.getTotalNumberOfaToken(Token.BATTERY_CELLS)).thenReturn(0);
        Mockito.when(shipBoardMock.getActivatableComponents(ComponentType.DOUBLE_CANNON)).thenReturn(new ArrayList<>());
        Mockito.when(shipBoardMock.getTotalFirePower()).thenReturn(3.0);
        smugglerCard.checkRequirements(playermock);
    }

    @Test
    void applyEffect() {

    }

}