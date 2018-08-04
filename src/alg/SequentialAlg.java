package alg;


import java.util.ArrayList;
import java.util.Collections;

import grph.properties.NumericalProperty;
import toools.collections.primitive.LucIntSet;
import util.ScheduleGrph;

public class SequentialAlg implements Algorithm {
	
	private ArrayList<Integer> _processorStarts = new ArrayList<Integer>();
	private NumericalProperty _vertStarts = new NumericalProperty("Start");
	private NumericalProperty _vertProcs = new NumericalProperty("Processor");
	private ScheduleGrph _grph;

	public ScheduleGrph runAlg(ScheduleGrph input, int numCores, int numProcessors) {
		
		_grph = input;
		
		for (int i = 0; i < numProcessors; i++) {
			_processorStarts.add(0);
		}
		
		ArrayList<Integer> unassignedTasks = new ArrayList<Integer>();
		for (int task : _grph.getVertices()) {
			unassignedTasks.add(task );
		}
		
		int count = 0;
		
		while (!unassignedTasks.isEmpty()) {
			int task = unassignedTasks.get(count);
			boolean taskReady = true;
			for (int dep : _grph.getInNeighbors(task)) {
				if (unassignedTasks.contains((Integer)dep)) {
					taskReady = false;
					break;
				}
			}
			
			if (!taskReady) {
				if (count + 1 < unassignedTasks.size()) {
					count++;
				}
				else {
					count = 0;
				}
			}
			else {
				log.info(task + " ready to run");
				
				int[] slot = findEarliestStart(task);
				_vertStarts.setValue(task, slot[0]);
				_vertProcs.setValue(task, slot[1]);
				_processorStarts.set(slot[1], slot[0] + _grph.getVertexWeightProperty().getValueAsInt(task));
				unassignedTasks.remove((Integer)task);
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
	
	private int[] findEarliestStart(int task) {
		LucIntSet deps = _grph.getInNeighbors(task);
		int[] retVal = new int[2]; //0: start time , 1: processor
		
		if (deps.isEmpty()) {
			retVal[0] = Collections.min(_processorStarts);
			retVal[1] = _processorStarts.indexOf(retVal[0]);
		}
		else {
			
			ArrayList<Integer> earliestProcTimes = new ArrayList<Integer>();
			for (int i = 0; i < _processorStarts.size(); i++) {
				earliestProcTimes.add(0);
			}
			
			
			for (int i = 0; i < _processorStarts.size(); i++) {
				int time = _processorStarts.get(i);
				
				for (int dep : deps) {
					int drt = _vertStarts.getValueAsInt(dep) + _grph.getVertexWeightProperty().getValueAsInt(dep);

					if (_vertProcs.getValueAsInt(dep) != i) {
						int edge = (Integer) _grph.getEdgesConnecting(dep,task).toArray()[0]; //wtffff luc why is your code like this
						drt += _grph.getEdgeWeightProperty().getValueAsInt(edge);
					}
					
					time = Math.max(drt,time);
				}
				
				earliestProcTimes.set(i, time);
			}
			
			retVal[0] = Collections.min(earliestProcTimes);
			retVal[1] = earliestProcTimes.indexOf(retVal[0]);
		}
		
		return retVal;
	}

}
