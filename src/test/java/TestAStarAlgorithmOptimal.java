package test.java;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import alg.AStarAlgorithm;
import alg.cost.TestCostFunction;
import grph.properties.NumericalProperty;
import io.Input;
import toools.collections.primitive.LucIntSet;
import util.ScheduleGrph;
 
class TestAStarAlgorithmOptimal {
	
	static ScheduleGrph _7In;
	static ScheduleGrph _8In;
	static ScheduleGrph _9In;
	static ScheduleGrph _10In;
	static ScheduleGrph _11In;
	
	/**
	 * Helper method for finding the finish time of a schedule
	 * @param grph The schedule to find finish time of
	 * @return The finish time (end time of last-finishing task)
	 */
	static int getEndTime(ScheduleGrph grph) {
		LucIntSet verts = grph.getVertices();
		NumericalProperty starts = grph.getVertexStartProperty();
		NumericalProperty weights = grph.getVertexWeightProperty();
    	int endTime = 0;
    	
    	// Iterate through each task, if it finishes later than current recorded endTime, update endTime
    	for (int vert : verts) {
    		int vertEnd = starts.getValueAsInt(vert) + weights.getValueAsInt(vert);
    		endTime = Math.max(endTime, vertEnd);
    	}
    	
    	return endTime;
	}
	
	@BeforeAll
	static void init() {
		_7In = Input.readDotInput("src/resources/Nodes_7_OutTree.dot");
		_8In = Input.readDotInput("src/resources/Nodes_8_Random.dot");
		_9In = Input.readDotInput("src/resources/Nodes_9_SeriesParallel.dot");
		_10In = Input.readDotInput("src/resources/Nodes_10_Random.dot");
		_11In = Input.readDotInput("src/resources/Nodes_11_OutTree.dot");
	}
	
 
    @Test
    void node7Proc2Core1() {
    	ScheduleGrph out = new AStarAlgorithm(new TestCostFunction(_7In)).runAlg(_7In, 1, 2);
    	assertEquals(getEndTime(out),28);
    }
    
    @Test
    void node8Proc2Core1() {
    	ScheduleGrph out = new AStarAlgorithm(new TestCostFunction(_8In)).runAlg(_8In, 1, 2);
    	assertEquals(getEndTime(out),581);
    }
    
    @Test
    void node9Proc2Core1() {
    	ScheduleGrph out = new AStarAlgorithm(new TestCostFunction(_9In)).runAlg(_9In, 1, 2);
    	assertEquals(getEndTime(out),55);
    }
    
    @Test
    void node10Proc2Core1() {
    	ScheduleGrph out = new AStarAlgorithm(new TestCostFunction(_10In)).runAlg(_10In, 1, 2);
    	assertEquals(getEndTime(out),50);
    }
    
    @Test
    void node11Proc2Core1() {
    	ScheduleGrph out = new AStarAlgorithm(new TestCostFunction(_11In)).runAlg(_11In, 1, 2);
    	assertEquals(getEndTime(out),350);
    }
    
    @Test
    void node7Proc4Core1() {
    	ScheduleGrph out = new AStarAlgorithm(new TestCostFunction(_7In)).runAlg(_7In, 1, 2);
    	assertEquals(getEndTime(out),22);
    }
    
    @Test
    void node8Proc4Core1() {
    	ScheduleGrph out = new AStarAlgorithm(new TestCostFunction(_8In)).runAlg(_8In, 1, 2);
    	assertEquals(getEndTime(out),581);
    }
    
    @Test
    void node9Proc4Core1() {
    	ScheduleGrph out = new AStarAlgorithm(new TestCostFunction(_9In)).runAlg(_9In, 1, 2);
    	assertEquals(getEndTime(out),55);
    }
    
    @Test
    void node10Proc4Core1() {
    	ScheduleGrph out = new AStarAlgorithm(new TestCostFunction(_10In)).runAlg(_10In, 1, 2);
    	assertEquals(getEndTime(out),50);
    }
    
    @Test
    void node11Proc4Core1() {
    	ScheduleGrph out = new AStarAlgorithm(new TestCostFunction(_11In)).runAlg(_11In, 1, 2);
    	assertEquals(getEndTime(out),227);
    }
}