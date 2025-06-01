package it.polimi.ingsw.is25am49.client.model;

import it.polimi.ingsw.is25am49.shared.enums.Color;
import it.polimi.ingsw.is25am49.shared.enums.GameMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LeaderBoardTest {

    @Test
    void testToString() {
        LeaderBoard l= new LeaderBoard(GameMode.LEVEL_2);
        Player p1 = new Player("p1", Color.RED,1);
        Player p2 = new Player("p2", Color.BLUE,2);
        Player p3 = new Player("p3", Color.GREEN,3);
        Player p4 = new Player("p4", Color.YELLOW,4);
        //l.add(p1);
        l.add(p2);
        l.add(p3);
        l.add(p4);

        System.out.println(l);
    }
}