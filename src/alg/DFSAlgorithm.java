package alg;

import java.util.HashSet;

import alg.cost.CostFunction;
import util.PartialScheduleGrph;
import util.ScheduleGrph;

public class DFSAlgorithm implements Algorithm {

	private final CostFunction _cost;
	private final ScheduleGrph _input;
	private final int _numProcessors;
	private int _lowerBound;
	private PartialScheduleGrph _bestState;
	private HashSet<String> _closed = new HashSet<String>();
	
	public DFSAlgorithm(ScheduleGrph input, CostFunction cost, int numProcessors) {
		this._cost = cost;
		this._input = input;
		this._numProcessors = numProcessors;
	}

	private void getSetupOutput(PartialScheduleGrph finished) {
		finished.setEdgeWeightProperty(_input.getEdgeWeightProperty());
		for (int edge : _input.getEdges()) {
			int head = _input.getDirectedSimpleEdgeHead(edge);
			int tail = _input.getTheOtherVertex(edge, head);
			finished.addDirectedSimpleEdge(tail, head);
		}
	}


	public PartialScheduleGrph runAlg() {

		_lowerBound = Integer.MAX_VALUE;

		_bestState = new PartialScheduleGrph(0);
		_bestState.setVerticesLabel(_input.getVertexLabelProperty());

		recursiveSolve(_bestState, -1);

		getSetupOutput(_bestState);

		return _bestState;

	}

	private void recursiveSolve(PartialScheduleGrph p, int lastAdded) {
		
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

	private void updateCurrentBest(PartialScheduleGrph s) {

		int underestimate = s.getScore();

		if (underestimate < _lowerBound) {
			_lowerBound = underestimate;
			_bestState = s;
		}
	}

}
