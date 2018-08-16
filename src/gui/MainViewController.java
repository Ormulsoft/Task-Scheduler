package gui;

import io.ScheduleEvent;
import io.ScheduleListener;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainViewController implements ScheduleListener{
	
	private int i = 0;
	
	@FXML private Label visited;
	@FXML private SwingNode Swing;
	public void update(ScheduleEvent event) {
		if(event.getType() == ScheduleEvent.EventType.NewState){
			i++;
			visited.setText(""+i);
		}
	}
	
	

}
