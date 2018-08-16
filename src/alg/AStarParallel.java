package alg;

import java.util.HashSet;
import java.util.concurrent.PriorityBlockingQueue;

import alg.cost.CostFunction;
import util.PartialScheduleGrph;
import util.ScheduleGrph;

public class AStarParallel implements Algorithm {

	private int _numCores;
	private int _numProcessors;
	private ScheduleGrph _input;
	private CostFunction _cost;

	public AStarParallel(ScheduleGrph input, CostFunction cost, int numProcessors, int numCores) {
		this._numCores = numCores;
		this._numProcessors = numProcessors;
		this._input = input;
		this._cost = cost;
	}

	/**
	 * 
	 */
	public PartialScheduleGrph runAlg() {
		HashSet<String> closedStates = new HashSet<String>();
		PriorityBlockingQueue<PartialScheduleGrph> states = new PriorityBlockingQueue<PartialScheduleGrph>(1);

		AStarThread[] t = new AStarThread[this._numCores];
		Thread[] threads = new Thread[this._numCores];

		for (int i = 0; i < this._numCores; i++) {
			t[i] = new AStarThread(_input, _cost, _numProcessors, states, closedStates);
			threads[i] = new Thread(t[i]);
		}

		for (int i = 0; i < this._numCores; i++) {
			threads[i].start();

		}

		// Wait for all the threads to finish
		for (int i = 0; i < this._numCores; i++) {
			try {
				threads[i].join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		int index = 0;
		int max = Integer.MAX_VALUE;
		// Loop through all the threads
		for (int i = 0; i < this._numCores; i++) {
			if (t[i].getOutput() != null) {
				int finishTime = t[i].getOutput().getBottomLevel();
				if (finishTime < max) {
					max = finishTime;
					index = i;
				}
			}
		}
		return t[index].getOutput();

	}

}
