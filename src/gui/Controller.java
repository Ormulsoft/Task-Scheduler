package gui;
import java.io.IOException;
import java.net.URL;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import org.graphstream.graph.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

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
import toools.collections.Collections;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import util.ScheduleGrph;

public class Controller implements ScheduleListener{
	final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    
	@FXML
	ScrollPane _input;
	@FXML
    private Label visited;
	
	@FXML
	private Label time;
	
	Timer myTimer = new Timer();
	
	private long seconds = 0;
	
	Controller parse = this;
	@FXML
	Button startBtn;
	@FXML
    private StackedBarChart<?, ?> sbc;
	HashMap<Integer, XYChart.Series> Processer = new HashMap<Integer, XYChart.Series>();
	
	
	@FXML
	public void initialize() {
		viewGraph(_input,io.Main.getIn());
	}
	
	
	@FXML
	private void startAlgorithm() {
		new Thread(new Runnable() {

			public void run() {
				ScheduleGrph out = new AStarAlgorithm(new AStarCostFunction(io.Main.getIn()), parse).runAlg(io.Main.getIn(), io.Main.getNumCores(), io.Main.getNumProcessers());
				myTimer.cancel();
			}
			
		}).start();
		intializeData();
		
		new Thread(new Runnable() {
			
			public void run() {
				myTimer.scheduleAtFixedRate(task, 1000, 1000);
				
			}
		}).start();
		
		
		
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
	
	TimerTask task = new TimerTask(){

		@Override
		public void run() {
			Platform.runLater(new Runnable() {
				
				public void run() {
					seconds++;
					time.setText(""+seconds);
					
				}
			});
			
		}
		
	};
	
	
	public static void viewGraph(ScrollPane display, ScheduleGrph graph) {
		
		boolean isNextLayer = true;
		int currentLayer = 0;
		ArrayList<Integer> freeNodes = new ArrayList<Integer>();
		ArrayList<Integer> nextLayer = new ArrayList<Integer>();
		
		freeNodes.addAll(graph.getSources());
		
		while (isNextLayer) {
			int i = 0;
			for (int vert : freeNodes) {
				Circle node = new Circle(20);
				node.setId(Integer.toString(vert));
				if (freeNodes.size() % 2 == 1) {
					node.setLayoutX(350 + (60 * ((i + 1)/ 2)  * (Math.pow(-1, i))));
				}
				else {
					node.setLayoutX(350 + (60 * ((i + 1)/ 2)  * (Math.pow(-1, i))));
				}
				node.setLayoutY(40 + currentLayer * 80);
				((AnchorPane)display.getContent()).getChildren().add(node);
				
				for (int child : graph.getOutNeighbors(vert)) {
					nextLayer.add(child);
				}
				i++;
			}
			
			if (nextLayer.isEmpty()) {
				isNextLayer = false;
			}
			else {
				freeNodes = (ArrayList<Integer>) nextLayer.clone();
				nextLayer.clear();
				currentLayer++;
			}
		}
		
	}
	
	
}
