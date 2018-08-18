package io;

public interface ScheduleListener {
	
	void update(ScheduleEvent event, int iterations, final double memory);

}
