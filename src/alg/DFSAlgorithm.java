package alg;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import alg.cost.CostFunction;
import util.PartialScheduleGrph;
import util.ScheduleGrph;

public class DFSAlgorithm extends RecursiveAction implements Algorithm {
	private ForkJoinPool forkJoinPool;
	private final CostFunction _cost;
	private final ScheduleGrph _input;
	private final int _numProcessors;
	private int _lowerBound;
	private PartialScheduleGrph _bestState;

	public DFSAlgorithm(ScheduleGrph input, CostFunction cost, int numProcessors, int numCores) {
		this.forkJoinPool = new ForkJoinPool(numCores);
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

	@Override
	public PartialScheduleGrph runAlg() {

		_lowerBound = Integer.MAX_VALUE;

		_bestState = new PartialScheduleGrph(0);
		_bestState.setVerticesLabel(_input.getVertexLabelProperty());

		recursiveSolve(_bestState);

		getSetupOutput(_bestState);
		return _bestState;

	}

	private void recursiveSolve(PartialScheduleGrph p) {

		if (p.getScore() >= _lowerBound) {
			return;
		}

		if (p.getVertices().size() == _input.getVertices().size()) {

			updateCurrentBest(p);
			return;
		}

		for (int freeTask : p.getFree(_input)) {
			for (int proc = 1; proc <= this._numProcessors; proc++) {

				PartialScheduleGrph next = p.copy();
				next.addFreeTask(_input, freeTask, proc);
				_cost.applyCost(next, freeTask, _numProcessors);
				recursiveSolve(next);

			}
		}
	}

	private void updateCurrentBest(PartialScheduleGrph s) {
		int underestimate = s.getScore();

		if (underestimate < _lowerBound) {
			log.info(s.getScore());
			_lowerBound = underestimate;
			_bestState = s;
		}
	}

	@Override
	protected void compute() {
		// TODO Auto-generated method stub

	}

}
