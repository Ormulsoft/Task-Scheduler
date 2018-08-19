package alg.cost;

import org.apache.log4j.Logger;

import alg.Algorithm;
import util.PartialScheduleGrph;

/**
 * This interface allows the creation and injection of different cost functions
 * into algorithms for optimization.
 * 
 * @author Matt
 *
 */
public interface CostFunction {

	final static Logger log = Logger.getLogger(Algorithm.class);

	/**
	 * Applies this cost function to the specified schedule graph, setting its score
	 * @param g The graph to apply the cost function to
	 * @param vertex The most recently added task
	 * @param numProcessors The number of processors available to assign tasks to
	 */
	public void applyCost(PartialScheduleGrph g, int vertex, int numProcessors);

}
