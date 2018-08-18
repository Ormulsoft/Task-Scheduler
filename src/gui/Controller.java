package gui;
import java.io.IOException;

import java.lang.management.ManagementFactory;

import java.lang.annotation.Annotation;

import java.net.URL;


import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

import org.apache.log4j.Logger;
import org.graphstream.graph.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.swing.SwingUtilities;
import alg.AStarAlgorithm;
import alg.DFSAlgorithm;
import alg.DFSParallel;
import alg.cost.AStarCostFunction;
import grph.properties.NumericalProperty;
import cnrs.i3s.papareto.demo.function.Main;
import grph.properties.NumericalProperty;
import gui.GanttChart.ExtraData;
import javafx.embed.swing.SwingNode;
import io.Output;
import io.ScheduleEvent;
import io.ScheduleListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import util.PartialScheduleGrph;
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
import util.PartialScheduleGrph;
import javafx.scene.text.TextAlignment;
import util.ScheduleGrph;

public class Controller implements ScheduleListener{
	
	private final Logger log = Logger.getLogger(getClass());
	@FXML
	ZoomableScrollPane _input;
	@FXML
	private Label visited;
	@FXML 
	private AnchorPane GanttPane;
	final static NumberAxis xAxis = new NumberAxis();
	final static CategoryAxis yAxis = new CategoryAxis();
	final static GanttChart<Number,String> chart = new GanttChart<Number,String>(xAxis,yAxis);

	@FXML
	private Label time;

	@FXML
	private Label mem;

	@FXML
	private Label cpuLoad;	

	private double cpuCalculation = 10.00;

	Timer myTimer = new Timer();

	private double seconds = 0.0;


	Controller parse = this;
	@FXML
	Button startBtn;
	@FXML
	private StackedBarChart<?, ?> sbc;
	HashMap<Integer, XYChart.Series> Processer = new HashMap<Integer, XYChart.Series>();
	private ArrayList<String> Processers = new ArrayList<String>();
	private ArrayList<String> Colors = new ArrayList<String>();
	private int rand = 0;



	@FXML
	public void initialize() {
		_input.updateContent();
		viewGraph(_input,io.Main.getIn());
		chart.setMinWidth(600);
		chart.setMinHeight(600);
		GanttPane.getChildren().add(chart);	
		xAxis.setLabel("");
		xAxis.setTickLabelFill(Color.CHOCOLATE);
		xAxis.setMinorTickCount(4);

		yAxis.setLabel("");
		yAxis.setTickLabelFill(Color.CHOCOLATE);
		yAxis.setTickLabelGap(10);
		intializeData();
		yAxis.setCategories(FXCollections.<String>observableArrayList(Processers));

		chart.setTitle("Machine Monitoring");
		chart.setLegendVisible(false);
		chart.setBlockHeight( 50);
		chart.getStylesheets().add(getClass().getResource("ganttchart.css").toExternalForm());
	}


	@FXML
	private void startAlgorithm() {

		seconds = 0;
		startBtn.setDisable(true);
		initalizeColour();		

		new Thread(new Runnable() {

			public void run() {
				long start = System.currentTimeMillis();
				ScheduleGrph out;
				if(io.Main.getNumCores() == 1){
					out = new DFSAlgorithm(io.Main.getIn(), new AStarCostFunction(io.Main.getIn()), io.Main.getNumProcessers(), parse).runAlg();
				}else{
					out = new DFSParallel(io.Main.getIn(), new AStarCostFunction(io.Main.getIn()), io.Main.getNumProcessers(),io.Main.getNumCores(),  parse).runAlg();
				}
				log.info("Algorithm took " + (System.currentTimeMillis() - start) + " ms");
				myTimer.cancel();
				try {
					Output.export(out, io.Main.getOutputFilename());
				} catch (IOException e) {

				} 
				startBtn.setDisable(false);

			}

		}).start();
		new Thread(new Runnable() {

			public void run() {
				myTimer.scheduleAtFixedRate(task, 1000, 1);

			}
		}).start();

		new Thread(new Runnable() {

			public void run() {
				myTimer.scheduleAtFixedRate(cpuTask, 1, 20000);

			}
		}).start();



	}


	public void update(final ScheduleEvent event, final int iterations, final double memory) {

		Platform.runLater(new Runnable() {

			public void run() {
				if(event.getType() == ScheduleEvent.EventType.NewState){


					visited.setText(""+iterations);

				}

				mem.setText(String.format("%.3f", memory));
				//				cpuLoad.setText(""+cpu);
				//System.out.println(""+cpu);
			}
		});

	}


	@FXML
	private void intializeData() {
		for(int i = 0;i<io.Main.getNumProcessers();i++) {
			XYChart.Series series = new XYChart.Series();
			chart.getData().add(series);
			String processer = ""+(i+1);
			Processers.add(processer);
		}
	}

	public void updateGraph(final ScheduleEvent event, final int iterations,final PartialScheduleGrph a) {
		Platform.runLater(new Runnable() {

			public void run() {
				if(event.getType() == ScheduleEvent.EventType.NewState){
					chart.getData().clear();
					intializeData();
					for(int i:a.getVertices()) {
						XYChart.Series series = chart.getData().get(a.getVertexProcessorProperty().getValueAsInt(i)-1);
						series.getData().add(new XYChart.Data(a.getVertexStartProperty().getValueAsInt(i),""+ a.getVertexProcessorProperty().getValueAsInt(i), new ExtraData(a.getVertexWeightProperty().getValueAsInt(i), getColour(i),a.getVertexLabelProperty().getValueAsString(i))));	   	
					}
				}
			}


		});

	}

	public String getColour(int i) {
		if( i%2 == 0) {
			return this.Colors.get(0);
		}
		else {
			return this.Colors.get(1);
		}

	}
	public void initalizeColour() {
		this.Colors.add("status-red");
		this.Colors.add("status-green");
	}


	TimerTask task = new TimerTask(){

		@Override
		public void run() {
			Platform.runLater(new Runnable() {

				public void run() {
					seconds = seconds + 0.001;
					time.setText(String.format("%.3f", seconds));

				}
			});

		}

	};

	TimerTask cpuTask = new TimerTask(){

		@Override
		public void run() {

			try {
				cpuCalculation = getProcessCpuLoad();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			Platform.runLater(new Runnable() {

				public void run() {

					cpuLoad.setText(""+cpuCalculation);

				}
			});

		}

	};


	public double getProcessCpuLoad() throws Exception {

		MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
		ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
		AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

		if (list.isEmpty())     return Double.NaN;

		Attribute att = (Attribute)list.get(0);
		Double value  = (Double)att.getValue();

		// usually takes a couple of seconds before we get real values
		if (value == -1.0)      return Double.NaN;
		// returns a percentage value with 1 decimal point precision
		return ((int)(value * 1000) / 10.0);
	}


	public void viewGraph(ZoomableScrollPane display, ScheduleGrph graph) {

		boolean isNextLayer = true;
		int currentLayer = 0;
		ArrayList<Integer> freeNodes = new ArrayList<Integer>();
		HashMap<Integer,Node> added = new HashMap<Integer,Node>();
		HashMap<Integer,Label> labels = new HashMap<Integer,Label>();

		NumericalProperty vertWeights = graph.getVertexWeightProperty();
		NumericalProperty edgeWeights = graph.getEdgeWeightProperty();

		double anchorWidth = graph.getVertices().size() * 120 + 300;
		double anchorHeight = graph.getVertices().size() * 120 + 300; 
		((AnchorPane)display.getTarget()).setPrefWidth(anchorWidth);
		((AnchorPane)display.getTarget()).setPrefHeight(anchorHeight);
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
				((AnchorPane)display.getTarget()).getChildren().add(node);

				Label label = new Label("ID: " + vert + "\nW: " + vertWeights.getValueAsInt(vert));
				label.setLayoutX(node.getLayoutX() - 20);
				label.setLayoutY(node.getLayoutY() - 20);
				label.setScaleX(1.5);
				label.setScaleY(1.5);
				label.setTextAlignment(TextAlignment.CENTER);
				((AnchorPane)display.getTarget()).getChildren().add(label);

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

				((AnchorPane)display.getTarget()).getChildren().add(line);
				line.toBack();
			}

			Tooltip tip = new Tooltip(toolTip.trim());
			Tooltip.install(added.get(vert), tip);
			Tooltip.install(labels.get(vert), tip);

		}
	}



}

