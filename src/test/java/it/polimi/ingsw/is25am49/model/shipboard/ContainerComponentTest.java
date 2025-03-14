package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import it.polimi.ingsw.is25am49.model.exceptions.IllegalTokenInComponentEcxeption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContainerComponentTest {

    ContainerComponent storage;
    ContainerComponent specialStorage;
    ContainerComponent battery;
    ContainerComponent alienModule;
    ContainerComponent cabin;

    @BeforeAll
    void setUpClass() {
        storage= new ContainerComponent(ComponentType.STORAGE,3);
        specialStorage= new ContainerComponent(ComponentType.SPECIAL_STORAGE,2);
        battery= new ContainerComponent(ComponentType.BATTERY,5);
        alienModule= new ContainerComponent(ComponentType.BROWN_ALIEN_MODULE,1);
        cabin= new ContainerComponent(ComponentType.CABINE,2);

        battery.fillComponent(Token.BATTERY_CELLS);
    }

    @Test
    void testNumberOfTokenEqualLevelInAFilledComponent() {
        assertEquals(battery.level,battery.getNumberOfToken());
    }

    void testInsertWrongTokenToComponent(){
        assertThrows(IllegalTokenInComponentEcxeption.class, ()-> storage.addToken(Token.ASTRONAUT));
        assertThrows(IllegalTokenInComponentEcxeption.class, ()-> cabin.addToken(Token.BATTERY_CELLS));
        assertThrows(IllegalTokenInComponentEcxeption.class, ()-> storage.addToken(Token.RED_GOODS));
    }

    void testInsertCorrectTokenToComponent(){
        cabin.addToken(Token.ASTRONAUT);
        assertEquals(Token.ASTRONAUT, cabin.getTokenList().getFirst());
        storage.addToken(Token.GREEN_GOODS);
        storage.addToken(Token.YELLOW_GOODS);
        assertEquals(Token.GREEN_GOODS,storage.getTokenList().get(0));
        assertEquals(Token.YELLOW_GOODS,storage.getTokenList().get(1));
    }
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
    void removeToken() {
    }

    @Test
    void testRemoveToken() {
    }

    @Test
    void testGetNumberOfToken() {
    }

    @Test
    void clearComponent() {
    }
}