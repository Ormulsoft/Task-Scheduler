package io;

import util.PartialScheduleGrph;

public interface ScheduleListener {
	
	void update(ScheduleEvent event, int iterations, final double memory);
	void updateGraph(ScheduleEvent event,int iterations,PartialScheduleGrph a);

}
