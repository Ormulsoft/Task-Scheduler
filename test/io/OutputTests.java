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
		int a = g.addVertex();
		int b = g.addVertex();
		int c = g.addVertex();
		int d = g.addVertex();

		int ab = g.addDirectedSimpleEdge(a, b);
		int ac = g.addDirectedSimpleEdge(a, c);
		int bd = g.addDirectedSimpleEdge(b, d);
		int cd = g.addDirectedSimpleEdge(c, d);

		NumericalProperty weightVerts = new NumericalProperty("Weight");
		weightVerts.setValue(a, 2);
		weightVerts.setValue(b, 3);
		weightVerts.setValue(c, 3);
		weightVerts.setValue(d, 2);

		NumericalProperty weightEdge = g.getVertexWeightProperty();
		weightEdge.setValue(ab, 1);
		weightEdge.setValue(ac, 2);
		weightEdge.setValue(bd, 2);
		weightEdge.setValue(cd, 1);

		NumericalProperty starts = new NumericalProperty("Starts");
		starts.setValue(a, 0);
		starts.setValue(b, 2);
		starts.setValue(c, 4);
		starts.setValue(d, 7);

		NumericalProperty processors = new NumericalProperty("Processors");
		processors.setValue(a, 1);
		processors.setValue(b, 1);
		processors.setValue(c, 2);
		processors.setValue(d, 2);

		g.setVertexWeightProperty(weightVerts);
		g.setVertexStartProperty(starts);
		g.setVertexProcessorProperty(processors);
		g.setEdgeWeightProperty(weightEdge);
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

}
