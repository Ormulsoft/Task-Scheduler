package io;

import util.PartialScheduleGrph;

public interface ScheduleListener {
	
	void update(ScheduleEvent event, int iterations);
	void updateGraph(ScheduleEvent event,int iterations,PartialScheduleGrph a);

}
