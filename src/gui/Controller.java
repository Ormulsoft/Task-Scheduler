package gui;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import util.ScheduleGrph;
public class Controller  implements Initializable  {
	final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
	@FXML
	Button startBtn;
	@FXML
    private StackedBarChart<?, ?> sbc;
	@FXML
	Text A;
	HashMap<Integer, XYChart.Series> Processer = new HashMap<Integer, XYChart.Series>();
	@FXML
	private void startAlgorithm() {
		ScheduleGrph out = new AStarAlgorithm(new AStarCostFunction(io.Main.getIn())).runAlg(io.Main.getIn(), io.Main.getNumCores(), io.Main.getNumProcessers());
		intializeData();
		startBtn.setDisable(true);
		A.setText("lamo");
	}
	private void startAlgorithmTest() {
		ScheduleGrph out = new AStarAlgorithm(new AStarCostFunction(io.Main.getIn())).runAlg(io.Main.getIn(), io.Main.getNumCores(), io.Main.getNumProcessers());
	}
	
	private void addAllData() {
		for(int key :Processer.keySet()){
			
			sbc.getData().add(Processer.get(key));
		}
	}
	Runnable BackgroundinitializeData = () -> {
		setLabel();
		};
    
    public void test(){
    	intializeData2();

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
	
	public void setLabel() {
		A = new Text();
		A.setText("letsGO");
		
	}
	@FXML
	public void intializeData2() {
		for(int i = 0;i<io.Main.getNumProcessers();i++) {
		 XYChart.Series a = new XYChart.Series();
		 a.setName(""+i);
		 a.getData().add(new XYChart.Data("LOL" + i,i+2));
		 Processer.put(i, a);
		}
		addAllData();
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
}
