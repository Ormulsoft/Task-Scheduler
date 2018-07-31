package alg;

import grph.Grph;

/**
 * Stub algorithm that doesnt alter the input, simply returns it as is
 */
public class AlgorithmStub implements Algorithm {

	@Override
	public Grph runAlg(Grph input, int numCores) {
		log.info("Running stub algorithm");
		return input;
	}

}
