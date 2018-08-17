package alg;

import java.util.ArrayList;
import java.util.Collections;

import grph.properties.NumericalProperty;
import toools.collections.primitive.LucIntSet;
import util.PartialScheduleGrph;
import util.ScheduleGrph;

public class SequentialAlg implements Algorithm {
	/*
	 * Simple sequential algorithm to generate a valid but not necessarily
	 * optimal solution. Intended as a placeholder, replaced by Astar algorithm.
	 * VERY SLOW
	 */

	private ArrayList<Integer> _processorStarts = new ArrayList<Integer>();
	private NumericalProperty _vertStarts = new NumericalProperty("Start");
	private NumericalProperty _vertProcs = new NumericalProperty("Processor");
	private ScheduleGrph _grph;

	public ScheduleGrph runAlg(ScheduleGrph input, int numCores, int numProcessors) {

		_grph = input;

		// Set up earliest available slots in each processor as zero
		for (int i = 0; i < numProcessors; i++) {
			_processorStarts.add(0);
		}

		// Keep a list of all tasks that havent been given a start time or
		// processor
		ArrayList<Integer> unassignedTasks = new ArrayList<Integer>();
		for (int task : _grph.getVertices()) {
			unassignedTasks.add(task);
		}

		int count = 0;

		// Until there are no more unassigned tasks, iterate through each
		// unassigned task and assign it
		while (!unassignedTasks.isEmpty()) {
			int task = unassignedTasks.get(count);
			boolean taskReady = true;

			// Check the unassigned tasks dependencies have been assigned
			for (int dep : _grph.getInNeighbors(task)) {
				if (unassignedTasks.contains((Integer) dep)) {
					taskReady = false;
					break;
				}
			}

			if (!taskReady) {
				// If the task still has unassigned dependencies, skip it for
				// now
				if (count + 1 < unassignedTasks.size()) {
					count++;
				} else {
					count = 0;
				}
			} else {
				log.info(task + " ready to run");

				// find earliest possible start and the processor that gives
				// this
				int[] slot = findEarliestStart(task);

				// Assign properties according to earliest start time/processor,
				// remove task from list of unassigned
				_vertStarts.setValue(task, slot[0]);
				_vertProcs.setValue(task, slot[1]);
				_processorStarts.set(slot[1], slot[0] + _grph.getVertexWeightProperty().getValueAsInt(task));
				unassignedTasks.remove((Integer) task);
				log.info(unassignedTasks);
				log.info(_processorStarts);
				log.info("");
			}

		}

		log.info("tasks" + unassignedTasks);

		input.setVertexStartProperty(_vertStarts);
		input.setVertexProcessorProperty(_vertProcs);

		return input;
	}

	/**
	 * Get the earliest start time available for this task (based on trial and
	 * error)
	 * 
	 * @param task
	 *            The task to be assigned
	 * @return an array with 0: the start time 1: the processor to assign to
	 */
	private int[] findEarliestStart(int task) {
		LucIntSet deps = _grph.getInNeighbors(task);
		int[] retVal = new int[2]; // 0: start time , 1: processor

		// If no dependencies, assign to whichever processor has the earliest
		// available slot
		if (deps.isEmpty()) {
			retVal[0] = Collections.min(_processorStarts);
			retVal[1] = _processorStarts.indexOf(retVal[0]);
		} else {

			// otherwise, try the task on each processor and find out the
			// earliest time on each that it can start
			// based on processor earliest free time and time required for
			// dependencies to transfer/finish
			ArrayList<Integer> earliestProcTimes = new ArrayList<Integer>();
			for (int i = 0; i < _processorStarts.size(); i++) {
				earliestProcTimes.add(0);
			}

			for (int i = 0; i < _processorStarts.size(); i++) {
				int time = _processorStarts.get(i);

				for (int dep : deps) {
					int drt = _vertStarts.getValueAsInt(dep) + _grph.getVertexWeightProperty().getValueAsInt(dep);

					if (_vertProcs.getValueAsInt(dep) != i) {
						int edge = (Integer) _grph.getEdgesConnecting(dep, task).toArray()[0];
						drt += _grph.getEdgeWeightProperty().getValueAsInt(edge);
					}

					time = Math.max(drt, time);
				}

				earliestProcTimes.set(i, time);
			}

			retVal[0] = Collections.min(earliestProcTimes);
			retVal[1] = earliestProcTimes.indexOf(retVal[0]);
		}

		return retVal;
	}

	public PartialScheduleGrph runAlg() {
		// TODO Auto-generated method stub
		return null;
	}

}
