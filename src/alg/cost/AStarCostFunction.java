package alg.cost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

	/**
	 * Gets the max Computational bottom level value for all current vertices
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
	public int getIdleTimeFit(PartialScheduleGrph sched, int numProcessors, int maxFinish) {
		int totalIdle = 0;
		int totalWeight = 0;
		NumericalProperty vertProcs = sched.getVertexProcessorProperty();
		final NumericalProperty vertStarts = sched.getVertexStartProperty();
		NumericalProperty vertWeights = sched.getVertexWeightProperty();
		LucIntSet taskIDs = sched.getVertices();

		// Create a list of lists, each list relates to a processor and stores
		// the tasks on that processor
		// for sorting later
		ArrayList<ArrayList<Integer>> processors = new ArrayList<ArrayList<Integer>>();

		for (int i = 0; i < numProcessors; i++) {
			processors.add(new ArrayList<Integer>());
		}

		// Add each task to the list related to the relevant processor
		for (int task : taskIDs) {
			processors.get(vertProcs.getValueAsInt(task) - 1).add(task);

		}
		for (int task : input.getVertices()) {
			totalWeight += input.getVertexWeightProperty().getValueAsInt(task);
		}

		// Add idle time of each processor to total
		for (int i = 0; i < numProcessors; i++) {

			ArrayList<Integer> list = processors.get(i);

			// Sort tasks based on start time
			Collections.sort(list, new Comparator<Integer>() {

				public int compare(Integer o1, Integer o2) {
					return ((Integer) (vertStarts.getValueAsInt(o1)))
							.compareTo((Integer) (vertStarts.getValueAsInt(o2)));
				}

			});
			int finishTime = 0;

			// If there is a gap between a previous task and this one, add the
			// gap to idletime.
			for (int task : list) {
				if (vertStarts.getValueAsInt(task) > finishTime) {
					totalIdle += vertStarts.getValueAsInt(task) - finishTime;
				}
				finishTime = vertStarts.getValueAsInt(task) + vertWeights.getValueAsInt(task);

			}

		}

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

}
