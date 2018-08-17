package io;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;


public class InformationModel {
	
	private static List<ScheduleListener> _listeners;
	
	private int _iterations;
	
	public InformationModel() {

		_listeners = new ArrayList<ScheduleListener>();
	}
	
	public void addListener(ScheduleListener listener) {
		_listeners.add(listener);
	}
	
	/**
	 * Deregisters a ShapeModelListener from this ShapeModel object.
	 */
	public void removeListener(ScheduleListener listener) {
		_listeners.remove(listener);
	}
	
	public void setIterations(int iterations) {
		_iterations = iterations;
	}
	
	
	public void fire(ScheduleEvent event) {
		for(ScheduleListener listener : _listeners) {
			listener.update(event, _iterations);
		}
	}

}
