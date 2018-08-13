package parallel;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang.SerializationUtils;

import alg.Algorithm;
import alg.cost.CostFunction;
import it.unimi.dsi.fastutil.ints.IntSet;
import pt.runtime.TaskID;
import pt.runtime.TaskIDGroup;
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
public class AStarAlgorithmParallel implements Algorithm {

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

	public AStarAlgorithmParallel(CostFunction cost) {
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

	int serializeTime = 0;
	int costTime = 0;

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
		int deepCopyTime = 0;
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
				log.info("Deep copy time: " + deepCopyTime);
				log.info("Serialize time: " + serializeTime);
				return s;
			} else {
				// loop over all free vertices
				for (int task : freeTasks) {
					for (int pc = 1; pc <= numProcessors; pc++) {
						long start = System.currentTimeMillis();
						PartialScheduleGrph next = s.copy();
						deepCopyTime += System.currentTimeMillis() - start;
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
							start = System.currentTimeMillis();
							cost.applyCost(next, task, numProcessors);
							costTime += System.currentTimeMillis() - start;
						}

						// log.info(next.toDot());
						start = System.currentTimeMillis();
						if (!storedInClosedSet(next.getNormalizedCopy(numProcessors), closedStates)) {
							deepCopyTime += System.currentTimeMillis() - start;
							states.add(next);
						}
					}
				}
			}
			// TODO this takes too long - find alternative
			long start = System.currentTimeMillis();
			storeInClosedSet(s.getNormalizedCopy(numProcessors), closedStates);
			deepCopyTime += System.currentTimeMillis() - start;
		}
		return null;

	}

	// TODO add equivalence check
	private void storeInClosedSet(PartialScheduleGrph g, Set<String> closedStates) {
		long start = System.currentTimeMillis();
		String serialized = new ScheduleDotWriter().createDotText(g, false);
		serializeTime += System.currentTimeMillis() - start;
		closedStates.add(serialized);
	}

	// TODO add equivalence check
	private boolean storedInClosedSet(PartialScheduleGrph g, Set<String> closedStates) {
		// long start = System.currentTimeMillis();
		String serialized = new ScheduleDotWriter().createDotText(g, false);
		// serializeTime += System.currentTimeMillis() - start;
		return closedStates.contains(serialized);
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
		TaskIDGroup g = new TaskIDGroup(pg.getVertices().size());
		CostFunctionParallel A = new CostFunctionParallel(inputSaved);
		for (int task : pg.getVertices()) {
			TaskID id = A.getFree(inputSaved, pg, task,a);
			g.add(id);
		}
		try {
			g.waitTillFinished();
			int i = 0;
			while(g.groupMembers().hasNext()) {
				if(i == g.groupSize()) {
					break;
				}
				TaskID t = (TaskID) g.groupMembers().next();
				a.addAll((Collection<? extends Integer>) t.getReturnResult());
				i++;
			}
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return a;
	}

}
