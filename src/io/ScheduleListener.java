package io;

import java.util.PriorityQueue;

import util.PartialScheduleGrph;

public interface ScheduleListener {
	
	void update(ScheduleEvent event, int iterations);
	void renderSearchSpace(PriorityQueue<PartialScheduleGrph> states);

}
