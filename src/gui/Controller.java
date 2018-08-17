package gui;
import java.net.URL;
import java.util.ResourceBundle;

import alg.AStarAlgorithm;
import alg.cost.AStarCostFunction;
import cnrs.i3s.papareto.demo.function.Main;
import io.ScheduleEvent;
import io.ScheduleListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import util.ScheduleGrph;
public class Controller implements ScheduleListener{
	
	@FXML
    private Label visited;
	
	@FXML
	Button startBtn;
	@FXML
	private void startAlgorithm() {
		ScheduleGrph out = new AStarAlgorithm(new AStarCostFunction(io.Main.getIn())).runAlg(io.Main.getIn(), io.Main.getNumCores(), io.Main.getNumProcessers());

	}
		
	public void update(ScheduleEvent event, int iterations) {
		if(event.getType() == ScheduleEvent.EventType.NewState){
			
			visited.setText(""+iterations);
			
		}
	}
	@FXML
	public void updateLabel() {
		visited.setText("6");
	}

}
