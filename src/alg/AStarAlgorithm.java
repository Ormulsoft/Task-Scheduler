package alg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.commons.lang.SerializationUtils;

import alg.cost.CostFunction;
import grph.Grph;
import grph.properties.NumericalProperty;
import it.unimi.dsi.fastutil.ints.IntSet;
import toools.collections.primitive.LucIntSet;
import util.PartialScheduleGrph;
import util.ScheduleGrph;

/**
 * This implementation of the AStar algorithm is not completely finished, but
 * returns optimal, valid schedules for all known test inputs.
 * 
 * @author matt frost
 *
 */
public class AStarAlgorithm implements Algorithm {

	// define a timeout (2 mins) for it to return a "valid" but not optimal
	// solution
	private static final int ALGORITHM_TIMEOUT = 2 * 60 * 1000;

	private final CostFunction cost;
	private ScheduleGrph _input;

	// used to compare the cost values in the PriorityQueue

	public AStarAlgorithm(CostFunction cost) {
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

	long serializeTime = 0;
	long costTime = 0;

	public ScheduleGrph runAlg(ScheduleGrph input, int numCores, int numProcessors) {
		_input = input;
		ScheduleGrph original = input;
		input = initializeIdenticalTaskEdges(input);
		long startTime = System.nanoTime();
		// A Queue of states (Partial Schedules), that are ordered by their Cost
		// values.
		PriorityQueue<PartialScheduleGrph> states = new PriorityQueue<PartialScheduleGrph>(1);

		// A set of closed states, which
		HashSet<String> closedStates = new HashSet<String>();

		// initially, add an state with no tasks scheduled (EMPTY)
		PartialScheduleGrph initial = new PartialScheduleGrph(0);
		initial.setVerticesLabel(input.getVertexLabelProperty());
		states.add(initial);
		int totalVertices = input.getNumberOfVertices();
		long deepCopyTime = 0;
		long totTime = System.nanoTime();
		PartialScheduleGrph prev = null;

		int iterations = 0;
		while (states.size() > 0) {
			iterations++;
			boolean foundSameScore = false;
			PartialScheduleGrph s = states.poll();
			String parentSerialized = s.getNormalizedCopy(numProcessors).serialize();
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
				log.info("Total time: " + (System.nanoTime() - totTime) / (1000.0 * 1000));
				log.info("Deep copy time: " + (deepCopyTime / (1000.0 * 1000)));
				log.info("Serialize time: " + serializeTime / (1000.0 * 1000));
				log.info("Cost time: " + costTime / (1000.0 * 1000));
				log.info(" Partial time: " + PartialScheduleGrph.time / (1000.0 * 1000));
				log.info("Number of states at end: " + states.size());
				log.info("Number of closed states: " + closedStates.size());
				log.info("Number of iterations: " + iterations);
				return s;
			} else if (!this.storedInClosedSet(parentSerialized, closedStates)) {
				// loop over all free vertices
				// foundSameScore = false;
				for (int task : freeTasks) {
					for (int pc = 1; pc <= numProcessors; pc++) {

						PartialScheduleGrph next = s.copy();
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
						//
						// find the maximum time the task can start on a
						// processor.
						next.getVertexStartProperty().setValue(task,
								Math.max(processorUpperBound, dependencyUpperBound));

						/**
						 * If the algorithm timed out, default to a "valid"
						 * solution
						 */
						long timeRunning = System.currentTimeMillis() - startTime;
						if (timeRunning > ALGORITHM_TIMEOUT) {
							next.setScore(totalVertices);
							totalVertices--;
							log.info("Out of time! Defaulting to valid only.");
						} else {

							cost.applyCost(next, task, numProcessors);

						}
						String serialized = next.getNormalizedCopy(numProcessors).serialize();
						if (!storedInClosedSet(serialized, closedStates)) {
							next.setTimeAdded(System.nanoTime());
							states.add(next);

						}

					}

				}
			}

			// TODO this takes too long - find alternative
			// if (!foundSameScore)
			storeInClosedSet(parentSerialized, closedStates);

		}
		return null;

	}

	// TODO fix this
	private void storeEquivalentInClosedSet(PartialScheduleGrph g, Set<String> closedStates) {
		// long start = System.nanoTime();
		for (int vert1 : g.getVertices()) {

		}
		String serialized = g.serialize();
		// serializeTime += System.nanoTime() - start;
		closedStates.add(serialized);
	}

	// TODO add equivalence check
	private void storeInClosedSet(String serialized, Set<String> closedStates) {
		// long start = System.nanoTime();
		//String serialized = g.serialize();
		// serializeTime += System.nanoTime() - start;
		closedStates.add(serialized);
	}

	// TODO add equivalence check
	private boolean storedInClosedSet(String serialized, Set<String> closedStates) {
		// long start = System.nanoTime();
		//String serialized = g.serialize();
		// serializeTime += System.nanoTime() - start;
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

	
	/**
	 * Not currently working, seems to break optimal on input 10, more testing needed
	 * 
	 * Checks whether or not a schedule has any equivalent schedules, and should therefore not be expanded.
	 * i.e leaves it to the last equivalentschedule to be expanded. RELIES ON THE ALGORITHM EVENTUALLY CHECKING THE 
	 * EQUIVALENT SCHEDULE WITH ALL TASKS THAT ARE ON THE SAME PROCESSOR AS LASTADDED BEING IN THE INDEX ORDER WHERE POSSIBLE 
	 * @param sched
	 * @param lastAdded
	 * @return
	 */
	private boolean equivalenceCheck(PartialScheduleGrph sched, int lastAdded, int numProcessors) {
		ArrayList<Integer> tasksOnP = new ArrayList<Integer>();
		final NumericalProperty starts = sched.getVertexStartProperty();
		NumericalProperty procs = sched.getVertexProcessorProperty();
		NumericalProperty vertWeights = sched.getVertexWeightProperty();
		NumericalProperty edgeWeights = sched.getEdgeWeightProperty();
		
		// Create an ordered list of all the tasks on the same processor as lastAdded
		int p = procs.getValueAsInt(lastAdded);
		final HashMap<Integer,Integer> newStarts = new HashMap<Integer,Integer>();
		
		for (int task : sched.getVertices()) {
			if (procs.getValueAsInt(task) == p) {
				tasksOnP.add(task);
				newStarts.put(task, starts.getValueAsInt(task));
			}
		}
		
		// Sort on start time
		Comparator<Integer> comp = new Comparator<Integer>(){

			public int compare(Integer o1, Integer o2) {
				return ((Integer)newStarts.get(o1)).compareTo(newStarts.get(o2));
			}
			
		};
		Collections.sort(tasksOnP, comp);
		
		int i = tasksOnP.size() - 2;
		
		// While a task can "bubble up"
		while (i >= 0 && lastAdded < tasksOnP.get(i) && !sched.areVerticesAdjacent(tasksOnP.get(i), lastAdded)) {

			// swap lastadded with task before it
			Collections.swap(tasksOnP, tasksOnP.indexOf((Integer)lastAdded), i);
			
			// for each affected task (now scheduled after lastAdded) reschedule at new earliest time
			for (int j = i; j < tasksOnP.size(); j++) {
				int thisTask = tasksOnP.get(j);
				int time = 0;
				
				if (j > 0) {
					time = newStarts.get(tasksOnP.get(j - 1)) + vertWeights.getValueAsInt(tasksOnP.get(j - 1));
				}
				
				// Find earliest time based on dependencies
				for (int dep : sched.getInNeighbors(thisTask)) {
					int drt = starts.getValueAsInt(dep) + vertWeights.getValueAsInt(dep);

					if (procs.getValueAsInt(dep) != p) {
						int edge = (Integer) sched.getEdgesConnecting(dep, thisTask).toArray()[0];
						drt += edgeWeights.getValueAsInt(edge);
					}

					time = Math.max(drt, time);
				}
				
				newStarts.put(thisTask, time);
			}

			// If processor end is the same, and outgoing dependency edges are "unaffected" - see ougoingCommsOK method - this schedule has equivalents
			if ((newStarts.get(tasksOnP.get(tasksOnP.size() - 1)) + vertWeights.getValueAsInt(tasksOnP.get(tasksOnP.size() - 1))) <= 
					(starts.getValueAsInt(lastAdded) + vertWeights.getValueAsInt(lastAdded))
					&& outgoingCommsOK(sched,newStarts, numProcessors)) {
				return true;
			}
			
			i--;
		}
		
		return false;
	}
	
	/**
	 * Helper method for equivalenceCheck which checks that all outgoing datatransfers of a set of tasks are unaffected by
	 * a change in order/position
	 * @param originalStarts A map of task id to start time, before the change
	 * @param newStarts A map of task id to start time, after the change
	 * @param tasksAfter A list of all the tasks displaced by the switch(es)
	 * @return
	 */
	private boolean outgoingCommsOK(PartialScheduleGrph sched, HashMap<Integer,Integer> newStarts, int numProcessors) {
		NumericalProperty starts = sched.getVertexStartProperty();
		NumericalProperty weights = sched.getVertexWeightProperty();
		NumericalProperty edgeWeights = _input.getEdgeWeightProperty();
		NumericalProperty procs = sched.getVertexProcessorProperty();
		LucIntSet placedTasks = sched.getVertices();
		
		// Check that the children of each affected task are unaffected
		for (int task : newStarts.keySet()) {
			if (newStarts.get(task) > starts.getValueAsInt(task)) {
				for (int child : _input.getOutNeighbors(task)) {
					int edge = (Integer) _input.getEdgesConnecting(task, child).toArray()[0];
					int time = (newStarts.get(task) + weights.getValueAsInt(task)) + edgeWeights.getValueAsInt(edge);
					if (placedTasks.contains(child)) {
						// If child is in the schedule, check that the start time does not contradict the new DRTs
						if (time > starts.getValueAsInt(child)) {
							return false;
						}
					}
					else {
						for (int i = 1; i <= numProcessors; i++) {
							// Verify that the changed end time of task is outweighed by DRTs of at least one other dependency on ALL processors 
							boolean atLeastOneLater = false;
							
							for (int parent : _input.getInNeighbours(child)) {
								
								if (parent != task) {
									if (placedTasks.contains(parent)) {
										int parentTime = starts.getValueAsInt(parent) + weights.getValueAsInt(parent);
										if (procs.getValueAsInt(parent) != i) {
											parentTime += edgeWeights.getValueAsInt((Integer) _input.getEdgesConnecting(parent, child).toArray()[0]);
										}
										if (parentTime >= time) {
											atLeastOneLater = true;
										}
									}
								}
							}
						
						
							if (!atLeastOneLater) {
								// If the child is affected by the changes when placed on any processor, this is not an equivalentsched
								return false;
							}
						}
						
					}
				}
			}
		}
		return true;
	}
}
