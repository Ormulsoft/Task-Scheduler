package gui;

import io.ScheduleEvent;
import io.ScheduleListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainViewController implements ScheduleListener{
	
	@FXML private Label visited;
	
	public MainViewController(){
		
	}
	
	public void update(ScheduleEvent event, int iterations) {
		if(event.getType() == ScheduleEvent.EventType.NewState){
			
			visited.setText(""+iterations);
			
		}
	}

}
