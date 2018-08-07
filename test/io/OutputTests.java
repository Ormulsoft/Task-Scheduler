package io;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.*;

import grph.properties.NumericalProperty;
import toools.io.file.RegularFile;
import util.ScheduleDotWriter;
import util.ScheduleGrph;


public class OutputTests {
	
public static ScheduleGrph g = new ScheduleGrph();;
final static Logger log = Logger.getLogger(OutputTests.class);
	
	@BeforeClass
	public static void before(){
		String outputFile = "C:\\Users\\AmritPal\\Documents\\3rd year\\306\\Softeng-306-Group-15\\src\\resources\\Nodes_8_Random.dot";
		g = Input.readDotInput(outputFile);
		
	}
	
	@Test
	public void testOutputFile() throws RemoteException {
		String outputPath = "C:\\Users\\AmritPal\\Desktop";
		try {
			ScheduleDotWriter d = new ScheduleDotWriter();

			d.writeGraph(g, new RegularFile(outputPath));
		} catch (Exception e) {
			log.error("Failed to export file - is your filepath invalid?", e);
		}
		
		
	}
	
	@Test
	public void testOutputValidity() throws RemoteException {
		
		for(int i : g.getVertices()){
			for(int j : g.getInNeighbors(i)){
				// Fail if child's start time is less that finishing time of it's parents
				if(g.getVertexStartProperty().getValue(i) < (g.getVertexStartProperty().getValue(j) + g.getVertexWeightProperty().getValue(j))){
					fail();
				}
			}
		}
				
	}

}
