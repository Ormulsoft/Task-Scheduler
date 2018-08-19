package alg;

import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import alg.cost.CostFunction;
import io.ScheduleEvent;
import io.ScheduleListener;
import util.PartialScheduleGrph;
import util.ScheduleGrph;
import util.StaticUtils;

public class DFSParallel implements Algorithm {

	private final CostFunction _cost;
	private final ScheduleGrph _input;
	private final int _numProcessors;
	private AtomicLong _lowerBound = new AtomicLong();
	private PartialScheduleGrph _bestState;
	private ForkJoinPool forkJoinPool;
	private HashSet<String> _closed;
	private ScheduleListener _listen;
	
	private AtomicLong _iterations = new AtomicLong();
	
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

	public  DFSParallel(ScheduleGrph input, CostFunction cost, int numProcessors, int numCores, ScheduleListener listen) {
		this(input, cost, numProcessors, numCores);
		this._listen = listen;
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
		
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(guiRunnable, 0, 1, TimeUnit.SECONDS);
		
		_bestState.setVerticesLabel(_input.getVertexLabelProperty());
		forkJoinPool.invoke(new DFSTask(_input, _start, _bestState, _cost, _closed, _numProcessors, -1, _lowerBound, _iterations));
		getSetupOutput(_bestState);
		executor.shutdown();
		return _bestState;
	}
	
	Runnable guiRunnable = new Runnable() {
		public void run() {
			_listen.updateGraph(new ScheduleEvent(ScheduleEvent.EventType.NewState), _iterations.intValue(),_bestState);
			_listen.update(new ScheduleEvent(ScheduleEvent.EventType.NewState), _iterations.intValue(), StaticUtils.getRemainingMemory());
		}
	};

}
