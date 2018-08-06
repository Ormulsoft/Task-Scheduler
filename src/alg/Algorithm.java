package alg;

import org.apache.log4j.Logger;

import util.ScheduleGrph;

/**
 * The Algorithm interface provides a starting point for any search algorithm to
 * be implemented, to allow for the testing and comparison of multiple
 * algorithms in this project.
 * 
 * @author Matt
 *
 */
public interface Algorithm {

	final static Logger log = Logger.getLogger(Algorithm.class);

	/**
	 * This function is used to run any algorithm. It produces a schedule of
	 * tasks, based on task dependencies and a number of processors to schedule
	 * on.
	 * 
	 * @param input
	 * @param numCores
	 * @param numProcessors
	 * @return
	 */
	public ScheduleGrph runAlg(ScheduleGrph input, int numCores,
			int numProcessors);

}
