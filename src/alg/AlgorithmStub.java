package alg;

import grph.properties.NumericalProperty;
import util.ScheduleGrph;

/**
 * Stub algorithm that doesnt alter the input, simply returns it as is
 */
public class AlgorithmStub implements Algorithm {

	public ScheduleGrph runAlg(ScheduleGrph input, int numCores, int numProcessors) {

		log.info("Running stub algorithm");

		ScheduleGrph g = new ScheduleGrph();

		int a = g.addVertex();
		int b = g.addVertex();
		int c = g.addVertex();
		int d = g.addVertex();

		int ab = g.addDirectedSimpleEdge(a, b);
		int ac = g.addDirectedSimpleEdge(a, c);
		int bd = g.addDirectedSimpleEdge(b, d);
		int cd = g.addDirectedSimpleEdge(c, d);

		NumericalProperty weightVerts = new NumericalProperty("Weight");
		weightVerts.setValue(a, 2);
		weightVerts.setValue(b, 3);
		weightVerts.setValue(c, 3);
		weightVerts.setValue(d, 2);

		NumericalProperty weightEdge = input.getVertexWeightProperty();
		weightEdge.setValue(ab, 1);
		weightEdge.setValue(ac, 2);
		weightEdge.setValue(bd, 2);
		weightEdge.setValue(cd, 1);

		NumericalProperty starts = new NumericalProperty("Starts");
		starts.setValue(a, 0);
		starts.setValue(b, 2);
		starts.setValue(c, 4);
		starts.setValue(d, 7);

		NumericalProperty processors = new NumericalProperty("Processors");
		processors.setValue(a, 1);
		processors.setValue(b, 1);
		processors.setValue(c, 2);
		processors.setValue(d, 2);

		g.setVertexWeightProperty(weightVerts);
		g.setVertexStartProperty(starts);
		g.setVertexProcessorProperty(processors);
		g.setEdgeWeightProperty(weightEdge);

		// log.info(g.getVertexProcessorProperty());

		return g;

	}

}
