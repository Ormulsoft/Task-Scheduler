package alg;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import alg.cost.TestCostFunction;
import grph.VertexPair;
import io.Input;
import io.OutputTests;
import util.PartialScheduleGrph;
import util.ScheduleGrph;

public class CostFuncTests {
	final static Logger log = Logger.getLogger(OutputTests.class);
	public static ScheduleGrph g;
	public static TestCostFunction c;
	public static PartialScheduleGrph gpart;

	@BeforeClass
	public static void before() {
		String inputFile = "src\\resources\\Nodes_8_Random.dot";
		g = Input.readDotInput(inputFile);
		gpart = new PartialScheduleGrph(0);
		gpart.setVertexWeightProperty(g.getVertexWeightProperty());
		gpart.setEdgeWeightProperty(g.getEdgeWeightProperty());
		gpart.setVerticesLabel(g.getVertexLabelProperty());
		for (int i : g.getVertices()) {
			gpart.addVertex();

		}
		for (VertexPair j : g.getEdgePairs()) {

			gpart.addDirectedSimpleEdge(j.first, j.second);
		}
		// gpart = Input.readDotInput(inputFile);
		c = new TestCostFunction(g);

	}

	@Test
	public void testAStarTimeout() throws RemoteException {

		log.info(g.toDot());
		gpart.getVertexStartProperty().setValue(5, 1000);
		gpart.removeVertex(1);
		log.info(gpart.toDot());
		int maxDRT = 0;

		for (int i : gpart.getFree(g)) {
			int valDRT = c.getDRT(i, gpart);
			if (valDRT > maxDRT) {
				maxDRT = valDRT;
			}
		}
		log.info(maxDRT);

		// Checking to see if Astar
		// Works under some time frame

	}

}
