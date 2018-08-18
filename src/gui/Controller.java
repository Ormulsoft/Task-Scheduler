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
import grph.properties.NumericalProperty;
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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import toools.collections.Collections;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TooltipBuilder;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.TextAlignment;
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
		HashMap<Integer,Node> added = new HashMap<Integer,Node>();
		HashMap<Integer,Label> labels = new HashMap<Integer,Label>();
		
		NumericalProperty vertWeights = graph.getVertexWeightProperty();
		NumericalProperty edgeWeights = graph.getEdgeWeightProperty();
		
		double anchorWidth = graph.getVertices().size() * 120 + 300;
		double anchorHeight = graph.getVertices().size() * 120 + 300; 
		((AnchorPane)display.getContent()).setPrefWidth(anchorWidth);
		((AnchorPane)display.getContent()).setPrefHeight(anchorHeight);
		display.setHvalue(0.45);
		
		freeNodes.addAll(graph.getSources());
		
		
		while (isNextLayer) {
			int i = 0;
			for (int vert : freeNodes) {
				Circle node = new Circle(36);
				node.setFill(Color.CADETBLUE);
				node.setId(Integer.toString(vert));
				node.setLayoutX((anchorWidth / 2) + (80 * ((i + 1)/ 2)  * (Math.pow(-1, i))) - (40 * (freeNodes.size() % 2)));
				node.setLayoutY(40 + currentLayer * 100);
				((AnchorPane)display.getContent()).getChildren().add(node);
				
				Label label = new Label("ID: " + vert + "\nW: " + vertWeights.getValueAsInt(vert));
				label.setLayoutX(node.getLayoutX() - 20);
				label.setLayoutY(node.getLayoutY() - 20);
				label.setScaleX(1.5);
				label.setScaleY(1.5);
				label.setTextAlignment(TextAlignment.CENTER);
				((AnchorPane)display.getContent()).getChildren().add(label);
				
				labels.put(vert, label);
				added.put(vert,node);
				i++;
			}
			
			freeNodes.clear();
			
			for (int vert : graph.getVertices()) {
				if (!added.containsKey(vert)) {
					boolean deps = true;
					for (int parent : graph.getInNeighbors(vert)) {
						if (!added.containsKey(parent)) {
							deps = false;
							break;
						}
					}
					
					if (deps) {
						freeNodes.add(vert);
					}
					
				}
			}
			
			
			if (freeNodes.isEmpty()) {
				isNextLayer = false;
			}
			else {
				currentLayer++;
			}
		}
		
		for (int vert : added.keySet()) {
			
			String toolTip = "No parent dependencies";
			
			for (int parent : graph.getInNeighbors(vert)) {
				
				if (toolTip.equals("No parent dependencies")) {
					toolTip = "";
				}
				
				int edge = (Integer)graph.getEdgesConnecting(parent, vert).toArray()[0];
				toolTip += "\nDepends on task " + parent + ", transfer cost " + edgeWeights.getValueAsInt(edge); 
				
				Line line = new Line(
						added.get(parent).getLayoutX(),
						added.get(parent).getLayoutY(),
						added.get(vert).getLayoutX(),
						added.get(vert).getLayoutY());
				
				((AnchorPane)display.getContent()).getChildren().add(line);
				line.toBack();
			}
			
			Tooltip tip = new Tooltip(toolTip.trim());
			Tooltip.install(added.get(vert), tip);
			Tooltip.install(labels.get(vert), tip);
			
		}
		
		
	}
	
	
}
