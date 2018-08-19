package alg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicLong;

import alg.cost.CostFunction;
import util.PartialScheduleGrph;
import util.ScheduleGrph;

/**
 * The DFS Task class provides a fork-join recursive action for the DFS / BB A*
 * algorithm
 * 
 * @author Matt Frost
 *
 */
public class DFSTask extends RecursiveAction {

	private ScheduleGrph _input;
	private PartialScheduleGrph _current;
	private AtomicLong _lowerBound;
	private PartialScheduleGrph _bestState;
	private final int _numProcessors;
	private final CostFunction _cost;
	private HashSet<String> _closed;
	private int _lastAdded;
	private AtomicLong _iterations;

	public DFSTask(ScheduleGrph input, PartialScheduleGrph current, PartialScheduleGrph best, CostFunction cost,
			HashSet<String> closed, int numProcessors, int lastAdded, AtomicLong lowerBound, AtomicLong iterations) {
		_input = input;
		_current = current;
		_lowerBound = lowerBound;
		_numProcessors = numProcessors;
		_cost = cost;
		_bestState = best;
		_closed = closed;
		_lastAdded = lastAdded;
		_iterations = iterations;
	}

	@Override
	protected void compute() {
		_iterations.set(_iterations.get() + 1);
		ArrayList<DFSTask> tasks = new ArrayList<DFSTask>();
		if (_current.getScore() >= _lowerBound.get()
				|| _closed.contains(_current.getNormalizedCopy(_numProcessors).serialize().getSerialString())
				|| (_lastAdded != -1 && _current.equivalenceCheck(_input, _lastAdded, _numProcessors))) {

			return;
		}

		if (_current.getVertices().size() == _input.getVertices().size()) {
			updateCurrentBest(_current);
			return;
		}

		_closed.add(_current.getNormalizedCopy(_numProcessors).serialize().getSerialString());

		for (int freeTask : _current.getFree(_input)) {
			for (int proc = 1; proc <= this._numProcessors; proc++) {

				PartialScheduleGrph next = _current.copy();
				next.setVerticesLabel(_bestState.getVertexLabelProperty());
				next.addFreeTask(_input, freeTask, proc);
				_cost.applyCost(next, freeTask, _numProcessors);
				if (_current.getScore() < _lowerBound.get()) {
					tasks.add(new DFSTask(_input, next, _bestState, _cost, _closed, _numProcessors, freeTask,
							_lowerBound, _iterations));
				}

			}
		}
		this.invokeAll(tasks);
	}

	private void updateCurrentBest(PartialScheduleGrph s) {
		int underestimate = s.getScore();

		if (underestimate < _lowerBound.get()) {

			_lowerBound.set(underestimate);
			_bestState.setVertexStartProperty(s.getVertexStartProperty());
			_bestState.setVertexProcessorProperty(s.getVertexProcessorProperty());
		}
	}

}