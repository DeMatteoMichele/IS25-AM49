package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalTokenInComponentEcxeption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

class ContainerComponentTest {

    ContainerComponent storage;
    ContainerComponent specialStorage;
    ContainerComponent battery;
    ContainerComponent alienModule;
    ContainerComponent cabin;

    @BeforeEach
    void setUpClass() {
        storage= new ContainerComponent(ComponentType.STORAGE,3);
        specialStorage= new ContainerComponent(ComponentType.SPECIAL_STORAGE,2);
        battery= new ContainerComponent(ComponentType.BATTERY,5);
        alienModule= new ContainerComponent(ComponentType.BROWN_ALIEN_MODULE,1);
        cabin= new ContainerComponent(ComponentType.CABINE,2);

        battery.fillComponent(Token.BATTERY_CELLS);
    }



    @Test //test per fillComponent
    void testNumberOfTokenEqualLevelInAFilledComponent() {
        assertEquals(battery.level,battery.getNumberOfToken());
        assertThrows(IllegalTokenInComponentEcxeption.class, ()-> cabin.fillComponent(Token.PURPLE_ALIENS));
        assertNotEquals(cabin.level,cabin.getNumberOfToken());
    }
    @Test
    void testInsertWrongTokenToComponent(){
        assertThrows(IllegalTokenInComponentEcxeption.class, ()-> storage.addToken(Token.ASTRONAUT));
        assertThrows(IllegalTokenInComponentEcxeption.class, ()-> cabin.addToken(Token.BATTERY_CELLS));
        assertThrows(IllegalTokenInComponentEcxeption.class, ()-> storage.addToken(Token.RED_GOODS));
    }
    @Test
    void testInsertCorrectTokenToComponent(){
        cabin.addToken(Token.ASTRONAUT);
        assertEquals(Token.ASTRONAUT, cabin.getTokenList().getFirst());
        int sNum= storage.getNumberOfToken();
        storage.addToken(Token.GREEN_GOODS);
        storage.addToken(Token.YELLOW_GOODS);
        assertEquals(Token.GREEN_GOODS,storage.getTokenList().get(0));
        assertEquals(Token.YELLOW_GOODS,storage.getTokenList().get(1));
        assertEquals(sNum+2,storage.getNumberOfToken());
        specialStorage.addToken(Token.RED_GOODS);
        specialStorage.addToken(Token.GREEN_GOODS);
        assertEquals(Token.RED_GOODS, specialStorage.getTokenList().getFirst());
        assertEquals(Token.GREEN_GOODS, specialStorage.getTokenList().get(1));
    }
    @Test
    void testInsertTokenWhenFullComponent(){
        assertThrows(IllegalStateException.class, ()-> battery.addToken(Token.BATTERY_CELLS));
    }
    @Test
    void testAddToken(){
        testInsertCorrectTokenToComponent();
        testInsertWrongTokenToComponent();
        testInsertTokenWhenFullComponent();
    }

    @Test
    void testAfterRemoveSizeDecrease(){
        int oldSize = battery.getTokenList().size();
        battery.removeToken(Token.BATTERY_CELLS);
        assertEquals(oldSize-1,battery.getTokenList().size());
        assertEquals(oldSize-1,battery.getNumberOfToken());
        alienModule.addToken(Token.BROWN_ALIENS);
        alienModule.removeToken(Token.BROWN_ALIENS);
        assertEquals(0, alienModule.getNumberOfToken());
    }
    @Test
    void testRemoveWrongToken(){
        assertThrows(IllegalTokenInComponentEcxeption.class, ()-> battery.removeToken(Token.ASTRONAUT));
        assertThrows(IndexOutOfBoundsException.class, ()-> alienModule.removeToken());
        assertThrows(IndexOutOfBoundsException.class, ()-> alienModule.removeToken(Token.BROWN_ALIENS));
        assertThrows(IndexOutOfBoundsException.class, ()-> alienModule.removeToken(Token.BLUE_GOODS));
    }
    @Test
    void testRemoveToken() {
        testAfterRemoveSizeDecrease();
        testRemoveWrongToken();
    }

    @Test
    void testGetNumberOfToken() {
        assertEquals(battery.getNumberOfToken(),battery.getTokenList().size());
        assertEquals(cabin.getNumberOfToken(),cabin.getNumberOfToken());
    }

    @Test
    void clearComponent() {
        battery.clearComponent();
        assertEquals (0,battery.getNumberOfToken());
    }

    @Test
    void checkIfTokenIsValid() {
        assertTrue(battery.checkIfTokenIsValid(Token.BATTERY_CELLS));
        assertTrue(alienModule.checkIfTokenIsValid(Token.BROWN_ALIENS));
        assertTrue(cabin.checkIfTokenIsValid(Token.ASTRONAUT));
        assertTrue(specialStorage.checkIfTokenIsValid(Token.RED_GOODS));
        assertTrue(specialStorage.checkIfTokenIsValid(Token.YELLOW_GOODS));
        assertTrue(storage.checkIfTokenIsValid(Token.GREEN_GOODS));
        assertTrue(storage.checkIfTokenIsValid(Token.BLUE_GOODS));
        assertTrue(storage.checkIfTokenIsValid(Token.YELLOW_GOODS));
        assertFalse(cabin.checkIfTokenIsValid(Token.YELLOW_GOODS));
        assertFalse(storage.checkIfTokenIsValid(Token.RED_GOODS));
    }
}