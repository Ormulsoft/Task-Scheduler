package gui;

import java.util.HashSet;
import java.util.PriorityQueue;

import util.PartialScheduleGrph;

/**
 * This interface allows implementation of a listener to be registered with an algorithm, 
 * with the view of observing changes to schedules explored by the algorithm.
 * @author Harpreet
 *
 */
public interface ScheduleListener {
	
	/**
	 * Update the listener for the GUI stats display
	 * @param event The event that has occurred
	 * @param iterations The current number of iterations performed by the algorithm
	 * @param memory The current used memory of the process
	 */
	void update(ScheduleEvent event, int iterations, final double memory);
	
	/**
	 * Update the listener for the current best schedule gantt chart display
	 * @param event The event that has occurred
	 * @param iterations The current number of iterations performed by the algorithm
	 * @param a The schedule graph (current best) to display on the gantt chart
	 */
	void updateGraph(ScheduleEvent event,int iterations,PartialScheduleGrph a);

}
