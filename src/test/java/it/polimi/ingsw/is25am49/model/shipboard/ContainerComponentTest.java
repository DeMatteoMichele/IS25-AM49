package it.polimi.ingsw.is25am49.model.shipboard;

import it.polimi.ingsw.is25am49.model.enums.ComponentType;
import it.polimi.ingsw.is25am49.model.enums.Token;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContainerComponentTest {

    ContainerComponent storage;
    ContainerComponent specialStorage;
    ContainerComponent battery;
    ContainerComponent alienModule;

    @BeforeAll
    void setUpClass() {
        storage= new ContainerComponent(ComponentType.STORAGE,3);
        specialStorage= new ContainerComponent(ComponentType.SPECIAL_STORAGE,2);
        battery= new ContainerComponent(ComponentType.BATTERY,5);
        alienModule= new ContainerComponent(ComponentType.BROWN_ALIEN_MODULE,1);
    }

    @Test
    void numberOfTokenEqualCapacityInAFilledComponent() {
        battery.fillComponent(Token.BATTERY_CELLS);
        assertEquals(battery.getNumberOfToken(), battery.level);
    }

    @Test
    void addToken() {
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