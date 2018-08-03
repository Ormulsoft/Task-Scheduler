package io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import alg.AlgorithmStub;
import util.ScheduleGrph;

/**
 * Entry point for the task scheduling assignment
 * 
 * @author
 *
 */
public class Main {

	final static Logger log = Logger.getLogger(Main.class);
	
	//private static final String DEFAULT_OUTPUT_TEMPLATE = "%s-OUTPUT.dot";
	
	private static String inputFile;	
	private static String outputFile = "out";
	
	private static int numProcessors;
	
	private static boolean visualization = false; // By default, no visualization should be shown.
	
	private static int numCores = 1; // 1 core implies sequential scheduling, (i.e. no parallelization)
	
	

	/**
	 * Inital setup / entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
	/*	Properties props = new Properties();

		// try log properties load from file, otherwise use basic
		try {
			props.load(new FileInputStream("src/resources/log4j.properties"));
			PropertyConfigurator.configure(props);
		} catch (FileNotFoundException e) {
			BasicConfigurator.configure();
			e.printStackTrace();
		} catch (IOException e) {
			BasicConfigurator.configure();
			e.printStackTrace();
		}
		

		parseCLIArgs(args);
		*/
		String inputFile = "src\\resources\\Nodes_7_OutTree.dot";
		String outputFile = "exp";
		startProcess(inputFile, outputFile);
	}
	

	private static void parseCLIArgs(String[] args) {
		
		// Get input file name and number of processors.
		inputFile = args[0];		
		numProcessors = Integer.parseInt(args[1]);
		
		// Get various options. 
		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		
		options.addOption("p", true, "The number of cores used for execution in parallel.");
		options.addOption("v", false, "Visualize the scheduling process");
		options.addOption("o", true, "The intended output file name");
		
		try {
			CommandLine commandLine = parser.parse(options, args);
			
			if (commandLine.hasOption('p')) {				
				numCores = Integer.parseInt(commandLine.getOptionValue('p'));
			}
			
			if (commandLine.hasOption('v')) {				
				visualization = true;
			}
			
			if (commandLine.hasOption('o')) {				
				outputFile = commandLine.getOptionValue('o');
			}
			

		} catch (ParseException e) {
			System.out.println("There was an processing your command line options.");
			
			// TODO display the usage help function 
			
			//e.printStackTrace();
		}


		
		
	}




	/**
	 * Begins the task scheduling process
	 */
	private static void startProcess(String inputFile, String outputFile) {
		log.info("Started scheduling");

		int cores = 1;
		String inputPath = "src/resources/Nodes_7_OutTree.dot";
		String outputPath = "test_output/exp";
		ScheduleGrph in = Input.readDotInput(inputPath);
		ScheduleGrph out = new AlgorithmStub().runAlg(in, cores);

		try {
			Output.export(out, outputPath);
		} catch (IOException e) {
			log.error("Failed to export file", e);
		}
	}
}