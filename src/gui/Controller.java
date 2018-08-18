package gui;
import java.io.IOException;
import java.net.URL;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

import org.graphstream.graph.Graph;

import java.util.HashMap;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;

import alg.AStarAlgorithm;
import alg.cost.AStarCostFunction;
import cnrs.i3s.papareto.demo.function.Main;
import javafx.embed.swing.SwingNode;
import io.Output;
import io.ScheduleEvent;
import io.ScheduleListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import util.ScheduleGrph;

public class Controller implements ScheduleListener{
	final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
	@FXML
    private Label visited;
	
	Controller parse = this;
	@FXML
	Button startBtn;
	@FXML
    private StackedBarChart<?, ?> sbc;
	HashMap<Integer, XYChart.Series> Processer = new HashMap<Integer, XYChart.Series>();
	@FXML
	private void startAlgorithm() {
		new Thread(new Runnable() {

			public void run() {
				ScheduleGrph out = new AStarAlgorithm(new AStarCostFunction(io.Main.getIn()), parse).runAlg(io.Main.getIn(), io.Main.getNumCores(), io.Main.getNumProcessers());
			}
			
		}).start();
		intializeData();
	}
	
	private void addAllData() {
		for(int key :Processer.keySet()){
			
			sbc.getData().add(Processer.get(key));
		}
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
	private void intializeData() {
		for(int i = 0;i<io.Main.getNumProcessers();i++) {
		 XYChart.Series a = new XYChart.Series();
		 a.setName(""+i);
		 a.getData().add(new XYChart.Data("" + i,i+2));
		 Processer.put(i, a);
		}
		addAllData();
	}
	
	
}
