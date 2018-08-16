package alg;

import java.util.HashSet;
import java.util.concurrent.PriorityBlockingQueue;

import alg.cost.CostFunction;
import util.PartialScheduleGrph;
import util.ScheduleGrph;

public class AStarThread extends AStarAlgorithm implements Runnable {

	private PartialScheduleGrph _output;

	public AStarThread(ScheduleGrph input, CostFunction cost, int numProcessors,
			PriorityBlockingQueue<PartialScheduleGrph> open, HashSet<String> closed) {

		super(input, cost, numProcessors);

		this.closedStates = closed;
		this.states = open;

	}

	public void run() {
		_output = runAlg();

	}

	public PartialScheduleGrph getOutput() {
		return _output;
	}

}
