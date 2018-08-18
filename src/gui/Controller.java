package gui;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import org.graphstream.graph.Graph;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
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
	@FXML
	private void startAlgorithm() {
		seconds = 0;
		startBtn.setDisable(true);
		
		new Thread(new Runnable() {

			public void run() {
				ScheduleGrph out = new AStarAlgorithm(new AStarCostFunction(io.Main.getIn()), parse).runAlg(io.Main.getIn(), io.Main.getNumCores(), io.Main.getNumProcessers());
				myTimer.cancel(); 
				startBtn.setDisable(false);
			}
			
		}).start();
		intializeData();
		
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
	
	private void addAllData() {
		for(int key :Processer.keySet()){
			
			sbc.getData().add(Processer.get(key));
		}
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
	
	public static double getProcessCpuLoad() throws Exception {

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
	
	
}
