package test.alg;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import io.Input;
import io.OutputTests;
import util.ScheduleGrph;

public class AStarAlgorithmTests {

	public static ScheduleGrph g = new ScheduleGrph();;
	final static Logger log = Logger.getLogger(OutputTests.class);

	@BeforeClass
	public static void before() {
		String inputFile = "src\\resources\\Nodes_8_Random.dot";
		g = Input.readDotInput(inputFile);

	}

	@Test(timeout = 1000)
	public void testAStarTimeout() throws RemoteException {
		// Checking to see if Astar
		// Works under some time frame
		String outputPath = "C:\\Users\\AmritPal\\Desktop";
		try {

		} catch (Exception e) {
			log.error("Failed to find file - is your filepath invalid?", e);
		}

	}

}
