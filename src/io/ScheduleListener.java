package io;

import java.util.HashSet;
import java.util.PriorityQueue;
import util.PartialScheduleGrph;

public interface ScheduleListener {
	

	void update(ScheduleEvent event, int iterations, final double memory);
	void updateGraph(ScheduleEvent event,int iterations,PartialScheduleGrph a);

}
