package alg.cost;

import org.apache.log4j.Logger;

import alg.Algorithm;
import util.PartialScheduleGrph;

/**
 * This interface allows the creation and injection of different cost functions into algorithms
 * for optimization.
 * @author Matt
 *
 */
public interface CostFunction {

	final static Logger log = Logger.getLogger(Algorithm.class);

	
	public void applyCost(PartialScheduleGrph g, int vertex, int numProcessors);

}
