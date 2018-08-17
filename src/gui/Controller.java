package gui;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import alg.AStarAlgorithm;
import alg.cost.AStarCostFunction;
import cnrs.i3s.papareto.demo.function.Main;
import io.Output;
import io.ScheduleEvent;
import io.ScheduleListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import util.ScheduleGrph;
public class Controller implements ScheduleListener{
	
	@FXML
    private Label visited;
	
	Controller parse = this;
	
	@FXML
	Button startBtn;
	@FXML
	private void startAlgorithm() {
		new Thread(new Runnable() {

			public void run() {
				ScheduleGrph out = new AStarAlgorithm(new AStarCostFunction(io.Main.getIn()), parse).runAlg(io.Main.getIn(), io.Main.getNumCores(), io.Main.getNumProcessers());
			}
			
		}).start();
	}
	
	
	public void update(final ScheduleEvent event, final int iterations) {
		Platform.runLater(new Runnable() {
			
			public void run() {
				if(event.getType() == ScheduleEvent.EventType.NewState){
					
					
					visited.setText(""+iterations);
					
				}
				
			}
		});
		
	}
	@FXML
	public void updateLabel() {
		visited.setText("6");
	}


}
