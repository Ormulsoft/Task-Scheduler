package test.java;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;

import alg.AStarAlgorithm;
import alg.cost.AStarCostFunction;
import grph.properties.NumericalProperty;
import io.Input;
import toools.collections.primitive.LucIntSet;
import util.ScheduleGrph;

public class TestAStarAlgorithmOptimal {

	static ScheduleGrph _7In;
	static ScheduleGrph _8In;
	static ScheduleGrph _9In;
	static ScheduleGrph _10In;
	static ScheduleGrph _11In;

	/**
	 * Helper method for finding the finish time of a schedule
	 * 
	 * @param grph
	 *            The schedule to find finish time of
	 * @return The finish time (end time of last-finishing task)
	 */
	static int getEndTime(ScheduleGrph grph) {
		LucIntSet verts = grph.getVertices();
		NumericalProperty starts = grph.getVertexStartProperty();
		NumericalProperty weights = grph.getVertexWeightProperty();
		int endTime = 0;

		// Iterate through each task, if it finishes later than current recorded
		// endTime, update endTime
		for (int vert : verts) {
			int vertEnd = starts.getValueAsInt(vert) + weights.getValueAsInt(vert);
			endTime = Math.max(endTime, vertEnd);
		}

		return endTime;
	}

	@Before
	public void init() {
		try {
			_7In = Input.readDotInput("src/resources/Nodes_7_OutTree.dot");
			_8In = Input.readDotInput("src/resources/Nodes_8_Random.dot");
			_9In = Input.readDotInput("src/resources/Nodes_9_SeriesParallel.dot");
			_10In = Input.readDotInput("src/resources/Nodes_10_Random.dot");
			_11In = Input.readDotInput("src/resources/Nodes_11_OutTree.dot");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void node7Proc2Core1() {
		System.out.println("node7Proc2Core1");
		ScheduleGrph out = new AStarAlgorithm(_7In, new AStarCostFunction(_7In), 2).runAlg();
		assertEquals(28, getEndTime(out));
	}

	@Test
	public void node8Proc2Core1() {
		System.out.println("node8Proc2Core1");
		ScheduleGrph out = new AStarAlgorithm(_8In, new AStarCostFunction(_8In), 2).runAlg();
		assertEquals(581, getEndTime(out));
	}

	@Test
	public void node9Proc2Core1() {
		System.out.println("node9Proc2Core1");
		ScheduleGrph out = new AStarAlgorithm(_9In, new AStarCostFunction(_9In), 2).runAlg();
		assertEquals(55, getEndTime(out));
	}

	@Test
	public void node10Proc2Core1() {
		System.out.println("node10Proc2Core1");
		ScheduleGrph out = new AStarAlgorithm(_10In, new AStarCostFunction(_10In), 2).runAlg();
		assertEquals(50, getEndTime(out));
	}

	@Test
	public void node11Proc2Core1() {
		System.out.println("node11Proc2Core1");
		ScheduleGrph out = new AStarAlgorithm(_11In, new AStarCostFunction(_11In), 2).runAlg();
		assertEquals(getEndTime(out), 350);
	}

	@Test
	public void node7Proc4Core1() {
		System.out.println("node7Proc4Core1");
		ScheduleGrph out = new AStarAlgorithm(_7In, new AStarCostFunction(_7In), 4).runAlg();
		assertEquals(22, getEndTime(out));
	}

	@Test
	public void node8Proc4Core1() {
		System.out.println("node8Proc4Core1");
		ScheduleGrph out = new AStarAlgorithm(_8In, new AStarCostFunction(_8In), 4).runAlg();
		assertEquals(581, getEndTime(out));
	}

	@Test
	public void node9Proc4Core1() {
		System.out.println("node9Proc4Core1");
		ScheduleGrph out = new AStarAlgorithm(_9In, new AStarCostFunction(_9In), 4).runAlg();
		assertEquals(55, getEndTime(out));
	}

	@Test
	public void node10Proc4Core1() {
		System.out.println("node10Proc4Core1");
		ScheduleGrph out = new AStarAlgorithm(_10In, new AStarCostFunction(_10In), 4).runAlg();
		assertEquals(50, getEndTime(out));
	}

	@Test
	public void node11Proc4Core1() {
		System.out.println("node11Proc4Core1");
		ScheduleGrph out = new AStarAlgorithm(_11In, new AStarCostFunction(_11In), 4).runAlg();
		assertEquals(getEndTime(out), 227);
	}
}