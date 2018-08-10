package alg;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import alg.cost.TestCostFunction;
import grph.VertexPair;
import io.Input;
import util.PartialScheduleGrph;
import util.ScheduleGrph;

public class CostFuncTests {
	private final Logger log = Logger.getLogger(this.getClass());
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
			gpart.getVertexProcessorProperty().setValue(i, 1);
			;

		}
		for (VertexPair j : g.getEdgePairs()) {

			gpart.addDirectedSimpleEdge(j.first, j.second);
		}
		// gpart = Input.readDotInput(inputFile);
		c = new TestCostFunction(g);

	}

	@Test
	public void drtTest() throws RemoteException {

		log.info(g.toDot());
		gpart.getVertexStartProperty().setValue(5, 1000);
		gpart.removeVertex(1);
		log.info(gpart.toDot());
		int maxDRT = 0;

		for (int i : gpart.getFree(g)) {

			int minProc = -1;
			for (int proc = 1; proc <= 2; proc++) {
				// Tdr (n, p)
				int valDRT = c.getDRT(i, gpart, proc);
				if (valDRT < minProc || minProc == -1) {
					minProc = valDRT;

				}
			}
			if (minProc > maxDRT) {
				maxDRT = minProc;
			}
		}
		// log.info(maxDRT);

		// Checking to see if Astar
		// Works under some time frame

	}

	@Test
	public void idleTest() {

		gpart.getVertexStartProperty().setValue(1, 0);

		gpart.getVertexStartProperty().setValue(1, 1000);

		gpart.getVertexStartProperty().setValue(2, 32);

		gpart.getVertexStartProperty().setValue(2, 3123);

		c.applyCost(gpart);
		log.info(gpart.toDot());
		log.info(c.getIdleTimeFit(gpart, 2));

		// Checking to see if Astar
		// Works under some time frame

	}

}
