package gui;

/**
 * This class represents an event associated with a schedule change, such as 
 * best state changing, a state being generated, or a state being pruned 
 */
public class ScheduleEvent {
	
	public enum EventType {NewBest, NewState, StatePruned};
	
	private EventType _type; 
	
	/**
	 * Create a ScheduleEvent of the specified type
	 * @param type The enum event type
	 */
	public ScheduleEvent(EventType type){
		_type = type;
	}
	
	/**
	 * Create a schedule event representing a new state being generated
	 * @return
	 */
	public static ScheduleEvent makeNewStateEvent(){
		
		return new ScheduleEvent(EventType.NewState);
	}
	
	/**
	 * Get the type of event of this ScheduleEvent
	 * @return
	 */
	public EventType getType(){
		
		return _type;
	}

}
