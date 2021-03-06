package alg;

import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import alg.cost.CostFunction;
import gui.ScheduleEvent;
import gui.ScheduleListener;
import util.PartialScheduleGrph;
import util.ScheduleGrph;
import util.StaticUtils;

/**
 * The DFSAlgorithm class provides an implementation of the DFS / BB A*
 * algorithm in a sequential (non parallelized) manner.
 * 
 * @author Matt Frost
 *
 */
public class DFSAlgorithm implements Algorithm {

	private final CostFunction _cost;
	private final ScheduleGrph _input;
	private final int _numProcessors;
	private int _lowerBound;
	private PartialScheduleGrph _bestState;
	private HashSet<String> _closed = new HashSet<String>();
	private final ScheduleListener _listen;
	private int _iterations = 0;
	private boolean _vis;

	/**
	 * Sets up the DFS alg with visualization
	 * 
	 * @param input The input graph to run the algorithm on
	 * @param cost The costfunction to use for this algorithm
	 * @param numProcessors The number of available processors to schedule tasks on
	 * @param listen The ScheduleListener to register with this object
	 */
	public DFSAlgorithm(ScheduleGrph input, CostFunction cost, int numProcessors, ScheduleListener listen) {
		this._cost = cost;
		this._input = input;
		this._numProcessors = numProcessors;
		this._listen = listen;
		_vis = true;
	}

	/**
	 * Sets up the DFS algorithm without visualization
	 * 
	 * @param input The input graph to run the algorithm on
	 * @param cost The costfunction to use for this algorithm
	 * @param numProcessors The number of available processors to schedule tasks on
	 */
	public DFSAlgorithm(ScheduleGrph input, CostFunction cost, int numProcessors) {
		this._cost = cost;
		this._input = input;
		this._numProcessors = numProcessors;
		this._listen = null;
		_vis = false;

	}

	/**
	 * Sets up the finished output with dependencies.
	 * 
	 * @param finished The partial schedule that the algorithm believes is the best complete solution
	 */
	private void getSetupOutput(PartialScheduleGrph finished) {
		finished.setEdgeWeightProperty(_input.getEdgeWeightProperty());
		for (int edge : _input.getEdges()) {
			int head = _input.getDirectedSimpleEdgeHead(edge);
			int tail = _input.getTheOtherVertex(edge, head);
			finished.addDirectedSimpleEdge(tail, head);
		}
	}

	/**
	 * Entry point for the algoritm - initializes state space and starts
	 * recursion
	 */
	public PartialScheduleGrph runAlg() {
		ScheduledExecutorService executor = null;
		if (_vis) {
			executor = Executors.newScheduledThreadPool(1);
			executor.scheduleAtFixedRate(statsRunnable, 0, 200, TimeUnit.MILLISECONDS);
		}
		_lowerBound = Integer.MAX_VALUE;

		_bestState = new PartialScheduleGrph(0);
		_bestState.setVerticesLabel(_input.getVertexLabelProperty());

		recursiveSolve(_bestState, -1);

		getSetupOutput(_bestState);
		if (_vis) {
			executor.execute(statsRunnable);
			executor.shutdown();
		}
		return _bestState;

	}

	/**
	 * algorithm recursive method - performs the DFS traversal of the state
	 * space
	 * 
	 * @param p The current partial schedule graph
	 * @param lastAdded The last added task
	 */
	private void recursiveSolve(PartialScheduleGrph p, int lastAdded) {
		_iterations++;
		if (p.getScore() >= _lowerBound
				|| _closed.contains(p.getNormalizedCopy(_numProcessors).serialize().getSerialString())
				|| (lastAdded != -1 && p.equivalenceCheck(_input, lastAdded, _numProcessors))) {
			return;
		}

		if (p.getVertices().size() == _input.getVertices().size()) {

			updateCurrentBest(p);
			return;
		}

		_closed.add(p.getNormalizedCopy(_numProcessors).serialize().getSerialString());

		for (int freeTask : p.getFree(_input)) {
			for (int proc = 1; proc <= this._numProcessors; proc++) {

				PartialScheduleGrph next = p.copy();
				next.setVerticesLabel(_bestState.getVertexLabelProperty());
				next.addFreeTask(_input, freeTask, proc);
				_cost.applyCost(next, freeTask, _numProcessors);
				recursiveSolve(next, freeTask);

			}
		}
	}

	/**
	 * Updates the current best schedulegrph, if it is better than the current.
	 * 
	 * @param s The partial schedule to update to
	 */
	private void updateCurrentBest(PartialScheduleGrph s) {

		int underestimate = s.getScore();

		if (underestimate < _lowerBound) {
			_lowerBound = underestimate;
			_bestState = s;
			if (_vis) {
				_listen.updateGraph(new ScheduleEvent(ScheduleEvent.EventType.NewState), _iterations, _bestState);
			}
		}
	}

	// The runnable to call the listener stats update via
	Runnable statsRunnable = new Runnable() {
		public void run() {
			_listen.update(new ScheduleEvent(ScheduleEvent.EventType.NewState), _iterations,
					StaticUtils.getUsedMemory());
		}
	};

}
