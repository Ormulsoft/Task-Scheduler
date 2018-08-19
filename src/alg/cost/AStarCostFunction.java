package alg.cost;

import grph.properties.NumericalProperty;
import toools.collections.primitive.LucIntSet;
import util.PartialScheduleGrph;
import util.ScheduleGrph;

/**
 * A basic cost function that assigns the end time as the cost.
 * 
 * @author Matt
 *
 */
public class AStarCostFunction implements CostFunction {

	ScheduleGrph input;

	public AStarCostFunction(ScheduleGrph input) {
		this.input = input;
	}

	public void applyCost(PartialScheduleGrph g, int addedVertex, int numProcessors) {

		int maxDRT = getComputationalBottomLevel(addedVertex) + (int) g.getVertexStartProperty().getValue(addedVertex);
		int maxBL = 0;

		maxBL = Math.max(g.getLastFBottomLevel(),
				this.getComputationalBottomLevel(addedVertex) + (int) g.getVertexStartProperty().getValue(addedVertex));
		g.setFBottomLevel(maxBL);

		for (int i : g.getFree(input)) {
			int minProc = -1;
			for (int proc = 1; proc <= numProcessors; proc++) {
				int valDRT = this.getDRT(i, g, proc);
				if (valDRT < minProc || minProc == -1) {
					minProc = valDRT;
				}
			}
			if (minProc + this.getComputationalBottomLevel(i) > maxDRT) {
				maxDRT = minProc + this.getComputationalBottomLevel(i);
			}
		}

		int max = Math.max(maxBL, Math.max(getIdleTimeFit(g, numProcessors, addedVertex), maxDRT));

		g.setScore(max);

	}

	/**
	 * Gets the max Computational bottom level value for specified vertex
	 */
	public int getComputationalBottomLevel(int addedVertex) {

		if (input.getOutEdgeDegree(addedVertex) > 0) {
			int max = 0;
			for (int i : input.getOutNeighbors(addedVertex)) {
				int current = (int) (getComputationalBottomLevel(i));
				if (max < current) {
					max = current;
				}
			}
			return max + (int) input.getVertexWeightProperty().getValue(addedVertex);
		} else {
			return (int) (input.getVertexWeightProperty().getValue(addedVertex));
		}
	}

	/**
	 * Returns the FIT(s) function representing the idle time bound of a parital
	 * schedule.
	 * 
	 * @param sched
	 *            The partial schedule whose bound is to be calculated
	 * @param numProcessors
	 *            The number of processors being used for task allocation
	 * @return The idle time bound of this schedule
	 */
	public int getIdleTimeFit(PartialScheduleGrph sched, int numProcessors, int addedVertex) {
		int totalIdle = sched.getLastIdleTime(); // idle time based on idle time
													// before addedVertex is
													// added
		int totalWeight = 0;
		NumericalProperty vertProcs = sched.getVertexProcessorProperty();
		final NumericalProperty vertStarts = sched.getVertexStartProperty();
		NumericalProperty vertWeights = sched.getVertexWeightProperty();
		LucIntSet taskIDs = sched.getVertices();

		for (int task : input.getVertices()) {
			totalWeight += input.getVertexWeightProperty().getValueAsInt(task);
		}

		// Find idle time between addedVertex and previous latest task on same
		// processor
		int lastFinishOnProc = 0;
		for (int task : taskIDs) {
			if (task != addedVertex && vertProcs.getValueAsInt(task) == vertProcs.getValueAsInt(addedVertex)
					&& vertStarts.getValueAsInt(task) < vertStarts.getValueAsInt(addedVertex)) {
				lastFinishOnProc = Math.max(lastFinishOnProc,
						vertStarts.getValueAsInt(task) + vertWeights.getValueAsInt(task));
			}
		}

		totalIdle += vertStarts.getValueAsInt(addedVertex) - lastFinishOnProc; // Idle
																				// time
																				// calculated
																				// incrementally
		sched.setIdleTime(totalIdle); // Update idle time for next increment use

		return (int) Math.ceil((totalIdle + totalWeight) / (double) numProcessors);
	}

	public int getDRT(int addedVertex, PartialScheduleGrph g, int processor) {
		// TODO change this!
		int maxFinTime = 0;

		if (input.getInEdgeDegree(addedVertex) > 0) {
			for (int i : input.getInNeighbors(addedVertex)) {
				int val = g.getVertexStartProperty().getValueAsInt(i) + g.getVertexWeightProperty().getValueAsInt(i);
				if (g.getVertexProcessorProperty().getValue(i) != processor) {
					val += input.getEdgeWeightProperty().getValueAsInt(input.getSomeEdgeConnecting(i, addedVertex));
				}
				if (maxFinTime < val) {
					maxFinTime = val;
				}
			}
			return maxFinTime;
		}

		return 0;

	}
	
	  public  void applyCostParallel(PartialScheduleGrph g, int addedVertex, int numProcessors) {
			int maxFinish = 0;
		int maxBL = 0;

		int maxDRT = getComputationalBottomLevel(addedVertex) + (int) g.getVertexStartProperty().getValue(addedVertex);
		for (int i : g.getVertices()) {
			// get the end time from the highest start time + weight combination
			int val = (int) g.getVertexStartProperty().getValue(i) + (int) g.getVertexWeightProperty().getValue(i);
			if (val > maxFinish) {
				maxFinish = val;

			}
			int valBL = this.getComputationalBottomLevel(i) + (int) g.getVertexStartProperty().getValue(i);
			if (valBL > maxBL) {
				maxBL = valBL;
			}
		}

		for (int i : g.getFree(input)) {
			int minProc = -1;
			for (int proc = 1; proc <= numProcessors; proc++) {
				int valDRT = this.getDRT(i, g, proc);
				if (valDRT < minProc || minProc == -1) {
					minProc = valDRT;
				}
			}
			if (minProc + this.getComputationalBottomLevel(i) > maxDRT) {
				maxDRT = minProc + this.getComputationalBottomLevel(i);
			}
		}

		int max = Math.max(maxFinish, Math.max(maxBL, Math.max(getIdleTimeFit(g, numProcessors, maxFinish), maxDRT)));

		g.setScore(max);

	}


}
