package alg;

import grph.Grph;
import grph.properties.NumericalProperty;
import util.ScheduleGrph;

/**
 * Stub algorithm that doesnt alter the input, simply returns it as is
 */
public class AlgorithmStub implements Algorithm {

	public ScheduleGrph runAlg(ScheduleGrph input, int numCores) {
		log.info("Running stub algorithm");

		log.info(input);
		input.getAllStartVertexIndices();
		input.getAllStartVertexIndices();
		return input;

	}

}
