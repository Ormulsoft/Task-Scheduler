package alg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.commons.lang.SerializationUtils;

import alg.cost.CostFunction;
import util.PartialScheduleGrph;
import util.ScheduleDotWriter;
import util.ScheduleGrph;

/**
 * This implementation of the AStar algorithm is not completely finished, but
 * returns optimal, valid schedules for all known test inputs.
 * 
 * @author matt frost
 *
 */
public class AStarAlgorithm implements Algorithm { 

	// define a timeout (10 mins) for it to return a "valid" but not optimal
	// solution
	private static final int ALGORITHM_TIMEOUT = 10 * 60 * 1000;

	private final CostFunction cost;

	// used to compare the cost values in the PriorityQueue
	private class CostChecker implements Comparator<PartialScheduleGrph> {

		public int compare(PartialScheduleGrph grph1, PartialScheduleGrph grph2) {
			if (grph1.getScore() < grph2.getScore())
				return -1;
			else
				return 1;
		}
	}

	public AStarAlgorithm(CostFunction cost) {
		this.cost = cost;
	}

	public ScheduleGrph runAlg(ScheduleGrph input, int numCores, int numProcessors) {
		long startTime = System.currentTimeMillis();

		// A Queue of states (Partial Schedules), that are ordered by their Cost values.
		PriorityQueue<PartialScheduleGrph> states = new PriorityQueue<PartialScheduleGrph>(1, new CostChecker());

		// A set of closed states, which
		HashSet<String> closedStates = new HashSet<String>();

		// initially, add an state with no tasks scheduled (EMPTY)
		PartialScheduleGrph initial = new PartialScheduleGrph(0);
		initial.setVerticesLabel(input.getVertexLabelProperty());
		states.add(initial);

		int totalVertices = input.getNumberOfVertices();

		while (states.size() > 0) {
			PartialScheduleGrph s = states.poll();
			if (!storedInClosedSet(s, closedStates)) {
				storeInClosedSet(s, closedStates);

				// if is a leaf, return the partial.
				ArrayList<Integer> freeTasks = getFree(input, s);
				// if 10 minutes, output valid, but non optimal solution
				if (freeTasks.size() == 0) {
					for (int edge : input.getEdges()) {
						int head = input.getDirectedSimpleEdgeHead(edge);
						int tail = input.getTheOtherVertex(edge, head);
						s.addDirectedSimpleEdge(tail, head);
					}
					s.setEdgeWeightProperty(input.getEdgeWeightProperty());
					return s;
				} else {

					// loop over all free vertices
					for (int task : freeTasks) {
						for (int pc = 1; pc <= numProcessors; pc++) {

							PartialScheduleGrph next = (PartialScheduleGrph) SerializationUtils.clone(s);
							next.addVertex(task);
							next.getVertexWeightProperty().setValue(task,
									input.getVertexWeightProperty().getValue(task));
							next.getVertexProcessorProperty().setValue(task, pc);

							// set the start time based on earliest first on a
							// processor

							// to get the start time, find the time of most recently
							// finishing vertex on the same processor,
							// and store that, also the finish time of the last
							// dependency. starting time would be the maximum\
							// of the two.
							int dependencyUpperBound = 0;
							for (int taskDp : input.getInNeighbours(task)) {
								int edgeTime = 0;
								if (next.getVertexProcessorProperty().getValue(taskDp) != pc) {
									edgeTime = (int) input.getEdgeWeightProperty()
											.getValue(input.getSomeEdgeConnecting(taskDp, task));
								}

								int totalTime = (int) (input.getVertexWeightProperty().getValue(taskDp)
										// needs to be next, not input for start
										+ next.getVertexStartProperty().getValue(taskDp) + edgeTime);
								if (totalTime > dependencyUpperBound) {
									dependencyUpperBound = totalTime;
								}
							}

							/**
							 * find the latest finishing process on the same processor, and factor into the
							 * timing
							 * 
							 * TODO make this a function of the PartialScheduleGrph to suit the abstraction
							 * Named getProcessorFinishTime() ??
							 */
							int processorUpperBound = 0;
							for (int pcTask : next.getVertices()) {
								if (next.getVertexProcessorProperty().getValue(pcTask) == pc && pcTask != task) {
									int totalTime = (int) (next.getVertexWeightProperty().getValue(pcTask)
											+ next.getVertexStartProperty().getValue(pcTask));
									if (totalTime > processorUpperBound) {
										processorUpperBound = totalTime;
									}
								}
							}

							// find the maximum time the task can start on a processor.
							next.getVertexStartProperty().setValue(task,
									Math.max(processorUpperBound, dependencyUpperBound));

							/**
							 * If the algorithm timed out, default to a "valid" solution
							 */
							long timeRunning = System.currentTimeMillis() - startTime;
							if (timeRunning > ALGORITHM_TIMEOUT) {
								next.setScore(totalVertices);
								totalVertices--;
								log.info("Out of time! Defaulting to valid only.");
							} else {
								cost.applyCost(next, task);
							}

							// log.info(next.toDot());
							if (!storedInClosedSet(next, closedStates)) {
								states.add(next);
							} else {

							}
						}
					}
				}
			}
		}
		return null;
	}

	private void storeInClosedSet(PartialScheduleGrph g, Set<String> closedStates) {
		String serialized = new ScheduleDotWriter().createDotText(g, false);
		closedStates.add(serialized);
	}

	private boolean storedInClosedSet(PartialScheduleGrph g, Set<String> closedStates) {
		return closedStates.contains(new ScheduleDotWriter().createDotText(g, false));
	}

	/**
	 * Select all the free vertices based on a current partial schedule, that are
	 * not stored in the current partial schedule
	 * 
	 */

	private ArrayList<Integer> getFree(ScheduleGrph inputSaved, PartialScheduleGrph pg) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		// get all source nodes (no in edges) that are not in the partialschedule
		for (int srcTask : inputSaved.getSources()) {
			if (!pg.containsVertex(srcTask)) {
				a.add(srcTask);
			}
		}
		/*
		 * iterate over tasks in the partial schedule, and add to output ones that are
		 * free and not contained in the current partial
		 */
		for (int task : pg.getVertices()) {
			for (int outEdge : inputSaved.getOutEdges(task)) {
				int otherVert = inputSaved.getTheOtherVertex(outEdge, task);
				// check that not contained in current
				if (!pg.containsVertex(otherVert)) {
					boolean add = true;
					// check that dependencies are satisfied.
					for (int e : inputSaved.getInEdges(otherVert)) {
						if (!pg.containsVertex(inputSaved.getTheOtherVertex(e, otherVert))) {
							add = false;
							break;
						}
					}
					if (add) {
						a.add(otherVert);
					}
				}
			}
		}
		return a;
	}

}
