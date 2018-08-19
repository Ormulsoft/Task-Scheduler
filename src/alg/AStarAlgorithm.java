package alg;

import java.util.HashSet;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.SerializationUtils;

import alg.cost.CostFunction;
import io.InformationModel;
import io.ScheduleEvent;
import io.ScheduleListener;
import it.unimi.dsi.fastutil.ints.IntSet;
import util.MinimalScheduleGrph;
import util.PartialScheduleGrph;
import util.ScheduleGrph;
import util.StaticUtils;

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
	private static final long serialVersionUID = 1L;

	private final CostFunction _cost;
	private final ScheduleGrph _input;
	private int _numProcessors;
	private int counter = 0;
	private ScheduleListener _listen;
	private int iterations = 0;
	private int statesVisited;
	
	private PartialScheduleGrph s;


	private InformationModel info;

	// A set of closed states, which is used to remove duplicates
	protected HashSet<String> _closedStates = new HashSet<String>();
	protected PriorityBlockingQueue<MinimalScheduleGrph> _openStates = new PriorityBlockingQueue<MinimalScheduleGrph>(
			1);

	public AStarAlgorithm(ScheduleGrph input, CostFunction cost, int numProcessors) {

		info = new InformationModel();
		statesVisited = 0;

		this._cost = cost;
		this._input = input;
		this._numProcessors = numProcessors;
	}

	public AStarAlgorithm(ScheduleGrph input, CostFunction cost, int numProcessors, ScheduleListener listen){
		statesVisited = 0;
		info = new InformationModel();
		this._cost = cost;
		this._input = input;
		this._numProcessors = numProcessors;
		_listen = listen;
	}

	/**
	 * 
	 * This function sets up the initial input graph with virtual edges to reduce
	 * the solution space
	 * 
	 * @param input
	 * @return
	 */
	private ScheduleGrph initializeIdenticalTaskEdges(ScheduleGrph input) {

		ScheduleGrph correctedInput = (ScheduleGrph) SerializationUtils.clone(input);
		// FORKS AND TODO JOINS
		/*
		 * for (int vert : input.getVertices()) { TreeMap<Integer, List<Integer>> sorted
		 * = new TreeMap<Integer, List<Integer>>(); for (int depend :
		 * input.getOutNeighbors(vert)) { int edge = input.getSomeEdgeConnecting(vert,
		 * depend); int weight = input.getEdgeWeightProperty().getValueAsInt(edge); if
		 * (sorted.get(weight) != null) { sorted.get(weight).add(depend); } else {
		 * ArrayList<Integer> a = new ArrayList(); a.add(depend); sorted.put(weight, a);
		 * }
		 * 
		 * } int curr = -1; for (List<Integer> l : sorted.values()) { for (int head : l)
		 * {
		 * 
		 * if (curr != -1) { correctedInput.addDirectedSimpleEdge(curr, head); } curr =
		 * head; }
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

	/**
	 * Puts the dependency information back into the output dotfile.
	 * 
	 * @param finished
	 */
	private void getSetupOutput(PartialScheduleGrph finished) {
		finished.setEdgeWeightProperty(_input.getEdgeWeightProperty());
		for (int edge : _input.getEdges()) {
			int head = _input.getDirectedSimpleEdgeHead(edge);
			int tail = _input.getTheOtherVertex(edge, head);
			finished.addDirectedSimpleEdge(tail, head);
		}
	}

	public PartialScheduleGrph runAlg() {

		ScheduleGrph init = _input;
		// used for timeout
		long startTime = System.nanoTime();
		int totalVertices = init.getNumberOfVertices();

		// create an initial empty state
		PartialScheduleGrph initial = new PartialScheduleGrph(0);
		initial.setVerticesLabel(init.getVertexLabelProperty());
		_openStates.add(initial.serialize());
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(guiRunnable, 0, 1, TimeUnit.SECONDS);
		
		while (_openStates.size() > 0 ) {
			s = _openStates.poll().toGraph();
			s.setVerticesLabel(_input.getVertexLabelProperty());
			s.setVertexWeightProperty(_input.getVertexWeightProperty());

			String parentSerialized = s.getNormalizedCopy(_numProcessors).serialize().getSerialString();

			TreeSet<Integer> freeTasks = s.getFixedFree(init);

			// if is a leaf, return the partial.
			if (freeTasks.size() == 0) {
				// puts the edges and edge weights back into the graph.
				getSetupOutput(s);
				executor.shutdown();
				_listen.updateGraph(new ScheduleEvent(ScheduleEvent.EventType.NewState), iterations,s);
				return s;

			} else if (!this.storedInClosedSet(parentSerialized)) {
				// loop over all free vertices
				// foundSameScore = false;
				for (int task : freeTasks) {
					for (int pc = 1; pc <= _numProcessors; pc++) {

						statesVisited++;
						info.setIterations(statesVisited);
						double memory = ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024.0 * 1024.0 * 1024.0 ));						

						_listen.update(new ScheduleEvent(ScheduleEvent.EventType.NewState), iterations, memory);
						counter++;

						PartialScheduleGrph next = s.copy();
						next.addFreeTask(init, task, pc);
						/**
						 * If the algorithm timed out, default to a "valid" solution
						 */
						long timeRunning = System.currentTimeMillis() - startTime;
						if (timeRunning > ALGORITHM_TIMEOUT) {
							next.setScore(totalVertices);
							totalVertices--;
							log.info("Out of time! Defaulting to valid only.");
						} else {
							// use the cost function object to apply the cost
							_cost.applyCost(next, task, _numProcessors);
						}
						String serialized = next.getNormalizedCopy(_numProcessors).serialize().getSerialString();
						if (!storedInClosedSet(serialized)) {
							_openStates.add(next.serialize());
						}
					}
				}
			}
			storeInClosedSet(parentSerialized);
		}
		return continueWithDFS();
	}

	private PartialScheduleGrph continueWithDFS() {
		log.info("Swapping to DFS!");
		DFSParallel dfs = new DFSParallel(_input, _cost, _numProcessors, 1);
		_openStates.clear();
		_closedStates.clear();
		return dfs.runAlg();
	}

	// TODO add equivalence check
	private void storeInClosedSet(String serialized) {
		_closedStates.add(serialized);
	}

	// TODO add equivalence check
	private boolean storedInClosedSet(String serialized) {
		return _closedStates.contains(serialized);
	}

	Runnable guiRunnable = new Runnable() {
		public void run() {
			_listen.updateGraph(new ScheduleEvent(ScheduleEvent.EventType.NewState), iterations,s);
		}
	};


	public void fireListener() {

		_listen.updateGraph(new ScheduleEvent(ScheduleEvent.EventType.NewState), iterations,s);
	}

}
