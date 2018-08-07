package io;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.junit.*;

import toools.io.file.RegularFile;
import util.ScheduleDotWriter;
import util.ScheduleGrph;


public class InputTests {
	
	public static ScheduleGrph g = new ScheduleGrph();;
	final static Logger log = Logger.getLogger(OutputTests.class);
	
	@BeforeClass
	public static void before(){
		String inputFile = "C:\\Users\\AmritPal\\Documents\\3rd year\\306\\Softeng-306-Group-15\\src\\resources\\Nodes_8_Random.dot";
		g = Input.readDotInput(inputFile);
		
	}
	
	@Test
	public void testInputFile() throws RemoteException {
		// Checking to see if any input file is read
		// depsite having random comments
		String outputPath = "C:\\Users\\AmritPal\\Desktop";
		try {
			
			
		} catch (Exception e) {
			log.error("Failed to find file - is your filepath invalid?", e);
		}
		
		
	}
	
	@Test
	public void testInputFileNotFound() throws RemoteException {
		// Checking to see if correct exception is thrown
		// and correctly handles if input file is not found
		String outputPath = "C:\\Users\\AmritPal\\Desktop";
		try {
			
			
		} catch (Exception e) {
			log.error("Failed to find file - is your filepath invalid?", e);
		}
		
		
	}

}
