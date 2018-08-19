package gui;
import javafx.embed.swing.JFXPanel;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JFrame;

import gui.GanttChart.ExtraData;
import cnrs.i3s.papareto.demo.function.Main;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainView extends Application {
	final static NumberAxis xAxis = new NumberAxis();
    final static CategoryAxis yAxis = new CategoryAxis();
    final static GanttChart<Number,String> chart = new GanttChart<Number,String>(xAxis,yAxis);
	@Override
	public void start(Stage primaryStage) {
		
		try {
			FXMLLoader loader = new FXMLLoader(
					  getClass().getResource(
					    "/gui/MainView.fxml"
					  )
					);
			AnchorPane root =(AnchorPane) FXMLLoader.load(getClass().getResource("MainView.fxml"));
						loader.getController();
	        Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
	public static void main(String[] args) {
		
		launch(args);
	}
}
