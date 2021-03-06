package gui;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import alg.DFSAlgorithm;
import alg.DFSParallel;
import alg.cost.AStarCostFunction;
import grph.properties.NumericalProperty;
import gui.GanttChart.ExtraData;
import io.Output;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import util.PartialScheduleGrph;
import util.ScheduleGrph;
/**
 * This class deals with interactivity of the user interface during scheduling visualisation. 
 * @author All
 */
@SuppressWarnings("restriction")
public class Controller implements ScheduleListener {

	private final Logger log = Logger.getLogger(getClass());
	@FXML
	ZoomableScrollPane _input;
	@FXML
	ZoomableScrollPane _ganttScroll;
	
	
	@FXML
	private Label visited;
	@FXML
	private AnchorPane GanttPane;
	final static NumberAxis xAxis = new NumberAxis();
	final static CategoryAxis yAxis = new CategoryAxis();
	final static GanttChart<Number, String> chart = new GanttChart<Number, String>(xAxis, yAxis);

	@FXML
	private Label time;

	@FXML
	private Label mem;
	


	@FXML
	Timer myTimer;

	private double seconds = 0.0;

	Controller parse = this;
	private DFSAlgorithm sequential;
	private DFSParallel parallel;
	@FXML
	Button startBtn;
	@FXML
	private StackedBarChart<?, ?> sbc;
	HashMap<Integer, XYChart.Series> Processer = new HashMap<Integer, XYChart.Series>();
	private ArrayList<String> Processers = new ArrayList<String>();
	private ArrayList<String> Colors = new ArrayList<String>();
	private int rand = 0;
	private Timeline GUITimeUpdate;
	
	
	/**
	 * The gui initiazation after all fxml objects have been placed
	 */
	@FXML
	public void initialize() {

		// update the zoomable panes to allow zoom and set to specific pan location
		_input.updateContent();
		_ganttScroll.updateContent();
		_ganttScroll.setVvalue(0.75);
		
		// Generate and display input graph
		viewGraph(_input, io.Main.getIn());
		
		// Generate and display empty gantt chart
		chart.setMinWidth(600);
		chart.setMinHeight(590);
		GanttPane.getChildren().add(chart);
		xAxis.setLabel("Time");
		xAxis.setTickLabelFill(Color.WHITE);

		yAxis.setLabel("Processor No.");
		yAxis.setTickLabelFill(Color.WHITE);
		intializeData();
		yAxis.setCategories(FXCollections.<String>observableArrayList(Processers));

		chart.setTitle("");
		chart.setLegendVisible(false);
		chart.setBlockHeight(50);
		chart.getStylesheets().add(getClass().getResource("ganttchart.css").toExternalForm());
	}

	@FXML
	/**
	 * Run the algorithm, triggered when start button is pressed
	 */
	private void startAlgorithm() {
		myTimer = new Timer();

		seconds = 0;
		
		// GUI time display animation 
		GUITimeUpdate = new Timeline(new KeyFrame(Duration.millis( 1 ),
			new EventHandler<ActionEvent>() {
						public void handle(ActionEvent event) {
							seconds = seconds + 0.001;
							time.setText(String.format("%.3f", seconds));				
						}
					})
				);
		GUITimeUpdate.setCycleCount( Animation.INDEFINITE );
		GUITimeUpdate.play();
		

		// Disable start button when running
		startBtn.setDisable(true);
		initalizeColour();

		// Run algorithm on a service thread
		Service<PartialScheduleGrph> algService = new Service<PartialScheduleGrph>() {

			@Override
			protected Task<PartialScheduleGrph> createTask() {
				
				
				return new Task<PartialScheduleGrph>(){
					
					@Override
					protected PartialScheduleGrph call() {
						long start = System.currentTimeMillis();
						PartialScheduleGrph out;
						// Run either parallel or series depending on number of cores
						if(io.Main.getNumCores() == 1){
							out = new DFSAlgorithm(io.Main.getIn(), new AStarCostFunction(io.Main.getIn()), io.Main.getNumProcessers(), parse).runAlg();
						}else{
							out = new DFSParallel(io.Main.getIn(), new AStarCostFunction(io.Main.getIn()), io.Main.getNumProcessers(),io.Main.getNumCores(),  parse).runAlg();
						}
						
						log.info("Algorithm took " + (System.currentTimeMillis() - start) + " ms");
						return out;
					}
					
				};
			}
			
			

			@Override
			protected void succeeded() {
				// On success, display stats, stop timers and save output
				log.info("Schedule length is: " + getValue().getScheduleLength());
				log.info("Outputting solution to file: " + io.Main.getOutputFilename());
				GUITimeUpdate.stop();
				myTimer.cancel();
				try {
					Output.export(getValue(), io.Main.getOutputFilename());
				} catch (IOException e) {

				}
				startBtn.setText("Done!");
			}
			
		};
		
		algService.start();

	}

	/**
	 * Update the listener for the GUI stats display
	 */
	public void update(final ScheduleEvent event, final int iterations, final double memory) {

		// Platform.runLater for gui thread safety, important on multi-core runs
		Platform.runLater(new Runnable() {

			public void run() {
				if (event.getType() == ScheduleEvent.EventType.NewState) {
					visited.setText("" + iterations);
				}

				mem.setText("" + (int) (memory));

			}
		});
	}

	/**
	 * Initialize ganttchart data
	 */
	@FXML
	private void intializeData() {
		for (int i = 0; i < io.Main.getNumProcessers(); i++) {
			XYChart.Series series = new XYChart.Series();
			chart.getData().add(series);
			String processer = "" + (i + 1);
			Processers.add(processer);
		}
	}

	/**
	 * Update the listener for the current best schedule gantt chart display
	 */
	public void updateGraph(final ScheduleEvent event, final int iterations, final PartialScheduleGrph a) {
		
		// Platform.runLater for gui thread safety, important on multi-core runs
		Platform.runLater(new Runnable() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void run() {
				if (event.getType() == ScheduleEvent.EventType.NewState) {
					// Clear gantt chart
					chart.getData().clear();
					intializeData();

					try {
						// Add vertices to gantt chart
						for (int i : a.getVertices()) {
							XYChart.Series series = chart.getData()
									.get(a.getVertexProcessorProperty().getValueAsInt(i) - 1);
							series.getData()
									.add(new XYChart.Data(a.getVertexStartProperty().getValueAsInt(i),
											"" + a.getVertexProcessorProperty().getValueAsInt(i),
											new ExtraData(a.getVertexWeightProperty().getValueAsInt(i), getColour(i),
													a.getVertexLabelProperty().getValueAsString(i))));
						}
					} catch (NullPointerException e) {

					} catch (ArrayIndexOutOfBoundsException e) {

					}
				}
			}

		});

	}

	/**
	 * Get the color associated with a value for the gantt chart
	 * @param i The value to get the color of
	 * @return
	 */
	public String getColour(int i) {
		if (i % 2 == 0) {
			return this.Colors.get(0);
		} else {
			return this.Colors.get(1);
		}

	}

	/**
	 * Set up the colors for the gantt chart color assignment
	 */
	public void initalizeColour() {
		this.Colors.add("status-red");
		this.Colors.add("status-green");
	}

	/**
	 * This method retrieves the current load on the CPU.	
	 * @return
	 * @throws Exception
	 */
	public double getProcessCpuLoad() throws Exception {

		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
		AttributeList list = mbs.getAttributes(name, new String[] { "ProcessCpuLoad" });

		if (list.isEmpty())
			return Double.NaN;

		Attribute att = (Attribute) list.get(0);
		Double value = (Double) att.getValue();

		// Usually takes a couple of seconds before we get real values
		if (value == -1.0)
			return Double.NaN;
		// Returns a percentage value with 1 decimal point precision
		return ((int) (value * 1000) / 10.0);
	}

	/**
	 * This method deals with the visualization of the input graph on the GUI.
	 * @param display
	 * @param graph
	 */
	public void viewGraph(ZoomableScrollPane display, ScheduleGrph graph) {

		// Set up node and label maps and lists
		boolean isNextLayer = true;
		int currentLayer = 0;
		ArrayList<Integer> freeNodes = new ArrayList<Integer>();
		HashMap<Integer, Node> added = new HashMap<Integer, Node>();
		HashMap<Integer, Label> labels = new HashMap<Integer, Label>();

		// Get these properties from the graph for easy ref later
		NumericalProperty vertWeights = graph.getVertexWeightProperty();
		NumericalProperty edgeWeights = graph.getEdgeWeightProperty();

		// Set up the anchor pane so it is large enough to fit all nodes
		double anchorWidth = graph.getVertices().size() * 120 + 300;
		double anchorHeight = graph.getVertices().size() * 120 + 300;
		((AnchorPane) display.getTarget()).setPrefWidth(anchorWidth);
		((AnchorPane) display.getTarget()).setPrefHeight(anchorHeight);
		display.setHvalue(0.45);

		// Add sources to first layer
		freeNodes.addAll(graph.getSources());

		while (isNextLayer) {
			// For each "layer" of dependencies, generate the layer 
			//then add the children of all nodes on that layer to the next one
			int i = 0;
			for (int vert : freeNodes) {
				// Set up the circle representing the node
				Circle node = new Circle(36);
				node.setFill(Color.web("#00A2D3"));
				node.setId(Integer.toString(vert));
				node.setLayoutX(
						(anchorWidth / 2) + (80 * ((i + 1) / 2) * (Math.pow(-1, i))) - (40 * (freeNodes.size() % 2)));
				node.setLayoutY(40 + currentLayer * 100);
				((AnchorPane) display.getTarget()).getChildren().add(node);

				// Set up the label to attach to this task
				Label label = new Label("ID: " + vert + "\nW: " + vertWeights.getValueAsInt(vert));
				label.setLayoutX(node.getLayoutX() - 20);
				label.setLayoutY(node.getLayoutY() - 20);
				label.setScaleX(1.5);
				label.setScaleY(1.5);
				label.setTextAlignment(TextAlignment.CENTER);
				label.getStyleClass().add("node-labels");
				((AnchorPane) display.getTarget()).getChildren().add(label);

				labels.put(vert, label);
				added.put(vert, node);
				i++;
			}

			// add next layer to freeNodes
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
			} else {
				currentLayer++;
			}
		}

		// Generate tooltips and lines AFTER node generation so that lines can see dependency node x and y
		for (int vert : added.keySet()) {

			String toolTip = "No parent dependencies";

			for (int parent : graph.getInNeighbors(vert)) {

				if (toolTip.equals("No parent dependencies")) {
					toolTip = "";
				}

				int edge = (Integer) graph.getEdgesConnecting(parent, vert).toArray()[0];
				toolTip += "\nDepends on task " + parent + ", transfer cost " + edgeWeights.getValueAsInt(edge);

				Line line = new Line(added.get(parent).getLayoutX(), added.get(parent).getLayoutY(),
						added.get(vert).getLayoutX(), added.get(vert).getLayoutY());

				((AnchorPane) display.getTarget()).getChildren().add(line);
				line.toBack();
			}

			Tooltip tip = new Tooltip(toolTip.trim());
			Tooltip.install(added.get(vert), tip);
			Tooltip.install(labels.get(vert), tip);

		}
	}

}
