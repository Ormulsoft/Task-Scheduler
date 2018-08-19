package io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import alg.DFSAlgorithm;
import alg.DFSParallel;
import alg.cost.AStarCostFunction;
import gui.Controller;
import gui.ScheduleListener;
import javafx.application.Application;
import javafx.stage.Stage;
import util.PartialScheduleGrph;
import util.ScheduleGrph;

/**
 * Entry point for the task scheduling assignment
 * 
 * @author All
 *
 */
public class Main extends Application {
	private static final int DEFAULT_CORES = 1;
	private static final boolean DEFAULT_VISUALISATION = false;
	private static ScheduleGrph in;
	private static int numOfCores = DEFAULT_CORES;
	private static int numOfProcessers;
	private static String _outputFile;
	final static Logger log = Logger.getLogger(Main.class);

	private static ScheduleListener listen;

	private static final String DEFAULT_OUTPUT_TEMPLATE = "%s-OUTPUT.dot";

	/**
	 * Inital setup / entry point
	 * 
	 * @param args
	 * @throws URISyntaxException
	 */

	public static void main(String[] args) throws URISyntaxException {

		listen = new Controller();
		log.info("Task scheduler launched");
		boolean visualization = DEFAULT_VISUALISATION;
		int numCores = DEFAULT_CORES;
		String inputFile = null;
		String outputFile = null;
		int numProcessors = 0;
		// interpret CLI options and args
		try {
			numProcessors = Integer.parseInt(args[1]);
			inputFile = args[0];

			// Input file argument
			outputFile = String.format(DEFAULT_OUTPUT_TEMPLATE, FilenameUtils.getBaseName(inputFile));

			CommandLine cli = parseCLIArgs(args);
			// Number of cores
			if (cli.hasOption('p')) {
				numCores = Integer.parseInt(cli.getOptionValue('p'));
			}

			// Whether or not to enable visualization
			if (cli.hasOption('v')) {
				visualization = true;
			}

			// Specific output location
			if (cli.hasOption('o')) {
				outputFile = cli.getOptionValue('o') + ".dot";
			}

		} catch (ArrayIndexOutOfBoundsException e) {

			log.error("Please pass the required inputs - { <input file name> <number of processors> }");
			System.exit(0);
		}
		startScheduling(inputFile, outputFile, visualization, numCores, numProcessors);
	}

	/**
	 * Defines logging properties
	 */
	private static void loggerSetup() {
		Properties props = new Properties();
		// try log properties load from file, otherwise use basic
		try {
			props.load(new FileInputStream("log4j.properties"));
			PropertyConfigurator.configure(props);
			return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BasicConfigurator.configure();
	}

	/**
	 * Parse command line arguments and return CLI object
	 * 
	 * @param args
	 * @return
	 */
	private static CommandLine parseCLIArgs(String[] args) {

		// Get various options.
		CommandLineParser parser = new DefaultParser();
		Options options = new Options();

		options.addOption("p", true, "The number of cores used for execution in parallel.");
		options.addOption("v", false, "Visualize the scheduling process");
		options.addOption("o", true, "The intended output file name");

		try {
			return parser.parse(options, args);
		} catch (ParseException e) {
			log.info("There was an processing your command line options.");
			System.exit(1);
			return null;
		}

	}

	/**
	 * Begins the task scheduling process.
	 * 
	 * @param inputFile	Path to input .DOT file
	 * @param outputFile	Path to output .DOT file
	 * @param visualization	Toggle visualization
	 * @param numCores	Number of cores to use for parallelization
	 * @param numProcessors Number of processors to use for scheduling
	 */
	private static void startScheduling(String inputFile, final String outputFile, boolean visualization,
			final int numCores, final int numProcessors) {

		numOfProcessers = numProcessors;
		numOfCores = numCores;
		_outputFile = outputFile;
		log.info("Reading input file");
		try {
			in = Input.readDotInput(inputFile);
		} catch (FileNotFoundException e) {
			log.info("Input filename is invalid!");
			System.exit(1);
		}
		log.info("Started scheduling algorithm with params: " + numProcessors + " processor(s), " + numCores
				+ " core(s)");
		// PartialScheduleGrph out = new AStarAlgorithm(in, new
		// AStarCostFunction(in), numProcessors).runAlg();
		DFSAlgorithm sequential = new DFSAlgorithm(in, new AStarCostFunction(in), numProcessors);
		DFSParallel parallel = new DFSParallel(in, new AStarCostFunction(in), numProcessors, numCores);
		if (visualization == true) {

			gui.MainView.main(null);

		} else {
			long start = System.currentTimeMillis();
			PartialScheduleGrph out;
			if (numCores == 1) {
				out = sequential.runAlg();
			} else {
				out = parallel.runAlg();
			}

			log.info("Algorithm took " + (System.currentTimeMillis() - start) + " ms");
			log.info("Schedule length is: " + out.getScheduleLength());
			log.info("Outputting solution to file: " + outputFile);

			try {
				Output.export(out, outputFile);
			} catch (IOException e) {
				log.error("Failed to export file - is your output filepath valid?", e);
			}

			log.info("Finished!");
			System.exit(0);
		}

	}

	public static ScheduleGrph getIn() {
		return in;
	}

	public static int getNumProcessers() {
		return numOfProcessers;
	}

	public static int getNumCores() {
		return numOfCores;
	}

	public static String getOutputFilename() {
		return _outputFile;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {}

}