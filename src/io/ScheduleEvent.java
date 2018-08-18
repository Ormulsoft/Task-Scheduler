package io;


public class ScheduleEvent {
	
	public enum EventType {NewBest, NewState, StatePruned};
	
	private EventType _type; 
	
	public ScheduleEvent(EventType type){
		_type = type;
	}
	
	public static ScheduleEvent makeNewStateEvent(){
		
		return new ScheduleEvent(EventType.NewState);
	}
	
	public EventType getType(){
		
		return _type;
	}

}
