package alg;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.commons.lang.SerializationUtils;

import alg.cost.CostFunction;
import it.unimi.dsi.fastutil.ints.IntSet;
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
public class BBAStarAlgorithm implements Algorithm {

	// define a timeout (2 mins) for it to return a "valid" but not optimal
	// solution
	private static final int ALGORITHM_TIMEOUT = 2 * 60 * 1000;

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

	public BBAStarAlgorithm(CostFunction cost) {
		this.cost = cost;
	}

	/**
	 * 
	 * This function TODO TEST ME
	 * 
	 * @param input
	 * @return
	 */
	private ScheduleGrph initializeIdenticalTaskEdges(ScheduleGrph input) {

		ScheduleGrph correctedInput = (ScheduleGrph) SerializationUtils.clone(input);
		// FORKS AND TODO JOINS
		/*
		 * for (int vert : input.getVertices()) { TreeMap<Integer,
		 * List<Integer>> sorted = new TreeMap<Integer, List<Integer>>(); for
		 * (int depend : input.getOutNeighbors(vert)) { int edge =
		 * input.getSomeEdgeConnecting(vert, depend); int weight =
		 * input.getEdgeWeightProperty().getValueAsInt(edge); if
		 * (sorted.get(weight) != null) { sorted.get(weight).add(depend); } else
		 * { ArrayList<Integer> a = new ArrayList(); a.add(depend);
		 * sorted.put(weight, a); }
		 * 
		 * } int curr = -1; for (List<Integer> l : sorted.values()) { for (int
		 * head : l) {
		 * 
		 * if (curr != -1) { correctedInput.addDirectedSimpleEdge(curr, head); }
		 * curr = head; }
		 * 
		 * } }
		 */

		for (int vert : input.getVertices()) {
			for (int vert2 : input.getVertices()) {
				int vertWeight = input.getVertexWeightProperty().getValueAsInt(vert);
				int vert2Weight = input.getVertexWeightProperty().getValueAsInt(vert2);

				IntSet vertParents = input.getInNeighbors(vert);
				IntSet vert2Parents = input.getInNeighbors(vert2);

				IntSet vertChildren = input.getOutNeighbors(vert);
				IntSet vert2Children = input.getOutNeighbors(vert2);

				boolean childrenEqual = vertChildren.containsAll(vert2Children)
						&& vert2Children.containsAll(vertChildren);

				boolean parentEqual = vertParents.containsAll(vert2Parents) && vert2Parents.containsAll(vertParents);

				boolean inWeightsSame = true;
				for (int parent : vertParents) {
					for (int parent2 : vert2Parents) {
						if (parent == parent2 && input.getEdgeWeightProperty()
								.getValue(input.getSomeEdgeConnecting(parent, vert)) == input.getEdgeWeightProperty()
										.getValue(input.getSomeEdgeConnecting(parent2, vert2))) {

						} else {
							inWeightsSame = false;
							break;
						}
					}
					if (!inWeightsSame) {
						break;
					}
				}

				boolean outWeightsSame = true;
				for (int child : vertChildren) {
					for (int child2 : vert2Children) {
						if (child == child2 && input.getEdgeWeightProperty()
								.getValue(input.getSomeEdgeConnecting(vert, child)) == input.getEdgeWeightProperty()
										.getValue(input.getSomeEdgeConnecting(vert2, child2))) {

						} else {
							outWeightsSame = false;
							break;
						}
					}
					if (!outWeightsSame) {
						break;
					}
				}
				boolean alreadyEdge = correctedInput.areVerticesAdjacent(vert, vert2)
						|| correctedInput.areVerticesAdjacent(vert2, vert);

				if (vert != vert2 && vertWeight == vert2Weight && parentEqual && childrenEqual && inWeightsSame
						&& outWeightsSame && !alreadyEdge) {
					int edge = correctedInput.addDirectedSimpleEdge(vert, vert2);
					correctedInput.getEdgeWeightProperty().setValue(edge, 0);
				}
			}
		}
		return correctedInput;
	}

	private int findBound(ScheduleGrph input, PartialScheduleGrph next) {

		for (int i : getFree(input, next)) {
			int dependencyUpperBound = 0;
			for (int taskDp : input.getInNeighbours(task)) {
				int edgeTime = 0;
				if (next.getVertexProcessorProperty().getValue(taskDp) != pc) {
					edgeTime = (int) input.getEdgeWeightProperty().getValue(input.getSomeEdgeConnecting(taskDp, task));
				}

				int totalTime = (int) (input.getVertexWeightProperty().getValue(taskDp)
						// needs to be next, not input for start
						+ next.getVertexStartProperty().getValue(taskDp) + edgeTime);
				if (totalTime > dependencyUpperBound) {
					dependencyUpperBound = totalTime;
				}
			}
		}
	}

	public ScheduleGrph runAlg(ScheduleGrph input, int numCores, int numProcessors) {
		ScheduleGrph original = input;
		input = initializeIdenticalTaskEdges(input);
		long startTime = System.currentTimeMillis();

		// A Queue of states (Partial Schedules), that are ordered by their Cost
		// values.
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

			// if is a leaf, return the partial.
			HashSet<Integer> freeTasks = getFree(input, s);
			// if 10 minutes, output valid, but non optimal solution
			if (freeTasks.size() == 0) {
				for (int edge : original.getEdges()) {
					int head = original.getDirectedSimpleEdgeHead(edge);
					int tail = original.getTheOtherVertex(edge, head);
					s.addDirectedSimpleEdge(tail, head);
				}
				s.setEdgeWeightProperty(original.getEdgeWeightProperty());
				return s;
			} else {

				// loop over all free vertices
				for (int task : freeTasks) {
					for (int pc = 1; pc <= numProcessors; pc++) {

						PartialScheduleGrph next = (PartialScheduleGrph) SerializationUtils.clone(s);
						next.addVertex(task);
						next.getVertexWeightProperty().setValue(task, input.getVertexWeightProperty().getValue(task));
						next.getVertexProcessorProperty().setValue(task, pc);

						// set the start time based on earliest first on a
						// processor

						// to get the start time, find the time of most
						// recently
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
						 * find the latest finishing process on the same
						 * processor, and factor into the timing
						 * 
						 * TODO make this a function of the PartialScheduleGrph
						 * to suit the abstraction Named
						 * getProcessorFinishTime() ??
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

						// find the maximum time the task can start on a
						// processor.
						next.getVertexStartProperty().setValue(task,
								Math.max(processorUpperBound, dependencyUpperBound));

						/**
						 * If the algorithm timed out, default to a "valid"
						 * solution
						 */
						long timeRunning = System.currentTimeMillis() - startTime;
						if (timeRunning > ALGORITHM_TIMEOUT && false) {
							next.setScore(totalVertices);
							totalVertices--;
							log.info("Out of time! Defaulting to valid only.");
						} else {
							cost.applyCost(next, task, numProcessors);
						}

						// log.info(next.toDot());
						if (!storedInClosedSet(next.getNormalizedCopy(), closedStates)) {

							states.add(next);
							// log.info(next.toDot());
							// storeInClosedSet(next, closedStates);
						} else {
							// log.info(closedStates.size());

						}
					}
				}
			}
			storeInClosedSet(s.getNormalizedCopy(), closedStates);
		}
		return null;

	}

	// TODO add equivalence check
	private void storeInClosedSet(ScheduleGrph g, Set<String> closedStates) {
		String serialized = new ScheduleDotWriter().createDotText(g, false);
		closedStates.add(serialized);
	}

	private boolean storedInClosedSet(ScheduleGrph g, Set<String> closedStates) {
		return closedStates.contains(new ScheduleDotWriter().createDotText(g, false));
	}

	/**
	 * Select all the free vertices based on a current partial schedule, that
	 * are not stored in the current partial schedule
	 * 
	 */

	private HashSet<Integer> getFree(ScheduleGrph inputSaved, PartialScheduleGrph pg) {
		HashSet<Integer> a = new HashSet<Integer>();
		// get all source nodes (no in edges) that are not in the
		// partialschedule
		for (int srcTask : inputSaved.getSources()) {
			if (!pg.containsVertex(srcTask)) {
				a.add(srcTask);
			}
		}
		/*
		 * iterate over tasks in the partial schedule, and add to output ones
		 * that are free and not contained in the current partial
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