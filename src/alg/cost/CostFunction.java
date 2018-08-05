package alg.cost;

import util.PartialScheduleGrph;

/**
 * This interface allows the creation and injection of different cost functions into algorithms
 * for optimization.
 * @author Matt
 *
 */
public interface CostFunction {

	public void applyCost(PartialScheduleGrph g, int vertex);

}
