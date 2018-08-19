package gui;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This class acts as the view for the GUI. Generated from the associated FXML file
 * @author Ormulsoft, various
 *
 */
public class MainView extends Application {
	final static NumberAxis xAxis = new NumberAxis();
    final static CategoryAxis yAxis = new CategoryAxis();
    final static GanttChart<Number,String> chart = new GanttChart<Number,String>(xAxis,yAxis);
    
	@Override
	public void start(Stage primaryStage) {
		
		try {
			// Load fxml controller
			FXMLLoader loader = new FXMLLoader(
					  getClass().getResource(
					    "/gui/MainView.fxml"
					  )
					);
			
			// Set up window/stage
			AnchorPane root =(AnchorPane) FXMLLoader.load(getClass().getResource("MainView.fxml"));
						loader.getController();
	        Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("Ormulsoft: Optimal Task Scheduler");
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			    public void handle(WindowEvent t) {
			        Platform.exit();
			        System.exit(0);
			    }
			});
		} catch(Exception e) {
		}
		
		
	}
	
	
	
	public static void main(String[] args) {
		
		launch(args);
	}
}
