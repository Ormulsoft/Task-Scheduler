package gui;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;


import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

import org.graphstream.graph.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;

import alg.AStarAlgorithm;
import alg.cost.AStarCostFunction;
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
import util.ScheduleGrph;

public class Controller implements ScheduleListener{
	@FXML
    private Label visited;
	@FXML 
	private AnchorPane GanttPane;
	final static NumberAxis xAxis = new NumberAxis();
    final static CategoryAxis yAxis = new CategoryAxis();
    final static GanttChart<Number,String> chart = new GanttChart<Number,String>(xAxis,yAxis);
	Controller parse = this;
	@FXML
	Button startBtn;
	@FXML
    private StackedBarChart<?, ?> sbc;
	HashMap<Integer, XYChart.Series> Processer = new HashMap<Integer, XYChart.Series>();
	private ArrayList<String> Processers = new ArrayList<String>();
	private ArrayList<String> Colors = new ArrayList<String>();
	private int rand = 0;
	private ObservableList<Annotation> annotationNodes =  FXCollections.observableArrayList();
	@FXML
	private void startAlgorithm() {
		initalizeColour();
		if(!GanttPane.getChildren().contains(chart)) {
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

        /* machine = machines[0];
        XYChart.Series series1 = new XYChart.Series();
        series1.getData().add(new XYChart.Data(0, machine, new ExtraData( 1, "status-red")));
        series1.getData().add(new XYChart.Data(1, machine, new ExtraData( 1, "status-green")));
        series1.getData().add(new XYChart.Data(2, machine, new ExtraData( 1, "status-red")));
        series1.getData().add(new XYChart.Data(3, machine, new ExtraData( 1, "status-green")));

        machine = machines[1];
        XYChart.Series series2 = new XYChart.Series();
        series2.getData().add(new XYChart.Data(0, machine, new ExtraData( 1, "status-green")));
        series2.getData().add(new XYChart.Data(1, machine, new ExtraData( 1, "status-green")));
        series2.getData().add(new XYChart.Data(2, machine, new ExtraData( 2, "status-red")));

        machine = machines[2];
        XYChart.Series series3 = new XYChart.Series();
        series3.getData().add(new XYChart.Data(0, machine, new ExtraData( 1, "status-blue")));
        series3.getData().add(new XYChart.Data(1, machine, new ExtraData( 2, "status-red")));
        series3.getData().add(new XYChart.Data(3, machine, new ExtraData( 1, "status-green"))); 

        chart.getData().addAll(series1, series2, series3);           */
		new Thread(new Runnable() {

			public void run() {
				ScheduleGrph out = new AStarAlgorithm(new AStarCostFunction(io.Main.getIn()), parse).runAlg(io.Main.getIn(), io.Main.getNumCores(), io.Main.getNumProcessers());

				  try {
					Output.export(out, "AAA");
				} catch (IOException e) {
					
				} 
			}
			
		}).start();
		intializeData();
		
	}
	
	private void addAllData() {
		for(int key :Processer.keySet()){
			
			sbc.getData().add(Processer.get(key));
		}
	}
    
	
	@Override
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
		 XYChart.Series series = new XYChart.Series();
		 chart.getData().add(series);
		 String processer = ""+(i+1);
		 Processers.add(processer);
		 }
	}
	private void testData() {
		
	}

	@Override
	public void updateGraph(final ScheduleEvent event, final int iterations,PartialScheduleGrph a) {
		Platform.runLater(new Runnable() {
			
			public void run() {
				if(event.getType() == ScheduleEvent.EventType.NewState){
					chart.getData().clear();
					intializeData();
					for(int i:a.getVertices().toIntArray()) {
						XYChart.Series series = chart.getData().get(a.getVertexProcessorProperty().getValueAsInt(i)-1);
						series.getData().add(new XYChart.Data(a.getVertexStartProperty().getValueAsInt(i),""+ a.getVertexProcessorProperty().getValueAsInt(i), new ExtraData(a.getVertexWeightProperty().getValueAsInt(i), getColour(),a.getVertexLabelProperty().getValueAsString(i))));	
						
						   
					}
					

			        
			        }
				}
			
				
		});
		
	}
	
	public String getColour() {
		if (rand % 2 == 0) {
			rand++;
			return this.Colors.get(0);
		} else {
			rand++;
			return this.Colors.get(1);
		}
		
	}
	public void initalizeColour() {
		this.Colors.add("status-red");
		this.Colors.add("status-green");
		this.Colors.add("status-blue");
	}
	
	 
	
}
