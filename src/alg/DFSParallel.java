package alg;

import java.util.HashSet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;

import alg.cost.CostFunction;
import util.PartialScheduleGrph;
import util.ScheduleGrph;

public class DFSParallel implements Algorithm {

	private final CostFunction _cost;
	private final ScheduleGrph _input;
	private final int _numProcessors;
	private AtomicLong _lowerBound = new AtomicLong();
	private PartialScheduleGrph _bestState;
	private ForkJoinPool forkJoinPool;
	private HashSet<String> _closed;

	private PartialScheduleGrph _start = new PartialScheduleGrph(0);

	public DFSParallel(ScheduleGrph input, CostFunction cost, int numProcessors, int numCores) {
		this.forkJoinPool = new ForkJoinPool(numCores);
		this._cost = cost;
		this._input = input;
		this._numProcessors = numProcessors;
		this._lowerBound.set(Long.MAX_VALUE);
		this._bestState = new PartialScheduleGrph(0);
		this._bestState.addVertices(input.getVertices());
		this._bestState.setVertexWeightProperty(input.getVertexWeightProperty());
		this._bestState.setVerticesLabel(input.getVertexLabelProperty());

		this._closed = new HashSet<String>();

	}

	public DFSParallel(ScheduleGrph input, HashSet<String> closed, CostFunction cost, int numProcessors, int numCores) {
		this(input, cost, numProcessors, numCores);

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
		_bestState.setVerticesLabel(_input.getVertexLabelProperty());
		forkJoinPool.invoke(new DFSTask(_input, _start, _bestState, _cost, _closed, _numProcessors, -1, _lowerBound));
		getSetupOutput(_bestState);

		return _bestState;
	}

}
