package test.io;
import io.Input;
import org.junit.Test;
import util.ScheduleGrph;

import static org.junit.Assert.assertEquals;

public class InputTests {

    @Test
     public void correctInputVertices() {
        ScheduleGrph in = Input.readDotInput("src/resources/Nodes_8_Random.dot");
        assertEquals(8,in.getVertices().size());
        int j = 0;
        for (int i : in.getVertices()) {
            assertEquals(j,i);
            if(j != 7) {
                j++;
            }
        }
    }

    @Test
    public  void correctEdges() {

    }

}
