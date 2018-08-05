package alg;

import org.apache.log4j.Logger;

import util.ScheduleGrph;

public interface Algorithm {
	/**
	 * The basic interface for all algorithm objects to fulfill.
	 */

	final static Logger log = Logger.getLogger(Algorithm.class);

	/**
	 * Runs the algorithm on the inputted ScheduleGrph, with options for number of cores, and number of processors
	 * @param input The ScheduleGrph representing the read in dot file graph
	 * @param numCores The number of cores to run the algorithm on
	 * @param numProcessors The number of processors to simulate task allocation on
	 * @return
	 */
	public ScheduleGrph runAlg(ScheduleGrph input, int numCores, int numProcessors);


}
